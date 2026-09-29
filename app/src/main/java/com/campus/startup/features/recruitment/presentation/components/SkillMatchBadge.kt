package com.campus.startup.features.recruitment.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.campus.startup.core.theme.StartupGreen
import com.campus.startup.core.theme.StartupGreenBg

@Composable
fun SkillMatchBadge(
    matchPercentage: Int,
    matchedSkills: List<String>,
    modifier: Modifier = Modifier
) {
    if (matchPercentage == 0) return

    val (badgeBg, badgeText, icon) = when {
        matchPercentage >= 75 -> Triple(
            Color(0xFFD1FAE5), // Light emerald
            Color(0xFF047857), // Dark emerald
            "🔥 "
        )
        matchPercentage >= 40 -> Triple(
            Color(0xFFFEF3C7), // Light amber
            Color(0xFFB45309), // Dark amber
            "⚡ "
        )
        else -> Triple(
            Color(0xFFE0E7FF), // Light indigo
            Color(0xFF3730A3), // Dark indigo
            "🎯 "
        )
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = badgeBg
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$icon$matchPercentage% Skill Match",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = badgeText
            )
            if (matchedSkills.isNotEmpty()) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "(${matchedSkills.take(2).joinToString(", ")}${if (matchedSkills.size > 2) "..." else ""})",
                    style = MaterialTheme.typography.labelSmall,
                    color = badgeText.copy(alpha = 0.85f),
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}
