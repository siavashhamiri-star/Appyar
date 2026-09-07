package com.apyar.app.domain.model

/**
 * RolePermissionPolicy defines the extensible mapping between Roles and their base Permissions.
 *
 * CRITICAL ARCHITECTURAL RULE:
 * APYAR_EXECUTIVE has NO default administrative or operational permissions (emptySet()).
 * An APYAR_EXECUTIVE user only receives permissions that are explicitly granted via DelegatedPermission.
 */
object RolePermissionPolicy {

    private val rolePermissionMap: Map<Role, Set<Permission>> = mapOf(
        Role.PLATFORM_ADMIN to Permission.entries.toSet(),

        Role.BUILDING_ADMIN to setOf(
            Permission.VIEW_BUILDING,
            Permission.EDIT_BUILDING,
            Permission.VIEW_BUILDING_SETTINGS,
            Permission.MANAGE_BUILDING_SETTINGS,
            Permission.VIEW_UNIT,
            Permission.EDIT_UNIT,
            Permission.VIEW_MEMBERS,
            Permission.MANAGE_MEMBERS,
            Permission.INVITE_MEMBER,
            Permission.REMOVE_MEMBER,
            Permission.VIEW_FINANCIAL_DATA,
            Permission.MANAGE_FINANCIAL_DATA,
            Permission.VIEW_DOCUMENTS,
            Permission.MANAGE_DOCUMENTS,
            Permission.VIEW_EVENT_LOG,
            Permission.CREATE_EVENT,
            Permission.VIEW_PARKING_DATA,
            Permission.MANAGE_PARKING_DATA,
            Permission.VIEW_STORAGE_DATA,
            Permission.MANAGE_STORAGE_DATA,
            Permission.VIEW_PRIVATE_AGREEMENTS,
            Permission.MANAGE_PRIVATE_AGREEMENTS,
            Permission.VIEW_SERVICE_PROVIDERS,
            Permission.MANAGE_SERVICE_PROVIDERS,
            Permission.VIEW_SERVICE_RECORDS,
            Permission.CREATE_SERVICE_RECORD,
            Permission.MANAGE_SERVICE_RECORDS,
            Permission.VIEW_MAINTENANCE,
            Permission.MANAGE_MAINTENANCE,
            Permission.VIEW_INVOICES,
            Permission.CREATE_INVOICE,
            Permission.MANAGE_INVOICES,
            Permission.VIEW_BUILDING_PAYMENTS,
            Permission.CREATE_BUILDING_PAYMENT,
            Permission.MANAGE_BUILDING_PAYMENTS,
            Permission.VIEW_BUILDING_BILLS,
            Permission.MANAGE_BUILDING_BILLS,
            Permission.MANAGE_EXECUTIVE_TASKS
        ),

        Role.BOARD_MEMBER to setOf(
            Permission.VIEW_BUILDING,
            Permission.VIEW_BUILDING_SETTINGS,
            Permission.VIEW_UNIT,
            Permission.VIEW_MEMBERS,
            Permission.VIEW_FINANCIAL_DATA,
            Permission.VIEW_DOCUMENTS,
            Permission.VIEW_EVENT_LOG,
            Permission.CREATE_EVENT,
            Permission.VIEW_PARKING_DATA,
            Permission.VIEW_STORAGE_DATA,
            Permission.VIEW_PRIVATE_AGREEMENTS,
            Permission.VIEW_SERVICE_PROVIDERS,
            Permission.VIEW_SERVICE_RECORDS,
            Permission.CREATE_SERVICE_RECORD,
            Permission.VIEW_MAINTENANCE,
            Permission.VIEW_INVOICES,
            Permission.VIEW_BUILDING_PAYMENTS,
            Permission.VIEW_BUILDING_BILLS
        ),

        Role.OWNER to setOf(
            Permission.VIEW_BUILDING,
            Permission.VIEW_UNIT,
            Permission.VIEW_MEMBERS,
            Permission.VIEW_FINANCIAL_DATA,
            Permission.VIEW_DOCUMENTS,
            Permission.VIEW_PARKING_DATA,
            Permission.VIEW_STORAGE_DATA,
            Permission.VIEW_PRIVATE_AGREEMENTS,
            Permission.VIEW_SERVICE_PROVIDERS,
            Permission.VIEW_SERVICE_RECORDS,
            Permission.CREATE_SERVICE_RECORD,
            Permission.VIEW_MAINTENANCE,
            Permission.VIEW_BUILDING_BILLS
        ),

        Role.TENANT to setOf(
            Permission.VIEW_BUILDING,
            Permission.VIEW_UNIT,
            Permission.VIEW_MEMBERS,
            Permission.VIEW_PARKING_DATA,
            Permission.VIEW_STORAGE_DATA,
            Permission.VIEW_SERVICE_PROVIDERS,
            Permission.VIEW_SERVICE_RECORDS,
            Permission.CREATE_SERVICE_RECORD
        ),

        Role.RESIDENT to setOf(
            Permission.VIEW_BUILDING,
            Permission.VIEW_UNIT,
            Permission.VIEW_PARKING_DATA,
            Permission.VIEW_STORAGE_DATA,
            Permission.VIEW_SERVICE_PROVIDERS,
            Permission.VIEW_SERVICE_RECORDS,
            Permission.CREATE_SERVICE_RECORD
        ),

        Role.ACCOUNTANT to setOf(
            Permission.VIEW_BUILDING,
            Permission.VIEW_UNIT,
            Permission.VIEW_FINANCIAL_DATA,
            Permission.MANAGE_FINANCIAL_DATA,
            Permission.VIEW_DOCUMENTS,
            Permission.VIEW_PARKING_DATA,
            Permission.VIEW_STORAGE_DATA,
            Permission.VIEW_PRIVATE_AGREEMENTS,
            Permission.VIEW_SERVICE_PROVIDERS,
            Permission.VIEW_SERVICE_RECORDS,
            Permission.VIEW_INVOICES,
            Permission.CREATE_INVOICE,
            Permission.MANAGE_INVOICES,
            Permission.VIEW_BUILDING_PAYMENTS,
            Permission.CREATE_BUILDING_PAYMENT,
            Permission.MANAGE_BUILDING_PAYMENTS,
            Permission.VIEW_BUILDING_BILLS,
            Permission.MANAGE_BUILDING_BILLS
        ),

        Role.CARETAKER to setOf(
            Permission.VIEW_BUILDING,
            Permission.VIEW_UNIT,
            Permission.VIEW_PARKING_DATA,
            Permission.VIEW_STORAGE_DATA,
            Permission.CREATE_EVENT,
            Permission.VIEW_SERVICE_PROVIDERS,
            Permission.VIEW_SERVICE_RECORDS,
            Permission.CREATE_SERVICE_RECORD,
            Permission.VIEW_MAINTENANCE,
            Permission.VIEW_BUILDING_BILLS
        ),

        // APYAR_EXECUTIVE has NO baseline automatic permissions.
        // It relies exclusively on explicit DelegatedPermission instances!
        Role.APYAR_EXECUTIVE to emptySet()
    )

    fun getPermissionsForRole(role: Role): Set<Permission> {
        return rolePermissionMap[role] ?: emptySet()
    }

    fun canRolePerform(role: Role, permission: Permission): Boolean {
        return getPermissionsForRole(role).contains(permission)
    }
}
