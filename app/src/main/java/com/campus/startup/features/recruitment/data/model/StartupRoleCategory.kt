package com.campus.startup.features.recruitment.data.model

enum class StartupRoleCategory(
    val displayName: String,
    val emoji: String,
    val description: String
) {
    ALL("All Roles", "✨", "Explore all startup openings across campus"),
    DEVELOPER("Developers", "💻", "Mobile, Web, Backend, AI/ML & Full-Stack engineers"),
    DESIGNER("Designers", "🎨", "UI/UX, Product, Brand & Visual creators"),
    MARKETER("Marketers", "📢", "Growth hackers, Content creators & Campus leads"),
    INTERN("Interns", "🎓", "Flexible student internships across tech & business")
}
