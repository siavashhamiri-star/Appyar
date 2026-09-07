package com.apyar.app.domain.repository

import com.apyar.app.domain.model.BuildingFinancialAccount
import kotlinx.coroutines.flow.Flow

interface FinancialAccountRepository {
    suspend fun createAccount(account: BuildingFinancialAccount)
    suspend fun getAccountById(id: String): BuildingFinancialAccount?
    suspend fun getAccountByBuildingId(buildingId: String): BuildingFinancialAccount?
    fun observeAccountByBuildingId(buildingId: String): Flow<BuildingFinancialAccount?>
    suspend fun updateBalance(accountId: String, newBalance: Long)
}
