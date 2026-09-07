package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.PaymentRecord
import com.apyar.app.domain.model.Permission
import com.apyar.app.domain.repository.ServiceInvoiceRepository
import kotlinx.coroutines.flow.Flow

class GetBuildingPaymentsUseCase(
    private val serviceInvoiceRepository: ServiceInvoiceRepository,
    private val checkPermissionUseCase: CheckPermissionUseCase
) {
    suspend fun getList(userId: String, buildingId: String): Result<List<PaymentRecord>> {
        val allowed = checkPermissionUseCase.hasPermission(buildingId, userId, Permission.VIEW_BUILDING_PAYMENTS)
        if (!allowed) {
            checkPermissionUseCase.enforce(buildingId, userId, Permission.VIEW_BUILDING_PAYMENTS)
        }
        val list = serviceInvoiceRepository.getPaymentsByBuilding(buildingId)
        return Result.success(list)
    }

    fun observe(userId: String, buildingId: String): Flow<List<PaymentRecord>> {
        return serviceInvoiceRepository.observePaymentsByBuilding(buildingId)
    }
}
