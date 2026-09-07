package com.apyar.app.test

import com.apyar.app.domain.model.AccountStatus
import com.apyar.app.domain.model.UserAccount
import com.apyar.app.domain.repository.UserAccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class FakeUserAccountRepository : UserAccountRepository {
    private val accounts = MutableStateFlow<Map<String, UserAccount>>(emptyMap())

    override suspend fun createUserAccount(userAccount: UserAccount): UserAccount {
        if (accounts.value.values.any { it.mobileNumber == userAccount.mobileNumber }) {
            throw IllegalArgumentException("شماره موبایل تکراری است.")
        }
        accounts.value = accounts.value + (userAccount.id to userAccount)
        return userAccount
    }

    override suspend fun getUserAccountById(id: String): UserAccount? {
        return accounts.value[id]
    }

    override suspend fun getUserAccountByMobile(mobileNumber: String): UserAccount? {
        return accounts.value.values.find { it.mobileNumber == mobileNumber }
    }

    override suspend fun getUserAccountByPersonId(personId: String): UserAccount? {
        return accounts.value.values.find { it.personId == personId }
    }

    override fun getAllUserAccounts(): Flow<List<UserAccount>> {
        return accounts.asStateFlow().map { it.values.toList() }
    }

    override suspend fun updateAccountStatus(id: String, status: AccountStatus) {
        val current = accounts.value[id] ?: return
        accounts.value = accounts.value + (id to current.copy(accountStatus = status, updatedAt = System.currentTimeMillis()))
    }

    override suspend fun updateLastLogin(id: String, timestamp: Long) {
        val current = accounts.value[id] ?: return
        accounts.value = accounts.value + (id to current.copy(lastLoginAt = timestamp, updatedAt = System.currentTimeMillis()))
    }
}
