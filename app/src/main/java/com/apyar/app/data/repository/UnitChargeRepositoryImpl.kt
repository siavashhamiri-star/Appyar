package com.apyar.app.data.repository

import com.apyar.app.data.local.dao.UnitChargeDao
import com.apyar.app.data.mapper.toDomain
import com.apyar.app.data.mapper.toEntity
import com.apyar.app.domain.model.ChargeStatus
import com.apyar.app.domain.model.UnitCharge
import com.apyar.app.domain.repository.UnitChargeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UnitChargeRepositoryImpl(
    private val unitChargeDao: UnitChargeDao
) : UnitChargeRepository {

    override suspend fun insertUnitCharges(charges: List<UnitCharge>) {
        unitChargeDao.insertUnitCharges(charges.map { it.toEntity() })
    }

    override suspend fun updateUnitCharge(charge: UnitCharge) {
        unitChargeDao.updateUnitCharge(charge.toEntity())
    }

    override suspend fun deleteUnitChargesByPeriodId(periodId: String) {
        unitChargeDao.deleteUnitChargesByPeriodId(periodId)
    }

    override suspend fun getUnitChargesByPeriodId(periodId: String): List<UnitCharge> {
        return unitChargeDao.getUnitChargesByPeriodId(periodId).map { it.toDomain() }
    }

    override fun observeUnitChargesByPeriodId(periodId: String): Flow<List<UnitCharge>> {
        return unitChargeDao.observeUnitChargesByPeriodId(periodId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getUnitChargeById(id: String): UnitCharge? {
        return unitChargeDao.getUnitChargeById(id)?.toDomain()
    }

    override suspend fun getUnitChargesByUnitId(unitId: String): List<UnitCharge> {
        return unitChargeDao.getUnitChargesByUnitId(unitId).map { it.toDomain() }
    }

    override suspend fun updatePayment(id: String, paidAmount: Long, status: ChargeStatus) {
        unitChargeDao.updatePayment(id, paidAmount, status.name)
    }

    override suspend fun updateAdjustment(
        id: String,
        adjustmentAmount: Long,
        finalAmount: Long,
        remainingAmount: Long,
        notes: String?
    ) {
        unitChargeDao.updateAdjustment(id, adjustmentAmount, finalAmount, remainingAmount, notes)
    }
}
