package com.campus.startup.features.opportunities.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.campus.startup.core.theme.DarkBorder
import com.campus.startup.core.theme.DarkSurface
import com.campus.startup.core.theme.PrimaryGreen
import com.campus.startup.features.opportunities.data.model.OpportunityType

@Composable
fun CategoryChipRow(
    selectedType: OpportunityType,
    onTypeSelected: (OpportunityType) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OpportunityType.values().forEach { type ->
            val isSelected = (selectedType == type)
            FilterChip(
                selected = isSelected,
                onClick = { onTypeSelected(type) },
                leadingIcon = {
                    Icon(
                        imageVector = type.getIcon(),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (isSelected) Color(0xFF071206) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                label = {
                    Text(
                        text = type.displayName,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                },
                shape = RoundedCornerShape(20.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = PrimaryGreen,
                    selectedLabelColor = Color(0xFF071206),
                    containerColor = DarkSurface,
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                border = if (isSelected) null else BorderStroke(1.dp, DarkBorder)
            )
        }
    }
}
