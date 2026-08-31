package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.TaskEntity
import com.example.data.model.TaskSection
import com.example.ui.components.CalmingBannerCard
import com.example.ui.components.NivaTopAppBar
import com.example.ui.components.SectionHeaderView
import com.example.ui.components.TaskCardItem
import com.example.ui.theme.*
import com.example.ui.viewmodel.NivaViewModel

@Composable
fun MyDayScreen(
    viewModel: NivaViewModel,
    modifier: Modifier = Modifier
) {
    val todayTasks by viewModel.todayTasks.collectAsStateWithLifecycle()
    val unfinishedTasks by viewModel.unfinishedTasks.collectAsStateWithLifecycle()
    val familyMembers by viewModel.allFamilyMembers.collectAsStateWithLifecycle()

    var showAddTaskDialog by remember { mutableStateOf(false) }
    var newTaskTitle by remember { mutableStateOf("") }
    var newTaskSection by remember { mutableStateOf(TaskSection.TODAY) }
    var newTaskPriority by remember { mutableStateOf("NORMAL") }

    val pendingToday = todayTasks.filter { !it.isCompleted }
    val completedToday = todayTasks.filter { it.isCompleted }

    Scaffold(
        topBar = {
            NivaTopAppBar(
                title = "My Day",
                subtitle = "Good morning ☀️ Here's your day."
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddTaskDialog = true },
                containerColor = SageGreenPrimary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape,
                modifier = Modifier
                    .padding(bottom = 60.dp)
                    .testTag("my_day_add_task_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding() + 8.dp,
                bottom = innerPadding.calculateBottomPadding() + 80.dp,
                start = 16.dp,
                end = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Smart Prioritize Button
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
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "✨ Make My Day Easier",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = SageGreenPrimary
                            )
                            Text(
                                text = "Auto-sort tasks by morning urgency & energy level",
                                style = MaterialTheme.typography.bodySmall,
                                color = CharcoalMedium
                            )
                        }

                        Button(
                            onClick = { viewModel.makeMyDayEasier() },
                            colors = ButtonDefaults.buttonColors(containerColor = SageContainer),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("make_day_easier_button")
                        ) {
                            Text(
                                text = "Auto-Prioritize",
                                style = MaterialTheme.typography.labelSmall,
                                color = OnSageContainer,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Pending Tasks
            item {
                SectionHeaderView(
                    title = "Pending for Today",
                    emoji = "☀️",
                    badgeText = "${pendingToday.size} remaining"
                )
            }

            if (pendingToday.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Text(text = "🎉", fontSize = 32.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "All tasks completed for today!",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Enjoy your free evening without stress.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = CharcoalMuted
                            )
                        }
                    }
                }
            } else {
                items(pendingToday, key = { it.id }) { task ->
                    TaskCardItem(
                        task = task,
                        onToggleCompleted = { viewModel.toggleTaskCompletion(task) },
                        onDelete = { viewModel.deleteTask(task.id) },
                        onReschedule = { newDate -> viewModel.rescheduleTask(task, newDate) }
                    )
                }
            }

            // Completed Tasks
            if (completedToday.isNotEmpty()) {
                item {
                    SectionHeaderView(
                        title = "Completed Today",
                        emoji = "✓",
                        badgeText = "${completedToday.size} done"
                    )
                }

                items(completedToday, key = { it.id }) { task ->
                    TaskCardItem(
                        task = task,
                        onToggleCompleted = { viewModel.toggleTaskCompletion(task) },
                        onDelete = { viewModel.deleteTask(task.id) },
                        onReschedule = { newDate -> viewModel.rescheduleTask(task, newDate) }
                    )
                }
            }

            item {
                CalmingBannerCard(
                    message = "“One task at a time. NIVA is taking care of the rest.”"
                )
            }
        }
    }

    if (showAddTaskDialog) {
        AlertDialog(
            onDismissRequest = { showAddTaskDialog = false },
            title = { Text("Add New Task") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newTaskTitle,
                        onValueChange = { newTaskTitle = it },
                        label = { Text("What needs to be done?") },
                        placeholder = { Text("e.g. Order fresh paneer, pay water bill...") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = newTaskPriority == "HIGH",
                            onClick = { newTaskPriority = if (newTaskPriority == "HIGH") "NORMAL" else "HIGH" },
                            label = { Text("Mark Priority") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TerracottaContainer,
                                selectedLabelColor = OnTerracottaContainer
                            )
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTaskTitle.isNotBlank()) {
                            viewModel.addNewTask(
                                title = newTaskTitle,
                                section = newTaskSection,
                                priority = newTaskPriority
                            )
                            newTaskTitle = ""
                            showAddTaskDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SageGreenPrimary)
                ) {
                    Text("Add to My Day")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddTaskDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
