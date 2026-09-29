package com.campus.startup.features.recruitment.data.model

data class SkillItem(
    val name: String,
    val category: StartupRoleCategory
) {
    companion object {
        val ALL_SKILLS = listOf(
            // Developer skills
            SkillItem("Kotlin", StartupRoleCategory.DEVELOPER),
            SkillItem("Jetpack Compose", StartupRoleCategory.DEVELOPER),
            SkillItem("Flutter", StartupRoleCategory.DEVELOPER),
            SkillItem("React / Next.js", StartupRoleCategory.DEVELOPER),
            SkillItem("Node.js / Express", StartupRoleCategory.DEVELOPER),
            SkillItem("Python", StartupRoleCategory.DEVELOPER),
            SkillItem("AI & LLMs", StartupRoleCategory.DEVELOPER),
            SkillItem("Firebase / SQL", StartupRoleCategory.DEVELOPER),

            // Designer skills
            SkillItem("Figma", StartupRoleCategory.DESIGNER),
            SkillItem("UI/UX Design", StartupRoleCategory.DESIGNER),
            SkillItem("Design Systems", StartupRoleCategory.DESIGNER),
            SkillItem("Graphic Design", StartupRoleCategory.DESIGNER),
            SkillItem("Prototyping", StartupRoleCategory.DESIGNER),
            SkillItem("Canva & Adobe Suite", StartupRoleCategory.DESIGNER),

            // Marketer skills
            SkillItem("Growth Marketing", StartupRoleCategory.MARKETER),
            SkillItem("Social Media & Reels", StartupRoleCategory.MARKETER),
            SkillItem("Content Writing", StartupRoleCategory.MARKETER),
            SkillItem("Campus Outreach", StartupRoleCategory.MARKETER),
            SkillItem("SEO & Analytics", StartupRoleCategory.MARKETER),
            SkillItem("Community Building", StartupRoleCategory.MARKETER),

            // Intern / General skills
            SkillItem("Product Management", StartupRoleCategory.INTERN),
            SkillItem("Operations", StartupRoleCategory.INTERN),
            SkillItem("Market Research", StartupRoleCategory.INTERN),
            SkillItem("Sales & Pitching", StartupRoleCategory.INTERN)
        )
    }
}
