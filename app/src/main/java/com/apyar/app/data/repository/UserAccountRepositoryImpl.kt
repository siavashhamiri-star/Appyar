package com.apyar.app.data.repository

import com.apyar.app.data.local.dao.UserAccountDao
import com.apyar.app.data.mapper.toDomain
import com.apyar.app.data.mapper.toEntity
import com.apyar.app.domain.model.AccountStatus
import com.apyar.app.domain.model.UserAccount
import com.apyar.app.domain.repository.UserAccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserAccountRepositoryImpl(
    private val dao: UserAccountDao
) : UserAccountRepository {

    override suspend fun createUserAccount(userAccount: UserAccount): UserAccount {
        dao.insertUserAccount(userAccount.toEntity())
        return userAccount
    }

    override suspend fun getUserAccountById(id: String): UserAccount? {
        return dao.getUserAccountById(id)?.toDomain()
    }

    override suspend fun getUserAccountByMobile(mobileNumber: String): UserAccount? {
        return dao.getUserAccountByMobile(mobileNumber)?.toDomain()
    }

    override suspend fun getUserAccountByPersonId(personId: String): UserAccount? {
        return dao.getUserAccountByPersonId(personId)?.toDomain()
    }

    override fun getAllUserAccounts(): Flow<List<UserAccount>> {
        return dao.getAllUserAccounts().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun updateAccountStatus(id: String, status: AccountStatus) {
        dao.updateStatus(id, status.name, System.currentTimeMillis())
    }

    override suspend fun updateLastLogin(id: String, timestamp: Long) {
        dao.updateLastLogin(id, timestamp, System.currentTimeMillis())
    }
}
