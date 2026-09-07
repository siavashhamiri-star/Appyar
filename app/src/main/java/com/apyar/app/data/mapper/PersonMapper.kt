package com.apyar.app.data.mapper

import com.apyar.app.data.local.entity.PersonEntity
import com.apyar.app.domain.model.Person

fun PersonEntity.toDomain(): Person {
    return Person(
        id = id,
        firstName = firstName,
        lastName = lastName,
        mobileNumber = mobileNumber,
        email = email,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isActive = isActive
    )
}

fun Person.toEntity(): PersonEntity {
    return PersonEntity(
        id = id,
        firstName = firstName,
        lastName = lastName,
        mobileNumber = mobileNumber,
        email = email,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isActive = isActive
    )
}
