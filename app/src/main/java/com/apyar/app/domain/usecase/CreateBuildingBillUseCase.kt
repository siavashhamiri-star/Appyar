package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.AuditEvent
import com.apyar.app.domain.model.BillPaymentStatus
import com.apyar.app.domain.model.BillType
import com.apyar.app.domain.model.BuildingBill
import com.apyar.app.domain.model.Permission
import com.apyar.app.domain.repository.AuditRepository
import com.apyar.app.domain.repository.BuildingBillRepository
import com.apyar.app.domain.repository.BuildingRepository
import java.util.UUID

class CreateBuildingBillUseCase(
    private val buildingBillRepository: BuildingBillRepository,
    private val buildingRepository: BuildingRepository,
    private val checkPermissionUseCase: CheckPermissionUseCase,
    private val auditRepository: AuditRepository
) {
    suspend operator fun invoke(
        userId: String,
        buildingId: String,
        billType: BillType,
        providerName: String,
        billingPeriod: String,
        amount: Double,
        issueDate: Long? = System.currentTimeMillis(),
        dueDate: Long? = null,
        referenceNumber: String? = null
    ): Result<BuildingBill> {
        checkPermissionUseCase.enforce(buildingId, userId, Permission.MANAGE_BUILDING_BILLS)

        val building = buildingRepository.getBuildingById(buildingId)
            ?: return Result.failure(IllegalArgumentException("ساختمان مورد نظر یافت نشد"))

        if (amount <= 0.0) {
            return Result.failure(IllegalArgumentException("مبلغ قبض باید بزرگتر از صفر باشد"))
        }

        val trimmedProvider = providerName.trim()
        if (trimmedProvider.isBlank()) {
            return Result.failure(IllegalArgumentException("نام صادرکننده یا سازمان مربوطه نمی‌تواند خالی باشد"))
        }

        val trimmedPeriod = billingPeriod.trim()
        if (trimmedPeriod.isBlank()) {
            return Result.failure(IllegalArgumentException("دوره قبض نمی‌تواند خالی باشد"))
        }

        val bill = BuildingBill(
            id = UUID.randomUUID().toString(),
            buildingId = buildingId,
            billType = billType,
            providerName = trimmedProvider,
            billingPeriod = trimmedPeriod,
            amount = amount,
            issueDate = issueDate,
            dueDate = dueDate,
            paidAt = null,
            paymentStatus = BillPaymentStatus.UNPAID,
            referenceNumber = referenceNumber?.trim(),
            createdBy = userId
        )

        val created = buildingBillRepository.createBuildingBill(bill)

        auditRepository.recordEvent(
            AuditEvent(
                actor = userId,
                action = "CREATE_BUILDING_BILL",
                entity = "BuildingBill",
                entityId = created.id,
                buildingId = buildingId,
                details = "ثبت قبض عمومی ${billType.titleFa} به مبلغ $amount تومان برای دوره $billingPeriod"
            )
        )

        return Result.success(created)
    }
}
