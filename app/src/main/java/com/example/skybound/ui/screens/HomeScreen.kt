package com.example.skybound.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skybound.data.*
import com.example.skybound.mascot.MascotHint
import com.example.skybound.mascot.MascotPose
import com.example.skybound.mascot.MascotView
import com.example.skybound.model.currentDateKey
import com.example.skybound.model.formatDateKey
import com.example.skybound.ui.components.GoalItemCard
import com.example.ui.theme.*
import java.util.Calendar

@Composable
fun HomeScreen(
    currentActivity: ActivityEntity?,
    currentAimGoals: List<GoalEntity>,
    feedbackList: List<FeedbackEntity>,
    sessions: List<SessionEntity>,
    habits: List<HabitEntity>,
    weeklyTarget: Int,
    isTeenTechBannerDismissed: Boolean,
    isMascotQuiet: Boolean,
    onGoalClick: (String) -> Unit,
    onToggleGoalDone: (String) -> Unit,
    onToggleGoalAim: (String) -> Unit,
    onLogTodayStep: (String) -> Unit,
    onReviewCurrentAim: () -> Unit,
    onRecordFeedback: () -> Unit,
    onScheduleSession: () -> Unit,
    onAddHabit: () -> Unit,
    onToggleHabitLog: (String, String) -> Unit,
    onToggleSessionDate: (String, String) -> Unit,
    onStartTour: () -> Unit,
    onDismissTourBanner: () -> Unit,
    onOpenGoalsTab: () -> Unit,
    modifier: Modifier = Modifier
) {
    val today = currentDateKey()
    val topGoal = currentAimGoals.firstOrNull()

    // Greeting logic based on time
    val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val (greetingText, mascotPose) = when {
        currentHour in 5..11 -> "Good morning. One small step on your Current Aim is plenty today." to MascotPose.WAVE
        currentHour in 12..17 -> "Good afternoon. Keep it gentle and keep building progress." to MascotPose.HAPPY
        currentHour in 18..21 -> "Good evening. Reflecting on today's practice is how progress sticks." to MascotPose.PROUD
        else -> "Getting late. A good night's rest is part of training." to MascotPose.TIRED
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Time-aware Mascot Hint
        if (!isMascotQuiet) {
            item {
                MascotHint(
                    text = greetingText,
                    pose = mascotPose
                )
            }
        }

        // 2. Today's Step Card (Actionable card right at the top)
        if (topGoal != null) {
            val stepAction = if (topGoal.nextAction.isNotBlank()) topGoal.nextAction else topGoal.title
            val hasDoneToday = Converters.parseTimeline(topGoal.timelineJson).any {
                it.description.startsWith("Did: ")
            }

            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(2.dp, SkyMist),
                    shadowElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "TODAY'S STEP",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = SkyBlue,
                                fontSize = 11.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = stepAction,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 19.sp,
                                color = SkyNavy
                            )
                        )

                        Text(
                            text = "For: ${topGoal.title}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = SkyTextSubtle,
                                fontWeight = FontWeight.SemiBold
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 2.dp)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (hasDoneToday) {
                                OutlinedButton(
                                    onClick = {},
                                    enabled = false,
                                    modifier = Modifier.weight(1f).height(44.dp),
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Text("Done today! Nice.", fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Button(
                                    onClick = { onLogTodayStep(topGoal.id) },
                                    modifier = Modifier.weight(1f).height(44.dp),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = SkyBlue)
                                ) {
                                    Text("I did it", fontWeight = FontWeight.Black, color = DeepNavy)
                                }
                            }

                            OutlinedButton(
                                onClick = { onGoalClick(topGoal.id) },
                                modifier = Modifier.weight(1f).height(44.dp),
                                shape = RoundedCornerShape(14.dp),
                                border = androidx.compose.foundation.BorderStroke(2.dp, SkyMist)
                            ) {
                                Text("Open goal", fontWeight = FontWeight.Bold, color = DeepSky)
                            }
                        }
                    }
                }
            }
        }

        // 3. TeenTech Tour CTA Banner (Dismissible)
        if (!isTeenTechBannerDismissed) {
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(2.dp, SkyMist),
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(AccentSun)
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable(onClick = onStartTour)
                        ) {
                            Text(
                                text = "TeenTech judge? Take the tour",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp,
                                    color = SkyNavy
                                )
                            )
                            Text(
                                text = "2 minutes, every feature & design decision",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = SkyTextSubtle,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }

                        Button(
                            onClick = onStartTour,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SkyBlue),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("Start →", fontWeight = FontWeight.Black, color = DeepNavy, fontSize = 13.sp)
                        }

                        IconButton(
                            onClick = onDismissTourBanner,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = SkyTextMuted, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }

        // 4. Current Aim Section (1-3 focus goals)
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(2.dp, SkyMist),
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Current Aim",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = SkyNavy
                            )
                        )

                        Text(
                            text = "${currentAimGoals.size}/3 focus",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = SkyBlue
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (currentAimGoals.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            currentAimGoals.forEach { goal ->
                                GoalItemCard(
                                    goal = goal,
                                    onToggleDone = { onToggleGoalDone(goal.id) },
                                    onToggleAim = { onToggleGoalAim(goal.id) },
                                    onClick = { onGoalClick(goal.id) },
                                    showAimStar = false
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Review Current Aim Button
                        Button(
                            onClick = onReviewCurrentAim,
                            modifier = Modifier.fillMaxWidth().height(46.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SkyCloud)
                        ) {
                            Icon(Icons.Default.RateReview, contentDescription = null, tint = DeepSky)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Review my Current Aim",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DeepSky
                                )
                            )
                        }
                    } else {
                        // Empty Current Aim state
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            MascotView(pose = MascotPose.THINKING, size = 72.dp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No focus goals selected yet.",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SkyTextSubtle
                                )
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = onOpenGoalsTab,
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = SkyBlue)
                            ) {
                                Text("Choose focus from Goals", fontWeight = FontWeight.Black, color = DeepNavy)
                            }
                        }
                    }
                }
            }
        }

        // 5. Latest Feedback Card
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(2.dp, SkyMist),
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Latest feedback",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = SkyNavy
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val latestFb = feedbackList.firstOrNull()
                    if (latestFb != null) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = SkyCloud,
                            border = androidx.compose.foundation.BorderStroke(1.dp, SkyMist),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = if (latestFb.coachName.isNotBlank()) "From ${latestFb.coachName}" else "Feedback note",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DeepSky
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = latestFb.note,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = SkyNavy,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "No feedback recorded yet. Turn advice into progress in under a minute.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = SkyTextSubtle
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = onRecordFeedback,
                        modifier = Modifier.fillMaxWidth().height(46.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(2.dp, SkyMist)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = DeepSky)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Record feedback", fontWeight = FontWeight.Bold, color = DeepSky)
                    }
                }
            }
        }

        // 6. Next Session Card
        val upcomingSession = sessions.filter { it.dateKey >= today }.minByOrNull { it.dateKey }
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(2.dp, SkyMist),
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Next session",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = SkyNavy
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (upcomingSession != null) {
                        val isDone = Converters.parseStringList(upcomingSession.completedDatesJson).contains(upcomingSession.dateKey)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = upcomingSession.title,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = SkyNavy
                                    )
                                )
                                Text(
                                    text = "${formatDateKey(upcomingSession.dateKey)}${if (upcomingSession.time.isNotBlank()) " at ${upcomingSession.time}" else ""}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = SkyTextSubtle,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }

                            Button(
                                onClick = { onToggleSessionDate(upcomingSession.id, upcomingSession.dateKey) },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isDone) AccentMint else SkyCloud
                                )
                            ) {
                                Text(
                                    text = if (isDone) "Done ✓" else "Mark Done",
                                    color = if (isDone) DeepNavy else SkyNavy,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "No upcoming sessions planned.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = SkyTextSubtle
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedButton(
                            onClick = onScheduleSession,
                            modifier = Modifier.fillMaxWidth().height(46.dp),
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(2.dp, SkyMist)
                        ) {
                            Text("Schedule a session", fontWeight = FontWeight.Bold, color = DeepSky)
                        }
                    }
                }
            }
        }

        // 7. This Week & Habits Section
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(2.dp, SkyMist),
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "This week",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = SkyNavy
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // MTWTFSS days circles
                    val daysLabels = listOf("M", "T", "W", "T", "F", "S", "S")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        daysLabels.forEachIndexed { i, label ->
                            val isToday = (i == (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) + 5) % 7)
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(SkyCloud)
                                        .then(
                                            if (isToday) Modifier.border(2.5.dp, SkyBlue, CircleShape)
                                             else Modifier
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Black,
                                            color = if (isToday) SkyBlue else SkyTextMuted
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Habits List
                    if (habits.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            habits.forEach { habit ->
                                val logs = Converters.parseStringList(habit.loggedDatesJson)
                                val isLoggedToday = logs.contains(today)

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(SkyCloud)
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(if (isLoggedToday) AccentMint else Color.Transparent)
                                            .border(2.5.dp, if (isLoggedToday) AccentMint else SkyMist, CircleShape)
                                            .clickable { onToggleHabitLog(habit.id, today) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isLoggedToday) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = DeepNavy, modifier = Modifier.size(18.dp))
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = habit.title,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = SkyNavy
                                            )
                                        )
                                        Text(
                                            text = "${logs.size}/${habit.targetDaysPerWeek} target this week",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = SkyTextSubtle
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = onAddHabit,
                        modifier = Modifier.fillMaxWidth().height(44.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(2.dp, SkyMist)
                    ) {
                        Text("Add a habit", fontWeight = FontWeight.Bold, color = DeepSky)
                    }
                }
            }
        }
    }
}
