package com.campus.startup.features.recruitment.data.model

data class JobApplication(
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
    val matchedSkills: List<String>,
    val pitchNote: String,
    val status: String = "Under Review",
    val appliedTimestamp: Long = System.currentTimeMillis()
)
