package com.campus.startup.features.matching.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.campus.startup.core.components.ModernAvatar
import com.campus.startup.core.theme.DarkBorder
import com.campus.startup.core.theme.DarkSurface
import com.campus.startup.core.theme.PrimaryGreen
import com.campus.startup.core.theme.PrimaryGreenContainer
import com.campus.startup.features.matching.data.model.ConnectionStatus
import com.campus.startup.features.matching.data.model.DiscoverableProfile
import com.campus.startup.features.matching.data.model.personLabel
import com.campus.startup.features.recruitment.data.model.StartupRoleCategory

@Composable
fun CofounderCard(
    student: DiscoverableProfile,
    onClick: () -> Unit,
    onConnect: () -> Unit,
    onAccept: () -> Unit,
    modifier: Modifier = Modifier
) {
    val profile = student.profile
    val roleColors = roleColors(profile.preferredRole)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 7.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(1.dp, DarkBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ModernAvatar(
                    name = profile.name,
                    role = profile.preferredRole,
                    size = 48.dp,
                    showActiveIndicator = true
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = profile.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (profile.academicLine.isNotBlank()) {
                        Text(
                            text = profile.academicLine,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                ConnectionAction(
                    status = student.connectionStatus,
                    onConnect = onConnect,
                    onAccept = onAccept
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = roleColors.first
                ) {
                    Text(
                        text = profile.preferredRole.personLabel(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = roleColors.second,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                ) {
                    Text(
                        text = profile.teamPreference.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            CompatibilityBadge(match = student.match)
            student.match.reasons.firstOrNull()?.let { reason ->
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = reason,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (profile.skills.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                TagRow(
                    tags = profile.skills,
                    highlighted = student.match.complementarySkills.toSet()
                )
            }

            if (profile.interests.isNotEmpty() || profile.areasOfInterest.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                TagRow(
                    tags = (profile.interests + profile.areasOfInterest).distinct(),
                    highlighted = student.match.sharedInterests.toSet(),
                    highlightColor = PrimaryGreen
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = DarkBorder)
            Spacer(modifier = Modifier.height(8.dp))

            if (profile.experience.isNotBlank()) {
                Text(
                    text = "Experience · ${profile.experience}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            if (profile.goals.isNotBlank()) {
                Text(
                    text = "Goal · ${profile.goals}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun TagRow(
    tags: List<String>,
    highlighted: Set<String>,
    highlightColor: Color = PrimaryGreen
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        tags.forEach { tag ->
            val isHighlighted = highlighted.any { it.equals(tag, ignoreCase = true) }
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isHighlighted) {
                    PrimaryGreenContainer
                } else {
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                },
                border = if (isHighlighted) BorderStroke(1.dp, highlightColor.copy(alpha = 0.4f)) else null
            ) {
                Text(
                    text = tag,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Normal,
                    color = if (isHighlighted) highlightColor else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }
    }
}

@Composable
private fun ConnectionAction(
    status: ConnectionStatus,
    onConnect: () -> Unit,
    onAccept: () -> Unit
) {
    when (status) {
        ConnectionStatus.NONE -> {
            Button(
                onClick = onConnect,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryGreen,
                    contentColor = Color(0xFF071206)
                ),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text("Connect", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            }
        }
        ConnectionStatus.PENDING_OUTGOING -> {
            OutlinedButton(
                onClick = {},
                enabled = false,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, DarkBorder),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text("Pending", style = MaterialTheme.typography.labelLarge)
            }
        }
        ConnectionStatus.PENDING_INCOMING -> {
            Button(
                onClick = onAccept,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryGreen,
                    contentColor = Color(0xFF071206)
                ),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text("Accept", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            }
        }
        ConnectionStatus.ACCEPTED -> {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = PrimaryGreenContainer,
                border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = PrimaryGreen,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Connected",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGreen
                    )
                }
            }
        }
    }
}

private fun roleColors(role: StartupRoleCategory): Pair<Color, Color> = when (role) {
    StartupRoleCategory.DEVELOPER -> Color(0xFF132238) to Color(0xFF38BDF8)
    StartupRoleCategory.DESIGNER -> Color(0xFF281834) to Color(0xFFC084FC)
    StartupRoleCategory.MARKETER -> Color(0xFF2C2210) to Color(0xFFFBBF24)
    StartupRoleCategory.INTERN -> PrimaryGreenContainer to PrimaryGreen
    StartupRoleCategory.ALL -> Color(0xFF1E242B) to Color(0xFF94A3B8)
}
