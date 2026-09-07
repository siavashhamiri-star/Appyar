package com.apyar.app.presentation.parking

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
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
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.runtime.mutableIntStateOf
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
import com.apyar.app.domain.model.AssignmentType
import com.apyar.app.domain.model.ParkingSpaceWithDetails
import com.apyar.app.domain.model.ParkingType
import com.apyar.app.domain.model.SpaceStatus
import com.apyar.app.domain.model.Unit
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParkingScreen(
    viewModel: ParkingViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val parkingSpaces by viewModel.parkingSpaces.collectAsState()
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
                            text = "مدیریت پارکینگ‌ها",
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
                    onClick = { viewModel.openCreateSpaceDialog() },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "تعریف پارکینگ",
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
                        ParkingSummaryCard(parkingSpaces = parkingSpaces)
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
                                label = { Text("همه (${parkingSpaces.size})") }
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

                    if (parkingSpaces.isEmpty()) {
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
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "هیچ پارکینگی مطابق فیلتر یافت نشد.",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "جهت تعریف پارکینگ جدید، دکمه 'تعریف پارکینگ' را بزنید.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    } else {
                        items(parkingSpaces, key = { it.space.id }) { spaceWithDetails ->
                            ParkingSpaceItemCard(
                                item = spaceWithDetails,
                                onAssignClick = { viewModel.openAssignDialog(spaceWithDetails) },
                                onTempUseClick = { viewModel.openTempUseDialog(spaceWithDetails) },
                                onEndAssignmentClick = { viewModel.endAssignment(spaceWithDetails.space.id) },
                                onHistoryClick = { viewModel.openHistoryDialog(spaceWithDetails) }
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
        if (uiState.isCreateSpaceDialogOpen) {
            CreateParkingSpaceDialog(
                onDismiss = { viewModel.closeCreateSpaceDialog() },
                onConfirm = { code, floor, loc, type, notes ->
                    viewModel.createParkingSpace(code, floor, loc, type, notes)
                }
            )
        }

        if (uiState.isAssignDialogOpen && uiState.selectedSpaceWithDetails != null) {
            AssignParkingSpaceDialog(
                space = uiState.selectedSpaceWithDetails!!,
                units = uiState.units,
                onDismiss = { viewModel.closeAssignDialog() },
                onConfirm = { unitId, type, notes ->
                    viewModel.assignParkingSpace(unitId, type, notes)
                }
            )
        }

        if (uiState.isTempUseDialogOpen && uiState.selectedSpaceWithDetails != null) {
            TemporaryUseParkingDialog(
                space = uiState.selectedSpaceWithDetails!!,
                units = uiState.units,
                onDismiss = { viewModel.closeTempUseDialog() },
                onConfirm = { targetUnitId, desc ->
                    viewModel.createTemporaryUse(targetUnitId, desc)
                }
            )
        }

        if (uiState.isHistoryDialogOpen && uiState.selectedSpaceWithDetails != null) {
            ParkingHistoryDialog(
                space = uiState.selectedSpaceWithDetails!!,
                onDismiss = { viewModel.closeHistoryDialog() }
            )
        }
    }
}

@Composable
fun ParkingSummaryCard(parkingSpaces: List<ParkingSpaceWithDetails>) {
    val total = parkingSpaces.size
    val available = parkingSpaces.count { it.space.status == SpaceStatus.AVAILABLE }
    val assigned = parkingSpaces.count { it.space.status == SpaceStatus.ASSIGNED }
    val tempUsed = parkingSpaces.count { it.space.status == SpaceStatus.TEMPORARILY_USED }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
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
                    text = "وضعیت فضای پارکینگ ساختمان",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "مجموع: $total واحد",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
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
                    label = "تخصیص واحد",
                    count = assigned,
                    color = Color(0xFF1565C0)
                )
                StatBadge(
                    modifier = Modifier.weight(1f),
                    label = "استفاده موقت",
                    count = tempUsed,
                    color = Color(0xFFE65100)
                )
            }
        }
    }
}

