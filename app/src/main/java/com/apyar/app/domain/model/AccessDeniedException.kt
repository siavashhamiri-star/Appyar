package com.apyar.app.domain.model

/**
 * AccessDeniedException is thrown when an operation is attempted without the required permissions
 * in the specific building context.
 */
class AccessDeniedException(
    val requiredPermission: Permission,
    val userId: String,
    val buildingId: String?,
    override val message: String = "دسترسی غیرمجاز (Access Denied): شما مجوز لازم برای انجام این عملیات را ندارید."
) : SecurityException(message)
