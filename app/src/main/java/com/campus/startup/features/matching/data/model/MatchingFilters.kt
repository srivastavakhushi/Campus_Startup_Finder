package com.campus.startup.features.matching.data.model

import com.campus.startup.features.recruitment.data.model.StartupRoleCategory

data class MatchingFilters(
    val query: String = "",
    val role: StartupRoleCategory = StartupRoleCategory.ALL,
    val skills: Set<String> = emptySet(),
    val interests: Set<String> = emptySet(),
    val experienceTags: Set<String> = emptySet(),
    val complementaryOnly: Boolean = false
) {
    val activeCount: Int
        get() {
            var count = 0
            if (query.isNotBlank()) count += 1
            if (role != StartupRoleCategory.ALL) count += 1
            if (skills.isNotEmpty()) count += 1
            if (interests.isNotEmpty()) count += 1
            if (experienceTags.isNotEmpty()) count += 1
            if (complementaryOnly) count += 1
            return count
        }
}

object MatchingDirectory {
    fun filter(
        students: List<DiscoverableProfile>,
        filters: MatchingFilters,
        profileReady: Boolean
    ): List<DiscoverableProfile> {
        return students.filter { item ->
            val profile = item.profile
            val matchesRole = filters.role == StartupRoleCategory.ALL || profile.preferredRole == filters.role
            val matchesQuery = filters.query.isBlank() || searchableText(profile).any { field ->
                field.contains(filters.query, ignoreCase = true)
            }
            val matchesSkill = filters.skills.isEmpty() || filters.skills.any { wanted ->
                profile.skills.any { it.equals(wanted, ignoreCase = true) }
            }
            val matchesInterest = filters.interests.isEmpty() || filters.interests.any { wanted ->
                (profile.interests + profile.areasOfInterest).any { it.equals(wanted, ignoreCase = true) }
            }
            val matchesExperience = filters.experienceTags.isEmpty() || filters.experienceTags.any { tag ->
                profile.experienceTags.any { it.equals(tag, ignoreCase = true) } ||
                    profile.experience.contains(tag, ignoreCase = true)
            }
            val matchesComplement = !filters.complementaryOnly ||
                !profileReady ||
                item.match.complementarySkills.isNotEmpty()

            matchesRole && matchesQuery && matchesSkill && matchesInterest && matchesExperience && matchesComplement
        }.sortedByDescending { it.match.compatibility }
    }

    private fun searchableText(profile: StudentMatchProfile): List<String> {
        return listOf(
            profile.name,
            profile.branch,
            profile.year,
            profile.college,
            profile.experience,
            profile.goals,
            profile.preferredRole.displayName,
            profile.preferredRole.personLabel(),
            profile.teamPreference.displayName
        ) + profile.skills + profile.interests + profile.areasOfInterest + profile.experienceTags
    }
}
