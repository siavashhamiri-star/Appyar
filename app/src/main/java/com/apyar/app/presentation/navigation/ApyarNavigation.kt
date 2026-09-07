package com.apyar.app.presentation.navigation

sealed class Screen(val route: String) {
    data object Buildings : Screen("buildings")
    
    data object BuildingDetail : Screen("building/{buildingId}") {
        fun createRoute(buildingId: String): String = "building/$buildingId"
    }

    data object UnitDetail : Screen("unit/{unitId}") {
        fun createRoute(unitId: String): String = "unit/$unitId"
    }

    data object UserProfile : Screen("user_profile/{userId}") {
        fun createRoute(userId: String): String = "user_profile/$userId"
    }

    data object BuildingMembers : Screen("building/{buildingId}/members") {
        fun createRoute(buildingId: String): String = "building/$buildingId/members"
    }

    data object Permissions : Screen("building/{buildingId}/permissions/{userId}") {
        fun createRoute(buildingId: String, userId: String): String = "building/$buildingId/permissions/$userId"
    }

    data object Delegations : Screen("building/{buildingId}/delegations") {
        fun createRoute(buildingId: String): String = "building/$buildingId/delegations"
    }

    // Stage 3 Financial Screens
    data object FinancialDashboard : Screen("building/{buildingId}/financial") {
        fun createRoute(buildingId: String): String = "building/$buildingId/financial"
    }

    data object ChargeCalculation : Screen("building/{buildingId}/charge_calculation") {
        fun createRoute(buildingId: String): String = "building/$buildingId/charge_calculation"
    }

    data object Transactions : Screen("building/{buildingId}/transactions") {
        fun createRoute(buildingId: String): String = "building/$buildingId/transactions"
    }

    // Stage 4 Parking, Storage & Agreements Screens
    data object Parking : Screen("building/{buildingId}/parking") {
        fun createRoute(buildingId: String): String = "building/$buildingId/parking"
    }

    data object Storage : Screen("building/{buildingId}/storage") {
        fun createRoute(buildingId: String): String = "building/$buildingId/storage"
    }

    data object Agreements : Screen("building/{buildingId}/agreements") {
        fun createRoute(buildingId: String): String = "building/$buildingId/agreements"
    }

    // Stage 6 Services, Maintenance, Bills & Archive Screens
    data object Services : Screen("building/{buildingId}/services") {
        fun createRoute(buildingId: String): String = "building/$buildingId/services"
    }

    data object ServiceArchive : Screen("building/{buildingId}/service_archive") {
        fun createRoute(buildingId: String): String = "building/$buildingId/service_archive"
    }
}
