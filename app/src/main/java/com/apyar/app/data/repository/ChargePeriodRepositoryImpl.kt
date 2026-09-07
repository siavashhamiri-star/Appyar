package com.apyar.app.data.repository

import com.apyar.app.data.local.dao.ChargePeriodDao
import com.apyar.app.data.mapper.toDomain
import com.apyar.app.data.mapper.toEntity
import com.apyar.app.domain.model.ChargePeriod
import com.apyar.app.domain.model.ChargePeriodStatus
import com.apyar.app.domain.repository.ChargePeriodRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ChargePeriodRepositoryImpl(
    private val chargePeriodDao: ChargePeriodDao
) : ChargePeriodRepository {

    override suspend fun insertChargePeriod(period: ChargePeriod) {
        chargePeriodDao.insertChargePeriod(period.toEntity())
    }

    override suspend fun updateChargePeriod(period: ChargePeriod) {
        chargePeriodDao.updateChargePeriod(period.toEntity())
    }

    override suspend fun getChargePeriodById(id: String): ChargePeriod? {
        return chargePeriodDao.getChargePeriodById(id)?.toDomain()
    }

    override suspend fun getChargePeriodsByBuildingId(buildingId: String): List<ChargePeriod> {
        return chargePeriodDao.getChargePeriodsByBuildingId(buildingId).map { it.toDomain() }
    }

    override fun observeChargePeriodsByBuildingId(buildingId: String): Flow<List<ChargePeriod>> {
        return chargePeriodDao.observeChargePeriodsByBuildingId(buildingId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun updateStatus(id: String, status: ChargePeriodStatus, finalizedAt: Long?) {
        chargePeriodDao.updateStatus(id, status.name, finalizedAt)
    }
}
