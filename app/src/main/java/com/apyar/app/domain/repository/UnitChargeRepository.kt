package com.apyar.app.domain.repository

import com.apyar.app.domain.model.ChargeStatus
import com.apyar.app.domain.model.UnitCharge
import kotlinx.coroutines.flow.Flow

interface UnitChargeRepository {
    suspend fun insertUnitCharges(charges: List<UnitCharge>)
    suspend fun updateUnitCharge(charge: UnitCharge)
    suspend fun deleteUnitChargesByPeriodId(periodId: String)
    suspend fun getUnitChargesByPeriodId(periodId: String): List<UnitCharge>
    fun observeUnitChargesByPeriodId(periodId: String): Flow<List<UnitCharge>>
    suspend fun getUnitChargeById(id: String): UnitCharge?
    suspend fun getUnitChargesByUnitId(unitId: String): List<UnitCharge>
    suspend fun updatePayment(id: String, paidAmount: Long, status: ChargeStatus)
    suspend fun updateAdjustment(id: String, adjustmentAmount: Long, finalAmount: Long, remainingAmount: Long, notes: String?)
}
