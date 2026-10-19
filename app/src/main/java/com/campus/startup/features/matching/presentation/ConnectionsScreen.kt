package com.campus.startup.features.matching.presentation

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.campus.startup.core.components.ModernAvatar
import com.campus.startup.core.theme.PrimaryBlue
import com.campus.startup.core.theme.StartupGreen
import com.campus.startup.features.matching.data.model.ConnectionRequest
import com.campus.startup.features.matching.data.model.ConnectionStatus
import com.campus.startup.features.matching.data.model.MatchCatalog
import com.campus.startup.features.matching.data.model.StudentMatchProfile
import com.campus.startup.features.matching.data.model.personLabel
import com.campus.startup.features.matching.data.model.resolveConnectionStatus

private enum class ConnectionTab(val label: String) {
    ALL("All"),
    INCOMING("Incoming"),
    SENT("Sent"),
    CONNECTED("Connected")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectionsScreen(
    viewModel: MatchingViewModel,
    onNavigateBack: () -> Unit,
    onStudentClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    MatchingStatusSnackbar(viewModel, snackbarHostState)
    var tab by remember { mutableStateOf(ConnectionTab.ALL) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Connections", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(
                            text = "Requests you've sent, received, and accepted",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        when (val state = uiState) {
            is MatchingUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            }
            is MatchingUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error)
                }
            }
            is MatchingUiState.Success -> {
                val allRows = state.connections.mapNotNull { request -> rowFor(request, state) }
                val rows = allRows.filter { row ->
                    when (tab) {
                        ConnectionTab.ALL -> true
                        ConnectionTab.INCOMING -> row.status == ConnectionStatus.PENDING_INCOMING
                        ConnectionTab.SENT -> row.status == ConnectionStatus.PENDING_OUTGOING
                        ConnectionTab.CONNECTED -> row.status == ConnectionStatus.ACCEPTED
                    }
                }
                Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ConnectionTab.values().forEach { option ->
                            FilterChip(
                                selected = tab == option,
                                onClick = { tab = option },
                                label = {
                                    Text(
                                        option.label,
                                        fontWeight = if (tab == option) FontWeight.Bold else FontWeight.Medium
                                    )
                                },
                                shape = RoundedCornerShape(20.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PrimaryBlue,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                border = null
                            )
                        }
                    }
                    if (rows.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.Group,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(56.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = if (allRows.isEmpty()) "No connection requests yet" else "Nothing in ${tab.label}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (allRows.isEmpty()) {
                                        "Open a student profile and send a request. Incoming requests appear here too."
                                    } else {
                                        "Try another tab to see sent, incoming, or accepted requests."
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        LazyColumn(contentPadding = PaddingValues(bottom = 24.dp)) {
                            items(rows, key = { it.request.id }) { row ->
                                ConnectionCard(
                                    row = row,
                                    onClick = { row.other?.id?.let(onStudentClick) },
                                    onAccept = { viewModel.acceptRequest(row.request.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private data class ConnectionRow(
    val request: ConnectionRequest,
    val other: StudentMatchProfile?,
    val status: ConnectionStatus
)

private fun rowFor(request: ConnectionRequest, state: MatchingUiState.Success): ConnectionRow? {
    val otherId = when (MatchCatalog.CURRENT_USER_ID) {
        request.fromProfileId -> request.toProfileId
        request.toProfileId -> request.fromProfileId
        else -> return null
    }
    val other = state.directory.find { it.profile.id == otherId }?.profile
    return ConnectionRow(
        request = request,
        other = other,
        status = resolveConnectionStatus(MatchCatalog.CURRENT_USER_ID, otherId, listOf(request))
    )
}

@Composable
private fun ConnectionCard(
    row: ConnectionRow,
    onClick: () -> Unit,
    onAccept: () -> Unit
) {
    val profile = row.other
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 7.dp)
            .clickable(enabled = profile != null, onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ModernAvatar(
                name = profile?.name ?: "Student",
                role = profile?.preferredRole,
                size = 46.dp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = profile?.name ?: "Campus student",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = when (row.status) {
                        ConnectionStatus.PENDING_INCOMING -> "Wants to connect"
                        ConnectionStatus.PENDING_OUTGOING -> "Request pending"
                        ConnectionStatus.ACCEPTED -> "Connected"
                        ConnectionStatus.NONE -> ""
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = if (row.status == ConnectionStatus.ACCEPTED) StartupGreen else MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (profile != null) {
                    Text(
                        text = "${profile.preferredRole.personLabel()} · ${profile.teamPreference.displayName}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            if (row.status == ConnectionStatus.PENDING_INCOMING) {
                Button(
                    onClick = onAccept,
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("Accept")
                }
            }
        }
    }
}
