package com.campus.startup.features.recruitment.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Workspaces
import androidx.compose.ui.graphics.vector.ImageVector

enum class StartupRoleCategory(
    val displayName: String,
    val emoji: String, // Kept empty for backwards compatibility, no emojis rendered
    val description: String
) {
    ALL("All Roles", "", "Explore all startup openings across campus"),
    DEVELOPER("Developers", "", "Mobile, Web, Backend, AI/ML & Full-Stack engineers"),
    DESIGNER("Designers", "", "UI/UX, Product, Brand & Visual creators"),
    MARKETER("Marketers", "", "Growth hackers, Content creators & Campus leads"),
    INTERN("Interns", "", "Flexible student internships across tech & business");

    fun getIcon(): ImageVector = when (this) {
        ALL -> Icons.Outlined.Workspaces
        DEVELOPER -> Icons.Outlined.Code
        DESIGNER -> Icons.Outlined.Palette
        MARKETER -> Icons.Outlined.Campaign
        INTERN -> Icons.Outlined.School
    }
}
