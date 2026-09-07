package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.Unit
import com.apyar.app.domain.repository.UnitRepository
import java.util.UUID

class CreateUnitUseCase(
    private val unitRepository: UnitRepository
) {
    suspend operator fun invoke(
        buildingId: String,
        unitNumber: String,
        floor: Int,
        areaSquareMeters: Double,
        residentCount: Int
    ): Result<Unit> {
        val trimmedUnitNumber = unitNumber.trim()
        if (trimmedUnitNumber.isBlank()) {
            return Result.failure(IllegalArgumentException("شماره واحد نمی‌تواند خالی باشد."))
        }
        if (areaSquareMeters <= 0) {
            return Result.failure(IllegalArgumentException("متراژ واحد باید بیشتر از صفر باشد."))
        }
        if (residentCount < 0) {
            return Result.failure(IllegalArgumentException("تعداد ساکنین نمی‌تواند منفی باشد."))
        }

        // Check for duplicate unit number in the same building
        val existingUnit = unitRepository.getUnitByBuildingAndNumber(buildingId, trimmedUnitNumber)
        if (existingUnit != null && existingUnit.isActive) {
            return Result.failure(IllegalStateException("واحدی با این شماره قبلاً در این ساختمان ثبت شده است."))
        }

        val currentTime = System.currentTimeMillis()
        val unit = Unit(
            id = UUID.randomUUID().toString(),
            buildingId = buildingId,
            unitNumber = trimmedUnitNumber,
            floor = floor,
            areaSquareMeters = areaSquareMeters,
            residentCount = residentCount,
            createdAt = currentTime,
            updatedAt = currentTime,
            isActive = true
        )

        unitRepository.insertUnit(unit)
        return Result.success(unit)
    }
}
