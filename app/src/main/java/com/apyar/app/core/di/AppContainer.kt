package com.apyar.app.core.di

import android.content.Context
import com.apyar.app.data.local.ApyarDatabase
import com.apyar.app.data.repository.AgreementRepositoryImpl
import com.apyar.app.data.repository.AuditRepositoryImpl
import com.apyar.app.data.repository.BuildingBillRepositoryImpl
import com.apyar.app.data.repository.BuildingMemberRepositoryImpl
import com.apyar.app.data.repository.BuildingRepositoryImpl
import com.apyar.app.data.repository.ChargePeriodRepositoryImpl
import com.apyar.app.data.repository.ChargeRuleRepositoryImpl
import com.apyar.app.data.repository.CustomFormulaRepositoryImpl
import com.apyar.app.data.repository.DelegatedPermissionRepositoryImpl
import com.apyar.app.data.repository.FinancialAccountRepositoryImpl
import com.apyar.app.data.repository.FinancialTransactionRepositoryImpl
import com.apyar.app.data.repository.MaintenanceRepositoryImpl
import com.apyar.app.data.repository.ParkingRepositoryImpl
import com.apyar.app.data.repository.PersonRepositoryImpl
import com.apyar.app.data.repository.ServiceInvoiceRepositoryImpl
import com.apyar.app.data.repository.ServiceProviderRepositoryImpl
import com.apyar.app.data.repository.ServiceRecordRepositoryImpl
import com.apyar.app.data.repository.StorageRepositoryImpl
import com.apyar.app.data.repository.UnitChargeRepositoryImpl
import com.apyar.app.data.repository.UnitRepositoryImpl
import com.apyar.app.data.repository.UserAccountRepositoryImpl
import com.apyar.app.domain.engine.ChargeCalculationEngine
import com.apyar.app.domain.repository.AgreementRepository
import com.apyar.app.domain.repository.AuditRepository
import com.apyar.app.domain.repository.BuildingBillRepository
import com.apyar.app.domain.repository.BuildingMemberRepository
import com.apyar.app.domain.repository.BuildingRepository
import com.apyar.app.domain.repository.ChargePeriodRepository
import com.apyar.app.domain.repository.ChargeRuleRepository
import com.apyar.app.domain.repository.CustomFormulaRepository
import com.apyar.app.domain.repository.DelegatedPermissionRepository
import com.apyar.app.domain.repository.FinancialAccountRepository
import com.apyar.app.domain.repository.FinancialTransactionRepository
import com.apyar.app.domain.repository.MaintenanceRepository
import com.apyar.app.domain.repository.ParkingRepository
import com.apyar.app.domain.repository.PersonRepository
import com.apyar.app.domain.repository.ServiceInvoiceRepository
import com.apyar.app.domain.repository.ServiceProviderRepository
import com.apyar.app.domain.repository.ServiceRecordRepository
import com.apyar.app.domain.repository.StorageRepository
import com.apyar.app.domain.repository.UnitChargeRepository
import com.apyar.app.domain.repository.UnitRepository
import com.apyar.app.domain.repository.UserAccountRepository
import com.apyar.app.domain.usecase.AddBuildingMemberUseCase
import com.apyar.app.domain.usecase.AssignParkingSpaceUseCase
import com.apyar.app.domain.usecase.AssignPersonToUnitUseCase
import com.apyar.app.domain.usecase.AssignStorageUnitUseCase
import com.apyar.app.domain.usecase.CalculateChargePeriodUseCase
import com.apyar.app.domain.usecase.CalculateParkingAdjustmentUseCase
import com.apyar.app.domain.usecase.CalculateStorageAdjustmentUseCase
import com.apyar.app.domain.usecase.CancelAgreementUseCase
import com.apyar.app.domain.usecase.CheckPermissionUseCase
import com.apyar.app.domain.usecase.CreateAdjustmentUseCase
import com.apyar.app.domain.usecase.CreateBuildingBillUseCase
import com.apyar.app.domain.usecase.CreateBuildingUseCase
import com.apyar.app.domain.usecase.CreateChargePeriodUseCase
import com.apyar.app.domain.usecase.CreateChargeRuleUseCase
import com.apyar.app.domain.usecase.CreateCustomFormulaUseCase
import com.apyar.app.domain.usecase.CreateDelegationUseCase
import com.apyar.app.domain.usecase.CreateFinancialAccountUseCase
import com.apyar.app.domain.usecase.CreateInvoiceUseCase
import com.apyar.app.domain.usecase.CreateMaintenanceRecordUseCase
import com.apyar.app.domain.usecase.CreateParkingAgreementUseCase
import com.apyar.app.domain.usecase.CreateParkingSpaceUseCase
import com.apyar.app.domain.usecase.CreatePersonUseCase
import com.apyar.app.domain.usecase.CreateServiceProviderUseCase
import com.apyar.app.domain.usecase.CreateServiceRecordUseCase
import com.apyar.app.domain.usecase.CreateStorageAgreementUseCase
import com.apyar.app.domain.usecase.CreateStorageUnitUseCase
import com.apyar.app.domain.usecase.CreateTemporaryParkingUseUseCase
import com.apyar.app.domain.usecase.CreateUnitUseCase
import com.apyar.app.domain.usecase.CreateUserAccountUseCase
import com.apyar.app.domain.usecase.EndParkingAssignmentUseCase
import com.apyar.app.domain.usecase.EndStorageAssignmentUseCase
import com.apyar.app.domain.usecase.FinalizeChargePeriodUseCase
import com.apyar.app.domain.usecase.GetAgreementByIdUseCase
import com.apyar.app.domain.usecase.GetBuildingAgreementsUseCase
import com.apyar.app.domain.usecase.GetBuildingBillsUseCase
import com.apyar.app.domain.usecase.GetBuildingByIdUseCase
import com.apyar.app.domain.usecase.GetBuildingDelegationsUseCase
import com.apyar.app.domain.usecase.GetBuildingInvoicesUseCase
import com.apyar.app.domain.usecase.GetBuildingMaintenanceRecordsUseCase
import com.apyar.app.domain.usecase.GetBuildingMembersUseCase
import com.apyar.app.domain.usecase.GetBuildingPaymentsUseCase
import com.apyar.app.domain.usecase.GetBuildingServiceArchiveUseCase
import com.apyar.app.domain.usecase.GetBuildingServiceProvidersUseCase
import com.apyar.app.domain.usecase.GetBuildingServiceRecordsUseCase
import com.apyar.app.domain.usecase.GetBuildingsUseCase
import com.apyar.app.domain.usecase.GetChargePeriodByIdUseCase
import com.apyar.app.domain.usecase.GetChargePeriodsUseCase
import com.apyar.app.domain.usecase.GetChargeRulesUseCase
import com.apyar.app.domain.usecase.GetCustomFormulasUseCase
import com.apyar.app.domain.usecase.GetFinancialAccountUseCase
import com.apyar.app.domain.usecase.GetFinancialSummaryUseCase
import com.apyar.app.domain.usecase.GetParkingSpaceByIdUseCase
import com.apyar.app.domain.usecase.GetParkingSpacesUseCase
import com.apyar.app.domain.usecase.GetPeopleByUnitUseCase
import com.apyar.app.domain.usecase.GetPeopleUseCase
import com.apyar.app.domain.usecase.GetServiceDashboardSummaryUseCase
import com.apyar.app.domain.usecase.GetStorageUnitByIdUseCase
import com.apyar.app.domain.usecase.GetStorageUnitsUseCase
import com.apyar.app.domain.usecase.GetTransactionsUseCase
import com.apyar.app.domain.usecase.GetUnitByIdUseCase
import com.apyar.app.domain.usecase.GetUnitChargesUseCase
import com.apyar.app.domain.usecase.GetUnitsByBuildingUseCase
import com.apyar.app.domain.usecase.GetUserEffectivePermissionsUseCase
import com.apyar.app.domain.usecase.GetUserProfileUseCase
import com.apyar.app.domain.usecase.LinkServiceProviderToBuildingUseCase
import com.apyar.app.domain.usecase.RecordBuildingPaymentUseCase
import com.apyar.app.domain.usecase.RecordTransactionUseCase
import com.apyar.app.domain.usecase.RemoveBuildingMemberUseCase
import com.apyar.app.domain.usecase.RevokeDelegationUseCase
import com.apyar.app.domain.usecase.UpdateBuildingMemberRoleUseCase
import com.apyar.app.domain.usecase.UpdateBuildingBillPaymentUseCase
import com.apyar.app.domain.usecase.UpdateBuildingServiceProviderRoleUseCase
import com.apyar.app.domain.usecase.UpdateServiceRecordStatusUseCase

