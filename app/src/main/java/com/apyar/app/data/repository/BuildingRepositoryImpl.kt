package com.apyar.app.data.repository

import com.apyar.app.data.local.dao.BuildingDao
import com.apyar.app.data.mapper.toDomain
import com.apyar.app.data.mapper.toEntity
import com.apyar.app.domain.model.Building
import com.apyar.app.domain.repository.BuildingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BuildingRepositoryImpl(
    private val buildingDao: BuildingDao
) : BuildingRepository {

    override fun getBuildings(onlyActive: Boolean): Flow<List<Building>> {
        return buildingDao.getBuildings(onlyActive).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getBuildingById(id: String): Building? {
        return buildingDao.getBuildingById(id)?.toDomain()
    }

    override suspend fun insertBuilding(building: Building): Long {
        return buildingDao.insertBuilding(building.toEntity())
    }

    override suspend fun updateBuilding(building: Building) {
        buildingDao.updateBuilding(building.toEntity())
    }

    override suspend fun setBuildingActive(id: String, isActive: Boolean) {
        buildingDao.setBuildingActive(id, isActive)
    }
}
