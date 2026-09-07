package com.apyar.app.test

import com.apyar.app.domain.model.Building
import com.apyar.app.domain.repository.BuildingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeBuildingRepository : BuildingRepository {
    private val buildingsFlow = MutableStateFlow<List<Building>>(emptyList())

    override fun getBuildings(onlyActive: Boolean): Flow<List<Building>> {
        return buildingsFlow.map { list ->
            if (onlyActive) list.filter { it.isActive } else list
        }
    }

    override suspend fun getBuildingById(id: String): Building? {
        return buildingsFlow.value.firstOrNull { it.id == id }
    }

    override suspend fun insertBuilding(building: Building): Long {
        buildingsFlow.value = buildingsFlow.value + building
        return 1L
    }

    override suspend fun updateBuilding(building: Building) {
        buildingsFlow.value = buildingsFlow.value.map {
            if (it.id == building.id) building else it
        }
    }

    override suspend fun setBuildingActive(id: String, isActive: Boolean) {
        buildingsFlow.value = buildingsFlow.value.map {
            if (it.id == id) it.copy(isActive = isActive) else it
        }
    }
}
