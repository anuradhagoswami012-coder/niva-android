package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
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
fun HelpNowScreen(
    viewModel: NivaViewModel,
    onBackClick: () -> Unit,
    onPlanGenerated: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val crisisOptions = listOf(
        CrisisItem(
            emoji = "👥",
            title = "Guests arriving soon",
            subtitle = "15-minute quick house reset & welcoming drink readiness",
            prompt = "Help! Guests are arriving in 15 minutes. Give me a rapid step-by-step house reset and welcome checklist."
        ),
        CrisisItem(
            emoji = "🍲",
            title = "Dinner in 30 mins",
            subtitle = "Fast, nutritious, low-mess one-pot dinner sequence",
            prompt = "I need to make dinner in 30 minutes with minimal cleanup. Give me a rapid cooking plan."
        ),
        CrisisItem(
            emoji = "🧹",
            title = "Quick house cleanup",
            subtitle = "20-minute visual clutter clear before people enter",
            prompt = "I have 20 minutes to clean up the living room, bathroom, and kitchen. Give me an organized fast plan."
        ),
        CrisisItem(
            emoji = "🎁",
            title = "Forgot an occasion",
            subtitle = "Instant gift, cake, e-voucher & warm message fix",
            prompt = "I forgot an anniversary/birthday today! Give me an instant plan for fast gift delivery, message, and evening fix."
        ),
        CrisisItem(
            emoji = "🧳",
            title = "Packing tonight",
            subtitle = "Fast essentials, chargers, medicines & clothes packing list",
            prompt = "I need to pack tonight for a trip tomorrow. Give me a fast, foolproof packing checklist."
        ),
        CrisisItem(
            emoji = "☀️",
            title = "Organize tomorrow",
            subtitle = "10-minute bedtime reset so tomorrow starts smoothly",
            prompt = "Help me organize tomorrow morning so I wake up calm and prepared."
        ),
        CrisisItem(
            emoji = "💸",
            title = "Budget until payday",
            subtitle = "Stretch household groceries and essentials peacefully",
            prompt = "I need to manage the household budget strictly for the next 7 days until payday."
        )
    )

    Scaffold(
        topBar = {
            NivaTopAppBar(
                title = "🚨 Help Me Now",
                subtitle = "Rapid calm checklists when life gets chaotic",
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
            item {
                CalmingBannerCard(
                    message = "“Take a deep breath. NIVA will break this down into 5-minute easy steps.”"
                )
            }

            // Rapid Choices
            items(crisisOptions) { option ->
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.updatePromptInput(option.prompt)
                            viewModel.handleUserProblem(option.prompt, PlanCategory.HELP_NOW)
                            onPlanGenerated()
                        }
                        .border(1.dp, DividerWarm, RoundedCornerShape(18.dp))
                        .testTag("help_now_option_${option.title}")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = TerracottaContainer,
                            modifier = Modifier.size(46.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = option.emoji, fontSize = 22.sp)
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = option.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = option.subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = CharcoalMedium
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = CharcoalMuted
                        )
                    }
                }
            }

            // Emergency Safety Disclaimer
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.HealthAndSafety, contentDescription = null, tint = TerracottaAccent)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Emergency Medical / Police Services",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "NIVA is for everyday life & household management. For real life-safety, medical emergencies or fire, immediately contact official local emergency services.",
                            style = MaterialTheme.typography.bodySmall,
                            color = CharcoalMedium
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:112"))
                                    context.startActivity(intent)
                                },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("Dial 112 (National Emergency)", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

data class CrisisItem(
    val emoji: String,
    val title: String,
    val subtitle: String,
    val prompt: String
)
