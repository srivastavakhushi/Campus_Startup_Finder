package com.campus.startup.features.startupprofiles.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.campus.startup.core.theme.DarkBorder
import com.campus.startup.core.theme.DarkSurface
import com.campus.startup.core.theme.DarkSurfaceVariant
import com.campus.startup.core.theme.DarkTextPrimary
import com.campus.startup.core.theme.DarkTextSecondary
import com.campus.startup.core.theme.PrimaryBlue
import com.campus.startup.core.theme.PrimaryGreen
import com.campus.startup.core.theme.PrimaryGreenContainer
import com.campus.startup.features.auth.CampusUserRole
import com.campus.startup.features.auth.UserSession
import com.campus.startup.features.matching.data.model.StudentMatchProfile
import com.campus.startup.features.matching.presentation.MatchingUiState
import com.campus.startup.features.matching.presentation.MatchingViewModel
import com.campus.startup.features.recruitment.presentation.RecruitmentViewModel
import com.campus.startup.features.startupprofiles.data.model.StartupOptions
import com.campus.startup.features.startupprofiles.data.model.toCsvList

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun StartupListScreen(
    viewModel: StartupProfilesViewModel,
    matchingViewModel: MatchingViewModel,
    recruitmentViewModel: RecruitmentViewModel,
    onStartupClick: (String) -> Unit,
    onAddClick: () -> Unit,
    onCreateOrEditProfile: () -> Unit,
    onOpenProfileHub: () -> Unit
) {
    val startups by viewModel.startups.collectAsStateWithLifecycle()
    val savedStartups by viewModel.savedStartups.collectAsStateWithLifecycle()
    val query by viewModel.query.collectAsStateWithLifecycle()
    val domainFilter by viewModel.domainFilter.collectAsStateWithLifecycle()
    val stageFilter by viewModel.stageFilter.collectAsStateWithLifecycle()

    val matchingState by matchingViewModel.uiState.collectAsState()
    val applications by recruitmentViewModel.allApplications.collectAsState()
    val savedOpenings by recruitmentViewModel.savedOpenings.collectAsState()
    val loggedInUser by UserSession.currentUser.collectAsState()

    var showCommunityChat by rememberSaveable { mutableStateOf(false) }

    val successMatch = matchingState as? MatchingUiState.Success
    val me = successMatch?.me
    val isProfileCreated = me?.profileComplete == true

    // Hub badge count: applications + saved startups + saved roles + pending incoming connections
    val hubBadgeCount = (successMatch?.pendingIncoming ?: 0) + applications.size + savedOpenings.size + savedStartups.size

    // Startups where the user has applied and been recruited/shortlisted
    val recruitedStartupNamesFromApps = applications
        .filter { it.status.equals("Recruited", ignoreCase = true) || it.status.equals("Shortlisted", ignoreCase = true) }
        .map { it.startupName }
        .toSet()

    val evaluatedStartups = startups.map { startup ->
        viewModel.evaluateStartupForProfile(startup, me, recruitedStartupNamesFromApps)
    }

    val recruitedStartups = evaluatedStartups.filter { it.isRecruited }
    val recommendedStartups = evaluatedStartups
        .filter { !it.isRecruited && (it.matchScore > 0 || it.startup.isInterested || it.startup.isSaved) }
        .sortedByDescending { it.matchScore }
    val otherStartups = evaluatedStartups
        .filter { !it.isRecruited && it.matchScore == 0 && !it.startup.isInterested && !it.startup.isSaved }

    Scaffold(
        contentWindowInsets = NoInsets,
        topBar = {
            TopAppBar(
                windowInsets = NoInsets,
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Campus Startups",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = DarkTextPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (loggedInUser?.role == CampusUserRole.PROFESSOR) PrimaryBlue.copy(alpha = 0.2f) else PrimaryGreenContainer,
                                border = BorderStroke(
                                    1.dp,
                                    if (loggedInUser?.role == CampusUserRole.PROFESSOR) PrimaryBlue else PrimaryGreen.copy(alpha = 0.4f)
                                )
                            ) {
                                Text(
                                    text = loggedInUser?.role?.badgeLabel ?: "HOME",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (loggedInUser?.role == CampusUserRole.PROFESSOR) PrimaryBlue else PrimaryGreen,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = when {
                                isProfileCreated -> "Personalized for ${me?.name}"
                                loggedInUser != null -> "Signed in as ${loggedInUser?.displayName} (${loggedInUser?.role?.displayName})"
                                else -> "Create your profile to see startup recommendations"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = DarkTextSecondary
                        )
                    }
                },
                actions = {
                    // Community Chat Popup Trigger in Top Bar
                    IconButton(onClick = { showCommunityChat = true }) {
                        Icon(
                            imageVector = Icons.Default.Forum,
                            contentDescription = "Open Community Chat Popup",
                            tint = PrimaryGreen
                        )
                    }

                    // Profile Icon in the corner -> Opens Saved Startups/Opportunities/Roles, Connections, Inbox & Applications
                    BadgedBox(
                        badge = {
                            if (hubBadgeCount > 0) {
                                Badge(containerColor = PrimaryGreen) {
                                    Text(
                                        text = hubBadgeCount.toString(),
                                        color = Color(0xFF071206),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        },
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        IconButton(onClick = onOpenProfileHub) {
                            if (isProfileCreated) {
                                Surface(
                                    shape = CircleShape,
                                    color = PrimaryGreenContainer,
                                    border = BorderStroke(1.5.dp, PrimaryGreen),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = me?.name?.trim()?.take(1)?.uppercase() ?: "P",
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = PrimaryGreen
                                        )
                                    }
                                }
                            } else {
                                Icon(
                                    imageVector = Icons.Outlined.AccountCircle,
                                    contentDescription = "My Profile, Saved Items, Inbox, Applications & Connections",
                                    tint = PrimaryGreen,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
            )
        },
        floatingActionButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Popup Community Chatbox Button on Home Page
                ExtendedFloatingActionButton(
                    onClick = { showCommunityChat = true },
                    containerColor = DarkSurfaceVariant,
                    contentColor = PrimaryGreen,
                    icon = { Icon(Icons.Filled.Forum, contentDescription = "Community Chat") },
                    text = { Text("Community Chat", fontWeight = FontWeight.Bold) }
                )

                ExtendedFloatingActionButton(
                    onClick = onAddClick,
                    containerColor = PrimaryGreen,
                    contentColor = Color(0xFF071206),
                    icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                    text = { Text("New startup", fontWeight = FontWeight.Bold) }
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (!isProfileCreated) {
                // Step 1: Prompt to create profile first with Name, Course, Age, Year, Interests & Resume
                item {
                    CreateProfileOnboardingCard(
                        onCreateProfile = onCreateOrEditProfile
                    )
                }

                // Search & Filters for browsing general directory before creating profile
                item {
                    SearchAndFiltersHeader(
                        query = query,
                        domainFilter = domainFilter,
                        stageFilter = stageFilter,
                        onQueryChange = viewModel::onQueryChange,
                        onDomainFilterChange = viewModel::onDomainFilterChange,
                        onStageFilterChange = viewModel::onStageFilterChange
                    )
                }

                item {
                    SectionHeader(
                        title = "All Campus Startups (${startups.size})",
                        subtitle = "Like or Save startups below — saved startups appear in your Profile"
                    )
                }

                items(evaluatedStartups, key = { "all_${it.startup.id}" }) { rec ->
                    StartupRecommendationCard(
                        recommendation = rec,
                        showMatchDetails = false,
                        onClick = { onStartupClick(rec.startup.id) },
                        onToggleInterested = { viewModel.toggleInterested(rec.startup) },
                        onToggleSaved = { viewModel.toggleSaved(rec.startup) },
                        onToggleRecruited = { viewModel.toggleRecruited(rec.startup) }
                    )
                }
            } else {
                // Step 2: Profile IS created! Show Profile Summary + Recruited Startups at Top + Interest Recommendations below
                item {
                    ActiveProfileSummaryCard(
                        profile = me!!,
                        onEditProfile = onCreateOrEditProfile,
                        onOpenHub = onOpenProfileHub
                    )
                }

                // Search & Domain/Stage filters
                item {
                    SearchAndFiltersHeader(
                        query = query,
                        domainFilter = domainFilter,
                        stageFilter = stageFilter,
                        onQueryChange = viewModel::onQueryChange,
                        onDomainFilterChange = viewModel::onDomainFilterChange,
                        onStageFilterChange = viewModel::onStageFilterChange
                    )
                }

                // TOP SECTION: Startups you're currently recruited in
                if (recruitedStartups.isNotEmpty()) {
                    item {
                        SectionHeader(
                            title = "Currently Recruited In (${recruitedStartups.size})",
                            subtitle = "Startups where you are an active team member",
                            highlightColor = PrimaryGreen
                        )
                    }
                    items(recruitedStartups, key = { "recruited_${it.startup.id}" }) { rec ->
                        StartupRecommendationCard(
                            recommendation = rec,
                            showMatchDetails = true,
                            onClick = { onStartupClick(rec.startup.id) },
                            onToggleInterested = { viewModel.toggleInterested(rec.startup) },
                            onToggleSaved = { viewModel.toggleSaved(rec.startup) },
                            onToggleRecruited = { viewModel.toggleRecruited(rec.startup) }
                        )
                    }
                }

                // SECOND SECTION: Startup Recommendations based on user's interests
                item {
                    SectionHeader(
                        title = "Recommended For You (${recommendedStartups.size})",
                        subtitle = "Startups aligned with your interests in ${(me?.areasOfInterest.orEmpty() + me?.interests.orEmpty()).take(4).joinToString(", ")}"
                    )
                }

                if (recommendedStartups.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkSurface),
                            border = BorderStroke(1.dp, DarkBorder)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "No exact interest matches for current filters",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Update your startup interests in your profile or clear domain filters to see more recommendations.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = DarkTextSecondary
                                )
                            }
                        }
                    }
                } else {
                    items(recommendedStartups, key = { "rec_${it.startup.id}" }) { rec ->
                        StartupRecommendationCard(
                            recommendation = rec,
                            showMatchDetails = true,
                            onClick = { onStartupClick(rec.startup.id) },
                            onToggleInterested = { viewModel.toggleInterested(rec.startup) },
                            onToggleSaved = { viewModel.toggleSaved(rec.startup) },
                            onToggleRecruited = { viewModel.toggleRecruited(rec.startup) }
                        )
                    }
                }

                // THIRD SECTION: Other Campus Startups to Explore
                if (otherStartups.isNotEmpty()) {
                    item {
                        SectionHeader(
                            title = "Explore More Campus Startups (${otherStartups.size})",
                            subtitle = "Discover ventures across other domains on campus"
                        )
                    }
                    items(otherStartups, key = { "other_${it.startup.id}" }) { rec ->
                        StartupRecommendationCard(
                            recommendation = rec,
                            showMatchDetails = false,
                            onClick = { onStartupClick(rec.startup.id) },
                            onToggleInterested = { viewModel.toggleInterested(rec.startup) },
                            onToggleSaved = { viewModel.toggleSaved(rec.startup) },
                            onToggleRecruited = { viewModel.toggleRecruited(rec.startup) }
                        )
                    }
                }
            }
        }
    }

    if (showCommunityChat) {
        val displayName = me?.name?.takeIf { it.isNotBlank() }
            ?: loggedInUser?.displayName
            ?: "Campus Member"
        val roleBadge = when {
            loggedInUser?.role == CampusUserRole.PROFESSOR -> "Professor • Faculty Mentor"
            me != null && me.course.isNotBlank() -> "Student • ${me.course}"
            else -> "Student • Campus Innovator"
        }
        CommunityChatDialog(
            currentUserName = displayName,
            currentUserRoleBadge = roleBadge,
            onDismiss = { showCommunityChat = false }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CreateProfileOnboardingCard(
    onCreateProfile: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(1.5.dp, PrimaryGreen.copy(alpha = 0.65f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = PrimaryGreenContainer,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = PrimaryGreen,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Create Your Campus Profile",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DarkTextPrimary
                    )
                    Text(
                        text = "Step 1 • Unlock Personalized Startup Recommendations",
                        style = MaterialTheme.typography.labelSmall,
                        color = PrimaryGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Set up your student profile to see startups tailored to your interests, pin startups you're currently recruited in at the top, and apply in 1 tap with your resume.",
                style = MaterialTheme.typography.bodyMedium,
                color = DarkTextSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("Name & Age", "Course & Year", "Startup Interests", "Upload Resume (PDF)", "Recruited Status").forEach { item ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = PrimaryGreenContainer,
                        border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = item,
                            style = MaterialTheme.typography.labelSmall,
                            color = PrimaryGreen,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onCreateProfile,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryGreen,
                    contentColor = Color(0xFF071206)
                )
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Create Profile Now", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ActiveProfileSummaryCard(
    profile: StudentMatchProfile,
    onEditProfile: () -> Unit,
    onOpenHub: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(1.dp, DarkBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Welcome back, ${profile.name}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DarkTextPrimary
                    )
                    Text(
                        text = profile.academicLine,
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkTextSecondary
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(
                        onClick = onEditProfile,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, DarkBorder),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp), tint = PrimaryGreen)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Edit Profile", style = MaterialTheme.typography.labelSmall, color = PrimaryGreen)
                    }
                }
            }

            if (profile.resumeName.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PrimaryGreenContainer,
                    border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.35f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = PrimaryGreen,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Resume Attached: ${profile.resumeName}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryGreen
                        )
                    }
                }
            }

            val allInterests = (profile.areasOfInterest + profile.interests).distinct()
            if (allInterests.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Your Interests:",
                    style = MaterialTheme.typography.labelSmall,
                    color = DarkTextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    allInterests.forEach { interest ->
                        StartupTag(interest)
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchAndFiltersHeader(
    query: String,
    domainFilter: String?,
    stageFilter: String?,
    onQueryChange: (String) -> Unit,
    onDomainFilterChange: (String?) -> Unit,
    onStageFilterChange: (String?) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search startups, domains, skills or needs…") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            singleLine = true,
            shape = RoundedCornerShape(14.dp)
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                FilterChip(
                    selected = domainFilter == null,
                    onClick = { onDomainFilterChange(null) },
                    label = { Text("All domains") }
                )
            }
            items(StartupOptions.domains) { domain ->
                FilterChip(
                    selected = domainFilter == domain,
                    onClick = {
                        onDomainFilterChange(if (domainFilter == domain) null else domain)
                    },
                    label = { Text(domain) }
                )
            }
        }

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                FilterChip(
                    selected = stageFilter == null,
                    onClick = { onStageFilterChange(null) },
                    label = { Text("All stages") }
                )
            }
            items(StartupOptions.stages) { stage ->
                FilterChip(
                    selected = stageFilter == stage,
                    onClick = {
                        onStageFilterChange(if (stageFilter == stage) null else stage)
                    },
                    label = { Text(stage) }
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    subtitle: String,
    highlightColor: Color = DarkTextPrimary
) {
    Column(modifier = Modifier.padding(top = 4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = highlightColor
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = DarkTextSecondary
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StartupRecommendationCard(
    recommendation: StartupRecommendation,
    showMatchDetails: Boolean,
    onClick: () -> Unit,
    onToggleInterested: () -> Unit,
    onToggleSaved: () -> Unit,
    onToggleRecruited: () -> Unit
) {
    val startup = recommendation.startup
    val isRecruited = recommendation.isRecruited

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(
            width = if (isRecruited) 1.5.dp else 1.dp,
            color = when {
                isRecruited -> PrimaryGreen
                recommendation.matchScore >= 75 -> PrimaryGreen.copy(alpha = 0.45f)
                else -> DarkBorder
            }
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (isRecruited) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PrimaryGreenContainer,
                    border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.5f)),
                    modifier = Modifier.padding(bottom = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = PrimaryGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (startup.recruitedRole.isNotBlank()) {
                                "CURRENTLY RECRUITED • ${startup.recruitedRole.uppercase()}"
                            } else {
                                "CURRENTLY RECRUITED • ACTIVE TEAM MEMBER"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGreen
                        )
                    }
                }
            } else if (showMatchDetails && recommendation.matchScore > 0) {
                Row(
                    modifier = Modifier.padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = PrimaryGreenContainer
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = PrimaryGreen,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${recommendation.matchScore}% Interest Match",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryGreen
                            )
                        }
                    }
                    if (recommendation.matchedInterests.isNotEmpty()) {
                        Text(
                            text = "Matches: ${recommendation.matchedInterests.take(3).joinToString(", ")}",
                            style = MaterialTheme.typography.labelSmall,
                            color = PrimaryGreen,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = startup.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DarkTextPrimary
                    )
                    if (startup.founderName.isNotBlank()) {
                        Text(
                            text = "Founded by ${startup.founderName}",
                            style = MaterialTheme.typography.labelSmall,
                            color = DarkTextSecondary
                        )
                    }
                }

                // Both Like and Save buttons on top right of Startup Card
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        modifier = Modifier.clickable(onClick = onToggleInterested),
                        shape = RoundedCornerShape(10.dp),
                        color = if (startup.isInterested) Color(0xFF36151E) else DarkSurfaceVariant,
                        border = BorderStroke(
                            1.dp,
                            if (startup.isInterested) Color(0xFFF43F5E).copy(alpha = 0.6f) else DarkBorder
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = if (startup.isInterested) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Like Startup",
                                tint = if (startup.isInterested) Color(0xFFF43F5E) else DarkTextSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = if (startup.isInterested) "Liked (${startup.likesCount})" else "Like (${startup.likesCount})",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (startup.isInterested) Color(0xFFF43F5E) else DarkTextSecondary
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier.clickable(onClick = onToggleSaved),
                        shape = RoundedCornerShape(10.dp),
                        color = if (startup.isSaved) PrimaryGreenContainer else DarkSurfaceVariant,
                        border = BorderStroke(
                            1.dp,
                            if (startup.isSaved) PrimaryGreen.copy(alpha = 0.6f) else DarkBorder
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = if (startup.isSaved) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                                contentDescription = "Save Startup",
                                tint = if (startup.isSaved) PrimaryGreen else DarkTextSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = if (startup.isSaved) "Saved" else "Save",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (startup.isSaved) PrimaryGreen else DarkTextSecondary
                            )
                        }
                    }
                }
            }

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
                color = DarkTextSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 8.dp)
            )

            val needs = startup.collaborationTypes.toCsvList()
            if (needs.isNotEmpty()) {
                Text(
                    text = "Looking for:",
                    style = MaterialTheme.typography.labelSmall,
                    color = DarkTextSecondary,
                    modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    needs.forEach { StartupTag(it) }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = isRecruited,
                    onClick = onToggleRecruited,
                    leadingIcon = if (isRecruited) {
                        {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = PrimaryGreen,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    } else null,
                    label = {
                        Text(
                            text = if (isRecruited) "Recruited Here" else "Mark as Recruited",
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryGreenContainer,
                        selectedLabelColor = PrimaryGreen
                    )
                )
            }
        }
    }
}
