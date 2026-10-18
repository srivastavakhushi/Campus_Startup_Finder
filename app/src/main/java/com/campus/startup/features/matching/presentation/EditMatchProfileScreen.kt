package com.campus.startup.features.matching.presentation

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.campus.startup.core.theme.PrimaryBlue
import com.campus.startup.features.matching.data.model.MatchCatalog
import com.campus.startup.features.matching.data.model.StudentMatchProfile
import com.campus.startup.features.matching.data.model.TeamPreference
import com.campus.startup.features.matching.data.model.personLabel
import com.campus.startup.features.recruitment.data.model.SkillItem
import com.campus.startup.features.recruitment.data.model.StartupRoleCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMatchProfileScreen(
    viewModel: MatchingViewModel,
    onNavigateBack: () -> Unit,
    onSaved: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val errors by viewModel.formErrors.collectAsState()
    val me = (uiState as? MatchingUiState.Success)?.me

    var ready by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var avatar by remember { mutableStateOf("🎓") }
    var branch by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var college by remember { mutableStateOf("") }
    var skills by remember { mutableStateOf(setOf<String>()) }
    var interests by remember { mutableStateOf(setOf<String>()) }
    var areas by remember { mutableStateOf(setOf<String>()) }
    var experience by remember { mutableStateOf("") }
    var experienceTags by remember { mutableStateOf(setOf<String>()) }
    var goals by remember { mutableStateOf("") }
    var role by remember { mutableStateOf(StartupRoleCategory.DEVELOPER) }
    var team by remember { mutableStateOf(TeamPreference.FOUNDING) }

    LaunchedEffect(me?.id, uiState is MatchingUiState.Success) {
        if (!ready && me != null) {
            name = me.name
            avatar = me.avatar.ifBlank { "🎓" }
            branch = me.branch
            year = me.year
            college = me.college
            skills = me.skills.toSet()
            interests = me.interests.toSet()
            areas = me.areasOfInterest.toSet()
            experience = me.experience
            experienceTags = me.experienceTags.toSet()
            goals = me.goals
            role = if (me.preferredRole == StartupRoleCategory.ALL) StartupRoleCategory.DEVELOPER else me.preferredRole
            team = me.teamPreference
            ready = true
            viewModel.clearFormErrors()
        } else if (!ready && uiState is MatchingUiState.Success) {
            ready = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (me?.profileComplete == true) "Edit profile" else "Create profile",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Used only for co-founder matching",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            Surface(color = MaterialTheme.colorScheme.surface, tonalElevation = 8.dp, shadowElevation = 8.dp) {
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Button(
                        onClick = {
                            val draft = StudentMatchProfile(
                                id = MatchCatalog.CURRENT_USER_ID,
                                name = name,
                                avatar = avatar,
                                branch = branch,
                                year = year,
                                college = college,
                                skills = ordered(skills, SkillItem.ALL_SKILLS.map { it.name }),
                                interests = ordered(interests, MatchCatalog.INTERESTS),
                                areasOfInterest = ordered(areas, MatchCatalog.AREAS),
                                experience = experience,
                                experienceTags = ordered(experienceTags, MatchCatalog.EXPERIENCE_TAGS),
                                goals = goals,
                                preferredRole = role,
                                teamPreference = team,
                                isCurrentUser = true
                            )
                            if (viewModel.saveProfile(draft)) onSaved()
                        },
                        enabled = ready,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = if (me?.profileComplete == true) "Update profile" else "Save profile",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    ) { padding ->
        if (!ready && uiState is MatchingUiState.Loading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Recommendations look for skills you don't already have, plus shared startup interests.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text("Profile image", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MatchCatalog.AVATARS.forEach { emoji ->
                        Button(
                            onClick = { avatar = emoji },
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (avatar == emoji) PrimaryBlue else MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (avatar == emoji) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        ) {
                            Text(emoji, style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name *") },
                    placeholder = { Text("e.g. Khushi Sharma") },
                    isError = errors.name != null,
                    supportingText = errors.name?.let { { Text(it) } },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp)
                )
                OutlinedTextField(
                    value = branch,
                    onValueChange = { branch = it },
                    label = { Text("Branch *") },
                    placeholder = { Text("e.g. CSE, Design, ECE") },
                    isError = errors.academic != null && branch.trim().length < 2,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp)
                )
                Text("Year *", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                ChoiceRow(
                    labels = MatchCatalog.YEARS,
                    selected = year,
                    onSelected = { year = it }
                )
                errors.academic?.let { message ->
                    Text(text = message, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
                }
                OutlinedTextField(
                    value = college,
                    onValueChange = { college = it },
                    label = { Text("College") },
                    placeholder = { Text("Optional") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp)
                )

                SectionLabel("Preferred role *")
                ChoiceRow(
                    labels = MatchCatalog.ROLES.map { "${it.emoji} ${it.personLabel()}" },
                    selected = "${role.emoji} ${role.personLabel()}",
                    onSelected = { label ->
                        role = MatchCatalog.ROLES.first { "${it.emoji} ${it.personLabel()}" == label }
                    }
                )
                errors.role?.let { message ->
                    Text(text = message, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
                }

                SectionLabel("Team you want to join *")
                ChoiceRow(
                    labels = TeamPreference.values().map { "${it.emoji} ${it.displayName}" },
                    selected = "${team.emoji} ${team.displayName}",
                    onSelected = { label ->
                        team = TeamPreference.values().first { "${it.emoji} ${it.displayName}" == label }
                    }
                )

                SectionLabel("Skills *")
                SkillItem.ALL_SKILLS.groupBy { it.category }.forEach { (category, items) ->
                    Text(
                        text = category.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    ToggleRow(
                        labels = items.map { it.name },
                        selected = skills,
                        onToggle = { skill -> skills = skills.toggle(skill) }
                    )
                }
                errors.skills?.let { message ->
                    Text(text = message, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
                }

                SectionLabel("Interests *")
                ToggleRow(
                    labels = MatchCatalog.INTERESTS,
                    selected = interests,
                    onToggle = { interests = interests.toggle(it) }
                )
                errors.interests?.let { message ->
                    Text(text = message, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
                }

                SectionLabel("Startup areas *")
                ToggleRow(
                    labels = MatchCatalog.AREAS,
                    selected = areas,
                    onToggle = { areas = areas.toggle(it) }
                )
                errors.areas?.let { message ->
                    Text(text = message, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
                }

                OutlinedTextField(
                    value = experience,
                    onValueChange = { experience = it },
                    label = { Text("Previous experience") },
                    placeholder = { Text("Projects, internships, hackathons, or ventures") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = RoundedCornerShape(14.dp)
                )
                Text("Experience highlights", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                ToggleRow(
                    labels = MatchCatalog.EXPERIENCE_TAGS,
                    selected = experienceTags,
                    onToggle = { experienceTags = experienceTags.toggle(it) }
                )

                OutlinedTextField(
                    value = goals,
                    onValueChange = { goals = it },
                    label = { Text("Entrepreneurial goals *") },
                    placeholder = { Text("What do you want to build, and who do you want beside you?") },
                    isError = errors.goals != null,
                    supportingText = errors.goals?.let { { Text(it) } },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = RoundedCornerShape(14.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text = text, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
}

@Composable
private fun ChoiceRow(
    labels: List<String>,
    selected: String,
    onSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        labels.forEach { label ->
            FilterChip(
                selected = label == selected,
                onClick = { onSelected(label) },
                label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                shape = RoundedCornerShape(12.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = PrimaryBlue,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}

@Composable
private fun ToggleRow(
    labels: List<String>,
    selected: Set<String>,
    onToggle: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        labels.forEach { label ->
            val isSelected = selected.any { it.equals(label, ignoreCase = true) }
            FilterChip(
                selected = isSelected,
                onClick = { onToggle(label) },
                label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                shape = RoundedCornerShape(12.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = PrimaryBlue.copy(alpha = 0.9f),
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}

private fun ordered(selected: Set<String>, catalog: List<String>): List<String> {
    return catalog.filter { item -> selected.any { it.equals(item, ignoreCase = true) } }
}

private fun Set<String>.toggle(value: String): Set<String> {
    return if (any { it.equals(value, ignoreCase = true) }) {
        filterNot { it.equals(value, ignoreCase = true) }.toSet()
    } else {
        this + value
    }
}
