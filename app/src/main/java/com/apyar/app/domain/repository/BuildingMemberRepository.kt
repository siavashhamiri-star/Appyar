package com.apyar.app.domain.repository

import com.apyar.app.domain.model.BuildingMember
import com.apyar.app.domain.model.Role
import kotlinx.coroutines.flow.Flow

interface BuildingMemberRepository {
    suspend fun addMember(member: BuildingMember): BuildingMember
    suspend fun updateMemberRole(memberId: String, newRole: Role)
    suspend fun removeMember(memberId: String, endDate: Long = System.currentTimeMillis())
    fun getMembersByBuilding(buildingId: String): Flow<List<BuildingMember>>
    fun getMembershipsByUser(userId: String): Flow<List<BuildingMember>>
    suspend fun getMembership(buildingId: String, userId: String): BuildingMember?
    suspend fun getMemberById(id: String): BuildingMember?
}
