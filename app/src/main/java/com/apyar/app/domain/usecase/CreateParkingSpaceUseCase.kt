package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.AuditEvent
import com.apyar.app.domain.model.ParkingSpace
import com.apyar.app.domain.model.ParkingType
import com.apyar.app.domain.model.Permission
import com.apyar.app.domain.model.SpaceStatus
import com.apyar.app.domain.repository.AuditRepository
import com.apyar.app.domain.repository.BuildingRepository
import com.apyar.app.domain.repository.ParkingRepository
import java.util.UUID

class CreateParkingSpaceUseCase(
    private val parkingRepository: ParkingRepository,
    private val buildingRepository: BuildingRepository,
    private val checkPermissionUseCase: CheckPermissionUseCase,
    private val auditRepository: AuditRepository
) {
    suspend operator fun invoke(
        userId: String,
        buildingId: String,
        code: String,
        floor: Int = 0,
        locationDescription: String? = null,
        parkingType: ParkingType = ParkingType.NORMAL,
        notes: String? = null
    ): Result<ParkingSpace> {
        val permResult = checkPermissionUseCase.enforce(buildingId, userId, Permission.MANAGE_PARKING_DATA)
        
        val building = buildingRepository.getBuildingById(buildingId)
            ?: return Result.failure(IllegalArgumentException("ساختمان مورد نظر یافت نشد"))

        val trimmedCode = code.trim()
        if (trimmedCode.isBlank()) {
            return Result.failure(IllegalArgumentException("کد پارکینگ نمی‌تواند خالی باشد"))
        }

        val existing = parkingRepository.getParkingSpaceByCode(buildingId, trimmedCode)
        if (existing != null) {
            return Result.failure(IllegalStateException("کد پارکینگ '$trimmedCode' در این ساختمان قبلاً ثبت شده است"))
        }

        val space = ParkingSpace(
            id = UUID.randomUUID().toString(),
            buildingId = buildingId,
            code = trimmedCode,
            floor = floor,
            locationDescription = locationDescription?.trim(),
            parkingType = parkingType,
            status = SpaceStatus.AVAILABLE,
            notes = notes?.trim(),
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            isActive = true
        )

        val created = parkingRepository.createParkingSpace(space)

        auditRepository.recordEvent(
            AuditEvent(
                actor = userId,
                action = "CREATE_PARKING_SPACE",
                entity = "ParkingSpace",
                entityId = created.id,
                buildingId = buildingId,
                details = "ایجاد پارکینگ جدید با کد ${created.code} در طبقه ${created.floor}"
            )
        )

        return Result.success(created)
    }
}
