package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.BudgetItemEntity
import com.example.ui.components.CalmingBannerCard
import com.example.ui.components.NivaTopAppBar
import com.example.ui.theme.*
import com.example.ui.viewmodel.NivaViewModel

@Composable
fun PlanMoneyScreen(
    viewModel: NivaViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val budgetItems by viewModel.allBudgetItems.collectAsStateWithLifecycle()
    val currency by viewModel.selectedCurrency.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }
    var newCategoryPlanned by remember { mutableStateOf("") }

    val totalPlanned = budgetItems.sumOf { it.plannedAmount }
    val totalActual = budgetItems.sumOf { it.actualAmount }
    val remaining = totalPlanned - totalActual

    Scaffold(
        topBar = {
            NivaTopAppBar(
                title = "Plan My Money",
                subtitle = "Peaceful household expense budgeting",
                showBack = true,
                onBackClick = onBackClick,
                actions = {
                    IconButton(
                        onClick = { showAddDialog = true },
                        modifier = Modifier.testTag("budget_add_button")
                    ) {
                        Icon(Icons.Default.AddCircleOutline, contentDescription = "Add Budget Category")
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding() + 8.dp,
                bottom = innerPadding.calculateBottomPadding() + 24.dp,
                start = 16.dp,
                end = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Overview Summary Card
            item {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, DividerWarm, RoundedCornerShape(22.dp))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Monthly Household Allocation",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text(text = "Total Planned", style = MaterialTheme.typography.labelSmall, color = CharcoalMuted)
                                Text(
                                    text = "$currency${String.format("%,.0f", totalPlanned)}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = SageGreenPrimary
                                )
                            }
                            Column {
                                Text(text = "Spent So Far", style = MaterialTheme.typography.labelSmall, color = CharcoalMuted)
                                Text(
                                    text = "$currency${String.format("%,.0f", totalActual)}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = CharcoalDark
                                )
                            }
                            Column {
                                Text(text = "Remaining Buffer", style = MaterialTheme.typography.labelSmall, color = CharcoalMuted)
                                Text(
                                    text = "$currency${String.format("%,.0f", remaining)}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = if (remaining >= 0) SageGreenPrimary else TerracottaAccent
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Progress Bar
                        val progress = if (totalPlanned > 0) (totalActual / totalPlanned).toFloat().coerceIn(0f, 1f) else 0f
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = if (progress > 0.9f) TerracottaAccent else SageGreenPrimary,
                            trackColor = SageContainer
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Category Allocations",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            // Budget items list
            items(budgetItems) { item ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, DividerWarm, RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = item.categoryName,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            IconButton(
                                onClick = { viewModel.deleteBudgetItem(item) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "Delete",
                                    tint = CharcoalMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Planned: $currency${String.format("%,.0f", item.plannedAmount)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = CharcoalMedium
                            )
                            Text(
                                text = "Spent: $currency${String.format("%,.0f", item.actualAmount)}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = CharcoalDark
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        val itemProgress = if (item.plannedAmount > 0) (item.actualAmount / item.plannedAmount).toFloat().coerceIn(0f, 1f) else 0f
                        LinearProgressIndicator(
                            progress = { itemProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (itemProgress >= 1f) TerracottaAccent else SageGreenPrimary,
                            trackColor = SageContainer
                        )
                    }
                }
            }

            item {
                CalmingBannerCard(
                    message = "“Clear household numbers give you daily peace of mind.”"
                )
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Budget Category") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newCategoryName,
                        onValueChange = { newCategoryName = it },
                        label = { Text("Category Name") },
                        placeholder = { Text("e.g. Milk & Daily Dairy, Petrol...") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newCategoryPlanned,
                        onValueChange = { newCategoryPlanned = it },
                        label = { Text("Planned Amount ($currency)") },
                        placeholder = { Text("e.g. 3500") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = newCategoryPlanned.toDoubleOrNull() ?: 0.0
                        if (newCategoryName.isNotBlank() && amount > 0) {
                            viewModel.addBudgetItem(newCategoryName, amount)
                            newCategoryName = ""
                            newCategoryPlanned = ""
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SageGreenPrimary)
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
