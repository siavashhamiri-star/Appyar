package com.apyar.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.apyar.app.data.local.entity.BuildingMemberEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BuildingMemberDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: BuildingMemberEntity)

    @Update
    suspend fun updateMember(member: BuildingMemberEntity)

    @Query("SELECT * FROM building_members WHERE id = :id LIMIT 1")
    suspend fun getMemberById(id: String): BuildingMemberEntity?

    @Query("SELECT * FROM building_members WHERE buildingId = :buildingId AND userId = :userId AND isActive = 1 LIMIT 1")
    suspend fun getMembership(buildingId: String, userId: String): BuildingMemberEntity?

    @Query("SELECT * FROM building_members WHERE buildingId = :buildingId AND isActive = 1 ORDER BY createdAt ASC")
    fun getMembersByBuilding(buildingId: String): Flow<List<BuildingMemberEntity>>

    @Query("SELECT * FROM building_members WHERE userId = :userId AND isActive = 1 ORDER BY createdAt DESC")
    fun getMembershipsByUser(userId: String): Flow<List<BuildingMemberEntity>>

    @Query("UPDATE building_members SET role = :newRole, updatedAt = :updatedAt WHERE id = :memberId")
    suspend fun updateMemberRole(memberId: String, newRole: String, updatedAt: Long)

    @Query("UPDATE building_members SET isActive = 0, endDate = :endDate, updatedAt = :updatedAt WHERE id = :memberId")
    suspend fun deactivateMember(memberId: String, endDate: Long, updatedAt: Long)
}
