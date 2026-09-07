package com.apyar.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.apyar.app.data.local.entity.UserAccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserAccountDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUserAccount(account: UserAccountEntity)

    @Update
    suspend fun updateUserAccount(account: UserAccountEntity)

    @Query("SELECT * FROM user_accounts WHERE id = :id LIMIT 1")
    suspend fun getUserAccountById(id: String): UserAccountEntity?

    @Query("SELECT * FROM user_accounts WHERE mobileNumber = :mobileNumber LIMIT 1")
    suspend fun getUserAccountByMobile(mobileNumber: String): UserAccountEntity?

    @Query("SELECT * FROM user_accounts WHERE personId = :personId LIMIT 1")
    suspend fun getUserAccountByPersonId(personId: String): UserAccountEntity?

    @Query("SELECT * FROM user_accounts ORDER BY createdAt DESC")
    fun getAllUserAccounts(): Flow<List<UserAccountEntity>>

    @Query("UPDATE user_accounts SET accountStatus = :status, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateStatus(id: String, status: String, updatedAt: Long)

    @Query("UPDATE user_accounts SET lastLoginAt = :timestamp, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateLastLogin(id: String, timestamp: Long, updatedAt: Long)
}
