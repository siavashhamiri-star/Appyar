package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.AuditEvent
import com.apyar.app.domain.model.Permission
import com.apyar.app.domain.model.SpaceStatus
import com.apyar.app.domain.repository.AuditRepository
import com.apyar.app.domain.repository.ParkingRepository

class EndParkingAssignmentUseCase(
    private val parkingRepository: ParkingRepository,
    private val checkPermissionUseCase: CheckPermissionUseCase,
    private val auditRepository: AuditRepository
) {
    suspend operator fun invoke(
        userId: String,
        buildingId: String,
        parkingSpaceId: String,
        endDate: Long = System.currentTimeMillis()
    ): Result<Unit> {
        checkPermissionUseCase.enforce(buildingId, userId, Permission.MANAGE_PARKING_DATA)

        val space = parkingRepository.getParkingSpaceById(parkingSpaceId)
            ?: return Result.failure(IllegalArgumentException("پارکینگ مورد نظر یافت نشد"))

        if (space.buildingId != buildingId) {
            return Result.failure(IllegalArgumentException("پارکینگ به این ساختمان تعلق ندارد"))
        }

        val activeAssignment = parkingRepository.getActiveAssignmentByParkingId(parkingSpaceId)
        if (activeAssignment != null) {
            parkingRepository.updateAssignment(
                activeAssignment.copy(
                    isActive = false,
                    endDate = endDate,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }

        parkingRepository.updateParkingSpace(
            space.copy(
                status = SpaceStatus.AVAILABLE,
                updatedAt = System.currentTimeMillis()
            )
        )

        auditRepository.recordEvent(
            AuditEvent(
                actor = userId,
                action = "END_PARKING_ASSIGNMENT",
                entity = "ParkingSpace",
                entityId = space.id,
                buildingId = buildingId,
                details = "خاتمه تخصیص/استفاده موقت پارکینگ ${space.code} و بازگشت به وضعیت آزاد"
            )
        )

        return Result.success(Unit)
    }
}
