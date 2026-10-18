package com.campus.startup.features.matching.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "student_match_profiles")
data class StudentProfileEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val avatar: String,
    val branch: String,
    val year: String,
    val college: String,
    val skillsCsv: String,
    val interestsCsv: String,
    val areasCsv: String,
    val experience: String,
    val experienceTagsCsv: String,
    val goals: String,
    val preferredRoleName: String,
    val teamPreferenceName: String,
    val isCurrentUser: Boolean,
    val profileComplete: Boolean,
    val updatedAt: Long = System.currentTimeMillis()
)
