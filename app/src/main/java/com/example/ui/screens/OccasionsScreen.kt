package com.example.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Celebration
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
fun OccasionsScreen(
    viewModel: NivaViewModel,
    onBackClick: () -> Unit,
    onPlanGenerated: () -> Unit,
    modifier: Modifier = Modifier
) {
    val occasions = listOf(
        OccasionPreset("🎉", "Birthday Celebration", "Cake, party snacks, decor, music playlist, guest invitations, return gifts"),
        OccasionPreset("🪔", "Diwali / Puja Gathering", "Puja samagri, sweets, lighting & diyas, deep clean sequence, prasad, festive dinner"),
        OccasionPreset("💍", "Anniversary Dinner", "Special reservations or candle-lit dinner, gift delivery, flowers, memories album"),
        OccasionPreset("🏡", "Housewarming (Griha Pravesh)", "Pooja timings, welcome snacks, catering arrangement, guest seating, return favours"),
        OccasionPreset("🚗", "Family Weekend Trip", "Car check, hotel reservations, packing checklist, snacks & water, emergency medicine kit")
    )

    var selectedOccasion by remember { mutableStateOf(occasions[0]) }
    var occasionGuestCount by remember { mutableIntStateOf(12) }
    var occasionBudget by remember { mutableStateOf("₹5,000") }
    var notes by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            NivaTopAppBar(
                title = "Occasions & Events",
                subtitle = "Festivals, birthdays and family milestones",
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
                            text = "🎉 Choose Event Type",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            occasions.forEach { occ ->
                                FilterChip(
                                    selected = selectedOccasion == occ,
                                    onClick = { selectedOccasion = occ },
                                    label = { Text("${occ.emoji} ${occ.name}") },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = SageContainer,
                                        selectedLabelColor = OnSageContainer
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Estimated Guests: $occasionGuestCount people",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Slider(
                            value = occasionGuestCount.toFloat(),
                            onValueChange = { occasionGuestCount = it.toInt() },
                            valueRange = 2f..50f,
                            steps = 47,
                            colors = SliderDefaults.colors(
                                thumbColor = SageGreenPrimary,
                                activeTrackColor = SageGreenPrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            label = { Text("Special themes or requests") },
                            placeholder = { Text("e.g. Cartoon theme for kids, Satvik sweets for puja...") },
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SageGreenPrimary),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = {
                                val prompt = "Plan a ${selectedOccasion.name} for $occasionGuestCount guests. Budget $occasionBudget. Focus on: ${selectedOccasion.details}. ${if (notes.isNotBlank()) "Notes: $notes" else ""}"
                                viewModel.updatePromptInput(prompt)
                                viewModel.handleUserProblem(prompt, PlanCategory.OCCASION)
                                onPlanGenerated()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SageGreenPrimary),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("occasion_plan_button")
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Create Occasion Plan ✨",
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

data class OccasionPreset(
    val emoji: String,
    val name: String,
    val details: String
)