@Composable
fun StatBadge(
    modifier: Modifier = Modifier,
    label: String,
    count: Int,
    color: Color
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ParkingSpaceItemCard(
    item: ParkingSpaceWithDetails,
    onAssignClick: () -> Unit,
    onTempUseClick: () -> Unit,
    onEndAssignmentClick: () -> Unit,
    onHistoryClick: () -> Unit
) {
    val space = item.space

    val (statusBg, statusFg) = when (space.status) {
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
            // Header Row: Code, Floor, Status Chip
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
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = space.code,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Text(
                        text = "طبقه ${space.floor} • ${space.parkingType.titleFa}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = statusBg
                ) {
                    Text(
                        text = space.status.titleFa,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = statusFg,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            // Unit Assignment / Usage Info
            if (item.assignedUnitNumber != null || item.temporaryUserUnitNumber != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (item.assignedUnitNumber != null) {
                        Text(
                            text = "واحد منتسب: واحد ${item.assignedUnitNumber}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    if (item.temporaryUserUnitNumber != null) {
                        Text(
                            text = "استفاده موقت: واحد ${item.temporaryUserUnitNumber}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE65100)
                        )
                    }
                }
            }

            if (!space.locationDescription.isNullOrBlank()) {
                Text(
                    text = "موقعیت: ${space.locationDescription}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (!space.notes.isNullOrBlank()) {
                Text(
                    text = "یادداشت: ${space.notes}",
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
                    when (space.status) {
                        SpaceStatus.AVAILABLE -> {
                            Button(
                                onClick = onAssignClick,
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("تخصیص به واحد", fontSize = 12.sp)
                            }
                            OutlinedButton(
                                onClick = onTempUseClick,
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("استفاده موقت", fontSize = 12.sp)
                            }
                        }
                        SpaceStatus.ASSIGNED -> {
                            OutlinedButton(
                                onClick = onTempUseClick,
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("ثبت واگذاری موقت", fontSize = 11.sp)
                            }
                            TextButton(
                                onClick = onEndAssignmentClick,
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Text("آزادسازی", fontSize = 11.sp, color = MaterialTheme.colorScheme.error)
                            }
                        }
                        SpaceStatus.TEMPORARILY_USED -> {
                            Button(
                                onClick = onEndAssignmentClick,
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("خاتمه استفاده موقت", fontSize = 11.sp)
                            }
                        }
                        else -> {
                            OutlinedButton(
                                onClick = onAssignClick,
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("تغییر وضعیت", fontSize = 11.sp)
                            }
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
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateParkingSpaceDialog(
    onDismiss: () -> Unit,
    onConfirm: (code: String, floor: Int, loc: String?, type: ParkingType, notes: String?) -> Unit
) {
    var code by remember { mutableStateOf("") }
    var floorText by remember { mutableStateOf("0") }
    var locationDescription by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(ParkingType.NORMAL) }
    var notes by remember { mutableStateOf("") }
    var typeExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("تعریف فضای پارکینگ جدید", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it },
                    label = { Text("کد / شماره پارکینگ *") },
                    placeholder = { Text("مثال: P-101 یا 12") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = floorText,
                    onValueChange = { floorText = it },
                    label = { Text("طبقه (مثلاً 0 یا -1)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(
                    expanded = typeExpanded,
                    onExpandedChange = { typeExpanded = !typeExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedType.titleFa,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("نوع پارکینگ") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = typeExpanded,
                        onDismissRequest = { typeExpanded = false }
                    ) {
                        ParkingType.entries.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.titleFa) },
                                onClick = {
                                    selectedType = type
                                    typeExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = locationDescription,
                    onValueChange = { locationDescription = it },
                    label = { Text("موقعیت در پارکینگ (اختیاری)") },
                    placeholder = { Text("مثال: سمت راست آسانسور جنوبی") },
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
                        val floor = floorText.toIntOrNull() ?: 0
                        onConfirm(code.trim(), floor, locationDescription.ifBlank { null }, selectedType, notes.ifBlank { null })
                    }
                },
                enabled = code.isNotBlank()
            ) {
                Text("ثبت پارکینگ")
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
fun AssignParkingSpaceDialog(
    space: ParkingSpaceWithDetails,
    units: List<Unit>,
    onDismiss: () -> Unit,
    onConfirm: (unitId: String, type: AssignmentType, notes: String?) -> Unit
) {
    var selectedUnitId by remember { mutableStateOf(units.firstOrNull()?.id ?: "") }
    var selectedType by remember { mutableStateOf(AssignmentType.UNIT_ASSIGNED) }
    var notes by remember { mutableStateOf("") }
    var unitExpanded by remember { mutableStateOf(false) }
    var typeExpanded by remember { mutableStateOf(false) }

    val selectedUnit = units.find { it.id == selectedUnitId }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("تخصیص پارکینگ ${space.space.code} به واحد", fontWeight = FontWeight.Bold) },
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
                                    text = { Text("واحد ${unit.unitNumber} (طبقه ${unit.floor} - متراژ ${unit.areaSquareMeters})") },
                                    onClick = {
                                        selectedUnitId = unit.id
                                        unitExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    ExposedDropdownMenuBox(
                        expanded = typeExpanded,
                        onExpandedChange = { typeExpanded = !typeExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedType.titleFa,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("نوع تخصیص") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = typeExpanded,
                            onDismissRequest = { typeExpanded = false }
                        ) {
                            AssignmentType.entries.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type.titleFa) },
                                    onClick = {
                                        selectedType = type
                                        typeExpanded = false
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemporaryUseParkingDialog(
    space: ParkingSpaceWithDetails,
    units: List<Unit>,
    onDismiss: () -> Unit,
    onConfirm: (targetUnitId: String, description: String?) -> Unit
) {
    var selectedUnitId by remember { mutableStateOf(units.firstOrNull()?.id ?: "") }
    var description by remember { mutableStateOf("") }
    var unitExpanded by remember { mutableStateOf(false) }

    val selectedUnit = units.find { it.id == selectedUnitId }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("ثبت استفاده موقت پارکینگ ${space.space.code}", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "در صورتی که واحد دیگری مجاز به استفاده موقت از این پارکینگ شده است، آن را ثبت نمایید:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                ExposedDropdownMenuBox(
                    expanded = unitExpanded,
                    onExpandedChange = { unitExpanded = !unitExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedUnit?.let { "واحد ${it.unitNumber} (طبقه ${it.floor})" } ?: "انتخاب واحد...",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("واحد استفاده‌کننده موقت *") },
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
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("توضیحات و توافق (اختیاری)") },
                    placeholder = { Text("مثال: استفاده مهمان تا پایان ماه") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (selectedUnitId.isNotBlank()) {
                        onConfirm(selectedUnitId, description.ifBlank { null })
                    }
                },
                enabled = selectedUnitId.isNotBlank()
            ) {
                Text("ثبت استفاده موقت")
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
fun ParkingHistoryDialog(
    space: ParkingSpaceWithDetails,
    onDismiss: () -> Unit
) {
    val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("سوابق پارکینگ ${space.space.code}", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (space.assignmentHistory.isEmpty()) {
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
                        items(space.assignmentHistory) { assignment ->
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
