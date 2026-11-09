package com.example.skybound.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skybound.data.*
import com.example.skybound.ui.components.GoalItemCard
import com.example.ui.theme.*

@Composable
fun HistoryScreen(
    currentActivity: ActivityEntity?,
    feedbackList: List<FeedbackEntity>,
    goals: List<GoalEntity>,
    sessions: List<SessionEntity>,
    onGoalClick: (String) -> Unit,
    onToggleGoalDone: (String) -> Unit,
    onToggleGoalAim: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    val q = searchQuery.trim().lowercase()

    val matchingFeedback = feedbackList.filter {
        q.isEmpty() || it.note.lowercase().contains(q) || it.coachName.lowercase().contains(q)
    }

    val matchingGoals = goals.filter {
        q.isEmpty() || it.title.lowercase().contains(q) || it.nextAction.lowercase().contains(q)
    }

    val matchingSessions = sessions.filter {
        q.isEmpty() || it.title.lowercase().contains(q)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("history_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Search Input Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(2.dp, SkyMist),
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Search ${currentActivity?.name ?: "Activity"}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = SkyNavy
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search feedback, goals and sessions") },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = SkyTextMuted)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true
                    )
                }
            }
        }

        // 1. Feedback Section
        item {
            Text(
                text = "Feedback (${matchingFeedback.size})",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = SkyNavy
                )
            )
        }

        if (matchingFeedback.isNotEmpty()) {
            items(matchingFeedback) { fb ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, SkyMist),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        if (fb.coachName.isNotBlank()) {
                            Text(
                                text = "From ${fb.coachName}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = DeepSky)
                            )
                        }
                        Text(
                            text = fb.note,
                            style = MaterialTheme.typography.bodyMedium.copy(color = SkyNavy, fontWeight = FontWeight.Medium)
                        )
                    }
                }
            }
        } else {
            item {
                Text("No matching feedback.", style = MaterialTheme.typography.bodySmall.copy(color = SkyTextMuted))
            }
        }

        // 2. Goals Section
        item {
            Text(
                text = "Goals (${matchingGoals.size})",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = SkyNavy
                ),
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (matchingGoals.isNotEmpty()) {
            items(matchingGoals) { goal ->
                GoalItemCard(
                    goal = goal,
                    onToggleDone = { onToggleGoalDone(goal.id) },
                    onToggleAim = { onToggleGoalAim(goal.id) },
                    onClick = { onGoalClick(goal.id) }
                )
            }
        } else {
            item {
                Text("No matching goals.", style = MaterialTheme.typography.bodySmall.copy(color = SkyTextMuted))
            }
        }

        // 3. Sessions Section
        item {
            Text(
                text = "Sessions (${matchingSessions.size})",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = SkyNavy
                ),
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (matchingSessions.isNotEmpty()) {
            items(matchingSessions) { session ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, SkyMist),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = session.title,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = SkyNavy)
                        )
                        Text(
                            text = session.dateKey,
                            style = MaterialTheme.typography.bodySmall.copy(color = SkyTextMuted)
                        )
                    }
                }
            }
        } else {
            item {
                Text("No matching sessions.", style = MaterialTheme.typography.bodySmall.copy(color = SkyTextMuted))
            }
        }
    }
}
