package com.campus.startup.features.recruitment.presentation.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.campus.startup.core.theme.PrimaryGreen
import com.campus.startup.core.theme.PrimaryGreenContainer

@Composable
fun SkillMatchBadge(
    matchPercentage: Int,
    matchedSkills: List<String>,
    modifier: Modifier = Modifier
) {
    if (matchPercentage == 0) return

    val (badgeBg, badgeText, icon) = when {
        matchPercentage >= 75 -> Triple(
            PrimaryGreenContainer,
            PrimaryGreen,
            Icons.Outlined.AutoAwesome
        )
        matchPercentage >= 40 -> Triple(
            Color(0xFF262112),
            Color(0xFFFBBF24),
            Icons.Outlined.TrendingUp
        )
        else -> Triple(
            Color(0xFF19222E),
            Color(0xFF60A5FA),
            Icons.Outlined.CheckCircle
        )
    }

    val shape = RoundedCornerShape(12.dp)
    Surface(
        modifier = modifier.border(1.dp, badgeText.copy(alpha = 0.35f), shape),
        shape = shape,
        color = badgeBg
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = badgeText,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "$matchPercentage% Skill Match",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = badgeText
            )
            if (matchedSkills.isNotEmpty()) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "• ${matchedSkills.take(2).joinToString(", ")}",
                    style = MaterialTheme.typography.labelSmall,
                    color = badgeText.copy(alpha = 0.85f),
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}
