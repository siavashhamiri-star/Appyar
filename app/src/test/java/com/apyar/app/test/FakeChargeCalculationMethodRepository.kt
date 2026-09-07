package com.apyar.app.test

import com.apyar.app.domain.model.ChargeCalculationMethod
import com.apyar.app.domain.repository.ChargeCalculationMethodRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeChargeCalculationMethodRepository : ChargeCalculationMethodRepository {
    private val methods = MutableStateFlow<List<ChargeCalculationMethod>>(emptyList())

    override suspend fun insertCalculationMethod(method: ChargeCalculationMethod) {
        methods.value = methods.value.filterNot { it.id == method.id } + method
    }

    override suspend fun updateCalculationMethod(method: ChargeCalculationMethod) {
        methods.value = methods.value.map { if (it.id == method.id) method else it }
    }

    override suspend fun getCalculationMethodById(id: String): ChargeCalculationMethod? {
        return methods.value.find { it.id == id }
    }

    override suspend fun getCalculationMethodsByBuildingId(buildingId: String): List<ChargeCalculationMethod> {
        return methods.value.filter { it.buildingId == buildingId }
    }

    override suspend fun getDefaultCalculationMethod(buildingId: String): ChargeCalculationMethod? {
        return methods.value.find { it.buildingId == buildingId && it.isDefault && it.isActive }
    }

    override fun observeCalculationMethodsByBuildingId(buildingId: String): Flow<List<ChargeCalculationMethod>> {
        return methods.map { list -> list.filter { it.buildingId == buildingId } }
    }
}
