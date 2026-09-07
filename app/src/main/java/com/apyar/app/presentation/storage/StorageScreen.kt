package com.apyar.app.presentation.storage

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
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
import com.apyar.app.domain.model.AssignmentType
import com.apyar.app.domain.model.SpaceStatus
import com.apyar.app.domain.model.StorageUnitWithDetails
import com.apyar.app.domain.model.Unit
import com.apyar.app.presentation.parking.StatBadge
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StorageScreen(
    viewModel: StorageViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val storageUnits by viewModel.storageUnits.collectAsState()
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
                            text = "مدیریت انباری‌ها",
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
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { viewModel.openCreateUnitDialog() },
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "تعریف انباری",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
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
                    // Summary Banner
                    item {
                        StorageSummaryCard(storageUnits = storageUnits)
                    }

                    // Status Filter Chips
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
                                label = { Text("همه (${storageUnits.size})") }
                            )
                            SpaceStatus.entries.forEach { status ->
                                FilterChip(
                                    selected = uiState.filterStatus == status,
                                    onClick = { viewModel.setFilterStatus(status) },
                                    label = { Text(status.titleFa) }
                                )
                            }
                        }
                    }

                    if (storageUnits.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
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
                                        modifier = Modifier.size(48.dp),
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                    Text(
                                        text = "هیچ انباری مطابق فیلتر یافت نشد.",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "جهت تعریف انباری جدید دکمه 'تعریف انباری' را بزنید.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    } else {
                        items(storageUnits, key = { it.storage.id }) { item ->
                            StorageUnitItemCard(
                                item = item,
                                onAssignClick = { viewModel.openAssignDialog(item) },
                                onEndAssignmentClick = { viewModel.endAssignment(item.storage.id) },
                                onHistoryClick = { viewModel.openHistoryDialog(item) }
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
        if (uiState.isCreateUnitDialogOpen) {
            CreateStorageUnitDialog(
                onDismiss = { viewModel.closeCreateUnitDialog() },
                onConfirm = { code, floor, area, loc, notes ->
                    viewModel.createStorageUnit(code, floor, area, loc, notes)
                }
            )
        }

        if (uiState.isAssignDialogOpen && uiState.selectedUnitWithDetails != null) {
            AssignStorageUnitDialog(
                storage = uiState.selectedUnitWithDetails!!,
                units = uiState.units,
                onDismiss = { viewModel.closeAssignDialog() },
                onConfirm = { unitId, type, notes ->
                    viewModel.assignStorageUnit(unitId, type, notes)
                }
            )
        }

        if (uiState.isHistoryDialogOpen && uiState.selectedUnitWithDetails != null) {
            StorageHistoryDialog(
                storage = uiState.selectedUnitWithDetails!!,
                onDismiss = { viewModel.closeHistoryDialog() }
            )
        }
    }
}

@Composable
fun StorageSummaryCard(storageUnits: List<StorageUnitWithDetails>) {
    val total = storageUnits.size
    val available = storageUnits.count { it.storage.status == SpaceStatus.AVAILABLE }
    val assigned = storageUnits.count { it.storage.status == SpaceStatus.ASSIGNED }
    val totalArea = storageUnits.sumOf { it.storage.areaSquareMeters }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "وضعیت انباری‌های ساختمان",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = "مجموع: $total باب (%.1f متر)".format(totalArea),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatBadge(
                    modifier = Modifier.weight(1f),
                    label = "آزاد",
                    count = available,
                    color = Color(0xFF2E7D32)
                )
                StatBadge(
                    modifier = Modifier.weight(1f),
                    label = "تخصیص یافته",
                    count = assigned,
                    color = Color(0xFF1565C0)
                )
            }
        }
    }
}

