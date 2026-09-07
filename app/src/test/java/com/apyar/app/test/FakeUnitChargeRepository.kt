package com.apyar.app.test

import com.apyar.app.domain.model.ChargeStatus
import com.apyar.app.domain.model.UnitCharge
import com.apyar.app.domain.repository.UnitChargeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class FakeUnitChargeRepository : UnitChargeRepository {
    private val charges = MutableStateFlow<Map<String, UnitCharge>>(emptyMap())

    override suspend fun insertUnitCharges(chargesList: List<UnitCharge>) {
        val map = charges.value.toMutableMap()
        chargesList.forEach { map[it.id] = it }
        charges.value = map
    }

    override suspend fun updateUnitCharge(charge: UnitCharge) {
        charges.value = charges.value + (charge.id to charge)
    }

    override suspend fun deleteUnitChargesByPeriodId(periodId: String) {
        charges.value = charges.value.filterValues { it.chargePeriodId != periodId }
    }

    override suspend fun getUnitChargesByPeriodId(periodId: String): List<UnitCharge> {
        return charges.value.values.filter { it.chargePeriodId == periodId }
    }

    override fun observeUnitChargesByPeriodId(periodId: String): Flow<List<UnitCharge>> {
        return charges.asStateFlow().map { map ->
            map.values.filter { it.chargePeriodId == periodId }
        }
    }

    override suspend fun getUnitChargeById(id: String): UnitCharge? {
        return charges.value[id]
    }

    override suspend fun getUnitChargesByUnitId(unitId: String): List<UnitCharge> {
        return charges.value.values.filter { it.unitId == unitId }
    }

    override suspend fun updatePayment(id: String, paidAmount: Long, status: ChargeStatus) {
        val current = charges.value[id] ?: return
        val remaining = (current.finalAmount - paidAmount).coerceAtLeast(0L)
        charges.value = charges.value + (id to current.copy(
            paidAmount = paidAmount,
            remainingAmount = remaining,
            status = status
        ))
    }

    override suspend fun updateAdjustment(
        id: String,
        adjustmentAmount: Long,
        finalAmount: Long,
        remainingAmount: Long,
        notes: String?
    ) {
        val current = charges.value[id] ?: return
        charges.value = charges.value + (id to current.copy(
            adjustmentAmount = adjustmentAmount,
            finalAmount = finalAmount,
            remainingAmount = remainingAmount,
            calculationNotes = notes
        ))
    }
}
