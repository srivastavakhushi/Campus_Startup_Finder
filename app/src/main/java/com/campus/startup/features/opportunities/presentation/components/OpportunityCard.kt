package com.campus.startup.features.opportunities.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.campus.startup.core.theme.*
import com.campus.startup.features.opportunities.data.model.Opportunity
import com.campus.startup.features.opportunities.data.model.OpportunityType

@Composable
fun OpportunityCard(
    opportunity: Opportunity,
    onClick: () -> Unit,
    onBookmarkToggle: () -> Unit,
    onLikeToggle: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val (typeBg, typeText) = when (opportunity.type) {
        OpportunityType.HACKATHON -> Pair(HackathonAmberBg, HackathonAmber)
        OpportunityType.STARTUP_ROLE -> Pair(StartupGreenBg, StartupGreen)
        OpportunityType.INTERNSHIP -> Pair(InternshipIndigoBg, InternshipIndigo)
        OpportunityType.GRANT -> Pair(GrantRoseBg, GrantRose)
        OpportunityType.COMPETITION -> Pair(HackathonAmberBg, HackathonAmber)
        OpportunityType.ALL -> Pair(SurfaceVariantLight, PrimaryBlue)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 7.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (opportunity.isBookmarked) PrimaryGreen.copy(alpha = 0.45f) else DarkBorder
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Type Tag & Both Like + Save Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(typeBg)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = opportunity.type.getIcon(),
                            contentDescription = null,
                            tint = typeText,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = opportunity.type.displayName,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = typeText
                        )
                    }
                }

                // Like and Save action pills
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Like Button
                    Surface(
                        modifier = Modifier.clickable(onClick = onLikeToggle),
                        shape = RoundedCornerShape(10.dp),
                        color = if (opportunity.isLiked) Color(0xFF36151E) else DarkSurfaceVariant,
                        border = BorderStroke(
                            1.dp,
                            if (opportunity.isLiked) Color(0xFFF43F5E).copy(alpha = 0.6f) else DarkBorder
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = if (opportunity.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Like Opportunity",
                                tint = if (opportunity.isLiked) Color(0xFFF43F5E) else DarkTextSecondary,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = if (opportunity.isLiked) "Liked (${opportunity.likesCount})" else "Like (${opportunity.likesCount})",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (opportunity.isLiked) Color(0xFFF43F5E) else DarkTextSecondary
                            )
                        }
                    }

                    // Save Button
                    Surface(
                        modifier = Modifier.clickable(onClick = onBookmarkToggle),
                        shape = RoundedCornerShape(10.dp),
                        color = if (opportunity.isBookmarked) PrimaryGreenContainer else DarkSurfaceVariant,
                        border = BorderStroke(
                            1.dp,
                            if (opportunity.isBookmarked) PrimaryGreen.copy(alpha = 0.6f) else DarkBorder
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = if (opportunity.isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                contentDescription = "Save Opportunity",
                                tint = if (opportunity.isBookmarked) PrimaryGreen else DarkTextSecondary,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = if (opportunity.isBookmarked) "Saved" else "Save",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (opportunity.isBookmarked) PrimaryGreen else DarkTextSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Opportunity Title
            Text(
                text = opportunity.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Organization
            Text(
                text = "by ${opportunity.organization}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Description snippet
            Text(
                text = opportunity.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Bottom Badges (Prize/Stipend, Domain, Location, Deadline)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Prize / Stipend Pill
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                ) {
                    Text(
                        text = opportunity.stipendOrPrize,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Deadline
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Timer,
                        contentDescription = "Deadline",
                        tint = if (opportunity.deadlineDays <= 5) GrantRose else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${opportunity.deadlineDays}d left",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = if (opportunity.deadlineDays <= 5) GrantRose else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
