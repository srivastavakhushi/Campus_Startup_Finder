package com.campus.startup.features.opportunities.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.BusinessCenter
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.MonetizationOn
import androidx.compose.material.icons.outlined.RocketLaunch
import androidx.compose.material.icons.outlined.WorkspacePremium
import androidx.compose.ui.graphics.vector.ImageVector

enum class OpportunityType(val displayName: String, val emoji: String) {
    ALL("All", ""),
    STARTUP_ROLE("Startup Role", ""),
    HACKATHON("Hackathon", ""),
    INTERNSHIP("Internship", ""),
    GRANT("Grant & Funding", ""),
    COMPETITION("Pitch / Competition", "");

    fun getIcon(): ImageVector = when (this) {
        ALL -> Icons.Outlined.AutoAwesome
        STARTUP_ROLE -> Icons.Outlined.RocketLaunch
        HACKATHON -> Icons.Outlined.Code
        INTERNSHIP -> Icons.Outlined.BusinessCenter
        GRANT -> Icons.Outlined.MonetizationOn
        COMPETITION -> Icons.Outlined.WorkspacePremium
    }
}
