package com.example.skybound.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.skybound.data.*
import com.example.skybound.model.GoalPriority
import com.example.skybound.model.GoalStep
import com.example.skybound.model.formatDateKey
import com.example.ui.theme.*

@Composable
fun GoalDetailScreen(
    goal: GoalEntity,
    linkedFeedback: FeedbackEntity?,
    onBack: () -> Unit,
    onSaveDetails: (title: String, nextAction: String, successDef: String, priority: GoalPriority) -> Unit,
    onToggleAim: () -> Unit,
    onToggleStep: (Int) -> Unit,
    onAddStep: (String) -> Unit,
    onDeleteStep: (Int) -> Unit,
    onStartReview: () -> Unit,
    onReopenGoal: () -> Unit,
    onDeleteGoal: () -> Unit,
    modifier: Modifier = Modifier
) {
    var titleText by remember(goal.title) { mutableStateOf(goal.title) }
    var nextActionText by remember(goal.nextAction) { mutableStateOf(goal.nextAction) }
    var successText by remember(goal.successDefinition) { mutableStateOf(goal.successDefinition) }
    var selectedPriority by remember(goal.priorityCode) { mutableStateOf(GoalPriority.fromCode(goal.priorityCode)) }
    var newStepInput by remember { mutableStateOf("") }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    val steps = Converters.parseSteps(goal.stepsJson)
    val timeline = Converters.parseTimeline(goal.timelineJson)

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete goal?", fontWeight = FontWeight.Black) },
            text = { Text("This goal, its steps, and its timeline will be permanently deleted.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirm = false
                        onDeleteGoal()
                    }
                ) {
                    Text("Delete", color = AccentCoral, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("goal_detail_screen"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Back Button & Navigation
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = SkyNavy)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Goal Details",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = SkyNavy
                    )
                )
            }
        }

        // Card 1: Core Goal Fields
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
                        text = "Goal",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = SkyTextSubtle)
                    )
                    OutlinedTextField(
                        value = titleText,
                        onValueChange = { titleText = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = false,
                        maxLines = 3
                    )

                    Text(
                        text = "Next Action",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = SkyTextSubtle)
                    )
                    OutlinedTextField(
                        value = nextActionText,
                        onValueChange = { nextActionText = it },
                        placeholder = { Text("One practical first step to start") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true
                    )

                    Text(
                        text = "What does success look like?",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = SkyTextSubtle)
                    )
                    OutlinedTextField(
                        value = successText,
                        onValueChange = { successText = it },
                        placeholder = { Text("Describe in your own words. Skybound never judges it.") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        minLines = 3
                    )

                    Text(
                        text = "Priority",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = SkyTextSubtle)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        GoalPriority.values().forEach { pri ->
                            val isSel = selectedPriority == pri
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { selectedPriority = pri },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSel) SkyBlue else SkyCloud,
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, if (isSel) DeepSky else SkyMist)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = pri.label,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Black,
                                            color = if (isSel) SkyNavy else SkyTextSubtle
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // In Current Aim Switch
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SkyCloud)
                            .clickable(onClick = onToggleAim)
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "In Current Aim (Focus)",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = SkyNavy)
                        )
                        Switch(
                            checked = goal.isInCurrentAim,
                            onCheckedChange = { onToggleAim() }
                        )
                    }

                    Button(
                        onClick = {
                            if (titleText.isNotBlank()) {
                                onSaveDetails(titleText.trim(), nextActionText.trim(), successText.trim(), selectedPriority)
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SkyBlue)
                    ) {
                        Text("Save Details", fontWeight = FontWeight.Black, color = SkyNavy)
                    }
                }
            }
        }

        // Card 2: Steps Checklist
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
                        text = "Steps Checklist",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, color = SkyNavy)
                    )

                    if (steps.isNotEmpty()) {
                        val doneCount = steps.count { it.isDone }
                        // Progress bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(99.dp))
                                .background(SkyMist)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(doneCount.toFloat() / steps.size.toFloat())
                                    .clip(RoundedCornerShape(99.dp))
                                    .background(if (doneCount == steps.size) AccentMint else SkyBlue)
                            )
                        }

                        steps.forEachIndexed { index, step ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SkyCloud)
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                    .background(if (step.isDone) AccentMint else Color.Transparent)
                                    .border(2.dp, if (step.isDone) AccentMint else SkyMist, CircleShape)
                                    .clickable { onToggleStep(index) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (step.isDone) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = SkyNavy, modifier = Modifier.size(18.dp))
                                    }
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Text(
                                    text = step.title,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (step.isDone) SkyTextMuted else SkyNavy
                                    ),
                                    modifier = Modifier.weight(1f)
                                )

                                IconButton(
                                    onClick = { onDeleteStep(index) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete step", tint = SkyTextMuted, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "Break this goal into small steps if that helps.",
                            style = MaterialTheme.typography.bodySmall.copy(color = SkyTextSubtle)
                        )
                    }

                    // Add new step
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = newStepInput,
                            onValueChange = { newStepInput = it },
                            placeholder = { Text("Add a step...") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                        IconButton(
                            onClick = {
                                if (newStepInput.isNotBlank()) {
                                    onAddStep(newStepInput.trim())
                                    newStepInput = ""
                                }
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(SkyBlue)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add step", tint = SkyNavy)
                        }
                    }
                }
            }
        }

        // Card 3: Feedback link (if exists)
        if (linkedFeedback != null) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(2.dp, SkyMist)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Origin Feedback",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, color = SkyNavy)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SkyCloud,
                            border = androidx.compose.foundation.BorderStroke(1.dp, SkyMist),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = if (linkedFeedback.coachName.isNotBlank()) "Coach ${linkedFeedback.coachName}" else "Note",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = DeepSky)
                                )
                                Text(
                                    text = linkedFeedback.note,
                                    style = MaterialTheme.typography.bodyMedium.copy(color = SkyNavy)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Card 4: Timeline
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(2.dp, SkyMist)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Timeline History",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, color = SkyNavy)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    timeline.forEach { entry ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(SkyBlue)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = entry.description,
                                style = MaterialTheme.typography.bodyMedium.copy(color = SkyNavy),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // Actions: Review, Reopen, Delete
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (goal.retiredAt != null) {
                    Button(
                        onClick = onReopenGoal,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentMint)
                    ) {
                        Text("Reopen Goal", fontWeight = FontWeight.Black, color = SkyNavy)
                    }
                } else {
                    Button(
                        onClick = onStartReview,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentViolet)
                    ) {
                        Text("Review Goal", fontWeight = FontWeight.Black, color = SkyNavy)
                    }
                }

                OutlinedButton(
                    onClick = { showDeleteConfirm = true },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(2.dp, AccentCoral)
                ) {
                    Text("Delete Goal", fontWeight = FontWeight.Bold, color = AccentCoral)
                }
            }
        }
    }
}
