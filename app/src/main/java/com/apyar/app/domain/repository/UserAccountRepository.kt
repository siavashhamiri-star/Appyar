package com.apyar.app.domain.repository

import com.apyar.app.domain.model.AccountStatus
import com.apyar.app.domain.model.UserAccount
import kotlinx.coroutines.flow.Flow

interface UserAccountRepository {
    suspend fun createUserAccount(userAccount: UserAccount): UserAccount
    suspend fun getUserAccountById(id: String): UserAccount?
    suspend fun getUserAccountByMobile(mobileNumber: String): UserAccount?
    suspend fun getUserAccountByPersonId(personId: String): UserAccount?
    fun getAllUserAccounts(): Flow<List<UserAccount>>
    suspend fun updateAccountStatus(id: String, status: AccountStatus)
    suspend fun updateLastLogin(id: String, timestamp: Long)
}
