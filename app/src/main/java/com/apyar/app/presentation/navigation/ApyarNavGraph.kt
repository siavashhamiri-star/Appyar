package com.apyar.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.apyar.app.core.di.AppContainer
import com.apyar.app.presentation.agreements.AgreementsScreen
import com.apyar.app.presentation.agreements.AgreementsViewModel
import com.apyar.app.presentation.building_detail.BuildingDetailScreen
import com.apyar.app.presentation.building_detail.BuildingDetailViewModel
import com.apyar.app.presentation.buildings.BuildingsScreen
import com.apyar.app.presentation.buildings.BuildingsViewModel
import com.apyar.app.presentation.delegations.DelegationsScreen
import com.apyar.app.presentation.delegations.DelegationsViewModel
import com.apyar.app.presentation.financial.ChargeCalculationScreen
import com.apyar.app.presentation.financial.ChargeCalculationViewModel
import com.apyar.app.presentation.financial.FinancialDashboardScreen
import com.apyar.app.presentation.financial.FinancialDashboardViewModel
import com.apyar.app.presentation.financial.TransactionsScreen
import com.apyar.app.presentation.financial.TransactionsViewModel
import com.apyar.app.presentation.members.BuildingMembersScreen
import com.apyar.app.presentation.members.BuildingMembersViewModel
import com.apyar.app.presentation.parking.ParkingScreen
import com.apyar.app.presentation.parking.ParkingViewModel
import com.apyar.app.presentation.permissions.PermissionsScreen
import com.apyar.app.presentation.permissions.PermissionsViewModel
import com.apyar.app.presentation.services.ServicesScreen
import com.apyar.app.presentation.services.ServicesViewModel
import com.apyar.app.presentation.storage.StorageScreen
import com.apyar.app.presentation.storage.StorageViewModel
import com.apyar.app.presentation.unit_detail.UnitDetailScreen
import com.apyar.app.presentation.unit_detail.UnitDetailViewModel
import com.apyar.app.presentation.user_profile.UserProfileScreen
import com.apyar.app.presentation.user_profile.UserProfileViewModel

