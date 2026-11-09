package com.example.skybound.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skybound.data.*
import com.example.skybound.model.currentDateKey
import com.example.skybound.model.formatDateKey
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun CalendarScreen(
    sessions: List<SessionEntity>,
    feedbackList: List<FeedbackEntity>,
    habits: List<HabitEntity>,
    onToggleSession: (String, String) -> Unit,
    onDeleteSession: (String) -> Unit,
    onScheduleSession: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCalendar by remember { mutableStateOf(Calendar.getInstance()) }
    var selectedDateKey by remember { mutableStateOf(currentDateKey()) }

    val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    val monthTitle = monthFormat.format(selectedCalendar.time)

    // Calculate calendar days
    val cal = selectedCalendar.clone() as Calendar
    cal.set(Calendar.DAY_OF_MONTH, 1)
    val firstDayOfWeek = (cal.get(Calendar.DAY_OF_WEEK) + 5) % 7 // Monday = 0
    val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

    val year = selectedCalendar.get(Calendar.YEAR)
    val month = selectedCalendar.get(Calendar.MONTH) + 1

    // Events on selected day
    val daySessions = sessions.filter { it.dateKey == selectedDateKey }
    val dayFeedback = feedbackList.filter {
        val fbDateKey = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it.createdAt)
        fbDateKey == selectedDateKey
    }
    val dayHabits = habits.filter {
        Converters.parseStringList(it.loggedDatesJson).contains(selectedDateKey)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("calendar_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Month Selector & Calendar Grid Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(2.dp, SkyMist),
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Month Navigation Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                val next = selectedCalendar.clone() as Calendar
                                next.add(Calendar.MONTH, -1)
                                selectedCalendar = next
                            }
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous month", tint = SkyNavy)
                        }

                        Text(
                            text = monthTitle,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = SkyNavy
                            )
                        )

                        IconButton(
                            onClick = {
                                val next = selectedCalendar.clone() as Calendar
                                next.add(Calendar.MONTH, 1)
                                selectedCalendar = next
                            }
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next month", tint = SkyNavy)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Days of week header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        listOf("M", "T", "W", "T", "F", "S", "S").forEach { day ->
                            Text(
                                text = day,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SkyTextMuted
                                ),
                                modifier = Modifier.width(36.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Day Cells
                    val totalCells = firstDayOfWeek + daysInMonth
                    val rows = (totalCells + 6) / 7

                    for (row in 0 until rows) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            for (col in 0 until 7) {
                                val cellIndex = row * 7 + col
                                val dayNum = cellIndex - firstDayOfWeek + 1

                                if (dayNum in 1..daysInMonth) {
                                    val cellDateKey = String.format("%04d-%02d-%02d", year, month, dayNum)
                                    val isSelected = cellDateKey == selectedDateKey
                                    val isToday = cellDateKey == currentDateKey()

                                    val hasEvent = sessions.any { it.dateKey == cellDateKey } ||
                                            habits.any { Converters.parseStringList(it.loggedDatesJson).contains(cellDateKey) }

                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                if (isSelected) SkyCloud else Color.Transparent
                                            )
                                            .border(
                                                width = if (isSelected) 2.dp else if (isToday) 1.5.dp else 0.dp,
                                                color = if (isSelected) SkyBlue else if (isToday) SkyTextMuted else Color.Transparent,
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .clickable { selectedDateKey = cellDateKey },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = "$dayNum",
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = if (isSelected || isToday) FontWeight.Black else FontWeight.Bold,
                                                    color = SkyNavy
                                                )
                                            )
                                            if (hasEvent) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(5.dp)
                                                        .clip(CircleShape)
                                                        .background(SkyBlue)
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    Spacer(modifier = Modifier.size(42.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Selected Day Details Card
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
                    Text(
                        text = formatDateKey(selectedDateKey),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = SkyNavy
                        )
                    )

                    // Scheduled Sessions on selected day
                    if (daySessions.isNotEmpty()) {
                        Text(
                            text = "Sessions",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = DeepSky)
                        )
                        daySessions.forEach { session ->
                            val isDone = Converters.parseStringList(session.completedDatesJson).contains(selectedDateKey)
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
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(if (isDone) AccentMint else Color.Transparent)
                                        .border(2.dp, if (isDone) AccentMint else SkyMist, CircleShape)
                                        .clickable { onToggleSession(session.id, selectedDateKey) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isDone) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = SkyNavy, modifier = Modifier.size(18.dp))
                                    }
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = session.title,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = SkyNavy)
                                    )
                                    if (session.time.isNotBlank()) {
                                        Text(
                                            text = "Time: ${session.time}",
                                            style = MaterialTheme.typography.bodySmall.copy(color = SkyTextSubtle)
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = { onDeleteSession(session.id) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = SkyTextMuted, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }

                    // Feedback on selected day
                    if (dayFeedback.isNotEmpty()) {
                        Text(
                            text = "Feedback Logged",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = DeepSky)
                        )
                        dayFeedback.forEach { fb ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = SkyCloud,
                                border = androidx.compose.foundation.BorderStroke(1.dp, SkyMist),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    if (fb.coachName.isNotBlank()) {
                                        Text("From ${fb.coachName}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DeepSky)
                                    }
                                    Text(fb.note, style = MaterialTheme.typography.bodySmall.copy(color = SkyNavy))
                                }
                            }
                        }
                    }

                    // Habits logged on selected day
                    if (dayHabits.isNotEmpty()) {
                        Text(
                            text = "Habits Practiced",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = DeepSky)
                        )
                        dayHabits.forEach { habit ->
                            Text(
                                text = "✓ ${habit.title}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = SkyNavy)
                            )
                        }
                    }

                    if (daySessions.isEmpty() && dayFeedback.isEmpty() && dayHabits.isEmpty()) {
                        Text(
                            text = "Nothing logged on this day.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = SkyTextSubtle)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = onScheduleSession,
                        modifier = Modifier.fillMaxWidth().height(46.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SkyBlue)
                    ) {
                        Text("Schedule a session", fontWeight = FontWeight.Black, color = SkyNavy)
                    }
                }
            }
        }
    }
}
