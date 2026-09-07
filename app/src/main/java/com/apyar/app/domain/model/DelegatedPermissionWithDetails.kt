package com.apyar.app.domain.model

data class DelegatedPermissionWithDetails(
    val delegation: DelegatedPermission,
    val grantedByUser: UserAccount?,
    val grantedByPerson: Person?,
    val grantedToUser: UserAccount?,
    val grantedToPerson: Person?
)