@Composable
fun ApyarNavGraph(
    navController: NavHostController,
    container: AppContainer
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Buildings.route
    ) {
        // 1. Buildings List Screen
        composable(route = Screen.Buildings.route) {
            val viewModel: BuildingsViewModel = viewModel(
                factory = BuildingsViewModel.Factory(
                    getBuildingsUseCase = container.getBuildingsUseCase,
                    createBuildingUseCase = container.createBuildingUseCase
                )
            )
            BuildingsScreen(
                viewModel = viewModel,
                onBuildingClick = { buildingId ->
                    navController.navigate(Screen.BuildingDetail.createRoute(buildingId))
                }
            )
        }

        // 2. Building Detail Screen
        composable(
            route = Screen.BuildingDetail.route,
            arguments = listOf(
                navArgument("buildingId") { type = NavType.StringType }
            )
        ) {
            val buildingId = it.arguments?.getString("buildingId") ?: ""
            val viewModel: BuildingDetailViewModel = viewModel(
                factory = BuildingDetailViewModel.Factory(
                    savedStateHandle = SavedStateHandle(mapOf("buildingId" to buildingId)),
                    getBuildingByIdUseCase = container.getBuildingByIdUseCase,
                    getUnitsByBuildingUseCase = container.getUnitsByBuildingUseCase,
                    createUnitUseCase = container.createUnitUseCase
                )
            )
            BuildingDetailScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onUnitClick = { unitId ->
                    navController.navigate(Screen.UnitDetail.createRoute(unitId))
                },
                onMembersClick = {
                    navController.navigate(Screen.BuildingMembers.createRoute(buildingId))
                },
                onDelegationsClick = {
                    navController.navigate(Screen.Delegations.createRoute(buildingId))
                },
                onFinancialClick = {
                    navController.navigate(Screen.FinancialDashboard.createRoute(buildingId))
                },
                onChargeCalculationClick = {
                    navController.navigate(Screen.ChargeCalculation.createRoute(buildingId))
                },
                onParkingClick = {
                    navController.navigate(Screen.Parking.createRoute(buildingId))
                },
                onStorageClick = {
                    navController.navigate(Screen.Storage.createRoute(buildingId))
                },
                onAgreementsClick = {
                    navController.navigate(Screen.Agreements.createRoute(buildingId))
                },
                onServicesClick = {
                    navController.navigate(Screen.Services.createRoute(buildingId))
                }
            )
        }

        // 3. Unit Detail Screen
        composable(
            route = Screen.UnitDetail.route,
            arguments = listOf(
                navArgument("unitId") { type = NavType.StringType }
            )
        ) {
            val viewModel: UnitDetailViewModel = viewModel(
                factory = UnitDetailViewModel.Factory(
                    savedStateHandle = SavedStateHandle(
                        mapOf("unitId" to (it.arguments?.getString("unitId") ?: ""))
                    ),
                    getUnitByIdUseCase = container.getUnitByIdUseCase,
                    getPeopleByUnitUseCase = container.getPeopleByUnitUseCase,
                    getPeopleUseCase = container.getPeopleUseCase,
                    createPersonUseCase = container.createPersonUseCase,
                    assignPersonToUnitUseCase = container.assignPersonToUnitUseCase
                )
            )
            UnitDetailScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 4. Building Members Screen
        composable(
            route = Screen.BuildingMembers.route,
            arguments = listOf(
                navArgument("buildingId") { type = NavType.StringType }
            )
        ) {
            val buildingId = it.arguments?.getString("buildingId") ?: ""
            val viewModel: BuildingMembersViewModel = viewModel(
                factory = BuildingMembersViewModel.Factory(
                    savedStateHandle = SavedStateHandle(mapOf("buildingId" to buildingId)),
                    getBuildingMembersUseCase = container.getBuildingMembersUseCase,
                    addBuildingMemberUseCase = container.addBuildingMemberUseCase,
                    updateBuildingMemberRoleUseCase = container.updateBuildingMemberRoleUseCase,
                    removeBuildingMemberUseCase = container.removeBuildingMemberUseCase,
                    userAccountRepository = container.userAccountRepository
                )
            )
            BuildingMembersScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onViewPermissionsClick = { userId ->
                    navController.navigate(Screen.Permissions.createRoute(buildingId, userId))
                },
                onViewUserProfileClick = { userId ->
                    navController.navigate(Screen.UserProfile.createRoute(userId))
                }
            )
        }

        // 5. Permissions Matrix Screen
        composable(
            route = Screen.Permissions.route,
            arguments = listOf(
                navArgument("buildingId") { type = NavType.StringType },
                navArgument("userId") { type = NavType.StringType }
            )
        ) {
            val buildingId = it.arguments?.getString("buildingId") ?: ""
            val userId = it.arguments?.getString("userId") ?: ""
            val viewModel: PermissionsViewModel = viewModel(
                factory = PermissionsViewModel.Factory(
                    savedStateHandle = SavedStateHandle(
                        mapOf("buildingId" to buildingId, "userId" to userId)
                    ),
                    getUserEffectivePermissionsUseCase = container.getUserEffectivePermissionsUseCase,
                    buildingMemberRepository = container.buildingMemberRepository,
                    buildingRepository = container.buildingRepository,
                    userAccountRepository = container.userAccountRepository,
                    personRepository = container.personRepository,
                    delegatedPermissionRepository = container.delegatedPermissionRepository
                )
            )
            PermissionsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 6. Delegations Screen
        composable(
            route = Screen.Delegations.route,
            arguments = listOf(
                navArgument("buildingId") { type = NavType.StringType }
            )
        ) {
            val buildingId = it.arguments?.getString("buildingId") ?: ""
            val viewModel: DelegationsViewModel = viewModel(
                factory = DelegationsViewModel.Factory(
                    savedStateHandle = SavedStateHandle(mapOf("buildingId" to buildingId)),
                    getBuildingDelegationsUseCase = container.getBuildingDelegationsUseCase,
                    createDelegationUseCase = container.createDelegationUseCase,
                    revokeDelegationUseCase = container.revokeDelegationUseCase,
                    getBuildingMembersUseCase = container.getBuildingMembersUseCase
                )
            )
            DelegationsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 7. User Profile Screen
        composable(
            route = Screen.UserProfile.route,
            arguments = listOf(
                navArgument("userId") { type = NavType.StringType }
            )
        ) {
            val userId = it.arguments?.getString("userId") ?: ""
            val viewModel: UserProfileViewModel = viewModel(
                factory = UserProfileViewModel.Factory(
                    savedStateHandle = SavedStateHandle(mapOf("userId" to userId)),
                    getUserProfileUseCase = container.getUserProfileUseCase
                )
            )
            UserProfileScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onBuildingClick = { bId ->
                    navController.navigate(Screen.BuildingDetail.createRoute(bId))
                }
            )
        }

        // 8. Financial Dashboard Screen (Stage 3)
        composable(
            route = Screen.FinancialDashboard.route,
            arguments = listOf(
                navArgument("buildingId") { type = NavType.StringType }
            )
        ) {
            val buildingId = it.arguments?.getString("buildingId") ?: ""
            val viewModel: FinancialDashboardViewModel = viewModel(
                factory = FinancialDashboardViewModel.provideFactory(
                    appContainer = container,
                    buildingId = buildingId,
                    currentUserId = "user-1"
                )
            )
            FinancialDashboardScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToChargeCalculation = {
                    navController.navigate(Screen.ChargeCalculation.createRoute(buildingId))
                },
                onNavigateToTransactions = {
                    navController.navigate(Screen.Transactions.createRoute(buildingId))
                }
            )
        }

        // 9. Charge Calculation Screen (Stage 3)
        composable(
            route = Screen.ChargeCalculation.route,
            arguments = listOf(
                navArgument("buildingId") { type = NavType.StringType }
            )
        ) {
            val buildingId = it.arguments?.getString("buildingId") ?: ""
            val viewModel: ChargeCalculationViewModel = viewModel(
                factory = ChargeCalculationViewModel.provideFactory(
                    appContainer = container,
                    buildingId = buildingId,
                    currentUserId = "user-1"
                )
            )
            ChargeCalculationScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDashboard = {
                    navController.navigate(Screen.FinancialDashboard.createRoute(buildingId))
                }
            )
        }

        // 10. Transactions Screen (Stage 3)
        composable(
            route = Screen.Transactions.route,
            arguments = listOf(
                navArgument("buildingId") { type = NavType.StringType }
            )
        ) {
            val buildingId = it.arguments?.getString("buildingId") ?: ""
            val viewModel: TransactionsViewModel = viewModel(
                factory = TransactionsViewModel.provideFactory(
                    appContainer = container,
                    buildingId = buildingId,
                    currentUserId = "user-1"
                )
            )
            TransactionsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 11. Parking Screen (Stage 4)
        composable(
            route = Screen.Parking.route,
            arguments = listOf(
                navArgument("buildingId") { type = NavType.StringType }
            )
        ) {
            val buildingId = it.arguments?.getString("buildingId") ?: ""
            val viewModel: ParkingViewModel = viewModel(
                factory = ParkingViewModel.provideFactory(
                    appContainer = container,
                    buildingId = buildingId,
                    currentUserId = "user-1"
                )
            )
            ParkingScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 12. Storage Screen (Stage 4)
        composable(
            route = Screen.Storage.route,
            arguments = listOf(
                navArgument("buildingId") { type = NavType.StringType }
            )
        ) {
            val buildingId = it.arguments?.getString("buildingId") ?: ""
            val viewModel: StorageViewModel = viewModel(
                factory = StorageViewModel.provideFactory(
                    appContainer = container,
                    buildingId = buildingId,
                    currentUserId = "user-1"
                )
            )
            StorageScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 13. Agreements Screen (Stage 4)
        composable(
            route = Screen.Agreements.route,
            arguments = listOf(
                navArgument("buildingId") { type = NavType.StringType }
            )
        ) {
            val buildingId = it.arguments?.getString("buildingId") ?: ""
            val viewModel: AgreementsViewModel = viewModel(
                factory = AgreementsViewModel.provideFactory(
                    appContainer = container,
                    buildingId = buildingId,
                    currentUserId = "user-1"
                )
            )
            AgreementsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 14. Services & Maintenance Screen (Stage 6)
        composable(
            route = Screen.Services.route,
            arguments = listOf(
                navArgument("buildingId") { type = NavType.StringType }
            )
        ) {
            val buildingId = it.arguments?.getString("buildingId") ?: ""
            val viewModel: ServicesViewModel = viewModel(
                factory = ServicesViewModel.provideFactory(
                    appContainer = container,
                    buildingId = buildingId,
                    currentUserId = "user-1"
                )
            )
            ServicesScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
