package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.Unit
import com.apyar.app.domain.repository.UnitRepository
import kotlinx.coroutines.flow.Flow

class GetUnitsByBuildingUseCase(
    private val unitRepository: UnitRepository
) {
    operator fun invoke(buildingId: String, onlyActive: Boolean = true): Flow<List<Unit>> {
        return unitRepository.getUnitsByBuilding(buildingId, onlyActive)
    }
}
