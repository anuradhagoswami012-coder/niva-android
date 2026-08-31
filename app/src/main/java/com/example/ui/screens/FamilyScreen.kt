package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
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
import com.example.data.model.FamilyMemberEntity
import com.example.ui.components.CalmingBannerCard
import com.example.ui.components.NivaTopAppBar
import com.example.ui.theme.*
import com.example.ui.viewmodel.NivaViewModel

@Composable
fun FamilyScreen(
    viewModel: NivaViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val familyMembers by viewModel.allFamilyMembers.collectAsStateWithLifecycle()
    val todayTasks by viewModel.todayTasks.collectAsStateWithLifecycle()

    var showAddMemberDialog by remember { mutableStateOf(false) }
    var newMemberName by remember { mutableStateOf("") }
    var newMemberRelation by remember { mutableStateOf("Partner") }
    var newMemberEmoji by remember { mutableStateOf("👤") }

    Scaffold(
        topBar = {
            NivaTopAppBar(
                title = "Family Hub",
                subtitle = "Share household load peacefully",
                actions = {
                    IconButton(
                        onClick = {
                            val shareSummary = viewModel.getShareableFamilySummary()
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shareSummary)
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, "Share Responsibilities via")
                            context.startActivity(shareIntent)
                        },
                        modifier = Modifier.testTag("family_share_button")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share Responsibilities")
                    }

                    IconButton(
                        onClick = { showAddMemberDialog = true },
                        modifier = Modifier.testTag("family_add_member_button")
                    ) {
                        Icon(Icons.Default.PersonAddAlt1, contentDescription = "Add Member")
                    }
                }
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
                            text = "🏡 Household Responsibilities",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Assign tasks to family members and share one clear WhatsApp/SMS message with who is handling what.",
                            style = MaterialTheme.typography.bodySmall,
                            color = CharcoalMedium,
                            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                        )

                        Button(
                            onClick = {
                                val shareSummary = viewModel.getShareableFamilySummary()
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, shareSummary)
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, "Share Responsibilities via")
                                context.startActivity(shareIntent)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SageGreenPrimary),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Share Responsibilities Summary")
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Household Members",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            items(familyMembers) { member ->
                val memberTasks = todayTasks.filter { it.assignedToMemberId == member.id || it.assignedMemberName == member.name }

                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, DividerWarm, RoundedCornerShape(18.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = SageContainer,
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(text = member.emoji, fontSize = 22.sp)
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = member.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = member.relationship,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CharcoalMedium
                                )
                            }

                            if (member.relationship != "Me") {
                                IconButton(
                                    onClick = { viewModel.deleteFamilyMember(member.id) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        Icons.Default.DeleteOutline,
                                        contentDescription = "Remove",
                                        tint = CharcoalMuted,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (memberTasks.isEmpty()) {
                            Text(
                                text = "• No tasks assigned today",
                                style = MaterialTheme.typography.bodySmall,
                                color = CharcoalMuted,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        } else {
                            memberTasks.forEach { task ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp)
                                ) {
                                    Icon(
                                        imageVector = if (task.isCompleted) Icons.Filled.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                                        contentDescription = null,
                                        tint = if (task.isCompleted) SageGreenPrimary else CharcoalMuted,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = task.title,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (task.isCompleted) CharcoalMuted else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                CalmingBannerCard(
                    message = "“Sharing the mental load brings calmness to the entire family.”"
                )
            }
        }
    }

    if (showAddMemberDialog) {
        AlertDialog(
            onDismissRequest = { showAddMemberDialog = false },
            title = { Text("Add Family Member") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newMemberName,
                        onValueChange = { newMemberName = it },
                        label = { Text("Name") },
                        placeholder = { Text("e.g., Rahul, Dadi, Shreya") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newMemberRelation,
                        onValueChange = { newMemberRelation = it },
                        label = { Text("Relationship") },
                        placeholder = { Text("Partner, Mother, Child, Roommate") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newMemberName.isNotBlank()) {
                            viewModel.addFamilyMember(newMemberName, newMemberRelation, newMemberEmoji)
                            newMemberName = ""
                            showAddMemberDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SageGreenPrimary)
                ) {
                    Text("Add Member")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddMemberDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
