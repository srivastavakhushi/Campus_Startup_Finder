package com.campus.startup.features.matching.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.MarkEmailUnread
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.campus.startup.core.components.ModernAvatar
import com.campus.startup.core.theme.DarkBorder
import com.campus.startup.core.theme.DarkSurface
import com.campus.startup.core.theme.DarkTextPrimary
import com.campus.startup.core.theme.DarkTextSecondary
import com.campus.startup.core.theme.PrimaryBlue
import com.campus.startup.core.theme.PrimaryGreen
import com.campus.startup.core.theme.PrimaryGreenContainer
import com.campus.startup.features.matching.data.model.ConnectionStatus
import com.campus.startup.features.matching.data.model.MatchCatalog
import com.campus.startup.features.matching.data.model.StudentMatchProfile
import com.campus.startup.features.matching.data.model.personLabel
import com.campus.startup.features.matching.data.model.resolveConnectionStatus
import com.campus.startup.features.recruitment.data.model.JobApplication
import com.campus.startup.features.recruitment.presentation.RecruitmentViewModel
import com.campus.startup.features.recruitment.presentation.components.StartupOpeningCard
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class InboxMessageItem(
    val id: String,
    val senderName: String,
    val senderRoleOrStartup: String,
    val subject: String,
    val preview: String,
    val timeAgo: String,
    val isUnread: Boolean = false,
    val replies: List<String> = emptyList()
)

private enum class ProfileConnectionFilter(val label: String) {
    ALL("All"),
    INCOMING("Incoming"),
    SENT("Sent"),
    CONNECTED("Connected")
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProfileHubScreen(
    matchingViewModel: MatchingViewModel,
    recruitmentViewModel: RecruitmentViewModel,
    onNavigateBack: () -> Unit,
    onEditProfile: () -> Unit,
    onOpeningClick: (String) -> Unit,
    onStudentClick: (String) -> Unit
) {
    val matchingState by matchingViewModel.uiState.collectAsState()
    val applications by recruitmentViewModel.allApplications.collectAsState()
    val savedOpenings by recruitmentViewModel.savedOpenings.collectAsState()
    val myPostedOpenings by recruitmentViewModel.myPostedOpenings.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    MatchingStatusSnackbar(matchingViewModel, snackbarHostState)

    val successState = matchingState as? MatchingUiState.Success
    val me = successState?.me

    // Order requested by user: 0 = Saved Roles, 1 = Your Connections, 2 = Your Inbox, 3 = Applications
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var connectionFilter by rememberSaveable { mutableStateOf(ProfileConnectionFilter.ALL) }

    val inboxMessages = remember {
        mutableStateListOf(
            InboxMessageItem(
                id = "msg_1",
                senderName = "Rohan Verma",
                senderRoleOrStartup = "Founder • StudyGenie AI",
                subject = "Welcome to the StudyGenie AI Product Team!",
                preview = "Hey! Excited to have you recruited on our core team. Let's sync at the Incubation Hub at 5 PM for our sprint planning.",
                timeAgo = "2h ago",
                isUnread = true
            ),
            InboxMessageItem(
                id = "msg_2",
                senderName = "Meera Iyer",
                senderRoleOrStartup = "Co-founder Match • BBA / MBA",
                subject = "Co-founder Connection Request",
                preview = "Hi! I saw your profile and loved your interest in campus products. Would love to team up for the upcoming E-Cell pitch!",
                timeAgo = "5h ago",
                isUnread = true
            ),
            InboxMessageItem(
                id = "msg_3",
                senderName = "Aman Sharma",
                senderRoleOrStartup = "Founder • CampusBites",
                subject = "Startup Opening matches your profile",
                preview = "We noticed your skills in Android & product building — check out our Founding Engineer opening at CampusBites!",
                timeAgo = "1d ago",
                isUnread = false
            )
        )
    }

    val allConnectionsCount = successState?.connections?.size ?: 0
    val pendingConnectionsCount = successState?.pendingIncoming ?: 0
    val unreadInboxCount = inboxMessages.count { it.isUnread }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "My Profile",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = DarkTextPrimary
                        )
                        Text(
                            text = "Saved Roles • Connections • Inbox • Applications",
                            style = MaterialTheme.typography.bodySmall,
                            color = DarkTextSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onEditProfile) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Profile", tint = PrimaryGreen)
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
            // Profile Summary Card at Top
            ProfileSummaryHeader(
                profile = me,
                onEditProfile = onEditProfile
            )

