package com.apyar.app.data.repository

import com.apyar.app.data.local.dao.BuildingBillDao
import com.apyar.app.data.mapper.toDomain
import com.apyar.app.data.mapper.toEntity
import com.apyar.app.domain.model.BillPaymentStatus
import com.apyar.app.domain.model.BillType
import com.apyar.app.domain.model.BuildingBill
import com.apyar.app.domain.repository.BuildingBillRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BuildingBillRepositoryImpl(
    private val buildingBillDao: BuildingBillDao
) : BuildingBillRepository {

    override suspend fun createBuildingBill(bill: BuildingBill): BuildingBill {
        buildingBillDao.insertBuildingBill(bill.toEntity())
        return bill
    }

    override suspend fun updateBuildingBill(bill: BuildingBill) {
        buildingBillDao.updateBuildingBill(bill.toEntity())
    }

    override suspend fun getBuildingBillById(id: String): BuildingBill? {
        return buildingBillDao.getBuildingBillById(id)?.toDomain()
    }

    override fun observeBuildingBillsByBuilding(buildingId: String): Flow<List<BuildingBill>> {
        return buildingBillDao.observeBuildingBillsByBuildingId(buildingId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getBuildingBillsByBuilding(buildingId: String): List<BuildingBill> {
        return buildingBillDao.getBuildingBillsByBuildingId(buildingId).map { it.toDomain() }
    }

    override suspend fun getBillsByType(
        buildingId: String,
        billType: BillType
    ): List<BuildingBill> {
        return buildingBillDao.getBillsByType(buildingId, billType.name).map { it.toDomain() }
    }

    override suspend fun getBillsByPaymentStatus(
        buildingId: String,
        status: BillPaymentStatus
    ): List<BuildingBill> {
        return buildingBillDao.getBillsByPaymentStatus(buildingId, status.name).map { it.toDomain() }
    }
}
