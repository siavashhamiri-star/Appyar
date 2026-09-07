package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.BuildingBill
import com.apyar.app.domain.model.InvoiceWithDetails
import com.apyar.app.domain.model.MaintenanceRecord
import com.apyar.app.domain.model.PaymentRecord
import com.apyar.app.domain.model.Permission
import com.apyar.app.domain.model.ServiceRecordWithDetails
import com.apyar.app.domain.repository.BuildingBillRepository
import com.apyar.app.domain.repository.MaintenanceRepository
import com.apyar.app.domain.repository.ServiceInvoiceRepository
import com.apyar.app.domain.repository.ServiceRecordRepository

data class BuildingServiceArchive(
    val serviceRecords: List<ServiceRecordWithDetails>,
    val maintenanceRecords: List<MaintenanceRecord>,
    val invoices: List<InvoiceWithDetails>,
    val payments: List<PaymentRecord>,
    val bills: List<BuildingBill>
)

class GetBuildingServiceArchiveUseCase(
    private val serviceRecordRepository: ServiceRecordRepository,
    private val maintenanceRepository: MaintenanceRepository,
    private val serviceInvoiceRepository: ServiceInvoiceRepository,
    private val buildingBillRepository: BuildingBillRepository,
    private val checkPermissionUseCase: CheckPermissionUseCase
) {
    suspend operator fun invoke(
        userId: String,
        buildingId: String,
        fromTimestamp: Long? = null,
        toTimestamp: Long? = null
    ): Result<BuildingServiceArchive> {
        val hasPerm = checkPermissionUseCase.hasPermission(buildingId, userId, Permission.VIEW_SERVICE_RECORDS)
                || checkPermissionUseCase.hasPermission(buildingId, userId, Permission.VIEW_DOCUMENTS)
        if (!hasPerm) {
            checkPermissionUseCase.enforce(buildingId, userId, Permission.VIEW_SERVICE_RECORDS)
        }

        var services = serviceRecordRepository.getServiceRecordsByBuilding(buildingId)
        var maintenance = maintenanceRepository.getMaintenanceRecordsByBuilding(buildingId)
        var invoices = serviceInvoiceRepository.getInvoicesByBuilding(buildingId)
        var payments = serviceInvoiceRepository.getPaymentsByBuilding(buildingId)
        var bills = buildingBillRepository.getBuildingBillsByBuilding(buildingId)

        if (fromTimestamp != null) {
            services = services.filter { it.record.requestedAt >= fromTimestamp }
            maintenance = maintenance.filter { it.performedAt >= fromTimestamp }
            invoices = invoices.filter { it.invoice.issueDate >= fromTimestamp }
            payments = payments.filter { it.paidAt >= fromTimestamp }
            bills = bills.filter { (it.issueDate ?: 0L) >= fromTimestamp }
        }

        if (toTimestamp != null) {
            services = services.filter { it.record.requestedAt <= toTimestamp }
            maintenance = maintenance.filter { it.performedAt <= toTimestamp }
            invoices = invoices.filter { it.invoice.issueDate <= toTimestamp }
            payments = payments.filter { it.paidAt <= toTimestamp }
            bills = bills.filter { (it.issueDate ?: 0L) <= toTimestamp }
        }

        return Result.success(
            BuildingServiceArchive(
                serviceRecords = services,
                maintenanceRecords = maintenance,
                invoices = invoices,
                payments = payments,
                bills = bills
            )
        )
    }
}
