package com.campus.startup.features.matching.domain

import com.campus.startup.features.matching.data.model.MatchResult
import com.campus.startup.features.matching.data.model.StudentMatchProfile
import com.campus.startup.features.matching.data.model.personLabel
import com.campus.startup.features.recruitment.data.model.SkillItem
import com.campus.startup.features.recruitment.data.model.StartupRoleCategory

/**
 * Transparent, rule-based co-founder score.
 *
 * Identical skills are intentionally worth less than skills the current student does not have.
 * A balanced campus team is rewarded when the other student covers a different craft
 * (technical, design, growth, or product) and still shares a startup interest, team type, or goal.
 *
 * Points, capped at 100:
 * - Complementary skills: up to 42
 * - Shared interests and startup areas: up to 22
 * - Team type and entrepreneurial goals: up to 14
 * - Different preferred role: up to 10
 * - Shared experience tags: up to 12
 */
object CofounderMatcher {

    fun isReady(profile: StudentMatchProfile): Boolean {
        return profile.name.trim().length >= 2 &&
            profile.skills.isNotEmpty() &&
            profile.goals.trim().length >= 12
    }

    fun incomplete(): MatchResult {
        return MatchResult(
            compatibility = 0,
            reasons = listOf("Complete your profile to see why this student is a complementary fit."),
            complementarySkills = emptyList(),
            sharedInterests = emptyList(),
            sharedExperience = emptyList(),
            profileReady = false
        )
    }

    fun evaluate(me: StudentMatchProfile, candidate: StudentMatchProfile): MatchResult {
        if (!isReady(me)) return incomplete()

        val complementarySkills = candidate.skills.filter { skill ->
            me.skills.none { mine -> skillsOverlap(mine, skill) }
        }
        val userFamilies = familiesOf(me.skills)
        val candidateFamilies = familiesOf(candidate.skills)
        val complementaryFamilies = candidateFamilies - userFamilies

        val skillPoints = if (candidate.skills.isNotEmpty() && complementarySkills.isEmpty()) {
            IDENTICAL_SKILL_POINTS
        } else {
            familyPoints(complementaryFamilies.size) + complementarySkills.size.coerceAtMost(3) * 4
        }

        val myTopics = (me.interests + me.areasOfInterest).map { it.lowercase() }.toSet()
        val sharedTopics = (candidate.interests + candidate.areasOfInterest).filter { topic ->
            myTopics.contains(topic.lowercase())
        }
        val interestPoints = if (myTopics.isEmpty()) {
            6
        } else {
            ((sharedTopics.size.coerceAtMost(2) / 2f) * 22f).toInt()
        }

        val goalTokens = sharedGoalTokens(me.goals, candidate.goals)
        var alignmentPoints = 0
        if (me.teamPreference == candidate.teamPreference) alignmentPoints += 7
        alignmentPoints += if (me.goals.isBlank()) 4 else if (goalTokens.isNotEmpty()) 7 else 0

        val differentRole = candidate.preferredRole != StartupRoleCategory.ALL &&
            candidate.preferredRole != me.preferredRole
        val rolePoints = if (differentRole) 10 else 2

        val sharedExperience = candidate.experienceTags.filter { tag ->
            me.experienceTags.any { mine -> mine.equals(tag, ignoreCase = true) }
        }
        val experiencePoints = when {
            me.experienceTags.isEmpty() && candidate.experience.isNotBlank() -> 4
            sharedExperience.isEmpty() -> 0
            else -> sharedExperience.size.coerceAtMost(2) * 6
        }

        val compatibility = (skillPoints + interestPoints + alignmentPoints + rolePoints + experiencePoints)
            .coerceIn(0, 100)

        return MatchResult(
            compatibility = compatibility,
            reasons = buildReasons(
                me = me,
                candidate = candidate,
                complementarySkills = complementarySkills,
                complementaryFamilies = complementaryFamilies,
                userFamilies = userFamilies,
                sharedTopics = sharedTopics,
                sharedExperience = sharedExperience,
                goalTokens = goalTokens,
                differentRole = differentRole
            ),
            complementarySkills = complementarySkills,
            sharedInterests = sharedTopics,
            sharedExperience = sharedExperience,
            profileReady = true
        )
    }

