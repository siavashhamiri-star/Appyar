package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.Building
import com.apyar.app.domain.repository.BuildingRepository
import kotlinx.coroutines.flow.Flow

class GetBuildingsUseCase(
    private val buildingRepository: BuildingRepository
) {
    operator fun invoke(onlyActive: Boolean = true): Flow<List<Building>> {
        return buildingRepository.getBuildings(onlyActive)
    }
}
