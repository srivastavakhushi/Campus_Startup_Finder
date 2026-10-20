package com.campus.startup.features.matching.presentation

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.campus.startup.core.theme.DarkBorder
import com.campus.startup.core.theme.DarkSurface
import com.campus.startup.core.theme.DarkTextPrimary
import com.campus.startup.core.theme.DarkTextSecondary
import com.campus.startup.core.theme.PrimaryBlue
import com.campus.startup.core.theme.PrimaryGreen
import com.campus.startup.core.theme.PrimaryGreenContainer
import com.campus.startup.features.matching.data.model.MatchCatalog
import com.campus.startup.features.matching.data.model.StudentMatchProfile
import com.campus.startup.features.matching.data.model.TeamPreference
import com.campus.startup.features.matching.data.model.personLabel
import com.campus.startup.features.recruitment.data.model.SkillItem
import com.campus.startup.features.recruitment.data.model.StartupRoleCategory
import com.campus.startup.features.startupprofiles.data.model.StartupEntity

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditMatchProfileScreen(
    viewModel: MatchingViewModel,
    availableStartups: List<StartupEntity> = emptyList(),
    onNavigateBack: () -> Unit,
    onSaved: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val errors by viewModel.formErrors.collectAsState()
    val me = (uiState as? MatchingUiState.Success)?.me

    var ready by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var avatar by remember { mutableStateOf("DEV") }
    var course by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var college by remember { mutableStateOf("") }
    var resumeUri by remember { mutableStateOf("") }
    var resumeName by remember { mutableStateOf("") }
    var recruitedIds by remember { mutableStateOf(setOf<String>()) }
    var skills by remember { mutableStateOf(setOf<String>()) }
    var interests by remember { mutableStateOf(setOf<String>()) }
    var areas by remember { mutableStateOf(setOf<String>()) }
    var experience by remember { mutableStateOf("") }
    var experienceTags by remember { mutableStateOf(setOf<String>()) }
    var goals by remember { mutableStateOf("") }
    var role by remember { mutableStateOf(StartupRoleCategory.DEVELOPER) }
    var team by remember { mutableStateOf(TeamPreference.FOUNDING) }

    val resumePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            resumeUri = uri.toString()
            var displayName = "Uploaded_Resume.pdf"
            runCatching {
                context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        if (idx >= 0) {
                            displayName = cursor.getString(idx) ?: displayName
                        }
                    }
                }
            }
            resumeName = displayName
        }
    }

    LaunchedEffect(me?.id, uiState is MatchingUiState.Success) {
        if (!ready && me != null) {
            name = me.name
            avatar = me.avatar.ifBlank { "DEV" }
            course = me.course.ifBlank { me.branch }
            age = me.age
            year = me.year
            college = me.college
            resumeUri = me.resumeUri
            resumeName = me.resumeName
            recruitedIds = if (me.recruitedStartupIds.isNotEmpty()) {
                me.recruitedStartupIds.toSet()
            } else {
                availableStartups.filter { it.isRecruited }.map { it.id }.toSet()
            }
            skills = me.skills.toSet()
            interests = me.interests.toSet()
            areas = me.areasOfInterest.toSet()
            experience = me.experience
            experienceTags = me.experienceTags.toSet()
            goals = me.goals
            role = if (me.preferredRole == StartupRoleCategory.ALL) StartupRoleCategory.DEVELOPER else me.preferredRole
            team = me.teamPreference
            ready = true
            viewModel.clearFormErrors()
        } else if (!ready && uiState is MatchingUiState.Success) {
            ready = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (me?.profileComplete == true) "Edit Campus Profile" else "Create Your Profile",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Personalizes your startup recommendations & co-founder matches",
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
                actions = {
                    if (me?.profileComplete != true) {
                        TextButton(
                            onClick = {
                                name = "Khushi Sharma"
                                course = "B.Tech CSE"
                                age = "20"
                                year = "3rd Year"
                                college = "Campus Institute of Technology"
                                resumeName = "Khushi_Sharma_Resume.pdf"
                                resumeUri = "content://campus.startup/resume/Khushi_Sharma_Resume.pdf"
                                areas = setOf("AI / ML", "EdTech", "FinTech")
                                interests = setOf("Artificial Intelligence", "Product Design", "Open Source")
                                skills = setOf("Kotlin", "Jetpack Compose", "Python", "Figma")
                                recruitedIds = availableStartups.filter { it.id == "startup_studygenie" }.map { it.id }.toSet()
                                    .ifEmpty { setOf("startup_studygenie") }
                                experience = "Built an Android campus assistant and won 2nd place at Smart Campus Hackathon."
                                experienceTags = setOf("Hackathon", "Shipped a product")
                                goals = "Looking to join and build high-impact AI and EdTech products for students."
                                role = StartupRoleCategory.DEVELOPER
                            }
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp), tint = PrimaryGreen)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Demo Fill", color = PrimaryGreen, fontWeight = FontWeight.Bold)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            Surface(color = MaterialTheme.colorScheme.surface, tonalElevation = 8.dp, shadowElevation = 8.dp) {
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Button(
                        onClick = {
                            val effectiveGoals = goals.ifBlank {
                                "Interested in ${areas.joinToString(", ").ifBlank { "campus startups" }} and building impactful student products."
                            }
                            val effectiveSkills = if (skills.isEmpty()) listOf("Kotlin", "Problem Solving") else ordered(skills, SkillItem.ALL_SKILLS.map { it.name })
                            val effectiveInterests = if (interests.isEmpty() && areas.isNotEmpty()) {
                                listOf("Artificial Intelligence")
                            } else {
                                ordered(interests, MatchCatalog.INTERESTS)
                            }
                            val draft = StudentMatchProfile(
                                id = MatchCatalog.CURRENT_USER_ID,
                                name = name,
                                avatar = avatar,
                                branch = course,
                                course = course,
                                age = age,
                                year = year,
                                college = college,
                                resumeUri = resumeUri,
                                resumeName = resumeName,
                                recruitedStartupIds = recruitedIds.toList(),
                                skills = effectiveSkills,
                                interests = effectiveInterests,
                                areasOfInterest = ordered(areas, MatchCatalog.AREAS),
                                experience = experience,
                                experienceTags = ordered(experienceTags, MatchCatalog.EXPERIENCE_TAGS),
                                goals = effectiveGoals,
                                preferredRole = role,
                                teamPreference = team,
                                isCurrentUser = true
                            )
                            if (viewModel.saveProfile(draft)) onSaved()
                        },
                        enabled = ready,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryGreen,
                            contentColor = Color(0xFF071206)
                        )
                    ) {
                        Text(
                            text = if (me?.profileComplete == true) "Save Profile Changes" else "Create Profile & View Recommendations",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    ) { padding ->
        if (!ready && uiState is MatchingUiState.Loading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryGreen)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Tell us about yourself, your course, age, year, interests, and attach your resume to unlock tailored startup recommendations.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // 1. Basic Info: Name, Age, Course, Year
                SectionLabel("Basic Details *")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Full Name *") },
                        placeholder = { Text("e.g. Khushi Sharma") },
                        isError = errors.name != null,
                        supportingText = errors.name?.let { { Text(it) } },
                        modifier = Modifier.weight(1.8f),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp)
                    )
                    OutlinedTextField(
                        value = age,
                        onValueChange = { input -> if (input.all { it.isDigit() } && input.length <= 2) age = input },
                        label = { Text("Age *") },
                        placeholder = { Text("20") },
                        isError = errors.age != null,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp)
                    )
                }

                OutlinedTextField(
                    value = course,
                    onValueChange = { course = it },
                    label = { Text("Course / Branch *") },
                    placeholder = { Text("e.g. B.Tech CSE, B.Des, MBA") },
                    isError = errors.academic != null && course.trim().length < 2,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp)
                )
                ChoiceRow(
                    labels = MatchCatalog.COURSES,
                    selected = course,
                    onSelected = { course = it }
                )

                SectionLabel("Academic Year *")
                ChoiceRow(
                    labels = MatchCatalog.YEARS,
                    selected = year,
                    onSelected = { year = it }
                )
                errors.academic?.let { message ->
                    Text(text = message, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
                }

                OutlinedTextField(
                    value = college,
                    onValueChange = { college = it },
                    label = { Text("College / University") },
                    placeholder = { Text("e.g. Campus Institute of Technology") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp)
                )

                // 2. Resume Upload Section
                SectionLabel("Resume / CV")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = BorderStroke(1.dp, if (resumeName.isNotBlank()) PrimaryGreen.copy(alpha = 0.5f) else DarkBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (resumeName.isNotBlank()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = PrimaryGreenContainer,
                                        modifier = Modifier.size(40.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.Description,
                                                contentDescription = null,
                                                tint = PrimaryGreen
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = resumeName,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = DarkTextPrimary
                                        )
                                        Text(
                                            text = "Attached to your campus profile",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = PrimaryGreen
                                        )
                                    }
                                }
                                IconButton(onClick = {
                                    resumeName = ""
                                    resumeUri = ""
                                }) {
                                    Icon(Icons.Default.Close, contentDescription = "Remove resume", tint = DarkTextSecondary)
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        } else {
                            Text(
                                text = "Attach your resume (PDF or DOC) so founders can review it when you apply or connect.",
                                style = MaterialTheme.typography.bodySmall,
                                color = DarkTextSecondary
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    resumePickerLauncher.launch(
                                        arrayOf("application/pdf", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "*/*")
                                    )
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.6f))
                            ) {
                                Icon(Icons.Default.UploadFile, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (resumeName.isBlank()) "Upload File" else "Change File", color = PrimaryGreen)
                            }
                            if (resumeName.isBlank()) {
                                OutlinedButton(
                                    onClick = {
                                        val cleanName = name.trim().ifBlank { "Student" }.replace(" ", "_")
                                        resumeName = "${cleanName}_Resume.pdf"
                                        resumeUri = "content://campus.startup/resume/$resumeName"
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, DarkBorder)
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = DarkTextPrimary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Use Sample PDF", color = DarkTextPrimary)
                                }
                            }
                        }
                    }
                }

                // 3. Startup Domains & Interests
                SectionLabel("Startup Domains You're Interested In *")
                Text(
                    text = "We'll recommend startups on your Home page that match these domains.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MatchCatalog.AREAS.forEach { domain ->
                        val isSelected = areas.any { it.equals(domain, ignoreCase = true) }
                        FilterChip(
                            selected = isSelected,
                            onClick = { areas = areas.toggle(domain) },
                            label = { Text(domain) },
                            shape = RoundedCornerShape(12.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryGreenContainer,
                                selectedLabelColor = PrimaryGreen
                            )
                        )
                    }
                }
                errors.areas?.let { message ->
                    Text(text = message, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
                }

                SectionLabel("Topics & Personal Interests *")
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MatchCatalog.INTERESTS.forEach { item ->
                        val isSelected = interests.any { it.equals(item, ignoreCase = true) }
                        FilterChip(
                            selected = isSelected,
                            onClick = { interests = interests.toggle(item) },
                            label = { Text(item) },
                            shape = RoundedCornerShape(12.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryBlue.copy(alpha = 0.25f),
                                selectedLabelColor = PrimaryBlue
                            )
                        )
                    }
                }
                errors.interests?.let { message ->
                    Text(text = message, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
                }

                // 4. Currently Recruited Startups
                if (availableStartups.isNotEmpty()) {
                    SectionLabel("Currently Recruited In (Optional)")
                    Text(
                        text = "If you're currently working or recruited in any campus startup, select it below to pin it at the top of your Home page.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        availableStartups.forEach { startup ->
                            val isSelected = startup.id in recruitedIds
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    recruitedIds = if (isSelected) recruitedIds - startup.id else recruitedIds + startup.id
                                },
                                label = { Text(startup.name) },
                                shape = RoundedCornerShape(12.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PrimaryGreenContainer,
                                    selectedLabelColor = PrimaryGreen
                                )
                            )
                        }
                    }
                }

                // 5. Skills & Role
                SectionLabel("Your Skills *")
                SkillItem.ALL_SKILLS.groupBy { it.category }.forEach { (category, items) ->
                    Text(
                        text = category.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    ToggleRow(
                        labels = items.map { it.name },
                        selected = skills,
                        onToggle = { skill -> skills = skills.toggle(skill) }
                    )
                }
                errors.skills?.let { message ->
                    Text(text = message, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
                }

                SectionLabel("Preferred Role *")
                ChoiceRow(
                    labels = MatchCatalog.ROLES.map { it.personLabel() },
                    selected = role.personLabel(),
                    onSelected = { label ->
                        role = MatchCatalog.ROLES.first { it.personLabel() == label }
                    }
                )

                SectionLabel("Profile Badge Tag")
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MatchCatalog.AVATARS.forEach { badge ->
                        FilterChip(
                            selected = avatar == badge,
                            onClick = { avatar = badge },
                            label = { Text(badge, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium) },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                OutlinedTextField(
                    value = goals,
                    onValueChange = { goals = it },
                    label = { Text("Bio / Entrepreneurial Goals *") },
                    placeholder = { Text("What kind of startups or projects do you want to build?") },
                    isError = errors.goals != null,
                    supportingText = errors.goals?.let { { Text(it) } },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    shape = RoundedCornerShape(14.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text = text, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
}

@Composable
private fun ChoiceRow(
    labels: List<String>,
    selected: String,
    onSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        labels.forEach { label ->
            FilterChip(
                selected = label.equals(selected, ignoreCase = true),
                onClick = { onSelected(label) },
                label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                shape = RoundedCornerShape(12.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = PrimaryBlue,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}

@Composable
private fun ToggleRow(
    labels: List<String>,
    selected: Set<String>,
    onToggle: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        labels.forEach { label ->
            val isSelected = selected.any { it.equals(label, ignoreCase = true) }
            FilterChip(
                selected = isSelected,
                onClick = { onToggle(label) },
                label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                shape = RoundedCornerShape(12.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = PrimaryBlue.copy(alpha = 0.9f),
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}

private fun ordered(selected: Set<String>, catalog: List<String>): List<String> {
    val inCatalog = catalog.filter { item -> selected.any { it.equals(item, ignoreCase = true) } }
    val extras = selected.filter { sel -> catalog.none { it.equals(sel, ignoreCase = true) } }
    return inCatalog + extras
}

private fun Set<String>.toggle(value: String): Set<String> {
    return if (any { it.equals(value, ignoreCase = true) }) {
        filterNot { it.equals(value, ignoreCase = true) }.toSet()
    } else {
        this + value
    }
}
