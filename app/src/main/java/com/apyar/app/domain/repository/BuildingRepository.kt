package com.apyar.app.domain.repository

import com.apyar.app.domain.model.Building
import kotlinx.coroutines.flow.Flow

interface BuildingRepository {
    fun getBuildings(onlyActive: Boolean = true): Flow<List<Building>>
    suspend fun getBuildingById(id: String): Building?
    suspend fun insertBuilding(building: Building): Long
    suspend fun updateBuilding(building: Building)
    suspend fun setBuildingActive(id: String, isActive: Boolean)
}
