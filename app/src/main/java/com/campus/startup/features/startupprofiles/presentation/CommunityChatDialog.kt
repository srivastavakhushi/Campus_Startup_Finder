package com.campus.startup.features.startupprofiles.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.campus.startup.core.theme.DarkBorder
import com.campus.startup.core.theme.DarkSurface
import com.campus.startup.core.theme.DarkSurfaceVariant
import com.campus.startup.core.theme.DarkTextPrimary
import com.campus.startup.core.theme.DarkTextSecondary
import com.campus.startup.core.theme.PrimaryBlue
import com.campus.startup.core.theme.PrimaryGreen
import com.campus.startup.core.theme.PrimaryGreenContainer

data class CommunityDiscussionPost(
    val id: String,
    val authorName: String,
    val authorBadge: String,
    val topic: String,
    val message: String,
    val timeAgo: String,
    val isCurrentUser: Boolean = false
)

object CommunityChatStore {
    val topics = listOf("All", "Startups", "Roles & Hiring", "Co-founders", "Professor Q&A")

    val messages = mutableStateListOf(
        CommunityDiscussionPost(
            id = "chat_1",
            authorName = "Rohan Verma",
            authorBadge = "Founder • StudyGenie AI",
            topic = "Roles & Hiring",
            message = "We just opened 2 Android & LLM Intern roles at StudyGenie AI! Drop a message here or apply via Recruitment if you're into Kotlin or Python.",
            timeAgo = "10m ago"
        ),
        CommunityDiscussionPost(
            id = "chat_2",
            authorName = "Dr. Rajesh Sharma",
            authorBadge = "Professor • Incubation Cell",
            topic = "Professor Q&A",
            message = "Reminder for all campus startup teams: Prototype Grant reviews are happening this Friday at 3 PM in the Incubation Lab. Happy to review pitch decks!",
            timeAgo = "25m ago"
        ),
        CommunityDiscussionPost(
            id = "chat_3",
            authorName = "Aman Sharma",
            authorBadge = "Founder • CampusBites",
            topic = "Startups",
            message = "CampusBites crossed 400+ orders across Hostel 4 & 5 last night! Looking for feedback on what late-night snacks we should add next.",
            timeAgo = "1h ago"
        ),
        CommunityDiscussionPost(
            id = "chat_4",
            authorName = "Aisha Rahman",
            authorBadge = "Product Designer • Design '26",
            topic = "Co-founders",
            message = "Anyone building in HealthTech or EdTech for Smart India Hackathon? I can lead UI/UX & Figma prototyping!",
            timeAgo = "2h ago"
        )
    )

    fun addMessage(
        authorName: String,
        authorBadge: String,
        topic: String,
        message: String
    ) {
        val clean = message.trim()
        if (clean.isEmpty()) return
        messages.add(
            0,
            CommunityDiscussionPost(
                id = "chat_${System.currentTimeMillis()}",
                authorName = authorName,
                authorBadge = authorBadge,
                topic = if (topic == "All") "Startups" else topic,
                message = clean,
                timeAgo = "Just now",
                isCurrentUser = true
            )
        )
    }
}

@Composable
fun CommunityChatDialog(
    currentUserName: String,
    currentUserRoleBadge: String,
    onDismiss: () -> Unit
) {
    var selectedTopic by remember { mutableStateOf("All") }
    var draftMessage by remember { mutableStateOf("") }

    val filteredMessages = CommunityChatStore.messages.filter { post ->
        selectedTopic == "All" || post.topic.equals(selectedTopic, ignoreCase = true)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.84f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = BorderStroke(1.5.dp, PrimaryGreen.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Popup Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 18.dp, end = 10.dp, top = 14.dp, bottom = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = PrimaryGreenContainer,
                            border = BorderStroke(1.dp, PrimaryGreen),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Forum,
                                    contentDescription = null,
                                    tint = PrimaryGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Campus Community Chat",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkTextPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = PrimaryGreenContainer
                                ) {
                                    Text(
                                        text = "LIVE",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryGreen,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Discuss roles, startups, hackathons & mentorship",
                                style = MaterialTheme.typography.labelSmall,
                                color = DarkTextSecondary
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Community Chat",
                            tint = DarkTextSecondary
                        )
                    }
                }

                // Topic Filter Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CommunityChatStore.topics.forEach { topic ->
                        FilterChip(
                            selected = selectedTopic == topic,
                            onClick = { selectedTopic = topic },
                            label = {
                                Text(
                                    text = topic,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (selectedTopic == topic) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryGreenContainer,
                                selectedLabelColor = PrimaryGreen
                            )
                        )
                    }
                }

                HorizontalDivider(color = DarkBorder, modifier = Modifier.padding(top = 4.dp))

                // Messages Feed
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredMessages, key = { it.id }) { post ->
                        CommunityMessageBubble(post = post)
                    }
                }

                HorizontalDivider(color = DarkBorder)

                // Composer Bar at Bottom
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Text(
                        text = "Posting as $currentUserName ($currentUserRoleBadge)",
                        style = MaterialTheme.typography.labelSmall,
                        color = PrimaryGreen,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = draftMessage,
                            onValueChange = { draftMessage = it },
                            placeholder = {
                                Text(
                                    "Ask about startup roles, teams, or share an idea...",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.weight(1f)
                        )

                        Surface(
                            shape = CircleShape,
                            color = PrimaryGreen,
                            modifier = Modifier.size(46.dp)
                        ) {
                            IconButton(
                                onClick = {
                                    if (draftMessage.isNotBlank()) {
                                        CommunityChatStore.addMessage(
                                            authorName = currentUserName,
                                            authorBadge = currentUserRoleBadge,
                                            topic = selectedTopic,
                                            message = draftMessage
                                        )
                                        draftMessage = ""
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Send Message",
                                    tint = Color(0xFF071206),
                                    modifier = Modifier.size(20.dp)
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
private fun CommunityMessageBubble(post: CommunityDiscussionPost) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = if (post.isCurrentUser) PrimaryGreenContainer.copy(alpha = 0.65f) else DarkSurfaceVariant,
        border = BorderStroke(
            1.dp,
            if (post.isCurrentUser) PrimaryGreen.copy(alpha = 0.5f) else DarkBorder
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = PrimaryGreenContainer,
                        modifier = Modifier.size(26.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = post.authorName.take(1).uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryGreen
                            )
                        }
                    }
                    Column {
                        Text(
                            text = post.authorName,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = DarkTextPrimary
                        )
                        Text(
                            text = post.authorBadge,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (post.authorBadge.contains("Professor", ignoreCase = true)) PrimaryBlue else PrimaryGreen
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = DarkSurface,
                    border = BorderStroke(1.dp, DarkBorder)
                ) {
                    Text(
                        text = "#${post.topic}",
                        style = MaterialTheme.typography.labelSmall,
                        color = DarkTextSecondary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = post.message,
                style = MaterialTheme.typography.bodySmall,
                color = DarkTextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = post.timeAgo,
                style = MaterialTheme.typography.labelSmall,
                color = DarkTextSecondary,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}
