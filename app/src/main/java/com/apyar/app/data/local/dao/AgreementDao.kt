package com.apyar.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.apyar.app.data.local.entity.AgreementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AgreementDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAgreement(agreement: AgreementEntity)

    @Update
    suspend fun updateAgreement(agreement: AgreementEntity)

    @Query("SELECT * FROM agreements WHERE id = :id LIMIT 1")
    suspend fun getAgreementById(id: String): AgreementEntity?

    @Query("SELECT * FROM agreements WHERE buildingId = :buildingId ORDER BY startDate DESC")
    fun observeAgreementsByBuildingId(buildingId: String): Flow<List<AgreementEntity>>

    @Query("SELECT * FROM agreements WHERE buildingId = :buildingId ORDER BY startDate DESC")
    suspend fun getAgreementsByBuildingId(buildingId: String): List<AgreementEntity>

    @Query("SELECT * FROM agreements WHERE sourceUnitId = :unitId OR targetUnitId = :unitId ORDER BY startDate DESC")
    fun observeAgreementsByUnitId(unitId: String): Flow<List<AgreementEntity>>

    @Query("SELECT * FROM agreements WHERE relatedParkingId = :parkingId AND status = 'ACTIVE'")
    suspend fun getActiveAgreementsForParking(parkingId: String): List<AgreementEntity>

    @Query("SELECT * FROM agreements WHERE relatedStorageId = :storageId AND status = 'ACTIVE'")
    suspend fun getActiveAgreementsForStorage(storageId: String): List<AgreementEntity>
}
