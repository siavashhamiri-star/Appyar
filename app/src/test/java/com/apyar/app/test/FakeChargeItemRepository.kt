package com.apyar.app.test

import com.apyar.app.domain.model.ChargeItem
import com.apyar.app.domain.repository.ChargeItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeChargeItemRepository : ChargeItemRepository {
    private val items = MutableStateFlow<List<ChargeItem>>(emptyList())

    override suspend fun insertChargeItems(newItems: List<ChargeItem>) {
        val newIds = newItems.map { it.id }.toSet()
        items.value = items.value.filterNot { it.id in newIds } + newItems
    }

    override suspend fun getChargeItemsByPeriodId(chargePeriodId: String): List<ChargeItem> {
        return items.value.filter { it.chargePeriodId == chargePeriodId }
    }

    override suspend fun getChargeItemsByBuildingId(buildingId: String): List<ChargeItem> {
        return items.value.filter { it.buildingId == buildingId }
    }

    override suspend fun getChargeItemsByUnitId(unitId: String): List<ChargeItem> {
        return items.value.filter { it.unitId == unitId }
    }

    override suspend fun deleteChargeItemsByPeriodId(chargePeriodId: String) {
        items.value = items.value.filterNot { it.chargePeriodId == chargePeriodId }
    }

    override fun observeChargeItemsByPeriodId(chargePeriodId: String): Flow<List<ChargeItem>> {
        return items.map { list -> list.filter { it.chargePeriodId == chargePeriodId } }
    }
}
