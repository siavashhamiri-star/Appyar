package com.apyar.app.test

import com.apyar.app.domain.engine.ChargeCalculationEngine
import com.apyar.app.domain.model.Building
import com.apyar.app.domain.model.BuildingExpense
import com.apyar.app.domain.model.ChargeCalculationMethod
import com.apyar.app.domain.model.ChargePeriod
import com.apyar.app.domain.model.ChargePeriodStatus
import com.apyar.app.domain.model.ComponentType
import com.apyar.app.domain.model.ExpenseCategory
import com.apyar.app.domain.model.FormulaComponent
import com.apyar.app.domain.model.MethodType
import com.apyar.app.domain.model.OccupancyStatus
import com.apyar.app.domain.model.Unit
import com.apyar.app.domain.usecase.AddBuildingExpenseUseCase
import com.apyar.app.domain.usecase.CalculateChargesUseCase
import com.apyar.app.domain.usecase.CreateCalculationMethodUseCase
import com.apyar.app.domain.usecase.CreateChargePeriodUseCase
import com.apyar.app.domain.usecase.GetChargeItemsUseCase
import com.apyar.app.domain.usecase.GetChargePeriodUseCase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.UUID

class Stage7_1ChargeCalculationCoreTest {

    private lateinit var buildingRepo: FakeBuildingRepository
    private lateinit var unitRepo: FakeUnitRepository
    private lateinit var chargePeriodRepo: FakeChargePeriodRepository
    private lateinit var calculationMethodRepo: FakeChargeCalculationMethodRepository
    private lateinit var expenseRepo: FakeBuildingExpenseRepository
    private lateinit var chargeItemRepo: FakeChargeItemRepository

    private lateinit var createChargePeriodUseCase: CreateChargePeriodUseCase
    private lateinit var addBuildingExpenseUseCase: AddBuildingExpenseUseCase
    private lateinit var createCalculationMethodUseCase: CreateCalculationMethodUseCase
    private lateinit var calculateChargesUseCase: CalculateChargesUseCase
    private lateinit var getChargePeriodUseCase: GetChargePeriodUseCase
    private lateinit var getChargeItemsUseCase: GetChargeItemsUseCase

    private val buildingId1 = "b-arghavan"
    private val buildingId2 = "b-niloufar"
    private val adminUserId = "usr-admin-1"

    @Before
    fun setUp() = runBlocking {
        buildingRepo = FakeBuildingRepository()
        unitRepo = FakeUnitRepository()
        chargePeriodRepo = FakeChargePeriodRepository()
        calculationMethodRepo = FakeChargeCalculationMethodRepository()
        expenseRepo = FakeBuildingExpenseRepository()
        chargeItemRepo = FakeChargeItemRepository()

        // Seed Building 1
        buildingRepo.insertBuilding(
            Building(
                id = buildingId1,
                name = "مجتمع مسکونی ارغوان",
                address = "خیابان شریعتی، پلاک ۱۲",
                city = "تهران",
                postalCode = "1939543210",
                unitCount = 4
            )
        )

        // Seed Building 2
        buildingRepo.insertBuilding(
            Building(
                id = buildingId2,
                name = "برج نیلوفر",
                address = "سعادت آباد، میدان کاج",
                city = "تهران",
                postalCode = "1998765432",
                unitCount = 2
            )
        )

        // Seed Units for Building 1 (Total area: 400m², Total residents: 9)
        unitRepo.insertUnit(Unit(id = "u-101", buildingId = buildingId1, unitNumber = "101", floor = 1, areaSquareMeters = 100.0, residentCount = 2, occupancyStatus = OccupancyStatus.OCCUPIED))
        unitRepo.insertUnit(Unit(id = "u-102", buildingId = buildingId1, unitNumber = "102", floor = 1, areaSquareMeters = 120.0, residentCount = 3, occupancyStatus = OccupancyStatus.OCCUPIED))
        unitRepo.insertUnit(Unit(id = "u-201", buildingId = buildingId1, unitNumber = "201", floor = 2, areaSquareMeters = 80.0, residentCount = 0, occupancyStatus = OccupancyStatus.VACANT))
        unitRepo.insertUnit(Unit(id = "u-202", buildingId = buildingId1, unitNumber = "202", floor = 2, areaSquareMeters = 100.0, residentCount = 4, occupancyStatus = OccupancyStatus.OCCUPIED))

        // Seed Units for Building 2
        unitRepo.insertUnit(Unit(id = "u-b2-1", buildingId = buildingId2, unitNumber = "1", floor = 1, areaSquareMeters = 150.0, residentCount = 3, occupancyStatus = OccupancyStatus.OCCUPIED))
        unitRepo.insertUnit(Unit(id = "u-b2-2", buildingId = buildingId2, unitNumber = "2", floor = 1, areaSquareMeters = 150.0, residentCount = 2, occupancyStatus = OccupancyStatus.OCCUPIED))

        createChargePeriodUseCase = CreateChargePeriodUseCase(
            chargePeriodRepository = chargePeriodRepo,
            calculationMethodRepository = calculationMethodRepo
        )

        addBuildingExpenseUseCase = AddBuildingExpenseUseCase(
            buildingExpenseRepository = expenseRepo,
            buildingRepository = buildingRepo,
            chargePeriodRepository = chargePeriodRepo
        )

        createCalculationMethodUseCase = CreateCalculationMethodUseCase(
            calculationMethodRepository = calculationMethodRepo,
            buildingRepository = buildingRepo
        )

        calculateChargesUseCase = CalculateChargesUseCase(
            chargePeriodRepository = chargePeriodRepo,
            calculationMethodRepository = calculationMethodRepo,
            unitRepository = unitRepo,
            chargeItemRepository = chargeItemRepo,
            buildingExpenseRepository = expenseRepo,
            calculationEngine = ChargeCalculationEngine()
        )

        getChargePeriodUseCase = GetChargePeriodUseCase(
            chargePeriodRepository = chargePeriodRepo
        )

        getChargeItemsUseCase = GetChargeItemsUseCase(
            chargeItemRepository = chargeItemRepo,
            chargePeriodRepository = chargePeriodRepo
        )
    }

