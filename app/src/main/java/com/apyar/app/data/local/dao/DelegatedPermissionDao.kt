package com.apyar.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.apyar.app.data.local.entity.DelegatedPermissionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DelegatedPermissionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDelegation(delegation: DelegatedPermissionEntity)

    @Update
    suspend fun updateDelegation(delegation: DelegatedPermissionEntity)

    @Query("SELECT * FROM delegated_permissions WHERE id = :id LIMIT 1")
    suspend fun getDelegationById(id: String): DelegatedPermissionEntity?

    @Query("SELECT * FROM delegated_permissions WHERE buildingId = :buildingId ORDER BY createdAt DESC")
    fun getDelegationsByBuilding(buildingId: String): Flow<List<DelegatedPermissionEntity>>

    @Query("SELECT * FROM delegated_permissions WHERE buildingId = :buildingId AND grantedToUserId = :userId ORDER BY createdAt DESC")
    fun getDelegationsForUser(buildingId: String, userId: String): Flow<List<DelegatedPermissionEntity>>

    @Query("SELECT * FROM delegated_permissions WHERE buildingId = :buildingId AND grantedToUserId = :userId AND status = 'ACTIVE' AND startDate <= :currentTime AND endDate >= :currentTime")
    suspend fun getActiveDelegationsForUser(buildingId: String, userId: String, currentTime: Long): List<DelegatedPermissionEntity>

    @Query("UPDATE delegated_permissions SET status = 'REVOKED' WHERE id = :id")
    suspend fun revokeDelegation(id: String)
}
