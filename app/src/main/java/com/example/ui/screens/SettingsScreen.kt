package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.CalmingBannerCard
import com.example.ui.components.NivaTopAppBar
import com.example.ui.theme.*
import com.example.ui.viewmodel.NivaViewModel

@Composable
fun SettingsScreen(
    viewModel: NivaViewModel,
    onNavigateToMemory: () -> Unit,
    onNavigateToFamily: () -> Unit,
    onNavigateToSavedPlans: () -> Unit,
    onNavigateToBudget: () -> Unit,
    modifier: Modifier = Modifier
) {
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val selectedLanguage by viewModel.selectedLanguage.collectAsStateWithLifecycle()
    val selectedCurrency by viewModel.selectedCurrency.collectAsStateWithLifecycle()

    var showEditNameDialog by remember { mutableStateOf(false) }
    var editNameInput by remember { mutableStateOf(userName) }

    Scaffold(
        topBar = {
            NivaTopAppBar(
                title = "Me & Settings",
                subtitle = "Preferences, privacy and about NIVA"
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
            // Profile Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, DividerWarm, RoundedCornerShape(20.dp))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(SageContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🌸", fontSize = 28.sp)
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = userName,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "NIVA Assistant Active",
                                style = MaterialTheme.typography.bodySmall,
                                color = SageGreenPrimary,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        IconButton(
                            onClick = {
                                editNameInput = userName
                                showEditNameDialog = true
                            },
                            modifier = Modifier.testTag("settings_edit_name_button")
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Name", tint = CharcoalMedium)
                        }
                    }
                }
            }

            // Quick Nav Links Card
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, DividerWarm, RoundedCornerShape(18.dp))
                ) {
                    Column(modifier = Modifier.padding(vertical = 6.dp)) {
                        SettingsRow(
                            icon = "🛡️",
                            title = "NIVA Knows Me",
                            subtitle = "View and control transparent memory items",
                            onClick = onNavigateToMemory
                        )
                        Divider(color = DividerWarm.copy(alpha = 0.5f))
                        SettingsRow(
                            icon = "🏡",
                            title = "Family Hub",
                            subtitle = "Manage family members & shared tasks",
                            onClick = onNavigateToFamily
                        )
                        Divider(color = DividerWarm.copy(alpha = 0.5f))
                        SettingsRow(
                            icon = "📑",
                            title = "Saved Action Plans",
                            subtitle = "Archive of cooking & hosting checklists",
                            onClick = onNavigateToSavedPlans
                        )
                        Divider(color = DividerWarm.copy(alpha = 0.5f))
                        SettingsRow(
                            icon = "💰",
                            title = "Household Budget",
                            subtitle = "Category allocations & peace-of-mind numbers",
                            onClick = onNavigateToBudget
                        )
                    }
                }
            }

            // Language & Regional Settings
            item {
                Text(
                    text = "Preferences",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, DividerWarm, RoundedCornerShape(18.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Assistant Language Mode",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Hinglish", "English", "हिंदी").forEach { lang ->
                                FilterChip(
                                    selected = selectedLanguage == lang,
                                    onClick = { viewModel.setLanguage(lang) },
                                    label = { Text(lang) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = SageContainer,
                                        selectedLabelColor = OnSageContainer
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Currency Symbol",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("₹", "$", "€", "£", "AED").forEach { curr ->
                                FilterChip(
                                    selected = selectedCurrency == curr,
                                    onClick = { viewModel.setCurrency(curr) },
                                    label = { Text(curr) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = TerracottaContainer,
                                        selectedLabelColor = OnTerracottaContainer
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // About & Emotional Promise
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = SageContainer.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "✨", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "NIVA",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = OnSageContainer
                                )
                                Text(
                                    text = "“Tell NIVA. It gets handled.”",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = SageGreenPrimary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "“You don’t have to keep everything in your head.”\nDesigned with care for everyday life, cooking, hosting, and household management.",
                            style = MaterialTheme.typography.bodySmall,
                            color = CharcoalMedium,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }

    if (showEditNameDialog) {
        AlertDialog(
            onDismissRequest = { showEditNameDialog = false },
            title = { Text("Update Name") },
            text = {
                OutlinedTextField(
                    value = editNameInput,
                    onValueChange = { editNameInput = it },
                    label = { Text("Your Name") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SageGreenPrimary),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editNameInput.isNotBlank()) {
                            viewModel.setUserName(editNameInput)
                            showEditNameDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SageGreenPrimary)
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditNameDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SettingsRow(
    icon: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Text(text = icon, fontSize = 20.sp)
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
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
