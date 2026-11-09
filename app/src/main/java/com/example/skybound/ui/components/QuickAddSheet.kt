package com.example.skybound.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickAddSheet(
    onRecordFeedback: () -> Unit,
    onAddGoalOnly: () -> Unit,
    onScheduleSession: () -> Unit,
    onAddHabit: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Add to Skybound",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    color = SkyNavy
                ),
                modifier = Modifier.padding(bottom = 6.dp)
            )

            // Option 1: Record Feedback (Primary action)
            Button(
                onClick = {
                    onDismiss()
                    onRecordFeedback()
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SkyBlue)
            ) {
                Icon(Icons.Default.ChatBubbleOutline, contentDescription = null, tint = SkyNavy)
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Record feedback",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = SkyNavy
                    )
                )
            }

            // Option 2: Just a goal
            OutlinedButton(
                onClick = {
                    onDismiss()
                    onAddGoalOnly()
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(2.dp, SkyMist)
            ) {
                Icon(Icons.Default.Flag, contentDescription = null, tint = DeepSky)
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Just a goal",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = DeepSky
                    )
                )
            }

            // Option 3: Schedule a session
            OutlinedButton(
                onClick = {
                    onDismiss()
                    onScheduleSession()
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(2.dp, SkyMist)
            ) {
                Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = AccentViolet)
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Schedule a session",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = SkyNavy
                    )
                )
            }

            // Option 4: Add a habit
            OutlinedButton(
                onClick = {
                    onDismiss()
                    onAddHabit()
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(2.dp, SkyMist)
            ) {
                Icon(Icons.Default.Repeat, contentDescription = null, tint = AccentOrange)
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Add a habit",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = SkyNavy
                    )
                )
            }
        }
    }
}
