package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.Unit
import com.apyar.app.domain.repository.UnitRepository

class GetUnitByIdUseCase(
    private val unitRepository: UnitRepository
) {
    suspend operator fun invoke(id: String): Unit? {
        return unitRepository.getUnitById(id)
    }
}
