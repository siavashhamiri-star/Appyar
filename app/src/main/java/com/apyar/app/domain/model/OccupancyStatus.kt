package com.apyar.app.domain.model

/**
 * Represents the occupancy status of a unit.
 * Independent of ownership, tenancy, or active status.
 */
enum class OccupancyStatus(val titleFa: String) {
    OCCUPIED("دارای سکونت (اشغال‌شده)"),
    VACANT("خالی از سکونت"),
    UNKNOWN("نامشخص")
}
