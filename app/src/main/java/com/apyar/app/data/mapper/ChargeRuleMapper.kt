package com.apyar.app.data.mapper

import com.apyar.app.data.local.entity.ChargeRuleEntity
import com.apyar.app.domain.model.CalculationType
import com.apyar.app.domain.model.ChargeRule

fun ChargeRuleEntity.toDomain(): ChargeRule {
    val calcType = try {
        CalculationType.valueOf(calculationType)
    } catch (_: Exception) {
        CalculationType.EQUAL
    }

    return ChargeRule(
        id = id,
        buildingId = buildingId,
        name = name,
        description = description,
        calculationType = calcType,
        formulaDefinition = formulaDefinition,
        version = version,
        isActive = isActive,
        startDate = startDate,
        endDate = endDate,
        createdAt = createdAt,
        updatedAt = updatedAt,
        createdBy = createdBy
    )
}

fun ChargeRule.toEntity(): ChargeRuleEntity {
    return ChargeRuleEntity(
        id = id,
        buildingId = buildingId,
        name = name,
        description = description,
        calculationType = calculationType.name,
        formulaDefinition = formulaDefinition,
        version = version,
        isActive = isActive,
        startDate = startDate,
        endDate = endDate,
        createdAt = createdAt,
        updatedAt = updatedAt,
        createdBy = createdBy
    )
}
