package com.campus.startup.features.opportunities.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_opportunities")
data class OpportunityEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val organization: String,
    val typeName: String,
    val domain: String,
    val stipendOrPrize: String,
    val location: String,
    val deadlineDays: Int,
    val description: String,
    val savedTimestamp: Long = System.currentTimeMillis()
)