/**
 * AppContainer provides dependencies across the application following Clean Architecture principles.
 */
interface AppContainer {
    val buildingRepository: BuildingRepository
    val unitRepository: UnitRepository
    val personRepository: PersonRepository
    val userAccountRepository: UserAccountRepository
    val buildingMemberRepository: BuildingMemberRepository
    val delegatedPermissionRepository: DelegatedPermissionRepository
    val auditRepository: AuditRepository

    // Stage 3 Repositories
    val financialAccountRepository: FinancialAccountRepository
    val financialTransactionRepository: FinancialTransactionRepository
    val chargeRuleRepository: ChargeRuleRepository
    val customFormulaRepository: CustomFormulaRepository
    val chargePeriodRepository: ChargePeriodRepository
    val unitChargeRepository: UnitChargeRepository
    val chargeCalculationEngine: ChargeCalculationEngine

    // Stage 1 UseCases
    val createBuildingUseCase: CreateBuildingUseCase
    val getBuildingsUseCase: GetBuildingsUseCase
    val getBuildingByIdUseCase: GetBuildingByIdUseCase
    val createUnitUseCase: CreateUnitUseCase
    val getUnitsByBuildingUseCase: GetUnitsByBuildingUseCase
    val getUnitByIdUseCase: GetUnitByIdUseCase
    val createPersonUseCase: CreatePersonUseCase
    val getPeopleUseCase: GetPeopleUseCase
    val assignPersonToUnitUseCase: AssignPersonToUnitUseCase
    val getPeopleByUnitUseCase: GetPeopleByUnitUseCase

