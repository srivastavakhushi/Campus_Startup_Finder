package com.campus.startup.features.matching.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Biotech
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Nature
import androidx.compose.material.icons.outlined.School
import androidx.compose.ui.graphics.vector.ImageVector

enum class TeamPreference(
    val displayName: String,
    val emoji: String
) {
    FOUNDING("Founding team", ""),
    HACKATHON("Hackathon squad", ""),
    RESEARCH("Research spinout", ""),
    SOCIAL("Social impact venture", ""),
    CAMPUS("Campus consumer startup", "");

    fun getIcon(): ImageVector = when (this) {
        FOUNDING -> Icons.Outlined.Flag
        HACKATHON -> Icons.Outlined.Bolt
        RESEARCH -> Icons.Outlined.Biotech
        SOCIAL -> Icons.Outlined.Nature
        CAMPUS -> Icons.Outlined.School
    }

    companion object {
        fun fromStored(value: String): TeamPreference {
            return values().find { it.name == value } ?: FOUNDING
        }
    }
}
