package com.campus.startup.features.matching.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.campus.startup.core.components.ModernAvatar
import com.campus.startup.core.theme.*
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
                title = { Text("Candidate Profile", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = DarkTextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DarkTextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
            )
        },
        bottomBar = {
            if (student != null) {
                Surface(
                    color = DarkSurface,
                    tonalElevation = 8.dp,
                    shadowElevation = 8.dp,
                    border = BorderStroke(1.dp, DarkBorder)
                ) {
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
                    CircularProgressIndicator(color = PrimaryGreen)
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
                            Text(text = "This profile isn't available.", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = DarkTextPrimary)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "It may have been removed from the campus directory.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = DarkTextSecondary
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
            ModernAvatar(
                name = profile.name,
                role = profile.preferredRole,
                size = 64.dp,
                showActiveIndicator = true
            )
            Spacer(modifier = Modifier.size(14.dp))
            Column {
                Text(text = profile.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = DarkTextPrimary)
                if (profile.academicLine.isNotBlank()) {
                    Text(
                        text = profile.academicLine,
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkTextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            InfoPill(profile.preferredRole.personLabel(), isAccent = true)
            InfoPill(profile.teamPreference.displayName)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF102415),
            border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(text = "Compatibility Analysis", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = PrimaryGreen)
                Spacer(modifier = Modifier.height(8.dp))
                CompatibilityBadge(match = student.match)
                Spacer(modifier = Modifier.height(8.dp))
                student.match.reasons.forEach { reason ->
                    Text(
                        text = "• $reason",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFD1FAE5),
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }

        SectionTitle("Complementary Skills")
        if (profile.skills.isEmpty()) {
            EmptyLine("No skills listed.")
        } else {
            WrappingTags(profile.skills, student.match.complementarySkills)
        }

        SectionTitle("Startup Interests")
        if (profile.interests.isEmpty()) {
            EmptyLine("No interests listed.")
        } else {
            WrappingTags(profile.interests, student.match.sharedInterests)
        }

        SectionTitle("Domain Focus")
        if (profile.areasOfInterest.isEmpty()) {
            EmptyLine("No startup areas listed.")
        } else {
            WrappingTags(profile.areasOfInterest, student.match.sharedInterests)
        }

        SectionTitle("Track Record & Experience")
        if (profile.experience.isBlank() && profile.experienceTags.isEmpty()) {
            EmptyLine("No experience notes added yet.")
        } else {
            if (profile.experienceTags.isNotEmpty()) {
                WrappingTags(profile.experienceTags, student.match.sharedExperience)
                Spacer(modifier = Modifier.height(8.dp))
            }
            if (profile.experience.isNotBlank()) {
                Text(text = profile.experience, style = MaterialTheme.typography.bodyMedium, color = DarkTextPrimary)
            }
        }

        SectionTitle("Entrepreneurial Goals")
        if (profile.goals.isBlank()) {
            EmptyLine("No goals stated yet.")
        } else {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DarkSurfaceVariant,
                border = BorderStroke(1.dp, DarkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "\"${profile.goals}\"",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkTextPrimary,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
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
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen, contentColor = Color(0xFF071206))
            ) {
                Text("Send Co-founder Request", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            }
        }
        ConnectionStatus.PENDING_OUTGOING -> {
            OutlinedButton(
                onClick = {},
                enabled = false,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, DarkBorder)
            ) {
                Text("Connection Request Pending", style = MaterialTheme.typography.labelLarge)
            }
        }
        ConnectionStatus.PENDING_INCOMING -> {
            Button(
                onClick = onAccept,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen, contentColor = Color(0xFF071206))
            ) {
                Text("Accept Connection Request", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            }
        }
        ConnectionStatus.ACCEPTED -> {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = PrimaryGreenContainer,
                border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Filled.Check, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Connected in Co-founder Network",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGreen
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Spacer(modifier = Modifier.height(18.dp))
    Text(text = text, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = DarkTextPrimary)
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
private fun EmptyLine(text: String) {
    Text(text = text, style = MaterialTheme.typography.bodyMedium, color = DarkTextMuted)
}

@Composable
private fun InfoPill(text: String, isAccent: Boolean = false) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isAccent) PrimaryGreenContainer else DarkSurfaceVariant,
        border = BorderStroke(1.dp, if (isAccent) PrimaryGreen.copy(alpha = 0.4f) else DarkBorder)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = if (isAccent) PrimaryGreen else DarkTextSecondary,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
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
                color = if (isHighlighted) PrimaryGreenContainer else DarkSurfaceVariant,
                border = BorderStroke(1.dp, if (isHighlighted) PrimaryGreen.copy(alpha = 0.5f) else DarkBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isHighlighted) {
                        Icon(imageVector = Icons.Filled.Check, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                        text = tag,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Medium,
                        color = if (isHighlighted) PrimaryGreen else DarkTextSecondary
                    )
                }
            }
        }
    }
}