    // Stage 2 UseCases (Identity, Role, Permission, Delegation)
    val createUserAccountUseCase: CreateUserAccountUseCase
    val getUserEffectivePermissionsUseCase: GetUserEffectivePermissionsUseCase
    val checkPermissionUseCase: CheckPermissionUseCase
    val addBuildingMemberUseCase: AddBuildingMemberUseCase
    val updateBuildingMemberRoleUseCase: UpdateBuildingMemberRoleUseCase
    val removeBuildingMemberUseCase: RemoveBuildingMemberUseCase
    val getBuildingMembersUseCase: GetBuildingMembersUseCase
    val getUserProfileUseCase: GetUserProfileUseCase
    val createDelegationUseCase: CreateDelegationUseCase
    val revokeDelegationUseCase: RevokeDelegationUseCase
    val getBuildingDelegationsUseCase: GetBuildingDelegationsUseCase

    // Stage 3 UseCases (Financial Core & Flexible Charge Engine)
    val createFinancialAccountUseCase: CreateFinancialAccountUseCase
    val getFinancialAccountUseCase: GetFinancialAccountUseCase
    val recordTransactionUseCase: RecordTransactionUseCase
    val getTransactionsUseCase: GetTransactionsUseCase
    val createChargeRuleUseCase: CreateChargeRuleUseCase
    val getChargeRulesUseCase: GetChargeRulesUseCase
    val createCustomFormulaUseCase: CreateCustomFormulaUseCase
    val getCustomFormulasUseCase: GetCustomFormulasUseCase
    val createChargePeriodUseCase: CreateChargePeriodUseCase
    val calculateChargePeriodUseCase: CalculateChargePeriodUseCase
    val getChargePeriodsUseCase: GetChargePeriodsUseCase
    val getChargePeriodByIdUseCase: GetChargePeriodByIdUseCase
    val getUnitChargesUseCase: GetUnitChargesUseCase
    val finalizeChargePeriodUseCase: FinalizeChargePeriodUseCase
    val createAdjustmentUseCase: CreateAdjustmentUseCase
    val getFinancialSummaryUseCase: GetFinancialSummaryUseCase

    // Stage 4 Repositories (Parking, Storage, Agreements)
    val parkingRepository: ParkingRepository
    val storageRepository: StorageRepository
    val agreementRepository: AgreementRepository

    // Stage 4 UseCases
    val createParkingSpaceUseCase: CreateParkingSpaceUseCase
    val getParkingSpacesUseCase: GetParkingSpacesUseCase
    val getParkingSpaceByIdUseCase: GetParkingSpaceByIdUseCase
    val assignParkingSpaceUseCase: AssignParkingSpaceUseCase
    val createTemporaryParkingUseUseCase: CreateTemporaryParkingUseUseCase
    val endParkingAssignmentUseCase: EndParkingAssignmentUseCase
    val createStorageUnitUseCase: CreateStorageUnitUseCase
    val getStorageUnitsUseCase: GetStorageUnitsUseCase
    val getStorageUnitByIdUseCase: GetStorageUnitByIdUseCase
    val assignStorageUnitUseCase: AssignStorageUnitUseCase
    val endStorageAssignmentUseCase: EndStorageAssignmentUseCase
    val createParkingAgreementUseCase: CreateParkingAgreementUseCase
    val createStorageAgreementUseCase: CreateStorageAgreementUseCase
    val getBuildingAgreementsUseCase: GetBuildingAgreementsUseCase
    val getAgreementByIdUseCase: GetAgreementByIdUseCase
    val cancelAgreementUseCase: CancelAgreementUseCase
    val calculateParkingAdjustmentUseCase: CalculateParkingAdjustmentUseCase
    val calculateStorageAdjustmentUseCase: CalculateStorageAdjustmentUseCase

    // Stage 6 Repositories (Services, Providers, Maintenance, Invoices, Bills)
    val serviceProviderRepository: ServiceProviderRepository
    val serviceRecordRepository: ServiceRecordRepository
    val maintenanceRepository: MaintenanceRepository
    val serviceInvoiceRepository: ServiceInvoiceRepository
    val buildingBillRepository: BuildingBillRepository

    // Stage 6 UseCases
    val createServiceProviderUseCase: CreateServiceProviderUseCase
    val linkServiceProviderToBuildingUseCase: LinkServiceProviderToBuildingUseCase
    val updateBuildingServiceProviderRoleUseCase: UpdateBuildingServiceProviderRoleUseCase
    val getBuildingServiceProvidersUseCase: GetBuildingServiceProvidersUseCase
    val createServiceRecordUseCase: CreateServiceRecordUseCase
    val updateServiceRecordStatusUseCase: UpdateServiceRecordStatusUseCase
    val getBuildingServiceRecordsUseCase: GetBuildingServiceRecordsUseCase
    val createMaintenanceRecordUseCase: CreateMaintenanceRecordUseCase
    val getBuildingMaintenanceRecordsUseCase: GetBuildingMaintenanceRecordsUseCase
    val createInvoiceUseCase: CreateInvoiceUseCase
    val getBuildingInvoicesUseCase: GetBuildingInvoicesUseCase
    val recordBuildingPaymentUseCase: RecordBuildingPaymentUseCase
    val getBuildingPaymentsUseCase: GetBuildingPaymentsUseCase
    val createBuildingBillUseCase: CreateBuildingBillUseCase
    val updateBuildingBillPaymentUseCase: UpdateBuildingBillPaymentUseCase
    val getBuildingBillsUseCase: GetBuildingBillsUseCase
    val getServiceDashboardSummaryUseCase: GetServiceDashboardSummaryUseCase
    val getBuildingServiceArchiveUseCase: GetBuildingServiceArchiveUseCase
}

