package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.AssignmentType
import com.apyar.app.domain.model.AuditEvent
import com.apyar.app.domain.model.Permission
import com.apyar.app.domain.model.SpaceStatus
import com.apyar.app.domain.model.StorageAssignment
import com.apyar.app.domain.repository.AuditRepository
import com.apyar.app.domain.repository.StorageRepository
import com.apyar.app.domain.repository.UnitRepository
import java.util.UUID

class AssignStorageUnitUseCase(
    private val storageRepository: StorageRepository,
    private val unitRepository: UnitRepository,
    private val checkPermissionUseCase: CheckPermissionUseCase,
    private val auditRepository: AuditRepository
) {
    suspend operator fun invoke(
        userId: String,
        buildingId: String,
        storageUnitId: String,
        unitId: String,
        personId: String? = null,
        assignmentType: AssignmentType = AssignmentType.UNIT_ASSIGNED,
        startDate: Long = System.currentTimeMillis(),
        endDate: Long? = null,
        notes: String? = null
    ): Result<StorageAssignment> {
        checkPermissionUseCase.enforce(buildingId, userId, Permission.MANAGE_STORAGE_DATA)

        val storageUnit = storageRepository.getStorageUnitById(storageUnitId)
            ?: return Result.failure(IllegalArgumentException("انباری مورد نظر یافت نشد"))

        if (storageUnit.buildingId != buildingId) {
            return Result.failure(IllegalArgumentException("انباری به این ساختمان تعلق ندارد"))
        }

        val unit = unitRepository.getUnitById(unitId)
            ?: return Result.failure(IllegalArgumentException("واحد مورد نظر یافت نشد"))

        if (unit.buildingId != buildingId) {
            return Result.failure(IllegalArgumentException("واحد به این ساختمان تعلق ندارد"))
        }

        if (endDate != null && endDate < startDate) {
            return Result.failure(IllegalArgumentException("تاریخ پایان نمی‌تواند قبل از تاریخ شروع باشد"))
        }

        val assignment = StorageAssignment(
            id = UUID.randomUUID().toString(),
            storageUnitId = storageUnitId,
            unitId = unitId,
            personId = personId,
            assignmentType = assignmentType,
            startDate = startDate,
            endDate = endDate,
            notes = notes?.trim(),
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            isActive = true
        )

        val createdAssignment = storageRepository.createAssignment(assignment)

        storageRepository.updateStorageUnit(
            storageUnit.copy(
                status = SpaceStatus.ASSIGNED,
                updatedAt = System.currentTimeMillis()
            )
        )

        auditRepository.recordEvent(
            AuditEvent(
                actor = userId,
                action = "ASSIGN_STORAGE_UNIT",
                entity = "StorageUnit",
                entityId = storageUnit.id,
                buildingId = buildingId,
                details = "تخصیص انباری ${storageUnit.code} به واحد ${unit.unitNumber} با نوع ${assignmentType.titleFa}"
            )
        )

        return Result.success(createdAssignment)
    }
}
