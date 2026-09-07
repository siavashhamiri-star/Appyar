package com.apyar.app.test

import com.apyar.app.domain.model.AccessDeniedException
import com.apyar.app.domain.model.AccountStatus
import com.apyar.app.domain.model.Building
import com.apyar.app.domain.model.BuildingMember
import com.apyar.app.domain.model.DelegatedPermission
import com.apyar.app.domain.model.DelegationStatus
import com.apyar.app.domain.model.Permission
import com.apyar.app.domain.model.Person
import com.apyar.app.domain.model.Role
import com.apyar.app.domain.model.RolePermissionPolicy
import com.apyar.app.domain.model.UserAccount
import com.apyar.app.domain.usecase.AddBuildingMemberUseCase
import com.apyar.app.domain.usecase.CheckPermissionUseCase
import com.apyar.app.domain.usecase.CreateDelegationUseCase
import com.apyar.app.domain.usecase.CreateUserAccountUseCase
import com.apyar.app.domain.usecase.GetUserEffectivePermissionsUseCase
import com.apyar.app.domain.usecase.RevokeDelegationUseCase
import com.apyar.app.domain.usecase.UpdateBuildingMemberRoleUseCase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class Stage2SecurityAndRbacTest {

    private lateinit var buildingRepo: FakeBuildingRepository
    private lateinit var personRepo: FakePersonRepository
    private lateinit var userAccountRepo: FakeUserAccountRepository
    private lateinit var buildingMemberRepo: FakeBuildingMemberRepository
    private lateinit var delegatedPermissionRepo: FakeDelegatedPermissionRepository
    private lateinit var auditRepo: FakeAuditRepository

    private lateinit var getUserEffectivePermissionsUseCase: GetUserEffectivePermissionsUseCase
    private lateinit var checkPermissionUseCase: CheckPermissionUseCase
    private lateinit var createUserAccountUseCase: CreateUserAccountUseCase
    private lateinit var addBuildingMemberUseCase: AddBuildingMemberUseCase
    private lateinit var updateBuildingMemberRoleUseCase: UpdateBuildingMemberRoleUseCase
    private lateinit var createDelegationUseCase: CreateDelegationUseCase
    private lateinit var revokeDelegationUseCase: RevokeDelegationUseCase

    @Before
    fun setup() {
        buildingRepo = FakeBuildingRepository()
        personRepo = FakePersonRepository()
        userAccountRepo = FakeUserAccountRepository()
        buildingMemberRepo = FakeBuildingMemberRepository()
        delegatedPermissionRepo = FakeDelegatedPermissionRepository()
        auditRepo = FakeAuditRepository()

        getUserEffectivePermissionsUseCase = GetUserEffectivePermissionsUseCase(
            buildingMemberRepository = buildingMemberRepo,
            delegatedPermissionRepository = delegatedPermissionRepo,
            userAccountRepository = userAccountRepo
        )

        checkPermissionUseCase = CheckPermissionUseCase(getUserEffectivePermissionsUseCase)

        createUserAccountUseCase = CreateUserAccountUseCase(
            userAccountRepository = userAccountRepo,
            personRepository = personRepo,
            auditRepository = auditRepo
        )

        addBuildingMemberUseCase = AddBuildingMemberUseCase(
            buildingMemberRepository = buildingMemberRepo,
            buildingRepository = buildingRepo,
            userAccountRepository = userAccountRepo,
            checkPermissionUseCase = checkPermissionUseCase,
            auditRepository = auditRepo
        )

        updateBuildingMemberRoleUseCase = UpdateBuildingMemberRoleUseCase(
            buildingMemberRepository = buildingMemberRepo,
            checkPermissionUseCase = checkPermissionUseCase,
            auditRepository = auditRepo
        )

        createDelegationUseCase = CreateDelegationUseCase(
            delegatedPermissionRepository = delegatedPermissionRepo,
            buildingRepository = buildingRepo,
            userAccountRepository = userAccountRepo,
            checkPermissionUseCase = checkPermissionUseCase,
            auditRepository = auditRepo
        )

        revokeDelegationUseCase = RevokeDelegationUseCase(
            delegatedPermissionRepository = delegatedPermissionRepo,
            checkPermissionUseCase = checkPermissionUseCase,
            auditRepository = auditRepo
        )
    }

    @Test
    fun testRolePermissionPolicy_ApyarExecutiveHasEmptyDefaultPermissions() {
        // Critical requirement: APYAR_EXECUTIVE has NO default administrative or operational permissions
        val executivePerms = RolePermissionPolicy.getPermissionsForRole(Role.APYAR_EXECUTIVE)
        assertTrue("APYAR_EXECUTIVE must have 0 base permissions by default", executivePerms.isEmpty())
    }

    @Test
    fun testUserWithoutMembership_HasZeroPermissions() = runBlocking {
        val person = personRepo.createPerson(Person(firstName = "علی", lastName = "رضایی", mobileNumber = "09121112233"))
        val user = userAccountRepo.createUserAccount(
            UserAccount(
                id = "user_1",
                personId = person.id,
                mobileNumber = person.mobileNumber,
                accountStatus = AccountStatus.ACTIVE
            )
        )

        val building = buildingRepo.createBuilding(
            Building(name = "برج آفتاب", address = "خیابان بهار", city = "تهران", unitCount = 20)
        )

        val perms = getUserEffectivePermissionsUseCase(building.id, user.id)
        assertTrue("User with no membership in building must have empty permissions", perms.isEmpty())
        assertFalse(checkPermissionUseCase.hasPermission(building.id, user.id, Permission.VIEW_BUILDING))
    }

    @Test
    fun testMultiBuildingRoles_Independence() = runBlocking {
        // User is BUILDING_ADMIN in Building A, but RESIDENT in Building B
        val person = personRepo.createPerson(Person(firstName = "سارا", lastName = "احمدی", mobileNumber = "09124445566"))
        val user = userAccountRepo.createUserAccount(
            UserAccount(id = "user_sara", personId = person.id, mobileNumber = person.mobileNumber, accountStatus = AccountStatus.ACTIVE)
        )

        val buildingA = buildingRepo.createBuilding(Building(name = "مجتمع بهار", address = "ونک", city = "تهران", unitCount = 10))
        val buildingB = buildingRepo.createBuilding(Building(name = "برج نگین", address = "سعادت آباد", city = "تهران", unitCount = 15))

        buildingMemberRepo.addMember(
            BuildingMember(buildingId = buildingA.id, userId = user.id, role = Role.BUILDING_ADMIN)
        )
        buildingMemberRepo.addMember(
            BuildingMember(buildingId = buildingB.id, userId = user.id, role = Role.RESIDENT)
        )

        // In Building A: Can manage building and units
        assertTrue(checkPermissionUseCase.hasPermission(buildingA.id, user.id, Permission.MANAGE_BUILDING))
        assertTrue(checkPermissionUseCase.hasPermission(buildingA.id, user.id, Permission.MANAGE_UNITS))
        assertTrue(checkPermissionUseCase.hasPermission(buildingA.id, user.id, Permission.MANAGE_MEMBERS))

        // In Building B: CANNOT manage building, units, or members
        assertFalse(checkPermissionUseCase.hasPermission(buildingB.id, user.id, Permission.MANAGE_BUILDING))
        assertFalse(checkPermissionUseCase.hasPermission(buildingB.id, user.id, Permission.MANAGE_UNITS))
        assertFalse(checkPermissionUseCase.hasPermission(buildingB.id, user.id, Permission.MANAGE_MEMBERS))
        // But CAN view building & unit in Building B
        assertTrue(checkPermissionUseCase.hasPermission(buildingB.id, user.id, Permission.VIEW_BUILDING))
        assertTrue(checkPermissionUseCase.hasPermission(buildingB.id, user.id, Permission.VIEW_UNIT))
    }

    @Test
    fun testDelegatedPermission_ExecutiveGetsAccessThroughDelegation() = runBlocking {
        // Executive starts with 0 permissions
        val execPerson = personRepo.createPerson(Person(firstName = "کارشناس", lastName = "اپیار", mobileNumber = "09129998877"))
        val execUser = userAccountRepo.createUserAccount(
            UserAccount(id = "user_exec", personId = execPerson.id, mobileNumber = execPerson.mobileNumber, accountStatus = AccountStatus.ACTIVE)
        )

        val adminPerson = personRepo.createPerson(Person(firstName = "مدیر", lastName = "ساختمان", mobileNumber = "09120001122"))
        val adminUser = userAccountRepo.createUserAccount(
            UserAccount(id = "user_admin", personId = adminPerson.id, mobileNumber = adminPerson.mobileNumber, accountStatus = AccountStatus.ACTIVE)
        )

        val building = buildingRepo.createBuilding(Building(name = "برج ستاره", address = "میرداماد", city = "تهران", unitCount = 30))

        buildingMemberRepo.addMember(BuildingMember(buildingId = building.id, userId = adminUser.id, role = Role.BUILDING_ADMIN))
        buildingMemberRepo.addMember(BuildingMember(buildingId = building.id, userId = execUser.id, role = Role.APYAR_EXECUTIVE))

        // Check before delegation: Executive has NO permissions
        var execPerms = getUserEffectivePermissionsUseCase(building.id, execUser.id)
        assertTrue(execPerms.isEmpty())

        // Admin delegates VIEW_BUILDING and MANAGE_UNITS to Executive
        val now = System.currentTimeMillis()
        val oneWeekLater = now + (7 * 24 * 3600 * 1000L)

        val delResult = createDelegationUseCase(
            actorUserId = adminUser.id,
            buildingId = building.id,
            grantedToUserId = execUser.id,
            permission = Permission.MANAGE_UNITS,
            startDate = now,
            endDate = oneWeekLater
        )
        assertTrue(delResult.isSuccess)

        // Check after delegation: Executive now has MANAGE_UNITS!
        execPerms = getUserEffectivePermissionsUseCase(building.id, execUser.id)
        assertTrue(execPerms.contains(Permission.MANAGE_UNITS))
        assertFalse(execPerms.contains(Permission.MANAGE_BUILDING)) // Still doesn't have other perms

        // Revoke delegation
        val delegationId = delResult.getOrThrow().id
        val revokeResult = revokeDelegationUseCase(
            actorUserId = adminUser.id,
            buildingId = building.id,
            delegationId = delegationId
        )
        assertTrue(revokeResult.isSuccess)

        // After revocation, executive loses MANAGE_UNITS
        execPerms = getUserEffectivePermissionsUseCase(building.id, execUser.id)
        assertFalse(execPerms.contains(Permission.MANAGE_UNITS))
    }

    @Test
    fun testDelegationExpiration() = runBlocking {
        val now = System.currentTimeMillis()
        val pastStart = now - (10 * 24 * 3600 * 1000L)
        val pastEnd = now - (2 * 24 * 3600 * 1000L) // expired 2 days ago

        val expiredDelegation = DelegatedPermission(
            buildingId = "b1",
            grantedByUserId = "u_admin",
            grantedToUserId = "u_user",
            permission = Permission.VIEW_RESIDENTS,
            startDate = pastStart,
            endDate = pastEnd,
            status = DelegationStatus.ACTIVE
        )

        assertFalse("Delegation with past endDate must not be valid", expiredDelegation.isCurrentlyValid(now))
    }

    @Test
    fun testPermissionEnforcement_ThrowsAccessDeniedException() = runBlocking {
        val person = personRepo.createPerson(Person(firstName = "مستاجر", lastName = "محترم", mobileNumber = "09121234567"))
        val user = userAccountRepo.createUserAccount(
            UserAccount(id = "user_tenant", personId = person.id, mobileNumber = person.mobileNumber, accountStatus = AccountStatus.ACTIVE)
        )

        val building = buildingRepo.createBuilding(Building(name = "ساختمان رز", address = "ونک", city = "تهران", unitCount = 5))

        buildingMemberRepo.addMember(BuildingMember(buildingId = building.id, userId = user.id, role = Role.TENANT))

        // Tenant cannot MANAGE_MEMBERS
        assertThrows(AccessDeniedException::class.java) {
            runBlocking {
                checkPermissionUseCase.enforce(building.id, user.id, Permission.MANAGE_MEMBERS)
            }
        }
    }

    @Test
    fun testSuspendedAccount_HasZeroPermissions() = runBlocking {
        val person = personRepo.createPerson(Person(firstName = "کاربر", lastName = "معلق", mobileNumber = "09120000000"))
        val user = userAccountRepo.createUserAccount(
            UserAccount(id = "user_suspended", personId = person.id, mobileNumber = person.mobileNumber, accountStatus = AccountStatus.SUSPENDED)
        )

        val building = buildingRepo.createBuilding(Building(name = "ساختمان بهشت", address = "خیابان بهشت", city = "تهران", unitCount = 8))

        buildingMemberRepo.addMember(BuildingMember(buildingId = building.id, userId = user.id, role = Role.BUILDING_ADMIN))

        // Even though role is BUILDING_ADMIN, accountStatus is SUSPENDED -> 0 effective permissions
        val perms = getUserEffectivePermissionsUseCase(building.id, user.id)
        assertTrue("Suspended account must have 0 effective permissions", perms.isEmpty())
    }
}
