package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NivaTopAppBar(
    title: String = "NIVA",
    subtitle: String = "Tell NIVA. It gets handled.",
    showBack: Boolean = false,
    onBackClick: () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}
) {
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                if (showBack) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(SurfaceSubtle)
                            .testTag("top_bar_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = CharcoalDark,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                } else {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(SageContainer)
                            .border(1.dp, SageGreenPrimary.copy(alpha = 0.3f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✨",
                            fontSize = 18.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                }

                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = CharcoalDark,
                        letterSpacing = (-0.2).sp
                    )
                    Text(
                        text = subtitle.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = SageGreenPrimary,
                        letterSpacing = 1.2.sp,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                actions()
            }
        }
    }
}

enum class NivaTab(val route: String, val title: String, val selectedIcon: ImageVector, val unselectedIcon: ImageVector) {
    HOME("home", "Home", Icons.Filled.Home, Icons.Outlined.Home),
    MY_DAY("my_day", "My Day", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth),
    HANDLE_IT("handle_it", "NIVA", Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome),
    FAMILY("family", "Family", Icons.Filled.Groups, Icons.Outlined.Groups),
    ME("me", "Me", Icons.Filled.Person, Icons.Outlined.Person)
}

@Composable
fun NivaBottomNavigation(
    currentTab: NivaTab,
    onTabSelected: (NivaTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = WarmSurface,
        tonalElevation = 4.dp,
        modifier = modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = DividerWarm)
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            tonalElevation = 0.dp,
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.navigationBars)
                .testTag("niva_bottom_nav")
        ) {
            NivaTab.values().forEach { tab ->
                val isSelected = currentTab == tab
                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onTabSelected(tab) },
                    icon = {
                        Icon(
                            imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                            contentDescription = tab.title,
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            text = tab.title,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            letterSpacing = 0.4.sp,
                            fontSize = 11.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = SageGreenPrimary,
                        selectedTextColor = SageGreenPrimary,
                        indicatorColor = SageContainer,
                        unselectedIconColor = CharcoalMuted,
                        unselectedTextColor = CharcoalMuted
                    ),
                    modifier = Modifier.testTag("nav_tab_${tab.route}")
                )
            }
        }
    }
}

@Composable
fun TaskCardItem(
    task: TaskEntity,
    onToggleCompleted: () -> Unit,
    onDelete: () -> Unit,
    onReschedule: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isCompleted) SurfaceSubtle.copy(alpha = 0.7f) else WarmSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (task.isCompleted) 0.dp else 1.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .border(
                width = 1.dp,
                color = if (task.isCompleted) Color.Transparent else DividerWarm,
                shape = RoundedCornerShape(18.dp)
            )
            .testTag("task_item_${task.id}")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            IconButton(
                onClick = onToggleCompleted,
                modifier = Modifier
                    .size(34.dp)
                    .minimumInteractiveComponentSize()
                    .testTag("task_check_${task.id}")
            ) {
                Icon(
                    imageVector = if (task.isCompleted) Icons.Filled.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                    contentDescription = if (task.isCompleted) "Completed" else "Mark complete",
                    tint = if (task.isCompleted) SageGreenPrimary else CharcoalMuted
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    color = if (task.isCompleted) CharcoalMuted else CharcoalDark,
                    fontWeight = if (task.isCompleted) FontWeight.Normal else FontWeight.SemiBold
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    // Due Date / Time pill
                    Surface(
                        color = SageContainer,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = task.dueDate,
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSageContainer,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    if (task.timeEstimate.isNotBlank()) {
                        Text(
                            text = "⏱ ${task.timeEstimate}",
                            style = MaterialTheme.typography.labelSmall,
                            color = CharcoalMuted
                        )
                    }

                    if (task.assignedMemberName != null) {
                        Surface(
                            color = TerracottaContainer,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "👤 ${task.assignedMemberName}",
                                style = MaterialTheme.typography.labelSmall,
                                color = OnTerracottaContainer,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    if (task.priority == "HIGH" && !task.isCompleted) {
                        Surface(
                            color = TerracottaContainer,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "PRIORITY",
                                style = MaterialTheme.typography.labelSmall,
                                color = TerracottaAccent,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            Box {
                IconButton(
                    onClick = { showMenu = true },
                    modifier = Modifier.minimumInteractiveComponentSize()
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Task options",
                        tint = CharcoalMuted
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Reschedule to Tomorrow") },
                        onClick = {
                            showMenu = false
                            onReschedule("Tomorrow")
                        },
                        leadingIcon = { Icon(Icons.Default.Event, contentDescription = null) }
                    )
                    DropdownMenuItem(
                        text = { Text("Reschedule to Weekend") },
                        onClick = {
                            showMenu = false
                            onReschedule("Weekend")
                        },
                        leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) }
                    )
                    Divider()
                    DropdownMenuItem(
                        text = { Text("Delete Task", color = MaterialTheme.colorScheme.error) },
                        onClick = {
                            showMenu = false
                            onDelete()
                        },
                        leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) }
                    )
                }
            }
        }
    }
}

@Composable
fun SectionHeaderView(
    title: String,
    emoji: String = "",
    badgeText: String? = null,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (emoji.isNotBlank()) {
                Text(text = emoji, fontSize = 16.sp)
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = CharcoalDark
            )
        }

        if (badgeText != null) {
            Surface(
                color = SageContainer,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = badgeText,
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSageContainer,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
fun QuickActionChip(
    emoji: String,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        color = WarmSurface,
        shadowElevation = 0.5.dp,
        modifier = modifier
            .border(1.dp, DividerWarm, RoundedCornerShape(18.dp))
            .testTag("quick_action_$label")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(text = emoji, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = CharcoalDark,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun CalmingBannerCard(
    message: String = "I’ve organized it. You don’t have to hold all of it in your head.",
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SageContainer.copy(alpha = 0.65f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, SageGreenPrimary.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(WarmSurface),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🌿", fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = OnSageContainer,
                fontWeight = FontWeight.Medium,
                lineHeight = 19.sp
            )
        }
    }
}

