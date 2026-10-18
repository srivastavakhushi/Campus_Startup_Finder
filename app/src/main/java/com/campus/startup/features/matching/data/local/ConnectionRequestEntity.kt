package com.campus.startup.features.matching.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "connection_requests")
data class ConnectionRequestEntity(
    @PrimaryKey
    val id: String,
    val fromProfileId: String,
    val toProfileId: String,
    val status: String,
    val createdAt: Long = System.currentTimeMillis()
)
