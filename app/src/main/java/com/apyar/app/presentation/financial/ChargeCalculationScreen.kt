package com.apyar.app.presentation.financial

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.apyar.app.domain.engine.UnitInclusionSpec
import com.apyar.app.domain.model.CalculationPreview
import com.apyar.app.domain.model.ChargeRule
import com.apyar.app.domain.model.InclusionStatus
import com.apyar.app.domain.model.OccupancyStatus
import com.apyar.app.domain.model.UnitChargeWithDetails

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChargeCalculationScreen(
    viewModel: ChargeCalculationViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToDashboard: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "موتور محاسبه و تسهیم شارژ",
                            fontWeight = FontWeight.Bold
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
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Step Indicator Banner
                    item {
                        StepIndicatorBanner(step = uiState.currentStep)
                    }

                    if (uiState.error != null) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                            ) {
                                Text(
                                    text = "خطا: ${uiState.error}",
                                    color = Color(0xFFC62828),
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }

                    if (uiState.successMessage != null) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                            ) {
                                Text(
                                    text = uiState.successMessage ?: "",
                                    color = Color(0xFF2E7D32),
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }

                    when (uiState.currentStep) {
                        1 -> {
                            // Step 1: Configuration & Selection
                            item {
                                PeriodConfigSection(
                                    title = uiState.title,
                                    onTitleChange = { viewModel.setTitle(it) },
                                    totalCost = uiState.totalCostInput,
                                    onTotalCostChange = { viewModel.setTotalCost(it) }
                                )
                            }

                            item {
                                RuleSelectionSection(
                                    rules = uiState.availableRules,
                                    selectedRule = uiState.selectedRule,
                                    onSelectRule = { viewModel.selectRule(it) }
                                )
                            }

                            item {
                                Text(
                                    text = "مدیریت شمول واحدها در دوره (${uiState.unitSpecs.count { it.inclusionStatus == InclusionStatus.INCLUDED }} از ${uiState.unitSpecs.size} واحد)",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            items(uiState.unitSpecs, key = { it.unit.id }) { spec ->
                                UnitInclusionItemCard(
                                    spec = spec,
                                    onToggle = { viewModel.toggleUnitInclusion(spec.unit.id) }
                                )
                            }

                            item {
                                Button(
                                    onClick = { viewModel.executeCalculation() },
                                    enabled = !uiState.isCalculating && uiState.selectedRule != null,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    if (uiState.isCalculating) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            color = MaterialTheme.colorScheme.onPrimary
                                        )
                                    } else {
                                        Text("⚡ اجرای محاسبه و نمایش پیش‌نمایش", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        2 -> {
                            // Step 2: Inspection & Finalization
                            uiState.calculationPreview?.let { preview ->
                                item {
                                    CalculationSummaryCard(preview)
                                }

                                item {
                                    Text(
                                        text = "ریز محاسبات تسهیم به ازای هر واحد",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                items(preview.unitCharges, key = { it.unitCharge.unitId }) { itemDetail ->
                                    UnitCalculationDetailCard(itemDetail)
                                }

                                item {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = { viewModel.resetWizard() },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Text("ویرایش و محاسبه مجدد")
                                        }

                                        Button(
                                            onClick = { viewModel.finalizePeriod() },
                                            enabled = !uiState.isFinalizing,
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(12.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                                        ) {
                                            if (uiState.isFinalizing) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size(20.dp),
                                                    color = Color.White
                                                )
                                            } else {
                                                Text("✓ نهایی‌سازی و ثبت در دفتر کل")
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        3 -> {
                            // Step 3: Finalized State
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(20.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Text(
                                            text = "🎉 دوره شارژ با موفقیت نهایی و قطعی شد",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1B5E20)
                                        )

                                        Text(
                                            text = "تمام صورت‌حساب‌های واحدها صادر و بدهی آن‌ها در دفتر مالی ساختمان اعمال گردید.",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color(0xFF2E7D32)
                                        )

                                        Button(
                                            onClick = onNavigateToDashboard,
                                            shape = RoundedCornerShape(12.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                                        ) {
                                            Text("مشاهده داشبورد مالی و صندوق")
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun StepIndicatorBanner(step: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StepDot(stepNumber = 1, title = "۱. تنظیم و فرمول", isActive = step >= 1, isCurrent = step == 1)
        Text("←", color = MaterialTheme.colorScheme.onSurfaceVariant)
        StepDot(stepNumber = 2, title = "۲. پیش‌نمایش و موازنه", isActive = step >= 2, isCurrent = step == 2)
        Text("←", color = MaterialTheme.colorScheme.onSurfaceVariant)
        StepDot(stepNumber = 3, title = "۳. ثبت نهایی", isActive = step >= 3, isCurrent = step == 3)
    }
}

@Composable
fun StepDot(stepNumber: Int, title: String, isActive: Boolean, isCurrent: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
            color = if (isCurrent) MaterialTheme.colorScheme.primary else if (isActive) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
    }
}

@Composable
fun PeriodConfigSection(
    title: String,
    onTitleChange: (String) -> Unit,
    totalCost: String,
    onTotalCostChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "اطلاعات دوره شارژ",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                label = { Text("عنوان دوره") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = totalCost,
                onValueChange = onTotalCostChange,
                label = { Text("کل هزینه قابل تسهیم (تومان)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun RuleSelectionSection(
    rules: List<ChargeRule>,
    selectedRule: ChargeRule?,
    onSelectRule: (ChargeRule) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "انتخاب روش و فرمول محاسبه",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            rules.forEach { rule ->
                val isSelected = selectedRule?.id == rule.id
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else Color.Transparent)
                        .clickable { onSelectRule(rule) }
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = { onSelectRule(rule) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = rule.name,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                        Text(
                            text = rule.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UnitInclusionItemCard(
    spec: UnitInclusionSpec,
    onToggle: () -> Unit
) {
    val isIncluded = spec.inclusionStatus == InclusionStatus.INCLUDED
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isIncluded) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Checkbox(
                    checked = isIncluded,
                    onCheckedChange = { onToggle() }
                )

                Column {
                    Text(
                        text = "واحد ${spec.unit.unitNumber}",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "${spec.unit.areaSquareMeters} متر • ${spec.unit.residentCount} نفر",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Occupancy Badge
            val isVacant = spec.unit.occupancyStatus == OccupancyStatus.VACANT
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isVacant) Color(0xFFFFF3E0) else Color(0xFFE8F5E9))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (isVacant) "خالی از سکنه" else "دارای سکنه",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isVacant) Color(0xFFE65100) else Color(0xFF2E7D32),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun CalculationSummaryCard(preview: CalculationPreview) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "موازنه و بازبینی محاسبات",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("مبلغ کل هدف:")
                Text("${formatMoney(preview.totalCost)} تومان", fontWeight = FontWeight.Bold)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("مجموع سهم محاسبه‌شده واحدها:")
                Text("${formatMoney(preview.calculatedTotal)} تومان", fontWeight = FontWeight.Bold)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("تفاضل گردکردن (Rounding):")
                Text("${preview.roundingDifference} تومان", color = MaterialTheme.colorScheme.primary)
            }

            Text(
                text = "روش گردکردن: الگوریتم همیلتون/نی‌مایر (Hamilton Integer Distribution) با حفظ برابری ۱۰۰٪ مجموع مبالغ.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun UnitCalculationDetailCard(item: UnitChargeWithDetails) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "واحد ${item.unit.unitNumber}",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "${item.unit.areaSquareMeters} متر • ${item.unit.residentCount} نفر • وضعیت: ${item.unitCharge.inclusionStatus.name}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (item.unitCharge.calculationNotes != null) {
                    Text(
                        text = item.unitCharge.calculationNotes,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${formatMoney(item.unitCharge.finalAmount)} ت",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "سهم قطعی",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
