package com.apyar.app.data.mapper

import com.apyar.app.data.local.entity.FinancialAccountEntity
import com.apyar.app.domain.model.BuildingFinancialAccount

fun FinancialAccountEntity.toDomain(): BuildingFinancialAccount {
    return BuildingFinancialAccount(
        id = id,
        buildingId = buildingId,
        accountName = accountName,
        currency = currency,
        balance = balance,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isActive = isActive
    )
}

fun BuildingFinancialAccount.toEntity(): FinancialAccountEntity {
    return FinancialAccountEntity(
        id = id,
        buildingId = buildingId,
        accountName = accountName,
        currency = currency,
        balance = balance,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isActive = isActive
    )
}
