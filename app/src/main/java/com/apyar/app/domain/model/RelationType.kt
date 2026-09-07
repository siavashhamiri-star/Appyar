package com.apyar.app.domain.model

/**
 * Type of relationship a Person has with a Unit.
 */
enum class RelationType {
    OWNER,
    TENANT,
    RESIDENT;

    companion object {
        fun fromString(value: String): RelationType {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: RESIDENT
        }
    }
}
