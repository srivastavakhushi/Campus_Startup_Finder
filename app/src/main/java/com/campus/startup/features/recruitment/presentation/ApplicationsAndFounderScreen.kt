package com.campus.startup.features.recruitment.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.BusinessCenter
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.campus.startup.core.theme.PrimaryBlue
import com.campus.startup.core.theme.StartupGreen
import com.campus.startup.features.recruitment.data.model.JobApplication
import com.campus.startup.features.recruitment.data.model.StartupOpening
import com.campus.startup.features.recruitment.presentation.components.StartupOpeningCard
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationsAndFounderScreen(
    viewModel: RecruitmentViewModel,
    onNavigateBack: () -> Unit,
    onOpeningClick: (String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("My Applications 📋", "Saved Roles 🔖", "Founder Portal 💼")

    val applications by viewModel.allApplications.collectAsState()
    val savedOpenings by viewModel.savedOpenings.collectAsState()
    val myPostedOpenings by viewModel.myPostedOpenings.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recruitment & Applications", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Tab Row
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = PrimaryBlue
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            when (selectedTab) {
                0 -> {
                    // 1. My Applications
                    if (applications.isEmpty()) {
                        EmptyStateView(
                            emoji = "📝",
                            title = "No applications submitted yet",
                            subtitle = "Find startup roles that match your skills and apply in 1 tap!"
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(applications, key = { it.id }) { app ->
                                StudentApplicationCard(application = app)
                            }
                        }
                    }
                }
                1 -> {
                    // 2. Saved Roles
                    if (savedOpenings.isEmpty()) {
                        EmptyStateView(
                            emoji = "🔖",
                            title = "No saved openings",
                            subtitle = "Bookmark startup openings you are interested in to review them here."
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(top = 10.dp, bottom = 20.dp)
                        ) {
                            items(savedOpenings, key = { it.id }) { opening ->
                                StartupOpeningCard(
                                    opening = opening,
                                    onClick = { onOpeningClick(opening.id) },
                                    onSaveToggle = { viewModel.toggleSaveOpening(opening) }
                                )
                            }
                        }
                    }
                }
                2 -> {
                    // 3. Founder Portal: Manage Posted Openings & Received Applicants
                    FounderPortalView(
                        myOpenings = myPostedOpenings,
                        allApplications = applications,
                        onOpeningClick = onOpeningClick,
                        onDeleteOpening = viewModel::deleteOpening,
                        onUpdateApplicationStatus = viewModel::updateApplicationStatus
                    )
                }
            }
        }
    }
}

@Composable
private fun StudentApplicationCard(application: JobApplication) {
    val dateStr = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
        .format(Date(application.appliedTimestamp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = application.openingTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Applied to ${application.startupName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (application.status) {
                        "Shortlisted" -> Color(0xFFD1FAE5)
                        "Interviewing" -> Color(0xFFE0E7FF)
                        else -> Color(0xFFFEF3C7)
                    }
                ) {
                    Text(
                        text = application.status,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = when (application.status) {
                            "Shortlisted" -> Color(0xFF047857)
                            "Interviewing" -> Color(0xFF3730A3)
                            else -> Color(0xFFB45309)
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (application.matchedSkills.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Submitted Skills:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    application.matchedSkills.forEach { skill ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = skill,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            Text(
                text = "Applied on $dateStr",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun FounderPortalView(
    myOpenings: List<StartupOpening>,
    allApplications: List<JobApplication>,
    onOpeningClick: (String) -> Unit,
    onDeleteOpening: (String) -> Unit,
    onUpdateApplicationStatus: (String, String) -> Unit
) {
    if (myOpenings.isEmpty()) {
        EmptyStateView(
            emoji = "💼",
            title = "No openings posted yet",
            subtitle = "Founders can post roles for developers, designers, marketers, and interns using the '+ Post Opening' button on the Recruitment tab!"
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Your Active Startup Postings (${myOpenings.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            items(myOpenings, key = { it.id }) { opening ->
                val openingApps = allApplications.filter { it.openingId == opening.id }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = opening.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${opening.startupName} • ${opening.stipend}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(onClick = { onDeleteOpening(opening.id) }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete Opening",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Received Applications (${openingApps.size})",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )

                        if (openingApps.isEmpty()) {
                            Text(
                                text = "No student applications received yet. Openings are active on the campus recruitment feed!",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        } else {
                            openingApps.forEach { app ->
                                Spacer(modifier = Modifier.height(8.dp))
                                FounderApplicantCard(
                                    application = app,
                                    onUpdateStatus = { status -> onUpdateApplicationStatus(app.id, status) }
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
private fun FounderApplicantCard(
    application: JobApplication,
    onUpdateStatus: (String) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = application.applicantName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = application.applicantBranchYear,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Status chip dropdown or buttons
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    FilterChip(
                        selected = application.status == "Shortlisted",
                        onClick = { onUpdateStatus("Shortlisted") },
                        label = { Text("Shortlist", style = MaterialTheme.typography.labelSmall) }
                    )
                    FilterChip(
                        selected = application.status == "Interviewing",
                        onClick = { onUpdateStatus("Interviewing") },
                        label = { Text("Interview", style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            if (application.pitchNote.isNotBlank()) {
                Text(
                    text = "\"${application.pitchNote}\"",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Email, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = application.applicantEmail,
                    style = MaterialTheme.typography.labelSmall
                )
                if (application.portfolioUrl.isNotBlank()) {
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(Icons.Outlined.Link, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = application.portfolioUrl,
                        style = MaterialTheme.typography.labelSmall,
                        color = PrimaryBlue
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyStateView(
    emoji: String,
    title: String,
    subtitle: String
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = emoji, style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
