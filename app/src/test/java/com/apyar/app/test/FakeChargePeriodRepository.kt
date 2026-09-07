package com.apyar.app.test

import com.apyar.app.domain.model.ChargePeriod
import com.apyar.app.domain.model.ChargePeriodStatus
import com.apyar.app.domain.repository.ChargePeriodRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class FakeChargePeriodRepository : ChargePeriodRepository {
    private val periods = MutableStateFlow<Map<String, ChargePeriod>>(emptyMap())

    override suspend fun insertChargePeriod(period: ChargePeriod) {
        periods.value = periods.value + (period.id to period)
    }

    override suspend fun updateChargePeriod(period: ChargePeriod) {
        periods.value = periods.value + (period.id to period)
    }

    override suspend fun getChargePeriodById(id: String): ChargePeriod? {
        return periods.value[id]
    }

    override suspend fun getChargePeriodsByBuildingId(buildingId: String): List<ChargePeriod> {
        return periods.value.values.filter { it.buildingId == buildingId }
    }

    override fun observeChargePeriodsByBuildingId(buildingId: String): Flow<List<ChargePeriod>> {
        return periods.asStateFlow().map { map ->
            map.values.filter { it.buildingId == buildingId }
        }
    }

    override suspend fun updateStatus(
        id: String,
        status: ChargePeriodStatus,
        finalizedAt: Long?
    ) {
        val current = periods.value[id] ?: return
        periods.value = periods.value + (id to current.copy(status = status, finalizedAt = finalizedAt))
    }
}
