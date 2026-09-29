package com.campus.startup.features.recruitment.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "startup_openings")
data class StartupOpeningEntity(
    @PrimaryKey
    val id: String,
    val startupName: String,
    val startupLogo: String,
    val startupStage: String,
    val title: String,
    val roleCategoryName: String,
    val skillsCsv: String,
    val workType: String,
    val commitment: String,
    val stipend: String,
    val location: String,
    val deadlineDays: Int,
    val description: String,
    val responsibilitiesCsv: String,
    val perksCsv: String,
    val founderName: String,
    val founderEmail: String,
    val isSaved: Boolean = false,
    val isPostedByMe: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
