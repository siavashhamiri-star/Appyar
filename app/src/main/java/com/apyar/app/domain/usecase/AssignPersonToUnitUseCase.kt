package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.RelationType
import com.apyar.app.domain.model.UnitPersonRelation
import com.apyar.app.domain.repository.PersonRepository
import java.util.UUID

class AssignPersonToUnitUseCase(
    private val personRepository: PersonRepository
) {
    suspend operator fun invoke(
        unitId: String,
        personId: String,
        relationType: RelationType,
        startDate: Long = System.currentTimeMillis(),
        endDate: Long? = null
    ): Result<UnitPersonRelation> {
        if (unitId.isBlank()) {
            return Result.failure(IllegalArgumentException("شناسه واحد نامعتبر است."))
        }
        if (personId.isBlank()) {
            return Result.failure(IllegalArgumentException("شناسه شخص نامعتبر است."))
        }

        val relation = UnitPersonRelation(
            id = UUID.randomUUID().toString(),
            unitId = unitId,
            personId = personId,
            relationType = relationType,
            startDate = startDate,
            endDate = endDate,
            isActive = true
        )

        personRepository.insertRelation(relation)
        return Result.success(relation)
    }
}