    @Test
    fun testCreateChargePeriod_successAndValidation() = runBlocking {
        val now = System.currentTimeMillis()
        val start = now
        val end = now + 86400000L * 30
        val due = end + 86400000L * 5

        // Valid creation
        val result = createChargePeriodUseCase(
            buildingId = buildingId1,
            title = "شارژ مرداد ۱۴۰۵",
            periodStart = start,
            periodEnd = end,
            dueDate = due,
            totalBuildingCost = 10_000_000L,
            createdBy = adminUserId
        )

        assertTrue(result.isSuccess)
        val period = result.getOrNull()!!
        assertEquals("شارژ مرداد ۱۴۰۵", period.title)
        assertEquals(buildingId1, period.buildingId)
        assertEquals(ChargePeriodStatus.DRAFT, period.status)
        assertEquals(10_000_000L, period.totalBuildingCost)

        // Invalid: start date after end date
        val invalidDatesResult = createChargePeriodUseCase(
            buildingId = buildingId1,
            title = "دوره نامعتبر",
            periodStart = end,
            periodEnd = start,
            createdBy = adminUserId
        )
        assertTrue(invalidDatesResult.isFailure)

        // Invalid: blank title
        val blankTitleResult = createChargePeriodUseCase(
            buildingId = buildingId1,
            title = "   ",
            periodStart = start,
            periodEnd = end,
            createdBy = adminUserId
        )
        assertTrue(blankTitleResult.isFailure)

        // Invalid: negative cost
        val negativeCostResult = createChargePeriodUseCase(
            buildingId = buildingId1,
            title = "هزینه منفی",
            periodStart = start,
            periodEnd = end,
            totalBuildingCost = -5000L,
            createdBy = adminUserId
        )
        assertTrue(negativeCostResult.isFailure)
    }

    @Test
    fun testAddBuildingExpense_successAndValidation() = runBlocking {
        // Create period first
        val period = createChargePeriodUseCase(
            buildingId = buildingId1,
            title = "شارژ تابستان",
            periodStart = 1000L,
            periodEnd = 2000L,
            createdBy = adminUserId
        ).getOrThrow()

        // Add valid expense
        val expenseResult = addBuildingExpenseUseCase(
            buildingId = buildingId1,
            title = "سرویس و نگهداری آسانسور",
            amount = 2_500_000L,
            category = ExpenseCategory.ELEVATOR_MAINTENANCE,
            chargePeriodId = period.id,
            createdBy = adminUserId
        )

        assertTrue(expenseResult.isSuccess)
        val expense = expenseResult.getOrNull()!!
        assertEquals(2_500_000L, expense.amount)
        assertEquals(period.id, expense.chargePeriodId)

        // Invalid: amount <= 0
        val zeroExpenseResult = addBuildingExpenseUseCase(
            buildingId = buildingId1,
            title = "هزینه صفر",
            amount = 0L,
            createdBy = adminUserId
        )
        assertTrue(zeroExpenseResult.isFailure)

        // Invalid: unknown building
        val invalidBuildingResult = addBuildingExpenseUseCase(
            buildingId = "unknown-b-id",
            title = "هزینه متفرقه",
            amount = 100_000L,
            createdBy = adminUserId
        )
        assertTrue(invalidBuildingResult.isFailure)
    }

