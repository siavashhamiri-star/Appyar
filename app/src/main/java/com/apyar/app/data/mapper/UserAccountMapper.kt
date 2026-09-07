package com.apyar.app.data.mapper

import com.apyar.app.data.local.entity.UserAccountEntity
import com.apyar.app.domain.model.AccountStatus
import com.apyar.app.domain.model.UserAccount

fun UserAccountEntity.toDomain(): UserAccount {
    return UserAccount(
        id = id,
        personId = personId,
        mobileNumber = mobileNumber,
        accountStatus = try {
            AccountStatus.valueOf(accountStatus)
        } catch (e: Exception) {
            AccountStatus.ACTIVE
        },
        createdAt = createdAt,
        updatedAt = updatedAt,
        lastLoginAt = lastLoginAt
    )
}

fun UserAccount.toEntity(): UserAccountEntity {
    return UserAccountEntity(
        id = id,
        personId = personId,
        mobileNumber = mobileNumber,
        accountStatus = accountStatus.name,
        createdAt = createdAt,
        updatedAt = updatedAt,
        lastLoginAt = lastLoginAt
    )
}
