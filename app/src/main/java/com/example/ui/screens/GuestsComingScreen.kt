package com.example.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PlanCategory
import com.example.ui.components.NivaTopAppBar
import com.example.ui.theme.*
import com.example.ui.viewmodel.NivaViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GuestsComingScreen(
    viewModel: NivaViewModel,
    onBackClick: () -> Unit,
    onPlanGenerated: () -> Unit,
    modifier: Modifier = Modifier
) {
    var guestCount by remember { mutableIntStateOf(10) }
    var selectedMeal by remember { mutableStateOf("Dinner") }
    var selectedDiet by remember { mutableStateOf("Vegetarian") }
    var selectedBudget by remember { mutableStateOf("₹3,000") }
    var selectedPrepTime by remember { mutableStateOf("3-4 Hours") }
    var specialNotes by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            NivaTopAppBar(
                title = "Guests Are Coming",
                subtitle = "Complete hosting checklist without stress",
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, DividerWarm, RoundedCornerShape(20.dp))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "👥 Hosting Details",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Tell NIVA a few essentials, and get a complete menu, exact ingredient quantities, prep timeline, and serving checklists.",
                            style = MaterialTheme.typography.bodySmall,
                            color = CharcoalMedium,
                            modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                        )

                        // Guest count selector
                        Text(
                            text = "Number of Guests: $guestCount people",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Slider(
                            value = guestCount.toFloat(),
                            onValueChange = { guestCount = it.toInt() },
                            valueRange = 2f..30f,
                            steps = 27,
                            colors = SliderDefaults.colors(
                                thumbColor = SageGreenPrimary,
                                activeTrackColor = SageGreenPrimary
                            ),
                            modifier = Modifier.testTag("guests_count_slider")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Meal Type
                        Text(
                            text = "Meal Type",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 6.dp)
                        ) {
                            listOf("Lunch", "Dinner", "High Tea & Snacks").forEach { meal ->
                                FilterChip(
                                    selected = selectedMeal == meal,
                                    onClick = { selectedMeal = meal },
                                    label = { Text(meal) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = SageContainer,
                                        selectedLabelColor = OnSageContainer
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Dietary Preference
                        Text(
                            text = "Dietary Preference",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 6.dp)
                        ) {
                            listOf("Vegetarian", "Jain (No Onion/Garlic)", "Non-Vegetarian", "Vegan / Satvik").forEach { diet ->
                                FilterChip(
                                    selected = selectedDiet == diet,
                                    onClick = { selectedDiet = diet },
                                    label = { Text(diet) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = SageContainer,
                                        selectedLabelColor = OnSageContainer
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Budget
                        Text(
                            text = "Approximate Budget",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 6.dp)
                        ) {
                            listOf("₹1,500", "₹3,000", "₹5,000", "₹8,000+").forEach { budget ->
                                FilterChip(
                                    selected = selectedBudget == budget,
                                    onClick = { selectedBudget = budget },
                                    label = { Text(budget) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = TerracottaContainer,
                                        selectedLabelColor = OnTerracottaContainer
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Prep Time Available
                        Text(
                            text = "Available Preparation Time",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 6.dp)
                        ) {
                            listOf("Under 1 Hour", "2 Hours", "3-4 Hours", "All Day").forEach { time ->
                                FilterChip(
                                    selected = selectedPrepTime == time,
                                    onClick = { selectedPrepTime = time },
                                    label = { Text(time) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = SageContainer,
                                        selectedLabelColor = OnSageContainer
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Special Notes
                        OutlinedTextField(
                            value = specialNotes,
                            onValueChange = { specialNotes = it },
                            label = { Text("Any specific preferences or dishes?") },
                            placeholder = { Text("e.g., Mummy loves gulab jamun, make paneer spicy...") },
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SageGreenPrimary),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = {
                                val prompt = "$guestCount guests are coming for $selectedMeal. Diet: $selectedDiet. Budget: $selectedBudget. Preparation time: $selectedPrepTime. ${if (specialNotes.isNotBlank()) "Notes: $specialNotes" else ""}"
                                viewModel.updatePromptInput(prompt)
                                viewModel.handleUserProblem(prompt, PlanCategory.GUESTS)
                                onPlanGenerated()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SageGreenPrimary),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("guests_generate_plan_button")
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Generate Hosting Plan ✨",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
