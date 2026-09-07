package com.apyar.app.domain.repository

import com.apyar.app.domain.model.BillPaymentStatus
import com.apyar.app.domain.model.BillType
import com.apyar.app.domain.model.BuildingBill
import kotlinx.coroutines.flow.Flow

interface BuildingBillRepository {
    suspend fun createBuildingBill(bill: BuildingBill): BuildingBill
    suspend fun updateBuildingBill(bill: BuildingBill)
    suspend fun getBuildingBillById(id: String): BuildingBill?
    fun observeBuildingBillsByBuilding(buildingId: String): Flow<List<BuildingBill>>
    suspend fun getBuildingBillsByBuilding(buildingId: String): List<BuildingBill>
    suspend fun getBillsByType(buildingId: String, billType: BillType): List<BuildingBill>
    suspend fun getBillsByPaymentStatus(buildingId: String, status: BillPaymentStatus): List<BuildingBill>
}
