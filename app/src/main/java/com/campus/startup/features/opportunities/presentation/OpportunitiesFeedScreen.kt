package com.campus.startup.features.opportunities.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
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
import com.campus.startup.features.opportunities.presentation.components.CategoryChipRow
import com.campus.startup.features.opportunities.presentation.components.OpportunityCard
import com.campus.startup.features.opportunities.presentation.components.SearchBarComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpportunitiesFeedScreen(
    viewModel: OpportunitiesViewModel,
    onOpportunityClick: (String) -> Unit,
    onOpenProfile: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedType by viewModel.selectedType.collectAsState()
    var showPostDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Campus Opportunities",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Internships, Hackathons & Startup Roles",
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
                onClick = { showPostDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp),
                icon = { Icon(Icons.Default.Add, contentDescription = "Post") },
                text = { Text("Post Opportunity", fontWeight = FontWeight.SemiBold) }
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

            CategoryChipRow(
                selectedType = selectedType,
                onTypeSelected = viewModel::onTypeSelected
            )

            when (val state = uiState) {
                is OpportunitiesUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = PrimaryBlue)
                    }
                }
                is OpportunitiesUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = state.message, color = MaterialTheme.colorScheme.error)
                    }
                }
                is OpportunitiesUiState.Success -> {
                    if (state.opportunities.isEmpty()) {
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
                                    text = "No matching opportunities found",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Try adjusting your search query or category filter.",
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
                                        text = "Showing ${state.opportunities.size} Openings",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = StartupGreen,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = "Verified Campus Posts",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Medium,
                                            color = StartupGreen
                                        )
                                    }
                                }
                            }

                            items(
                                items = state.opportunities,
                                key = { it.id }
                            ) { opportunity ->
                                OpportunityCard(
                                    opportunity = opportunity,
                                    onClick = { onOpportunityClick(opportunity.id) },
                                    onBookmarkToggle = { viewModel.toggleBookmark(opportunity) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showPostDialog) {
        PostOpportunityDialog(
            onDismiss = { showPostDialog = false },
            onSubmit = { title, org, type, domain, stipend, location, days, desc ->
                viewModel.postOpportunity(
                    title = title,
                    organization = org,
                    type = type,
                    domain = domain,
                    stipendOrPrize = stipend,
                    location = location,
                    deadlineDays = days,
                    description = desc
                )
                showPostDialog = false
            }
        )
    }
}
