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

class AssignParkingSpaceUseCase(
    private val parkingRepository: ParkingRepository,
    private val unitRepository: UnitRepository,
    private val checkPermissionUseCase: CheckPermissionUseCase,
    private val auditRepository: AuditRepository
) {
    suspend operator fun invoke(
        userId: String,
        buildingId: String,
        parkingSpaceId: String,
        unitId: String,
        personId: String? = null,
        assignmentType: AssignmentType = AssignmentType.UNIT_ASSIGNED,
        startDate: Long = System.currentTimeMillis(),
        endDate: Long? = null,
        notes: String? = null
    ): Result<ParkingAssignment> {
        checkPermissionUseCase.enforce(buildingId, userId, Permission.MANAGE_PARKING_DATA)

        val space = parkingRepository.getParkingSpaceById(parkingSpaceId)
            ?: return Result.failure(IllegalArgumentException("پارکینگ مورد نظر یافت نشد"))

        if (space.buildingId != buildingId) {
            return Result.failure(IllegalArgumentException("پارکینگ به این ساختمان تعلق ندارد"))
        }

        val unit = unitRepository.getUnitById(unitId)
            ?: return Result.failure(IllegalArgumentException("واحد مورد نظر یافت نشد"))

        if (unit.buildingId != buildingId) {
            return Result.failure(IllegalArgumentException("واحد به این ساختمان تعلق ندارد"))
        }

        if (endDate != null && endDate < startDate) {
            return Result.failure(IllegalArgumentException("تاریخ پایان نمی‌تواند قبل از تاریخ شروع باشد"))
        }

        val assignment = ParkingAssignment(
            id = UUID.randomUUID().toString(),
            parkingSpaceId = parkingSpaceId,
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

        val createdAssignment = parkingRepository.createAssignment(assignment)

        // Update space status
        parkingRepository.updateParkingSpace(
            space.copy(
                status = SpaceStatus.ASSIGNED,
                updatedAt = System.currentTimeMillis()
            )
        )

        auditRepository.recordEvent(
            AuditEvent(
                actor = userId,
                action = "ASSIGN_PARKING_SPACE",
                entity = "ParkingSpace",
                entityId = space.id,
                buildingId = buildingId,
                details = "تخصیص پارکینگ ${space.code} به واحد ${unit.unitNumber} با نوع ${assignmentType.titleFa}"
            )
        )

        return Result.success(createdAssignment)
    }
}
