package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.Person
import com.apyar.app.domain.repository.PersonRepository
import kotlinx.coroutines.flow.Flow

class GetPeopleUseCase(
    private val personRepository: PersonRepository
) {
    operator fun invoke(onlyActive: Boolean = true): Flow<List<Person>> {
        return personRepository.getAllPeople(onlyActive)
    }
}
