package com.apyar.app.test

import com.apyar.app.domain.model.FinancialTransaction
import com.apyar.app.domain.model.TransactionType
import com.apyar.app.domain.repository.FinancialTransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class FakeFinancialTransactionRepository : FinancialTransactionRepository {
    private val transactions = MutableStateFlow<List<FinancialTransaction>>(emptyList())

    override suspend fun recordTransaction(transaction: FinancialTransaction) {
        transactions.value = listOf(transaction) + transactions.value
    }

    override suspend fun getTransactionById(id: String): FinancialTransaction? {
        return transactions.value.find { it.id == id }
    }

    override suspend fun getTransactionsByBuildingId(buildingId: String): List<FinancialTransaction> {
        return transactions.value.filter { it.buildingId == buildingId }
    }

    override fun observeTransactionsByBuildingId(buildingId: String): Flow<List<FinancialTransaction>> {
        return transactions.asStateFlow().map { list ->
            list.filter { it.buildingId == buildingId }
        }
    }

    override suspend fun getTransactionsByUnitId(unitId: String): List<FinancialTransaction> {
        return transactions.value.filter { it.unitId == unitId }
    }

    override suspend fun getTransactionsByType(
        buildingId: String,
        type: TransactionType
    ): List<FinancialTransaction> {
        return transactions.value.filter { it.buildingId == buildingId && it.type == type }
    }
}
