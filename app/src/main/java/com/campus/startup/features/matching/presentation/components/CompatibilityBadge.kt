package com.campus.startup.features.matching.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.campus.startup.features.matching.data.model.MatchResult

@Composable
fun CompatibilityBadge(
    match: MatchResult,
    modifier: Modifier = Modifier
) {
    if (!match.profileReady) {
        Surface(
            modifier = modifier,
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Text(
                text = "Add your profile to score fit",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
        return
    }

    val (badgeBg, badgeText, icon) = when {
        match.compatibility >= 70 -> Triple(Color(0xFFD1FAE5), Color(0xFF047857), "🔥 ")
        match.compatibility >= 45 -> Triple(Color(0xFFFEF3C7), Color(0xFFB45309), "⚡ ")
        else -> Triple(Color(0xFFE0E7FF), Color(0xFF3730A3), "🎯 ")
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
                text = "$icon${match.compatibility}% complementary",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = badgeText
            )
            if (match.complementarySkills.isNotEmpty()) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "(${match.complementarySkills.take(2).joinToString(", ")}${if (match.complementarySkills.size > 2) "..." else ""})",
                    style = MaterialTheme.typography.labelSmall,
                    color = badgeText.copy(alpha = 0.85f)
                )
            }
        }
    }
}
