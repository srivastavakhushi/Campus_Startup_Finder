package com.campus.startup.features.startupprofiles.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.campus.startup.features.startupprofiles.data.model.StartupEntity
import com.campus.startup.features.startupprofiles.data.model.StartupOptions
import com.campus.startup.features.startupprofiles.data.model.toCsvList

/** [startupId] == null means "create new"; otherwise edit the existing startup. */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun StartupFormScreen(
    startupId: String?,
    viewModel: StartupProfilesViewModel,
    onNavigateBack: () -> Unit
) {
    var existing by remember { mutableStateOf<StartupEntity?>(null) }
    var name by rememberSaveable { mutableStateOf("") }
    var founderName by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var domain by rememberSaveable { mutableStateOf("") }
    var stage by rememberSaveable { mutableStateOf("") }
    var teamMembers by rememberSaveable { mutableStateOf("") }
    var requirements by rememberSaveable { mutableStateOf("") }
    var collabTypes by remember { mutableStateOf(setOf<String>()) }
    var submitted by remember { mutableStateOf(false) }

    // Pre-fill when editing
    LaunchedEffect(startupId) {
        if (startupId != null) {
            viewModel.getStartup(startupId)?.let { s ->
                existing = s
                name = s.name
                founderName = s.founderName
                description = s.description
                domain = s.domain
                stage = s.stage
                teamMembers = s.teamMembers
                requirements = s.requirements
                collabTypes = s.collaborationTypes.toCsvList().toSet()
            }
        }
    }

    Scaffold(
        contentWindowInsets = NoInsets,
        topBar = {
            TopAppBar(
                windowInsets = NoInsets,
                title = { Text(if (startupId == null) "New startup" else "Edit startup") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Startup name *") },
                isError = submitted && name.isBlank(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = founderName,
                onValueChange = { founderName = it },
                label = { Text("Founder name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("What does your startup do? *") },
                isError = submitted && description.isBlank(),
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            Text("Domain *", style = MaterialTheme.typography.titleSmall)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StartupOptions.domains.forEach { option ->
                    FilterChip(
                        selected = domain == option,
                        onClick = { domain = option },
                        label = { Text(option) }
                    )
                }
            }
            if (submitted && domain.isBlank()) {
                Text("Pick a domain", color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall)
            }

            Text("Development stage *", style = MaterialTheme.typography.titleSmall)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StartupOptions.stages.forEach { option ->
                    FilterChip(
                        selected = stage == option,
                        onClick = { stage = option },
                        label = { Text(option) }
                    )
                }
            }
            if (submitted && stage.isBlank()) {
                Text("Pick a stage", color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall)
            }

            OutlinedTextField(
                value = teamMembers,
                onValueChange = { teamMembers = it },
                label = { Text("Team members") },
                supportingText = { Text("Separate names with commas") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Looking for (type of help)", style = MaterialTheme.typography.titleSmall)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StartupOptions.collaborationTypes.forEach { option ->
                    FilterChip(
                        selected = option in collabTypes,
                        onClick = {
                            collabTypes = if (option in collabTypes) collabTypes - option
                            else collabTypes + option
                        },
                        label = { Text(option) }
                    )
                }
            }

            OutlinedTextField(
                value = requirements,
                onValueChange = { requirements = it },
                label = { Text("Requirements / details") },
                supportingText = { Text("Skills, time commitment, anything else you need") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    submitted = true
                    if (name.isNotBlank() && description.isNotBlank() &&
                        domain.isNotBlank() && stage.isNotBlank()
                    ) {
                        val base = existing
                        val entity = StartupEntity(
                            id = base?.id ?: java.util.UUID.randomUUID().toString(),
                            name = name.trim(),
                            domain = domain,
                            stage = stage,
                            description = description.trim(),
                            founderName = founderName.trim(),
                            teamMembers = teamMembers.toCsvList().joinToString(", "),
                            collaborationTypes = StartupOptions.collaborationTypes
                                .filter { it in collabTypes }.joinToString(", "),
                            requirements = requirements.trim(),
                            createdAt = base?.createdAt ?: System.currentTimeMillis()
                        )
                        viewModel.save(entity)
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (startupId == null) "Create startup" else "Save changes")
            }
        }
    }
}
