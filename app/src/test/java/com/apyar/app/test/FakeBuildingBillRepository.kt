package com.apyar.app.test

import com.apyar.app.domain.model.BillType
import com.apyar.app.domain.model.BuildingBill
import com.apyar.app.domain.repository.BuildingBillRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class FakeBuildingBillRepository : BuildingBillRepository {
    private val bills = MutableStateFlow<Map<String, BuildingBill>>(emptyMap())

    override suspend fun createBuildingBill(bill: BuildingBill): Result<BuildingBill> {
        bills.value = bills.value + (bill.id to bill)
        return Result.success(bill)
    }

    override suspend fun getBuildingBillById(billId: String): BuildingBill? {
        return bills.value[billId]
    }

    override fun getBuildingBills(buildingId: String): Flow<List<BuildingBill>> {
        return bills.asStateFlow().map { map -> map.values.filter { it.buildingId == buildingId } }
    }

    override fun getBuildingBillsByType(buildingId: String, billType: BillType): Flow<List<BuildingBill>> {
        return bills.asStateFlow().map { map -> map.values.filter { it.buildingId == buildingId && it.billType == billType } }
    }

    override fun getUnpaidBills(buildingId: String): Flow<List<BuildingBill>> {
        return bills.asStateFlow().map { map -> map.values.filter { it.buildingId == buildingId && !it.isPaid } }
    }

    override suspend fun updateBillPaymentStatus(
        billId: String,
        isPaid: Boolean,
        paymentDate: Long?,
        paymentReference: String?
    ): Result<Unit> {
        val existing = bills.value[billId] ?: return Result.failure(IllegalArgumentException("Bill not found"))
        val updated = existing.copy(
            isPaid = isPaid,
            paymentDate = paymentDate,
            paymentReference = paymentReference
        )
        bills.value = bills.value + (billId to updated)
        return Result.success(Unit)
    }
}
