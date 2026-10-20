package com.campus.startup.features.matching.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Handshake
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.icons.outlined.Style
import androidx.compose.material.icons.outlined.ViewAgenda
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.campus.startup.core.theme.*
import com.campus.startup.features.matching.data.model.personLabel
import com.campus.startup.features.matching.presentation.components.CofounderCard
import com.campus.startup.features.matching.presentation.components.MatchFilterSection
import com.campus.startup.features.matching.presentation.components.SwipeableCofounderCard
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
    var isSwipeMode by rememberSaveable { mutableStateOf(true) }
    var activeCardIndex by rememberSaveable { mutableIntStateOf(0) }

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
                                fontWeight = FontWeight.Bold,
                                color = DarkTextPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = PrimaryGreenContainer,
                                border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.4f))
                            ) {
                                Text(
                                    text = "MATCHING",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryGreen,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = if (isSwipeMode) "Swipe right to connect, left to pass" else "Browse students whose skills complement yours",
                            style = MaterialTheme.typography.bodySmall,
                            color = DarkTextSecondary
                        )
                    }
                },
                actions = {
                    // View Mode Toggle (Swipe Deck vs Directory List)
                    IconButton(onClick = { isSwipeMode = !isSwipeMode }) {
                        Icon(
                            imageVector = if (isSwipeMode) Icons.Outlined.ViewAgenda else Icons.Outlined.Style,
                            contentDescription = if (isSwipeMode) "Switch to list view" else "Switch to card deck",
                            tint = PrimaryGreen
                        )
                    }

                    // Connection requests badge
                    val pending = (uiState as? MatchingUiState.Success)?.pendingIncoming ?: 0
                    BadgedBox(
                        badge = {
                            if (pending > 0) {
                                Badge(containerColor = PrimaryGreen) {
                                    Text(pending.toString(), color = Color(0xFF071206), fontWeight = FontWeight.Bold)
                                }
                            }
                        },
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        IconButton(onClick = onOpenConnections) {
                            Icon(
                                imageVector = if (pending > 0) Icons.Filled.Groups else Icons.Outlined.Groups,
                                contentDescription = "Connection requests",
                                tint = DarkTextPrimary
                            )
                        }
                    }

                    // Profile icon button
                    IconButton(onClick = onEditProfile) {
                        Icon(
                            imageVector = Icons.Outlined.AccountCircle,
                            contentDescription = "My Profile",
                            tint = DarkTextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val state = uiState) {
                is MatchingUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = PrimaryGreen)
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
                            Button(
                                onClick = viewModel::retry,
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                            ) {
                                Text("Try again", color = Color(0xFF071206))
                            }
                        }
                    }
                }
                is MatchingUiState.Success -> {
                    if (state.students.isEmpty()) {
                        EmptyMatches(
                            directoryEmpty = state.directory.isEmpty(),
                            complementaryOnly = complementaryOnly && profileReady,
                            onClear = viewModel::clearFilters
                        )
                    } else if (isSwipeMode) {
                        // Section 3: Dating-App Swipe Interface
                        val studentList = state.students
                        val currentStudent = studentList.getOrNull(activeCardIndex)

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Mini deck progress indicator
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (currentStudent != null) "Candidate ${activeCardIndex + 1} of ${studentList.size}" else "Deck completed",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = DarkTextSecondary
                                )
                                TextButton(
                                    onClick = { activeCardIndex = 0 },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.RestartAlt,
                                        contentDescription = "Restart deck",
                                        tint = PrimaryGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Reset Deck", style = MaterialTheme.typography.labelSmall, color = PrimaryGreen)
                                }
                            }

                            if (currentStudent != null) {
                                SwipeableCofounderCard(
                                    student = currentStudent,
                                    onSwipeLeft = {
                                        activeCardIndex++
                                    },
                                    onSwipeRight = { student ->
                                        viewModel.connect(student.profile.id)
                                        activeCardIndex++
                                    },
                                    onViewDetails = { student ->
                                        onStudentClick(student.profile.id)
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            } else {
                                // Deck Finished / Empty State (Requirement 3C)
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(24.dp),
                                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                                        border = BorderStroke(1.dp, DarkBorder)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(24.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Surface(
                                                shape = RoundedCornerShape(20.dp),
                                                color = PrimaryGreenContainer,
                                                border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.4f)),
                                                modifier = Modifier.size(72.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Icon(
                                                        imageVector = Icons.Outlined.CheckCircle,
                                                        contentDescription = null,
                                                        tint = PrimaryGreen,
                                                        modifier = Modifier.size(36.dp)
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(16.dp))

                                            Text(
                                                text = "You're All Caught Up!",
                                                style = MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = DarkTextPrimary,
                                                textAlign = TextAlign.Center
                                            )

                                            Spacer(modifier = Modifier.height(8.dp))

                                            Text(
                                                text = "You've reviewed all suggested co-founder candidates. Reset your deck to review again, or explore the directory with custom filters.",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = DarkTextSecondary,
                                                textAlign = TextAlign.Center
                                            )

                                            Spacer(modifier = Modifier.height(20.dp))

                                            Button(
                                                onClick = { activeCardIndex = 0 },
                                                shape = RoundedCornerShape(14.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = PrimaryGreen,
                                                    contentColor = Color(0xFF071206)
                                                ),
                                                modifier = Modifier.fillMaxWidth().height(48.dp)
                                            ) {
                                                Icon(Icons.Outlined.RestartAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("Reset Swipes Deck", fontWeight = FontWeight.Bold)
                                            }

                                            Spacer(modifier = Modifier.height(10.dp))

                                            OutlinedButton(
                                                onClick = { isSwipeMode = false },
                                                shape = RoundedCornerShape(14.dp),
                                                border = BorderStroke(1.dp, DarkBorder),
                                                modifier = Modifier.fillMaxWidth().height(48.dp)
                                            ) {
                                                Text("Explore Full Directory", color = DarkTextPrimary)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        // Section 3: Directory / List View Mode
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

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(top = 4.dp, bottom = 88.dp)
                        ) {
                            item {
                                ProfileBanner(state = state)
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
    state: MatchingUiState.Success
) {
    val me = state.me
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${state.students.size} students available",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = DarkTextPrimary
            )
            if (me?.profileComplete == true && state.complementaryCount > 0) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PrimaryGreenContainer,
                    border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.35f))
                ) {
                    Text(
                        text = "Sorted by complementary fit",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGreen,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            } else if (me?.profileComplete == true) {
                Text(
                    text = "Matching as ${me.name} · ${me.preferredRole.personLabel()}",
                    style = MaterialTheme.typography.labelSmall,
                    color = DarkTextSecondary
                )
            }
        }
    }
}

@Composable
private fun EmptyMatches(
    directoryEmpty: Boolean,
    complementaryOnly: Boolean,
    onClear: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = DarkSurfaceVariant,
                border = BorderStroke(1.dp, DarkBorder),
                modifier = Modifier.size(64.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.Handshake,
                        contentDescription = null,
                        tint = PrimaryGreen,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = when {
                    directoryEmpty -> "No matching students yet"
                    complementaryOnly -> "No complementary teammates found"
                    else -> "No students match these filters"
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = DarkTextPrimary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = when {
                    directoryEmpty -> "Campus profiles will appear here once students join the co-founder directory."
                    complementaryOnly -> "All students in this category share skills you already have. Adjust filters to discover new crafts."
                    else -> "Try adjusting your skills, interests, or role filters."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = DarkTextSecondary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onClear,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen, contentColor = Color(0xFF071206))
            ) {
                Text("Clear filters", fontWeight = FontWeight.Bold)
            }
        }
    }
}
