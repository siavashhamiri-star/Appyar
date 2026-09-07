package com.apyar.app.domain.repository

import com.apyar.app.domain.model.ChargePeriod
import com.apyar.app.domain.model.ChargePeriodStatus
import kotlinx.coroutines.flow.Flow

interface ChargePeriodRepository {
    suspend fun insertChargePeriod(period: ChargePeriod)
    suspend fun updateChargePeriod(period: ChargePeriod)
    suspend fun getChargePeriodById(id: String): ChargePeriod?
    suspend fun getChargePeriodsByBuildingId(buildingId: String): List<ChargePeriod>
    fun observeChargePeriodsByBuildingId(buildingId: String): Flow<List<ChargePeriod>>
    suspend fun updateStatus(id: String, status: ChargePeriodStatus, finalizedAt: Long? = null)
}
