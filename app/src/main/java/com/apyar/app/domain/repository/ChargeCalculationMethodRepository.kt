package com.apyar.app.domain.repository

import com.apyar.app.domain.model.ChargeCalculationMethod
import kotlinx.coroutines.flow.Flow

interface ChargeCalculationMethodRepository {
    suspend fun insertCalculationMethod(method: ChargeCalculationMethod)
    suspend fun updateCalculationMethod(method: ChargeCalculationMethod)
    suspend fun getCalculationMethodById(id: String): ChargeCalculationMethod?
    suspend fun getCalculationMethodsByBuildingId(buildingId: String): List<ChargeCalculationMethod>
    suspend fun getDefaultCalculationMethod(buildingId: String): ChargeCalculationMethod?
    fun observeCalculationMethodsByBuildingId(buildingId: String): Flow<List<ChargeCalculationMethod>>
}
