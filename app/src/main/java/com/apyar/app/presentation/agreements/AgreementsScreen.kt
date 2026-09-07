package com.apyar.app.presentation.agreements

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apyar.app.domain.model.AgreementDetails
import com.apyar.app.domain.model.AgreementStatus
import com.apyar.app.domain.model.FinancialArrangement
import com.apyar.app.domain.model.ParkingSpaceWithDetails
import com.apyar.app.domain.model.StorageUnitWithDetails
import com.apyar.app.domain.model.Unit
import com.apyar.app.presentation.parking.StatBadge
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgreementsScreen(
    viewModel: AgreementsViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val agreements by viewModel.agreements.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage, uiState.successMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "توافقات بین واحدها و اجاره فضاها",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        ) { innerPadding ->
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Fundamental Disclaimer Card
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    Icons.Default.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "اصل بنیادین: اپیار مالکیت حقوقی پارکینگ یا انباری را تشخیص نمی‌دهد؛ صرفاً اطلاعات ثبت‌شده توسط کاربران مجاز و آثار توافقات مالی در ساختمان را مدیریت و اعمال می‌کند.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            }
                        }
                    }

                    // Action Buttons: Register New Agreements
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { viewModel.openCreateParkingAgreementDialog() },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("توافق پارکینگ", fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { viewModel.openCreateStorageAgreementDialog() },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("توافق انباری", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Summary Card
                    item {
                        AgreementsSummaryCard(agreements = agreements)
                    }

                    // Status Filters
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = uiState.filterStatus == null,
                                onClick = { viewModel.setFilterStatus(null) },
                                label = { Text("همه (${agreements.size})") }
                            )
                            AgreementStatus.entries.forEach { status ->
                                FilterChip(
                                    selected = uiState.filterStatus == status,
                                    onClick = { viewModel.setFilterStatus(status) },
                                    label = { Text(status.titleFa) }
                                )
                            }
                        }
                    }

                    if (agreements.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        modifier = Modifier.size(44.dp),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "هیچ توافقی با این وضعیت ثبت نشده است.",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "برای واگذاری یا اجاره پارکینگ یا انباری میان واحدها، از دکمه‌های بالا استفاده کنید.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    } else {
                        items(agreements, key = { it.agreement.id }) { item ->
                            AgreementItemCard(
                                item = item,
                                onClick = { viewModel.openDetailsDialog(item) }
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(72.dp))
                    }
                }
            }
        }

        // Dialogs
        if (uiState.isCreateParkingAgreementDialogOpen) {
            CreateParkingAgreementDialog(
                units = uiState.units,
                parkingSpaces = uiState.parkingSpaces,
                onDismiss = { viewModel.closeCreateParkingAgreementDialog() },
                onConfirm = { spaceId, srcId, tgtId, sDate, eDate, finArr, amt, desc, docTitle, docPath ->
                    viewModel.createParkingAgreement(
                        parkingSpaceId = spaceId,
                        sourceUnitId = srcId,
                        targetUnitId = tgtId,
                        startDate = sDate,
                        endDate = eDate,
                        financialArrangement = finArr,
                        amount = amt,
                        description = desc,
                        documentTitle = docTitle,
                        documentFilePath = docPath
                    )
                }
            )
        }

        if (uiState.isCreateStorageAgreementDialogOpen) {
            CreateStorageAgreementDialog(
                units = uiState.units,
                storageUnits = uiState.storageUnits,
                onDismiss = { viewModel.closeCreateStorageAgreementDialog() },
                onConfirm = { storageId, srcId, tgtId, sDate, eDate, finArr, amt, desc, docTitle, docPath ->
                    viewModel.createStorageAgreement(
                        storageUnitId = storageId,
                        sourceUnitId = srcId,
                        targetUnitId = tgtId,
                        startDate = sDate,
                        endDate = eDate,
                        financialArrangement = finArr,
                        amount = amt,
                        description = desc,
                        documentTitle = docTitle,
                        documentFilePath = docPath
                    )
                }
            )
        }

        if (uiState.isDetailsDialogOpen && uiState.selectedAgreement != null) {
            AgreementDetailsDialog(
                details = uiState.selectedAgreement!!,
                onDismiss = { viewModel.closeDetailsDialog() },
                onCancelAgreement = { reason ->
                    viewModel.cancelAgreement(uiState.selectedAgreement!!.agreement.id, reason)
                }
            )
        }
    }
}

