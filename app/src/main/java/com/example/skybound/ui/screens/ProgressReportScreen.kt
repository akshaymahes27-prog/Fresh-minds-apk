package com.example.skybound.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skybound.data.*
import com.example.skybound.model.GoalPriority
import com.example.skybound.model.currentDateKey
import com.example.skybound.model.formatDateKey
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun ProgressReportScreen(
    activityName: String,
    goals: List<GoalEntity>,
    feedbackList: List<FeedbackEntity>,
    sessions: List<SessionEntity>,
    habits: List<HabitEntity>,
    records: List<RecordEntity>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var copiedToast by remember { mutableStateOf(false) }

    // Default: past 30 days
    val cal = Calendar.getInstance()
    cal.add(Calendar.DAY_OF_YEAR, -30)
    val defaultFromDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)

    var fromDate by remember { mutableStateOf(defaultFromDate) }
    var toDate by remember { mutableStateOf(currentDateKey()) }

    var incGoals by remember { mutableStateOf(true) }
    var incFeedback by remember { mutableStateOf(true) }
    var incSessions by remember { mutableStateOf(true) }
    var incHabits by remember { mutableStateOf(true) }
    var incRecords by remember { mutableStateOf(true) }

    // Generate formatted report text
    val reportText = remember(fromDate, toDate, incGoals, incFeedback, incSessions, incHabits, incRecords, goals, feedbackList, sessions, habits, records) {
        val lines = mutableListOf<String>()
        lines.add("Skybound Progress Report: $activityName")
        lines.add("${formatDateKey(fromDate)} to ${formatDateKey(toDate)}")
        lines.add("========================================\n")

        if (incGoals) {
            lines.add("GOALS:")
            val matchedGoals = goals.filter { it.createdAt >= 0 }
            if (matchedGoals.isNotEmpty()) {
                matchedGoals.forEach { g ->
                    val status = if (g.completedAt != null) "done" else if (g.retiredAt != null) "retired" else "active"
                    val pri = GoalPriority.fromCode(g.priorityCode).label
                    val successDesc = if (g.successDefinition.isNotBlank()) " · Success: ${g.successDefinition}" else ""
                    lines.add("• ${g.title} ($status, $pri priority)$successDesc")
                }
            } else {
                lines.add("- No goals in this period")
            }
            lines.add("")
        }

        if (incFeedback) {
            lines.add("FEEDBACK RECEIVED:")
            if (feedbackList.isNotEmpty()) {
                feedbackList.forEach { fb ->
                    val coach = if (fb.coachName.isNotBlank()) "from ${fb.coachName}" else "Coach note"
                    lines.add("• $coach: \"${fb.note}\"")
                }
            } else {
                lines.add("- No feedback recorded")
            }
            lines.add("")
        }

        if (incSessions) {
            lines.add("SESSIONS ATTENDED:")
            val attended = sessions.filter { it.dateKey in fromDate..toDate }
            if (attended.isNotEmpty()) {
                attended.forEach { s ->
                    lines.add("• ${formatDateKey(s.dateKey)}: ${s.title}${if (s.time.isNotBlank()) " at ${s.time}" else ""}")
                }
            } else {
                lines.add("- No sessions recorded in this period")
            }
            lines.add("")
        }

        if (incHabits) {
            lines.add("HABITS PRACTICED:")
            if (habits.isNotEmpty()) {
                habits.forEach { h ->
                    val logs = Converters.parseStringList(h.loggedDatesJson).count { it in fromDate..toDate }
                    lines.add("• ${h.title}: logged $logs times")
                }
            } else {
                lines.add("- No habits recorded")
            }
            lines.add("")
        }

        if (incRecords) {
            lines.add("PERSONAL BESTS & RECORDS:")
            val matchedRecs = records.filter { it.dateKey in fromDate..toDate }
            if (matchedRecs.isNotEmpty()) {
                matchedRecs.forEach { r ->
                    lines.add("• ${formatDateKey(r.dateKey)}: ${r.title}${if (r.note.isNotBlank()) " (${r.note})" else ""}")
                }
            } else {
                lines.add("- No records in this period")
            }
        }

        lines.joinToString("\n")
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
                    text = "Progress Report",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = SkyNavy
                    )
                )
            }
        }

        // Section Selection Card
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
                        text = "Customize Content",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, color = SkyNavy)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = fromDate,
                            onValueChange = { fromDate = it },
                            label = { Text("From") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = toDate,
                            onValueChange = { toDate = it },
                            label = { Text("To") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    Text(
                        text = "Include Sections:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = DeepSky)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterToggleChip("Goals", incGoals) { incGoals = !incGoals }
                        FilterToggleChip("Feedback", incFeedback) { incFeedback = !incFeedback }
                        FilterToggleChip("Sessions", incSessions) { incSessions = !incSessions }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterToggleChip("Habits", incHabits) { incHabits = !incHabits }
                        FilterToggleChip("Records", incRecords) { incRecords = !incRecords }
                    }
                }
            }
        }

        // Report Preview Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(2.dp, SkyMist)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Report Preview",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, color = SkyNavy)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SkyCloud,
                        border = androidx.compose.foundation.BorderStroke(1.dp, SkyMist),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = reportText,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                color = SkyNavy,
                                fontSize = 12.sp,
                                lineHeight = 18.sp
                            ),
                            modifier = Modifier.padding(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(reportText))
                                copiedToast = true
                            },
                            modifier = Modifier.weight(1f).height(46.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SkyBlue)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, tint = SkyNavy)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (copiedToast) "Copied!" else "Copy text", fontWeight = FontWeight.Black, color = SkyNavy)
                        }

                        Button(
                            onClick = {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, reportText)
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, "Share Skybound Progress Report")
                                context.startActivity(shareIntent)
                            },
                            modifier = Modifier.weight(1f).height(46.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SkyCloud)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = DeepSky)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share", fontWeight = FontWeight.Bold, color = DeepSky)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.FilterToggleChip(
    label: String,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Surface(
        modifier = Modifier
            .weight(1f)
            .height(36.dp)
            .clip(RoundedCornerShape(10.dp)),
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) SkyBlue else SkyCloud,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) DeepSky else SkyMist),
        onClick = onToggle
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) SkyNavy else Color(0xFF4A6584)
                )
            )
        }
    }
}
