package com.apyar.app.test

import com.apyar.app.domain.model.Person
import com.apyar.app.domain.model.UnitOccupantInfo
import com.apyar.app.domain.model.UnitPersonRelation
import com.apyar.app.domain.repository.PersonRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakePersonRepository : PersonRepository {
    private val peopleFlow = MutableStateFlow<List<Person>>(emptyList())
    private val relationsFlow = MutableStateFlow<List<UnitPersonRelation>>(emptyList())

    override fun getAllPeople(onlyActive: Boolean): Flow<List<Person>> {
        return peopleFlow.map { list ->
            if (onlyActive) list.filter { it.isActive } else list
        }
    }

    override suspend fun getPersonById(id: String): Person? {
        return peopleFlow.value.firstOrNull { it.id == id }
    }

    override suspend fun insertPerson(person: Person): Long {
        peopleFlow.value = peopleFlow.value + person
        return 1L
    }

    override suspend fun updatePerson(person: Person) {
        peopleFlow.value = peopleFlow.value.map {
            if (it.id == person.id) person else it
        }
    }

    override fun getOccupantsByUnit(unitId: String): Flow<List<UnitOccupantInfo>> {
        return relationsFlow.map { relations ->
            relations.filter { it.unitId == unitId }.mapNotNull { rel ->
                val person = peopleFlow.value.firstOrNull { it.id == rel.personId }
                if (person != null) UnitOccupantInfo(rel, person) else null
            }
        }
    }

    override suspend fun insertRelation(relation: UnitPersonRelation): Long {
        relationsFlow.value = relationsFlow.value + relation
        return 1L
    }

    override suspend fun endRelation(relationId: String, endDate: Long) {
        relationsFlow.value = relationsFlow.value.map {
            if (it.id == relationId) it.copy(isActive = false, endDate = endDate) else it
        }
    }
}
