package com.campus.startup.features.matching.presentation.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.campus.startup.core.theme.PrimaryGreen
import com.campus.startup.features.matching.data.model.MatchCatalog
import com.campus.startup.features.recruitment.data.model.SkillItem

@Composable
fun MatchFilterSection(
    selectedSkills: Set<String>,
    selectedInterests: Set<String>,
    selectedExperience: Set<String>,
    complementaryOnly: Boolean,
    profileReady: Boolean,
    onSkillToggle: (String) -> Unit,
    onInterestToggle: (String) -> Unit,
    onExperienceToggle: (String) -> Unit,
    onComplementaryOnlyChanged: (Boolean) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val active = selectedSkills.size + selectedInterests.size + selectedExperience.size + if (complementaryOnly) 1 else 0

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        tonalElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        tint = PrimaryGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Complementary filters",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (active == 0) "Skills, interests, and experience" else "$active filters active",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                TextButton(onClick = { expanded = !expanded }, contentPadding = PaddingValues(horizontal = 8.dp)) {
                    Text(
                        text = if (expanded) "Hide" else "Filters",
                        style = MaterialTheme.typography.labelMedium,
                        color = com.campus.startup.core.theme.PrimaryGreen,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    FilterLabel("Skills")
                    ChipRow(
                        labels = SkillItem.ALL_SKILLS.map { it.name },
                        selected = selectedSkills,
                        onToggle = onSkillToggle
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FilterLabel("Interests and startup areas")
                    ChipRow(
                        labels = (MatchCatalog.INTERESTS + MatchCatalog.AREAS).distinct(),
                        selected = selectedInterests,
                        onToggle = onInterestToggle
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FilterLabel("Experience")
                    ChipRow(
                        labels = MatchCatalog.EXPERIENCE_TAGS,
                        selected = selectedExperience,
                        onToggle = onExperienceToggle
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Hide similar-only profiles",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (profileReady) {
                                    "Keeps students who add skills you don't have"
                                } else {
                                    "Available after you save your profile"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = complementaryOnly && profileReady,
                            onCheckedChange = onComplementaryOnlyChanged,
                            enabled = profileReady
                        )
                    }
                    if (active > 0) {
                        TextButton(onClick = onClear) {
                            Text("Clear filters")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(modifier = Modifier.height(6.dp))
}

@Composable
private fun ChipRow(
    labels: List<String>,
    selected: Set<String>,
    onToggle: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        labels.forEach { label ->
            val isSelected = selected.any { it.equals(label, ignoreCase = true) }
            FilterChip(
                selected = isSelected,
                onClick = { onToggle(label) },
                label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                leadingIcon = if (isSelected) {
                    {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                } else {
                    null
                },
                shape = RoundedCornerShape(12.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = com.campus.startup.core.theme.PrimaryGreen,
                    selectedLabelColor = androidx.compose.ui.graphics.Color(0xFF071206),
                    selectedLeadingIconColor = androidx.compose.ui.graphics.Color(0xFF071206)
                )
            )
        }
    }
}
