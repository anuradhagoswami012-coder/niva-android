package com.example.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.PlanCategory
import com.example.data.model.PlanEntity
import com.example.ui.components.CalmingBannerCard
import com.example.ui.components.NivaTopAppBar
import com.example.ui.theme.*
import com.example.ui.viewmodel.NivaViewModel

@Composable
fun SavedPlansScreen(
    viewModel: NivaViewModel,
    onBackClick: () -> Unit,
    onOpenPlan: () -> Unit,
    modifier: Modifier = Modifier
) {
    val plans by viewModel.allPlans.collectAsStateWithLifecycle()
    var selectedCategoryFilter by remember { mutableStateOf<PlanCategory?>(null) }

    val filteredPlans = remember(plans, selectedCategoryFilter) {
        if (selectedCategoryFilter == null) plans
        else plans.filter { it.category == selectedCategoryFilter }
    }

    Scaffold(
        topBar = {
            NivaTopAppBar(
                title = "Saved Plans",
                subtitle = "Your organized archive",
                showBack = true,
                onBackClick = onBackClick
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
            // Category filter chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChip(
                            selected = selectedCategoryFilter == null,
                            onClick = { selectedCategoryFilter = null },
                            label = { Text("All Plans (${plans.size})") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SageContainer,
                                selectedLabelColor = OnSageContainer
                            )
                        )
                    }

                    items(PlanCategory.values()) { category ->
                        val count = plans.count { it.category == category }
                        if (count > 0) {
                            FilterChip(
                                selected = selectedCategoryFilter == category,
                                onClick = { selectedCategoryFilter = category },
                                label = { Text("${category.iconEmoji} ${category.displayName} ($count)") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SageContainer,
                                    selectedLabelColor = OnSageContainer
                                )
                            )
                        }
                    }
                }
            }

            if (filteredPlans.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Text(text = "✨", fontSize = 36.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "No saved plans yet in this category",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Generate any plan with NIVA and tap 'Save Plan'.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = CharcoalMuted
                            )
                        }
                    }
                }
            } else {
                items(filteredPlans) { plan ->
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.handleUserProblem(plan.originalPrompt, plan.category)
                                onOpenPlan()
                            }
                            .border(1.dp, DividerWarm, RoundedCornerShape(18.dp))
                            .testTag("saved_plan_${plan.id}")
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = plan.category.iconEmoji, fontSize = 18.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = plan.category.displayName,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = SageGreenPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (plan.estimatedBudget.isNotBlank()) {
                                        Surface(
                                            color = MaterialTheme.colorScheme.surfaceVariant,
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = plan.estimatedBudget,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = CharcoalDark,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                    }

                                    IconButton(
                                        onClick = { viewModel.deletePlan(plan.id) },
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
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = plan.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = plan.summary,
                                style = MaterialTheme.typography.bodySmall,
                                color = CharcoalMedium,
                                maxLines = 2
                            )
                        }
                    }
                }
            }

            item {
                CalmingBannerCard(
                    message = "“All your recipes, hosting plans, and household checklists stay safely organized here.”"
                )
            }
        }
    }
}
