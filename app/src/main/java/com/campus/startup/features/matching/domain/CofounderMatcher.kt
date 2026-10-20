package com.campus.startup.features.matching.domain

import com.campus.startup.features.matching.data.model.MatchResult
import com.campus.startup.features.matching.data.model.StudentMatchProfile
import com.campus.startup.features.matching.data.model.personLabel
import com.campus.startup.features.recruitment.data.model.SkillItem
import com.campus.startup.features.recruitment.data.model.StartupRoleCategory

/**
 * Interest-driven and skill-complementary co-founder compatibility scorer.
 *
 * Prioritizes shared startup domains and personal interests while rewarding complementary skills.
 * Always produces a visible compatibility score on the Co-founder Matching page.
 */
object CofounderMatcher {

    private val DEFAULT_CAMPUS_INTERESTS = listOf(
        "AI / ML", "EdTech", "FinTech", "SaaS", "Artificial Intelligence",
        "Product Design", "Campus consumer", "Open Source", "Community"
    )

    fun isReady(profile: StudentMatchProfile?): Boolean {
        if (profile == null) return true
        return profile.name.trim().isNotEmpty() ||
            profile.interests.isNotEmpty() ||
            profile.areasOfInterest.isNotEmpty() ||
            profile.skills.isNotEmpty()
    }

    fun evaluate(
        me: StudentMatchProfile?,
        candidate: StudentMatchProfile,
        activeInterestFilters: Set<String> = emptySet()
    ): MatchResult {
        val myInterestList = buildList {
            if (me != null) {
                addAll(me.interests)
                addAll(me.areasOfInterest)
            }
            addAll(activeInterestFilters)
            if (isEmpty()) {
                addAll(DEFAULT_CAMPUS_INTERESTS)
            }
        }.map { it.trim() }.filter { it.isNotEmpty() }.distinct()

        val candidateInterests = (candidate.interests + candidate.areasOfInterest)
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .distinct()

        val sharedTopics = candidateInterests.filter { candTopic ->
            myInterestList.any { myTopic ->
                myTopic.equals(candTopic, ignoreCase = true) ||
                    myTopic.contains(candTopic, ignoreCase = true) ||
                    candTopic.contains(myTopic, ignoreCase = true)
            }
        }.distinct()

        // Interest score up to 52 points (primary driver requested by user)
        val interestPoints = when {
            sharedTopics.size >= 3 -> 52
            sharedTopics.size == 2 -> 44
            sharedTopics.size == 1 -> 34
            candidateInterests.isNotEmpty() -> 20
            else -> 15
        }

        val mySkills = me?.skills.orEmpty()
        val complementarySkills = if (mySkills.isEmpty()) {
            candidate.skills
        } else {
            candidate.skills.filter { skill ->
                mySkills.none { mine -> skillsOverlap(mine, skill) }
            }
        }

        val userFamilies = familiesOf(mySkills)
        val candidateFamilies = familiesOf(candidate.skills)
        val complementaryFamilies = candidateFamilies - userFamilies

        val skillPoints = if (candidate.skills.isNotEmpty() && complementarySkills.isEmpty()) {
            14
        } else {
            familyPoints(complementaryFamilies.size) + complementarySkills.size.coerceAtMost(3) * 3
        }.coerceAtMost(30)

        val differentRole = me == null || (
            candidate.preferredRole != StartupRoleCategory.ALL &&
                candidate.preferredRole != me.preferredRole
            )
        val rolePoints = if (differentRole) 10 else 5

        val sharedExperience = if (me == null) {
            candidate.experienceTags.take(1)
        } else {
            candidate.experienceTags.filter { tag ->
                me.experienceTags.any { mine -> mine.equals(tag, ignoreCase = true) }
            }
        }
        val experiencePoints = (sharedExperience.size.coerceAtMost(2) * 4).coerceAtLeast(4)

        val compatibility = (interestPoints + skillPoints + rolePoints + experiencePoints)
            .coerceIn(52, 98)

        return MatchResult(
            compatibility = compatibility,
            reasons = buildReasons(
                me = me,
                candidate = candidate,
                complementarySkills = complementarySkills,
                complementaryFamilies = complementaryFamilies,
                userFamilies = userFamilies,
                sharedTopics = sharedTopics,
                differentRole = differentRole
            ),
            complementarySkills = complementarySkills,
            sharedInterests = if (sharedTopics.isNotEmpty()) sharedTopics else candidateInterests.take(2),
            sharedExperience = sharedExperience,
            profileReady = true
        )
    }

    private fun buildReasons(
        me: StudentMatchProfile?,
        candidate: StudentMatchProfile,
        complementarySkills: List<String>,
        complementaryFamilies: Set<SkillFamily>,
        userFamilies: Set<SkillFamily>,
        sharedTopics: List<String>,
        differentRole: Boolean
    ): List<String> {
        val reasons = mutableListOf<String>()

        if (sharedTopics.isNotEmpty()) {
            reasons += "Matched interests: ${sharedTopics.take(3).joinToString(", ")}."
        } else {
            val candidateFocus = (candidate.areasOfInterest + candidate.interests).distinct().take(2)
            if (candidateFocus.isNotEmpty()) {
                reasons += "Focuses on ${candidateFocus.joinToString(" & ")} ventures."
            }
        }

        if (complementaryFamilies.isNotEmpty()) {
            val yours = if (userFamilies.isEmpty()) "your profile" else "your ${phrase(userFamilies)} background"
            reasons += "Complements $yours with ${phrase(complementaryFamilies)} skills."
        } else if (complementarySkills.isNotEmpty()) {
            reasons += "Brings ${complementarySkills.take(2).joinToString(" and ")} to the team."
        }

        if (me != null && differentRole) {
            reasons += "Covers the ${candidate.preferredRole.personLabel().lowercase()} role alongside you."
        }

        return reasons.take(3)
    }

    private fun familyPoints(familyCount: Int): Int = when (familyCount) {
        0 -> 10
        1 -> 18
        2 -> 21
        else -> 24
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
}
