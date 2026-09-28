package com.campus.startup.features.opportunities.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface OpportunityDao {

    @Query("SELECT * FROM saved_opportunities ORDER BY savedTimestamp DESC")
    fun getAllSavedOpportunities(): Flow<List<OpportunityEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM saved_opportunities WHERE id = :id)")
    fun isOpportunitySaved(id: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveOpportunity(opportunity: OpportunityEntity)

    @Query("DELETE FROM saved_opportunities WHERE id = :id")
    suspend fun removeOpportunity(id: String)
}
