package com.campus.startup.features.recruitment.data.model

data class StartupOpening(
    val id: String,
    val startupName: String,
    val startupLogo: String = "CS",
    val startupStage: String,
    val title: String,
    val roleCategory: StartupRoleCategory,
    val requiredSkills: List<String>,
    val workType: String = "Hybrid",
    val commitment: String = "Internship",
    val stipend: String,
    val location: String = "Campus Incubation Center",
    val deadlineDays: Int = 14,
    val description: String,
    val responsibilities: List<String> = emptyList(),
    val perks: List<String> = emptyList(),
    val founderName: String = "Campus Founder",
    val founderEmail: String = "founder@startup.campus.edu",
    val isSaved: Boolean = false,
    val isPostedByMe: Boolean = false,
    val matchPercentage: Int = 0,
    val matchedSkills: List<String> = emptyList()
)
