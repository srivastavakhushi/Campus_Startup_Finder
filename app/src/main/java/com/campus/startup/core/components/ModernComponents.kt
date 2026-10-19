package com.campus.startup.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.campus.startup.core.theme.AccentTeal
import com.campus.startup.core.theme.DarkBorder
import com.campus.startup.core.theme.PrimaryGreen
import com.campus.startup.core.theme.PrimaryGreenLight
import com.campus.startup.features.recruitment.data.model.StartupRoleCategory

/**
 * Modern avatar component inspired by Reference Images 1 and 2.
 * Renders high-contrast professional monograms with subtle green accent rings
 * instead of emojis.
 */
@Composable
fun ModernAvatar(
    name: String,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    role: StartupRoleCategory? = null,
    showActiveIndicator: Boolean = false
) {
    val initials = getInitials(name)
    val gradientColors = when (role) {
        StartupRoleCategory.DEVELOPER -> listOf(Color(0xFF1E293B), Color(0xFF0F172A))
        StartupRoleCategory.DESIGNER -> listOf(Color(0xFF2A1B38), Color(0xFF1A1324))
        StartupRoleCategory.MARKETER -> listOf(Color(0xFF2E2413), Color(0xFF1F170B))
        StartupRoleCategory.INTERN -> listOf(Color(0xFF132B1C), Color(0xFF0C1D13))
        else -> listOf(Color(0xFF1A222C), Color(0xFF11171E))
    }

    val ringColor = when (role) {
        StartupRoleCategory.DEVELOPER -> Color(0xFF38BDF8)
        StartupRoleCategory.DESIGNER -> Color(0xFFC084FC)
        StartupRoleCategory.MARKETER -> Color(0xFFFBBF24)
        StartupRoleCategory.INTERN -> PrimaryGreen
        else -> PrimaryGreenLight
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(Brush.linearGradient(gradientColors))
                .border(1.5.dp, ringColor.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initials,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = (size.value * 0.38f).sp,
                letterSpacing = 0.5.sp
            )
        }

        if (showActiveIndicator) {
            Box(
                modifier = Modifier
                    .size(size * 0.28f)
                    .align(Alignment.BottomEnd)
                    .offset(x = 1.dp, y = 1.dp)
                    .clip(CircleShape)
                    .background(PrimaryGreen)
                    .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
            )
        }
    }
}

/**
 * Modern startup brand logo badge (e.g. "CB", "SG", "FW") inspired by Reference Image 1 cards.
 */
@Composable
fun StartupBadgeLogo(
    startupName: String,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    shapeRadius: Dp = 12.dp
) {
    val initials = getInitials(startupName)
    val shape = RoundedCornerShape(shapeRadius)

    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF142B1A), Color(0xFF1B3823))
                )
            )
            .border(1.dp, PrimaryGreen.copy(alpha = 0.35f), shape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            color = PrimaryGreenLight,
            fontWeight = FontWeight.ExtraBold,
            fontSize = (size.value * 0.38f).sp,
            letterSpacing = 0.5.sp
        )
    }
}

fun getInitials(text: String): String {
    val cleaned = text.filter { it.isLetterOrDigit() || it.isWhitespace() }.trim()
    if (cleaned.isEmpty()) return "CS"
    val parts = cleaned.split("\\s+".toRegex()).filter { it.isNotEmpty() }
    return when {
        parts.size >= 2 -> "${parts[0].first()}${parts[1].first()}".uppercase()
        parts.isNotEmpty() -> parts[0].take(2).uppercase()
        else -> "CS"
    }
}
