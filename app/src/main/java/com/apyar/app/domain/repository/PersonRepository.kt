package com.apyar.app.domain.repository

import com.apyar.app.domain.model.Person
import com.apyar.app.domain.model.UnitOccupantInfo
import com.apyar.app.domain.model.UnitPersonRelation
import kotlinx.coroutines.flow.Flow

interface PersonRepository {
    fun getAllPeople(onlyActive: Boolean = true): Flow<List<Person>>
    suspend fun getPersonById(id: String): Person?
    suspend fun insertPerson(person: Person): Long
    suspend fun updatePerson(person: Person)
    
    // Relations
    fun getOccupantsByUnit(unitId: String): Flow<List<UnitOccupantInfo>>
    suspend fun insertRelation(relation: UnitPersonRelation): Long
    suspend fun endRelation(relationId: String, endDate: Long = System.currentTimeMillis())
}
