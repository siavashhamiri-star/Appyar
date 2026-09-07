package com.apyar.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.apyar.app.data.local.entity.BuildingServiceProviderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BuildingServiceProviderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBuildingServiceProvider(link: BuildingServiceProviderEntity)

    @Update
    suspend fun updateBuildingServiceProvider(link: BuildingServiceProviderEntity)

    @Query("SELECT * FROM building_service_providers WHERE id = :id LIMIT 1")
    suspend fun getBuildingServiceProviderById(id: String): BuildingServiceProviderEntity?

    @Query("SELECT * FROM building_service_providers WHERE buildingId = :buildingId AND serviceProviderId = :serviceProviderId LIMIT 1")
    suspend fun getLinkByBuildingAndProvider(buildingId: String, serviceProviderId: String): BuildingServiceProviderEntity?

    @Query("SELECT * FROM building_service_providers WHERE buildingId = :buildingId ORDER BY isPrimary DESC, role ASC")
    fun observeBuildingServiceProviders(buildingId: String): Flow<List<BuildingServiceProviderEntity>>

    @Query("SELECT * FROM building_service_providers WHERE buildingId = :buildingId ORDER BY isPrimary DESC, role ASC")
    suspend fun getBuildingServiceProviders(buildingId: String): List<BuildingServiceProviderEntity>

    @Query("SELECT * FROM building_service_providers WHERE buildingId = :buildingId AND serviceCategory = :category ORDER BY isPrimary DESC")
    suspend fun getProvidersByCategory(buildingId: String, category: String): List<BuildingServiceProviderEntity>

    @Query("SELECT * FROM building_service_providers WHERE buildingId = :buildingId AND serviceCategory = :category AND isPrimary = 1 AND isActive = 1 LIMIT 1")
    suspend fun getPrimaryProviderForCategory(buildingId: String, category: String): BuildingServiceProviderEntity?
}
