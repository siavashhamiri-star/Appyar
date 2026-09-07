package com.apyar.app.data.mapper

import com.apyar.app.data.local.entity.ChargePeriodEntity
import com.apyar.app.domain.model.ChargePeriod
import com.apyar.app.domain.model.ChargePeriodStatus

fun ChargePeriodEntity.toDomain(): ChargePeriod {
    val periodStatus = try {
        ChargePeriodStatus.valueOf(status)
    } catch (_: Exception) {
        ChargePeriodStatus.DRAFT
    }

    return ChargePeriod(
        id = id,
        buildingId = buildingId,
        title = title,
        startDate = startDate,
        endDate = endDate,
        totalAmount = totalAmount,
        chargeRuleId = chargeRuleId,
        formulaVersion = formulaVersion,
        status = periodStatus,
        createdAt = createdAt,
        finalizedAt = finalizedAt,
        createdBy = createdBy
    )
}

fun ChargePeriod.toEntity(): ChargePeriodEntity {
    return ChargePeriodEntity(
        id = id,
        buildingId = buildingId,
        title = title,
        startDate = startDate,
        endDate = endDate,
        totalAmount = totalAmount,
        chargeRuleId = chargeRuleId,
        formulaVersion = formulaVersion,
        status = status.name,
        createdAt = createdAt,
        finalizedAt = finalizedAt,
        createdBy = createdBy
    )
}
