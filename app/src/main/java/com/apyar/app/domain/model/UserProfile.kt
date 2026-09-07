package com.apyar.app.domain.model

data class UserBuildingRole(
    val building: Building,
    val member: BuildingMember,
    val role: Role
)

data class UserProfile(
    val userAccount: UserAccount,
    val person: Person,
    val buildingRoles: List<UserBuildingRole>,
    val activeUnits: List<PersonUnitRelationWithDetails> = emptyList()
)