class DefaultAppContainer(private val context: Context) : AppContainer {
    private val database: ApyarDatabase by lazy {
        ApyarDatabase.getInstance(context)
    }

    override val buildingRepository: BuildingRepository by lazy {
        BuildingRepositoryImpl(database.buildingDao)
    }

    override val unitRepository: UnitRepository by lazy {
        UnitRepositoryImpl(database.unitDao)
    }

    override val personRepository: PersonRepository by lazy {
        PersonRepositoryImpl(database.personDao, database.unitPersonRelationDao)
    }

    override val userAccountRepository: UserAccountRepository by lazy {
        UserAccountRepositoryImpl(database.userAccountDao)
    }

    override val buildingMemberRepository: BuildingMemberRepository by lazy {
        BuildingMemberRepositoryImpl(database.buildingMemberDao)
    }

    override val delegatedPermissionRepository: DelegatedPermissionRepository by lazy {
        DelegatedPermissionRepositoryImpl(database.delegatedPermissionDao)
    }

    override val auditRepository: AuditRepository by lazy {
        AuditRepositoryImpl(database.auditEventDao)
    }

    // Stage 3 Repositories
    override val financialAccountRepository: FinancialAccountRepository by lazy {
        FinancialAccountRepositoryImpl(database.financialAccountDao)
    }

    override val financialTransactionRepository: FinancialTransactionRepository by lazy {
        FinancialTransactionRepositoryImpl(database.financialTransactionDao)
    }

    override val chargeRuleRepository: ChargeRuleRepository by lazy {
        ChargeRuleRepositoryImpl(database.chargeRuleDao)
    }

    override val customFormulaRepository: CustomFormulaRepository by lazy {
        CustomFormulaRepositoryImpl(database.customFormulaDao)
    }

    override val chargePeriodRepository: ChargePeriodRepository by lazy {
        ChargePeriodRepositoryImpl(database.chargePeriodDao)
    }

    override val unitChargeRepository: UnitChargeRepository by lazy {
        UnitChargeRepositoryImpl(database.unitChargeDao)
    }

    override val chargeCalculationEngine: ChargeCalculationEngine by lazy {
        ChargeCalculationEngine()
    }

    // Stage 1 UseCases
    override val createBuildingUseCase: CreateBuildingUseCase by lazy {
        CreateBuildingUseCase(buildingRepository)
    }

    override val getBuildingsUseCase: GetBuildingsUseCase by lazy {
        GetBuildingsUseCase(buildingRepository)
    }

    override val getBuildingByIdUseCase: GetBuildingByIdUseCase by lazy {
        GetBuildingByIdUseCase(buildingRepository)
    }

    override val createUnitUseCase: CreateUnitUseCase by lazy {
        CreateUnitUseCase(unitRepository)
    }

    override val getUnitsByBuildingUseCase: GetUnitsByBuildingUseCase by lazy {
        GetUnitsByBuildingUseCase(unitRepository)
    }

    override val getUnitByIdUseCase: GetUnitByIdUseCase by lazy {
        GetUnitByIdUseCase(unitRepository)
    }

    override val createPersonUseCase: CreatePersonUseCase by lazy {
        CreatePersonUseCase(personRepository)
    }

    override val getPeopleUseCase: GetPeopleUseCase by lazy {
        GetPeopleUseCase(personRepository)
    }

    override val assignPersonToUnitUseCase: AssignPersonToUnitUseCase by lazy {
        AssignPersonToUnitUseCase(personRepository)
    }

    override val getPeopleByUnitUseCase: GetPeopleByUnitUseCase by lazy {
        GetPeopleByUnitUseCase(personRepository)
    }

    // Stage 2 UseCases
    override val getUserEffectivePermissionsUseCase: GetUserEffectivePermissionsUseCase by lazy {
        GetUserEffectivePermissionsUseCase(
            buildingMemberRepository = buildingMemberRepository,
            delegatedPermissionRepository = delegatedPermissionRepository,
            userAccountRepository = userAccountRepository
        )
    }

    override val checkPermissionUseCase: CheckPermissionUseCase by lazy {
        CheckPermissionUseCase(getUserEffectivePermissionsUseCase)
    }

    override val createUserAccountUseCase: CreateUserAccountUseCase by lazy {
        CreateUserAccountUseCase(
            userAccountRepository = userAccountRepository,
            personRepository = personRepository,
            auditRepository = auditRepository
        )
    }

    override val addBuildingMemberUseCase: AddBuildingMemberUseCase by lazy {
        AddBuildingMemberUseCase(
            buildingMemberRepository = buildingMemberRepository,
            buildingRepository = buildingRepository,
            userAccountRepository = userAccountRepository,
            checkPermissionUseCase = checkPermissionUseCase,
            auditRepository = auditRepository
        )
    }

