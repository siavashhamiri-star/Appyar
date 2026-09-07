package com.apyar.app.data.mapper

import com.apyar.app.data.local.entity.FinancialTransactionEntity
import com.apyar.app.domain.model.ExpenseCategory
import com.apyar.app.domain.model.FinancialTransaction
import com.apyar.app.domain.model.TransactionType

fun FinancialTransactionEntity.toDomain(): FinancialTransaction {
    val transactionType = try {
        TransactionType.valueOf(type)
    } catch (_: Exception) {
        TransactionType.INCOME
    }

    val expenseCategory = category?.let { cat ->
        try {
            ExpenseCategory.valueOf(cat)
        } catch (_: Exception) {
            null
        }
    }

    return FinancialTransaction(
        id = id,
        buildingId = buildingId,
        accountId = accountId,
        unitId = unitId,
        type = transactionType,
        amount = amount,
        description = description,
        reference = reference,
        category = expenseCategory,
        createdAt = createdAt,
        createdBy = createdBy
    )
}

fun FinancialTransaction.toEntity(): FinancialTransactionEntity {
    return FinancialTransactionEntity(
        id = id,
        buildingId = buildingId,
        accountId = accountId,
        unitId = unitId,
        type = type.name,
        amount = amount,
        description = description,
        reference = reference,
        category = category?.name,
        createdAt = createdAt,
        createdBy = createdBy
    )
}
