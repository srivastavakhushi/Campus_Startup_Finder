package com.campus.startup.features.matching.data.model

import com.campus.startup.features.recruitment.data.model.StartupRoleCategory

data class StudentMatchProfile(
    val id: String,
    val name: String,
    val avatar: String = "DEV",
    val branch: String = "",
    val year: String = "",
    val college: String = "",
    val skills: List<String> = emptyList(),
    val interests: List<String> = emptyList(),
    val areasOfInterest: List<String> = emptyList(),
    val experience: String = "",
    val experienceTags: List<String> = emptyList(),
    val goals: String = "",
    val preferredRole: StartupRoleCategory = StartupRoleCategory.DEVELOPER,
    val teamPreference: TeamPreference = TeamPreference.FOUNDING,
    val isCurrentUser: Boolean = false,
    val profileComplete: Boolean = false
) {
    val academicLine: String
        get() = listOf(branch, year, college).filter { it.isNotBlank() }.joinToString(" · ")
}
