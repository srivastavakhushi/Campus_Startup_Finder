package com.campus.startup.features.startupprofiles.data.repository

import com.campus.startup.features.startupprofiles.data.local.StartupDao
import com.campus.startup.features.startupprofiles.data.model.StartupEntity
import kotlinx.coroutines.flow.Flow

class StartupRepository(private val dao: StartupDao) {
    fun observeAll(): Flow<List<StartupEntity>> = dao.observeAll()
    fun observeById(id: String): Flow<StartupEntity?> = dao.observeById(id)
    suspend fun getById(id: String): StartupEntity? = dao.getById(id)
    suspend fun save(startup: StartupEntity) = dao.upsert(startup)
    suspend fun delete(id: String) = dao.deleteById(id)
}
