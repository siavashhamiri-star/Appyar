package com.apyar.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.apyar.app.data.local.entity.ServiceProviderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ServiceProviderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServiceProvider(provider: ServiceProviderEntity)

    @Update
    suspend fun updateServiceProvider(provider: ServiceProviderEntity)

    @Query("SELECT * FROM service_providers WHERE id = :id LIMIT 1")
    suspend fun getServiceProviderById(id: String): ServiceProviderEntity?

    @Query("SELECT * FROM service_providers WHERE mobileNumber = :mobileNumber LIMIT 1")
    suspend fun getServiceProviderByMobileNumber(mobileNumber: String): ServiceProviderEntity?

    @Query("SELECT * FROM service_providers ORDER BY name ASC")
    fun observeAllServiceProviders(): Flow<List<ServiceProviderEntity>>

    @Query("SELECT * FROM service_providers ORDER BY name ASC")
    suspend fun getAllServiceProviders(): List<ServiceProviderEntity>
}
