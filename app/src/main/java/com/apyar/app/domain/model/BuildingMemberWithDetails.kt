package com.apyar.app.domain.model

data class BuildingMemberWithDetails(
    val member: BuildingMember,
    val userAccount: UserAccount?,
    val person: Person?
)
