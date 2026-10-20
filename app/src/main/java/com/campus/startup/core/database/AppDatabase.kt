package com.campus.startup.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.campus.startup.features.opportunities.data.local.OpportunityDao
import com.campus.startup.features.opportunities.data.local.OpportunityEntity
import com.campus.startup.features.recruitment.data.local.JobApplicationEntity
import com.campus.startup.features.recruitment.data.local.RecruitmentDao
import com.campus.startup.features.recruitment.data.local.StartupOpeningEntity
import com.campus.startup.features.startupprofiles.data.local.StartupDao
import com.campus.startup.features.startupprofiles.data.model.StartupEntity

@Database(
    entities = [
        OpportunityEntity::class,
        StartupOpeningEntity::class,
        JobApplicationEntity::class,
        StartupEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun opportunityDao(): OpportunityDao
    abstract fun recruitmentDao(): RecruitmentDao

    abstract fun startupDao(): StartupDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "campus_startup_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
