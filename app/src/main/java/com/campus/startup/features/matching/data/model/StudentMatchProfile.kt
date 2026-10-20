package com.campus.startup.features.matching.data.model

import com.campus.startup.features.recruitment.data.model.StartupRoleCategory

data class StudentMatchProfile(
    val id: String,
    val name: String,
    val avatar: String = "DEV",
    val branch: String = "",
    val course: String = "",
    val age: String = "",
    val year: String = "",
    val college: String = "",
    val resumeUri: String = "",
    val resumeName: String = "",
    val recruitedStartupIds: List<String> = emptyList(),
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
    val displayCourse: String
        get() = course.ifBlank { branch }

    val academicLine: String
        get() = listOf(
            displayCourse,
            year,
            if (age.isNotBlank()) "$age yrs" else "",
            college
        ).filter { it.isNotBlank() }.joinToString(" · ")
}
