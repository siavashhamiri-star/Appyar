package com.apyar.app.presentation.members

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.apyar.app.domain.model.BuildingMemberWithDetails
import com.apyar.app.domain.model.Role

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuildingMembersScreen(
    viewModel: BuildingMembersViewModel,
    onNavigateBack: () -> Unit,
    onViewPermissionsClick: (String) -> Unit,
    onViewUserProfileClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val members by viewModel.members.collectAsState()
    val allUserAccounts by viewModel.allUserAccounts.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var showEditRoleDialog by remember { mutableStateOf(false) }
    var selectedMemberForEdit by remember { mutableStateOf<BuildingMemberWithDetails?>(null) }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("اعضای ساختمان و نقش‌ها") },
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
                FloatingActionButton(onClick = { showAddDialog = true }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "افزودن عضو جدید")
                }
            },
            modifier = modifier
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (members.isEmpty()) {
                    Text(
                        text = "هیچ عضوی در این ساختمان ثبت نشده است.",
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
                        items(members) { item ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
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
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.clickable {
                                                onViewUserProfileClick(item.member.userId)
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Person,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text(
                                                    text = item.person?.fullName ?: "کاربر",
                                                    style = MaterialTheme.typography.titleSmall
                                                )
                                                item.userAccount?.let {
                                                    Text(
                                                        text = it.mobileNumber,
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }
                                        }

                                        Surface(
                                            color = MaterialTheme.colorScheme.secondaryContainer,
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = item.member.role.titleFa,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        TextButton(
                                            onClick = {
                                                onViewPermissionsClick(item.member.userId)
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Security,
                                                contentDescription = null,
                                                modifier = Modifier.padding(end = 4.dp)
                                            )
                                            Text("مشاهده مجوزها")
                                        }

                                        IconButton(
                                            onClick = {
                                                selectedMemberForEdit = item
                                                showEditRoleDialog = true
                                            }
                                        ) {
                                            Icon(imageVector = Icons.Default.Edit, contentDescription = "ویرایش نقش")
                                        }

                                        IconButton(
                                            onClick = {
                                                viewModel.removeMember(item.member.id)
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "حذف عضو",
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Dialog for adding a new member
            if (showAddDialog) {
                var selectedUserId by remember { mutableStateOf("") }
                var selectedRole by remember { mutableStateOf(Role.RESIDENT) }
                var isRoleDropdownExpanded by remember { mutableStateOf(false) }

                AlertDialog(
                    onDismissRequest = { showAddDialog = false },
                    title = { Text("افزودن عضو جدید به ساختمان") },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = selectedUserId,
                                onValueChange = { selectedUserId = it },
                                label = { Text("شناسه کاربری (User ID) یا شماره همراه") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Role Selection
                            ExposedDropdownMenuBox(
                                expanded = isRoleDropdownExpanded,
                                onExpandedChange = { isRoleDropdownExpanded = it }
                            ) {
                                OutlinedTextField(
                                    value = selectedRole.titleFa,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("نقش در این ساختمان") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isRoleDropdownExpanded) },
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth()
                                )
                                ExposedDropdownMenu(
                                    expanded = isRoleDropdownExpanded,
                                    onDismissRequest = { isRoleDropdownExpanded = false }
                                ) {
                                    Role.entries.forEach { role ->
                                        DropdownMenuItem(
                                            text = {
                                                Column {
                                                    Text(role.titleFa, style = MaterialTheme.typography.bodyMedium)
                                                    Text(
                                                        role.descriptionFa,
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            },
                                            onClick = {
                                                selectedRole = role
                                                isRoleDropdownExpanded = false
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
                                if (selectedUserId.isNotBlank()) {
                                    viewModel.addMember(
                                        targetUserId = selectedUserId.trim(),
                                        role = selectedRole
                                    )
                                    showAddDialog = false
                                }
                            }
                        ) {
                            Text("افزودن")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showAddDialog = false }) {
                            Text("انصراف")
                        }
                    }
                )
            }

            // Dialog for editing member role
            if (showEditRoleDialog && selectedMemberForEdit != null) {
                var selectedRole by remember { mutableStateOf(selectedMemberForEdit!!.member.role) }
                var isRoleDropdownExpanded by remember { mutableStateOf(false) }

                AlertDialog(
                    onDismissRequest = { showEditRoleDialog = false },
                    title = { Text("تغییر نقش عضو در ساختمان") },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                text = "عضو: ${selectedMemberForEdit?.person?.fullName ?: selectedMemberForEdit?.member?.userId}",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            ExposedDropdownMenuBox(
                                expanded = isRoleDropdownExpanded,
                                onExpandedChange = { isRoleDropdownExpanded = it }
                            ) {
                                OutlinedTextField(
                                    value = selectedRole.titleFa,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("نقش جدید") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isRoleDropdownExpanded) },
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth()
                                )
                                ExposedDropdownMenu(
                                    expanded = isRoleDropdownExpanded,
                                    onDismissRequest = { isRoleDropdownExpanded = false }
                                ) {
                                    Role.entries.forEach { role ->
                                        DropdownMenuItem(
                                            text = {
                                                Column {
                                                    Text(role.titleFa, style = MaterialTheme.typography.bodyMedium)
                                                    Text(
                                                        role.descriptionFa,
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            },
                                            onClick = {
                                                selectedRole = role
                                                isRoleDropdownExpanded = false
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
                                viewModel.updateRole(
                                    memberId = selectedMemberForEdit!!.member.id,
                                    newRole = selectedRole
                                )
                                showEditRoleDialog = false
                            }
                        ) {
                            Text("ذخیره تغییرات")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showEditRoleDialog = false }) {
                            Text("انصراف")
                        }
                    }
                )
            }
        }
    }
}
