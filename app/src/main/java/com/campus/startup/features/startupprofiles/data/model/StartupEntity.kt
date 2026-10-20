package com.campus.startup.features.startupprofiles.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "startups")
data class StartupEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val domain: String,
    val stage: String,
    val description: String,
    val founderName: String,
    /** Comma-separated names, e.g. "Asha (Dev), Rohan (Design)" */
    val teamMembers: String = "",
    /** Comma-separated values from StartupOptions.collaborationTypes */
    val collaborationTypes: String = "",
    /** Free-text description of what help/skills the startup needs */
    val requirements: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

fun String.toCsvList(): List<String> =
    split(",").map { it.trim() }.filter { it.isNotEmpty() }

object StartupOptions {
    val domains = listOf(
        "EdTech", "HealthTech", "FinTech", "AgriTech", "E-Commerce",
        "AI / ML", "SaaS", "Social Impact", "Gaming", "Other"
    )
    val stages = listOf("Idea", "Prototype / MVP", "Early Traction", "Growth")
    val collaborationTypes = listOf(
        "Developers", "Designers", "Marketers", "Interns",
        "Mentorship", "Funding", "Co-founder"
    )
}
