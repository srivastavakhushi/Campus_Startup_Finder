package com.campus.startup.core.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Reference Image 2 & 1 Color Palette: Deep Charcoal with Vibrant Neon-Lime / Emerald Green
val PrimaryGreen = Color(0xFF5BE834)
val PrimaryGreenLight = Color(0xFF7BFA5B)
val PrimaryGreenDark = Color(0xFF38B81B)
val PrimaryGreenContainer = Color(0xFF132A17)
val OnPrimaryGreenContainer = Color(0xFF6DF048)

// Secondary & Accent Colors
val AccentEmerald = Color(0xFF10B981)
val AccentTeal = Color(0xFF2DD4BF)
val AccentAmber = Color(0xFFF59E0B)
val AccentAmberContainer = Color(0xFF2A2312)
val AccentRose = Color(0xFFF43F5E)
val AccentRoseContainer = Color(0xFF2C1318)
val AccentIndigo = Color(0xFF6366F1)
val AccentIndigoContainer = Color(0xFF181B30)

// Semantic Tag Colors
val HackathonAmber = Color(0xFFF59E0B)
val HackathonAmberBg = Color(0xFF261F12)
val StartupGreen = Color(0xFF5BE834)
val StartupGreenBg = Color(0xFF132A17)
val InternshipIndigo = Color(0xFF818CF8)
val InternshipIndigoBg = Color(0xFF191D33)
val GrantRose = Color(0xFFFB7185)
val GrantRoseBg = Color(0xFF2E161C)

// Neutral Dark Background & Surface Colors (Reference Image 2 Dark Theme)
val DarkBackground = Color(0xFF0C0F12)
val DarkSurface = Color(0xFF15191E)
val DarkSurfaceVariant = Color(0xFF1D232B)
val DarkSurfaceElevated = Color(0xFF232A34)
val DarkBorder = Color(0xFF27323E)
val DarkBorderSubtle = Color(0xFF1C242C)

val DarkTextPrimary = Color(0xFFF8FAFC)
val DarkTextSecondary = Color(0xFF94A3B8)
val DarkTextMuted = Color(0xFF64748B)

// Light Theme Neutrals (Refined Green Enterprise)
val LightBackground = Color(0xFFF4F6F5)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFE8ECE9)
val LightBorder = Color(0xFFD6DDD7)
val LightTextPrimary = Color(0xFF0F1713)
val LightTextSecondary = Color(0xFF4A5568)
val LightTextMuted = Color(0xFF718096)

// Gradients inspired by Reference Image 1 & 2
val GreenAccentGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFF5BE834), Color(0xFF10B981))
)

val DarkCardGlowGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF18231C), Color(0xFF15191E))
)

val SwipeConnectGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFF22C55E), Color(0xFF10B981))
)

val SwipePassGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFFEF4444), Color(0xFFF43F5E))
)

// Aliases mapped directly to the new green theme for complete backwards compatibility
val PrimaryBlue = PrimaryGreen
val PrimaryBlueDark = PrimaryGreenDark
val PrimaryBlueLight = PrimaryGreenLight
val AccentPurple = AccentEmerald
val SurfaceLight = LightSurface
val SurfaceVariantLight = DarkSurfaceVariant
val SurfaceDark = DarkSurface
val SurfaceVariantDark = DarkSurfaceVariant
val BackgroundLight = DarkBackground
val BackgroundDark = DarkBackground
val TextPrimary = DarkTextPrimary
val TextSecondary = DarkTextSecondary
val TextPrimaryDark = DarkTextPrimary
val TextSecondaryDark = DarkTextSecondary
val TextMuted = DarkTextMuted
val BorderLight = DarkBorder

