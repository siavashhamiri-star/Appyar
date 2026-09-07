package com.apyar.app.data.repository

import com.apyar.app.data.local.dao.UnitDao
import com.apyar.app.data.mapper.toDomain
import com.apyar.app.data.mapper.toEntity
import com.apyar.app.domain.model.Unit
import com.apyar.app.domain.repository.UnitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UnitRepositoryImpl(
    private val unitDao: UnitDao
) : UnitRepository {

    override fun getUnitsByBuilding(buildingId: String, onlyActive: Boolean): Flow<List<Unit>> {
        return unitDao.getUnitsByBuilding(buildingId, onlyActive).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getUnitById(id: String): Unit? {
        return unitDao.getUnitById(id)?.toDomain()
    }

    override suspend fun getUnitByBuildingAndNumber(buildingId: String, unitNumber: String): Unit? {
        return unitDao.getUnitByBuildingAndNumber(buildingId, unitNumber)?.toDomain()
    }

    override suspend fun insertUnit(unit: Unit): Long {
        return unitDao.insertUnit(unit.toEntity())
    }

    override suspend fun updateUnit(unit: Unit) {
        unitDao.updateUnit(unit.toEntity())
    }

    override suspend fun setUnitActive(id: String, isActive: Boolean) {
        unitDao.setUnitActive(id, isActive)
    }
}
