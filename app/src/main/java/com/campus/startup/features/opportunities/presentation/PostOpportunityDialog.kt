package com.campus.startup.features.opportunities.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.campus.startup.features.opportunities.data.model.OpportunityType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostOpportunityDialog(
    onDismiss: () -> Unit,
    onSubmit: (
        title: String,
        organization: String,
        type: OpportunityType,
        domain: String,
        stipend: String,
        location: String,
        deadlineDays: Int,
        description: String
    ) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var organization by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(OpportunityType.STARTUP_ROLE) }
    var domain by remember { mutableStateOf("") }
    var stipend by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("Campus / Remote") }
    var deadlineDaysStr by remember { mutableStateOf("14") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Post New Opportunity",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title (e.g. Lead AI Dev, SIH Hackathon)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = organization,
                    onValueChange = { organization = it },
                    label = { Text("Startup / Club / Org Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = domain,
                    onValueChange = { domain = it },
                    label = { Text("Domain (e.g. Android, AI, Design)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = stipend,
                    onValueChange = { stipend = it },
                    label = { Text("Stipend / Prize Pool (e.g. ₹15,000/mo)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = deadlineDaysStr,
                    onValueChange = { deadlineDaysStr = it.filter { char -> char.isDigit() } },
                    label = { Text("Days until Deadline") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description & Requirements") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && organization.isNotBlank()) {
                        onSubmit(
                            title,
                            organization,
                            selectedType,
                            domain.ifBlank { "General" },
                            stipend.ifBlank { "Unpaid / Volunteer" },
                            location,
                            deadlineDaysStr.toIntOrNull() ?: 7,
                            description.ifBlank { "Exciting opportunity to build real campus projects." }
                        )
                    }
                },
                enabled = title.isNotBlank() && organization.isNotBlank()
            ) {
                Text("Publish Opportunity")
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
