package com.example.skybound.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skybound.data.MilestoneEntity
import com.example.skybound.data.RecordEntity
import com.example.skybound.model.MILESTONES
import com.example.skybound.model.currentDateKey
import com.example.skybound.model.formatDateKey
import com.example.ui.theme.*

@Composable
fun RecordsScreen(
    activityName: String,
    records: List<RecordEntity>,
    milestones: List<MilestoneEntity>,
    onAddRecord: (title: String, note: String, dateKey: String) -> Unit,
    onDeleteRecord: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var titleInput by remember { mutableStateOf("") }
    var noteInput by remember { mutableStateOf("") }
    var dateInput by remember { mutableStateOf(currentDateKey()) }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Personal Best", fontWeight = FontWeight.Black) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = titleInput,
                        onValueChange = { titleInput = it },
                        label = { Text("What did you achieve?") },
                        placeholder = { Text("e.g. Ran entire session without stopping") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = noteInput,
                        onValueChange = { noteInput = it },
                        label = { Text("Note (optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = dateInput,
                        onValueChange = { dateInput = it },
                        label = { Text("Date (yyyy-MM-dd)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (titleInput.isNotBlank()) {
                            onAddRecord(titleInput.trim(), noteInput.trim(), dateInput.trim())
                            showAddDialog = false
                            titleInput = ""
                            noteInput = ""
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SkyBlue)
                ) {
                    Text("Save", fontWeight = FontWeight.Black, color = SkyNavy)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = SkyNavy)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Records & Milestones",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = SkyNavy
                    )
                )
            }
        }

        // Section 1: Personal Bests
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(2.dp, SkyMist)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Personal Bests: $activityName",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = SkyNavy
                            )
                        )
                        IconButton(
                            onClick = { showAddDialog = true },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(SkyBlue)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add record", tint = SkyNavy)
                        }
                    }

                    if (records.isNotEmpty()) {
                        records.forEach { record ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SkyCloud)
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = record.title,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Black,
                                            color = SkyNavy
                                        )
                                    )
                                    Text(
                                        text = "${formatDateKey(record.dateKey)}${if (record.note.isNotBlank()) " · ${record.note}" else ""}",
                                        style = MaterialTheme.typography.bodySmall.copy(color = SkyTextSubtle)
                                    )
                                }

                                IconButton(
                                    onClick = { onDeleteRecord(record.id) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = SkyTextMuted, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "No records added yet. Add something you are proud of from training.",
                            style = MaterialTheme.typography.bodySmall.copy(color = SkyTextSubtle)
                        )
                    }
                }
            }
        }

        // Section 2: Milestones
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(2.dp, SkyMist)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Milestones",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = SkyNavy
                        )
                    )
                    Text(
                        text = "Milestones celebrate effort and reflection. They are only for you, never compared with anyone.",
                        style = MaterialTheme.typography.bodySmall.copy(color = SkyTextSubtle)
                    )

                    MILESTONES.forEach { def ->
                        val isUnlocked = milestones.any { it.milestoneId == def.id }
                        val milestoneBg = if (isUnlocked) {
                            if (SkyboundThemeState.isDark) Color(0xFF2A2818) else Color(0xFFFFF8DC)
                        } else SkyCloud

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(milestoneBg)
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(if (isUnlocked) AccentSun else SkyMist),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = null,
                                    tint = if (isUnlocked) DeepNavy else SkyTextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = def.title,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = SkyNavy
                                    )
                                )
                                Text(
                                    text = def.description,
                                    style = MaterialTheme.typography.bodySmall.copy(color = SkyTextSubtle)
                                )
                            }

                            Text(
                                text = if (isUnlocked) "Unlocked ✓" else "Not yet",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isUnlocked) AccentMint else SkyTextMuted
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
