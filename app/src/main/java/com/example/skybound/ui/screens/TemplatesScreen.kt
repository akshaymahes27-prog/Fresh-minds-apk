package com.example.skybound.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skybound.model.GoalPriority
import com.example.ui.theme.*

@Composable
fun TemplatesScreen(
    activityName: String,
    onUseTemplate: (String, GoalPriority) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val templatesMap = mapOf(
        "Sport" to listOf(
            "Warm up properly before every session",
            "Work on my weaker side in drills",
            "Review one match or session each week",
            "Build my fitness and stamina base"
        ),
        "Music" to listOf(
            "Practise scales for 10 minutes daily",
            "Learn one tricky bar slowly with metronome",
            "Play along with a backing recording",
            "Record myself and listen back"
        ),
        "Gym" to listOf(
            "Fix my form on one core lift",
            "Dynamic stretch after every session",
            "Add a small progressive weight increase",
            "Plan my training splits for the week"
        ),
        "Dance" to listOf(
            "Drill one movement until it is crisp and clean",
            "Practise to counts, not just muscle feel",
            "Record a run-through and review posture",
            "Dedicated 15 min flexibility routine"
        ),
        "Study" to listOf(
            "Do one 25-minute uninterrupted focus block",
            "Summarise key topic from memory without looking",
            "Practise 3 past exam questions",
            "Plan weekly revision schedule"
        ),
        "General / Growth" to listOf(
            "Pick one specific thing to practise daily",
            "Ask my coach or teacher for feedback this week",
            "Break a big goal into 3 bite-sized steps",
            "Reflect on what felt good after each session"
        )
    )

    var templateToUse by remember { mutableStateOf<String?>(null) }
    var editedText by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf(GoalPriority.MEDIUM) }

    if (templateToUse != null) {
        AlertDialog(
            onDismissRequest = { templateToUse = null },
            title = { Text("Use Template for $activityName", fontWeight = FontWeight.Black) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Edit goal text before saving:",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF4A6584))
                    )
                    OutlinedTextField(
                        value = editedText,
                        onValueChange = { editedText = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Text(
                        text = "Priority:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = DeepSky)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        GoalPriority.values().forEach { pri ->
                            val isSel = selectedPriority == pri
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { selectedPriority = pri },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSel) SkyBlue else SkyCloud
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(pri.label, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = SkyNavy)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editedText.isNotBlank()) {
                            onUseTemplate(editedText.trim(), selectedPriority)
                            templateToUse = null
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SkyBlue)
                ) {
                    Text("Add Goal", fontWeight = FontWeight.Black, color = SkyNavy)
                }
            },
            dismissButton = {
                TextButton(onClick = { templateToUse = null }) {
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
                    text = "Goal Templates",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = SkyNavy
                    )
                )
            }
        }

        templatesMap.forEach { (category, list) ->
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(2.dp, SkyMist)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = category,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = SkyNavy
                            )
                        )

                        list.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SkyCloud)
                                    .clickable {
                                        editedText = item
                                        templateToUse = item
                                    }
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = item,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = SkyNavy
                                    ),
                                    modifier = Modifier.weight(1f)
                                )

                                Text(
                                    text = "Use →",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        color = DeepSky
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
