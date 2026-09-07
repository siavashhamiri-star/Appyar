package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.InvoiceWithDetails
import com.apyar.app.domain.model.Permission
import com.apyar.app.domain.repository.ServiceInvoiceRepository
import kotlinx.coroutines.flow.Flow

class GetBuildingInvoicesUseCase(
    private val serviceInvoiceRepository: ServiceInvoiceRepository,
    private val checkPermissionUseCase: CheckPermissionUseCase
) {
    suspend fun getList(userId: String, buildingId: String): Result<List<InvoiceWithDetails>> {
        val allowed = checkPermissionUseCase.hasPermission(buildingId, userId, Permission.VIEW_INVOICES)
        if (!allowed) {
            checkPermissionUseCase.enforce(buildingId, userId, Permission.VIEW_INVOICES)
        }
        val list = serviceInvoiceRepository.getInvoicesByBuilding(buildingId)
        return Result.success(list)
    }

    suspend fun getById(userId: String, buildingId: String, invoiceId: String): Result<InvoiceWithDetails?> {
        val allowed = checkPermissionUseCase.hasPermission(buildingId, userId, Permission.VIEW_INVOICES)
        if (!allowed) {
            checkPermissionUseCase.enforce(buildingId, userId, Permission.VIEW_INVOICES)
        }
        val item = serviceInvoiceRepository.getInvoiceWithDetailsById(invoiceId)
        return Result.success(item)
    }

    fun observe(userId: String, buildingId: String): Flow<List<InvoiceWithDetails>> {
        return serviceInvoiceRepository.observeInvoicesByBuilding(buildingId)
    }
}
