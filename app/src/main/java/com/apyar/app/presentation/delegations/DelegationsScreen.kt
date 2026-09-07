package com.apyar.app.presentation.delegations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.apyar.app.domain.model.DelegationStatus
import com.apyar.app.domain.model.Permission

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DelegationsScreen(
    viewModel: DelegationsViewModel,
    onNavigateBack: () -> Unit,
    currentUserId: String = "SYSTEM",
    modifier: Modifier = Modifier
) {
    val delegations by viewModel.delegations.collectAsState()
    val members by viewModel.members.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("تفویض اختیارات و مجوزها") },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "بازگشت"
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = { showCreateDialog = true }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "تفویض اختیار جدید")
                }
            },
            modifier = modifier
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (delegations.isEmpty()) {
                    Text(
                        text = "هیچ تفویض اختیاری در این ساختمان ثبت نشده است.",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(delegations) { item ->
                            val isValid = item.delegation.isCurrentlyValid()

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isValid) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = item.delegation.permission.titleFa,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.primary
                                        )

                                        Surface(
                                            color = when {
                                                item.delegation.status == DelegationStatus.REVOKED -> MaterialTheme.colorScheme.errorContainer
                                                !item.delegation.isCurrentlyValid() -> MaterialTheme.colorScheme.outlineVariant
                                                else -> MaterialTheme.colorScheme.secondaryContainer
                                            },
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = when {
                                                    item.delegation.status == DelegationStatus.REVOKED -> "لغو شده"
                                                    !item.delegation.isCurrentlyValid() -> "منقضی شده"
                                                    else -> "فعال"
                                                },
                                                style = MaterialTheme.typography.labelSmall,
                                                color = when {
                                                    item.delegation.status == DelegationStatus.REVOKED -> MaterialTheme.colorScheme.onErrorContainer
                                                    !item.delegation.isCurrentlyValid() -> MaterialTheme.colorScheme.onSurfaceVariant
                                                    else -> MaterialTheme.colorScheme.onSecondaryContainer
                                                },
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(end = 6.dp)
                                        )
                                        Text(
                                            text = "به: ${item.grantedToPerson?.fullName ?: item.delegation.grantedToUserId}",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.DateRange,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(end = 6.dp)
                                        )
                                        Text(
                                            text = "مهلت اعتبار: ${item.delegation.daysRemaining()} روز باقی‌مانده",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    if (item.delegation.isCurrentlyValid()) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.End
                                        ) {
                                            TextButton(
                                                onClick = {
                                                    viewModel.revokeDelegation(
                                                        actorUserId = currentUserId,
                                                        delegationId = item.delegation.id
                                                    )
                                                }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Cancel,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.error,
                                                    modifier = Modifier.padding(end = 4.dp)
                                                )
                                                Text(
                                                    "لغو تفویض اختیار",
                                                    color = MaterialTheme.colorScheme.error
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Create Delegation Dialog
            if (showCreateDialog) {
                var selectedGranteeUserId by remember { mutableStateOf("") }
                var selectedPermission by remember { mutableStateOf(Permission.VIEW_BUILDING) }
                var durationDays by remember { mutableIntStateOf(30) }
                var isMemberDropdownExpanded by remember { mutableStateOf(false) }
                var isPermissionDropdownExpanded by remember { mutableStateOf(false) }

                AlertDialog(
                    onDismissRequest = { showCreateDialog = false },
                    title = { Text("تفویض اختیار به کاربر") },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            // Grantee Selection
                            ExposedDropdownMenuBox(
                                expanded = isMemberDropdownExpanded,
                                onExpandedChange = { isMemberDropdownExpanded = it }
                            ) {
                                val selectedMember = members.find { it.member.userId == selectedGranteeUserId }
                                OutlinedTextField(
                                    value = selectedMember?.person?.fullName ?: selectedGranteeUserId.ifEmpty { "انتخاب دریافت‌کننده" },
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("دریافت‌کننده تفویض") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isMemberDropdownExpanded) },
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth()
                                )
                                ExposedDropdownMenu(
                                    expanded = isMemberDropdownExpanded,
                                    onDismissRequest = { isMemberDropdownExpanded = false }
                                ) {
                                    members.forEach { m ->
                                        DropdownMenuItem(
                                            text = {
                                                Text("${m.person?.fullName ?: "کاربر"} (${m.member.role.titleFa})")
                                            },
                                            onClick = {
                                                selectedGranteeUserId = m.member.userId
                                                isMemberDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            // Permission Selection
                            ExposedDropdownMenuBox(
                                expanded = isPermissionDropdownExpanded,
                                onExpandedChange = { isPermissionDropdownExpanded = it }
                            ) {
                                OutlinedTextField(
                                    value = selectedPermission.titleFa,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("مجوز تفویض‌شونده") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isPermissionDropdownExpanded) },
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth()
                                )
                                ExposedDropdownMenu(
                                    expanded = isPermissionDropdownExpanded,
                                    onDismissRequest = { isPermissionDropdownExpanded = false }
                                ) {
                                    Permission.entries.forEach { perm ->
                                        DropdownMenuItem(
                                            text = {
                                                Column {
                                                    Text(perm.titleFa, style = MaterialTheme.typography.bodyMedium)
                                                    Text(
                                                        "دسته: ${perm.categoryFa}",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            },
                                            onClick = {
                                                selectedPermission = perm
                                                isPermissionDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = durationDays.toString(),
                                onValueChange = {
                                    durationDays = it.toIntOrNull() ?: 30
                                },
                                label = { Text("مدت زمان اعتبار (روز)") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (selectedGranteeUserId.isNotBlank()) {
                                    viewModel.createDelegation(
                                        actorUserId = currentUserId,
                                        grantedToUserId = selectedGranteeUserId,
                                        permission = selectedPermission,
                                        durationDays = durationDays
                                    )
                                    showCreateDialog = false
                                }
                            }
                        ) {
                            Text("ثبت تفویض")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showCreateDialog = false }) {
                            Text("انصراف")
                        }
                    }
                )
            }
        }
    }
}
