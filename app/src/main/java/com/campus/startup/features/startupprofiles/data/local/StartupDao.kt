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

    @Query("SELECT * FROM startups WHERE id = :id")
    fun observeById(id: String): Flow<StartupEntity?>

    @Query("SELECT * FROM startups WHERE id = :id")
    suspend fun getById(id: String): StartupEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(startup: StartupEntity)

    @Query("DELETE FROM startups WHERE id = :id")
    suspend fun deleteById(id: String)
}
