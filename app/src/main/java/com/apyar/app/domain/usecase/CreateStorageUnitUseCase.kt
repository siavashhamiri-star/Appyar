package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.AuditEvent
import com.apyar.app.domain.model.Permission
import com.apyar.app.domain.model.SpaceStatus
import com.apyar.app.domain.model.StorageUnit
import com.apyar.app.domain.repository.AuditRepository
import com.apyar.app.domain.repository.BuildingRepository
import com.apyar.app.domain.repository.StorageRepository
import java.util.UUID

class CreateStorageUnitUseCase(
    private val storageRepository: StorageRepository,
    private val buildingRepository: BuildingRepository,
    private val checkPermissionUseCase: CheckPermissionUseCase,
    private val auditRepository: AuditRepository
) {
    suspend operator fun invoke(
        userId: String,
        buildingId: String,
        code: String,
        areaSquareMeters: Double? = null,
        locationDescription: String? = null,
        notes: String? = null
    ): Result<StorageUnit> {
        checkPermissionUseCase.enforce(buildingId, userId, Permission.MANAGE_STORAGE_DATA)

        val building = buildingRepository.getBuildingById(buildingId)
            ?: return Result.failure(IllegalArgumentException("ساختمان مورد نظر یافت نشد"))

        val trimmedCode = code.trim()
        if (trimmedCode.isBlank()) {
            return Result.failure(IllegalArgumentException("کد انباری نمی‌تواند خالی باشد"))
        }

        val existing = storageRepository.getStorageUnitByCode(buildingId, trimmedCode)
        if (existing != null) {
            return Result.failure(IllegalStateException("کد انباری '$trimmedCode' در این ساختمان قبلاً ثبت شده است"))
        }

        val unit = StorageUnit(
            id = UUID.randomUUID().toString(),
            buildingId = buildingId,
            code = trimmedCode,
            areaSquareMeters = areaSquareMeters,
            locationDescription = locationDescription?.trim(),
            status = SpaceStatus.AVAILABLE,
            notes = notes?.trim(),
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            isActive = true
        )

        val created = storageRepository.createStorageUnit(unit)

        auditRepository.recordEvent(
            AuditEvent(
                actor = userId,
                action = "CREATE_STORAGE_UNIT",
                entity = "StorageUnit",
                entityId = created.id,
                buildingId = buildingId,
                details = "ایجاد انباری جدید با کد ${created.code} و متراژ ${created.areaSquareMeters ?: 0.0} مترمربع"
            )
        )

        return Result.success(created)
    }
}
