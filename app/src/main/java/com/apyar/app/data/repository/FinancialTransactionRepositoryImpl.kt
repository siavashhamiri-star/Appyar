package com.apyar.app.data.repository

import com.apyar.app.data.local.dao.FinancialTransactionDao
import com.apyar.app.data.mapper.toDomain
import com.apyar.app.data.mapper.toEntity
import com.apyar.app.domain.model.FinancialTransaction
import com.apyar.app.domain.model.TransactionType
import com.apyar.app.domain.repository.FinancialTransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FinancialTransactionRepositoryImpl(
    private val financialTransactionDao: FinancialTransactionDao
) : FinancialTransactionRepository {

    override suspend fun recordTransaction(transaction: FinancialTransaction) {
        financialTransactionDao.insertTransaction(transaction.toEntity())
    }

    override suspend fun getTransactionById(id: String): FinancialTransaction? {
        return financialTransactionDao.getTransactionById(id)?.toDomain()
    }

    override suspend fun getTransactionsByBuildingId(buildingId: String): List<FinancialTransaction> {
        return financialTransactionDao.getTransactionsByBuildingId(buildingId).map { it.toDomain() }
    }

    override fun observeTransactionsByBuildingId(buildingId: String): Flow<List<FinancialTransaction>> {
        return financialTransactionDao.observeTransactionsByBuildingId(buildingId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getTransactionsByUnitId(unitId: String): List<FinancialTransaction> {
        return financialTransactionDao.getTransactionsByUnitId(unitId).map { it.toDomain() }
    }

    override suspend fun getTransactionsByType(buildingId: String, type: TransactionType): List<FinancialTransaction> {
        return financialTransactionDao.getTransactionsByType(buildingId, type.name).map { it.toDomain() }
    }
}
