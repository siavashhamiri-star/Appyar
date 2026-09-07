package com.apyar.app.domain.repository

import com.apyar.app.domain.model.FinancialTransaction
import com.apyar.app.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow

interface FinancialTransactionRepository {
    suspend fun recordTransaction(transaction: FinancialTransaction)
    suspend fun getTransactionById(id: String): FinancialTransaction?
    suspend fun getTransactionsByBuildingId(buildingId: String): List<FinancialTransaction>
    fun observeTransactionsByBuildingId(buildingId: String): Flow<List<FinancialTransaction>>
    suspend fun getTransactionsByUnitId(unitId: String): List<FinancialTransaction>
    suspend fun getTransactionsByType(buildingId: String, type: TransactionType): List<FinancialTransaction>
}
