package com.apyar.app.domain.repository

import com.apyar.app.domain.model.Unit
import kotlinx.coroutines.flow.Flow

interface UnitRepository {
    fun getUnitsByBuilding(buildingId: String, onlyActive: Boolean = true): Flow<List<Unit>>
    suspend fun getUnitById(id: String): Unit?
    suspend fun getUnitByBuildingAndNumber(buildingId: String, unitNumber: String): Unit?
    suspend fun insertUnit(unit: Unit): Long
    suspend fun updateUnit(unit: Unit)
    suspend fun setUnitActive(id: String, isActive: Boolean)
}
