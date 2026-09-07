package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.AuditEvent
import com.apyar.app.domain.model.Permission
import com.apyar.app.domain.model.ServiceRecord
import com.apyar.app.domain.model.ServiceRecordStatus
import com.apyar.app.domain.repository.AuditRepository
import com.apyar.app.domain.repository.ServiceRecordRepository

class UpdateServiceRecordStatusUseCase(
    private val serviceRecordRepository: ServiceRecordRepository,
    private val checkPermissionUseCase: CheckPermissionUseCase,
    private val auditRepository: AuditRepository
) {
    suspend operator fun invoke(
        userId: String,
        buildingId: String,
        serviceRecordId: String,
        newStatus: ServiceRecordStatus,
        notes: String? = null
    ): Result<ServiceRecord> {
        checkPermissionUseCase.enforce(buildingId, userId, Permission.MANAGE_SERVICE_RECORDS)

        val record = serviceRecordRepository.getServiceRecordById(serviceRecordId)
            ?: return Result.failure(IllegalArgumentException("سوابق خدمت مورد نظر یافت نشد"))

        if (record.buildingId != buildingId) {
            return Result.failure(IllegalArgumentException("سوابق مربوط به این ساختمان نیست"))
        }

        val completedAt = if (newStatus == ServiceRecordStatus.COMPLETED) {
            record.completedAt ?: System.currentTimeMillis()
        } else {
            record.completedAt
        }

        val updated = record.copy(
            status = newStatus,
            completedAt = completedAt,
            notes = notes?.trim() ?: record.notes
        )

        serviceRecordRepository.updateServiceRecord(updated)

        auditRepository.recordEvent(
            AuditEvent(
                actor = userId,
                action = "UPDATE_SERVICE_RECORD_STATUS",
                entity = "ServiceRecord",
                entityId = updated.id,
                buildingId = buildingId,
                details = "تغییر وضعیت خدمت '${updated.title}' به ${newStatus.titleFa}"
            )
        )

        return Result.success(updated)
    }
}
