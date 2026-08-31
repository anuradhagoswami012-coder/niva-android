package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.NivaViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HandleItScreen(
    viewModel: NivaViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val promptInput by viewModel.currentPromptInput.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isGenerating.collectAsStateWithLifecycle()
    val plan by viewModel.currentStructuredPlan.collectAsStateWithLifecycle()
    val isListeningVoice by viewModel.isListeningVoice.collectAsStateWithLifecycle()
    val lastSavedPlanId by viewModel.lastSavedPlanId.collectAsStateWithLifecycle()

    // Local checklist state for immediate interactivity before/after saving
    val checkedItems = remember { mutableStateMapOf<String, Boolean>() }

    Scaffold(
        topBar = {
            NivaTopAppBar(
                title = "Ask NIVA",
                subtitle = "Tell NIVA. It gets handled.",
                showBack = true,
                onBackClick = onBackClick,
                actions = {
                    if (plan != null) {
                        IconButton(
                            onClick = {
                                val shareText = viewModel.getShareablePlanText(plan)
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, "Share Plan via")
                                context.startActivity(shareIntent)
                            },
                            modifier = Modifier.testTag("handle_it_share_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share Plan",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (plan != null) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.navigationBars)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                val shareText = viewModel.getShareablePlanText(plan)
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, "Share Plan")
                                context.startActivity(shareIntent)
                            },
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("handle_it_bottom_share")
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share Plan")
                        }

                        Button(
                            onClick = { viewModel.saveCurrentPlan() },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SageGreenPrimary),
                            modifier = Modifier
                                .weight(1.3f)
                                .height(50.dp)
                                .testTag("handle_it_bottom_save")
                        ) {
                            Icon(Icons.Default.BookmarkBorder, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Save Plan")
                        }
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding() + 8.dp,
                bottom = innerPadding.calculateBottomPadding() + 16.dp,
                start = 16.dp,
                end = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Input refinement bar
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, DividerWarm, RoundedCornerShape(18.dp))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        OutlinedTextField(
                            value = promptInput,
                            onValueChange = { viewModel.updatePromptInput(it) },
                            placeholder = { Text("Ask or refine in English, Hindi, Hinglish...", fontSize = 13.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent
                            ),
                            singleLine = false,
                            maxLines = 3,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("handle_it_refine_text_field")
                        )

                        IconButton(
                            onClick = {
                                if (isListeningVoice) viewModel.stopVoiceInput() else viewModel.startVoiceInput(context)
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (isListeningVoice) TerracottaContainer else MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Icon(
                                imageVector = if (isListeningVoice) Icons.Filled.MicOff else Icons.Filled.Mic,
                                contentDescription = "Voice",
                                tint = if (isListeningVoice) TerracottaAccent else SageGreenPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        IconButton(
                            onClick = { viewModel.handleUserProblem() },
                            enabled = promptInput.isNotBlank() && !isGenerating,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(SageGreenPrimary)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Handle",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Generating Progress
            if (isGenerating) {
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = SageContainer.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(18.dp)
                        ) {
                            CircularProgressIndicator(
                                color = SageGreenPrimary,
                                strokeWidth = 3.dp,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = "NIVA is organizing your plan…",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = OnSageContainer
                                )
                                Text(
                                    text = "Structuring checklists, timelines, and quantities",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = CharcoalMedium
                                )
                            }
                        }
                    }
                }
            }

            // Clarification Questions Card (If essential info was missing)
            if (plan != null && plan!!.missingQuestions.isNotEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = TerracottaContainer.copy(alpha = 0.7f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("clarification_card")
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "❤️", fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Got it! Just tell me a couple quick details to fine-tune:",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = OnTerracottaContainer
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            plan!!.missingQuestions.forEach { question ->
                                Text(
                                    text = "• $question",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = OnTerracottaContainer,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "Quick answer chips:",
                                style = MaterialTheme.typography.labelSmall,
                                color = CharcoalMedium
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf("Lunch", "Dinner", "Vegetarian", "Jain", "Non-Veg", "Budget ₹2,500", "Budget ₹4,000").forEach { chipText ->
                                    SuggestionChip(
                                        onClick = {
                                            viewModel.answerClarification("Detail", chipText)
                                        },
                                        label = { Text(chipText, fontSize = 12.sp) },
                                        colors = SuggestionChipDefaults.suggestionChipColors(
                                            containerColor = MaterialTheme.colorScheme.surface
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Structured Plan Display
            if (plan != null) {
                // Header Info
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, DividerWarm, RoundedCornerShape(20.dp))
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Surface(
                                    color = SageContainer,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(text = plan!!.category.iconEmoji, fontSize = 14.sp)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = plan!!.category.displayName,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = OnSageContainer,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                if (plan!!.estimatedBudget.isNotBlank()) {
                                    Surface(
                                        color = TerracottaContainer,
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = plan!!.estimatedBudget,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = OnTerracottaContainer,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = plan!!.title,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = plan!!.empatheticIntro,
                                style = MaterialTheme.typography.bodyMedium,
                                color = CharcoalMedium,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }

                // Interactive Sections & Checklists
                plan!!.sections.forEach { section ->
                    item {
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, DividerWarm.copy(alpha = 0.7f), RoundedCornerShape(18.dp))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = section.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = SageGreenPrimary
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    Text(
                                        text = "${section.items.size} items",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = CharcoalMuted
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                section.items.forEachIndexed { index, itemText ->
                                    val isChecked = checkedItems[itemText] ?: false
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable { checkedItems[itemText] = !isChecked }
                                            .padding(vertical = 6.dp)
                                    ) {
                                        Checkbox(
                                            checked = isChecked,
                                            onCheckedChange = { checkedItems[itemText] = it },
                                            colors = CheckboxDefaults.colors(
                                                checkedColor = SageGreenPrimary,
                                                uncheckedColor = CharcoalMuted
                                            ),
                                            modifier = Modifier.size(28.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = itemText,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = if (isChecked) CharcoalMuted else MaterialTheme.colorScheme.onSurface,
                                            fontWeight = if (isChecked) FontWeight.Normal else FontWeight.Medium
                                        )
                                    }
                                    if (index < section.items.size - 1) {
                                        Divider(
                                            color = DividerWarm.copy(alpha = 0.4f),
                                            modifier = Modifier.padding(vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    CalmingBannerCard(
                        message = "“I’ve organized it. You don’t have to hold all of it in your head.”"
                    )
                }
            } else if (!isGenerating) {
                // Empty state when no plan is active
                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(SageContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "✨", fontSize = 36.sp)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Tell NIVA what’s stressing you out",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Type or speak anything — from grocery chaos to hosting 15 guests.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = CharcoalMuted
                        )
                    }
                }
            }
        }
    }
}