@Composable
fun StorageUnitItemCard(
    item: StorageUnitWithDetails,
    onAssignClick: () -> Unit,
    onEndAssignmentClick: () -> Unit,
    onHistoryClick: () -> Unit
) {
    val storage = item.storage

    val (statusBg, statusFg) = when (storage.status) {
        SpaceStatus.AVAILABLE -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
        SpaceStatus.ASSIGNED -> Color(0xFFE3F2FD) to Color(0xFF1565C0)
        SpaceStatus.TEMPORARILY_USED -> Color(0xFFFFF3E0) to Color(0xFFE65100)
        SpaceStatus.MAINTENANCE -> Color(0xFFFFF8E1) to Color(0xFFF57F17)
        SpaceStatus.BLOCKED -> Color(0xFFFFEBEE) to Color(0xFFC62828)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text(
                            text = "انباری ${storage.code}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Text(
                        text = "طبقه ${storage.floor} • ${storage.areaSquareMeters} متر مربع",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = statusBg
                ) {
                    Text(
                        text = storage.status.titleFa,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = statusFg,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            // Unit Assignment Info
            if (item.assignedUnitNumber != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "منتسب به واحد: واحد ${item.assignedUnitNumber}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            if (!storage.locationDescription.isNullOrBlank()) {
                Text(
                    text = "موقعیت: ${storage.locationDescription}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (!storage.notes.isNullOrBlank()) {
                Text(
                    text = "یادداشت: ${storage.notes}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (storage.status == SpaceStatus.AVAILABLE) {
                        Button(
                            onClick = onAssignClick,
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("تخصیص به واحد", fontSize = 12.sp)
                        }
                    } else if (storage.status == SpaceStatus.ASSIGNED) {
                        TextButton(
                            onClick = onEndAssignmentClick,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text("آزادسازی / لغو تخصیص", fontSize = 11.sp, color = MaterialTheme.colorScheme.error)
                        }
                    }
                }

                IconButton(
                    onClick = onHistoryClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.History,
                        contentDescription = "سوابق",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateStorageUnitDialog(
    onDismiss: () -> Unit,
    onConfirm: (code: String, floor: Int, area: Double, loc: String?, notes: String?) -> Unit
) {
    var code by remember { mutableStateOf("") }
    var floorText by remember { mutableStateOf("-1") }
    var areaText by remember { mutableStateOf("3.0") }
    var locationDescription by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("تعریف انباری جدید", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it },
                    label = { Text("کد / شماره انباری *") },
                    placeholder = { Text("مثال: S-1 یا S10") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = floorText,
                    onValueChange = { floorText = it },
                    label = { Text("طبقه (مثلاً -1 یا -2)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = areaText,
                    onValueChange = { areaText = it },
                    label = { Text("متراژ (متر مربع)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = locationDescription,
                    onValueChange = { locationDescription = it },
                    label = { Text("موقعیت در طبقه (اختیاری)") },
                    placeholder = { Text("مثال: انتهای راهروی شرقی") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("یادداشت (اختیاری)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (code.isNotBlank()) {
                        val floor = floorText.toIntOrNull() ?: -1
                        val area = areaText.toDoubleOrNull() ?: 0.0
                        onConfirm(code.trim(), floor, area, locationDescription.ifBlank { null }, notes.ifBlank { null })
                    }
                },
                enabled = code.isNotBlank()
            ) {
                Text("ثبت انباری")
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
fun AssignStorageUnitDialog(
    storage: StorageUnitWithDetails,
    units: List<Unit>,
    onDismiss: () -> Unit,
    onConfirm: (unitId: String, type: AssignmentType, notes: String?) -> Unit
) {
    var selectedUnitId by remember { mutableStateOf(units.firstOrNull()?.id ?: "") }
    var selectedType by remember { mutableStateOf(AssignmentType.UNIT_ASSIGNED) }
    var notes by remember { mutableStateOf("") }
    var unitExpanded by remember { mutableStateOf(false) }

    val selectedUnit = units.find { it.id == selectedUnitId }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("تخصیص انباری ${storage.storage.code} به واحد", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (units.isEmpty()) {
                    Text("هیچ واحدی در ساختمان یافت نشد.", color = MaterialTheme.colorScheme.error)
                } else {
                    ExposedDropdownMenuBox(
                        expanded = unitExpanded,
                        onExpandedChange = { unitExpanded = !unitExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedUnit?.let { "واحد ${it.unitNumber} (طبقه ${it.floor})" } ?: "انتخاب واحد...",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("انتخاب واحد *") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = unitExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = unitExpanded,
                            onDismissRequest = { unitExpanded = false }
                        ) {
                            units.forEach { unit ->
                                DropdownMenuItem(
                                    text = { Text("واحد ${unit.unitNumber} (طبقه ${unit.floor})") },
                                    onClick = {
                                        selectedUnitId = unit.id
                                        unitExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("توضیحات (اختیاری)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (selectedUnitId.isNotBlank()) {
                        onConfirm(selectedUnitId, selectedType, notes.ifBlank { null })
                    }
                },
                enabled = selectedUnitId.isNotBlank()
            ) {
                Text("ثبت تخصیص")
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
fun StorageHistoryDialog(
    storage: StorageUnitWithDetails,
    onDismiss: () -> Unit
) {
    val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("سوابق انباری ${storage.storage.code}", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (storage.assignmentHistory.isEmpty()) {
                    Text(
                        text = "هیچ سابقه تخصیص قبلی ثبت نشده است.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.height(260.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(storage.assignmentHistory) { assignment ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = assignment.assignmentType.titleFa,
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                        Text(
                                            text = if (assignment.isActive) "فعال" else "خاتمه‌یافته",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (assignment.isActive) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Text(
                                        text = "از تاریخ: ${dateFormat.format(Date(assignment.startDate))}" +
                                                (assignment.endDate?.let { " تا ${dateFormat.format(Date(it))}" } ?: " (ادامه‌دار)"),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    if (!assignment.notes.isNullOrBlank()) {
                                        Text(
                                            text = "توضیحات: ${assignment.notes}",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("بستن")
            }
        }
    )
}
