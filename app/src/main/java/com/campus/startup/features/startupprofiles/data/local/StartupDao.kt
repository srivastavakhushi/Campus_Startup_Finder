package com.campus.startup.features.startupprofiles.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.campus.startup.features.startupprofiles.data.model.StartupEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StartupDao {

    @Query("SELECT * FROM startups ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<StartupEntity>>

    @Query("SELECT * FROM startups WHERE isSaved = 1 ORDER BY createdAt DESC")
    fun observeSavedStartups(): Flow<List<StartupEntity>>

    @Query("SELECT * FROM startups WHERE id = :id")
    fun observeById(id: String): Flow<StartupEntity?>

    @Query("SELECT * FROM startups WHERE id = :id")
    suspend fun getById(id: String): StartupEntity?

    @Query("SELECT COUNT(*) FROM startups")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(startup: StartupEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(startups: List<StartupEntity>)

    @Query("UPDATE startups SET isInterested = :isInterested, likesCount = :likesCount WHERE id = :id")
    suspend fun updateInterested(id: String, isInterested: Boolean, likesCount: Int)

    @Query("UPDATE startups SET isSaved = :isSaved WHERE id = :id")
    suspend fun updateSaved(id: String, isSaved: Boolean)

    @Query("UPDATE startups SET isRecruited = :isRecruited, recruitedRole = :role WHERE id = :id")
    suspend fun updateRecruited(id: String, isRecruited: Boolean, role: String)

    @Query("DELETE FROM startups WHERE id = :id")
    suspend fun deleteById(id: String)
}
