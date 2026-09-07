package com.apyar.app.test

import com.apyar.app.domain.model.RelationType
import com.apyar.app.domain.usecase.AssignPersonToUnitUseCase
import com.apyar.app.domain.usecase.CreateBuildingUseCase
import com.apyar.app.domain.usecase.CreatePersonUseCase
import com.apyar.app.domain.usecase.CreateUnitUseCase
import com.apyar.app.domain.usecase.GetBuildingsUseCase
import com.apyar.app.domain.usecase.GetPeopleByUnitUseCase
import com.apyar.app.domain.usecase.GetUnitsByBuildingUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class Stage1CoreLogicTest {

    private lateinit var buildingRepository: FakeBuildingRepository
    private lateinit var unitRepository: FakeUnitRepository
    private lateinit var personRepository: FakePersonRepository

    private lateinit var createBuildingUseCase: CreateBuildingUseCase
    private lateinit var getBuildingsUseCase: GetBuildingsUseCase
    private lateinit var createUnitUseCase: CreateUnitUseCase
    private lateinit var getUnitsByBuildingUseCase: GetUnitsByBuildingUseCase
    private lateinit var createPersonUseCase: CreatePersonUseCase
    private lateinit var assignPersonToUnitUseCase: AssignPersonToUnitUseCase
    private lateinit var getPeopleByUnitUseCase: GetPeopleByUnitUseCase

    @Before
    fun setUp() {
        buildingRepository = FakeBuildingRepository()
        unitRepository = FakeUnitRepository()
        personRepository = FakePersonRepository()

        createBuildingUseCase = CreateBuildingUseCase(buildingRepository)
        getBuildingsUseCase = GetBuildingsUseCase(buildingRepository)
        createUnitUseCase = CreateUnitUseCase(unitRepository)
        getUnitsByBuildingUseCase = GetUnitsByBuildingUseCase(unitRepository)
        createPersonUseCase = CreatePersonUseCase(personRepository)
        assignPersonToUnitUseCase = AssignPersonToUnitUseCase(personRepository)
        getPeopleByUnitUseCase = GetPeopleByUnitUseCase(personRepository)
    }

    @Test
    fun test1_createBuilding_success() = runTest {
        val result = createBuildingUseCase(
            name = "برج بهاران",
            address = "خیابان لاله، کوچه دوم",
            city = "تهران",
            postalCode = "1234567890",
            unitCount = 20
        )

        assertTrue(result.isSuccess)
        val building = result.getOrThrow()
        assertNotNull(building.id)
        assertEquals("برج بهاران", building.name)
        assertEquals(20, building.unitCount)
        assertTrue(building.isActive)

        val allBuildings = getBuildingsUseCase().first()
        assertEquals(1, allBuildings.size)
        assertEquals("برج بهاران", allBuildings[0].name)
    }

    @Test
    fun test2_createUnit_success() = runTest {
        val building = createBuildingUseCase(
            name = "ساختمان یاس",
            address = "پلاک ۵",
            city = "تهران",
            postalCode = "9876543210",
            unitCount = 10
        ).getOrThrow()

        val unitResult = createUnitUseCase(
            buildingId = building.id,
            unitNumber = "101",
            floor = 1,
            areaSquareMeters = 120.5,
            residentCount = 3
        )

        assertTrue(unitResult.isSuccess)
        val unit = unitResult.getOrThrow()
        assertEquals(building.id, unit.buildingId)
        assertEquals("101", unit.unitNumber)
        assertEquals(1, unit.floor)
        assertEquals(120.5, unit.areaSquareMeters, 0.01)
        assertEquals(3, unit.residentCount)
    }

    @Test
    fun test3_preventDuplicateUnitNumberInSameBuilding() = runTest {
        val building = createBuildingUseCase(
            name = "مجتمع نیلوفر",
            address = "بلوار شقایق",
            city = "شیراز",
            postalCode = "1122334455",
            unitCount = 8
        ).getOrThrow()

        // 1st unit with number "202"
        val firstUnitResult = createUnitUseCase(
            buildingId = building.id,
            unitNumber = "202",
            floor = 2,
            areaSquareMeters = 85.0,
            residentCount = 2
        )
        assertTrue(firstUnitResult.isSuccess)

        // 2nd unit with DUPLICATE number "202" in the SAME building
        val duplicateUnitResult = createUnitUseCase(
            buildingId = building.id,
            unitNumber = "202",
            floor = 2,
            areaSquareMeters = 90.0,
            residentCount = 4
        )

        assertTrue(duplicateUnitResult.isFailure)
        val exception = duplicateUnitResult.exceptionOrNull()
        assertTrue(exception is IllegalStateException)
        assertEquals("واحدی با این شماره قبلاً در این ساختمان ثبت شده است.", exception?.message)
    }

    @Test
    fun test4_createPerson_success() = runTest {
        val personResult = createPersonUseCase(
            firstName = "سیاوش",
            lastName = "حمیری",
            mobileNumber = "09121234567",
            email = "siavash@example.com"
        )

        assertTrue(personResult.isSuccess)
        val person = personResult.getOrThrow()
        assertNotNull(person.id)
        assertEquals("سیاوش", person.firstName)
        assertEquals("حمیری", person.lastName)
        assertEquals("سیاوش حمیری", person.fullName)
        assertEquals("09121234567", person.mobileNumber)
        assertTrue(person.isActive)
    }

    @Test
    fun test5_assignPersonToUnit_success() = runTest {
        val building = createBuildingUseCase("سپیدار", "خیابان ۱", "اصفهان", "12345", 4).getOrThrow()
        val unit = createUnitUseCase(building.id, "10", 1, 95.0, 2).getOrThrow()
        val person = createPersonUseCase("علی", "رضایی", "09130000000").getOrThrow()

        val assignResult = assignPersonToUnitUseCase(
            unitId = unit.id,
            personId = person.id,
            relationType = RelationType.OWNER
        )

        assertTrue(assignResult.isSuccess)
        val relation = assignResult.getOrThrow()
        assertEquals(unit.id, relation.unitId)
        assertEquals(person.id, relation.personId)
        assertEquals(RelationType.OWNER, relation.relationType)
        assertTrue(relation.isActive)

        val occupants = getPeopleByUnitUseCase(unit.id).first()
        assertEquals(1, occupants.size)
        assertEquals("علی رضایی", occupants[0].person.fullName)
        assertEquals(RelationType.OWNER, occupants[0].relation.relationType)
    }

    @Test
    fun test6_recordHistoricalTenancyAndOwnership() = runTest {
        val building = createBuildingUseCase("ارغوان", "خیابان ۲", "تبریز", "54321", 6).getOrThrow()
        val unit = createUnitUseCase(building.id, "5", 2, 110.0, 3).getOrThrow()

        val owner = createPersonUseCase("حسن", "محمدی", "09141111111").getOrThrow()
        val tenant1 = createPersonUseCase("سارا", "کریمی", "09142222222").getOrThrow()
        val tenant2 = createPersonUseCase("رضا", "حسینی", "09143333333").getOrThrow()

        // Assign Owner
        assignPersonToUnitUseCase(unit.id, owner.id, RelationType.OWNER)

        // Assign Tenant 1 and then end tenancy
        val tenant1Relation = assignPersonToUnitUseCase(unit.id, tenant1.id, RelationType.TENANT).getOrThrow()
        personRepository.endRelation(tenant1Relation.id, System.currentTimeMillis())

        // Assign Tenant 2
        assignPersonToUnitUseCase(unit.id, tenant2.id, RelationType.TENANT)

        val history = getPeopleByUnitUseCase(unit.id).first()
        assertEquals(3, history.size) // All 3 records preserved (history maintained!)
    }

    @Test
    fun test7_getUnitsByBuilding_filteringAndActive() = runTest {
        val b1 = createBuildingUseCase("ساختمان الف", "آدرس ۱", "تهران", "111", 5).getOrThrow()
        val b2 = createBuildingUseCase("ساختمان ب", "آدرس ۲", "تهران", "222", 5).getOrThrow()

        createUnitUseCase(b1.id, "1", 1, 80.0, 1)
        createUnitUseCase(b1.id, "2", 1, 90.0, 2)
        createUnitUseCase(b2.id, "1", 1, 100.0, 3)

        val b1Units = getUnitsByBuildingUseCase(b1.id).first()
        val b2Units = getUnitsByBuildingUseCase(b2.id).first()

        assertEquals(2, b1Units.size)
        assertEquals(1, b2Units.size)
    }
}
