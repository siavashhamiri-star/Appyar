package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.AuditEvent
import com.apyar.app.domain.model.Permission
import com.apyar.app.domain.model.SpaceStatus
import com.apyar.app.domain.repository.AuditRepository
import com.apyar.app.domain.repository.StorageRepository

class EndStorageAssignmentUseCase(
    private val storageRepository: StorageRepository,
    private val checkPermissionUseCase: CheckPermissionUseCase,
    private val auditRepository: AuditRepository
) {
    suspend operator fun invoke(
        userId: String,
        buildingId: String,
        storageUnitId: String,
        endDate: Long = System.currentTimeMillis()
    ): Result<Unit> {
        checkPermissionUseCase.enforce(buildingId, userId, Permission.MANAGE_STORAGE_DATA)

        val storageUnit = storageRepository.getStorageUnitById(storageUnitId)
            ?: return Result.failure(IllegalArgumentException("انباری مورد نظر یافت نشد"))

        if (storageUnit.buildingId != buildingId) {
            return Result.failure(IllegalArgumentException("انباری به این ساختمان تعلق ندارد"))
        }

        val activeAssignment = storageRepository.getActiveAssignmentByStorageId(storageUnitId)
        if (activeAssignment != null) {
            storageRepository.updateAssignment(
                activeAssignment.copy(
                    isActive = false,
                    endDate = endDate,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }

        storageRepository.updateStorageUnit(
            storageUnit.copy(
                status = SpaceStatus.AVAILABLE,
                updatedAt = System.currentTimeMillis()
            )
        )

        auditRepository.recordEvent(
            AuditEvent(
                actor = userId,
                action = "END_STORAGE_ASSIGNMENT",
                entity = "StorageUnit",
                entityId = storageUnit.id,
                buildingId = buildingId,
                details = "خاتمه تخصیص انباری ${storageUnit.code} و بازگشت به وضعیت آزاد"
            )
        )

        return Result.success(Unit)
    }
}
