package com.campus.startup.features.startupprofiles.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.campus.startup.core.theme.PrimaryGreen
import com.campus.startup.core.theme.PrimaryGreenContainer
import com.campus.startup.features.startupprofiles.data.model.toCsvList

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun StartupDetailScreen(
    startupId: String,
    viewModel: StartupProfilesViewModel,
    onNavigateBack: () -> Unit,
    onEdit: (String) -> Unit
) {
    val startup by remember(startupId) { viewModel.observeStartup(startupId) }
        .collectAsStateWithLifecycle(initialValue = null)
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        contentWindowInsets = NoInsets,
        topBar = {
            TopAppBar(
                windowInsets = NoInsets,
                title = { Text("Startup profile") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    val s = startup
                    if (s != null) {
                        IconButton(onClick = { viewModel.toggleInterested(s) }) {
                            Icon(
                                imageVector = if (s.isInterested) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = "Like Startup",
                                tint = if (s.isInterested) Color(0xFFF43F5E) else MaterialTheme.colorScheme.onSurface
                            )
                        }
                        IconButton(onClick = { viewModel.toggleSaved(s) }) {
                            Icon(
                                imageVector = if (s.isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                contentDescription = "Save Startup",
                                tint = if (s.isSaved) PrimaryGreen else MaterialTheme.colorScheme.onSurface
                            )
                        }
                        IconButton(onClick = { onEdit(startupId) }) {
                            Icon(Icons.Filled.Edit, contentDescription = "Edit")
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete")
                        }
                    }
                }
            )
        }
    ) { padding ->
        val s = startup
        if (s == null) {
            Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Text(
                    text = s.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StartupTag(s.domain)
                    StartupTag(s.stage)
                }

                Spacer(modifier = Modifier.height(10.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = s.isInterested,
                        onClick = { viewModel.toggleInterested(s) },
                        label = { Text(if (s.isInterested) "Liked (${s.likesCount}) ♥" else "Like (${s.likesCount})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF36151E),
                            selectedLabelColor = Color(0xFFF43F5E)
                        )
                    )
                    FilterChip(
                        selected = s.isSaved,
                        onClick = { viewModel.toggleSaved(s) },
                        label = { Text(if (s.isSaved) "Saved to Profile ✓" else "Save Startup") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryGreenContainer,
                            selectedLabelColor = PrimaryGreen
                        )
                    )
                    FilterChip(
                        selected = s.isRecruited,
                        onClick = { viewModel.toggleRecruited(s) },
                        label = { Text(if (s.isRecruited) "Currently Recruited ✓" else "Mark as Recruited") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryGreenContainer,
                            selectedLabelColor = PrimaryGreen
                        )
                    )
                }

                DetailSection("About") { Text(s.description) }

                if (s.founderName.isNotBlank()) {
                    DetailSection("Founder") { Text(s.founderName) }
                }

                val team = s.teamMembers.toCsvList()
                if (team.isNotEmpty()) {
                    DetailSection("Team members") {
                        team.forEach { Text("• $it", modifier = Modifier.padding(vertical = 2.dp)) }
                    }
                }

                val needs = s.collaborationTypes.toCsvList()
                if (needs.isNotEmpty()) {
                    DetailSection("Looking for") {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) { needs.forEach { StartupTag(it) } }
                    }
                }

                if (s.requirements.isNotBlank()) {
                    DetailSection("Requirements") { Text(s.requirements) }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete startup?") },
            text = { Text("This will permanently remove this startup profile.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    viewModel.delete(startupId)
                    onNavigateBack()
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }
}
