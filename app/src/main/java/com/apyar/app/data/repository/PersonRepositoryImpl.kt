package com.apyar.app.data.repository

import com.apyar.app.data.local.dao.PersonDao
import com.apyar.app.data.local.dao.UnitPersonRelationDao
import com.apyar.app.data.mapper.toDomain
import com.apyar.app.data.mapper.toEntity
import com.apyar.app.domain.model.Person
import com.apyar.app.domain.model.UnitOccupantInfo
import com.apyar.app.domain.model.UnitPersonRelation
import com.apyar.app.domain.repository.PersonRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PersonRepositoryImpl(
    private val personDao: PersonDao,
    private val relationDao: UnitPersonRelationDao
) : PersonRepository {

    override fun getAllPeople(onlyActive: Boolean): Flow<List<Person>> {
        return personDao.getAllPeople(onlyActive).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getPersonById(id: String): Person? {
        return personDao.getPersonById(id)?.toDomain()
    }

    override suspend fun insertPerson(person: Person): Long {
        return personDao.insertPerson(person.toEntity())
    }

    override suspend fun updatePerson(person: Person) {
        personDao.updatePerson(person.toEntity())
    }

    override fun getOccupantsByUnit(unitId: String): Flow<List<UnitOccupantInfo>> {
        return relationDao.getRelationsWithPersonByUnit(unitId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun insertRelation(relation: UnitPersonRelation): Long {
        return relationDao.insertRelation(relation.toEntity())
    }

    override suspend fun endRelation(relationId: String, endDate: Long) {
        relationDao.endRelation(relationId, endDate)
    }
}
