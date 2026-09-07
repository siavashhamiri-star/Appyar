package com.apyar.app.data.local.dao

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Update
import com.apyar.app.data.local.entity.PersonEntity
import com.apyar.app.data.local.entity.UnitPersonRelationEntity
import kotlinx.coroutines.flow.Flow

data class UnitOccupantRelationWithPerson(
    @Embedded val relation: UnitPersonRelationEntity,
    @Relation(
        parentColumn = "personId",
        entityColumn = "id"
    )
    val person: PersonEntity
)

@Dao
interface UnitPersonRelationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRelation(relation: UnitPersonRelationEntity): Long

    @Update
    suspend fun updateRelation(relation: UnitPersonRelationEntity)

    @Query("""
        SELECT * FROM unit_person_relations 
        WHERE unitId = :unitId 
        ORDER BY isActive DESC, startDate DESC
    """)
    fun getRelationsWithPersonByUnit(unitId: String): Flow<List<UnitOccupantRelationWithPerson>>

    @Query("UPDATE unit_person_relations SET isActive = 0, endDate = :endDate WHERE id = :relationId")
    suspend fun endRelation(relationId: String, endDate: Long)
}