    override val updateBuildingMemberRoleUseCase: UpdateBuildingMemberRoleUseCase by lazy {
        UpdateBuildingMemberRoleUseCase(
            buildingMemberRepository = buildingMemberRepository,
            checkPermissionUseCase = checkPermissionUseCase,
            auditRepository = auditRepository
        )
    }

    override val removeBuildingMemberUseCase: RemoveBuildingMemberUseCase by lazy {
        RemoveBuildingMemberUseCase(
            buildingMemberRepository = buildingMemberRepository,
            checkPermissionUseCase = checkPermissionUseCase,
            auditRepository = auditRepository
        )
    }

    override val getBuildingMembersUseCase: GetBuildingMembersUseCase by lazy {
        GetBuildingMembersUseCase(
            buildingMemberRepository = buildingMemberRepository,
            userAccountRepository = userAccountRepository,
            personRepository = personRepository
        )
    }

    override val getUserProfileUseCase: GetUserProfileUseCase by lazy {
        GetUserProfileUseCase(
            userAccountRepository = userAccountRepository,
            personRepository = personRepository,
            buildingMemberRepository = buildingMemberRepository,
            buildingRepository = buildingRepository
        )
    }

    override val createDelegationUseCase: CreateDelegationUseCase by lazy {
        CreateDelegationUseCase(
            delegatedPermissionRepository = delegatedPermissionRepository,
            buildingRepository = buildingRepository,
            userAccountRepository = userAccountRepository,
            checkPermissionUseCase = checkPermissionUseCase,
            auditRepository = auditRepository
        )
    }

    override val revokeDelegationUseCase: RevokeDelegationUseCase by lazy {
        RevokeDelegationUseCase(
            delegatedPermissionRepository = delegatedPermissionRepository,
            checkPermissionUseCase = checkPermissionUseCase,
            auditRepository = auditRepository
        )
    }

    override val getBuildingDelegationsUseCase: GetBuildingDelegationsUseCase by lazy {
        GetBuildingDelegationsUseCase(
            delegatedPermissionRepository = delegatedPermissionRepository,
            userAccountRepository = userAccountRepository,
            personRepository = personRepository
        )
    }

    // Stage 3 UseCases
    override val createFinancialAccountUseCase: CreateFinancialAccountUseCase by lazy {
        CreateFinancialAccountUseCase(
            financialAccountRepository = financialAccountRepository,
            checkPermissionUseCase = checkPermissionUseCase,
            auditRepository = auditRepository
        )
    }

    override val getFinancialAccountUseCase: GetFinancialAccountUseCase by lazy {
        GetFinancialAccountUseCase(
            financialAccountRepository = financialAccountRepository,
            checkPermissionUseCase = checkPermissionUseCase
        )
    }

    override val recordTransactionUseCase: RecordTransactionUseCase by lazy {
        RecordTransactionUseCase(
            financialTransactionRepository = financialTransactionRepository,
            financialAccountRepository = financialAccountRepository,
            checkPermissionUseCase = checkPermissionUseCase,
            auditRepository = auditRepository
        )
    }

    override val getTransactionsUseCase: GetTransactionsUseCase by lazy {
        GetTransactionsUseCase(
            financialTransactionRepository = financialTransactionRepository,
            checkPermissionUseCase = checkPermissionUseCase
        )
    }

    override val createChargeRuleUseCase: CreateChargeRuleUseCase by lazy {
        CreateChargeRuleUseCase(
            chargeRuleRepository = chargeRuleRepository,
            checkPermissionUseCase = checkPermissionUseCase,
            auditRepository = auditRepository
        )
    }

    override val getChargeRulesUseCase: GetChargeRulesUseCase by lazy {
        GetChargeRulesUseCase(
            chargeRuleRepository = chargeRuleRepository,
            checkPermissionUseCase = checkPermissionUseCase
        )
    }

    override val createCustomFormulaUseCase: CreateCustomFormulaUseCase by lazy {
        CreateCustomFormulaUseCase(
            customFormulaRepository = customFormulaRepository,
            checkPermissionUseCase = checkPermissionUseCase,
            auditRepository = auditRepository
        )
    }

    override val getCustomFormulasUseCase: GetCustomFormulasUseCase by lazy {
        GetCustomFormulasUseCase(
            customFormulaRepository = customFormulaRepository,
            checkPermissionUseCase = checkPermissionUseCase
        )
    }

    override val createChargePeriodUseCase: CreateChargePeriodUseCase by lazy {
        CreateChargePeriodUseCase(
            chargePeriodRepository = chargePeriodRepository,
            chargeRuleRepository = chargeRuleRepository,
            checkPermissionUseCase = checkPermissionUseCase,
            auditRepository = auditRepository
        )
    }

    override val calculateChargePeriodUseCase: CalculateChargePeriodUseCase by lazy {
        CalculateChargePeriodUseCase(
            chargePeriodRepository = chargePeriodRepository,
            chargeRuleRepository = chargeRuleRepository,
            unitChargeRepository = unitChargeRepository,
            checkPermissionUseCase = checkPermissionUseCase,
            auditRepository = auditRepository,
            calculationEngine = chargeCalculationEngine
        )
    }

