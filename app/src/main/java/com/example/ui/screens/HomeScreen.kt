package com.example.ui.screens

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.NivaViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    viewModel: NivaViewModel,
    onNavigateToHandleIt: () -> Unit,
    onNavigateToEmptyMyHead: () -> Unit,
    onNavigateToGuestsComing: () -> Unit,
    onNavigateToWhatToCook: () -> Unit,
    onNavigateToPlanMoney: () -> Unit,
    onNavigateToHelpNow: () -> Unit,
    onNavigateToOccasions: () -> Unit,
    onNavigateToMyDay: () -> Unit,
    onNavigateToSavedPlans: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val promptInput by viewModel.currentPromptInput.collectAsStateWithLifecycle()
    val isListeningVoice by viewModel.isListeningVoice.collectAsStateWithLifecycle()
    val todayTasks by viewModel.todayTasks.collectAsStateWithLifecycle()
    val allPlans by viewModel.allPlans.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            NivaTopAppBar(
                title = "NIVA",
                subtitle = "Tell NIVA. It gets handled.",
                actions = {
                    IconButton(
                        onClick = onNavigateToHelpNow,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(TerracottaContainer)
                            .border(1.dp, TerracottaAccent.copy(alpha = 0.3f), CircleShape)
                            .testTag("home_help_now_button")
                    ) {
                        Text(text = "🚨", fontSize = 16.sp)
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding() + 4.dp,
                bottom = innerPadding.calculateBottomPadding() + 24.dp,
                start = 18.dp,
                end = 18.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Personalized Greeting
            item {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Text(
                        text = "Good morning, $userName ✨",
                        style = MaterialTheme.typography.titleMedium,
                        color = CharcoalMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = buildAnnotatedString {
                            append("What do you need ")
                            withStyle(
                                SpanStyle(
                                    color = TerracottaAccent,
                                    fontStyle = FontStyle.Italic,
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append("handled")
                            }
                            append(" today?")
                        },
                        style = MaterialTheme.typography.headlineLarge,
                        color = CharcoalDark,
                        lineHeight = 32.sp
                    )
                }
            }

            // Hero Input Card (Geometric Balance Style)
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = WarmSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, SageGreenPrimary.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
                        .testTag("home_input_card")
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp)
                    ) {
                        OutlinedTextField(
                            value = promptInput,
                            onValueChange = { viewModel.updatePromptInput(it) },
                            placeholder = {
                                Text(
                                    text = "Tell NIVA what’s on your mind…\n(e.g., “10 guests coming tomorrow”, or “paneer, capsicum what to cook?”)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = CharcoalMuted,
                                    lineHeight = 20.sp
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            ),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(color = CharcoalDark),
                            minLines = 3,
                            maxLines = 5,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("home_problem_text_field")
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Mic Button
                            IconButton(
                                onClick = {
                                    if (isListeningVoice) {
                                        viewModel.stopVoiceInput()
                                    } else {
                                        viewModel.startVoiceInput(context)
                                    }
                                },
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(if (isListeningVoice) TerracottaContainer else SageContainer)
                                    .border(
                                        1.dp,
                                        if (isListeningVoice) TerracottaAccent.copy(alpha = 0.4f) else SageGreenPrimary.copy(alpha = 0.3f),
                                        CircleShape
                                    )
                                    .testTag("home_voice_input_button")
                            ) {
                                Icon(
                                    imageVector = if (isListeningVoice) Icons.Filled.MicOff else Icons.Filled.Mic,
                                    contentDescription = "Voice input",
                                    tint = if (isListeningVoice) TerracottaAccent else SageGreenPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // Main Handle It Button
                            Button(
                                onClick = {
                                    if (promptInput.isNotBlank()) {
                                        viewModel.handleUserProblem()
                                        onNavigateToHandleIt()
                                    }
                                },
                                enabled = promptInput.isNotBlank(),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = SageGreenPrimary,
                                    contentColor = Color.White,
                                    disabledContainerColor = SageGreenPrimary.copy(alpha = 0.4f),
                                    disabledContentColor = Color.White.copy(alpha = 0.7f)
                                ),
                                contentPadding = PaddingValues(horizontal = 22.dp, vertical = 12.dp),
                                modifier = Modifier
                                    .height(46.dp)
                                    .testTag("home_handle_it_button")
                            ) {
                                Text(
                                    text = "✨ HANDLE IT",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }
                }
            }

            // Quick Actions Section
            item {
                Column(modifier = Modifier.padding(top = 4.dp)) {
                    Text(
                        text = "QUICK ACTIONS",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = SageGreenPrimary,
                        letterSpacing = 1.2.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        QuickActionChip(
                            emoji = "🧠",
                            label = "Empty My Head",
                            onClick = onNavigateToEmptyMyHead
                        )
                        QuickActionChip(
                            emoji = "🍲",
                            label = "What Should I Cook?",
                            onClick = onNavigateToWhatToCook
                        )
                        QuickActionChip(
                            emoji = "👥",
                            label = "Guests Are Coming",
                            onClick = onNavigateToGuestsComing
                        )
                        QuickActionChip(
                            emoji = "💰",
                            label = "Plan My Money",
                            onClick = onNavigateToPlanMoney
                        )
                        QuickActionChip(
                            emoji = "🚨",
                            label = "Help Me Now",
                            onClick = onNavigateToHelpNow
                        )
                        QuickActionChip(
                            emoji = "🎉",
                            label = "Occasions",
                            onClick = onNavigateToOccasions
                        )
                    }
                }
            }

            // Calming Emotional Banner
            item {
                CalmingBannerCard(message = "“You don’t have to keep everything in your head.”")
            }

            // TODAY: "X things NIVA is helping you handle"
            item {
                val pendingToday = todayTasks.filter { !it.isCompleted }.take(4)
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(
                                text = "TODAY",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = TerracottaAccent,
                                letterSpacing = 1.4.sp
                            )
                            Text(
                                text = "${todayTasks.count { !it.isCompleted }} things NIVA is helping you handle",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = CharcoalDark
                            )
                        }

                        TextButton(
                            onClick = onNavigateToMyDay,
                            modifier = Modifier.testTag("home_view_all_my_day_button")
                        ) {
                            Text(
                                text = "See All",
                                style = MaterialTheme.typography.labelLarge,
                                color = SageGreenPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (pendingToday.isEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = WarmSurface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, DividerWarm, RoundedCornerShape(20.dp))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(18.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(SageContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "✨", fontSize = 20.sp)
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column {
                                    Text(
                                        text = "Your day is fully clear!",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = CharcoalDark
                                    )
                                    Text(
                                        text = "Tell NIVA anything whenever you need a plan.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = CharcoalMuted
                                    )
                                }
                            }
                        }
                    } else {
                        pendingToday.forEach { task ->
                            TaskCardItem(
                                task = task,
                                onToggleCompleted = { viewModel.toggleTaskCompletion(task) },
                                onDelete = { viewModel.deleteTask(task.id) },
                                onReschedule = { newDate -> viewModel.rescheduleTask(task, newDate) }
                            )
                        }
                    }
                }
            }

            // Saved Plans Section
            if (allPlans.isNotEmpty()) {
                item {
                    Column(modifier = Modifier.padding(top = 4.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "SAVED ACTION PLANS",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = SageGreenPrimary,
                                letterSpacing = 1.2.sp
                            )
                            TextButton(onClick = onNavigateToSavedPlans) {
                                Text(
                                    text = "View All (${allPlans.size})",
                                    color = SageGreenPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(allPlans.take(5)) { plan ->
                                Card(
                                    shape = RoundedCornerShape(20.dp),
                                    colors = CardDefaults.cardColors(containerColor = WarmSurface),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                                    modifier = Modifier
                                        .width(230.dp)
                                        .clickable {
                                            viewModel.handleUserProblem(plan.originalPrompt, plan.category)
                                            onNavigateToHandleIt()
                                        }
                                        .border(1.dp, DividerWarm, RoundedCornerShape(20.dp))
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clip(CircleShape)
                                                    .background(SageContainer),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(text = plan.category.iconEmoji, fontSize = 18.sp)
                                            }
                                            if (plan.estimatedBudget.isNotBlank()) {
                                                Surface(
                                                    color = TerracottaContainer,
                                                    shape = RoundedCornerShape(8.dp)
                                                ) {
                                                    Text(
                                                        text = plan.estimatedBudget,
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = OnTerracottaContainer,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            text = plan.title,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 2,
                                            color = CharcoalDark
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
                    }
                }
            }
        }
    }
}