@Composable
fun AgreementsSummaryCard(agreements: List<AgreementDetails>) {
    val total = agreements.size
    val active = agreements.count { it.agreement.status == AgreementStatus.ACTIVE }
    val cancelled = agreements.count { it.agreement.status == AgreementStatus.CANCELLED }
    val expired = agreements.count { it.agreement.status == AgreementStatus.EXPIRED }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "خلاصه توافقات ساختمان",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatBadge(
                    modifier = Modifier.weight(1f),
                    label = "فعال",
                    count = active,
                    color = Color(0xFF2E7D32)
                )
                StatBadge(
                    modifier = Modifier.weight(1f),
                    label = "لغوشده",
                    count = cancelled,
                    color = Color(0xFFC62828)
                )
                StatBadge(
                    modifier = Modifier.weight(1f),
                    label = "منقضی‌شده",
                    count = expired,
                    color = Color(0xFF616161)
                )
            }
        }
    }
}

@Composable
fun AgreementItemCard(
    item: AgreementDetails,
    onClick: () -> Unit
) {
    val agreement = item.agreement
    val numberFormat = remember { NumberFormat.getNumberInstance(Locale("fa", "IR")) }
    val dateFormat = remember { SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()) }

    val (statusBg, statusFg) = when (agreement.status) {
        AgreementStatus.ACTIVE -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
        AgreementStatus.EXPIRED -> Color(0xFFEEEEEE) to Color(0xFF616161)
        AgreementStatus.CANCELLED -> Color(0xFFFFEBEE) to Color(0xFFC62828)
        AgreementStatus.TERMINATED -> Color(0xFFFFF3E0) to Color(0xFFE65100)
    }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Top Row: Type & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = agreement.agreementType.titleFa,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = statusBg
                ) {
                    Text(
                        text = agreement.status.titleFa,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = statusFg,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            // Unit Flow: Source Unit -> Target Unit
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("واگذارکننده:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("واحد ${item.sourceUnitNumber ?: "-"}", fontWeight = FontWeight.Bold)
                }

                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = MaterialTheme.colorScheme.primary)

                Column {
                    Text("استفاده‌کننده:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("واحد ${item.targetUnitNumber ?: "-"}", fontWeight = FontWeight.Bold)
                }

                if (item.parkingSpaceCode != null) {
                    Surface(shape = RoundedCornerShape(6.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                        Text(
                            text = "پارکینگ ${item.parkingSpaceCode}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                } else if (item.storageUnitCode != null) {
                    Surface(shape = RoundedCornerShape(6.dp), color = MaterialTheme.colorScheme.secondaryContainer) {
                        Text(
                            text = "انباری ${item.storageUnitCode}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Financial & Validity Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ترتیبات مالی: ${agreement.financialArrangement.titleFa}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (agreement.amount > 0) {
                        Text(
                            text = "${numberFormat.format(agreement.amount)} ریال",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Text(
                    text = "${dateFormat.format(Date(agreement.startDate))}" +
                            (agreement.endDate?.let { " تا ${dateFormat.format(Date(it))}" } ?: " (نامحدود)"),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (item.documents.isNotEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(Icons.Default.AttachFile, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                    Text(
                        text = "${item.documents.size} سند/مستند ضمیمه شده",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateParkingAgreementDialog(
    units: List<Unit>,
    parkingSpaces: List<ParkingSpaceWithDetails>,
    onDismiss: () -> Unit,
    onConfirm: (
        parkingSpaceId: String,
        sourceUnitId: String,
        targetUnitId: String,
        startDate: Long,
        endDate: Long?,
        financialArrangement: FinancialArrangement,
        amount: Double,
        description: String?,
        documentTitle: String?,
        documentFilePath: String?
    ) -> Unit
) {
    var selectedSpaceId by remember { mutableStateOf(parkingSpaces.firstOrNull()?.space?.id ?: "") }
    var selectedSourceUnitId by remember { mutableStateOf(units.firstOrNull()?.id ?: "") }
    var selectedTargetUnitId by remember { mutableStateOf(units.getOrNull(1)?.id ?: units.firstOrNull()?.id ?: "") }
    var selectedFinancialArrangement by remember { mutableStateOf(FinancialArrangement.MONTHLY_AMOUNT) }
    var amountText by remember { mutableStateOf("5000000") }
    var description by remember { mutableStateOf("") }
    var documentTitle by remember { mutableStateOf("") }
    var documentPath by remember { mutableStateOf("") }

    var spaceExpanded by remember { mutableStateOf(false) }
    var sourceUnitExpanded by remember { mutableStateOf(false) }
    var targetUnitExpanded by remember { mutableStateOf(false) }
    var finExpanded by remember { mutableStateOf(false) }

    val selectedSpace = parkingSpaces.find { it.space.id == selectedSpaceId }
    val selectedSourceUnit = units.find { it.id == selectedSourceUnitId }
    val selectedTargetUnit = units.find { it.id == selectedTargetUnitId }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("ثبت توافق استفاده / اجاره پارکینگ", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(
                modifier = Modifier.height(380.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    // Parking Space Selector
                    ExposedDropdownMenuBox(
                        expanded = spaceExpanded,
                        onExpandedChange = { spaceExpanded = !spaceExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedSpace?.let { "پارکینگ ${it.space.code} (طبقه ${it.space.floor})" } ?: "انتخاب پارکینگ...",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("پارکینگ مورد توافق *") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = spaceExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = spaceExpanded,
                            onDismissRequest = { spaceExpanded = false }
                        ) {
                            parkingSpaces.forEach { spaceWithDetails ->
                                DropdownMenuItem(
                                    text = { Text("پارکینگ ${spaceWithDetails.space.code} (طبقه ${spaceWithDetails.space.floor} - ${spaceWithDetails.space.status.titleFa})") },
                                    onClick = {
                                        selectedSpaceId = spaceWithDetails.space.id
                                        // If space is assigned to a unit, preset source unit
                                        spaceWithDetails.space.assignedUnitId?.let { assignedId ->
                                            selectedSourceUnitId = assignedId
                                        }
                                        spaceExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    // Source Unit (واگذارکننده)
                    ExposedDropdownMenuBox(
                        expanded = sourceUnitExpanded,
                        onExpandedChange = { sourceUnitExpanded = !sourceUnitExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedSourceUnit?.let { "واحد ${it.unitNumber} (واگذارکننده)" } ?: "انتخاب واحد...",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("واحد واگذارکننده *") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sourceUnitExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = sourceUnitExpanded,
                            onDismissRequest = { sourceUnitExpanded = false }
                        ) {
                            units.forEach { unit ->
                                DropdownMenuItem(
                                    text = { Text("واحد ${unit.unitNumber} (طبقه ${unit.floor})") },
                                    onClick = {
                                        selectedSourceUnitId = unit.id
                                        sourceUnitExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    // Target Unit (استفاده‌کننده)
                    ExposedDropdownMenuBox(
                        expanded = targetUnitExpanded,
                        onExpandedChange = { targetUnitExpanded = !targetUnitExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedTargetUnit?.let { "واحد ${it.unitNumber} (استفاده‌کننده / مستأجر)" } ?: "انتخاب واحد...",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("واحد متقاضی / استفاده‌کننده *") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = targetUnitExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = targetUnitExpanded,
                            onDismissRequest = { targetUnitExpanded = false }
                        ) {
                            units.forEach { unit ->
                                DropdownMenuItem(
                                    text = { Text("واحد ${unit.unitNumber} (طبقه ${unit.floor})") },
                                    onClick = {
                                        selectedTargetUnitId = unit.id
                                        targetUnitExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    // Financial Arrangement
                    ExposedDropdownMenuBox(
                        expanded = finExpanded,
                        onExpandedChange = { finExpanded = !finExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedFinancialArrangement.titleFa,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("ترتیبات مالی توافق") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = finExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = finExpanded,
                            onDismissRequest = { finExpanded = false }
                        ) {
                            FinancialArrangement.entries.forEach { fa ->
                                DropdownMenuItem(
                                    text = { Text(fa.titleFa) },
                                    onClick = {
                                        selectedFinancialArrangement = fa
                                        finExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                if (selectedFinancialArrangement != FinancialArrangement.NO_PAYMENT) {
                    item {
                        OutlinedTextField(
                            value = amountText,
                            onValueChange = { amountText = it },
                            label = { Text("مبلغ توافق شده (ریال)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("توضیحات و شرایط توافق") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = documentTitle,
                        onValueChange = { documentTitle = it },
                        label = { Text("عنوان سند / صورت‌جلسه (اختیاری)") },
                        placeholder = { Text("مثال: توافق‌نامه دست‌نویس مالکین") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (selectedSpaceId.isNotBlank() && selectedSourceUnitId.isNotBlank() && selectedTargetUnitId.isNotBlank()) {
                        val amount = amountText.toDoubleOrNull() ?: 0.0
                        onConfirm(
                            selectedSpaceId,
                            selectedSourceUnitId,
                            selectedTargetUnitId,
                            System.currentTimeMillis(),
                            null, // ongoing agreement
                            selectedFinancialArrangement,
                            amount,
                            description.ifBlank { null },
                            documentTitle.ifBlank { null },
                            documentPath.ifBlank { null }
                        )
                    }
                },
                enabled = selectedSpaceId.isNotBlank() && selectedSourceUnitId.isNotBlank() && selectedTargetUnitId.isNotBlank() && selectedSourceUnitId != selectedTargetUnitId
            ) {
                Text("ثبت توافق")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("انصراف")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateStorageAgreementDialog(
    units: List<Unit>,
    storageUnits: List<StorageUnitWithDetails>,
    onDismiss: () -> Unit,
    onConfirm: (
        storageUnitId: String,
        sourceUnitId: String,
        targetUnitId: String,
        startDate: Long,
        endDate: Long?,
        financialArrangement: FinancialArrangement,
        amount: Double,
        description: String?,
        documentTitle: String?,
        documentFilePath: String?
    ) -> Unit
) {
    var selectedStorageId by remember { mutableStateOf(storageUnits.firstOrNull()?.storage?.id ?: "") }
    var selectedSourceUnitId by remember { mutableStateOf(units.firstOrNull()?.id ?: "") }
    var selectedTargetUnitId by remember { mutableStateOf(units.getOrNull(1)?.id ?: units.firstOrNull()?.id ?: "") }
    var selectedFinancialArrangement by remember { mutableStateOf(FinancialArrangement.MONTHLY_AMOUNT) }
    var amountText by remember { mutableStateOf("2000000") }
    var description by remember { mutableStateOf("") }
    var documentTitle by remember { mutableStateOf("") }
    var documentPath by remember { mutableStateOf("") }

    var storageExpanded by remember { mutableStateOf(false) }
    var sourceUnitExpanded by remember { mutableStateOf(false) }
    var targetUnitExpanded by remember { mutableStateOf(false) }
    var finExpanded by remember { mutableStateOf(false) }

    val selectedStorage = storageUnits.find { it.storage.id == selectedStorageId }
    val selectedSourceUnit = units.find { it.id == selectedSourceUnitId }
    val selectedTargetUnit = units.find { it.id == selectedTargetUnitId }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("ثبت توافق استفاده / اجاره انباری", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(
                modifier = Modifier.height(380.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    // Storage Selector
                    ExposedDropdownMenuBox(
                        expanded = storageExpanded,
                        onExpandedChange = { storageExpanded = !storageExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedStorage?.let { "انباری ${it.storage.code} (طبقه ${it.storage.floor})" } ?: "انتخاب انباری...",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("انباری مورد توافق *") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = storageExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = storageExpanded,
                            onDismissRequest = { storageExpanded = false }
                        ) {
                            storageUnits.forEach { storageWithDetails ->
                                DropdownMenuItem(
                                    text = { Text("انباری ${storageWithDetails.storage.code} (طبقه ${storageWithDetails.storage.floor} - ${storageWithDetails.storage.status.titleFa})") },
                                    onClick = {
                                        selectedStorageId = storageWithDetails.storage.id
                                        storageWithDetails.storage.assignedUnitId?.let { assignedId ->
                                            selectedSourceUnitId = assignedId
                                        }
                                        storageExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    // Source Unit
                    ExposedDropdownMenuBox(
                        expanded = sourceUnitExpanded,
                        onExpandedChange = { sourceUnitExpanded = !sourceUnitExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedSourceUnit?.let { "واحد ${it.unitNumber} (واگذارکننده)" } ?: "انتخاب واحد...",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("واحد واگذارکننده *") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sourceUnitExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = sourceUnitExpanded,
                            onDismissRequest = { sourceUnitExpanded = false }
                        ) {
                            units.forEach { unit ->
                                DropdownMenuItem(
                                    text = { Text("واحد ${unit.unitNumber} (طبقه ${unit.floor})") },
                                    onClick = {
                                        selectedSourceUnitId = unit.id
                                        sourceUnitExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    // Target Unit
                    ExposedDropdownMenuBox(
                        expanded = targetUnitExpanded,
                        onExpandedChange = { targetUnitExpanded = !targetUnitExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedTargetUnit?.let { "واحد ${it.unitNumber} (استفاده‌کننده / مستأجر)" } ?: "انتخاب واحد...",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("واحد متقاضی / استفاده‌کننده *") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = targetUnitExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = targetUnitExpanded,
                            onDismissRequest = { targetUnitExpanded = false }
                        ) {
                            units.forEach { unit ->
                                DropdownMenuItem(
                                    text = { Text("واحد ${unit.unitNumber} (طبقه ${unit.floor})") },
                                    onClick = {
                                        selectedTargetUnitId = unit.id
                                        targetUnitExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    // Financial Arrangement
                    ExposedDropdownMenuBox(
                        expanded = finExpanded,
                        onExpandedChange = { finExpanded = !finExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedFinancialArrangement.titleFa,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("ترتیبات مالی توافق") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = finExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = finExpanded,
                            onDismissRequest = { finExpanded = false }
                        ) {
                            FinancialArrangement.entries.forEach { fa ->
                                DropdownMenuItem(
                                    text = { Text(fa.titleFa) },
                                    onClick = {
                                        selectedFinancialArrangement = fa
                                        finExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                if (selectedFinancialArrangement != FinancialArrangement.NO_PAYMENT) {
                    item {
                        OutlinedTextField(
                            value = amountText,
                            onValueChange = { amountText = it },
                            label = { Text("مبلغ توافق شده (ریال)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("توضیحات و شرایط توافق") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = documentTitle,
                        onValueChange = { documentTitle = it },
                        label = { Text("عنوان سند / صورت‌جلسه (اختیاری)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (selectedStorageId.isNotBlank() && selectedSourceUnitId.isNotBlank() && selectedTargetUnitId.isNotBlank()) {
                        val amount = amountText.toDoubleOrNull() ?: 0.0
                        onConfirm(
                            selectedStorageId,
                            selectedSourceUnitId,
                            selectedTargetUnitId,
                            System.currentTimeMillis(),
                            null,
                            selectedFinancialArrangement,
                            amount,
                            description.ifBlank { null },
                            documentTitle.ifBlank { null },
                            documentPath.ifBlank { null }
                        )
                    }
                },
                enabled = selectedStorageId.isNotBlank() && selectedSourceUnitId.isNotBlank() && selectedTargetUnitId.isNotBlank() && selectedSourceUnitId != selectedTargetUnitId
            ) {
                Text("ثبت توافق")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("انصراف")
            }
        }
    )
}

@Composable
fun AgreementDetailsDialog(
    details: AgreementDetails,
    onDismiss: () -> Unit,
    onCancelAgreement: (reason: String?) -> Unit
) {
    var isCancelling by remember { mutableStateOf(false) }
    var cancelReason by remember { mutableStateOf("") }
    val numberFormat = remember { NumberFormat.getNumberInstance(Locale("fa", "IR")) }
    val dateFormat = remember { SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "جزئیات ${details.agreement.agreementType.titleFa}",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("وضعیت توافق:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(details.agreement.status.titleFa, fontWeight = FontWeight.Bold)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("واگذارکننده:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("واحد ${details.sourceUnitNumber ?: "-"}", fontWeight = FontWeight.Bold)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("استفاده‌کننده:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("واحد ${details.targetUnitNumber ?: "-"}", fontWeight = FontWeight.Bold)
                }

                if (details.parkingSpaceCode != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("کد پارکینگ:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("پارکینگ ${details.parkingSpaceCode}", fontWeight = FontWeight.Bold)
                    }
                }

                if (details.storageUnitCode != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("کد انباری:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("انباری ${details.storageUnitCode}", fontWeight = FontWeight.Bold)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("ترتیبات مالی:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(details.agreement.financialArrangement.titleFa, fontWeight = FontWeight.SemiBold)
                }

                if (details.agreement.amount > 0) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("مبلغ توافق:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${numberFormat.format(details.agreement.amount)} ریال", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("تاریخ شروع:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(dateFormat.format(Date(details.agreement.startDate)))
                }

                if (!details.agreement.description.isNullOrBlank()) {
                    Text(
                        text = "توضیحات: ${details.agreement.description}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (isCancelling) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = cancelReason,
                        onValueChange = { cancelReason = it },
                        label = { Text("علت لغو / فسخ توافق") },
                        placeholder = { Text("مثال: تخلیه مستأجر یا پایان دوره") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            if (details.agreement.status == AgreementStatus.ACTIVE) {
                if (isCancelling) {
                    Button(
                        onClick = { onCancelAgreement(cancelReason.ifBlank { null }) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("تأیید لغو توافق")
                    }
                } else {
                    OutlinedButton(
                        onClick = { isCancelling = true },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("لغو / فسخ توافق")
                    }
                }
            } else {
                Button(onClick = onDismiss) {
                    Text("بستن")
                }
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("انصراف")
            }
        }
    )
}