    override val getChargePeriodsUseCase: GetChargePeriodsUseCase by lazy {
        GetChargePeriodsUseCase(
            chargePeriodRepository = chargePeriodRepository,
            checkPermissionUseCase = checkPermissionUseCase
        )
    }

    override val getChargePeriodByIdUseCase: GetChargePeriodByIdUseCase by lazy {
        GetChargePeriodByIdUseCase(
            chargePeriodRepository = chargePeriodRepository,
            checkPermissionUseCase = checkPermissionUseCase
        )
    }

    override val getUnitChargesUseCase: GetUnitChargesUseCase by lazy {
        GetUnitChargesUseCase(
            unitChargeRepository = unitChargeRepository,
            chargePeriodRepository = chargePeriodRepository,
            unitRepository = unitRepository,
            checkPermissionUseCase = checkPermissionUseCase
        )
    }

    override val finalizeChargePeriodUseCase: FinalizeChargePeriodUseCase by lazy {
        FinalizeChargePeriodUseCase(
            chargePeriodRepository = chargePeriodRepository,
            unitChargeRepository = unitChargeRepository,
            financialAccountRepository = financialAccountRepository,
            financialTransactionRepository = financialTransactionRepository,
            checkPermissionUseCase = checkPermissionUseCase,
            auditRepository = auditRepository
        )
    }

    override val createAdjustmentUseCase: CreateAdjustmentUseCase by lazy {
        CreateAdjustmentUseCase(
            unitChargeRepository = unitChargeRepository,
            financialAccountRepository = financialAccountRepository,
            financialTransactionRepository = financialTransactionRepository,
            checkPermissionUseCase = checkPermissionUseCase,
            auditRepository = auditRepository
        )
    }

    override val getFinancialSummaryUseCase: GetFinancialSummaryUseCase by lazy {
        GetFinancialSummaryUseCase(
            financialAccountRepository = financialAccountRepository,
            financialTransactionRepository = financialTransactionRepository,
            chargePeriodRepository = chargePeriodRepository,
            unitChargeRepository = unitChargeRepository,
            checkPermissionUseCase = checkPermissionUseCase
        )
    }

    // Stage 4 Repositories
    override val parkingRepository: ParkingRepository by lazy {
        ParkingRepositoryImpl(database.parkingSpaceDao, database.parkingAssignmentDao)
    }

    override val storageRepository: StorageRepository by lazy {
        StorageRepositoryImpl(database.storageUnitDao, database.storageAssignmentDao)
    }

    override val agreementRepository: AgreementRepository by lazy {
        AgreementRepositoryImpl(database.agreementDao, database.agreementDocumentDao)
    }

    // Stage 4 UseCases
    override val createParkingSpaceUseCase: CreateParkingSpaceUseCase by lazy {
        CreateParkingSpaceUseCase(
            parkingRepository = parkingRepository,
            buildingRepository = buildingRepository,
            checkPermissionUseCase = checkPermissionUseCase,
            auditRepository = auditRepository
        )
    }

    override val getParkingSpacesUseCase: GetParkingSpacesUseCase by lazy {
        GetParkingSpacesUseCase(
            parkingRepository = parkingRepository,
            unitRepository = unitRepository,
            agreementRepository = agreementRepository,
            checkPermissionUseCase = checkPermissionUseCase
        )
    }

    override val getParkingSpaceByIdUseCase: GetParkingSpaceByIdUseCase by lazy {
        GetParkingSpaceByIdUseCase(
            parkingRepository = parkingRepository,
            unitRepository = unitRepository,
            agreementRepository = agreementRepository,
            checkPermissionUseCase = checkPermissionUseCase
        )
    }

    override val assignParkingSpaceUseCase: AssignParkingSpaceUseCase by lazy {
        AssignParkingSpaceUseCase(
            parkingRepository = parkingRepository,
            unitRepository = unitRepository,
            checkPermissionUseCase = checkPermissionUseCase,
            auditRepository = auditRepository
        )
    }

    override val createTemporaryParkingUseUseCase: CreateTemporaryParkingUseUseCase by lazy {
        CreateTemporaryParkingUseUseCase(
            parkingRepository = parkingRepository,
            unitRepository = unitRepository,
            checkPermissionUseCase = checkPermissionUseCase,
            auditRepository = auditRepository
        )
    }

    override val endParkingAssignmentUseCase: EndParkingAssignmentUseCase by lazy {
        EndParkingAssignmentUseCase(
            parkingRepository = parkingRepository,
            checkPermissionUseCase = checkPermissionUseCase,
            auditRepository = auditRepository
        )
    }

    override val createStorageUnitUseCase: CreateStorageUnitUseCase by lazy {
        CreateStorageUnitUseCase(
            storageRepository = storageRepository,
            buildingRepository = buildingRepository,
            checkPermissionUseCase = checkPermissionUseCase,
            auditRepository = auditRepository
        )
    }

    override val getStorageUnitsUseCase: GetStorageUnitsUseCase by lazy {
        GetStorageUnitsUseCase(
            storageRepository = storageRepository,
            unitRepository = unitRepository,
            agreementRepository = agreementRepository,
            checkPermissionUseCase = checkPermissionUseCase
        )
    }

