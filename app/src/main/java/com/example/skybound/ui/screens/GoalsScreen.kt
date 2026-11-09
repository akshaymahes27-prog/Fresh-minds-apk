package com.example.skybound.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skybound.data.ActivityEntity
import com.example.skybound.data.GoalEntity
import com.example.skybound.mascot.MascotPose
import com.example.skybound.mascot.MascotView
import com.example.skybound.model.GoalPriority
import com.example.skybound.ui.components.GoalItemCard
import com.example.ui.theme.*

@Composable
fun GoalsScreen(
    currentActivity: ActivityEntity?,
    goals: List<GoalEntity>,
    statusFilter: String,
    priorityFilter: String,
    onStatusFilterChange: (String) -> Unit,
    onPriorityFilterChange: (String) -> Unit,
    onGoalClick: (String) -> Unit,
    onToggleGoalDone: (String) -> Unit,
    onToggleGoalAim: (String) -> Unit,
    onAddGoal: () -> Unit,
    onRecordFeedback: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalCount = goals.size
    val doneCount = goals.count { it.completedAt != null }
    val activeCount = goals.count { it.completedAt == null && it.retiredAt == null }

    val filteredGoals = goals.filter { goal ->
        val matchesStatus = when (statusFilter) {
            "active" -> goal.completedAt == null && goal.retiredAt == null
            "done" -> goal.completedAt != null
            "retired" -> goal.retiredAt != null
            else -> true
        }
        val matchesPriority = when (priorityFilter) {
            "all" -> true
            else -> goal.priorityCode.equals(priorityFilter, ignoreCase = true)
        }
        matchesStatus && matchesPriority
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("goals_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Hero Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(2.dp, SkyMist),
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        MascotView(
                            pose = if (totalCount == 0) MascotPose.HI else if (activeCount == 0) MascotPose.PROUD else MascotPose.HAPPY,
                            size = 64.dp,
                            isInteractive = false
                        )

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            val headerTitle = when {
                                totalCount == 0 -> "Start with one goal"
                                activeCount == 0 -> "All caught up!"
                                else -> "$activeCount goal${if (activeCount == 1) "" else "s"} in progress"
                            }
                            Text(
                                text = headerTitle,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    color = SkyNavy
                                )
                            )
                            Text(
                                text = if (totalCount == 0) "Record feedback and Skybound turns it into goals." else "Tap the star on a goal to focus on it.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = SkyTextSubtle,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }

                    if (totalCount > 0) {
                        Spacer(modifier = Modifier.height(14.dp))
                        // Animated progress bar
                        val targetProgress = if (totalCount > 0) (doneCount.toFloat() / totalCount.toFloat()).coerceIn(0f, 1f) else 0f
                        val animatedProgress by androidx.compose.animation.core.animateFloatAsState(
                            targetValue = targetProgress,
                            animationSpec = androidx.compose.animation.core.spring(
                                dampingRatio = androidx.compose.animation.core.Spring.DampingRatioLowBouncy,
                                stiffness = androidx.compose.animation.core.Spring.StiffnessMediumLow
                            ),
                            label = "goals_progress"
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(14.dp)
                                .clip(RoundedCornerShape(99.dp))
                                .background(SkyMist)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(animatedProgress)
                                    .clip(RoundedCornerShape(99.dp))
                                    .background(if (doneCount == totalCount) AccentMint else SkyBlue)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "$doneCount of $totalCount finished",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SkyNavy
                                )
                            )
                            Text(
                                text = currentActivity?.name ?: "",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SkyTextMuted
                                )
                            )
                        }
                    }
                }
            }
        }

        // Status Filter Chips
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    "active" to "Active",
                    "done" to "Done",
                    "retired" to "Retired",
                    "all" to "All"
                ).forEach { (key, label) ->
                    val isSelected = statusFilter == key
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onStatusFilterChange(key) },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) SkyBlue else SkyCloud,
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, if (isSelected) DeepSky else SkyMist)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isSelected) DeepNavy else SkyTextSubtle
                                )
                            )
                        }
                    }
                }
            }
        }

        // Priority Filter Chips
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    "all" to "Any Priority",
                    "H" to "High",
                    "M" to "Medium",
                    "L" to "Low"
                ).forEach { (key, label) ->
                    val isSelected = priorityFilter == key
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onPriorityFilterChange(key) },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) AccentSun else MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) AccentSun else SkyMist)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) DeepNavy else SkyNavy
                                )
                            )
                        }
                    }
                }
            }
        }

        // Goals List
        if (filteredGoals.isNotEmpty()) {
            items(filteredGoals, key = { it.id }) { goal ->
                GoalItemCard(
                    goal = goal,
                    onToggleDone = { onToggleGoalDone(goal.id) },
                    onToggleAim = { onToggleGoalAim(goal.id) },
                    onClick = { onGoalClick(goal.id) }
                )
            }

            item {
                Button(
                    onClick = onAddGoal,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SkyBlue)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = DeepNavy)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add a goal", fontWeight = FontWeight.Black, color = DeepNavy)
                }
            }
        } else {
            // Empty filtered state
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(2.dp, SkyMist)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        MascotView(pose = if (totalCount == 0) MascotPose.SLEEP else MascotPose.SAD, size = 80.dp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (totalCount == 0) "No goals yet" else "No goals match these filters",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = SkyNavy
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (totalCount == 0) "Record coach feedback to turn advice into goals." else "Try changing your status or priority filters.",
                            style = MaterialTheme.typography.bodySmall.copy(color = SkyTextSubtle)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onRecordFeedback,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SkyBlue)
                        ) {
                            Text("Record feedback", fontWeight = FontWeight.Black, color = DeepNavy)
                        }
                    }
                }
            }
        }
    }
}
