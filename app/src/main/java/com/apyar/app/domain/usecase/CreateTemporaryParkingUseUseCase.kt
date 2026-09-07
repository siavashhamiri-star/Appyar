package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.AssignmentType
import com.apyar.app.domain.model.AuditEvent
import com.apyar.app.domain.model.ParkingAssignment
import com.apyar.app.domain.model.Permission
import com.apyar.app.domain.model.SpaceStatus
import com.apyar.app.domain.repository.AuditRepository
import com.apyar.app.domain.repository.ParkingRepository
import com.apyar.app.domain.repository.UnitRepository
import java.util.UUID

class CreateTemporaryParkingUseUseCase(
    private val parkingRepository: ParkingRepository,
    private val unitRepository: UnitRepository,
    private val checkPermissionUseCase: CheckPermissionUseCase,
    private val auditRepository: AuditRepository
) {
    suspend operator fun invoke(
        userId: String,
        buildingId: String,
        parkingSpaceId: String,
        targetUnitId: String,
        startDate: Long = System.currentTimeMillis(),
        endDate: Long? = null,
        description: String? = null
    ): Result<ParkingAssignment> {
        checkPermissionUseCase.enforce(buildingId, userId, Permission.MANAGE_PARKING_DATA)

        val space = parkingRepository.getParkingSpaceById(parkingSpaceId)
            ?: return Result.failure(IllegalArgumentException("پارکینگ مورد نظر یافت نشد"))

        if (space.buildingId != buildingId) {
            return Result.failure(IllegalArgumentException("پارکینگ به این ساختمان تعلق ندارد"))
        }

        val targetUnit = unitRepository.getUnitById(targetUnitId)
            ?: return Result.failure(IllegalArgumentException("واحد مقصد یافت نشد"))

        if (targetUnit.buildingId != buildingId) {
            return Result.failure(IllegalArgumentException("واحد مقصد به این ساختمان تعلق ندارد"))
        }

        if (endDate != null && endDate < startDate) {
            return Result.failure(IllegalArgumentException("تاریخ پایان استفاده موقت نمی‌تواند قبل از تاریخ شروع باشد"))
        }

        val assignment = ParkingAssignment(
            id = UUID.randomUUID().toString(),
            parkingSpaceId = parkingSpaceId,
            unitId = targetUnitId,
            assignmentType = AssignmentType.TEMPORARY,
            startDate = startDate,
            endDate = endDate,
            notes = description?.trim(),
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            isActive = true
        )

        val createdAssignment = parkingRepository.createAssignment(assignment)

        parkingRepository.updateParkingSpace(
            space.copy(
                status = SpaceStatus.TEMPORARILY_USED,
                updatedAt = System.currentTimeMillis()
            )
        )

        auditRepository.recordEvent(
            AuditEvent(
                actor = userId,
                action = "CREATE_TEMPORARY_PARKING_USE",
                entity = "ParkingSpace",
                entityId = space.id,
                buildingId = buildingId,
                details = "ثبت استفاده موقت پارکینگ ${space.code} توسط واحد ${targetUnit.unitNumber} (${description ?: "بدون توضیحات"})"
            )
        )

        return Result.success(createdAssignment)
    }
}
