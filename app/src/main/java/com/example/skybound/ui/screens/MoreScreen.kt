package com.example.skybound.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skybound.data.ActivityEntity
import com.example.skybound.data.AppSettingsEntity
import com.example.skybound.ui.components.ActivityIcon
import com.example.ui.theme.*

@Composable
fun MoreScreen(
    appSettings: AppSettingsEntity,
    activities: List<ActivityEntity>,
    onNavigate: (String) -> Unit,
    onWeeklyTargetChange: (Int) -> Unit,
    onToggleMascotQuiet: () -> Unit,
    onAddNewActivity: () -> Unit,
    onDeleteActivity: (String) -> Unit,
    onLoadExampleData: () -> Unit,
    onRemoveExampleData: () -> Unit,
    onExportBackup: () -> Unit,
    onRestoreBackup: () -> Unit,
    onWipeData: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showWipeConfirm by remember { mutableStateOf(false) }

    if (showWipeConfirm) {
        AlertDialog(
            onDismissRequest = { showWipeConfirm = false },
            title = { Text("Delete everything?", fontWeight = FontWeight.Black) },
            text = { Text("All activities, goals, and history on this device will be deleted. This cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showWipeConfirm = false
                        onWipeData()
                    }
                ) {
                    Text("Delete Everything", color = AccentCoral, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showWipeConfirm = false }) {
                    Text("Cancel", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("more_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Section 1: Your Journey Links
        item {
            Text(
                text = "YOUR JOURNEY",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = SkyTextMuted
                )
            )
        }

        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(2.dp, SkyMist),
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MoreNavButton("About Skybound & How It Works", isPrimary = true) { onNavigate("about") }
                    MoreNavButton("Take the TeenTech Tour (Full Project)") { onNavigate("tour") }
                    MoreNavButton("Records & Milestones (Personal Bests)") { onNavigate("records") }
                    MoreNavButton("Goal Templates Library") { onNavigate("templates") }
                    MoreNavButton("Generate Progress Report") { onNavigate("report") }
                    MoreNavButton("Home Screen Widget Settings") { onNavigate("widget") }
                    MoreNavButton("Gentle Notifications & Reminders") { onNavigate("notifications") }
                    MoreNavButton("Appearance & Customization") { onNavigate("appearance") }
                }
            }
        }

        // Section 2: Activities Management
        item {
            Text(
                text = "ACTIVITIES",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = SkyTextMuted
                )
            )
        }

        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(2.dp, SkyMist),
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    activities.forEach { act ->
                        val color = try {
                            Color(android.graphics.Color.parseColor(act.color))
                        } catch (e: Exception) {
                            SkyBlue
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(SkyCloud)
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(color),
                                contentAlignment = Alignment.Center
                            ) {
                                ActivityIcon(
                                    iconKey = act.icon,
                                    tint = Color.White,
                                    size = 20.dp
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Text(
                                text = act.name,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SkyNavy
                                ),
                                modifier = Modifier.weight(1f)
                            )

                            if (activities.size > 1) {
                                IconButton(
                                    onClick = { onDeleteActivity(act.id) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = SkyTextMuted, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }

                    OutlinedButton(
                        onClick = onAddNewActivity,
                        modifier = Modifier.fillMaxWidth().height(44.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(2.dp, SkyMist)
                    ) {
                        Text("+ Add another activity", fontWeight = FontWeight.Bold, color = DeepSky)
                    }
                }
            }
        }

        // Section 3: Weekly Rhythm Target
        item {
            Text(
                text = "WEEKLY RHYTHM",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = SkyTextMuted
                )
            )
        }

        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(2.dp, SkyMist),
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Target sessions each week",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = SkyNavy
                            )
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { onWeeklyTargetChange(-1) },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SkyCloud)
                            ) {
                                Text("−", fontWeight = FontWeight.Black, fontSize = 18.sp, color = SkyNavy)
                            }

                            Text(
                                text = "${appSettings.weeklyTarget}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = SkyNavy
                                ),
                                modifier = Modifier.padding(horizontal = 14.dp)
                            )

                            IconButton(
                                onClick = { onWeeklyTargetChange(1) },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SkyCloud)
                            ) {
                                Text("+", fontWeight = FontWeight.Black, fontSize = 18.sp, color = SkyNavy)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Rest days are fine. Nothing carries over as a broken streak from week to week.",
                        style = MaterialTheme.typography.bodySmall.copy(color = SkyTextSubtle)
                    )
                }
            }
        }

        // Section 4: Mascot Options
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(2.dp, SkyMist)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Show Mascot",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = SkyNavy
                            )
                        )
                        Text(
                            text = "Friendly guidance and celebrations",
                            style = MaterialTheme.typography.bodySmall.copy(color = SkyTextSubtle)
                        )
                    }

                    Switch(
                        checked = !appSettings.isMascotQuiet,
                        onCheckedChange = { onToggleMascotQuiet() }
                    )
                }
            }
        }

        // Section 5: Example Data
        item {
            val hasExample = activities.any { it.isExample }
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(2.dp, SkyMist)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Example Data",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = SkyNavy
                        )
                    )
                    Text(
                        text = "Example data is isolated and labelled, never mixing with your real training.",
                        style = MaterialTheme.typography.bodySmall.copy(color = SkyTextSubtle)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    if (hasExample) {
                        OutlinedButton(
                            onClick = onRemoveExampleData,
                            modifier = Modifier.fillMaxWidth().height(44.dp),
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(2.dp, AccentCoral)
                        ) {
                            Text("Remove example data", fontWeight = FontWeight.Bold, color = AccentCoral)
                        }
                    } else {
                        Button(
                            onClick = onLoadExampleData,
                            modifier = Modifier.fillMaxWidth().height(44.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SkyCloud)
                        ) {
                            Text("Load example data", fontWeight = FontWeight.Bold, color = DeepSky)
                        }
                    }
                }
            }
        }

        // Section 6: Data Management & Backup
        item {
            Text(
                text = "YOUR DATA",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = SkyTextMuted
                )
            )
        }

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
                        text = "Everything stays private on your device. Export a backup anytime to keep your progress safe.",
                        style = MaterialTheme.typography.bodySmall.copy(color = SkyTextSubtle)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onExportBackup,
                            modifier = Modifier.weight(1f).height(44.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SkyCloud)
                        ) {
                            Text("Back up", fontWeight = FontWeight.Bold, color = DeepSky)
                        }

                        Button(
                            onClick = onRestoreBackup,
                            modifier = Modifier.weight(1f).height(44.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SkyCloud)
                        ) {
                            Text("Restore", fontWeight = FontWeight.Bold, color = DeepSky)
                        }
                    }

                    OutlinedButton(
                        onClick = { showWipeConfirm = true },
                        modifier = Modifier.fillMaxWidth().height(44.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(2.dp, AccentCoral)
                    ) {
                        Text("Delete everything", fontWeight = FontWeight.Bold, color = AccentCoral)
                    }
                }
            }
        }
    }
}

@Composable
private fun MoreNavButton(
    title: String,
    isPrimary: Boolean = false,
    onClick: () -> Unit
) {
    if (isPrimary) {
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SkyBlue)
        ) {
            Text(title, fontWeight = FontWeight.Black, color = DeepNavy)
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth().height(46.dp),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, SkyMist)
        ) {
            Text(title, fontWeight = FontWeight.Bold, color = if (SkyboundThemeState.isDark) Color(0xFF8FD3FA) else DeepSky)
        }
    }
}
