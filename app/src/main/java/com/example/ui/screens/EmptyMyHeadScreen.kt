package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
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
import com.example.ui.components.CalmingBannerCard
import com.example.ui.components.NivaTopAppBar
import com.example.ui.theme.*
import com.example.ui.viewmodel.NivaViewModel

@Composable
fun EmptyMyHeadScreen(
    viewModel: NivaViewModel,
    onBackClick: () -> Unit,
    onPlanGenerated: () -> Unit,
    modifier: Modifier = Modifier
) {
    var brainDumpText by remember {
        mutableStateOf(
            "Tomorrow I need vegetables, call mummy, pay electricity bill, get clothes ironed, guests might come Sunday, buy papa’s medicine and clean the guest room."
        )
    }

    val samplePills = listOf(
        "Tomorrow I need vegetables, call mummy, pay electricity bill...",
        "Kids school project, milk delivery, order paneer, dry cleaning, weekend dinner...",
        "Deep clean kitchen, service AC, bank KYC, call plumber, buy fruits..."
    )

    Scaffold(
        topBar = {
            NivaTopAppBar(
                title = "Empty My Head",
                subtitle = "Tell me everything. I’ll organize it.",
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
                Column {
                    Text(
                        text = "🧠 Brain Dump",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Type an unorganized stream of thoughts in English, Hindi, or Hinglish. NIVA will automatically group it into Today, Tomorrow, Shopping, Home, Family, Money & Later.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = CharcoalMedium,
                        lineHeight = 20.sp
                    )
                }
            }

            // Input Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, DividerWarm, RoundedCornerShape(20.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        OutlinedTextField(
                            value = brainDumpText,
                            onValueChange = { brainDumpText = it },
                            placeholder = {
                                Text("Tell me everything on your mind without worrying about order or grammar...")
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SageGreenPrimary,
                                unfocusedBorderColor = Color.Transparent
                            ),
                            minLines = 5,
                            maxLines = 10,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("empty_head_input_text_field")
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                if (brainDumpText.isNotBlank()) {
                                    viewModel.updatePromptInput(brainDumpText)
                                    viewModel.handleUserProblem(brainDumpText, PlanCategory.EMPTY_HEAD)
                                    onPlanGenerated()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SageGreenPrimary),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("empty_head_organize_button")
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Organize Everything ✨",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }

            // Quick Samples
            item {
                Text(
                    text = "Tap to try a messy example:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = CharcoalMedium
                )
            }

            items(samplePills) { sample ->
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(14.dp),
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { brainDumpText = sample }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Text(text = "💡", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = sample,
                            style = MaterialTheme.typography.bodySmall,
                            color = CharcoalDark
                        )
                    }
                }
            }

            item {
                CalmingBannerCard(
                    message = "“I’ve organized it. You don’t have to hold all of it in your head.”"
                )
            }
        }
    }
}
