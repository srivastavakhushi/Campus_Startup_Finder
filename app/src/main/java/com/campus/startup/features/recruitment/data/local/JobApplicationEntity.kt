package com.campus.startup.features.recruitment.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "job_applications")
data class JobApplicationEntity(
    @PrimaryKey
    val id: String,
    val openingId: String,
    val openingTitle: String,
    val startupName: String,
    val roleCategory: String,
    val applicantName: String,
    val applicantEmail: String,
    val applicantPhone: String,
    val applicantBranchYear: String,
    val portfolioUrl: String,
    val matchedSkillsCsv: String,
    val pitchNote: String,
    val status: String = "Under Review",
    val appliedTimestamp: Long = System.currentTimeMillis()
)
