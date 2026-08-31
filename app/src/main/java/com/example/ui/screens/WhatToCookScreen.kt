package com.example.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
fun WhatToCookScreen(
    viewModel: NivaViewModel,
    onBackClick: () -> Unit,
    onPlanGenerated: () -> Unit,
    modifier: Modifier = Modifier
) {
    val commonIngredients = listOf(
        "Paneer", "Capsicum / Shimla Mirch", "Potatoes (Aloo)", "Curd / Dahi",
        "Atta / Wheat", "Tomatoes", "Green Peas (Matar)", "Moong Dal", "Basmati Rice",
        "Onion & Garlic", "Ginger & Green Chillies", "Eggs", "Spinach (Palak)", "Bread"
    )

    val selectedIngredients = remember { mutableStateListOf("Paneer", "Capsicum / Shimla Mirch", "Curd / Dahi") }
    var extraIngredientsInput by remember { mutableStateOf("") }
    var selectedMeal by remember { mutableStateOf("Dinner") }
    var peopleCount by remember { mutableIntStateOf(4) }
    var dietPreference by remember { mutableStateOf("Vegetarian") }

    Scaffold(
        topBar = {
            NivaTopAppBar(
                title = "What Should I Cook?",
                subtitle = "Balanced meals from what’s in your kitchen",
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
                            text = "🍲 Available Kitchen Ingredients",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Select what you have in your fridge or pantry right now:",
                            style = MaterialTheme.typography.bodySmall,
                            color = CharcoalMedium,
                            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                        )

                        // Pantry chips
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            commonIngredients.forEach { item ->
                                val isSelected = selectedIngredients.contains(item)
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        if (isSelected) selectedIngredients.remove(item)
                                        else selectedIngredients.add(item)
                                    },
                                    label = { Text(item, fontSize = 12.sp) },
                                    leadingIcon = if (isSelected) {
                                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                                    } else null,
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = SageContainer,
                                        selectedLabelColor = OnSageContainer
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = extraIngredientsInput,
                            onValueChange = { extraIngredientsInput = it },
                            label = { Text("Any other ingredients?") },
                            placeholder = { Text("e.g., mushrooms, sweet corn, leftover dal...") },
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SageGreenPrimary),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Meal & Diners
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Meal",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    listOf("Dinner", "Lunch", "Breakfast").forEach { meal ->
                                        FilterChip(
                                            selected = selectedMeal == meal,
                                            onClick = { selectedMeal = meal },
                                            label = { Text(meal, fontSize = 12.sp) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = SageContainer,
                                                selectedLabelColor = OnSageContainer
                                            )
                                        )
                                    }
                                }
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Diners: $peopleCount people",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Slider(
                                    value = peopleCount.toFloat(),
                                    onValueChange = { peopleCount = it.toInt() },
                                    valueRange = 1f..10f,
                                    steps = 8,
                                    colors = SliderDefaults.colors(
                                        thumbColor = SageGreenPrimary,
                                        activeTrackColor = SageGreenPrimary
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Diet Filter
                        Text(
                            text = "Preferences",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            listOf("Vegetarian", "Jain (No Onion/Garlic)", "Light / Low Oil", "Quick Under 20 Mins").forEach { diet ->
                                FilterChip(
                                    selected = dietPreference == diet,
                                    onClick = { dietPreference = diet },
                                    label = { Text(diet, fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = TerracottaContainer,
                                        selectedLabelColor = OnTerracottaContainer
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = {
                                val allIng = (selectedIngredients + listOfNotNull(extraIngredientsInput.takeIf { it.isNotBlank() })).joinToString(", ")
                                val prompt = "I have $allIng for $selectedMeal for $peopleCount people. Preference: $dietPreference. What should I cook tonight?"
                                viewModel.updatePromptInput(prompt)
                                viewModel.handleUserProblem(prompt, PlanCategory.COOKING)
                                onPlanGenerated()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SageGreenPrimary),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("what_to_cook_submit_button")
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Recommend Tonight’s Menu ✨",
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
