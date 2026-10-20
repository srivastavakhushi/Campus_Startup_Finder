package com.campus.startup.features.matching.domain

import com.campus.startup.features.matching.data.model.StudentMatchProfile
import com.campus.startup.features.recruitment.data.model.StartupRoleCategory

data class ProfileFormErrors(
    val name: String? = null,
    val academic: String? = null,
    val age: String? = null,
    val skills: String? = null,
    val interests: String? = null,
    val areas: String? = null,
    val goals: String? = null,
    val role: String? = null
) {
    val isValid: Boolean
        get() = listOf(name, academic, age, skills, interests, areas, goals, role).all { it == null }
}

object ProfileValidator {
    fun validate(profile: StudentMatchProfile): ProfileFormErrors {
        val nameError = if (profile.name.trim().length < 2) {
            "Enter your name (at least 2 characters)."
        } else {
            null
        }
        val effectiveCourse = profile.course.ifBlank { profile.branch }
        val academicError = if (effectiveCourse.trim().length < 2 || profile.year.isBlank()) {
            "Add your course/branch and year."
        } else {
            null
        }
        val ageError = if (profile.age.isNotBlank() && profile.age.trim().toIntOrNull() !in 15..80) {
            "Enter a valid age (e.g. 20)."
        } else {
            null
        }
        val skillsError = if (profile.skills.isEmpty()) "Pick at least one skill." else null
        val interestsError = if (profile.interests.isEmpty() && profile.areasOfInterest.isEmpty()) {
            "Pick at least one interest or startup domain."
        } else if (profile.interests.isEmpty()) {
            "Pick at least one interest."
        } else {
            null
        }
        val areasError = if (profile.areasOfInterest.isEmpty()) "Pick at least one startup area/domain." else null
        val goalsError = if (profile.goals.trim().length < 12) {
            "Describe your entrepreneurial goals in a short sentence."
        } else {
            null
        }
        val roleError = if (profile.preferredRole == StartupRoleCategory.ALL) {
            "Choose the role you want to play on a team."
        } else {
            null
        }
        return ProfileFormErrors(
            name = nameError,
            academic = academicError,
            age = ageError,
            skills = skillsError,
            interests = interestsError,
            areas = areasError,
            goals = goalsError,
            role = roleError
        )
    }
}