    @Test
    fun testCreateCalculationMethod_successAndScriptPrevention() = runBlocking {
        // Create valid method
        val methodResult = createCalculationMethodUseCase(
            buildingId = buildingId1,
            name = "فرمول ترکیبی متراژ ۶۰ و نفرات ۴۰",
            methodType = MethodType.MIXED,
            createdBy = adminUserId,
            components = listOf(
                FormulaComponent(
                    id = UUID.randomUUID().toString(),
                    calculationMethodId = "",
                    componentType = ComponentType.AREA,
                    weight = 0.6
                ),
                FormulaComponent(
                    id = UUID.randomUUID().toString(),
                    calculationMethodId = "",
                    componentType = ComponentType.RESIDENT_COUNT,
                    weight = 0.4
                )
            )
        )

        assertTrue(methodResult.isSuccess)
        val method = methodResult.getOrNull()!!
        assertEquals(MethodType.MIXED, method.methodType)
        assertEquals(2, method.components.size)

        // Attempting to inject executable script must be strictly prevented
        val unsafeMethodResult = createCalculationMethodUseCase(
            buildingId = buildingId1,
            name = "فرمول مخرب",
            methodType = MethodType.CUSTOM,
            createdBy = adminUserId,
            components = listOf(
                FormulaComponent(
                    id = UUID.randomUUID().toString(),
                    calculationMethodId = "",
                    componentType = ComponentType.CUSTOM_RULE,
                    configuration = mapOf("script" to "<script>alert('xss')</script>")
                )
            )
        )

        assertTrue(unsafeMethodResult.isFailure)
    }

    @Test
    fun testCalculateCharges_equalSharing() = runBlocking {
        val totalCost = 10_000_000L // 10 million rials across 4 units -> exactly 2,500,000 each

        val period = createChargePeriodUseCase(
            buildingId = buildingId1,
            title = "دوره تسهیم مساوی",
            periodStart = 1000L,
            periodEnd = 2000L,
            totalBuildingCost = totalCost,
            createdBy = adminUserId
        ).getOrThrow()

        val equalMethod = createCalculationMethodUseCase(
            buildingId = buildingId1,
            name = "روش مساوی",
            methodType = MethodType.EQUAL,
            createdBy = adminUserId
        ).getOrThrow()

        val result = calculateChargesUseCase(
            buildingId = buildingId1,
            chargePeriodId = period.id,
            calculationMethodId = equalMethod.id
        )

        assertTrue(result.isSuccess)
        val items = result.getOrThrow()
        assertEquals(4, items.size)

        // Sum of calculated items MUST equal exactly totalCost
        val sum = items.sumOf { it.finalAmount }
        assertEquals(totalCost, sum)

        // Each unit receives 2,500,000
        items.forEach {
            assertEquals(2_500_000L, it.baseAmount)
            assertEquals(2_500_000L, it.finalAmount)
        }

        // Verify period is updated to CALCULATED
        val updatedPeriod = getChargePeriodUseCase(buildingId1, period.id).getOrThrow()
        assertEquals(ChargePeriodStatus.CALCULATED, updatedPeriod.status)
    }

    @Test
    fun testCalculateCharges_areaBasedSharing() = runBlocking {
        // Building 1 total area = 100 + 120 + 80 + 100 = 400 m²
        // Total cost = 4_000_000 -> 10,000 per m²
        // u-101 (100m²) -> 1,000,000
        // u-102 (120m²) -> 1,200,000
        // u-201 (80m²)  -> 800,000
        // u-202 (100m²) -> 1,000,000
        val totalCost = 4_000_000L

        val period = createChargePeriodUseCase(
            buildingId = buildingId1,
            title = "دوره متراژی",
            periodStart = 1000L,
            periodEnd = 2000L,
            totalBuildingCost = totalCost,
            createdBy = adminUserId
        ).getOrThrow()

        val areaMethod = createCalculationMethodUseCase(
            buildingId = buildingId1,
            name = "روش متراژی",
            methodType = MethodType.AREA_BASED,
            createdBy = adminUserId
        ).getOrThrow()

        val result = calculateChargesUseCase(
            buildingId = buildingId1,
            chargePeriodId = period.id,
            calculationMethodId = areaMethod.id
        )

        assertTrue(result.isSuccess)
        val items = result.getOrThrow()
        val itemMap = items.associateBy { it.unitId }

        assertEquals(1_000_000L, itemMap["u-101"]?.baseAmount)
        assertEquals(1_200_000L, itemMap["u-102"]?.baseAmount)
        assertEquals(800_000L, itemMap["u-201"]?.baseAmount)
        assertEquals(1_000_000L, itemMap["u-202"]?.baseAmount)
        assertEquals(totalCost, items.sumOf { it.finalAmount })
    }

