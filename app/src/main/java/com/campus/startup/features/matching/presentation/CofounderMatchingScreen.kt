package com.campus.startup.features.matching.presentation

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.campus.startup.core.theme.AccentPurple
import com.campus.startup.core.theme.PrimaryBlue
import com.campus.startup.core.theme.StartupGreen
import com.campus.startup.features.matching.data.model.personLabel
import com.campus.startup.features.matching.presentation.components.CofounderCard
import com.campus.startup.features.matching.presentation.components.MatchFilterSection
import com.campus.startup.features.opportunities.presentation.components.SearchBarComponent
import com.campus.startup.features.recruitment.presentation.components.RoleCategoryTabs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CofounderMatchingScreen(
    viewModel: MatchingViewModel,
    onStudentClick: (String) -> Unit,
    onEditProfile: () -> Unit,
    onOpenConnections: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedRole by viewModel.selectedRole.collectAsState()
    val skillFilters by viewModel.skillFilters.collectAsState()
    val interestFilters by viewModel.interestFilters.collectAsState()
    val experienceFilters by viewModel.experienceFilters.collectAsState()
    val complementaryOnly by viewModel.complementaryOnly.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    MatchingStatusSnackbar(viewModel, snackbarHostState)

    val profileReady = (uiState as? MatchingUiState.Success)?.me?.profileComplete == true

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Co-founders",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = AccentPurple.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "TEAMS",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentPurple,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Find students whose skills complement yours",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    val pending = (uiState as? MatchingUiState.Success)?.pendingIncoming ?: 0
                    BadgedBox(
                        badge = {
                            if (pending > 0) {
                                Badge(containerColor = PrimaryBlue) { Text(pending.toString()) }
                            }
                        },
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        IconButton(onClick = onOpenConnections) {
                            Icon(
                                imageVector = if (pending > 0) Icons.Filled.Groups else Icons.Outlined.Groups,
                                contentDescription = "Connection requests",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onEditProfile,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp),
                icon = { Icon(Icons.Default.Edit, contentDescription = "Edit profile") },
                text = {
                    Text(
                        text = if (profileReady) "Edit Profile" else "Create Profile",
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            SearchBarComponent(
                query = searchQuery,
                onQueryChange = viewModel::onSearchQueryChanged,
                placeholder = "Search students, skills, interests..."
            )
            RoleCategoryTabs(
                selectedCategory = selectedRole,
                onCategorySelected = viewModel::onRoleSelected
            )
            MatchFilterSection(
                selectedSkills = skillFilters,
                selectedInterests = interestFilters,
                selectedExperience = experienceFilters,
                complementaryOnly = complementaryOnly,
                profileReady = profileReady,
                onSkillToggle = viewModel::toggleSkillFilter,
                onInterestToggle = viewModel::toggleInterestFilter,
                onExperienceToggle = viewModel::toggleExperienceFilter,
                onComplementaryOnlyChanged = viewModel::onComplementaryOnlyChanged,
                onClear = viewModel::clearFilters
            )

            when (val state = uiState) {
                is MatchingUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = PrimaryBlue)
                    }
                }
                is MatchingUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = state.message,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(onClick = viewModel::retry) { Text("Try again") }
                        }
                    }
                }
                is MatchingUiState.Success -> {
                    if (state.students.isEmpty()) {
                        EmptyMatches(
                            directoryEmpty = state.directory.isEmpty(),
                            complementaryOnly = complementaryOnly && profileReady,
                            onClear = viewModel::clearFilters,
                            onCreateProfile = onEditProfile
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(top = 4.dp, bottom = 88.dp)
                        ) {
                            item {
                                ProfileBanner(
                                    state = state,
                                    onEditProfile = onEditProfile
                                )
                            }
                            items(items = state.students, key = { it.profile.id }) { student ->
                                CofounderCard(
                                    student = student,
                                    onClick = { onStudentClick(student.profile.id) },
                                    onConnect = { viewModel.connect(student.profile.id) },
                                    onAccept = { viewModel.acceptFrom(student.profile.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileBanner(
    state: MatchingUiState.Success,
    onEditProfile: () -> Unit
) {
    val me = state.me
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
        if (me == null || !me.profileComplete) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Create your matching profile",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Add skills, interests, and goals so recommendations favour teammates who balance your strengths.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = onEditProfile, shape = RoundedCornerShape(12.dp)) {
                        Text("Create profile")
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${state.students.size} students",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
            if (me?.profileComplete == true && state.complementaryCount > 0) {
                Surface(shape = RoundedCornerShape(8.dp), color = StartupGreen.copy(alpha = 0.15f)) {
                    Text(
                        text = "Sorted by complementary fit",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = StartupGreen,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            } else if (me?.profileComplete == true) {
                Text(
                    text = "Matching as ${me.name} · ${me.preferredRole.personLabel()}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun EmptyMatches(
    directoryEmpty: Boolean,
    complementaryOnly: Boolean,
    onClear: () -> Unit,
    onCreateProfile: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "🤝", style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = when {
                    directoryEmpty -> "No matching students yet"
                    complementaryOnly -> "No complementary teammates"
                    else -> "No students match these filters"
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = when {
                    directoryEmpty -> "Campus profiles will show up here once the directory has students to explore."
                    complementaryOnly -> "Everyone in this view shares skills you already have. Turn off the similar-only filter or widen your profile."
                    else -> "Try another skill, interest, role, or search term."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            if (directoryEmpty) {
                Button(onClick = onCreateProfile) { Text("Create your profile") }
            } else {
                Button(onClick = onClear) { Text("Clear filters") }
            }
        }
    }
}
