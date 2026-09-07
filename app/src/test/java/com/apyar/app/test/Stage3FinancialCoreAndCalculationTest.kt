package com.apyar.app.test

import com.apyar.app.domain.engine.ChargeCalculationEngine
import com.apyar.app.domain.engine.FinancialRoundingService
import com.apyar.app.domain.engine.UnitInclusionSpec
import com.apyar.app.domain.model.AccountStatus
import com.apyar.app.domain.model.AppPermission
import com.apyar.app.domain.model.Building
import com.apyar.app.domain.model.BuildingMember
import com.apyar.app.domain.model.CalculationType
import com.apyar.app.domain.model.ChargePeriodStatus
import com.apyar.app.domain.model.ChargeRule
import com.apyar.app.domain.model.ChargeStatus
import com.apyar.app.domain.model.CustomFormula
import com.apyar.app.domain.model.ExpenseCategory
import com.apyar.app.domain.model.InclusionStatus
import com.apyar.app.domain.model.OccupancyStatus
import com.apyar.app.domain.model.Person
import com.apyar.app.domain.model.Role
import com.apyar.app.domain.model.TransactionType
import com.apyar.app.domain.model.Unit
import com.apyar.app.domain.model.UserAccount
import com.apyar.app.domain.usecase.CalculateChargePeriodUseCase
import com.apyar.app.domain.usecase.CheckPermissionUseCase
import com.apyar.app.domain.usecase.CreateAdjustmentUseCase
import com.apyar.app.domain.usecase.CreateChargePeriodUseCase
import com.apyar.app.domain.usecase.CreateChargeRuleUseCase
import com.apyar.app.domain.usecase.CreateCustomFormulaUseCase
import com.apyar.app.domain.usecase.CreateFinancialAccountUseCase
import com.apyar.app.domain.usecase.FinalizeChargePeriodUseCase
import com.apyar.app.domain.usecase.GetFinancialAccountUseCase
import com.apyar.app.domain.usecase.GetFinancialSummaryUseCase
import com.apyar.app.domain.usecase.GetTransactionsUseCase
import com.apyar.app.domain.usecase.GetUserEffectivePermissionsUseCase
import com.apyar.app.domain.usecase.RecordTransactionUseCase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class Stage3FinancialCoreAndCalculationTest {

    private lateinit var buildingRepo: FakeBuildingRepository
    private lateinit var unitRepo: FakeUnitRepository
    private lateinit var personRepo: FakePersonRepository
    private lateinit var userAccountRepo: FakeUserAccountRepository
    private lateinit var buildingMemberRepo: FakeBuildingMemberRepository
    private lateinit var delegatedPermissionRepo: FakeDelegatedPermissionRepository
    private lateinit var auditRepo: FakeAuditRepository
    private lateinit var financialAccountRepo: FakeFinancialAccountRepository
    private lateinit var financialTransactionRepo: FakeFinancialTransactionRepository
    private lateinit var chargeRuleRepo: FakeChargeRuleRepository
    private lateinit var customFormulaRepo: FakeCustomFormulaRepository
    private lateinit var chargePeriodRepo: FakeChargePeriodRepository
    private lateinit var unitChargeRepo: FakeUnitChargeRepository

    private lateinit var getUserEffectivePermissionsUseCase: GetUserEffectivePermissionsUseCase
    private lateinit var checkPermissionUseCase: CheckPermissionUseCase
    private lateinit var calculationEngine: ChargeCalculationEngine
    private lateinit var roundingService: FinancialRoundingService

    private val buildingId = "b-1"
    private val managerUserId = "u-manager"
    private val residentUserId = "u-resident"

    @Before
    fun setUp() = runBlocking {
        buildingRepo = FakeBuildingRepository()
        unitRepo = FakeUnitRepository()
        personRepo = FakePersonRepository()
        userAccountRepo = FakeUserAccountRepository()
        buildingMemberRepo = FakeBuildingMemberRepository()
        delegatedPermissionRepo = FakeDelegatedPermissionRepository()
        auditRepo = FakeAuditRepository()
        financialAccountRepo = FakeFinancialAccountRepository()
        financialTransactionRepo = FakeFinancialTransactionRepository()
        chargeRuleRepo = FakeChargeRuleRepository()
        customFormulaRepo = FakeCustomFormulaRepository()
        chargePeriodRepo = FakeChargePeriodRepository()
        unitChargeRepo = FakeUnitChargeRepository()

        roundingService = FinancialRoundingService()
        calculationEngine = ChargeCalculationEngine(roundingService)

        getUserEffectivePermissionsUseCase = GetUserEffectivePermissionsUseCase(
            buildingMemberRepository = buildingMemberRepo,
            delegatedPermissionRepository = delegatedPermissionRepo,
            userAccountRepository = userAccountRepo
        )

        checkPermissionUseCase = CheckPermissionUseCase(getUserEffectivePermissionsUseCase)

        // Seed Building
        buildingRepo.createBuilding(
            Building(id = buildingId, name = "ساختمان نگین", address = "تهران، ونک", city = "تهران", unitCount = 10)
        )

        // Seed Manager User & Member
        val managerPerson = Person(id = "p-manager", fullName = "مدیر ساختمان", mobileNumber = "09121111111")
        personRepo.createPerson(managerPerson)
        userAccountRepo.createAccount(
            UserAccount(id = managerUserId, personId = managerPerson.id, mobileNumber = managerPerson.mobileNumber, accountStatus = AccountStatus.ACTIVE)
        )
        buildingMemberRepo.addMember(
            BuildingMember(id = "m-manager", buildingId = buildingId, userId = managerUserId, role = Role.BUILDING_MANAGER)
        )

        // Seed Resident User & Member
        val residentPerson = Person(id = "p-resident", fullName = "ساکن ساختمان", mobileNumber = "09122222222")
        personRepo.createPerson(residentPerson)
        userAccountRepo.createAccount(
            UserAccount(id = residentUserId, personId = residentPerson.id, mobileNumber = residentPerson.mobileNumber, accountStatus = AccountStatus.ACTIVE)
        )
        buildingMemberRepo.addMember(
            BuildingMember(id = "m-resident", buildingId = buildingId, userId = residentUserId, role = Role.RESIDENT)
        )
    }

    /**
     * Test 1: Equal sharing across 10 units for 1,000,000 Tomans.
     * Each gets 100,000 and the sum is exactly 1,000,000.
     */
    @Test
    fun test1_EqualSharing_ExactDivision() = runBlocking {
        val units = (1..10).map { i ->
            Unit(id = "u-$i", buildingId = buildingId, unitNumber = "$i", floor = 1, areaSquareMeters = 100.0, residentCount = 2)
        }
        val specs = units.map { UnitInclusionSpec(it, InclusionStatus.INCLUDED) }
        val rule = ChargeRule(
            id = "rule-equal",
            buildingId = buildingId,
            name = "تسهیم مساوی",
            calculationType = CalculationType.EQUAL,
            createdBy = managerUserId
        )

        val preview = calculationEngine.calculate(
            totalAmount = 1_000_000L,
            rule = rule,
            unitSpecs = specs
        )

        assertEquals(10, preview.unitCharges.size)
        preview.unitCharges.forEach { charge ->
            assertEquals(100_000L, charge.unitCharge.finalAmount)
        }
        assertEquals(1_000_000L, preview.calculatedTotal)
        assertEquals(0L, preview.roundingDifference)
    }

    /**
     * Test 2: Area-based sharing (3 units: 50m, 100m, 150m, total 3,000,000 -> 500k, 1M, 1.5M).
     */
    @Test
    fun test2_AreaBasedSharing() = runBlocking {
        val u1 = Unit(id = "u-1", buildingId = buildingId, unitNumber = "1", floor = 1, areaSquareMeters = 50.0, residentCount = 2)
        val u2 = Unit(id = "u-2", buildingId = buildingId, unitNumber = "2", floor = 1, areaSquareMeters = 100.0, residentCount = 2)
        val u3 = Unit(id = "u-3", buildingId = buildingId, unitNumber = "3", floor = 2, areaSquareMeters = 150.0, residentCount = 2)
        val specs = listOf(u1, u2, u3).map { UnitInclusionSpec(it, InclusionStatus.INCLUDED) }

        val rule = ChargeRule(
            id = "rule-area",
            buildingId = buildingId,
            name = "تسهیم متراژی",
            calculationType = CalculationType.AREA_BASED,
            createdBy = managerUserId
        )

        val preview = calculationEngine.calculate(
            totalAmount = 3_000_000L,
            rule = rule,
            unitSpecs = specs
        )

        val shareMap = preview.unitCharges.associate { it.unit.id to it.unitCharge.finalAmount }
        assertEquals(500_000L, shareMap["u-1"])
        assertEquals(1_000_000L, shareMap["u-2"])
        assertEquals(1_500_000L, shareMap["u-3"])
        assertEquals(3_000_000L, preview.calculatedTotal)
    }

    /**
     * Test 3: Resident-based sharing (3 units: 1 person, 2 persons, 3 persons, total 6,000,000 -> 1M, 2M, 3M).
     */
    @Test
    fun test3_ResidentBasedSharing() = runBlocking {
        val u1 = Unit(id = "u-1", buildingId = buildingId, unitNumber = "1", floor = 1, areaSquareMeters = 100.0, residentCount = 1)
        val u2 = Unit(id = "u-2", buildingId = buildingId, unitNumber = "2", floor = 1, areaSquareMeters = 100.0, residentCount = 2)
        val u3 = Unit(id = "u-3", buildingId = buildingId, unitNumber = "3", floor = 2, areaSquareMeters = 100.0, residentCount = 3)
        val specs = listOf(u1, u2, u3).map { UnitInclusionSpec(it, InclusionStatus.INCLUDED) }

        val rule = ChargeRule(
            id = "rule-res",
            buildingId = buildingId,
            name = "تسهیم نفراتی",
            calculationType = CalculationType.RESIDENT_BASED,
            createdBy = managerUserId
        )

        val preview = calculationEngine.calculate(
            totalAmount = 6_000_000L,
            rule = rule,
            unitSpecs = specs
        )

        val shareMap = preview.unitCharges.associate { it.unit.id to it.unitCharge.finalAmount }
        assertEquals(1_000_000L, shareMap["u-1"])
        assertEquals(2_000_000L, shareMap["u-2"])
        assertEquals(3_000_000L, shareMap["u-3"])
        assertEquals(6_000_000L, preview.calculatedTotal)
    }

    /**
     * Test 4: Combined Area and Resident formula (e.g. 50% Area + 50% Resident).
     */
    @Test
    fun test4_CombinedAreaAndResidentFormula() = runBlocking {
        // u1: 100m (50% of 200m area), 1 person (25% of 4 persons) -> (0.5 * 0.5) + (0.5 * 0.25) = 0.25 + 0.125 = 37.5%
        // u2: 100m (50% of 200m area), 3 persons (75% of 4 persons) -> (0.5 * 0.5) + (0.5 * 0.75) = 0.25 + 0.375 = 62.5%
        val u1 = Unit(id = "u-1", buildingId = buildingId, unitNumber = "1", floor = 1, areaSquareMeters = 100.0, residentCount = 1)
        val u2 = Unit(id = "u-2", buildingId = buildingId, unitNumber = "2", floor = 1, areaSquareMeters = 100.0, residentCount = 3)
        val specs = listOf(u1, u2).map { UnitInclusionSpec(it, InclusionStatus.INCLUDED) }

        val rule = ChargeRule(
            id = "rule-comb",
            buildingId = buildingId,
            name = "ترکیبی متراژ و نفرات",
            calculationType = CalculationType.AREA_AND_RESIDENT,
            formulaDefinition = """{"areaWeight":0.5, "residentWeight":0.5}""",
            createdBy = managerUserId
        )

        val preview = calculationEngine.calculate(
            totalAmount = 8_000_000L,
            rule = rule,
            unitSpecs = specs
        )

        val shareMap = preview.unitCharges.associate { it.unit.id to it.unitCharge.finalAmount }
        assertEquals(3_000_000L, shareMap["u-1"]) // 37.5% of 8M = 3M
        assertEquals(5_000_000L, shareMap["u-2"]) // 62.5% of 8M = 5M
        assertEquals(8_000_000L, preview.calculatedTotal)
    }

    /**
     * Test 5: Custom Formula execution (custom weights).
     */
    @Test
    fun test5_CustomFormula() = runBlocking {
        val u1 = Unit(id = "u-1", buildingId = buildingId, unitNumber = "1", floor = 1, areaSquareMeters = 100.0, residentCount = 1)
        val u2 = Unit(id = "u-2", buildingId = buildingId, unitNumber = "2", floor = 1, areaSquareMeters = 100.0, residentCount = 1)
        val specs = listOf(u1, u2).map { UnitInclusionSpec(it, InclusionStatus.INCLUDED) }

        val customFormula = CustomFormula(
            id = "f-custom",
            buildingId = buildingId,
            name = "فرمول مصوب مجمع",
            description = "فرمول توافقی ساکنین",
            definition = """{"weights":{"u-1":0.7,"u-2":0.3}}""",
            approvedBy = managerUserId
        )

        val rule = ChargeRule(
            id = "rule-custom",
            buildingId = buildingId,
            name = "فرمول سفارشی",
            calculationType = CalculationType.CUSTOM,
            formulaDefinition = customFormula.definition,
            createdBy = managerUserId
        )

        val preview = calculationEngine.calculate(
            totalAmount = 10_000_000L,
            rule = rule,
            unitSpecs = specs,
            customFormula = customFormula
        )

        val shareMap = preview.unitCharges.associate { it.unit.id to it.unitCharge.finalAmount }
        assertEquals(7_000_000L, shareMap["u-1"])
        assertEquals(3_000_000L, shareMap["u-2"])
        assertEquals(10_000_000L, preview.calculatedTotal)
    }

    /**
     * Test 6: Rounding test with odd division (1,000,000 / 3).
     * Exact integer distribution guarantees sum == 1,000,000 (333,334 + 333,333 + 333,333).
     */
    @Test
    fun test6_RoundingWithOddDivision_HamiltonMethod() = runBlocking {
        val units = (1..3).map { i ->
            Unit(id = "u-$i", buildingId = buildingId, unitNumber = "$i", floor = 1, areaSquareMeters = 100.0, residentCount = 1)
        }
        val specs = units.map { UnitInclusionSpec(it, InclusionStatus.INCLUDED) }
        val rule = ChargeRule(
            id = "rule-equal",
            buildingId = buildingId,
            name = "تسهیم مساوی",
            calculationType = CalculationType.EQUAL,
            createdBy = managerUserId
        )

        val preview = calculationEngine.calculate(
            totalAmount = 1_000_000L,
            rule = rule,
            unitSpecs = specs
        )

        val amounts = preview.unitCharges.map { it.unitCharge.finalAmount }
        assertEquals(3, amounts.size)
        // Sum MUST be exactly 1,000,000 without losing or gaining a single Toman
        assertEquals(1_000_000L, amounts.sum())
        assertTrue(amounts.contains(333334L))
        assertTrue(amounts.contains(333333L))
    }

    /**
     * Test 7: Vacant unit excluded from resident-based calculation.
     */
    @Test
    fun test7_VacantUnit_ZeroResidentInResidentBasedCalculation() = runBlocking {
        val u1 = Unit(id = "u-1", buildingId = buildingId, unitNumber = "1", floor = 1, areaSquareMeters = 100.0, residentCount = 0, occupancyStatus = OccupancyStatus.VACANT)
        val u2 = Unit(id = "u-2", buildingId = buildingId, unitNumber = "2", floor = 1, areaSquareMeters = 100.0, residentCount = 2, occupancyStatus = OccupancyStatus.OCCUPIED)
        val specs = listOf(u1, u2).map { UnitInclusionSpec(it, InclusionStatus.INCLUDED) }

        val rule = ChargeRule(
            id = "rule-res",
            buildingId = buildingId,
            name = "تسهیم نفراتی",
            calculationType = CalculationType.RESIDENT_BASED,
            createdBy = managerUserId
        )

        val preview = calculationEngine.calculate(
            totalAmount = 2_000_000L,
            rule = rule,
            unitSpecs = specs
        )

        val shareMap = preview.unitCharges.associate { it.unit.id to it.unitCharge.finalAmount }
        assertEquals(0L, shareMap["u-1"])
        assertEquals(2_000_000L, shareMap["u-2"])
        assertEquals(2_000_000L, preview.calculatedTotal)
    }

    /**
     * Test 8: Vacant unit still participates in area-based calculation.
     */
    @Test
    fun test8_VacantUnit_ParticipatesInAreaBasedCalculation() = runBlocking {
        val u1 = Unit(id = "u-1", buildingId = buildingId, unitNumber = "1", floor = 1, areaSquareMeters = 100.0, residentCount = 0, occupancyStatus = OccupancyStatus.VACANT)
        val u2 = Unit(id = "u-2", buildingId = buildingId, unitNumber = "2", floor = 1, areaSquareMeters = 100.0, residentCount = 2, occupancyStatus = OccupancyStatus.OCCUPIED)
        val specs = listOf(u1, u2).map { UnitInclusionSpec(it, InclusionStatus.INCLUDED) }

        val rule = ChargeRule(
            id = "rule-area",
            buildingId = buildingId,
            name = "تسهیم متراژی",
            calculationType = CalculationType.AREA_BASED,
            createdBy = managerUserId
        )

        val preview = calculationEngine.calculate(
            totalAmount = 2_000_000L,
            rule = rule,
            unitSpecs = specs
        )

        val shareMap = preview.unitCharges.associate { it.unit.id to it.unitCharge.finalAmount }
        assertEquals(1_000_000L, shareMap["u-1"])
        assertEquals(1_000_000L, shareMap["u-2"])
        assertEquals(2_000_000L, preview.calculatedTotal)
    }

    /**
     * Test 9: Excluded unit does not receive charge (0 share).
     */
    @Test
    fun test9_ExcludedUnit_DoesNotParticipate() = runBlocking {
        val u1 = Unit(id = "u-1", buildingId = buildingId, unitNumber = "1", floor = 1, areaSquareMeters = 100.0, residentCount = 2)
        val u2 = Unit(id = "u-2", buildingId = buildingId, unitNumber = "2", floor = 1, areaSquareMeters = 100.0, residentCount = 2)
        val specs = listOf(
            UnitInclusionSpec(u1, InclusionStatus.INCLUDED),
            UnitInclusionSpec(u2, InclusionStatus.EXCLUDED)
        )

        val rule = ChargeRule(
            id = "rule-equal",
            buildingId = buildingId,
            name = "تسهیم مساوی",
            calculationType = CalculationType.EQUAL,
            createdBy = managerUserId
        )

        val preview = calculationEngine.calculate(
            totalAmount = 500_000L,
            rule = rule,
            unitSpecs = specs
        )

        val shareMap = preview.unitCharges.associate { it.unit.id to it.unitCharge.finalAmount }
        assertEquals(500_000L, shareMap["u-1"])
        assertEquals(0L, shareMap["u-2"])
        assertEquals(500_000L, preview.calculatedTotal)
    }

    /**
     * Test 10: Period Finalization locks period and records ledger transactions.
     */
    @Test
    fun test10_PeriodFinalization_LocksAndRecordsLedger() = runBlocking {
        val createAccountUseCase = CreateFinancialAccountUseCase(financialAccountRepo, checkPermissionUseCase, auditRepo)
        createAccountUseCase(managerUserId, buildingId, "صندوق ساختمان", "تومان", 0L)

        val u1 = Unit(id = "u-1", buildingId = buildingId, unitNumber = "1", floor = 1, areaSquareMeters = 100.0, residentCount = 2)
        val u2 = Unit(id = "u-2", buildingId = buildingId, unitNumber = "2", floor = 1, areaSquareMeters = 100.0, residentCount = 2)
        unitRepo.createUnit(u1)
        unitRepo.createUnit(u2)

        val rule = ChargeRule(id = "rule-1", buildingId = buildingId, name = "مساوی", calculationType = CalculationType.EQUAL, createdBy = managerUserId)
        chargeRuleRepo.insertChargeRule(rule)

        val createPeriodUseCase = CreateChargePeriodUseCase(chargePeriodRepo, chargeRuleRepo, checkPermissionUseCase, auditRepo)
        val period = createPeriodUseCase(
            userId = managerUserId,
            buildingId = buildingId,
            title = "شارژ تیر",
            startDate = 1000L,
            endDate = 2000L,
            totalAmount = 1_000_000L,
            chargeRuleId = rule.id
        ).getOrThrow()

        val calculateUseCase = CalculateChargePeriodUseCase(
            chargePeriodRepo, chargeRuleRepo, unitChargeRepo, checkPermissionUseCase, auditRepo, calculationEngine
        )
        val specs = listOf(u1, u2).map { UnitInclusionSpec(it, InclusionStatus.INCLUDED) }
        calculateUseCase(managerUserId, buildingId, period.id, specs)

        val finalizeUseCase = FinalizeChargePeriodUseCase(
            chargePeriodRepo, unitChargeRepo, financialAccountRepo, financialTransactionRepo, checkPermissionUseCase, auditRepo
        )
        val finalizedPeriod = finalizeUseCase(managerUserId, buildingId, period.id).getOrThrow()

        assertEquals(ChargePeriodStatus.FINALIZED, finalizedPeriod.status)
        assertNotNull(finalizedPeriod.finalizedAt)

        // Verify ledger transactions were created for issued charges
        val txs = financialTransactionRepo.getTransactionsByBuildingId(buildingId)
        assertEquals(2, txs.size)
        assertTrue(txs.all { it.type == TransactionType.CHARGE_INVOICE })

        // Attempting to recalculate finalized period must fail
        val recalculateResult = calculateUseCase(managerUserId, buildingId, period.id, specs)
        assertTrue(recalculateResult.isFailure)
    }

    /**
     * Test 11: Manual adjustment updates charge amount and remaining amount.
     */
    @Test
    fun test11_ManualAdjustment() = runBlocking {
        val createAccountUseCase = CreateFinancialAccountUseCase(financialAccountRepo, checkPermissionUseCase, auditRepo)
        val account = createAccountUseCase(managerUserId, buildingId, "صندوق ساختمان", "تومان", 0L).getOrThrow()

        val u1 = Unit(id = "u-1", buildingId = buildingId, unitNumber = "1", floor = 1, areaSquareMeters = 100.0, residentCount = 2)
        unitRepo.createUnit(u1)

        val rule = ChargeRule(id = "rule-1", buildingId = buildingId, name = "مساوی", calculationType = CalculationType.EQUAL, createdBy = managerUserId)
        chargeRuleRepo.insertChargeRule(rule)

        val createPeriodUseCase = CreateChargePeriodUseCase(chargePeriodRepo, chargeRuleRepo, checkPermissionUseCase, auditRepo)
        val period = createPeriodUseCase(managerUserId, buildingId, "شارژ", 1000L, 2000L, 500_000L, rule.id).getOrThrow()

        val calculateUseCase = CalculateChargePeriodUseCase(
            chargePeriodRepo, chargeRuleRepo, unitChargeRepo, checkPermissionUseCase, auditRepo, calculationEngine
        )
        calculateUseCase(managerUserId, buildingId, period.id, listOf(UnitInclusionSpec(u1, InclusionStatus.INCLUDED)))

        val finalizeUseCase = FinalizeChargePeriodUseCase(
            chargePeriodRepo, unitChargeRepo, financialAccountRepo, financialTransactionRepo, checkPermissionUseCase, auditRepo
        )
        finalizeUseCase(managerUserId, buildingId, period.id)

        val unitCharge = unitChargeRepo.getUnitChargesByPeriodId(period.id).first()

        val adjustmentUseCase = CreateAdjustmentUseCase(
            unitChargeRepo, financialAccountRepo, financialTransactionRepo, checkPermissionUseCase, auditRepo
        )
        // Add a 50,000 Toman adjustment (e.g. penalty or extra service)
        val adjusted = adjustmentUseCase(
            userId = managerUserId,
            buildingId = buildingId,
            unitChargeId = unitCharge.id,
            adjustmentAmount = 50_000L,
            reason = "جریمه دیرکرد یا خدمات ویژه"
        ).getOrThrow()

        assertEquals(550_000L, adjusted.finalAmount)
        assertEquals(550_000L, adjusted.remainingAmount)
    }

    /**
     * Test 12: Partial Payment updates paidAmount and charge status.
     */
    @Test
    fun test12_PartialPayment() = runBlocking {
        val u1 = Unit(id = "u-1", buildingId = buildingId, unitNumber = "1", floor = 1, areaSquareMeters = 100.0, residentCount = 2)
        unitRepo.createUnit(u1)
        val rule = ChargeRule(id = "rule-1", buildingId = buildingId, name = "مساوی", calculationType = CalculationType.EQUAL, createdBy = managerUserId)
        chargeRuleRepo.insertChargeRule(rule)

        val createPeriodUseCase = CreateChargePeriodUseCase(chargePeriodRepo, chargeRuleRepo, checkPermissionUseCase, auditRepo)
        val period = createPeriodUseCase(managerUserId, buildingId, "دوره", 1000L, 2000L, 400_000L, rule.id).getOrThrow()

        val calculateUseCase = CalculateChargePeriodUseCase(
            chargePeriodRepo, chargeRuleRepo, unitChargeRepo, checkPermissionUseCase, auditRepo, calculationEngine
        )
        calculateUseCase(managerUserId, buildingId, period.id, listOf(UnitInclusionSpec(u1, InclusionStatus.INCLUDED)))

        val unitCharge = unitChargeRepo.getUnitChargesByPeriodId(period.id).first()

        // Partial payment of 150,000
        unitChargeRepo.updatePayment(unitCharge.id, 150_000L, ChargeStatus.PARTIALLY_PAID)

        val updated = unitChargeRepo.getUnitChargeById(unitCharge.id)
        assertNotNull(updated)
        assertEquals(150_000L, updated!!.paidAmount)
        assertEquals(250_000L, updated.remainingAmount)
        assertEquals(ChargeStatus.PARTIALLY_PAID, updated.status)
    }

    /**
     * Test 13: Financial Transaction Ledger updates account balance accurately.
     */
    @Test
    fun test13_TransactionLedger_AccountBalanceSync() = runBlocking {
        val createAccountUseCase = CreateFinancialAccountUseCase(financialAccountRepo, checkPermissionUseCase, auditRepo)
        val account = createAccountUseCase(managerUserId, buildingId, "صندوق ساختمان", "تومان", 1_000_000L).getOrThrow()

        val recordTxUseCase = RecordTransactionUseCase(
            financialTransactionRepo, financialAccountRepo, checkPermissionUseCase, auditRepo
        )

        // Expense: 300,000
        recordTxUseCase(
            userId = managerUserId,
            buildingId = buildingId,
            accountId = account.id,
            unitId = null,
            type = TransactionType.EXPENSE,
            amount = 300_000L,
            description = "تعمیر آسانسور",
            category = ExpenseCategory.ELEVATOR
        )

        // Income / Charge Payment: 500,000
        recordTxUseCase(
            userId = managerUserId,
            buildingId = buildingId,
            accountId = account.id,
            unitId = "u-1",
            type = TransactionType.CHARGE_PAYMENT,
            amount = 500_000L,
            description = "واریز شارژ واحد 1"
        )

        val updatedAccount = financialAccountRepo.getAccountById(account.id)
        // 1,000,000 - 300,000 + 500,000 = 1,200,000
        assertEquals(1_200_000L, updatedAccount?.balance)

        val txs = financialTransactionRepo.getTransactionsByBuildingId(buildingId)
        assertEquals(2, txs.size)
    }

    /**
     * Test 14: Permission Enforcement (Resident cannot calculate charge or record transactions).
     */
    @Test
    fun test14_PermissionEnforcement_UnauthorizedAccessFails() = runBlocking {
        val createAccountUseCase = CreateFinancialAccountUseCase(financialAccountRepo, checkPermissionUseCase, auditRepo)
        val account = createAccountUseCase(managerUserId, buildingId, "صندوق", "تومان", 0L).getOrThrow()

        val recordTxUseCase = RecordTransactionUseCase(
            financialTransactionRepo, financialAccountRepo, checkPermissionUseCase, auditRepo
        )

        // Resident attempts to record expense without MANAGE_FINANCIAL_DATA permission
        val residentExpenseResult = recordTxUseCase(
            userId = residentUserId,
            buildingId = buildingId,
            accountId = account.id,
            unitId = null,
            type = TransactionType.EXPENSE,
            amount = 100_000L,
            description = "هزینه غیرمجاز"
        )

        assertTrue(residentExpenseResult.isFailure)
        assertTrue(residentExpenseResult.exceptionOrNull()?.message?.contains("مجوز") == true)
    }
}
