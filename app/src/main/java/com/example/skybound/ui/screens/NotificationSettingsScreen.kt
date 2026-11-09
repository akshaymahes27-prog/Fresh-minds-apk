package com.example.skybound.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.skybound.data.AppSettingsEntity
import com.example.ui.theme.*

@Composable
fun NotificationSettingsScreen(
    appSettings: AppSettingsEntity,
    onToggleMaster: (Boolean) -> Unit,
    onTogglePreSession: (Boolean) -> Unit,
    onTogglePostSession: (Boolean) -> Unit,
    onToggleDailyAim: (Boolean) -> Unit,
    onToggleQuietHours: (Boolean) -> Unit,
    onSendTestNotification: () -> Unit,
    onTriggerPreSessionReminder: () -> Unit,
    onTriggerPostSessionReminder: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else true
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasNotificationPermission = isGranted
        if (isGranted) {
            onToggleMaster(true)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("notification_settings_screen"),
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
                    text = "Gentle Notifications",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = SkyNavy
                    )
                )
            }
        }

        // Philosophy Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = SkyCloud,
                border = androidx.compose.foundation.BorderStroke(2.dp, SkyMist)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Skybound Notification Rules",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = SkyNavy
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• Never nag or shame you for a missed day.\n• Kind, encouraging reminders before & after training.\n• Quiet hours ensure peaceful sleep without disruptions.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF4A6584),
                            lineHeight = 19.sp
                        )
                    )
                }
            }
        }

        // Toggles Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(2.dp, SkyMist)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Master Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Enable Notifications",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, color = SkyNavy)
                            )
                            Text(
                                text = "Allow gentle reminders on your device",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF4A6584))
                            )
                        }

                        Switch(
                            checked = appSettings.notificationsEnabled && hasNotificationPermission,
                            onCheckedChange = { checked ->
                                if (checked && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotificationPermission) {
                                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                } else {
                                    onToggleMaster(checked)
                                }
                            }
                        )
                    }

                    Divider(color = SkyMist)

                    // Pre-session reminder
                    NotificationToggleRow(
                        title = "Pre-Session Reminder",
                        description = "Gentle prompt 1 hour before training with your focus goal",
                        checked = appSettings.preSessionReminderEnabled,
                        onCheckedChange = onTogglePreSession
                    )

                    // Post-session reflection
                    NotificationToggleRow(
                        title = "Post-Training Reflection",
                        description = "Short prompt to do a 2-minute review after practice",
                        checked = appSettings.postSessionReflectionEnabled,
                        onCheckedChange = onTogglePostSession
                    )

                    // Daily Aim Check-in
                    NotificationToggleRow(
                        title = "Daily Aim Check-in",
                        description = "Morning inspiration on today's step",
                        checked = appSettings.dailyAimReminderEnabled,
                        onCheckedChange = onToggleDailyAim
                    )

                    // Quiet Hours
                    NotificationToggleRow(
                        title = "Quiet Hours (21:00 – 08:00)",
                        description = "Silences all notifications so rest is never disturbed",
                        checked = appSettings.quietHoursEnabled,
                        onCheckedChange = onToggleQuietHours
                    )
                }
            }
        }

        // Test Triggers Card
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
                        text = "Instant Notification Test",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, color = SkyNavy)
                    )
                    Text(
                        text = "Test how notifications look and sound right now on your device:",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF4A6584))
                    )

                    Button(
                        onClick = onSendTestNotification,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SkyBlue)
                    ) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = SkyNavy)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Send Test Notification", fontWeight = FontWeight.Black, color = SkyNavy)
                    }

                    OutlinedButton(
                        onClick = onTriggerPreSessionReminder,
                        modifier = Modifier.fillMaxWidth().height(46.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, SkyMist)
                    ) {
                        Text("Simulate: Pre-Session Reminder", fontWeight = FontWeight.Bold, color = DeepSky)
                    }

                    OutlinedButton(
                        onClick = onTriggerPostSessionReminder,
                        modifier = Modifier.fillMaxWidth().height(46.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, SkyMist)
                    ) {
                        Text("Simulate: Post-Session Reflection", fontWeight = FontWeight.Bold, color = DeepSky)
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationToggleRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = SkyNavy)
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF4A6584))
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
