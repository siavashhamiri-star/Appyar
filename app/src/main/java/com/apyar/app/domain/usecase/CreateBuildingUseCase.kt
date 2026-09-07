package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.Building
import com.apyar.app.domain.repository.BuildingRepository
import java.util.UUID

class CreateBuildingUseCase(
    private val buildingRepository: BuildingRepository
) {
    suspend operator fun invoke(
        name: String,
        address: String,
        city: String,
        postalCode: String,
        unitCount: Int
    ): Result<Building> {
        if (name.isBlank()) {
            return Result.failure(IllegalArgumentException("نام ساختمان نمی‌تواند خالی باشد."))
        }
        if (unitCount < 0) {
            return Result.failure(IllegalArgumentException("تعداد واحدها نمی‌تواند منفی باشد."))
        }

        val currentTime = System.currentTimeMillis()
        val building = Building(
            id = UUID.randomUUID().toString(),
            name = name.trim(),
            address = address.trim(),
            city = city.trim(),
            postalCode = postalCode.trim(),
            unitCount = unitCount,
            createdAt = currentTime,
            updatedAt = currentTime,
            isActive = true
        )

        buildingRepository.insertBuilding(building)
        return Result.success(building)
    }
}
