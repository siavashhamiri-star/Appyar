package com.apyar.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.apyar.app.data.local.entity.AuditEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AuditEventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: AuditEventEntity)

    @Query("SELECT * FROM audit_events WHERE buildingId = :buildingId ORDER BY timestamp DESC")
    fun getEventsByBuilding(buildingId: String): Flow<List<AuditEventEntity>>

    @Query("SELECT * FROM audit_events WHERE actor = :actor ORDER BY timestamp DESC")
    fun getEventsByActor(actor: String): Flow<List<AuditEventEntity>>
}
