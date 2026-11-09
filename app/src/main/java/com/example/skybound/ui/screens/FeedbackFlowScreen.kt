package com.example.skybound.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.skybound.mascot.MascotHint
import com.example.skybound.mascot.MascotPose
import com.example.skybound.mascot.MascotView
import com.example.skybound.model.GoalPriority
import com.example.ui.theme.*

@Composable
fun FeedbackFlowScreen(
    activityName: String,
    onComplete: (coach: String, note: String, goals: List<Pair<String, GoalPriority>>, aimSelection: Set<Int>) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var step by remember { mutableStateOf(0) }
    var coachInput by remember { mutableStateOf("") }
    var noteInput by remember { mutableStateOf("") }

    val goalItems = remember { mutableStateListOf("Warm up with dynamic drills", "Focus on clean footwork") }
    val goalPriorities = remember { mutableStateListOf(GoalPriority.MEDIUM, GoalPriority.MEDIUM) }
    var aimSelection by remember { mutableStateOf(setOf(0)) }

    val mascotPoses = listOf(
        MascotPose.WAVE,
        MascotPose.THINKING,
        MascotPose.THINKING,
        MascotPose.HAPPY,
        MascotPose.PROUD
    )

    val stepPrompts = listOf(
        "Who gave you this feedback? A coach, a teacher, or yourself.",
        "What did they say? Write it the way you heard it.",
        "Now turn it into goals. Starting each with a verb helps.",
        "Which goals matter most? Pick priorities and up to 3 for Current Aim.",
        "Nice work! Your feedback and goals are ready to soar."
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SkyCloud)
            .statusBarsPadding()
            .testTag("feedback_flow_screen")
    ) {
        // Top Header: Close button + Lesson Progress Bar
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

            // 5-segment or smooth progress bar
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(99.dp))
                    .background(SkyMist)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth((step + 1) / 5f)
                        .clip(RoundedCornerShape(99.dp))
                        .background(SkyBlue)
                )
            }
        }

        // Mascot Guidance Hint
        MascotHint(
            text = stepPrompts[step],
            pose = mascotPoses[step]
        )

        // Step Content Container
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            when (step) {
                0 -> {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(2.dp, SkyMist)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Who was it from? (optional)",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = SkyNavy)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = coachInput,
                                onValueChange = { coachInput = it },
                                placeholder = { Text("e.g. Coach Sam, Mr. Davies, or Self") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                singleLine = true
                            )
                        }
                    }
                }
                1 -> {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(2.dp, SkyMist)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "What did they say?",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = SkyNavy)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = noteInput,
                                onValueChange = { noteInput = it },
                                placeholder = { Text("e.g. Use your weaker foot more in drills and keep your chest upright...") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                minLines = 4
                            )
                        }
                    }
                }
                2 -> {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        goalItems.forEachIndexed { index, text ->
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(18.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = androidx.compose.foundation.BorderStroke(2.dp, SkyMist)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = "Goal ${index + 1}",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = SkyBlue)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    OutlinedTextField(
                                        value = text,
                                        onValueChange = { goalItems[index] = it },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                }
                            }
                        }

                        if (goalItems.size < 5) {
                            OutlinedButton(
                                onClick = {
                                    goalItems.add("")
                                    goalPriorities.add(GoalPriority.MEDIUM)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                border = androidx.compose.foundation.BorderStroke(2.dp, SkyMist)
                            ) {
                                Text("+ Add another goal", fontWeight = FontWeight.Bold, color = DeepSky)
                            }
                        }
                    }
                }
                3 -> {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        goalItems.forEachIndexed { index, title ->
                            if (title.isNotBlank()) {
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(18.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    border = androidx.compose.foundation.BorderStroke(2.dp, SkyMist)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text(
                                            text = title,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, color = SkyNavy)
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        // Priority segment
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            GoalPriority.values().forEach { p ->
                                                val isP = goalPriorities.getOrNull(index) == p
                                                Surface(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .height(34.dp)
                                                        .clip(RoundedCornerShape(10.dp))
                                                        .clickable {
                                                            if (index in goalPriorities.indices) {
                                                                goalPriorities[index] = p
                                                            }
                                                        },
                                                    shape = RoundedCornerShape(10.dp),
                                                    color = if (isP) SkyBlue else SkyCloud
                                                ) {
                                                    Box(contentAlignment = Alignment.Center) {
                                                        Text(p.label, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = SkyNavy)
                                                    }
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        // In Current Aim button
                                        val isAim = aimSelection.contains(index)
                                        OutlinedButton(
                                            onClick = {
                                                aimSelection = if (isAim) {
                                                    aimSelection - index
                                                } else {
                                                    if (aimSelection.size < 3) {
                                                        aimSelection + index
                                                    } else aimSelection
                                                }
                                            },
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(12.dp),
                                            colors = ButtonDefaults.outlinedButtonColors(
                                                containerColor = if (isAim) AccentSun.copy(alpha = 0.2f) else Color.Transparent
                                            ),
                                            border = androidx.compose.foundation.BorderStroke(
                                                1.5.dp,
                                                if (isAim) AccentSun else SkyMist
                                            )
                                        ) {
                                            Text(
                                                text = if (isAim) "★ In Current Aim" else "☆ Add to Current Aim",
                                                fontWeight = FontWeight.Bold,
                                                color = SkyNavy
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                4 -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        MascotView(pose = MascotPose.PROUD, size = 100.dp)
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Goals Created!",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = SkyNavy
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${goalItems.count { it.isNotBlank() }} goals added (${aimSelection.size} in your Current Aim).",
                            style = MaterialTheme.typography.bodyMedium.copy(color = SkyTextSubtle)
                        )
                    }
                }
            }
        }

        // Bottom Navigation Bar for Flow
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (step > 0 && step < 4) {
                    OutlinedButton(
                        onClick = { step-- },
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(2.dp, SkyMist)
                    ) {
                        Text("Back", fontWeight = FontWeight.Bold, color = DeepSky)
                    }
                }

                Button(
                    onClick = {
                        if (step < 4) {
                            step++
                        } else {
                            val validGoals = goalItems.filter { it.isNotBlank() }.mapIndexed { i, title ->
                                val pri = goalPriorities.getOrElse(i) { GoalPriority.MEDIUM }
                                title to pri
                            }
                            onComplete(coachInput, noteInput, validGoals, aimSelection)
                        }
                    },
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SkyBlue)
                ) {
                    Text(
                        text = if (step < 4) "Continue" else "Go to Home",
                        fontWeight = FontWeight.Black,
                        color = SkyNavy
                    )
                }
            }
        }
    }
}