    @Test
    fun testCalculateCharges_residentBasedSharing() = runBlocking {
        // Total residents = 2 + 3 + 0 + 4 = 9 residents
        // Total cost = 9_000_000
        // u-101 (2 res) -> 2,000,000
        // u-102 (3 res) -> 3,000,000
        // u-201 (0 res) -> 0
        // u-202 (4 res) -> 4,000,000
        val totalCost = 9_000_000L

        val period = createChargePeriodUseCase(
            buildingId = buildingId1,
            title = "دوره نفرمحور",
            periodStart = 1000L,
            periodEnd = 2000L,
            totalBuildingCost = totalCost,
            createdBy = adminUserId
        ).getOrThrow()

        val resMethod = createCalculationMethodUseCase(
            buildingId = buildingId1,
            name = "روش نفرات",
            methodType = MethodType.RESIDENT_BASED,
            createdBy = adminUserId
        ).getOrThrow()

        val result = calculateChargesUseCase(
            buildingId = buildingId1,
            chargePeriodId = period.id,
            calculationMethodId = resMethod.id
        )

        assertTrue(result.isSuccess)
        val items = result.getOrThrow()
        val itemMap = items.associateBy { it.unitId }

        assertEquals(2_000_000L, itemMap["u-101"]?.baseAmount)
        assertEquals(3_000_000L, itemMap["u-102"]?.baseAmount)
        assertEquals(0L, itemMap["u-201"]?.baseAmount)
        assertEquals(4_000_000L, itemMap["u-202"]?.baseAmount)
        assertEquals(totalCost, items.sumOf { it.finalAmount })
    }

    @Test
    fun testBuildingIsolation_chargesAndDataStrictlySeparated() = runBlocking {
        // Create period in Building 2
        val periodB2 = createChargePeriodUseCase(
            buildingId = buildingId2,
            title = "دوره برج نیلوفر",
            periodStart = 1000L,
            periodEnd = 2000L,
            totalBuildingCost = 6_000_000L,
            createdBy = adminUserId
        ).getOrThrow()

        // Trying to calculate Building 2 period under Building 1 must fail
        val calculationCrossFail = calculateChargesUseCase(
            buildingId = buildingId1,
            chargePeriodId = periodB2.id
        )
        assertTrue(calculationCrossFail.isFailure)

        // Querying items of Building 2 under Building 1 must return empty/error
        val queryCross = getChargeItemsUseCase(buildingId1, periodB2.id)
        assertTrue(queryCross.isFailure || queryCross.getOrThrow().isEmpty())
    }

    @Test
    fun testImmutability_cannotRecalculateFinalizedOrSettledPeriod() = runBlocking {
        val period = createChargePeriodUseCase(
            buildingId = buildingId1,
            title = "دوره قطعی شده",
            periodStart = 1000L,
            periodEnd = 2000L,
            totalBuildingCost = 5_000_000L,
            createdBy = adminUserId
        ).getOrThrow()

        // Calculate first time -> becomes CALCULATED
        val firstCalc = calculateChargesUseCase(buildingId1, period.id)
        assertTrue(firstCalc.isSuccess)

        // Finalize period
        chargePeriodRepo.updateStatus(period.id, ChargePeriodStatus.FINALIZED, System.currentTimeMillis())

        // Recalculating finalized period must fail to preserve historical integrity
        val secondCalc = calculateChargesUseCase(buildingId1, period.id)
        assertTrue(secondCalc.isFailure)
    }

    @Test
    fun testChargeCalculation_withExpensesSummation() = runBlocking {
        // Period created with totalBuildingCost = 0
        val period = createChargePeriodUseCase(
            buildingId = buildingId1,
            title = "دوره تجمیع هزینه‌ها",
            periodStart = 1000L,
            periodEnd = 2000L,
            totalBuildingCost = 0L,
            createdBy = adminUserId
        ).getOrThrow()

        // Add 2 expenses attached to this period: 1,500,000 + 2,500,000 = 4,000,000
        addBuildingExpenseUseCase(
            buildingId = buildingId1,
            title = "هزینه نظافت",
            amount = 1_500_000L,
            chargePeriodId = period.id,
            createdBy = adminUserId
        )
        addBuildingExpenseUseCase(
            buildingId = buildingId1,
            title = "هزینه باغبانی",
            amount = 2_500_000L,
            chargePeriodId = period.id,
            createdBy = adminUserId
        )

        // Calculate charges
        val result = calculateChargesUseCase(
            buildingId = buildingId1,
            chargePeriodId = period.id
        )

        assertTrue(result.isSuccess)
        val items = result.getOrThrow()
        assertEquals(4_000_000L, items.sumOf { it.finalAmount })

        // Check updated period cost
        val updatedPeriod = getChargePeriodUseCase(buildingId1, period.id).getOrThrow()
        assertEquals(4_000_000L, updatedPeriod.totalBuildingCost)
    }
}
