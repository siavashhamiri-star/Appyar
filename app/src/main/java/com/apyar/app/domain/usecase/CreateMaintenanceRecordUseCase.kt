package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.AuditEvent
import com.apyar.app.domain.model.MaintenanceRecord
import com.apyar.app.domain.model.Permission
import com.apyar.app.domain.repository.AuditRepository
import com.apyar.app.domain.repository.BuildingRepository
import com.apyar.app.domain.repository.MaintenanceRepository
import java.util.UUID

class CreateMaintenanceRecordUseCase(
    private val maintenanceRepository: MaintenanceRepository,
    private val buildingRepository: BuildingRepository,
    private val checkPermissionUseCase: CheckPermissionUseCase,
    private val auditRepository: AuditRepository
) {
    suspend operator fun invoke(
        userId: String,
        buildingId: String,
        title: String,
        description: String,
        serviceRecordId: String? = null,
        equipmentType: String? = null,
        location: String? = null,
        performedAt: Long = System.currentTimeMillis(),
        nextServiceDate: Long? = null,
        performedBy: String,
        cost: Double? = null,
        notes: String? = null
    ): Result<MaintenanceRecord> {
        checkPermissionUseCase.enforce(buildingId, userId, Permission.MANAGE_MAINTENANCE)

        val building = buildingRepository.getBuildingById(buildingId)
            ?: return Result.failure(IllegalArgumentException("ساختمان مورد نظر یافت نشد"))

        val trimmedTitle = title.trim()
        if (trimmedTitle.isBlank()) {
            return Result.failure(IllegalArgumentException("عنوان سرویس یا تعمیر نمی‌تواند خالی باشد"))
        }

        val trimmedPerformedBy = performedBy.trim()
        if (trimmedPerformedBy.isBlank()) {
            return Result.failure(IllegalArgumentException("نام انجام‌دهنده سرویس مشخص نشده است"))
        }

        val record = MaintenanceRecord(
            id = UUID.randomUUID().toString(),
            buildingId = buildingId,
            serviceRecordId = serviceRecordId,
            title = trimmedTitle,
            description = description.trim(),
            equipmentType = equipmentType?.trim(),
            location = location?.trim(),
            performedAt = performedAt,
            nextServiceDate = nextServiceDate,
            performedBy = trimmedPerformedBy,
            cost = cost,
            notes = notes?.trim()
        )

        val created = maintenanceRepository.createMaintenanceRecord(record)

        auditRepository.recordEvent(
            AuditEvent(
                actor = userId,
                action = "CREATE_MAINTENANCE_RECORD",
                entity = "MaintenanceRecord",
                entityId = created.id,
                buildingId = buildingId,
                details = "ثبت سابقه تعمیر و نگهداری برای '${created.title}' (تجهیز: ${created.equipmentType ?: "عمومی"})"
            )
        )

        return Result.success(created)
    }
}
