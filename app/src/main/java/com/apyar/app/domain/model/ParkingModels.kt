package com.apyar.app.domain.model

import java.util.UUID

/**
 * Types of parking spaces available in a building.
 */
enum class ParkingType(val titleFa: String) {
    NORMAL("سواری معمولی"),
    GUEST("پارکینگ میهمان"),
    DISABLED_ACCESS("معلولین / دسترس‌پذیر"),
    MOTORCYCLE("موتورسیکلت"),
    OTHER("سایر موارد")
}

/**
 * Physical/operational status of a parking space or storage unit.
 */
enum class SpaceStatus(val titleFa: String) {
    AVAILABLE("آزاد / تخصیص‌نیافته"),
    ASSIGNED("اختصاص‌یافته به واحد"),
    TEMPORARILY_USED("استفاده موقت"),
    INACTIVE("غیرفعال / خارج از سرویس")
}

/**
 * Types of assignment/use recorded in system.
 * Apyar does not act as a legal authority; it only registers reported assignments.
 */
enum class AssignmentType(val titleFa: String) {
    OWNER("ثبت‌شده به نام مالک واحد"),
    UNIT_ASSIGNED("تخصیص‌یافته به واحد"),
    RENTAL("اجاره / واگذاری دوره‌ای"),
    TEMPORARY("استفاده موقت توافقی"),
    SHARED("بهره‌برداری اشتراکی")
}

/**
 * Status representation reflecting legal neutrality.
 */
enum class LegalOccupancyStatus(val titleFa: String) {
    REGISTERED_OWNER("مالک ثبت‌شده در سامانه"),
    ASSIGNED_TO_UNIT("منتسب به واحد"),
    AUTHORIZED_USER("بهره‌بردار مجاز"),
    TEMPORARY_USER("استفاده‌کننده موقت"),
    RENTED_TO_UNIT("اجاره به واحد"),
    SHARED_USE("استفاده اشتراکی"),
    AVAILABLE("آزاد"),
    INACTIVE("غیرفعال")
}

/**
 * Domain entity representing a physical parking space.
 */
data class ParkingSpace(
    val id: String = UUID.randomUUID().toString(),
    val buildingId: String,
    val code: String,
    val floor: Int,
    val locationDescription: String? = null,
    val parkingType: ParkingType = ParkingType.NORMAL,
    val status: SpaceStatus = SpaceStatus.AVAILABLE,
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
)

/**
 * Assignment record for a parking space, maintaining complete historical trail.
 */
data class ParkingAssignment(
    val id: String = UUID.randomUUID().toString(),
    val parkingSpaceId: String,
    val unitId: String,
    val personId: String? = null,
    val assignmentType: AssignmentType = AssignmentType.UNIT_ASSIGNED,
    val startDate: Long = System.currentTimeMillis(),
    val endDate: Long? = null,
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
)

/**
 * Rich domain model combining parking space with current assignment and details.
 */
data class ParkingSpaceWithDetails(
    val space: ParkingSpace,
    val currentAssignment: ParkingAssignment? = null,
    val assignedUnitNumber: String? = null,
    val activeAgreement: Agreement? = null,
    val temporaryUserUnitNumber: String? = null,
    val assignmentHistory: List<ParkingAssignment> = emptyList()
)
