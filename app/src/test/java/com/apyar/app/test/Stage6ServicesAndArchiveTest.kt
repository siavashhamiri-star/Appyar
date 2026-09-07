package com.apyar.app.test

import com.apyar.app.domain.model.BillType
import com.apyar.app.domain.model.Building
import com.apyar.app.domain.model.InvoiceStatus
import com.apyar.app.domain.model.PaymentMethod
import com.apyar.app.domain.model.ProviderCategory
import com.apyar.app.domain.model.ProviderRoleInBuilding
import com.apyar.app.domain.model.ProviderStatus
import com.apyar.app.domain.model.ServicePriority
import com.apyar.app.domain.model.ServiceRecordStatus
import com.apyar.app.domain.model.ServiceType
import com.apyar.app.domain.usecase.CreateBuildingBillUseCase
import com.apyar.app.domain.usecase.CreateInvoiceUseCase
import com.apyar.app.domain.usecase.CreateMaintenanceRecordUseCase
import com.apyar.app.domain.usecase.CreateServiceProviderUseCase
import com.apyar.app.domain.usecase.CreateServiceRecordUseCase
import com.apyar.app.domain.usecase.GetBuildingArchiveUseCase
import com.apyar.app.domain.usecase.GetBuildingBillsUseCase
import com.apyar.app.domain.usecase.GetBuildingInvoicesUseCase
import com.apyar.app.domain.usecase.GetBuildingMaintenanceRecordsUseCase
import com.apyar.app.domain.usecase.GetBuildingServiceProvidersUseCase
import com.apyar.app.domain.usecase.GetBuildingServiceRecordsUseCase
import com.apyar.app.domain.usecase.GetServicesDashboardSummaryUseCase
import com.apyar.app.domain.usecase.LinkServiceProviderToBuildingUseCase
import com.apyar.app.domain.usecase.RecordPaymentUseCase
import com.apyar.app.domain.usecase.UpdateBillPaymentUseCase
import com.apyar.app.domain.usecase.UpdateBuildingServiceProviderRoleUseCase
import com.apyar.app.domain.usecase.UpdateServiceRecordStatusUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class Stage6ServicesAndArchiveTest {

    private lateinit var buildingRepo: FakeBuildingRepository
    private lateinit var providerRepo: FakeServiceProviderRepository
    private lateinit var serviceRecordRepo: FakeServiceRecordRepository
    private lateinit var maintenanceRepo: FakeMaintenanceRepository
    private lateinit var invoiceRepo: FakeServiceInvoiceRepository
    private lateinit var billRepo: FakeBuildingBillRepository

    // UseCases
    private lateinit var createServiceProviderUseCase: CreateServiceProviderUseCase
    private lateinit var linkServiceProviderToBuildingUseCase: LinkServiceProviderToBuildingUseCase
    private lateinit var getBuildingServiceProvidersUseCase: GetBuildingServiceProvidersUseCase
    private lateinit var updateBuildingServiceProviderRoleUseCase: UpdateBuildingServiceProviderRoleUseCase
    private lateinit var createServiceRecordUseCase: CreateServiceRecordUseCase
    private lateinit var getBuildingServiceRecordsUseCase: GetBuildingServiceRecordsUseCase
    private lateinit var updateServiceRecordStatusUseCase: UpdateServiceRecordStatusUseCase
    private lateinit var createMaintenanceRecordUseCase: CreateMaintenanceRecordUseCase
    private lateinit var getBuildingMaintenanceRecordsUseCase: GetBuildingMaintenanceRecordsUseCase
    private lateinit var createInvoiceUseCase: CreateInvoiceUseCase
    private lateinit var getBuildingInvoicesUseCase: GetBuildingInvoicesUseCase
    private lateinit var recordPaymentUseCase: RecordPaymentUseCase
    private lateinit var createBuildingBillUseCase: CreateBuildingBillUseCase
    private lateinit var getBuildingBillsUseCase: GetBuildingBillsUseCase
    private lateinit var updateBillPaymentUseCase: UpdateBillPaymentUseCase
    private lateinit var getServicesDashboardSummaryUseCase: GetServicesDashboardSummaryUseCase
    private lateinit var getBuildingArchiveUseCase: GetBuildingArchiveUseCase

    private val buildingId1 = "bldg-101"
    private val buildingId2 = "bldg-102"

    @Before
    fun setUp() = runBlocking {
        buildingRepo = FakeBuildingRepository()
        providerRepo = FakeServiceProviderRepository()
        serviceRecordRepo = FakeServiceRecordRepository()
        maintenanceRepo = FakeMaintenanceRepository()
        invoiceRepo = FakeServiceInvoiceRepository()
        billRepo = FakeBuildingBillRepository()

        createServiceProviderUseCase = CreateServiceProviderUseCase(providerRepo)
        linkServiceProviderToBuildingUseCase = LinkServiceProviderToBuildingUseCase(providerRepo)
        getBuildingServiceProvidersUseCase = GetBuildingServiceProvidersUseCase(providerRepo)
        updateBuildingServiceProviderRoleUseCase = UpdateBuildingServiceProviderRoleUseCase(providerRepo)
        createServiceRecordUseCase = CreateServiceRecordUseCase(serviceRecordRepo)
        getBuildingServiceRecordsUseCase = GetBuildingServiceRecordsUseCase(serviceRecordRepo)
        updateServiceRecordStatusUseCase = UpdateServiceRecordStatusUseCase(serviceRecordRepo)
        createMaintenanceRecordUseCase = CreateMaintenanceRecordUseCase(maintenanceRepo)
        getBuildingMaintenanceRecordsUseCase = GetBuildingMaintenanceRecordsUseCase(maintenanceRepo)
        createInvoiceUseCase = CreateInvoiceUseCase(invoiceRepo)
        getBuildingInvoicesUseCase = GetBuildingInvoicesUseCase(invoiceRepo)
        recordPaymentUseCase = RecordPaymentUseCase(invoiceRepo)
        createBuildingBillUseCase = CreateBuildingBillUseCase(billRepo)
        getBuildingBillsUseCase = GetBuildingBillsUseCase(billRepo)
        updateBillPaymentUseCase = UpdateBillPaymentUseCase(billRepo)
        getServicesDashboardSummaryUseCase = GetServicesDashboardSummaryUseCase(
            serviceProviderRepository = providerRepo,
            serviceRecordRepository = serviceRecordRepo,
            maintenanceRepository = maintenanceRepo,
            serviceInvoiceRepository = invoiceRepo,
            buildingBillRepository = billRepo
        )
        getBuildingArchiveUseCase = GetBuildingArchiveUseCase(
            serviceRecordRepository = serviceRecordRepo,
            maintenanceRepository = maintenanceRepo,
            serviceInvoiceRepository = invoiceRepo,
            buildingBillRepository = billRepo
        )

        // Seed sample buildings
        buildingRepo.createBuilding(Building(id = buildingId1, name = "برج آفتاب", address = "تهران", city = "تهران", unitCount = 20))
        buildingRepo.createBuilding(Building(id = buildingId2, name = "مجتمع نیلوفر", address = "مشهد", city = "مشهد", unitCount = 12))
    }

    @Test
    fun `test creating service providers and linking primary and backup roles`() = runBlocking {
        // Create 2 providers
        val p1Result = createServiceProviderUseCase(
            name = "مهندس اکبری",
            phoneNumber = "09121112233",
            companyName = "شرکت اوج گستران",
            category = ProviderCategory.ELEVATOR,
            notes = "سرویسکار مجرب آسانسور با گواهی استاندارد"
        )
        assertTrue(p1Result.isSuccess)
        val p1 = p1Result.getOrThrow()

        val p2Result = createServiceProviderUseCase(
            name = "استاد رضایی",
            phoneNumber = "09129998877",
            companyName = "تأسیسات رازی",
            category = ProviderCategory.ELEVATOR,
            notes = "تکنسین پشتیبان آسانسور"
        )
        assertTrue(p2Result.isSuccess)
        val p2 = p2Result.getOrThrow()

        // Link p1 as PRIMARY in building 1
        val link1 = linkServiceProviderToBuildingUseCase(
            buildingId = buildingId1,
            providerId = p1.id,
            roleInBuilding = ProviderRoleInBuilding.PRIMARY,
            category = ProviderCategory.ELEVATOR,
            status = ProviderStatus.ACTIVE
        )
        assertTrue(link1.isSuccess)

        // Link p2 as BACKUP in building 1
        val link2 = linkServiceProviderToBuildingUseCase(
            buildingId = buildingId1,
            providerId = p2.id,
            roleInBuilding = ProviderRoleInBuilding.BACKUP,
            category = ProviderCategory.ELEVATOR,
            status = ProviderStatus.ACTIVE
        )
        assertTrue(link2.isSuccess)

        val building1Providers = getBuildingServiceProvidersUseCase(buildingId1).first()
        assertEquals(2, building1Providers.size)

        val primary = building1Providers.find { it.roleInBuilding == ProviderRoleInBuilding.PRIMARY }
        assertNotNull(primary)
        assertEquals("مهندس اکبری", primary?.provider?.name)

        val backup = building1Providers.find { it.roleInBuilding == ProviderRoleInBuilding.BACKUP }
        assertNotNull(backup)
        assertEquals("استاد رضایی", backup?.provider?.name)

        // Switch role: update p2 to PRIMARY
        val updateRoleResult = updateBuildingServiceProviderRoleUseCase(
            buildingId = buildingId1,
            providerId = p2.id,
            roleInBuilding = ProviderRoleInBuilding.PRIMARY,
            status = ProviderStatus.ACTIVE,
            category = ProviderCategory.ELEVATOR
        )
        assertTrue(updateRoleResult.isSuccess)

        val updatedProviders = getBuildingServiceProvidersUseCase(buildingId1).first()
        val p2Updated = updatedProviders.find { it.provider.id == p2.id }
        assertEquals(ProviderRoleInBuilding.PRIMARY, p2Updated?.roleInBuilding)
    }

    @Test
    fun `test service record lifecycle from request to completion`() = runBlocking {
        val createResult = createServiceRecordUseCase(
            buildingId = buildingId1,
            serviceType = ServiceType.REPAIR,
            category = ProviderCategory.WATER_PUMP,
            providerId = null,
            title = "رفع افت فشار آب طبقات بالا",
            description = "پمپ تحت فشار طبقه ۴ و ۵ کار نمی‌کند",
            priority = ServicePriority.HIGH,
            scheduledDate = System.currentTimeMillis()
        )
        assertTrue(createResult.isSuccess)
        val record = createResult.getOrThrow()
        assertEquals(ServiceRecordStatus.REQUESTED, record.status)
        assertEquals(ServicePriority.HIGH, record.priority)

        // Update status to IN_PROGRESS with estimate
        val progressResult = updateServiceRecordStatusUseCase(
            recordId = record.id,
            status = ServiceRecordStatus.IN_PROGRESS,
            costEstimate = 15_000_000L,
            actualCost = null,
            resolutionNotes = "قطعه مکانیکال سیل تعویض شد"
        )
        assertTrue(progressResult.isSuccess)

        // Complete the service
        val completeResult = updateServiceRecordStatusUseCase(
            recordId = record.id,
            status = ServiceRecordStatus.COMPLETED,
            costEstimate = 15_000_000L,
            actualCost = 14_500_000L,
            resolutionNotes = "پمپ با موفقیت تست و تحویل مدیر ساختمان شد"
        )
        assertTrue(completeResult.isSuccess)

        val records = getBuildingServiceRecordsUseCase(buildingId1).first()
        assertEquals(1, records.size)
        val completed = records.first()
        assertEquals(ServiceRecordStatus.COMPLETED, completed.status)
        assertEquals(14_500_000L, completed.actualCost)
        assertNotNull(completed.completedDate)
    }

    @Test
    fun `test periodic maintenance tracking and upcoming calculation`() = runBlocking {
        val now = System.currentTimeMillis()
        val nextMonth = now + 30L * 24 * 60 * 60 * 1000

        val m1 = createMaintenanceRecordUseCase(
            buildingId = buildingId1,
            equipmentName = "آسانسور مسافربری ۱",
            serviceType = "سرویس و روغن‌کاری ماهانه",
            maintenanceDate = now,
            nextScheduledDate = nextMonth,
            performedBy = "تکنسین ایمن آسانسور",
            cost = 4_000_000L,
            partsReplaced = "روغن گیربکس و لنت ترمز",
            checklistNotes = "تمام کلیدهای طبقات و ترمز ایمنی بازرسی شد"
        )
        assertTrue(m1.isSuccess)

        val list = getBuildingMaintenanceRecordsUseCase(buildingId1).first()
        assertEquals(1, list.size)
        assertEquals("آسانسور مسافربری ۱", list.first().equipmentName)

        val upcoming = maintenanceRepo.getUpcomingMaintenances(buildingId1, now)
        assertEquals(1, upcoming.size)
        assertEquals(nextMonth, upcoming.first().nextScheduledDate)
    }

    @Test
    fun `test invoice creation, partial payment, and full settlement`() = runBlocking {
        val now = System.currentTimeMillis()
        val invoiceResult = createInvoiceUseCase(
            buildingId = buildingId1,
            invoiceNumber = "INV-2026-001",
            providerId = "p-1",
            serviceRecordId = null,
            issueDate = now,
            dueDate = now + 7L * 24 * 60 * 60 * 1000,
            totalAmount = 20_000_000L,
            description = "فاکتور تعمیرات موتورخانه مرکزی"
        )
        assertTrue(invoiceResult.isSuccess)
        val invoice = invoiceResult.getOrThrow()
        assertEquals(InvoiceStatus.UNPAID, invoice.status)
        assertEquals(0L, invoice.paidAmount)

        // 1. First partial payment: 8,000,000 Rials
        val p1 = recordPaymentUseCase(
            invoiceId = invoice.id,
            amount = 8_000_000L,
            paymentDate = now + 1000,
            paymentMethod = PaymentMethod.BANK_TRANSFER,
            referenceNumber = "TRX-998811",
            paidBy = "صندوق مرکزی ساختمان",
            notes = "پرداخت قسط اول فاکتور"
        )
        assertTrue(p1.isSuccess)

        val invAfterP1 = invoiceRepo.getInvoiceById(invoice.id)
        assertNotNull(invAfterP1)
        assertEquals(InvoiceStatus.PARTIALLY_PAID, invAfterP1?.status)
        assertEquals(8_000_000L, invAfterP1?.paidAmount)

        // 2. Final payment: 12,000,000 Rials (Full settlement)
        val p2 = recordPaymentUseCase(
            invoiceId = invoice.id,
            amount = 12_000_000L,
            paymentDate = now + 5000,
            paymentMethod = PaymentMethod.CHEQUE,
            referenceNumber = "CHQ-5544",
            paidBy = "مدیر ساختمان",
            notes = "تسویه نهایی فاکتور"
        )
        assertTrue(p2.isSuccess)

        val invSettled = invoiceRepo.getInvoiceById(invoice.id)
        assertNotNull(invSettled)
        assertEquals(InvoiceStatus.PAID, invSettled?.status)
        assertEquals(20_000_000L, invSettled?.paidAmount)
    }

    @Test
    fun `test public building utility bills and payment toggle`() = runBlocking {
        val now = System.currentTimeMillis()
        val billResult = createBuildingBillUseCase(
            buildingId = buildingId1,
            billType = BillType.ELECTRICITY,
            billIdentifier = "BILL-ELEC-987654",
            periodStartDate = now - 30L * 24 * 60 * 60 * 1000,
            periodEndDate = now,
            dueDate = now + 10L * 24 * 60 * 60 * 1000,
            amount = 6_500_000L,
            description = "قبض برق مشاعات و آسانسور"
        )
        assertTrue(billResult.isSuccess)
        val bill = billResult.getOrThrow()
        assertFalse(bill.isPaid)

        val unpaidBills = billRepo.getUnpaidBills(buildingId1).first()
        assertEquals(1, unpaidBills.size)

        // Pay the bill
        val payResult = updateBillPaymentUseCase(
            billId = bill.id,
            isPaid = true,
            paymentDate = now + 1000,
            paymentReference = "APP-PAY-2026"
        )
        assertTrue(payResult.isSuccess)

        val paidBill = billRepo.getBuildingBillById(bill.id)
        assertNotNull(paidBill)
        assertTrue(paidBill!!.isPaid)
        assertEquals("APP-PAY-2026", paidBill.paymentReference)

        val remainingUnpaid = billRepo.getUnpaidBills(buildingId1).first()
        assertTrue(remainingUnpaid.isEmpty())
    }

    @Test
    fun `test dashboard summary and archive aggregation isolation`() = runBlocking {
        val now = System.currentTimeMillis()

        // Setup Building 1 items
        val p1 = createServiceProviderUseCase("سرویسکار ۱", "0912111", null, ProviderCategory.CLEANING, null).getOrThrow()
        linkServiceProviderToBuildingUseCase(buildingId1, p1.id, ProviderRoleInBuilding.PRIMARY, ProviderCategory.CLEANING, ProviderStatus.ACTIVE)

        createServiceRecordUseCase(buildingId1, ServiceType.CLEANING, ProviderCategory.CLEANING, p1.id, "نظافت راه‌پله", "هفتگی", ServicePriority.NORMAL, now)
        createInvoiceUseCase(buildingId1, "INV-100", p1.id, null, now, now + 5000, 3_000_000L, "نظافت")
        createBuildingBillUseCase(buildingId1, BillType.WATER, "W-1", now - 1000, now, now + 10000, 4_000_000L, "آب")

        // Setup Building 2 items (Must remain completely separate)
        val p2 = createServiceProviderUseCase("سرویسکار ۲", "0912222", null, ProviderCategory.HVAC, null).getOrThrow()
        linkServiceProviderToBuildingUseCase(buildingId2, p2.id, ProviderRoleInBuilding.PRIMARY, ProviderCategory.HVAC, ProviderStatus.ACTIVE)
        createInvoiceUseCase(buildingId2, "INV-200", p2.id, null, now, now + 5000, 10_000_000L, "چیلر")

        // 1. Verify Building 1 Summary
        val summary1 = getServicesDashboardSummaryUseCase(buildingId1)
        assertEquals(1, summary1.activeProvidersCount)
        assertEquals(1, summary1.activeServiceRecordsCount)
        assertEquals(1, summary1.unpaidInvoicesCount)
        assertEquals(3_000_000L, summary1.unpaidInvoicesTotalAmount)
        assertEquals(1, summary1.unpaidBillsCount)
        assertEquals(4_000_000L, summary1.unpaidBillsTotalAmount)

        // 2. Verify Building 2 Summary
        val summary2 = getServicesDashboardSummaryUseCase(buildingId2)
        assertEquals(1, summary2.activeProvidersCount)
        assertEquals(0, summary2.activeServiceRecordsCount)
        assertEquals(1, summary2.unpaidInvoicesCount)
        assertEquals(10_000_000L, summary2.unpaidInvoicesTotalAmount)
        assertEquals(0, summary2.unpaidBillsCount)

        // 3. Verify Archive Aggregator for Building 1
        val archive1 = getBuildingArchiveUseCase(buildingId1)
        assertEquals(1, archive1.serviceRecords.size)
        assertEquals(1, archive1.invoices.size)
        assertEquals(1, archive1.bills.size)
        assertEquals("INV-100", archive1.invoices.first().invoiceNumber)
    }
}
