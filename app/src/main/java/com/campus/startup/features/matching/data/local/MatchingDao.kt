package com.campus.startup.features.matching.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchingDao {

    @Query("SELECT * FROM student_match_profiles")
    fun observeProfiles(): Flow<List<StudentProfileEntity>>

    @Query("SELECT COUNT(*) FROM student_match_profiles")
    suspend fun profileCount(): Int

    @Query("SELECT * FROM student_match_profiles WHERE id = :id LIMIT 1")
    suspend fun getProfile(id: String): StudentProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: StudentProfileEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfiles(profiles: List<StudentProfileEntity>)

    @Query("SELECT * FROM connection_requests ORDER BY createdAt DESC")
    fun observeConnections(): Flow<List<ConnectionRequestEntity>>

    @Query("SELECT COUNT(*) FROM connection_requests")
    suspend fun connectionCount(): Int

    @Query(
        "SELECT * FROM connection_requests WHERE " +
            "(fromProfileId = :firstId AND toProfileId = :secondId) OR " +
            "(fromProfileId = :secondId AND toProfileId = :firstId) LIMIT 1"
    )
    suspend fun findConnection(firstId: String, secondId: String): ConnectionRequestEntity?

    @Query("SELECT * FROM connection_requests WHERE id = :id LIMIT 1")
    suspend fun getConnection(id: String): ConnectionRequestEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConnection(request: ConnectionRequestEntity)

    @Query("UPDATE connection_requests SET status = :status WHERE id = :id")
    suspend fun updateConnectionStatus(id: String, status: String)
}