    override val getStorageUnitByIdUseCase: GetStorageUnitByIdUseCase by lazy {
        GetStorageUnitByIdUseCase(
            storageRepository = storageRepository,
            unitRepository = unitRepository,
            agreementRepository = agreementRepository,
            checkPermissionUseCase = checkPermissionUseCase
        )
    }

    override val assignStorageUnitUseCase: AssignStorageUnitUseCase by lazy {
        AssignStorageUnitUseCase(
            storageRepository = storageRepository,
            unitRepository = unitRepository,
            checkPermissionUseCase = checkPermissionUseCase,
            auditRepository = auditRepository
        )
    }

    override val endStorageAssignmentUseCase: EndStorageAssignmentUseCase by lazy {
        EndStorageAssignmentUseCase(
            storageRepository = storageRepository,
            checkPermissionUseCase = checkPermissionUseCase,
            auditRepository = auditRepository
        )
    }

    override val createParkingAgreementUseCase: CreateParkingAgreementUseCase by lazy {
        CreateParkingAgreementUseCase(
            agreementRepository = agreementRepository,
            parkingRepository = parkingRepository,
            unitRepository = unitRepository,
            checkPermissionUseCase = checkPermissionUseCase,
            auditRepository = auditRepository
        )
    }

    override val createStorageAgreementUseCase: CreateStorageAgreementUseCase by lazy {
        CreateStorageAgreementUseCase(
            agreementRepository = agreementRepository,
            storageRepository = storageRepository,
            unitRepository = unitRepository,
            checkPermissionUseCase = checkPermissionUseCase,
            auditRepository = auditRepository
        )
    }

    override val getBuildingAgreementsUseCase: GetBuildingAgreementsUseCase by lazy {
        GetBuildingAgreementsUseCase(
            agreementRepository = agreementRepository,
            unitRepository = unitRepository,
            parkingRepository = parkingRepository,
            storageRepository = storageRepository,
            checkPermissionUseCase = checkPermissionUseCase
        )
    }

    override val getAgreementByIdUseCase: GetAgreementByIdUseCase by lazy {
        GetAgreementByIdUseCase(
            agreementRepository = agreementRepository,
            unitRepository = unitRepository,
            parkingRepository = parkingRepository,
            storageRepository = storageRepository,
            checkPermissionUseCase = checkPermissionUseCase
        )
    }

    override val cancelAgreementUseCase: CancelAgreementUseCase by lazy {
        CancelAgreementUseCase(
            agreementRepository = agreementRepository,
            parkingRepository = parkingRepository,
            storageRepository = storageRepository,
            checkPermissionUseCase = checkPermissionUseCase,
            auditRepository = auditRepository
        )
    }

    override val calculateParkingAdjustmentUseCase: CalculateParkingAdjustmentUseCase by lazy {
        CalculateParkingAdjustmentUseCase(
            unitChargeRepository = unitChargeRepository,
            agreementRepository = agreementRepository,
            unitRepository = unitRepository,
            chargePeriodRepository = chargePeriodRepository,
            checkPermissionUseCase = checkPermissionUseCase,
            auditRepository = auditRepository
        )
    }

    override val calculateStorageAdjustmentUseCase: CalculateStorageAdjustmentUseCase by lazy {
        CalculateStorageAdjustmentUseCase(
            unitChargeRepository = unitChargeRepository,
            agreementRepository = agreementRepository,
            unitRepository = unitRepository,
            chargePeriodRepository = chargePeriodRepository,
            checkPermissionUseCase = checkPermissionUseCase,
            auditRepository = auditRepository
        )
    }

    // Stage 6 Repositories
    override val serviceProviderRepository: ServiceProviderRepository by lazy {
        ServiceProviderRepositoryImpl(database.serviceProviderDao, database.buildingServiceProviderDao)
    }

    override val serviceRecordRepository: ServiceRecordRepository by lazy {
        ServiceRecordRepositoryImpl(database.serviceRecordDao, database.serviceProviderDao)
    }

    override val maintenanceRepository: MaintenanceRepository by lazy {
        MaintenanceRepositoryImpl(database.maintenanceRecordDao)
    }

    override val serviceInvoiceRepository: ServiceInvoiceRepository by lazy {
        ServiceInvoiceRepositoryImpl(
            database.invoiceDao,
            database.paymentRecordDao,
            database.serviceProviderDao,
            database.serviceRecordDao
        )
    }

    override val buildingBillRepository: BuildingBillRepository by lazy {
        BuildingBillRepositoryImpl(database.buildingBillDao)
    }

    // Stage 6 UseCases
    override val createServiceProviderUseCase: CreateServiceProviderUseCase by lazy {
        CreateServiceProviderUseCase(
            serviceProviderRepository = serviceProviderRepository,
            buildingRepository = buildingRepository,
            checkPermissionUseCase = checkPermissionUseCase,
            auditRepository = auditRepository
        )
    }

    override val linkServiceProviderToBuildingUseCase: LinkServiceProviderToBuildingUseCase by lazy {
        LinkServiceProviderToBuildingUseCase(
            serviceProviderRepository = serviceProviderRepository,
            checkPermissionUseCase = checkPermissionUseCase,
            auditRepository = auditRepository
        )
    }

