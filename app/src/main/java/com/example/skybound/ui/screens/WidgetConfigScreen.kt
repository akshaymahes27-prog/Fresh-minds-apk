package com.example.skybound.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skybound.data.ActivityEntity
import com.example.skybound.data.GoalEntity
import com.example.skybound.model.AltitudeInfo
import com.example.skybound.widget.WidgetUpdater
import com.example.ui.theme.*

@Composable
fun WidgetConfigScreen(
    currentActivity: ActivityEntity?,
    altitudeInfo: AltitudeInfo,
    currentAimGoals: List<GoalEntity>,
    weeklyRhythmCount: Int,
    weeklyTarget: Int,
    nextSessionCountdown: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedSize by remember { mutableStateOf("small") } // "small", "medium"
    var refreshNotice by remember { mutableStateOf(false) }

    val activityName = currentActivity?.name ?: "Skybound"

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
                    text = "Home Screen Widgets",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = SkyNavy
                    )
                )
            }
        }

        // Size Toggle Segment
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(2.dp, SkyMist)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Widget Size Preview",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, color = SkyNavy)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { selectedSize = "small" },
                            shape = RoundedCornerShape(12.dp),
                            color = if (selectedSize == "small") SkyBlue else SkyCloud,
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, if (selectedSize == "small") DeepSky else SkyMist)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("Small (2x2)", fontWeight = FontWeight.Black, color = SkyNavy)
                            }
                        }

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { selectedSize = "medium" },
                            shape = RoundedCornerShape(12.dp),
                            color = if (selectedSize == "medium") SkyBlue else SkyCloud,
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, if (selectedSize == "medium") DeepSky else SkyMist)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("Medium (4x2)", fontWeight = FontWeight.Black, color = SkyNavy)
                            }
                        }
                    }
                }
            }
        }

        // Live Simulated Widget Preview on Simulated Home Wallpaper
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                color = Color(0xFF68B2E8), // Sky wallpaper background
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "HOME SCREEN PREVIEW",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    if (selectedSize == "small") {
                        // Small Widget Preview
                        Surface(
                            modifier = Modifier
                                .width(180.dp)
                                .height(170.dp),
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surface,
                            shadowElevation = 8.dp,
                            border = androidx.compose.foundation.BorderStroke(2.dp, SkyMist)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = activityName,
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Black,
                                            color = SkyNavy
                                        )
                                    )
                                    Text(
                                        text = "🔥 $weeklyRhythmCount/$weeklyTarget",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = AccentOrange
                                        )
                                    )
                                }

                                Text(
                                    text = "${altitudeInfo.currentRank.title} · ${altitudeInfo.totalFinished} done",
                                    style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF5B7794)),
                                    modifier = Modifier.padding(top = 2.dp)
                                )

                                Divider(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    color = SkyMist
                                )

                                Text(
                                    text = "CURRENT AIM",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        color = SkyBlue,
                                        fontSize = 10.sp
                                    )
                                )

                                val topGoal = currentAimGoals.firstOrNull()
                                if (topGoal != null) {
                                    Text(
                                        text = topGoal.title,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = SkyNavy
                                        ),
                                        maxLines = 2
                                    )
                                    if (topGoal.nextAction.isNotBlank()) {
                                        Text(
                                            text = "Next: ${topGoal.nextAction}",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = Color(0xFF4A6584)
                                            ),
                                            maxLines = 1
                                        )
                                    }
                                } else {
                                    Text(
                                        text = "No focus goals selected",
                                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF5B7794))
                                    )
                                }
                            }
                        }
                    } else {
                        // Medium Widget Preview
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp),
                            shape = RoundedCornerShape(22.dp),
                            color = MaterialTheme.colorScheme.surface,
                            shadowElevation = 8.dp,
                            border = androidx.compose.foundation.BorderStroke(2.dp, SkyMist)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Skybound · $activityName",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Black,
                                            color = SkyNavy
                                        )
                                    )
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(
                                            text = "🛡️ ${altitudeInfo.currentRank.title}",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = SkyBlue
                                            )
                                        )
                                        Text(
                                            text = "🔥 $weeklyRhythmCount/$weeklyTarget",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = AccentOrange
                                            )
                                        )
                                    }
                                }

                                Divider(modifier = Modifier.padding(vertical = 8.dp), color = SkyMist)

                                Text(
                                    text = "CURRENT AIM",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        color = SkyBlue,
                                        fontSize = 10.sp
                                    )
                                )

                                if (currentAimGoals.isNotEmpty()) {
                                    currentAimGoals.forEachIndexed { i, g ->
                                        Text(
                                            text = "${i + 1}. ${g.title}",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                color = SkyNavy
                                            ),
                                            maxLines = 1
                                        )
                                    }
                                } else {
                                    Text(
                                        text = "No focus goals selected",
                                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF5B7794))
                                    )
                                }

                                Spacer(modifier = Modifier.weight(1f))

                                Text(
                                    text = "📅 Next session: $nextSessionCountdown",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF4A6584)
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // How to Add Widget on Android
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(2.dp, SkyMist)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "How to add to your Home Screen",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, color = SkyNavy)
                    )
                    Text(
                        text = "1. Go to your Android Home Screen.\n2. Touch and hold any empty space.\n3. Tap 'Widgets' and scroll to 'Skybound Focus'.\n4. Touch and hold the widget, then drag it to your screen!\n5. You can resize it horizontally or vertically anytime.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF4A6584),
                            lineHeight = 20.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = {
                            WidgetUpdater.updateAllWidgets(context)
                            refreshNotice = true
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SkyBlue)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, tint = SkyNavy)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (refreshNotice) "Widgets Refreshed ✓" else "Sync / Update Widgets Now",
                            fontWeight = FontWeight.Black,
                            color = SkyNavy
                        )
                    }
                }
            }
        }
    }
}
