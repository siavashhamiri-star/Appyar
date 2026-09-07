package com.apyar.app.domain.repository

import com.apyar.app.domain.model.ChargeItem
import kotlinx.coroutines.flow.Flow

interface ChargeItemRepository {
    suspend fun insertChargeItems(items: List<ChargeItem>)
    suspend fun getChargeItemsByPeriodId(chargePeriodId: String): List<ChargeItem>
    suspend fun getChargeItemsByBuildingId(buildingId: String): List<ChargeItem>
    suspend fun getChargeItemsByUnitId(unitId: String): List<ChargeItem>
    suspend fun deleteChargeItemsByPeriodId(chargePeriodId: String)
    fun observeChargeItemsByPeriodId(chargePeriodId: String): Flow<List<ChargeItem>>
}
