package com.campus.startup.features.recruitment.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.campus.startup.features.recruitment.data.model.StartupOpening

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplyJobDialog(
    opening: StartupOpening,
    onDismiss: () -> Unit,
    onSubmit: (
        name: String,
        email: String,
        phone: String,
        branchYear: String,
        portfolioUrl: String,
        matchedSkills: List<String>,
        pitchNote: String
    ) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var branchYear by remember { mutableStateOf("") }
    var portfolioUrl by remember { mutableStateOf("") }
    var pitchNote by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "Apply to ${opening.startupName}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Role: ${opening.title}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
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
                // Info banner
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                ) {
                    Text(
                        text = "Your profile & pitch will be directly sent to founder ${opening.founderName}.",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(10.dp)
                    )
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Your Full Name *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = branchYear,
                    onValueChange = { branchYear = it },
                    label = { Text("Department & Year *") },
                    placeholder = { Text("e.g. CSE, 3rd Year") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("College / Personal Email *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("WhatsApp / Phone Number") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = portfolioUrl,
                    onValueChange = { portfolioUrl = it },
                    label = { Text("GitHub / Portfolio / LinkedIn Link *") },
                    placeholder = { Text("https://github.com/yourhandle") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = pitchNote,
                    onValueChange = { pitchNote = it },
                    label = { Text("Why are you a great fit? (Pitch note)") },
                    placeholder = { Text("Mention projects you've built or relevant skills you're excited to apply...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && email.isNotBlank() && branchYear.isNotBlank()) {
                        onSubmit(
                            name,
                            email,
                            phone,
                            branchYear,
                            portfolioUrl,
                            opening.matchedSkills.ifEmpty { opening.requiredSkills.take(2) },
                            pitchNote.ifBlank { "Excited to contribute to ${opening.startupName} and build real impact." }
                        )
                    }
                },
                enabled = name.isNotBlank() && email.isNotBlank() && branchYear.isNotBlank()
            ) {
                Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Submit Application")
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
