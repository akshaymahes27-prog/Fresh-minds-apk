package com.example.skybound.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.skybound.data.AppSettingsEntity
import com.example.skybound.mascot.MascotPose
import com.example.skybound.mascot.MascotView
import com.example.skybound.notification.NotificationBrandingConfig
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
            Toast.makeText(context, "Notification permission granted!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Notification permission is required for alerts", Toast.LENGTH_SHORT).show()
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

        // Branded Mascot Banner Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                color = SkyCloud,
                border = androidx.compose.foundation.BorderStroke(2.dp, SkyMist),
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MascotView(
                        pose = MascotPose.WAVE,
                        size = 56.dp,
                        animateBob = true,
                        isInteractive = true
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${NotificationBrandingConfig.MASCOT_NAME} · Branded Alerts",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = SkyNavy
                            )
                        )
                        Text(
                            text = "Custom notifications with ${NotificationBrandingConfig.MASCOT_NAME}'s avatar, non-nagging prompts, and guilt-free encouragement.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SkyTextSubtle,
                                lineHeight = 18.sp
                            )
                        )
                    }
                }
            }
        }

        // Instant Test Notification Card (Prominent User Request!)
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(2.dp, SkyMist),
                shadowElevation = 3.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = SkyBlue,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Test Notifications Now",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, color = SkyNavy)
                        )
                    }

                    Text(
                        text = "Trigger an instant branded notification to test how it looks, sounds, and appears in your Android notification drawer:",
                        style = MaterialTheme.typography.bodySmall.copy(color = SkyTextSubtle)
                    )

                    Button(
                        onClick = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotificationPermission) {
                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                onSendTestNotification()
                                Toast.makeText(context, "🔔 Branded Pip notification sent! Check drawer.", Toast.LENGTH_LONG).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SkyBlue)
                    ) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = DeepNavy)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Send Test Notification", fontWeight = FontWeight.Black, color = DeepNavy)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotificationPermission) {
                                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                } else {
                                    onTriggerPreSessionReminder()
                                    Toast.makeText(context, "Pre-session alert sent!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.weight(1f).height(42.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, SkyMist)
                        ) {
                            Text("Pre-Session", fontWeight = FontWeight.Bold, color = SkyInteractiveText, fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotificationPermission) {
                                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                } else {
                                    onTriggerPostSessionReminder()
                                    Toast.makeText(context, "Post-session alert sent!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.weight(1f).height(42.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, SkyMist)
                        ) {
                            Text("Post-Session", fontWeight = FontWeight.Bold, color = SkyInteractiveText, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Notification Philosophy Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
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
                            color = SkyTextSubtle,
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
                                style = MaterialTheme.typography.bodySmall.copy(color = SkyTextSubtle)
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
                        title = "Pre-Session Focus Cue",
                        description = "Gentle prompt 30m before scheduled sessions with your Current Aim",
                        checked = appSettings.preSessionReminderEnabled,
                        onCheckedChange = onTogglePreSession
                    )

                    Divider(color = SkyMist)

                    // Post-session reflection
                    NotificationToggleRow(
                        title = "Post-Session Reflection",
                        description = "Quick prompt 15m after sessions for a 2-minute review",
                        checked = appSettings.postSessionReflectionEnabled,
                        onCheckedChange = onTogglePostSession
                    )

                    Divider(color = SkyMist)

                    // Daily aim nudge
                    NotificationToggleRow(
                        title = "Daily Aim Nudge",
                        description = "One morning reminder of your current focus step",
                        checked = appSettings.dailyAimReminderEnabled,
                        onCheckedChange = onToggleDailyAim
                    )

                    Divider(color = SkyMist)

                    // Quiet hours
                    NotificationToggleRow(
                        title = "Quiet Hours (21:00 - 08:00)",
                        description = "Silence notifications overnight so sleep remains uninterrupted",
                        checked = appSettings.quietHoursEnabled,
                        onCheckedChange = onToggleQuietHours
                    )

                    if (appSettings.quietHoursEnabled) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(SkyCloud),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = com.example.R.drawable.scene_quiet_hours),
                                contentDescription = "Quiet hours peaceful sleep",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .padding(vertical = 6.dp)
                            )
                        }
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
                style = MaterialTheme.typography.bodySmall.copy(color = SkyTextSubtle)
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
