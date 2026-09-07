package com.apyar.app.test

import com.apyar.app.domain.model.Unit
import com.apyar.app.domain.repository.UnitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeUnitRepository : UnitRepository {
    private val unitsFlow = MutableStateFlow<List<Unit>>(emptyList())

    override fun getUnitsByBuilding(buildingId: String, onlyActive: Boolean): Flow<List<Unit>> {
        return unitsFlow.map { list ->
            list.filter { it.buildingId == buildingId && (!onlyActive || it.isActive) }
        }
    }

    override suspend fun getUnitById(id: String): Unit? {
        return unitsFlow.value.firstOrNull { it.id == id }
    }

    override suspend fun getUnitByBuildingAndNumber(buildingId: String, unitNumber: String): Unit? {
        return unitsFlow.value.firstOrNull {
            it.buildingId == buildingId && it.unitNumber.equals(unitNumber, ignoreCase = true)
        }
    }

    override suspend fun insertUnit(unit: Unit): Long {
        unitsFlow.value = unitsFlow.value + unit
        return 1L
    }

    override suspend fun updateUnit(unit: Unit) {
        unitsFlow.value = unitsFlow.value.map {
            if (it.id == unit.id) unit else it
        }
    }

    override suspend fun setUnitActive(id: String, isActive: Boolean) {
        unitsFlow.value = unitsFlow.value.map {
            if (it.id == id) it.copy(isActive = isActive) else it
        }
    }
}
