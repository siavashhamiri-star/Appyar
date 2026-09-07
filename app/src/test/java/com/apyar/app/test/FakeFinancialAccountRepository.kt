package com.apyar.app.test

import com.apyar.app.domain.model.BuildingFinancialAccount
import com.apyar.app.domain.repository.FinancialAccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class FakeFinancialAccountRepository : FinancialAccountRepository {
    private val accounts = MutableStateFlow<Map<String, BuildingFinancialAccount>>(emptyMap())

    override suspend fun createAccount(account: BuildingFinancialAccount) {
        accounts.value = accounts.value + (account.id to account)
    }

    override suspend fun getAccountById(id: String): BuildingFinancialAccount? {
        return accounts.value[id]
    }

    override suspend fun getAccountByBuildingId(buildingId: String): BuildingFinancialAccount? {
        return accounts.value.values.find { it.buildingId == buildingId && it.isActive }
    }

    override fun observeAccountByBuildingId(buildingId: String): Flow<BuildingFinancialAccount?> {
        return accounts.asStateFlow().map { map ->
            map.values.find { it.buildingId == buildingId && it.isActive }
        }
    }

    override suspend fun updateBalance(accountId: String, newBalance: Long) {
        val current = accounts.value[accountId] ?: return
        accounts.value = accounts.value + (accountId to current.copy(balance = newBalance, updatedAt = System.currentTimeMillis()))
    }
}
