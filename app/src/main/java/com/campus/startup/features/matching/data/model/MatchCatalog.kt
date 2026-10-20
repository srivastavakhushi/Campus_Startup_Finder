package com.campus.startup.features.matching.data.model

import com.campus.startup.features.recruitment.data.model.StartupRoleCategory

/**
 * Fixed choices for the co-founder profile form and discovery filters.
 * Skill names come from the recruitment catalog so both features speak the same language.
 */
object MatchCatalog {
    const val CURRENT_USER_ID = "profile_me"

    val AVATARS = listOf("DEV", "DES", "MKT", "OPS", "AI", "PM", "BIZ", "LEAD")

    val COURSES = listOf(
        "B.Tech CSE",
        "B.Tech AI & DS",
        "B.Tech ECE",
        "B.Tech IT",
        "B.Des",
        "BBA / MBA",
        "B.Tech Mechanical",
        "B.Com / Economics"
    )

    val YEARS = listOf("1st Year", "2nd Year", "3rd Year", "4th Year", "Postgraduate")

    val INTERESTS = listOf(
        "Artificial Intelligence",
        "Product Design",
        "Public Speaking",
        "Open Source",
        "Finance",
        "Sustainability",
        "Community",
        "Hardware",
        "Writing",
        "Data"
    )

    val AREAS = listOf(
        "EdTech",
        "FinTech",
        "HealthTech",
        "AI / ML",
        "SaaS",
        "Climate",
        "Campus consumer",
        "Social impact",
        "E-Commerce",
        "Hardware & IoT",
        "Gaming",
        "Marketplace"
    )

    val EXPERIENCE_TAGS = listOf(
        "Shipped a product",
        "Hackathon",
        "Internship",
        "Research",
        "Campus leadership",
        "First-time founder"
    )

    val ROLES = listOf(
        StartupRoleCategory.DEVELOPER,
        StartupRoleCategory.DESIGNER,
        StartupRoleCategory.MARKETER,
        StartupRoleCategory.INTERN
    )
}

fun StartupRoleCategory.personLabel(): String = when (this) {
    StartupRoleCategory.DEVELOPER -> "Developer"
    StartupRoleCategory.DESIGNER -> "Designer"
    StartupRoleCategory.MARKETER -> "Marketer"
    StartupRoleCategory.INTERN -> "Generalist"
    StartupRoleCategory.ALL -> "Any role"
}
