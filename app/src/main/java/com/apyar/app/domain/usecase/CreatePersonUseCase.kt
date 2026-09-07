package com.apyar.app.domain.usecase

import com.apyar.app.domain.model.Person
import com.apyar.app.domain.repository.PersonRepository
import java.util.UUID

class CreatePersonUseCase(
    private val personRepository: PersonRepository
) {
    suspend operator fun invoke(
        firstName: String,
        lastName: String,
        mobileNumber: String,
        email: String? = null
    ): Result<Person> {
        if (firstName.isBlank()) {
            return Result.failure(IllegalArgumentException("نام نمی‌تواند خالی باشد."))
        }
        if (lastName.isBlank()) {
            return Result.failure(IllegalArgumentException("نام خانوادگی نمی‌تواند خالی باشد."))
        }
        if (mobileNumber.isBlank()) {
            return Result.failure(IllegalArgumentException("شماره همراه نمی‌تواند خالی باشد."))
        }

        val currentTime = System.currentTimeMillis()
        val person = Person(
            id = UUID.randomUUID().toString(),
            firstName = firstName.trim(),
            lastName = lastName.trim(),
            mobileNumber = mobileNumber.trim(),
            email = email?.trim()?.ifBlank { null },
            createdAt = currentTime,
            updatedAt = currentTime,
            isActive = true
        )

        personRepository.insertPerson(person)
        return Result.success(person)
    }
}
