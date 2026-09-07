package com.apyar.app.domain.model

import java.util.UUID

/**
 * UserAccount represents the identity account of a user in the Apyar system.
 * It is tied to a Person, but contains credentials/auth metadata and account status.
 * Notice: Roles are NOT stored statically on UserAccount or Person. Roles are contextual
 * to buildings via BuildingMember.
 */
data class UserAccount(
    val id: String = UUID.randomUUID().toString(),
    val personId: String,
    val mobileNumber: String,
    val accountStatus: AccountStatus = AccountStatus.ACTIVE,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val lastLoginAt: Long? = null
)