    override val updateBuildingServiceProviderRoleUseCase: UpdateBuildingServiceProviderRoleUseCase by lazy {
        UpdateBuildingServiceProviderRoleUseCase(
            serviceProviderRepository = serviceProviderRepository,
            checkPermissionUseCase = checkPermissionUseCase,
            auditRepository = auditRepository
        )
    }

    override val getBuildingServiceProvidersUseCase: GetBuildingServiceProvidersUseCase by lazy {
        GetBuildingServiceProvidersUseCase(
            serviceProviderRepository = serviceProviderRepository,
            checkPermissionUseCase = checkPermissionUseCase
        )
    }

    override val createServiceRecordUseCase: CreateServiceRecordUseCase by lazy {
        CreateServiceRecordUseCase(
            serviceRecordRepository = serviceRecordRepository,
            serviceProviderRepository = serviceProviderRepository,
            buildingRepository = buildingRepository,
            checkPermissionUseCase = checkPermissionUseCase,
            auditRepository = auditRepository
        )
    }

    override val updateServiceRecordStatusUseCase: UpdateServiceRecordStatusUseCase by lazy {
        UpdateServiceRecordStatusUseCase(
            serviceRecordRepository = serviceRecordRepository,
            checkPermissionUseCase = checkPermissionUseCase,
            auditRepository = auditRepository
        )
    }

    override val getBuildingServiceRecordsUseCase: GetBuildingServiceRecordsUseCase by lazy {
        GetBuildingServiceRecordsUseCase(
            serviceRecordRepository = serviceRecordRepository,
            checkPermissionUseCase = checkPermissionUseCase
        )
    }

    override val createMaintenanceRecordUseCase: CreateMaintenanceRecordUseCase by lazy {
        CreateMaintenanceRecordUseCase(
            maintenanceRepository = maintenanceRepository,
            buildingRepository = buildingRepository,
            checkPermissionUseCase = checkPermissionUseCase,
            auditRepository = auditRepository
        )
    }

    override val getBuildingMaintenanceRecordsUseCase: GetBuildingMaintenanceRecordsUseCase by lazy {
        GetBuildingMaintenanceRecordsUseCase(
            maintenanceRepository = maintenanceRepository,
            checkPermissionUseCase = checkPermissionUseCase
        )
    }

    override val createInvoiceUseCase: CreateInvoiceUseCase by lazy {
        CreateInvoiceUseCase(
            serviceInvoiceRepository = serviceInvoiceRepository,
            buildingRepository = buildingRepository,
            checkPermissionUseCase = checkPermissionUseCase,
            auditRepository = auditRepository
        )
    }

    override val getBuildingInvoicesUseCase: GetBuildingInvoicesUseCase by lazy {
        GetBuildingInvoicesUseCase(
            serviceInvoiceRepository = serviceInvoiceRepository,
            checkPermissionUseCase = checkPermissionUseCase
        )
    }

    override val recordBuildingPaymentUseCase: RecordBuildingPaymentUseCase by lazy {
        RecordBuildingPaymentUseCase(
            serviceInvoiceRepository = serviceInvoiceRepository,
            buildingRepository = buildingRepository,
            checkPermissionUseCase = checkPermissionUseCase,
            auditRepository = auditRepository
        )
    }

    override val getBuildingPaymentsUseCase: GetBuildingPaymentsUseCase by lazy {
        GetBuildingPaymentsUseCase(
            serviceInvoiceRepository = serviceInvoiceRepository,
            checkPermissionUseCase = checkPermissionUseCase
        )
    }

    override val createBuildingBillUseCase: CreateBuildingBillUseCase by lazy {
        CreateBuildingBillUseCase(
            buildingBillRepository = buildingBillRepository,
            buildingRepository = buildingRepository,
            checkPermissionUseCase = checkPermissionUseCase,
            auditRepository = auditRepository
        )
    }

    override val updateBuildingBillPaymentUseCase: UpdateBuildingBillPaymentUseCase by lazy {
        UpdateBuildingBillPaymentUseCase(
            buildingBillRepository = buildingBillRepository,
            checkPermissionUseCase = checkPermissionUseCase,
            auditRepository = auditRepository
        )
    }

    override val getBuildingBillsUseCase: GetBuildingBillsUseCase by lazy {
        GetBuildingBillsUseCase(
            buildingBillRepository = buildingBillRepository,
            checkPermissionUseCase = checkPermissionUseCase
        )
    }

    override val getServiceDashboardSummaryUseCase: GetServiceDashboardSummaryUseCase by lazy {
        GetServiceDashboardSummaryUseCase(
            serviceProviderRepository = serviceProviderRepository,
            serviceRecordRepository = serviceRecordRepository,
            maintenanceRepository = maintenanceRepository,
            serviceInvoiceRepository = serviceInvoiceRepository,
            buildingBillRepository = buildingBillRepository,
            checkPermissionUseCase = checkPermissionUseCase
        )
    }

    override val getBuildingServiceArchiveUseCase: GetBuildingServiceArchiveUseCase by lazy {
        GetBuildingServiceArchiveUseCase(
            serviceRecordRepository = serviceRecordRepository,
            maintenanceRepository = maintenanceRepository,
            serviceInvoiceRepository = serviceInvoiceRepository,
            buildingBillRepository = buildingBillRepository,
            checkPermissionUseCase = checkPermissionUseCase
        )
    }
}
