package com.apyar.app.presentation.services

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apyar.app.domain.model.BillType
import com.apyar.app.domain.model.BuildingBill
import com.apyar.app.domain.model.BuildingServiceProvider
import com.apyar.app.domain.model.Invoice
import com.apyar.app.domain.model.InvoiceStatus
import com.apyar.app.domain.model.MaintenanceRecord
import com.apyar.app.domain.model.PaymentMethod
import com.apyar.app.domain.model.PaymentRecord
import com.apyar.app.domain.model.ProviderCategory
import com.apyar.app.domain.model.ProviderRoleInBuilding
import com.apyar.app.domain.model.ProviderStatus
import com.apyar.app.domain.model.ServicePriority
import com.apyar.app.domain.model.ServiceRecord
import com.apyar.app.domain.model.ServiceRecordStatus
import com.apyar.app.domain.model.ServiceType
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicesScreen(
    viewModel: ServicesViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "مدیریت خدمات، تأمین‌کنندگان و آرشیو",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Text(
                                text = "→",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            floatingActionButton = {
                when (uiState.selectedTab) {
                    ServiceTab.PROVIDERS -> {
                        FloatingActionButton(
                            onClick = { viewModel.openAddProviderDialog() },
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("+ افزودن سرویسکار", modifier = Modifier.padding(horizontal = 16.dp), fontWeight = FontWeight.Bold)
                        }
                    }
                    ServiceTab.SERVICES -> {
                        FloatingActionButton(
                            onClick = { viewModel.openAddServiceRecordDialog() },
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("+ ثبت درخواست خدمت", modifier = Modifier.padding(horizontal = 16.dp), fontWeight = FontWeight.Bold)
                        }
                    }
                    ServiceTab.MAINTENANCE -> {
                        FloatingActionButton(
                            onClick = { viewModel.openAddMaintenanceDialog() },
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("+ ثبت سرویس دوره‌ای", modifier = Modifier.padding(horizontal = 16.dp), fontWeight = FontWeight.Bold)
                        }
                    }
                    ServiceTab.INVOICES -> {
                        FloatingActionButton(
                            onClick = { viewModel.openAddInvoiceDialog() },
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("+ صدور فاکتور خدمت", modifier = Modifier.padding(horizontal = 16.dp), fontWeight = FontWeight.Bold)
                        }
                    }
                    ServiceTab.BILLS -> {
                        FloatingActionButton(
                            onClick = { viewModel.openAddBillDialog() },
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("+ ثبت قبض عمومی", modifier = Modifier.padding(horizontal = 16.dp), fontWeight = FontWeight.Bold)
                        }
                    }
                    else -> {}
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Messages Banner
                uiState.errorMessage?.let { error ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = error, color = MaterialTheme.colorScheme.onErrorContainer, style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = "✕",
                                modifier = Modifier.clickable { viewModel.clearMessages() },
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                uiState.successMessage?.let { success ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = success, color = Color(0xFF2E7D32), style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = "✕",
                                modifier = Modifier.clickable { viewModel.clearMessages() },
                                color = Color(0xFF2E7D32),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Scrollable Tabs
                ScrollableTabRow(
                    selectedTabIndex = uiState.selectedTab.ordinal,
                    edgePadding = 16.dp,
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    ServiceTab.entries.forEach { tab ->
                        Tab(
                            selected = uiState.selectedTab == tab,
                            onClick = { viewModel.selectTab(tab) },
                            text = {
                                Text(
                                    text = tab.title,
                                    fontWeight = if (uiState.selectedTab == tab) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 14.sp
                                )
                            }
                        )
                    }
                }

                // Content View according to selected tab
                when (uiState.selectedTab) {
                    ServiceTab.DASHBOARD -> DashboardSection(uiState, onTabSelect = { viewModel.selectTab(it) })
                    ServiceTab.PROVIDERS -> ProvidersSection(uiState, viewModel)
                    ServiceTab.SERVICES -> ServiceRecordsSection(uiState, viewModel)
                    ServiceTab.MAINTENANCE -> MaintenanceSection(uiState, viewModel)
                    ServiceTab.INVOICES -> InvoicesSection(uiState, viewModel)
                    ServiceTab.BILLS -> BillsSection(uiState, viewModel)
                    ServiceTab.ARCHIVE -> ArchiveSection(uiState, viewModel)
                }
            }
        }

        // Dialogs
        if (uiState.isAddProviderDialogOpen) {
            AddProviderDialog(
                onDismiss = { viewModel.closeAddProviderDialog() },
                onConfirm = { name, phone, company, cat, notes, role ->
                    viewModel.createProviderAndLink(name, phone, company, cat, notes, role)
                }
            )
        }

        if (uiState.isAddServiceRecordDialogOpen) {
            AddServiceRecordDialog(
                providers = uiState.providers,
                onDismiss = { viewModel.closeAddServiceRecordDialog() },
                onConfirm = { type, cat, provId, title, desc, prio, schedDate ->
                    viewModel.createServiceRecord(type, cat, provId, title, desc, prio, schedDate)
                }
            )
        }

        if (uiState.isAddMaintenanceDialogOpen) {
            AddMaintenanceDialog(
                onDismiss = { viewModel.closeAddMaintenanceDialog() },
                onConfirm = { equip, sType, mDate, nDate, perfBy, cost, parts, notes ->
                    viewModel.createMaintenanceRecord(equip, sType, mDate, nDate, perfBy, cost, parts, notes)
                }
            )
        }

        if (uiState.isAddInvoiceDialogOpen) {
            AddInvoiceDialog(
                providers = uiState.providers,
                serviceRecords = uiState.serviceRecords,
                onDismiss = { viewModel.closeAddInvoiceDialog() },
                onConfirm = { num, provId, recId, issueDate, dueDate, total, desc ->
                    viewModel.createInvoice(num, provId, recId, issueDate, dueDate, total, desc)
                }
            )
        }

        if (uiState.isAddPaymentDialogOpen) {
            AddPaymentDialog(
                invoice = uiState.selectedInvoiceForPayment,
                onDismiss = { viewModel.closeAddPaymentDialog() },
                onConfirm = { invId, amount, pDate, method, ref, paidBy, notes ->
                    viewModel.recordPayment(invId, amount, pDate, method, ref, paidBy, notes)
                }
            )
        }

        if (uiState.isAddBillDialogOpen) {
            AddBillDialog(
                onDismiss = { viewModel.closeAddBillDialog() },
                onConfirm = { bType, ident, sDate, eDate, dDate, amt, desc ->
                    viewModel.createBuildingBill(bType, ident, sDate, eDate, dDate, amt, desc)
                }
            )
        }

        uiState.selectedServiceRecordForStatusUpdate?.let { record ->
            UpdateServiceStatusDialog(
                record = record,
                onDismiss = { viewModel.closeStatusUpdateDialog() },
                onConfirm = { status, est, act, notes ->
                    viewModel.updateServiceRecordStatus(record.id, status, est, act, notes)
                }
            )
        }
    }
}

// ----------------------------------------------------
// 1. DASHBOARD SECTION
// ----------------------------------------------------
@Composable
fun DashboardSection(
    uiState: ServicesUiState,
    onTabSelect: (ServiceTab) -> Unit
) {
    val summary = uiState.summary

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "خلاصه وضعیت خدمات و تعهدات ساختمان",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        // Summary Metric Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "سرویسکاران فعال",
                    value = "${summary?.activeProvidersCount ?: uiState.providers.size} نفر/شرکت",
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.weight(1f),
                    onClick = { onTabSelect(ServiceTab.PROVIDERS) }
                )
                MetricCard(
                    title = "خدمات جاری",
                    value = "${summary?.activeServiceRecordsCount ?: 0} مورد",
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.weight(1f),
                    onClick = { onTabSelect(ServiceTab.SERVICES) }
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "فاکتورهای پرداخت‌نشده",
                    value = "${summary?.unpaidInvoicesCount ?: 0} فاکتور",
                    subValue = "مبلغ: ${formatCurrency(summary?.unpaidInvoicesTotalAmount ?: 0)} ریال",
                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f),
                    modifier = Modifier.weight(1f),
                    onClick = { onTabSelect(ServiceTab.INVOICES) }
                )
                MetricCard(
                    title = "قبوض منتظر پرداخت",
                    value = "${summary?.unpaidBillsCount ?: 0} قبض",
                    subValue = "مبلغ: ${formatCurrency(summary?.unpaidBillsTotalAmount ?: 0)} ریال",
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    modifier = Modifier.weight(1f),
                    onClick = { onTabSelect(ServiceTab.BILLS) }
                )
            }
        }

        // Upcoming Maintenance Notice
        if (!summary?.upcomingMaintenances.isNullOrEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "🔔 سرویس‌های دوره‌ای آینده",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall
                        )
                        summary!!.upcomingMaintenances.forEach { m ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "• ${m.equipmentName} (${m.serviceType})", style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    text = formatDate(m.nextScheduledDate ?: m.maintenanceDate),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Recent Invoices Quick List
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "فاکتورهای اخیر ساختمان", fontWeight = FontWeight.Bold)
                        Text(
                            text = "مشاهده همه →",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable { onTabSelect(ServiceTab.INVOICES) }
                        )
                    }

                    if (uiState.invoices.isEmpty()) {
                        Text(text = "هیچ فاکتوری ثبت نشده است.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    } else {
                        uiState.invoices.take(3).forEach { inv ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(text = "فاکتور #${inv.invoiceNumber}", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                                    Text(text = "سررسید: ${formatDate(inv.dueDate)}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(text = "${formatCurrency(inv.totalAmount)} ریال", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                    InvoiceStatusBadge(status = inv.status)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// 2. PROVIDERS SECTION
// ----------------------------------------------------
@Composable
fun ProvidersSection(
    uiState: ServicesUiState,
    viewModel: ServicesViewModel
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "سرویسکاران، تعمیرکاران و تأمین‌کنندگان طرف قرارداد ساختمان",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        if (uiState.providers.isEmpty()) {
            item {
                EmptyStateCard(message = "هنوز هیچ سرویسکار یا پیمانکاری برای این ساختمان ثبت نشده است.")
            }
        } else {
            items(uiState.providers, key = { it.provider.id }) { item ->
                ProviderCard(
                    provider = item,
                    onRoleChange = { role, status, cat ->
                        viewModel.updateProviderBuildingRole(item.provider.id, role, status, cat)
                    }
                )
            }
        }
        item { Spacer(modifier = Modifier.height(64.dp)) }
    }
}

@Composable
fun ProviderCard(
    provider: BuildingServiceProvider,
    onRoleChange: (ProviderRoleInBuilding, ProviderStatus, ProviderCategory) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = provider.provider.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (!provider.provider.companyName.isNullOrBlank()) {
                        Text(
                            text = provider.provider.companyName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    RoleBadge(role = provider.roleInBuilding)
                    CategoryBadge(category = provider.category)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "📞 ${provider.provider.phoneNumber}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = if (provider.status == ProviderStatus.ACTIVE) "🟢 فعال" else "⚪ غیرفعال",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (!provider.provider.notes.isNullOrBlank()) {
                Text(
                    text = "یادداشت: ${provider.provider.notes}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = {
                        val newRole = if (provider.roleInBuilding == ProviderRoleInBuilding.PRIMARY) {
                            ProviderRoleInBuilding.BACKUP
                        } else {
                            ProviderRoleInBuilding.PRIMARY
                        }
                        onRoleChange(newRole, provider.status, provider.category)
                    }
                ) {
                    Text(
                        text = if (provider.roleInBuilding == ProviderRoleInBuilding.PRIMARY) "تغییر به جایگزین" else "تغییر به سرویسکار اصلی",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

// ----------------------------------------------------
// 3. SERVICE RECORDS SECTION
// ----------------------------------------------------
@Composable
fun ServiceRecordsSection(
    uiState: ServicesUiState,
    viewModel: ServicesViewModel
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "سوابق درخواست‌ها، تعمیرات و خدمات انجام‌شده",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        // Status filter chips
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChipButton(
                        text = "همه",
                        isSelected = uiState.serviceStatusFilter == null,
                        onClick = { viewModel.setServiceStatusFilter(null) }
                    )
                }
                ServiceRecordStatus.entries.forEach { st ->
                    item {
                        FilterChipButton(
                            text = getStatusPersianTitle(st),
                            isSelected = uiState.serviceStatusFilter == st,
                            onClick = { viewModel.setServiceStatusFilter(st) }
                        )
                    }
                }
            }
        }

        val filteredRecords = uiState.serviceRecords.filter {
            uiState.serviceStatusFilter == null || it.status == uiState.serviceStatusFilter
        }

        if (filteredRecords.isEmpty()) {
            item {
                EmptyStateCard(message = "موردی در این دسته از خدمات یافت نشد.")
            }
        } else {
            items(filteredRecords, key = { it.id }) { record ->
                ServiceRecordCard(
                    record = record,
                    onUpdateStatusClick = { viewModel.openStatusUpdateDialog(record) }
                )
            }
        }
        item { Spacer(modifier = Modifier.height(64.dp)) }
    }
}

@Composable
fun ServiceRecordCard(
    record: ServiceRecord,
    onUpdateStatusClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = record.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                ServiceStatusBadge(status = record.status)
            }

            Text(
                text = record.description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "دسته: ${getCategoryPersianTitle(record.category)} • اولویت: ${getPriorityPersianTitle(record.priority)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                if (record.scheduledDate != null) {
                    Text(
                        text = "تاریخ: ${formatDate(record.scheduledDate)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (record.actualCost != null || record.costEstimate != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (record.costEstimate != null) {
                        Text(text = "برآورد هزینه: ${formatCurrency(record.costEstimate)} ریال", style = MaterialTheme.typography.bodySmall)
                    }
                    if (record.actualCost != null) {
                        Text(
                            text = "هزینه نهایی: ${formatCurrency(record.actualCost)} ریال",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            if (!record.resolutionNotes.isNullOrBlank()) {
                Text(
                    text = "گزارش انجام: ${record.resolutionNotes}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF2E7D32)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(onClick = onUpdateStatusClick) {
                    Text("تغییر وضعیت و ثبت هزینه", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

// ----------------------------------------------------
// 4. MAINTENANCE SECTION
// ----------------------------------------------------
@Composable
fun MaintenanceSection(
    uiState: ServicesUiState,
    viewModel: ServicesViewModel
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "دفترچه سرویس و نگهداری ادواری تأسیسات ساختمان",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        if (uiState.maintenanceRecords.isEmpty()) {
            item {
                EmptyStateCard(message = "هیچ سابقه سرویس دوره‌ای ثبت نشده است.")
            }
        } else {
            items(uiState.maintenanceRecords, key = { it.id }) { m ->
                MaintenanceCard(m)
            }
        }
        item { Spacer(modifier = Modifier.height(64.dp)) }
    }
}

@Composable
fun MaintenanceCard(m: MaintenanceRecord) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "⚙️ ${m.equipmentName}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = m.serviceType,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "سرویسکار: ${m.performedBy}", style = MaterialTheme.typography.bodySmall)
                Text(text = "تاریخ سرویس: ${formatDate(m.maintenanceDate)}", style = MaterialTheme.typography.bodySmall)
            }

            if (m.nextScheduledDate != null) {
                Text(
                    text = "موعد سرویس بعدی: ${formatDate(m.nextScheduledDate)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.Bold
                )
            }

            if (!m.partsReplaced.isNullOrBlank()) {
                Text(text = "قطعات تعویض شده: ${m.partsReplaced}", style = MaterialTheme.typography.bodySmall)
            }

            if (!m.checklistNotes.isNullOrBlank()) {
                Text(text = "چک‌لیست / توضیحات: ${m.checklistNotes}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }

            Text(
                text = "هزینه سرویس: ${formatCurrency(m.cost)} ریال",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ----------------------------------------------------
// 5. INVOICES & PAYMENTS SECTION
// ----------------------------------------------------
@Composable
fun InvoicesSection(
    uiState: ServicesUiState,
    viewModel: ServicesViewModel
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "فاکتورها و پرداخت‌های خدمات و تعمیرات",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        // Status Filters
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChipButton(
                        text = "همه",
                        isSelected = uiState.invoiceStatusFilter == null,
                        onClick = { viewModel.setInvoiceStatusFilter(null) }
                    )
                }
                InvoiceStatus.entries.forEach { st ->
                    item {
                        FilterChipButton(
                            text = getInvoiceStatusPersianTitle(st),
                            isSelected = uiState.invoiceStatusFilter == st,
                            onClick = { viewModel.setInvoiceStatusFilter(st) }
                        )
                    }
                }
            }
        }

        val filteredInvoices = uiState.invoices.filter {
            uiState.invoiceStatusFilter == null || it.status == uiState.invoiceStatusFilter
        }

        if (filteredInvoices.isEmpty()) {
            item {
                EmptyStateCard(message = "فاکتوری در این وضعیت یافت نشد.")
            }
        } else {
            items(filteredInvoices, key = { it.id }) { inv ->
                InvoiceCard(
                    invoice = inv,
                    payments = uiState.payments.filter { it.invoiceId == inv.id },
                    onRecordPaymentClick = { viewModel.openAddPaymentDialog(inv) }
                )
            }
        }
        item { Spacer(modifier = Modifier.height(64.dp)) }
    }
}

@Composable
fun InvoiceCard(
    invoice: Invoice,
    payments: List<PaymentRecord>,
    onRecordPaymentClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "فاکتور #${invoice.invoiceNumber}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                InvoiceStatusBadge(status = invoice.status)
            }

            if (!invoice.description.isNullOrBlank()) {
                Text(text = invoice.description, style = MaterialTheme.typography.bodyMedium)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "صدور: ${formatDate(invoice.issueDate)}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Text(text = "سررسید: ${formatDate(invoice.dueDate)}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "کل: ${formatCurrency(invoice.totalAmount)} ریال", fontWeight = FontWeight.Bold)
                Text(text = "پرداخت شده: ${formatCurrency(invoice.paidAmount)} ریال", color = Color(0xFF2E7D32))
            }

            val remaining = invoice.totalAmount - invoice.paidAmount
            if (remaining > 0) {
                Text(
                    text = "مانده قابل پرداخت: ${formatCurrency(remaining)} ریال",
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (payments.isNotEmpty()) {
                Text(text = "سوابق پرداخت (${payments.size} فقره):", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall)
                payments.forEach { p ->
                    Text(
                        text = "• مبلغ ${formatCurrency(p.amount)} ریال توسط ${p.paidBy} در ${formatDate(p.paymentDate)} (${p.paymentMethod.name})",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.DarkGray
                    )
                }
            }

            if (invoice.status != InvoiceStatus.PAID) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(onClick = onRecordPaymentClick) {
                        Text("ثبت پرداخت برای این فاکتور", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// 6. PUBLIC BILLS SECTION
// ----------------------------------------------------
@Composable
fun BillsSection(
    uiState: ServicesUiState,
    viewModel: ServicesViewModel
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "قبوض عمومی و مصارف مشترک ساختمان (آب، برق، گاز، نظافت و پسماند)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        // Bill type filters
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChipButton(
                        text = "همه قبوض",
                        isSelected = uiState.billTypeFilter == null,
                        onClick = { viewModel.setBillTypeFilter(null) }
                    )
                }
                BillType.entries.forEach { bt ->
                    item {
                        FilterChipButton(
                            text = getBillTypePersianTitle(bt),
                            isSelected = uiState.billTypeFilter == bt,
                            onClick = { viewModel.setBillTypeFilter(bt) }
                        )
                    }
                }
            }
        }

        val filteredBills = uiState.bills.filter {
            uiState.billTypeFilter == null || it.billType == uiState.billTypeFilter
        }

        if (filteredBills.isEmpty()) {
            item {
                EmptyStateCard(message = "قبضی در این دسته بندی ثبت نشده است.")
            }
        } else {
            items(filteredBills, key = { it.id }) { bill ->
                BillCard(
                    bill = bill,
                    onTogglePaid = { isPaid ->
                        viewModel.updateBillPayment(
                            billId = bill.id,
                            isPaid = isPaid,
                            paymentDate = if (isPaid) System.currentTimeMillis() else null,
                            paymentReference = if (isPaid) "پرداخت شده توسط مدیر/صندوق" else null
                        )
                    }
                )
            }
        }
        item { Spacer(modifier = Modifier.height(64.dp)) }
    }
}

@Composable
fun BillCard(
    bill: BuildingBill,
    onTogglePaid: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${getBillTypeIcon(bill.billType)} قبض ${getBillTypePersianTitle(bill.billType)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                BillStatusBadge(isPaid = bill.isPaid)
            }

            if (!bill.billIdentifier.isNullOrBlank()) {
                Text(text = "شناسه / شماره قبض: ${bill.billIdentifier}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "دوره: ${formatDate(bill.periodStartDate)} تا ${formatDate(bill.periodEndDate)}", style = MaterialTheme.typography.bodySmall)
                Text(text = "سررسید: ${formatDate(bill.dueDate)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
            }

            Text(
                text = "مبلغ قبض: ${formatCurrency(bill.amount)} ریال",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            if (bill.isPaid && bill.paymentDate != null) {
                Text(
                    text = "تاریخ پرداخت: ${formatDate(bill.paymentDate)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF2E7D32)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (!bill.isPaid) {
                    Button(onClick = { onTogglePaid(true) }) {
                        Text("علامت‌گذاری به عنوان پرداخت شده", style = MaterialTheme.typography.labelSmall)
                    }
                } else {
                    OutlinedButton(onClick = { onTogglePaid(false) }) {
                        Text("بازنشانی به پرداخت نشده", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// 7. ARCHIVE SECTION
// ----------------------------------------------------
@Composable
fun ArchiveSection(
    uiState: ServicesUiState,
    viewModel: ServicesViewModel
) {
    val archive = uiState.archive

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "آرشیو جامع خدمات، تعمیرات، فاکتورها و قبوض ساختمان",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            OutlinedTextField(
                value = uiState.archiveQuery,
                onValueChange = { viewModel.setArchiveQuery(it) },
                label = { Text("جستجو در آرشیو سوابق...") },
                placeholder = { Text("عنوان خدمت، سرویسکار، شماره فاکتور یا تجهیز...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        if (archive == null) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        } else {
            // Archive Overview
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = "📊 آمار بایگانی اسناد ساختمان", fontWeight = FontWeight.Bold)
                        Text(text = "• ${archive.serviceRecords.size} سابقه درخواست و تعمیرات")
                        Text(text = "• ${archive.maintenanceRecords.size} سابقه سرویس دوره‌ای")
                        Text(text = "• ${archive.invoices.size} فقره فاکتور ثبت شده")
                        Text(text = "• ${archive.payments.size} فقره پرداخت و تسویه")
                        Text(text = "• ${archive.bills.size} قبض عمومی بایگانی شده")
                    }
                }
            }

            val query = uiState.archiveQuery.trim()

            // Filtered Service Records
            val filteredRecords = archive.serviceRecords.filter {
                query.isEmpty() || it.title.contains(query, ignoreCase = true) || it.description.contains(query, ignoreCase = true)
            }
            if (filteredRecords.isNotEmpty()) {
                item { Text(text = "📑 سوابق خدمات منطبق (${filteredRecords.size}):", fontWeight = FontWeight.Bold) }
                items(filteredRecords) { r ->
                    ServiceRecordCard(record = r, onUpdateStatusClick = { viewModel.openStatusUpdateDialog(r) })
                }
            }

            // Filtered Maintenance
            val filteredMaint = archive.maintenanceRecords.filter {
                query.isEmpty() || it.equipmentName.contains(query, ignoreCase = true) || it.serviceType.contains(query, ignoreCase = true)
            }
            if (filteredMaint.isNotEmpty()) {
                item { Text(text = "⚙️ سوابق سرویس دوره‌ای منطبق (${filteredMaint.size}):", fontWeight = FontWeight.Bold) }
                items(filteredMaint) { m -> MaintenanceCard(m = m) }
            }

            // Filtered Invoices
            val filteredInvs = archive.invoices.filter {
                query.isEmpty() || it.invoiceNumber.contains(query, ignoreCase = true) || (it.description?.contains(query, ignoreCase = true) == true)
            }
            if (filteredInvs.isNotEmpty()) {
                item { Text(text = "🧾 فاکتورهای منطبق (${filteredInvs.size}):", fontWeight = FontWeight.Bold) }
                items(filteredInvs) { inv ->
                    InvoiceCard(
                        invoice = inv,
                        payments = archive.payments.filter { it.invoiceId == inv.id },
                        onRecordPaymentClick = { viewModel.openAddPaymentDialog(inv) }
                    )
                }
            }

            // Filtered Bills
            val filteredBills = archive.bills.filter {
                query.isEmpty() || it.billIdentifier?.contains(query, ignoreCase = true) == true || it.billType.name.contains(query, ignoreCase = true)
            }
            if (filteredBills.isNotEmpty()) {
                item { Text(text = "💡 قبوض عمومی منطبق (${filteredBills.size}):", fontWeight = FontWeight.Bold) }
                items(filteredBills) { b ->
                    BillCard(bill = b, onTogglePaid = { isPaid -> viewModel.updateBillPayment(b.id, isPaid, System.currentTimeMillis(), null) })
                }
            }
        }
        item { Spacer(modifier = Modifier.height(64.dp)) }
    }
}

// ----------------------------------------------------
// DIALOGS
// ----------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProviderDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String?, ProviderCategory, String?, ProviderRoleInBuilding) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var company by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(ProviderCategory.ELEVATOR) }
    var role by remember { mutableStateOf(ProviderRoleInBuilding.PRIMARY) }
    var notes by remember { mutableStateOf("") }
    var categoryExpanded by remember { mutableStateOf(false) }
    var roleExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "افزودن سرویسکار / تأمین‌کننده", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("نام شخص / کارشناس") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("شماره تماس") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = company,
                    onValueChange = { company = it },
                    label = { Text("نام شرکت / مجموعه (اختیاری)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Category Dropdown
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = !categoryExpanded }
                ) {
                    OutlinedTextField(
                        value = getCategoryPersianTitle(category),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("حوزه تخصصی و خدمت") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        ProviderCategory.entries.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(getCategoryPersianTitle(cat)) },
                                onClick = {
                                    category = cat
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }

                // Role Dropdown
                ExposedDropdownMenuBox(
                    expanded = roleExpanded,
                    onExpandedChange = { roleExpanded = !roleExpanded }
                ) {
                    OutlinedTextField(
                        value = if (role == ProviderRoleInBuilding.PRIMARY) "سرویسکار اصلی ساختمان" else "سرویسکار جایگزین / پشتیبان",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("نقش در ساختمان") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roleExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = roleExpanded,
                        onDismissRequest = { roleExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("سرویسکار اصلی ساختمان") },
                            onClick = {
                                role = ProviderRoleInBuilding.PRIMARY
                                roleExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("سرویسکار جایگزین / پشتیبان") },
                            onClick = {
                                role = ProviderRoleInBuilding.BACKUP
                                roleExpanded = false
                            }
                        )
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("توضیحات و یادداشت (اختیاری)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && phone.isNotBlank()) {
                        onConfirm(name.trim(), phone.trim(), company.trim().ifEmpty { null }, category, notes.trim().ifEmpty { null }, role)
                    }
                },
                enabled = name.isNotBlank() && phone.isNotBlank()
            ) {
                Text("ثبت و پیوند به ساختمان")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("انصراف") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddServiceRecordDialog(
    providers: List<BuildingServiceProvider>,
    onDismiss: () -> Unit,
    onConfirm: (ServiceType, ProviderCategory, String?, String, String, ServicePriority, Long?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var serviceType by remember { mutableStateOf(ServiceType.REPAIR) }
    var category by remember { mutableStateOf(ProviderCategory.GENERAL_MAINTENANCE) }
    var priority by remember { mutableStateOf(ServicePriority.NORMAL) }
    var selectedProviderId by remember { mutableStateOf<String?>(null) }
    var catExpanded by remember { mutableStateOf(false) }
    var typeExpanded by remember { mutableStateOf(false) }
    var prioExpanded by remember { mutableStateOf(false) }
    var provExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "ثبت درخواست خدمت / سفارش تعمیرات", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("عنوان خدمت یا خرابی") },
                    placeholder = { Text("مثال: رفع خرابی پمپ آب مرکزی") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("شرح جزئیات نیاز به خدمت") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Category
                ExposedDropdownMenuBox(
                    expanded = catExpanded,
                    onExpandedChange = { catExpanded = !catExpanded }
                ) {
                    OutlinedTextField(
                        value = getCategoryPersianTitle(category),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("حوزه تخصصی") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = catExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = catExpanded,
                        onDismissRequest = { catExpanded = false }
                    ) {
                        ProviderCategory.entries.forEach { c ->
                            DropdownMenuItem(
                                text = { Text(getCategoryPersianTitle(c)) },
                                onClick = {
                                    category = c
                                    catExpanded = false
                                }
                            )
                        }
                    }
                }

                // Service Type
                ExposedDropdownMenuBox(
                    expanded = typeExpanded,
                    onExpandedChange = { typeExpanded = !typeExpanded }
                ) {
                    OutlinedTextField(
                        value = getServiceTypePersianTitle(serviceType),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("نوع خدمت") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = typeExpanded,
                        onDismissRequest = { typeExpanded = false }
                    ) {
                        ServiceType.entries.forEach { st ->
                            DropdownMenuItem(
                                text = { Text(getServiceTypePersianTitle(st)) },
                                onClick = {
                                    serviceType = st
                                    typeExpanded = false
                                }
                            )
                        }
                    }
                }

                // Priority
                ExposedDropdownMenuBox(
                    expanded = prioExpanded,
                    onExpandedChange = { prioExpanded = !prioExpanded }
                ) {
                    OutlinedTextField(
                        value = getPriorityPersianTitle(priority),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("اولویت") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = prioExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = prioExpanded,
                        onDismissRequest = { prioExpanded = false }
                    ) {
                        ServicePriority.entries.forEach { p ->
                            DropdownMenuItem(
                                text = { Text(getPriorityPersianTitle(p)) },
                                onClick = {
                                    priority = p
                                    prioExpanded = false
                                }
                            )
                        }
                    }
                }

                // Provider Assignment
                ExposedDropdownMenuBox(
                    expanded = provExpanded,
                    onExpandedChange = { provExpanded = !provExpanded }
                ) {
                    val provName = providers.find { it.provider.id == selectedProviderId }?.provider?.name ?: "انتخاب سرویسکار (اختیاری)"
                    OutlinedTextField(
                        value = provName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("سرویسکار مأمور") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = provExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = provExpanded,
                        onDismissRequest = { provExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("بدون تخصیص (فعلاً)") },
                            onClick = {
                                selectedProviderId = null
                                provExpanded = false
                            }
                        )
                        providers.forEach { p ->
                            DropdownMenuItem(
                                text = { Text("${p.provider.name} (${getCategoryPersianTitle(p.category)})") },
                                onClick = {
                                    selectedProviderId = p.provider.id
                                    provExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && description.isNotBlank()) {
                        onConfirm(serviceType, category, selectedProviderId, title.trim(), description.trim(), priority, System.currentTimeMillis())
                    }
                },
                enabled = title.isNotBlank() && description.isNotBlank()
            ) {
                Text("ثبت درخواست")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("انصراف") }
        }
    )
}

@Composable
fun AddMaintenanceDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, Long, Long?, String, Long, String?, String?) -> Unit
) {
    var equipmentName by remember { mutableStateOf("") }
    var serviceType by remember { mutableStateOf("سرویس و بازرسی ماهانه") }
    var performedBy by remember { mutableStateOf("") }
    var costStr by remember { mutableStateOf("0") }
    var partsReplaced by remember { mutableStateOf("") }
    var checklistNotes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "ثبت سرویس و نگهداری دوره‌ای تجهیزات", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = equipmentName,
                    onValueChange = { equipmentName = it },
                    label = { Text("نام تجهیز") },
                    placeholder = { Text("مثال: آسانسور بلوک A / موتورخانه") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = serviceType,
                    onValueChange = { serviceType = it },
                    label = { Text("عنوان / نوع سرویس") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = performedBy,
                    onValueChange = { performedBy = it },
                    label = { Text("سرویسکار / تکنسین انجام‌دهنده") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = costStr,
                    onValueChange = { costStr = it },
                    label = { Text("هزینه سرویس (ریال)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = partsReplaced,
                    onValueChange = { partsReplaced = it },
                    label = { Text("قطعات مصرفی / تعویض‌شده (اختیاری)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = checklistNotes,
                    onValueChange = { checklistNotes = it },
                    label = { Text("چک‌لیست / نکات فنی بازرسی (اختیاری)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val cost = costStr.toLongOrNull() ?: 0L
                    val now = System.currentTimeMillis()
                    val nextMonth = now + 30L * 24 * 60 * 60 * 1000
                    if (equipmentName.isNotBlank() && performedBy.isNotBlank()) {
                        onConfirm(equipmentName.trim(), serviceType.trim(), now, nextMonth, performedBy.trim(), cost, partsReplaced.trim().ifEmpty { null }, checklistNotes.trim().ifEmpty { null })
                    }
                },
                enabled = equipmentName.isNotBlank() && performedBy.isNotBlank()
            ) {
                Text("ثبت در دفترچه نگهداری")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("انصراف") }
        }
    )
}

@Composable
fun AddInvoiceDialog(
    providers: List<BuildingServiceProvider>,
    serviceRecords: List<ServiceRecord>,
    onDismiss: () -> Unit,
    onConfirm: (String, String?, String?, Long, Long, Long, String?) -> Unit
) {
    var invoiceNumber by remember { mutableStateOf("INV-${(1000..9999).random()}") }
    var totalAmountStr by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedProviderId by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "صدور و ثبت فاکتور خدمات", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = invoiceNumber,
                    onValueChange = { invoiceNumber = it },
                    label = { Text("شماره فاکتور") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = totalAmountStr,
                    onValueChange = { totalAmountStr = it },
                    label = { Text("مبلغ کل فاکتور (ریال)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("شرح فاکتور و اقلام خدماتی") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val total = totalAmountStr.toLongOrNull() ?: 0L
                    val now = System.currentTimeMillis()
                    val due = now + 7L * 24 * 60 * 60 * 1000
                    if (invoiceNumber.isNotBlank() && total > 0) {
                        onConfirm(invoiceNumber.trim(), selectedProviderId, null, now, due, total, description.trim().ifEmpty { null })
                    }
                },
                enabled = invoiceNumber.isNotBlank() && (totalAmountStr.toLongOrNull() ?: 0L) > 0
            ) {
                Text("ثبت فاکتور")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("انصراف") }
        }
    )
}

@Composable
fun AddPaymentDialog(
    invoice: Invoice?,
    onDismiss: () -> Unit,
    onConfirm: (String, Long, Long, PaymentMethod, String?, String, String?) -> Unit
) {
    val remaining = if (invoice != null) invoice.totalAmount - invoice.paidAmount else 0L
    var amountStr by remember { mutableStateOf(if (remaining > 0) remaining.toString() else "") }
    var paidBy by remember { mutableStateOf("مدیر ساختمان (صندوق مرکزی)") }
    var referenceNumber by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "ثبت پرداخت و تسویه فاکتور", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                invoice?.let {
                    Text(text = "فاکتور: #${it.invoiceNumber} • مانده: ${formatCurrency(remaining)} ریال", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                }
                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("مبلغ پرداختی (ریال)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = paidBy,
                    onValueChange = { paidBy = it },
                    label = { Text("پرداخت‌کننده / منبع پرداخت") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = referenceNumber,
                    onValueChange = { referenceNumber = it },
                    label = { Text("شماره پیگیری / ارجاع بانکی") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("توضیحات پرداخت (اختیاری)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountStr.toLongOrNull() ?: 0L
                    if (invoice != null && amt > 0) {
                        onConfirm(invoice.id, amt, System.currentTimeMillis(), PaymentMethod.BANK_TRANSFER, referenceNumber.trim().ifEmpty { null }, paidBy.trim(), notes.trim().ifEmpty { null })
                    }
                },
                enabled = invoice != null && (amountStr.toLongOrNull() ?: 0L) > 0
            ) {
                Text("ثبت تسویه")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("انصراف") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBillDialog(
    onDismiss: () -> Unit,
    onConfirm: (BillType, String, Long, Long, Long, Long, String?) -> Unit
) {
    var billType by remember { mutableStateOf(BillType.ELECTRICITY) }
    var identifier by remember { mutableStateOf("") }
    var amountStr by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var billTypeExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "ثبت قبض عمومی ساختمان", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ExposedDropdownMenuBox(
                    expanded = billTypeExpanded,
                    onExpandedChange = { billTypeExpanded = !billTypeExpanded }
                ) {
                    OutlinedTextField(
                        value = getBillTypePersianTitle(billType),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("نوع قبض") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = billTypeExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = billTypeExpanded,
                        onDismissRequest = { billTypeExpanded = false }
                    ) {
                        BillType.entries.forEach { bt ->
                            DropdownMenuItem(
                                text = { Text(getBillTypePersianTitle(bt)) },
                                onClick = {
                                    billType = bt
                                    billTypeExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = identifier,
                    onValueChange = { identifier = it },
                    label = { Text("شناسه قبض / شناسه پرداخت") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("مبلغ قبض (ریال)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("توضیحات قبض (اختیاری)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountStr.toLongOrNull() ?: 0L
                    val now = System.currentTimeMillis()
                    val monthAgo = now - 30L * 24 * 60 * 60 * 1000
                    val due = now + 10L * 24 * 60 * 60 * 1000
                    if (amt > 0) {
                        onConfirm(billType, identifier.trim(), monthAgo, now, due, amt, description.trim().ifEmpty { null })
                    }
                },
                enabled = (amountStr.toLongOrNull() ?: 0L) > 0
            ) {
                Text("ثبت قبض")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("انصراف") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateServiceStatusDialog(
    record: ServiceRecord,
    onDismiss: () -> Unit,
    onConfirm: (ServiceRecordStatus, Long?, Long?, String?) -> Unit
) {
    var status by remember { mutableStateOf(record.status) }
    var costEstimateStr by remember { mutableStateOf(record.costEstimate?.toString() ?: "") }
    var actualCostStr by remember { mutableStateOf(record.actualCost?.toString() ?: "") }
    var resolutionNotes by remember { mutableStateOf(record.resolutionNotes ?: "") }
    var statusExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "به‌روزرسانی وضعیت خدمت", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(text = "خدمت: ${record.title}", fontWeight = FontWeight.SemiBold)

                ExposedDropdownMenuBox(
                    expanded = statusExpanded,
                    onExpandedChange = { statusExpanded = !statusExpanded }
                ) {
                    OutlinedTextField(
                        value = getStatusPersianTitle(status),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("وضعیت جدید") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = statusExpanded,
                        onDismissRequest = { statusExpanded = false }
                    ) {
                        ServiceRecordStatus.entries.forEach { st ->
                            DropdownMenuItem(
                                text = { Text(getStatusPersianTitle(st)) },
                                onClick = {
                                    status = st
                                    statusExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = costEstimateStr,
                    onValueChange = { costEstimateStr = it },
                    label = { Text("برآورد هزینه (ریال)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = actualCostStr,
                    onValueChange = { actualCostStr = it },
                    label = { Text("هزینه نهایی انجام شده (ریال)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = resolutionNotes,
                    onValueChange = { resolutionNotes = it },
                    label = { Text("گزارش انجام کار و نتیجه") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val est = costEstimateStr.toLongOrNull()
                    val act = actualCostStr.toLongOrNull()
                    onConfirm(status, est, act, resolutionNotes.trim().ifEmpty { null })
                }
            ) {
                Text("به‌روزرسانی")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("انصراف") }
        }
    )
}

// ----------------------------------------------------
// HELPER COMPONENTS & BADGES
// ----------------------------------------------------

@Composable
fun MetricCard(
    title: String,
    value: String,
    subValue: String? = null,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = title, style = MaterialTheme.typography.bodySmall, color = Color.Black.copy(alpha = 0.7f))
            Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            if (subValue != null) {
                Text(text = subValue, style = MaterialTheme.typography.labelSmall, color = Color.DarkGray)
            }
        }
    }
}

@Composable
fun EmptyStateCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
            Text(text = message, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        }
    }
}

@Composable
fun FilterChipButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun RoleBadge(role: ProviderRoleInBuilding) {
    val isPrimary = role == ProviderRoleInBuilding.PRIMARY
    val bg = if (isPrimary) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
    val fg = if (isPrimary) Color(0xFF2E7D32) else Color(0xFFE65100)
    val text = if (isPrimary) "سرویسکار اصلی" else "سرویسکار جایگزین"

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = text, color = fg, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun CategoryBadge(category: ProviderCategory) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = getCategoryPersianTitle(category), style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
fun ServiceStatusBadge(status: ServiceRecordStatus) {
    val (bg, fg) = when (status) {
        ServiceRecordStatus.REQUESTED -> Color(0xFFFFF3E0) to Color(0xFFE65100)
        ServiceRecordStatus.SCHEDULED -> Color(0xFFE3F2FD) to Color(0xFF1565C0)
        ServiceRecordStatus.IN_PROGRESS -> Color(0xFFEDE7F6) to Color(0xFF5E35B1)
        ServiceRecordStatus.COMPLETED -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
        ServiceRecordStatus.CANCELLED -> Color(0xFFFFEBEE) to Color(0xFFC62828)
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = getStatusPersianTitle(status), color = fg, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun InvoiceStatusBadge(status: InvoiceStatus) {
    val (bg, fg) = when (status) {
        InvoiceStatus.UNPAID -> Color(0xFFFFEBEE) to Color(0xFFC62828)
        InvoiceStatus.PARTIALLY_PAID -> Color(0xFFFFF3E0) to Color(0xFFE65100)
        InvoiceStatus.PAID -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
        InvoiceStatus.CANCELLED -> Color(0xFFEEEEEE) to Color(0xFF757575)
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = getInvoiceStatusPersianTitle(status), color = fg, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun BillStatusBadge(isPaid: Boolean) {
    val bg = if (isPaid) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
    val fg = if (isPaid) Color(0xFF2E7D32) else Color(0xFFC62828)
    val text = if (isPaid) "تسویه شده" else "پرداخت نشده"

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = text, color = fg, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
    }
}

// ----------------------------------------------------
// FORMATTERS & PERSIAN CONVERTERS
// ----------------------------------------------------

fun getCategoryPersianTitle(cat: ProviderCategory): String = when (cat) {
    ProviderCategory.ELEVATOR -> "آسانسور و بالابر"
    ProviderCategory.ENGINE_ROOM -> "موتورخانه و گرمایش"
    ProviderCategory.WATER_PUMP -> "پمپ آب و آبرسانی"
    ProviderCategory.FIRE_SAFETY -> "اطفاء و اعلام حریق"
    ProviderCategory.HVAC -> "سرمایش، چیلر و اسپلیت"
    ProviderCategory.ELECTRICAL -> "برق و روشنایی مشاعات"
    ProviderCategory.PLUMBING -> "لوله کشی و فاضلاب"
    ProviderCategory.CLEANING -> "نظافت و بهداشت مشاعات"
    ProviderCategory.SECURITY_CAMERAS -> "دوربین و سیستم امنیتی"
    ProviderCategory.GARDENING -> "فضای سبز و باغبانی"
    ProviderCategory.GENERAL_MAINTENANCE -> "نگهداری عمومی و تأسیسات"
    ProviderCategory.OTHER -> "سایر خدمات"
}

fun getServiceTypePersianTitle(type: ServiceType): String = when (type) {
    ServiceType.PERIODIC_MAINTENANCE -> "سرویس دوره‌ای"
    ServiceType.REPAIR -> "تعمیرات خرابی"
    ServiceType.EMERGENCY -> "رفع نقص فوری"
    ServiceType.INSPECTION -> "بازرسی و معاینه فنی"
    ServiceType.INSTALLATION -> "نصب و راه‌اندازی"
    ServiceType.CLEANING -> "نظافت و شستشو"
    ServiceType.OTHER -> "سایر"
}

fun getPriorityPersianTitle(priority: ServicePriority): String = when (priority) {
    ServicePriority.LOW -> "کم"
    ServicePriority.NORMAL -> "عادی"
    ServicePriority.HIGH -> "مهم"
    ServicePriority.CRITICAL -> "اضطراری / حیاتی"
}

fun getStatusPersianTitle(status: ServiceRecordStatus): String = when (status) {
    ServiceRecordStatus.REQUESTED -> "درخواست شده"
    ServiceRecordStatus.SCHEDULED -> "برنامه‌ریزی شده"
    ServiceRecordStatus.IN_PROGRESS -> "در حال انجام"
    ServiceRecordStatus.COMPLETED -> "تکمیل شده"
    ServiceRecordStatus.CANCELLED -> "لغو شده"
}

fun getInvoiceStatusPersianTitle(status: InvoiceStatus): String = when (status) {
    InvoiceStatus.UNPAID -> "پرداخت نشده"
    InvoiceStatus.PARTIALLY_PAID -> "پرداخت ناقص"
    InvoiceStatus.PAID -> "تسویه کامل"
    InvoiceStatus.CANCELLED -> "باطل شده"
}

fun getBillTypePersianTitle(type: BillType): String = when (type) {
    BillType.WATER -> "آب عمومی"
    BillType.ELECTRICITY -> "برق مشاعات"
    BillType.GAS -> "گاز مرکزی"
    BillType.INTERNET -> "اینترنت مرکزی"
    BillType.WASTE_MANAGEMENT -> "عوارض پسماند و نوسازی"
    BillType.OTHER -> "سایر قبوض"
}

fun getBillTypeIcon(type: BillType): String = when (type) {
    BillType.WATER -> "💧"
    BillType.ELECTRICITY -> "⚡"
    BillType.GAS -> "🔥"
    BillType.INTERNET -> "🌐"
    BillType.WASTE_MANAGEMENT -> "🗑️"
    BillType.OTHER -> "📄"
}

fun formatCurrency(amount: Long): String {
    return NumberFormat.getNumberInstance(Locale.US).format(amount)
}

fun formatDate(timeMillis: Long): String {
    val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
    return sdf.format(Date(timeMillis))
}
