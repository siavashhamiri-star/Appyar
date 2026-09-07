package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.UnitOccupantInfo
import com.apyar.app.domain.repository.PersonRepository
import kotlinx.coroutines.flow.Flow

class GetPeopleByUnitUseCase(
    private val personRepository: PersonRepository
) {
    operator fun invoke(unitId: String): Flow<List<UnitOccupantInfo>> {
        return personRepository.getOccupantsByUnit(unitId)
    }
}
