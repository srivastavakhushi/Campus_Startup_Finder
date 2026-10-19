package com.campus.startup.features.matching.presentation.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.campus.startup.core.theme.DarkBorder
import com.campus.startup.core.theme.PrimaryGreen
import com.campus.startup.core.theme.PrimaryGreenContainer
import com.campus.startup.features.matching.data.model.MatchResult

@Composable
fun CompatibilityBadge(
    match: MatchResult,
    modifier: Modifier = Modifier
) {
    if (!match.profileReady) {
        Surface(
            modifier = modifier.border(1.dp, DarkBorder, RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Text(
                text = "Complete profile to score fit",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
        return
    }

    val (badgeBg, badgeText, icon) = when {
        match.compatibility >= 70 -> Triple(PrimaryGreenContainer, PrimaryGreen, Icons.Outlined.AutoAwesome)
        match.compatibility >= 45 -> Triple(Color(0xFF262112), Color(0xFFFBBF24), Icons.Outlined.TrendingUp)
        else -> Triple(Color(0xFF19222E), Color(0xFF60A5FA), Icons.Outlined.CheckCircle)
    }

    val shape = RoundedCornerShape(12.dp)
    Surface(
        modifier = modifier.border(1.dp, badgeText.copy(alpha = 0.35f), shape),
        shape = shape,
        color = badgeBg
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = badgeText,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = "${match.compatibility}% Match",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = badgeText
            )
            if (match.complementarySkills.isNotEmpty()) {
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = "• ${match.complementarySkills.take(2).joinToString(", ")}",
                    style = MaterialTheme.typography.labelSmall,
                    color = badgeText.copy(alpha = 0.85f)
                )
            }
        }
    }
}
