package com.example.skybound.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import com.example.skybound.data.GoalEntity
import com.example.skybound.mascot.MascotHint
import com.example.skybound.mascot.MascotPose
import com.example.ui.theme.*

@Composable
fun ReviewFlowScreen(
    goal: GoalEntity,
    onReviewAction: (action: String, newValue: String?, note: String?) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var noteText by remember { mutableStateOf("") }
    var editMode by remember { mutableStateOf<String?>(null) } // "edit", "shrink", "change"
    var newGoalText by remember { mutableStateOf(goal.title) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SkyCloud)
            .statusBarsPadding()
            .testTag("review_flow_screen")
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = SkyNavy)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Review Goal",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    color = SkyNavy
                )
            )
        }

        MascotHint(
            text = "Reflecting after training helps progress stick. Small tweaks beat big restarts.",
            pose = MascotPose.HAPPY
        )

        Spacer(modifier = Modifier.height(10.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(2.dp, SkyMist)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Goal under review",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = DeepSky)
                )
                Text(
                    text = goal.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = SkyNavy
                    )
                )

                if (editMode == null) {
                    Text(
                        text = "How did it go? (optional note)",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF4A6584))
                    )
                    OutlinedTextField(
                        value = noteText,
                        onValueChange = { noteText = it },
                        placeholder = { Text("What felt good? What needs tuning?") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        minLines = 2
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Review Action Buttons
                    Button(
                        onClick = { onReviewAction("keep", null, noteText) },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SkyBlue)
                    ) {
                        Text("Keep going as-is", fontWeight = FontWeight.Black, color = SkyNavy)
                    }

                    OutlinedButton(
                        onClick = {
                            newGoalText = goal.title
                            editMode = "edit"
                        },
                        modifier = Modifier.fillMaxWidth().height(46.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(2.dp, SkyMist)
                    ) {
                        Text("Reword it", fontWeight = FontWeight.Bold, color = DeepSky)
                    }

                    OutlinedButton(
                        onClick = {
                            newGoalText = goal.title
                            editMode = "shrink"
                        },
                        modifier = Modifier.fillMaxWidth().height(46.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(2.dp, SkyMist)
                    ) {
                        Text("Make it smaller", fontWeight = FontWeight.Bold, color = DeepSky)
                    }

                    OutlinedButton(
                        onClick = {
                            newGoalText = ""
                            editMode = "change"
                        },
                        modifier = Modifier.fillMaxWidth().height(46.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(2.dp, SkyMist)
                    ) {
                        Text("Swap for a new goal", fontWeight = FontWeight.Bold, color = DeepSky)
                    }

                    OutlinedButton(
                        onClick = { onReviewAction("retire", null, noteText) },
                        modifier = Modifier.fillMaxWidth().height(46.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(2.dp, AccentCoral)
                    ) {
                        Text("Retire goal", fontWeight = FontWeight.Bold, color = AccentCoral)
                    }
                } else {
                    // Prompt for reword / shrink / swap
                    val promptLabel = when (editMode) {
                        "shrink" -> "Make it smaller (an easier step)"
                        "change" -> "New goal to swap in"
                        else -> "Reword this goal"
                    }
                    Text(
                        text = promptLabel,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = SkyNavy)
                    )
                    OutlinedTextField(
                        value = newGoalText,
                        onValueChange = { newGoalText = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { editMode = null },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("Back", fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                if (newGoalText.isNotBlank()) {
                                    onReviewAction(editMode!!, newGoalText.trim(), noteText)
                                }
                            },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SkyBlue)
                        ) {
                            Text("Save", fontWeight = FontWeight.Black, color = SkyNavy)
                        }
                    }
                }
            }
        }
    }
}