            // Quick stats selector cards for Saved Roles, Your Connections, Your Inbox, Applications
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ProfileStatPill(
                    title = "Saved Roles",
                    count = savedOpenings.size,
                    icon = Icons.Default.Bookmark,
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    modifier = Modifier.weight(1f)
                )
                ProfileStatPill(
                    title = "Connections",
                    count = allConnectionsCount,
                    badgeAlert = pendingConnectionsCount > 0,
                    icon = Icons.Default.Group,
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    modifier = Modifier.weight(1f)
                )
                ProfileStatPill(
                    title = "Inbox",
                    count = inboxMessages.size,
                    badgeAlert = unreadInboxCount > 0,
                    icon = Icons.Default.Inbox,
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    modifier = Modifier.weight(1f)
                )
                ProfileStatPill(
                    title = "Applications",
                    count = applications.size,
                    icon = Icons.AutoMirrored.Outlined.Assignment,
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    modifier = Modifier.weight(1f)
                )
            }

            // 4 Hub Tabs: Saved Roles, Your Connections, Your Inbox, Applications
            val tabs = listOf(
                Triple("Saved Roles", savedOpenings.size, Icons.Default.Bookmark),
                Triple("Your Connections", pendingConnectionsCount, Icons.Default.Group),
                Triple("Your Inbox", unreadInboxCount, Icons.Default.Inbox),
                Triple("Applications", applications.size, Icons.AutoMirrored.Outlined.Assignment)
            )

            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 12.dp,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = PrimaryGreen
            ) {
                tabs.forEachIndexed { index, (title, badgeCount, _) ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = title,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == index) PrimaryGreen else DarkTextSecondary
                                )
                                if (badgeCount > 0) {
                                    Badge(
                                        containerColor = if (selectedTab == index) PrimaryGreen else DarkBorder
                                    ) {
                                        Text(
                                            text = badgeCount.toString(),
                                            color = if (selectedTab == index) Color(0xFF071206) else DarkTextPrimary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    )
                }
            }

            when (selectedTab) {
                0 -> {
                    // 1. Saved Roles
                    if (savedOpenings.isEmpty()) {
                        EmptyHubState(
                            title = "No Saved Roles Yet",
                            subtitle = "Bookmark startup openings in the Recruitment tab and they will appear exclusively here in your Profile."
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
                        ) {
                            items(savedOpenings, key = { it.id }) { opening ->
                                StartupOpeningCard(
                                    opening = opening,
                                    onClick = { onOpeningClick(opening.id) },
                                    onSaveToggle = { recruitmentViewModel.toggleSaveOpening(opening) }
                                )
                            }
                        }
                    }
                }
                1 -> {
                    // 2. Your Connections
                    if (successState == null) {
                        EmptyHubState(
                            title = "Loading Connections...",
                            subtitle = "Fetching your co-founder network."
                        )
                    } else {
                        val allRows = successState.connections.mapNotNull { req ->
                            val otherId = when (MatchCatalog.CURRENT_USER_ID) {
                                req.fromProfileId -> req.toProfileId
                                req.toProfileId -> req.fromProfileId
                                else -> null
                            } ?: return@mapNotNull null
                            val other = successState.directory.find { it.profile.id == otherId }?.profile
                            val status = resolveConnectionStatus(MatchCatalog.CURRENT_USER_ID, otherId, listOf(req))
                            Triple(req, other, status)
                        }

                        val filteredRows = allRows.filter { (_, _, status) ->
                            when (connectionFilter) {
                                ProfileConnectionFilter.ALL -> true
                                ProfileConnectionFilter.INCOMING -> status == ConnectionStatus.PENDING_INCOMING
                                ProfileConnectionFilter.SENT -> status == ConnectionStatus.PENDING_OUTGOING
                                ProfileConnectionFilter.CONNECTED -> status == ConnectionStatus.ACCEPTED
                            }
                        }

                        Column(modifier = Modifier.fillMaxSize()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState())
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                ProfileConnectionFilter.values().forEach { filterOption ->
                                    FilterChip(
                                        selected = connectionFilter == filterOption,
                                        onClick = { connectionFilter = filterOption },
                                        label = {
                                            Text(
                                                filterOption.label,
                                                fontWeight = if (connectionFilter == filterOption) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = PrimaryGreenContainer,
                                            selectedLabelColor = PrimaryGreen
                                        )
                                    )
                                }
                            }

                            if (filteredRows.isEmpty()) {
                                EmptyHubState(
                                    title = if (allRows.isEmpty()) "No Connections Yet" else "No ${connectionFilter.label} Connections",
                                    subtitle = "Swipe right or send connection requests in the Co-founders tab to manage all your connections here."
                                )
                            } else {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    items(filteredRows, key = { it.first.id }) { (req, other, status) ->
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable(enabled = other != null) {
                                                    other?.id?.let(onStudentClick)
                                                },
                                            shape = RoundedCornerShape(16.dp),
                                            colors = CardDefaults.cardColors(containerColor = DarkSurface),
                                            border = BorderStroke(1.dp, DarkBorder)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth().padding(14.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                ModernAvatar(
                                                    name = other?.name ?: "Student",
                                                    role = other?.preferredRole,
                                                    size = 46.dp
                                                )
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = other?.name ?: "Campus Student",
                                                        style = MaterialTheme.typography.titleSmall,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    if (other != null) {
                                                        Text(
                                                            text = "${other.preferredRole.personLabel()} • ${other.academicLine}",
                                                            style = MaterialTheme.typography.labelSmall,
                                                            color = DarkTextSecondary
                                                        )
                                                    }
                                                    Spacer(modifier = Modifier.height(2.dp))
                                                    Text(
                                                        text = when (status) {
                                                            ConnectionStatus.PENDING_INCOMING -> "Wants to connect with you"
                                                            ConnectionStatus.PENDING_OUTGOING -> "Request sent • Pending"
                                                            ConnectionStatus.ACCEPTED -> "Connected Co-founder"
                                                            ConnectionStatus.NONE -> ""
                                                        },
                                                        style = MaterialTheme.typography.labelSmall,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = if (status == ConnectionStatus.ACCEPTED) PrimaryGreen else PrimaryBlue
                                                    )
                                                }
                                                if (status == ConnectionStatus.PENDING_INCOMING) {
                                                    Button(
                                                        onClick = { matchingViewModel.acceptRequest(req.id) },
                                                        colors = ButtonDefaults.buttonColors(
                                                            containerColor = PrimaryGreen,
                                                            contentColor = Color(0xFF071206)
                                                        ),
                                                        shape = RoundedCornerShape(10.dp),
                                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                                    ) {
                                                        Text("Accept", fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                2 -> {
                    // 3. Your Inbox
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(inboxMessages, key = { it.id }) { msg ->
                            InboxMessageCard(
                                item = msg,
                                onMarkRead = {
                                    val idx = inboxMessages.indexOfFirst { it.id == msg.id }
                                    if (idx >= 0) {
                                        inboxMessages[idx] = inboxMessages[idx].copy(isUnread = false)
                                    }
                                },
                                onReply = { replyText ->
                                    val idx = inboxMessages.indexOfFirst { it.id == msg.id }
                                    if (idx >= 0 && replyText.isNotBlank()) {
                                        val updated = inboxMessages[idx]
                                        inboxMessages[idx] = updated.copy(
                                            isUnread = false,
                                            replies = updated.replies + replyText.trim()
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
                3 -> {
                    // 4. Applications (Submitted Applications + Posted Openings management if any)
                    if (applications.isEmpty() && myPostedOpenings.isEmpty()) {
                        EmptyHubState(
                            title = "No Applications Submitted Yet",
                            subtitle = "Apply to startup roles in the Recruitment tab and track your application status here in your Profile."
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (applications.isNotEmpty()) {
                                item {
                                    Text(
                                        text = "Submitted Applications (${applications.size})",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = DarkTextPrimary
                                    )
                                }
                                items(applications, key = { it.id }) { app ->
                                    HubApplicationCard(
                                        application = app,
                                        onClick = { onOpeningClick(app.openingId) }
                                    )
                                }
                            }

                            if (myPostedOpenings.isNotEmpty()) {
                                item {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Roles Posted by You (${myPostedOpenings.size})",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryGreen
                                    )
                                }
                                items(myPostedOpenings, key = { "posted_${it.id}" }) { opening ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { onOpeningClick(opening.id) },
                                        shape = RoundedCornerShape(14.dp),
                                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                                        border = BorderStroke(1.dp, DarkBorder)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(14.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = opening.title,
                                                    style = MaterialTheme.typography.titleSmall,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = "${opening.startupName} • ${opening.stipend}",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = DarkTextSecondary
                                                )
                                            }
                                            IconButton(onClick = { recruitmentViewModel.deleteOpening(opening.id) }) {
                                                Icon(
                                                    Icons.Default.Delete,
                                                    contentDescription = "Delete Opening",
                                                    tint = MaterialTheme.colorScheme.error
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileStatPill(
    title: String,
    count: Int,
    badgeAlert: Boolean = false,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = if (selected) PrimaryGreenContainer else DarkSurface,
        border = BorderStroke(1.dp, if (selected || badgeAlert) PrimaryGreen.copy(alpha = 0.6f) else DarkBorder)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (selected) PrimaryGreen else DarkTextSecondary,
                    modifier = Modifier.size(15.dp)
                )
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (selected) PrimaryGreen else DarkTextPrimary
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = if (selected) PrimaryGreen else DarkTextSecondary,
                maxLines = 1
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ProfileSummaryHeader(
    profile: StudentMatchProfile?,
    onEditProfile: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(1.dp, DarkBorder)
    ) {
        if (profile == null || !profile.profileComplete) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Complete Your Campus Profile",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = DarkTextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Add your name, course, age, year, interests & resume.",
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkTextSecondary
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Button(
                    onClick = onEditProfile,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryGreen,
                        contentColor = Color(0xFF071206)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Create", fontWeight = FontWeight.Bold)
                }
            }
        } else {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ModernAvatar(
                        name = profile.name,
                        role = profile.preferredRole,
                        size = 50.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = profile.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = DarkTextPrimary
                        )
                        Text(
                            text = profile.academicLine.ifBlank { profile.preferredRole.personLabel() },
                            style = MaterialTheme.typography.bodySmall,
                            color = DarkTextSecondary
                        )
                        if (profile.resumeName.isNotBlank()) {
                            Spacer(modifier = Modifier.height(3.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = null,
                                    tint = PrimaryGreen,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Resume: ${profile.resumeName}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PrimaryGreen,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                    OutlinedButton(
                        onClick = onEditProfile,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, DarkBorder),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("Edit", style = MaterialTheme.typography.labelSmall, color = PrimaryGreen)
                    }
                }

                val allInterests = (profile.areasOfInterest + profile.interests).distinct()
                if (allInterests.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        allInterests.take(6).forEach { tag ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = PrimaryGreenContainer,
                                border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.35f))
                            ) {
                                Text(
                                    text = tag,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PrimaryGreen,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
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
private fun HubApplicationCard(
    application: JobApplication,
    onClick: () -> Unit
) {
    val dateStr = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
        .format(Date(application.appliedTimestamp))

    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(1.dp, DarkBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = application.openingTitle,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = DarkTextPrimary
                    )
                    Text(
                        text = application.startupName,
                        style = MaterialTheme.typography.bodySmall,
                        color = PrimaryGreen,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PrimaryGreenContainer
                ) {
                    Text(
                        text = application.status,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGreen,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Applied on $dateStr",
                style = MaterialTheme.typography.labelSmall,
                color = DarkTextSecondary
            )
        }
    }
}

@Composable
private fun InboxMessageCard(
    item: InboxMessageItem,
    onMarkRead: () -> Unit,
    onReply: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var replyText by remember { mutableStateOf("") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                expanded = !expanded
                if (item.isUnread) onMarkRead()
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(1.dp, if (item.isUnread) PrimaryGreen.copy(alpha = 0.6f) else DarkBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Surface(
                        shape = CircleShape,
                        color = if (item.isUnread) PrimaryGreenContainer else DarkBorder,
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.MarkEmailUnread,
                                contentDescription = null,
                                tint = if (item.isUnread) PrimaryGreen else DarkTextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = item.senderName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = DarkTextPrimary
                        )
                        Text(
                            text = item.senderRoleOrStartup,
                            style = MaterialTheme.typography.labelSmall,
                            color = PrimaryGreen
                        )
                    }
                }
                Text(
                    text = item.timeAgo,
                    style = MaterialTheme.typography.labelSmall,
                    color = DarkTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.subject,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = DarkTextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.preview,
                style = MaterialTheme.typography.bodySmall,
                color = DarkTextSecondary
            )

            if (item.replies.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = DarkBorder)
                Spacer(modifier = Modifier.height(6.dp))
                item.replies.forEach { reply ->
                    Text(
                        text = "You: $reply",
                        style = MaterialTheme.typography.bodySmall,
                        color = PrimaryGreen,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = replyText,
                        onValueChange = { replyText = it },
                        placeholder = { Text("Write a reply...") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    IconButton(
                        onClick = {
                            if (replyText.isNotBlank()) {
                                onReply(replyText)
                                replyText = ""
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send Reply",
                            tint = PrimaryGreen
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyHubState(
    title: String,
    subtitle: String
) {
    Box(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = DarkTextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = DarkTextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}
