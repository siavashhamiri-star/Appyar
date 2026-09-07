package com.apyar.app.data.repository

import com.apyar.app.data.local.dao.FinancialAccountDao
import com.apyar.app.data.mapper.toDomain
import com.apyar.app.data.mapper.toEntity
import com.apyar.app.domain.model.BuildingFinancialAccount
import com.apyar.app.domain.repository.FinancialAccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FinancialAccountRepositoryImpl(
    private val financialAccountDao: FinancialAccountDao
) : FinancialAccountRepository {

    override suspend fun createAccount(account: BuildingFinancialAccount) {
        financialAccountDao.insertAccount(account.toEntity())
    }

    override suspend fun getAccountById(id: String): BuildingFinancialAccount? {
        return financialAccountDao.getAccountById(id)?.toDomain()
    }

    override suspend fun getAccountByBuildingId(buildingId: String): BuildingFinancialAccount? {
        return financialAccountDao.getAccountByBuildingId(buildingId)?.toDomain()
    }

    override fun observeAccountByBuildingId(buildingId: String): Flow<BuildingFinancialAccount?> {
        return financialAccountDao.observeAccountByBuildingId(buildingId).map { it?.toDomain() }
    }

    override suspend fun updateBalance(accountId: String, newBalance: Long) {
        financialAccountDao.updateBalance(accountId, newBalance)
    }
}
