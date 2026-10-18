package com.campus.startup.features.matching.presentation

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.unit.dp
import com.campus.startup.core.theme.PrimaryBlue
import com.campus.startup.core.theme.StartupGreen
import com.campus.startup.features.matching.data.model.ConnectionStatus
import com.campus.startup.features.matching.data.model.DiscoverableProfile
import com.campus.startup.features.matching.data.model.personLabel
import com.campus.startup.features.matching.presentation.components.CompatibilityBadge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CofounderDetailScreen(
    profileId: String,
    viewModel: MatchingViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    MatchingStatusSnackbar(viewModel, snackbarHostState)

    val student = (uiState as? MatchingUiState.Success)?.directory?.find { it.profile.id == profileId }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Student profile", style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            if (student != null) {
                Surface(color = MaterialTheme.colorScheme.surface, tonalElevation = 8.dp, shadowElevation = 8.dp) {
                    Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        DetailConnectionButton(
                            student = student,
                            onConnect = { viewModel.connect(student.profile.id) },
                            onAccept = { viewModel.acceptFrom(student.profile.id) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        when (val state = uiState) {
            is MatchingUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            }
            is MatchingUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize().padding(padding).padding(32.dp), contentAlignment = Alignment.Center) {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyLarge)
                }
            }
            is MatchingUiState.Success -> {
                val person = state.directory.find { it.profile.id == profileId }
                if (person == null) {
                    Box(modifier = Modifier.fillMaxSize().padding(padding).padding(32.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "This profile isn't available.", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "It may have been removed from the campus directory.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    ProfileBody(student = person, modifier = Modifier.padding(padding))
                }
            }
        }
    }
}

@Composable
private fun ProfileBody(student: DiscoverableProfile, modifier: Modifier = Modifier) {
    val profile = student.profile
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .padding(bottom = 24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(64.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = profile.avatar, style = MaterialTheme.typography.headlineMedium)
                }
            }
            Spacer(modifier = Modifier.size(12.dp))
            Column {
                Text(text = profile.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                if (profile.academicLine.isNotBlank()) {
                    Text(
                        text = profile.academicLine,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            InfoPill("${profile.preferredRole.emoji} ${profile.preferredRole.personLabel()}")
            InfoPill("${profile.teamPreference.emoji} ${profile.teamPreference.displayName}")
        }

        Spacer(modifier = Modifier.height(14.dp))
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(text = "Why this match", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                CompatibilityBadge(match = student.match)
                Spacer(modifier = Modifier.height(8.dp))
                student.match.reasons.forEach { reason ->
                    Text(
                        text = "• $reason",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }

        SectionTitle("Skills")
        if (profile.skills.isEmpty()) {
            EmptyLine("No skills listed.")
        } else {
            WrappingTags(profile.skills, student.match.complementarySkills)
        }

        SectionTitle("Interests")
        if (profile.interests.isEmpty()) {
            EmptyLine("No interests listed.")
        } else {
            WrappingTags(profile.interests, student.match.sharedInterests)
        }

        SectionTitle("Startup areas")
        if (profile.areasOfInterest.isEmpty()) {
            EmptyLine("No startup areas listed.")
        } else {
            WrappingTags(profile.areasOfInterest, student.match.sharedInterests)
        }

        SectionTitle("Experience")
        if (profile.experienceTags.isNotEmpty()) {
            WrappingTags(profile.experienceTags, student.match.sharedExperience)
            Spacer(modifier = Modifier.height(8.dp))
        }
        Text(
            text = profile.experience.ifBlank { "No experience written yet." },
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        SectionTitle("Entrepreneurial goals")
        Text(
            text = profile.goals.ifBlank { "No goals written yet." },
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun DetailConnectionButton(
    student: DiscoverableProfile,
    onConnect: () -> Unit,
    onAccept: () -> Unit
) {
    when (student.connectionStatus) {
        ConnectionStatus.NONE -> {
            Button(
                onClick = onConnect,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Send Request", fontWeight = FontWeight.Bold)
            }
        }
        ConnectionStatus.PENDING_OUTGOING -> {
            OutlinedButton(
                onClick = {},
                enabled = false,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Request Pending", fontWeight = FontWeight.Bold)
            }
        }
        ConnectionStatus.PENDING_INCOMING -> {
            Button(
                onClick = onAccept,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Accept Request", fontWeight = FontWeight.Bold)
            }
        }
        ConnectionStatus.ACCEPTED -> {
            Button(
                onClick = {},
                enabled = false,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = StartupGreen)
                Spacer(modifier = Modifier.size(6.dp))
                Text("Connected", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Spacer(modifier = Modifier.height(16.dp))
    Text(text = text, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
private fun EmptyLine(text: String) {
    Text(text = text, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
}

@Composable
private fun InfoPill(text: String) {
    Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun WrappingTags(tags: List<String>, highlighted: List<String>) {
    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        tags.forEach { tag ->
            val isHighlighted = highlighted.any { it.equals(tag, ignoreCase = true) }
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isHighlighted) StartupGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
            ) {
                Text(
                    text = if (isHighlighted) "✓ $tag" else tag,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Medium,
                    color = if (isHighlighted) StartupGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}