    private fun buildReasons(
        me: StudentMatchProfile,
        candidate: StudentMatchProfile,
        complementarySkills: List<String>,
        complementaryFamilies: Set<SkillFamily>,
        userFamilies: Set<SkillFamily>,
        sharedTopics: List<String>,
        sharedExperience: List<String>,
        goalTokens: List<String>,
        differentRole: Boolean
    ): List<String> {
        val reasons = mutableListOf<String>()

        if (complementaryFamilies.isNotEmpty()) {
            val yours = if (userFamilies.isEmpty()) "your profile" else "your ${phrase(userFamilies)} background"
            reasons += "Complements $yours with ${phrase(complementaryFamilies)}."
        } else if (complementarySkills.isNotEmpty()) {
            reasons += "Adds ${complementarySkills.take(2).joinToString(" and ")} without duplicating your current skill set."
        } else if (me.skills.isNotEmpty() && candidate.skills.isNotEmpty()) {
            reasons += "Overlapping skills only — useful as a peer, less balanced as a co-founder pair."
        }

        if (sharedTopics.isNotEmpty()) {
            reasons += "Shared startup interests: ${sharedTopics.take(3).joinToString(", ")}."
        }

        if (sharedExperience.isNotEmpty()) {
            reasons += "Similar experience: ${sharedExperience.take(2).joinToString(", ")}."
        }

        val roleAlreadyDescribed = familyOfRole(candidate.preferredRole)?.let { it in complementaryFamilies } == true
        if (differentRole && !roleAlreadyDescribed) {
            reasons += "Fills the ${candidate.preferredRole.personLabel().lowercase()} seat beside your ${me.preferredRole.personLabel().lowercase()} focus."
        }

        if (me.teamPreference == candidate.teamPreference) {
            reasons += "You both want a ${candidate.teamPreference.displayName.lowercase()}."
        } else if (goalTokens.isNotEmpty()) {
            reasons += "Aligned entrepreneurial goals around ${goalTokens.take(2).joinToString(" and ")}."
        }

        if (reasons.isEmpty()) {
            reasons += "Recommended from the campus directory based on the profile you shared."
        }
        return reasons.take(3)
    }

    private fun familyPoints(familyCount: Int): Int = when (familyCount) {
        0 -> 0
        1 -> 22
        2 -> 28
        else -> 30
    }

    private fun familiesOf(skills: List<String>): Set<SkillFamily> {
        return skills.mapNotNull { familyOf(it) }.toSet()
    }

    private fun familyOf(skill: String): SkillFamily? {
        val exact = SkillItem.ALL_SKILLS.find { it.name.equals(skill, ignoreCase = true) }
        if (exact != null) return exact.category.toFamily()
        val partial = SkillItem.ALL_SKILLS.find { item ->
            skill.contains(item.name, ignoreCase = true) || item.name.contains(skill, ignoreCase = true)
        }
        if (partial != null) return partial.category.toFamily()
        val lower = skill.lowercase()
        return when {
            TECHNICAL_HINTS.any { lower.contains(it) } -> SkillFamily.TECHNICAL
            DESIGN_HINTS.any { lower.contains(it) } -> SkillFamily.DESIGN
            GROWTH_HINTS.any { lower.contains(it) } -> SkillFamily.GROWTH
            PRODUCT_HINTS.any { lower.contains(it) } -> SkillFamily.PRODUCT
            else -> null
        }
    }

    private fun familyOfRole(role: StartupRoleCategory): SkillFamily? = role.toFamily()

    private fun StartupRoleCategory.toFamily(): SkillFamily? = when (this) {
        StartupRoleCategory.DEVELOPER -> SkillFamily.TECHNICAL
        StartupRoleCategory.DESIGNER -> SkillFamily.DESIGN
        StartupRoleCategory.MARKETER -> SkillFamily.GROWTH
        StartupRoleCategory.INTERN -> SkillFamily.PRODUCT
        StartupRoleCategory.ALL -> null
    }

    private fun phrase(families: Set<SkillFamily>): String {
        val labels = families.map {
            when (it) {
                SkillFamily.TECHNICAL -> "technical"
                SkillFamily.DESIGN -> "design"
                SkillFamily.GROWTH -> "growth"
                SkillFamily.PRODUCT -> "product"
            }
        }
        return when (labels.size) {
            0 -> "general"
            1 -> labels[0]
            2 -> "${labels[0]} and ${labels[1]}"
            else -> labels.dropLast(1).joinToString(", ") + " and " + labels.last()
        }
    }

    private fun skillsOverlap(left: String, right: String): Boolean {
        return left.equals(right, ignoreCase = true) ||
            left.contains(right, ignoreCase = true) ||
            right.contains(left, ignoreCase = true)
    }

    private fun sharedGoalTokens(mine: String, theirs: String): List<String> {
        val myTokens = tokens(mine)
        return tokens(theirs).filter { it in myTokens }.distinct()
    }

    private fun tokens(text: String): Set<String> {
        return text.lowercase()
            .split(Regex("[^a-z0-9]+"))
            .filter { it.length >= 5 && it !in STOPWORDS }
            .toSet()
    }

    private enum class SkillFamily {
        TECHNICAL,
        DESIGN,
        GROWTH,
        PRODUCT
    }

    private val TECHNICAL_HINTS = listOf(
        "kotlin", "android", "flutter", "react", "node", "python", "firebase", "sql", "llm", "java", "backend", "mobile"
    )
    private val DESIGN_HINTS = listOf("figma", "ui", "ux", "design", "prototype", "canva", "adobe", "graphic")
    private val GROWTH_HINTS = listOf("market", "social", "content", "seo", "community", "outreach", "sales", "growth")
    private val PRODUCT_HINTS = listOf("product", "operation", "research", "pitch", "strategy")

    private val STOPWORDS = setOf(
        "about", "after", "also", "and", "are", "because", "build", "building", "campus",
        "from", "have", "into", "looking", "startup", "startups", "student", "students",
        "that", "their", "them", "they", "this", "want", "with", "would", "your", "team",
        "teams", "join", "help", "someone", "something", "where", "which", "while", "will",
        "work", "working", "people", "other", "than", "then", "just", "like", "make", "making"
    )

    private const val IDENTICAL_SKILL_POINTS = 8
}
