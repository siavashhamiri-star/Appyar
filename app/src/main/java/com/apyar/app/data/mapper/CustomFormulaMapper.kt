package com.apyar.app.data.mapper

import com.apyar.app.data.local.entity.CustomFormulaEntity
import com.apyar.app.domain.model.CustomFormula

fun CustomFormulaEntity.toDomain(): CustomFormula {
    return CustomFormula(
        id = id,
        buildingId = buildingId,
        name = name,
        description = description,
        definition = definition,
        version = version,
        approvedAt = approvedAt,
        approvedBy = approvedBy,
        effectiveFrom = effectiveFrom,
        isActive = isActive
    )
}

fun CustomFormula.toEntity(): CustomFormulaEntity {
    return CustomFormulaEntity(
        id = id,
        buildingId = buildingId,
        name = name,
        description = description,
        definition = definition,
        version = version,
        approvedAt = approvedAt,
        approvedBy = approvedBy,
        effectiveFrom = effectiveFrom,
        isActive = isActive
    )
}
