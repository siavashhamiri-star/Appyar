package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.BillPaymentStatus
import com.apyar.app.domain.model.InvoicePaymentStatus
import com.apyar.app.domain.model.Permission
import com.apyar.app.domain.model.ServiceDashboardSummary
import com.apyar.app.domain.model.ServiceRecordStatus
import com.apyar.app.domain.repository.BuildingBillRepository
import com.apyar.app.domain.repository.MaintenanceRepository
import com.apyar.app.domain.repository.ServiceInvoiceRepository
import com.apyar.app.domain.repository.ServiceProviderRepository
import com.apyar.app.domain.repository.ServiceRecordRepository

class GetServiceDashboardSummaryUseCase(
    private val serviceProviderRepository: ServiceProviderRepository,
    private val serviceRecordRepository: ServiceRecordRepository,
    private val maintenanceRepository: MaintenanceRepository,
    private val serviceInvoiceRepository: ServiceInvoiceRepository,
    private val buildingBillRepository: BuildingBillRepository,
    private val checkPermissionUseCase: CheckPermissionUseCase
) {
    suspend operator fun invoke(userId: String, buildingId: String): Result<ServiceDashboardSummary> {
        checkPermissionUseCase.enforce(buildingId, userId, Permission.VIEW_BUILDING)

        val providers = serviceProviderRepository.getBuildingServiceProviders(buildingId)
        val activeProviders = providers.count { it.link.isActive }

        val activeServices = serviceRecordRepository.getActiveServiceRecords(buildingId)
        val totalActiveServices = activeServices.size

        val now = System.currentTimeMillis()
        val upcomingMaintenance = maintenanceRepository.getUpcomingMaintenance(buildingId, now)
        val upcomingMaintenanceCount = upcomingMaintenance.size

        val invoices = serviceInvoiceRepository.getInvoicesByBuilding(buildingId)
        val unpaidInvoices = invoices.filter { it.invoice.paymentStatus != InvoicePaymentStatus.PAID }
        val unpaidInvoicesCount = unpaidInvoices.size
        val totalUnpaidInvoicesAmount = unpaidInvoices.sumOf { it.remainingAmount }

        val bills = buildingBillRepository.getBuildingBillsByBuilding(buildingId)
        val unpaidBills = bills.filter { it.paymentStatus != BillPaymentStatus.PAID }
        val unpaidBillsCount = unpaidBills.size
        val totalUnpaidBillsAmount = unpaidBills.sumOf { it.amount }

        val recentMaintenance = maintenanceRepository.getMaintenanceRecordsByBuilding(buildingId).take(5)
        val recentPayments = serviceInvoiceRepository.getPaymentsByBuilding(buildingId).take(5)

        val summary = ServiceDashboardSummary(
            totalActiveProviders = activeProviders,
            totalActiveServices = totalActiveServices,
            upcomingMaintenanceCount = upcomingMaintenanceCount,
            unpaidInvoicesCount = unpaidInvoicesCount,
            totalUnpaidInvoicesAmount = totalUnpaidInvoicesAmount,
            unpaidBillsCount = unpaidBillsCount,
            totalUnpaidBillsAmount = totalUnpaidBillsAmount,
            recentMaintenance = recentMaintenance,
            recentPayments = recentPayments
        )

        return Result.success(summary)
    }
}
