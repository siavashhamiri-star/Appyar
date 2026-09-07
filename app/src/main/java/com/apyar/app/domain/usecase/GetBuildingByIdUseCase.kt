package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.Building
import com.apyar.app.domain.repository.BuildingRepository

class GetBuildingByIdUseCase(
    private val buildingRepository: BuildingRepository
) {
    suspend operator fun invoke(id: String): Building? {
        return buildingRepository.getBuildingById(id)
    }
}
