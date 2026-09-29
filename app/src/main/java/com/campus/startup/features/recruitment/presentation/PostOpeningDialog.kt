package com.campus.startup.features.recruitment.presentation

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.campus.startup.core.theme.PrimaryBlue
import com.campus.startup.features.recruitment.data.model.SkillItem
import com.campus.startup.features.recruitment.data.model.StartupRoleCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostOpeningDialog(
    onDismiss: () -> Unit,
    onSubmit: (
        startupName: String,
        startupLogo: String,
        startupStage: String,
        title: String,
        roleCategory: StartupRoleCategory,
        skills: List<String>,
        workType: String,
        commitment: String,
        stipend: String,
        location: String,
        deadlineDays: Int,
        description: String,
        responsibilities: List<String>,
        perks: List<String>,
        founderName: String,
        founderEmail: String
    ) -> Unit
) {
    var startupName by remember { mutableStateOf("") }
    var startupLogo by remember { mutableStateOf("🚀") }
    var startupStage by remember { mutableStateOf("Incubated at Campus Hub") }
    var title by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(StartupRoleCategory.DEVELOPER) }
    var skillsInput by remember { mutableStateOf("") }
    var workType by remember { mutableStateOf("Hybrid") }
    var commitment by remember { mutableStateOf("Internship") }
    var stipend by remember { mutableStateOf("₹15,000/mo") }
    var location by remember { mutableStateOf("Campus Incubator") }
    var deadlineDaysStr by remember { mutableStateOf("14") }
    var description by remember { mutableStateOf("") }
    var responsibilitiesInput by remember { mutableStateOf("") }
    var perksInput by remember { mutableStateOf("Flexible hours during exams, Certificate & PPO Opportunity") }
    var founderName by remember { mutableStateOf("") }
    var founderEmail by remember { mutableStateOf("") }

    val emojis = listOf("🚀", "⚡", "💻", "🎨", "📢", "🍔", "🤖", "🌱", "📱", "🎯")
    val workTypes = listOf("Remote", "Hybrid", "On-Campus")
    val commitments = listOf("Internship", "Part-Time", "Full-Time")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "Post Startup Opening",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Recruit developers, designers, marketers & interns",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Startup Name
                OutlinedTextField(
                    value = startupName,
                    onValueChange = { startupName = it },
                    label = { Text("Startup Name *") },
                    placeholder = { Text("e.g. EduAI Labs, QuickBite") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Logo selector
                Text(text = "Choose Startup Badge / Emoji:", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    emojis.forEach { emoji ->
                        val isSelected = startupLogo == emoji
                        FilledTonalButton(
                            onClick = { startupLogo = emoji },
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = if (isSelected) PrimaryBlue else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(text = emoji, style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }

                // Startup Stage
                OutlinedTextField(
                    value = startupStage,
                    onValueChange = { startupStage = it },
                    label = { Text("Startup Stage") },
                    placeholder = { Text("e.g. Pre-Seed Funded, E-Cell Backed, Student MVP") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Role Category
                Text(text = "Role Category *", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    StartupRoleCategory.values().filter { it != StartupRoleCategory.ALL }.forEach { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = { Text("${category.emoji} ${category.displayName}") }
                        )
                    }
                }

                // Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Position Title *") },
                    placeholder = { Text("e.g. Frontend Dev (React), UI/UX Designer") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Required Skills
                OutlinedTextField(
                    value = skillsInput,
                    onValueChange = { skillsInput = it },
                    label = { Text("Required Skills (comma separated) *") },
                    placeholder = { Text("e.g. Kotlin, Compose, Room, Firebase") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Quick skill chips for selected category
                Text(text = "Suggested Skills:", style = MaterialTheme.typography.labelSmall)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    SkillItem.ALL_SKILLS.filter { it.category == selectedCategory }.take(6).forEach { skill ->
                        SuggestionChip(
                            onClick = {
                                val current = skillsInput.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toMutableList()
                                if (!current.contains(skill.name)) {
                                    current.add(skill.name)
                                    skillsInput = current.joinToString(", ")
                                }
                            },
                            label = { Text("+ ${skill.name}", style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                // Work Type & Commitment
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Format:", style = MaterialTheme.typography.labelSmall)
                        Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                            workTypes.forEach { wt ->
                                FilterChip(
                                    selected = workType == wt,
                                    onClick = { workType = wt },
                                    label = { Text(wt, style = MaterialTheme.typography.labelSmall) },
                                    modifier = Modifier.padding(end = 4.dp)
                                )
                            }
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Commitment:", style = MaterialTheme.typography.labelSmall)
                        Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                            commitments.forEach { cm ->
                                FilterChip(
                                    selected = commitment == cm,
                                    onClick = { commitment = cm },
                                    label = { Text(cm, style = MaterialTheme.typography.labelSmall) },
                                    modifier = Modifier.padding(end = 4.dp)
                                )
                            }
                        }
                    }
                }

                // Compensation & Location
                OutlinedTextField(
                    value = stipend,
                    onValueChange = { stipend = it },
                    label = { Text("Compensation / Stipend *") },
                    placeholder = { Text("e.g. ₹15,000/mo, ₹20,000 + Equity") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = deadlineDaysStr,
                    onValueChange = { deadlineDaysStr = it.filter { c -> c.isDigit() } },
                    label = { Text("Application Deadline (days from now)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Description & Responsibilities
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Role Description *") },
                    placeholder = { Text("Explain what your startup does and why this role is exciting...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                OutlinedTextField(
                    value = responsibilitiesInput,
                    onValueChange = { responsibilitiesInput = it },
                    label = { Text("Key Responsibilities (line separated)") },
                    placeholder = { Text("Design mobile screens\nCollaborate with devs\nRun user tests") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                OutlinedTextField(
                    value = perksInput,
                    onValueChange = { perksInput = it },
                    label = { Text("Perks & Benefits (comma separated)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Founder details
                OutlinedTextField(
                    value = founderName,
                    onValueChange = { founderName = it },
                    label = { Text("Founder Name & Branch") },
                    placeholder = { Text("e.g. Aryan Sharma (Final Year CSE)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = founderEmail,
                    onValueChange = { founderEmail = it },
                    label = { Text("Founder Contact Email") },
                    placeholder = { Text("founder@startup.campus.edu") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (startupName.isNotBlank() && title.isNotBlank() && description.isNotBlank()) {
                        val skillsList = skillsInput.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                        val respList = responsibilitiesInput.split("\n").map { it.trim() }.filter { it.isNotEmpty() }
                        val perksList = perksInput.split(",").map { it.trim() }.filter { it.isNotEmpty() }

                        onSubmit(
                            startupName,
                            startupLogo,
                            startupStage,
                            title,
                            selectedCategory,
                            if (skillsList.isEmpty()) listOf(selectedCategory.displayName) else skillsList,
                            workType,
                            commitment,
                            stipend.ifBlank { "Unpaid / Volunteer" },
                            location.ifBlank { "Campus" },
                            deadlineDaysStr.toIntOrNull() ?: 14,
                            description,
                            if (respList.isEmpty()) listOf("Build awesome features with the startup team") else respList,
                            if (perksList.isEmpty()) listOf("Internship certificate", "Startup mentorship") else perksList,
                            founderName.ifBlank { "Campus Founder" },
                            founderEmail.ifBlank { "contact@campusstartup.edu" }
                        )
                    }
                },
                enabled = startupName.isNotBlank() && title.isNotBlank() && description.isNotBlank()
            ) {
                Text("Publish Opening 🚀")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}
