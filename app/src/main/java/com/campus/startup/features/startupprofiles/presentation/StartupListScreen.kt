package com.campus.startup.features.startupprofiles.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.campus.startup.features.startupprofiles.data.model.StartupEntity
import com.campus.startup.features.startupprofiles.data.model.StartupOptions
import com.campus.startup.features.startupprofiles.data.model.toCsvList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartupListScreen(
    viewModel: StartupProfilesViewModel,
    onStartupClick: (String) -> Unit,
    onAddClick: () -> Unit
) {
    val startups by viewModel.startups.collectAsStateWithLifecycle()
    val query by viewModel.query.collectAsStateWithLifecycle()
    val domainFilter by viewModel.domainFilter.collectAsStateWithLifecycle()
    val stageFilter by viewModel.stageFilter.collectAsStateWithLifecycle()

    Scaffold(
        contentWindowInsets = NoInsets,
        topBar = {
            TopAppBar(
                title = { Text("Startups", fontWeight = FontWeight.Bold) },
                windowInsets = NoInsets
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddClick,
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("New startup") }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            OutlinedTextField(
                value = query,
                onValueChange = viewModel::onQueryChange,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search name, domain, needs…") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                singleLine = true
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = domainFilter == null,
                        onClick = { viewModel.onDomainFilterChange(null) },
                        label = { Text("All domains") }
                    )
                }
                items(StartupOptions.domains) { domain ->
                    FilterChip(
                        selected = domainFilter == domain,
                        onClick = {
                            viewModel.onDomainFilterChange(if (domainFilter == domain) null else domain)
                        },
                        label = { Text(domain) }
                    )
                }
            }

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = stageFilter == null,
                        onClick = { viewModel.onStageFilterChange(null) },
                        label = { Text("All stages") }
                    )
                }
                items(StartupOptions.stages) { stage ->
                    FilterChip(
                        selected = stageFilter == stage,
                        onClick = {
                            viewModel.onStageFilterChange(if (stageFilter == stage) null else stage)
                        },
                        label = { Text(stage) }
                    )
                }
            }

            if (startups.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No startups found.\nTap “New startup” to add yours.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(startups, key = { it.id }) { startup ->
                        StartupCard(startup = startup, onClick = { onStartupClick(startup.id) })
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StartupCard(startup: StartupEntity, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = startup.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier.padding(top = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StartupTag(startup.domain)
                StartupTag(startup.stage)
            }
            Text(
                text = startup.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 8.dp)
            )
            val needs = startup.collaborationTypes.toCsvList()
            if (needs.isNotEmpty()) {
                Text(
                    text = "Looking for",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    needs.forEach { StartupTag(it) }
                }
            }
        }
    }
}
