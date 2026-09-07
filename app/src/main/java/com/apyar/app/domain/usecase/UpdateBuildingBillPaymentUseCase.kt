package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.AuditEvent
import com.apyar.app.domain.model.BillPaymentStatus
import com.apyar.app.domain.model.BuildingBill
import com.apyar.app.domain.model.Permission
import com.apyar.app.domain.repository.AuditRepository
import com.apyar.app.domain.repository.BuildingBillRepository

class UpdateBuildingBillPaymentUseCase(
    private val buildingBillRepository: BuildingBillRepository,
    private val checkPermissionUseCase: CheckPermissionUseCase,
    private val auditRepository: AuditRepository
) {
    suspend operator fun invoke(
        userId: String,
        buildingId: String,
        billId: String,
        paymentStatus: BillPaymentStatus,
        paidAt: Long? = System.currentTimeMillis(),
        referenceNumber: String? = null
    ): Result<BuildingBill> {
        checkPermissionUseCase.enforce(buildingId, userId, Permission.MANAGE_BUILDING_BILLS)

        val bill = buildingBillRepository.getBuildingBillById(billId)
            ?: return Result.failure(IllegalArgumentException("قبض مورد نظر یافت نشد"))

        if (bill.buildingId != buildingId) {
            return Result.failure(IllegalArgumentException("قبض مربوط به این ساختمان نیست"))
        }

        val updated = bill.copy(
            paymentStatus = paymentStatus,
            paidAt = if (paymentStatus == BillPaymentStatus.PAID) (paidAt ?: System.currentTimeMillis()) else null,
            referenceNumber = referenceNumber?.trim() ?: bill.referenceNumber
        )

        buildingBillRepository.updateBuildingBill(updated)

        auditRepository.recordEvent(
            AuditEvent(
                actor = userId,
                action = "UPDATE_BUILDING_BILL_PAYMENT",
                entity = "BuildingBill",
                entityId = updated.id,
                buildingId = buildingId,
                details = "تغییر وضعیت پرداخت قبض ${updated.billType.titleFa} به ${paymentStatus.titleFa}"
            )
        )

        return Result.success(updated)
    }
}
