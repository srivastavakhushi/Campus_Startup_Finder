package com.campus.startup.features.recruitment.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RecruitmentDao {

    @Query("SELECT * FROM startup_openings ORDER BY createdAt DESC")
    fun getAllOpenings(): Flow<List<StartupOpeningEntity>>

    @Query("SELECT * FROM startup_openings WHERE isSaved = 1 ORDER BY createdAt DESC")
    fun getSavedOpenings(): Flow<List<StartupOpeningEntity>>

    @Query("SELECT * FROM startup_openings WHERE isPostedByMe = 1 ORDER BY createdAt DESC")
    fun getMyPostedOpenings(): Flow<List<StartupOpeningEntity>>

    @Query("SELECT * FROM startup_openings WHERE id = :id LIMIT 1")
    suspend fun getOpeningById(id: String): StartupOpeningEntity?

    @Query("SELECT COUNT(*) FROM startup_openings")
    suspend fun getOpeningCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOpening(opening: StartupOpeningEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOpenings(openings: List<StartupOpeningEntity>)

    @Query("UPDATE startup_openings SET isSaved = :isSaved WHERE id = :id")
    suspend fun updateSavedStatus(id: String, isSaved: Boolean)

    @Query("DELETE FROM startup_openings WHERE id = :id")
    suspend fun deleteOpening(id: String)

    // Applications
    @Query("SELECT * FROM job_applications ORDER BY appliedTimestamp DESC")
    fun getAllApplications(): Flow<List<JobApplicationEntity>>

    @Query("SELECT * FROM job_applications WHERE openingId = :openingId ORDER BY appliedTimestamp DESC")
    fun getApplicationsForOpening(openingId: String): Flow<List<JobApplicationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApplication(application: JobApplicationEntity)

    @Query("UPDATE job_applications SET status = :status WHERE id = :id")
    suspend fun updateApplicationStatus(id: String, status: String)

    @Query("SELECT EXISTS(SELECT 1 FROM job_applications WHERE openingId = :openingId)")
    fun hasAppliedForOpening(openingId: String): Flow<Boolean>
}
