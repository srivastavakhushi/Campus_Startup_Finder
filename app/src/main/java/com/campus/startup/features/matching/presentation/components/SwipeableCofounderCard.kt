package com.campus.startup.features.matching.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.campus.startup.core.components.ModernAvatar
import com.campus.startup.core.theme.*
import com.campus.startup.features.matching.data.model.DiscoverableProfile
import com.campus.startup.features.matching.data.model.personLabel
import com.campus.startup.features.recruitment.data.model.StartupRoleCategory
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * High-fidelity swipe card inspired by dating applications and Reference Image 1 Screen 2.
 * Supports:
 * - Touch & mouse drag gestures with rotational tilt
 * - Left swipe = Pass, Right swipe = Connect
 * - "PASS" and "CONNECT" interactive stamp indicators
 * - Prominent compatibility score percentage & reason breakdown
 * - Accessible action buttons underneath the card
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SwipeableCofounderCard(
    student: DiscoverableProfile,
    onSwipeLeft: (DiscoverableProfile) -> Unit,
    onSwipeRight: (DiscoverableProfile) -> Unit,
    onViewDetails: (DiscoverableProfile) -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val swipeThreshold = screenWidthPx * 0.35f

    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }

    // Re-center when profile changes
    LaunchedEffect(student.profile.id) {
        offsetX.snapTo(0f)
        offsetY.snapTo(0f)
    }

    val dragRotation = (offsetX.value / 30f).coerceIn(-18f, 18f)
    val rightSwipeAlpha = (offsetX.value / 180f).coerceIn(0f, 1f)
    val leftSwipeAlpha = (-offsetX.value / 180f).coerceIn(0f, 1f)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Main Swipeable Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .graphicsLayer {
                    translationX = offsetX.value
                    translationY = offsetY.value
                    rotationZ = dragRotation
                }
                .pointerInput(student.profile.id) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            coroutineScope.launch {
                                if (offsetX.value > swipeThreshold) {
                                    // Animate off screen right
                                    offsetX.animateTo(screenWidthPx * 1.3f, tween(260))
                                    onSwipeRight(student)
                                } else if (offsetX.value < -swipeThreshold) {
                                    // Animate off screen left
                                    offsetX.animateTo(-screenWidthPx * 1.3f, tween(260))
                                    onSwipeLeft(student)
                                } else {
                                    // Return to center
                                    launch { offsetX.animateTo(0f, spring(stiffness = Spring.StiffnessMediumLow)) }
                                    launch { offsetY.animateTo(0f, spring(stiffness = Spring.StiffnessMediumLow)) }
                                }
                            }
                        },
                        onDragCancel = {
                            coroutineScope.launch {
                                offsetX.animateTo(0f)
                                offsetY.animateTo(0f)
                            }
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            coroutineScope.launch {
                                offsetX.snapTo(offsetX.value + dragAmount)
                                offsetY.snapTo(0f)
                            }
                        }
                    )
                }
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(12.dp, RoundedCornerShape(24.dp), spotColor = PrimaryGreen.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = BorderStroke(1.dp, DarkBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp)
                ) {
                    // Top Card Header: Avatar & Key Profile Info
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ModernAvatar(
                            name = student.profile.name,
                            role = student.profile.preferredRole,
                            size = 64.dp,
                            showActiveIndicator = true
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = student.profile.name,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = DarkTextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (student.profile.academicLine.isNotBlank()) {
                                Text(
                                    text = student.profile.academicLine,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = DarkTextSecondary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = PrimaryGreenContainer,
                                    border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.4f))
                                ) {
                                    Text(
                                        text = student.profile.preferredRole.personLabel(),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryGreen,
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = DarkSurfaceVariant,
                                    border = BorderStroke(1.dp, DarkBorder)
                                ) {
                                    Text(
                                        text = student.profile.teamPreference.displayName,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = DarkTextSecondary,
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Prominent Compatibility Score Banner (Requirement 3B)
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF102415),
                        border = BorderStroke(1.5.dp, PrimaryGreen.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Outlined.AutoAwesome,
                                        contentDescription = null,
                                        tint = PrimaryGreen,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Compatibility Score",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = DarkTextSecondary
                                    )
                                }

                                Text(
                                    text = if (student.match.profileReady) "${student.match.compatibility}% Match" else "Unscored",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = PrimaryGreen
                                )
                            }

                            if (student.match.profileReady) {
                                Spacer(modifier = Modifier.height(8.dp))
                                LinearProgressIndicator(
                                    progress = { student.match.compatibility / 100f },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = PrimaryGreen,
                                    trackColor = Color(0xFF1C3A23)
                                )
                            }

                            student.match.reasons.firstOrNull()?.let { reason ->
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = reason,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFC7ECC5),
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }

                    // Vision / Quote Section (Reference 1 style)
                    if (student.profile.goals.isNotBlank()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = DarkSurfaceVariant,
                            border = BorderStroke(1.dp, DarkBorder)
                        ) {
                            Text(
                                text = "\"${student.profile.goals}\"",
                                style = MaterialTheme.typography.bodyMedium,
                                fontStyle = FontStyle.Italic,
                                color = DarkTextPrimary,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    // Complementary Skills Section
                    if (student.profile.skills.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Complementary Skills",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = DarkTextSecondary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            student.profile.skills.forEach { skill ->
                                val isComplementary = student.match.complementarySkills.any { it.equals(skill, ignoreCase = true) }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isComplementary) PrimaryGreenContainer else DarkSurfaceVariant,
                                    border = BorderStroke(
                                        1.dp,
                                        if (isComplementary) PrimaryGreen.copy(alpha = 0.5f) else DarkBorder
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (isComplementary) {
                                            Icon(
                                                imageVector = Icons.Outlined.Check,
                                                contentDescription = null,
                                                tint = PrimaryGreen,
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                        }
                                        Text(
                                            text = skill,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = if (isComplementary) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isComplementary) PrimaryGreen else DarkTextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Interests & Startup Focus
                    val allInterests = (student.profile.interests + student.profile.areasOfInterest).distinct()
                    if (allInterests.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Interests & Domain Focus",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = DarkTextSecondary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            allInterests.forEach { interest ->
                                val isShared = student.match.sharedInterests.any { it.equals(interest, ignoreCase = true) }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isShared) Color(0xFF132832) else DarkSurfaceVariant,
                                    border = BorderStroke(
                                        1.dp,
                                        if (isShared) AccentTeal.copy(alpha = 0.5f) else DarkBorder
                                    )
                                ) {
                                    Text(
                                        text = interest,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (isShared) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isShared) AccentTeal else DarkTextSecondary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Experience Highlight
                    if (student.profile.experience.isNotBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Experience Highlight",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = DarkTextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = student.profile.experience,
                            style = MaterialTheme.typography.bodySmall,
                            color = DarkTextPrimary,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // Right Swipe "CONNECT" Stamp Overlay
            if (rightSwipeAlpha > 0.05f) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(24.dp)
                        .rotate(-15f)
                        .graphicsLayer { alpha = rightSwipeAlpha },
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF123518).copy(alpha = 0.95f),
                    border = BorderStroke(3.dp, PrimaryGreen)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Handshake,
                            contentDescription = null,
                            tint = PrimaryGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "CONNECT",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = PrimaryGreen,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            // Left Swipe "PASS" Stamp Overlay
            if (leftSwipeAlpha > 0.05f) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(24.dp)
                        .rotate(15f)
                        .graphicsLayer { alpha = leftSwipeAlpha },
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF381519).copy(alpha = 0.95f),
                    border = BorderStroke(3.dp, Color(0xFFEF4444))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "PASS",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFEF4444),
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Accessible Action Buttons Underneath Card (Requirement 3A & Reference 1)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Pass Button (Left)
            IconButton(
                onClick = {
                    coroutineScope.launch {
                        offsetX.animateTo(-screenWidthPx * 1.2f, tween(260))
                        onSwipeLeft(student)
                    }
                },
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(DarkSurfaceElevated)
                    .border(1.dp, Color(0xFFEF4444).copy(alpha = 0.4f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Pass on profile",
                    tint = Color(0xFFF87171),
                    modifier = Modifier.size(28.dp)
                )
            }

            // View Details Button (Center)
            IconButton(
                onClick = { onViewDetails(student) },
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(DarkSurfaceVariant)
                    .border(1.dp, DarkBorder, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Visibility,
                    contentDescription = "View full details",
                    tint = DarkTextSecondary,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Connect Button (Right)
            IconButton(
                onClick = {
                    coroutineScope.launch {
                        offsetX.animateTo(screenWidthPx * 1.2f, tween(260))
                        onSwipeRight(student)
                    }
                },
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(GreenAccentGradient)
                    .shadow(8.dp, CircleShape, spotColor = PrimaryGreen.copy(alpha = 0.4f))
            ) {
                Icon(
                    imageVector = Icons.Default.Handshake,
                    contentDescription = "Connect with student",
                    tint = Color(0xFF071206),
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}
