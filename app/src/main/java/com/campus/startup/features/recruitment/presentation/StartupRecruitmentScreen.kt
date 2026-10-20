package com.campus.startup.features.recruitment.presentation

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.campus.startup.core.theme.PrimaryBlue
import com.campus.startup.core.theme.PrimaryGreen
import com.campus.startup.core.theme.StartupGreen
import com.campus.startup.features.opportunities.presentation.components.SearchBarComponent
import com.campus.startup.features.recruitment.presentation.components.RoleCategoryTabs
import com.campus.startup.features.recruitment.presentation.components.SkillFilterSection
import com.campus.startup.features.recruitment.presentation.components.StartupOpeningCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartupRecruitmentScreen(
    viewModel: RecruitmentViewModel,
    onOpeningClick: (String) -> Unit,
    onOpenProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val studentSkills by viewModel.studentSkills.collectAsState()
    val onlyMatching by viewModel.onlyMatchingSkills.collectAsState()
    val workTypeFilter by viewModel.workTypeFilter.collectAsState()

    var showPostOpeningDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Startup Recruitment",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = StartupGreen.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "HIRING",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = StartupGreen,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Founders hiring developers, designers, marketers & interns",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onOpenProfile) {
                        Icon(
                            imageVector = Icons.Outlined.AccountCircle,
                            contentDescription = "My Profile",
                            tint = PrimaryGreen,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showPostOpeningDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp),
                icon = { Icon(Icons.Default.Add, contentDescription = "Post Opening") },
                text = { Text("Post Opening", fontWeight = FontWeight.Bold) }
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
                onQueryChange = viewModel::onSearchQueryChanged
            )

            RoleCategoryTabs(
                selectedCategory = selectedCategory,
                onCategorySelected = viewModel::onCategorySelected
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("All", "Remote", "Hybrid", "On-Campus").forEach { type ->
                    val isSelected = workTypeFilter == type
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.onWorkTypeFilterChanged(type) },
                        label = { Text(type, style = MaterialTheme.typography.labelSmall) },
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }

            SkillFilterSection(
                studentSkills = studentSkills,
                onlyMatching = onlyMatching,
                onSkillToggle = viewModel::toggleStudentSkill,
                onOnlyMatchingToggle = viewModel::toggleOnlyMatching
            )

            when (val state = uiState) {
                is RecruitmentUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = PrimaryBlue)
                    }
                }
                is RecruitmentUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = state.message, color = MaterialTheme.colorScheme.error)
                    }
                }
                is RecruitmentUiState.Success -> {
                    if (state.openings.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(52.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "No startup openings found",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Try adjusting your role category, search keyword, or toggle off 'Only show matching roles'.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(top = 4.dp, bottom = 80.dp)
                        ) {
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${state.openings.size} Openings Available",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    if (state.highMatchCount > 0) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = StartupGreen.copy(alpha = 0.15f)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.AutoAwesome,
                                                    contentDescription = null,
                                                    tint = StartupGreen,
                                                    modifier = Modifier.size(13.dp)
                                                )
                                                Text(
                                                    text = "${state.highMatchCount} High Matches for you",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = StartupGreen
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            items(
                                items = state.openings,
                                key = { it.id }
                            ) { opening ->
                                StartupOpeningCard(
                                    opening = opening,
                                    onClick = { onOpeningClick(opening.id) },
                                    onSaveToggle = { viewModel.toggleSaveOpening(opening) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showPostOpeningDialog) {
        PostOpeningDialog(
            onDismiss = { showPostOpeningDialog = false },
            onSubmit = { startupName, startupLogo, startupStage, title, roleCat, skills, workType, commitment, stipend, loc, days, desc, resps, perks, fName, fEmail ->
                viewModel.postOpening(
                    startupName = startupName,
                    startupLogo = startupLogo,
                    startupStage = startupStage,
                    title = title,
                    roleCategory = roleCat,
                    skills = skills,
                    workType = workType,
                    commitment = commitment,
                    stipend = stipend,
                    location = loc,
                    deadlineDays = days,
                    description = desc,
                    responsibilities = resps,
                    perks = perks,
                    founderName = fName,
                    founderEmail = fEmail
                )
                showPostOpeningDialog = false
            }
        )
    }
}
