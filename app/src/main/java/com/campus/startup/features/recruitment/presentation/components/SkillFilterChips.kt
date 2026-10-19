package com.campus.startup.features.recruitment.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.campus.startup.core.theme.DarkBorder
import com.campus.startup.core.theme.DarkSurface
import com.campus.startup.core.theme.PrimaryGreen
import com.campus.startup.features.recruitment.data.model.SkillItem

@Composable
fun SkillFilterSection(
    studentSkills: Set<String>,
    onlyMatching: Boolean,
    onSkillToggle: (String) -> Unit,
    onOnlyMatchingToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    SkillFilterChips(studentSkills, onlyMatching, onSkillToggle, onOnlyMatchingToggle, modifier)
}

@Composable
fun SkillFilterChips(
    studentSkills: Set<String>,
    onlyMatching: Boolean,
    onSkillToggle: (String) -> Unit,
    onOnlyMatchingToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .border(1.dp, DarkBorder, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        color = DarkSurface,
        tonalElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Tune,
                        contentDescription = null,
                        tint = PrimaryGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Smart Skill Matcher",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${studentSkills.size} skills active • Auto-matching openings",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(
                        onClick = { isExpanded = !isExpanded },
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Text(
                            text = if (isExpanded) "Hide" else "Customize",
                            style = MaterialTheme.typography.labelMedium,
                            color = PrimaryGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    Text(
                        text = "Select your skills & interests:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        SkillItem.ALL_SKILLS.forEach { skill ->
                            val isSelected = studentSkills.contains(skill.name)
                            FilterChip(
                                selected = isSelected,
                                onClick = { onSkillToggle(skill.name) },
                                label = {
                                    Text(
                                        text = skill.name,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                leadingIcon = if (isSelected) {
                                    {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                } else null,
                                shape = RoundedCornerShape(12.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PrimaryGreen,
                                    selectedLabelColor = Color(0xFF071206),
                                    selectedLeadingIconColor = Color(0xFF071206)
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Filter: Only show roles matching my skills",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Switch(
                            checked = onlyMatching,
                            onCheckedChange = onOnlyMatchingToggle,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF071206),
                                checkedTrackColor = PrimaryGreen
                            )
                        )
                    }
                }
            }
        }
    }
}
