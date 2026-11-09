package com.example.skybound.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skybound.data.ActivityEntity
import com.example.skybound.mascot.MascotPose
import com.example.skybound.mascot.MascotView
import com.example.skybound.model.AltitudeInfo
import com.example.ui.theme.*

@Composable
fun DuolingoTopBar(
    appName: String,
    currentActivity: ActivityEntity?,
    altitudeInfo: AltitudeInfo,
    weeklyRhythmCount: Int,
    weeklyTarget: Int,
    currentAimCount: Int,
    nextSessionCountdown: String,
    onActivityClick: () -> Unit,
    onWeeklyRhythmClick: () -> Unit,
    onCurrentAimClick: () -> Unit,
    onNextSessionClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shadowElevation = 3.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(bottom = 6.dp)
        ) {
            // Header Row: App Mascot Head Logo, Name, Settings Gear
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Interactive Mini Mascot Head
                MascotView(
                    pose = MascotPose.WAVE,
                    size = 36.dp,
                    animateBob = true,
                    isInteractive = true
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = appName,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        color = SkyNavy
                    ),
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                IconButton(
                    onClick = onSettingsClick,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings and more",
                        tint = SkyTextMuted
                    )
                }
            }

            // Stat Chips Row: Duolingo style 4 chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // 1. Activity Switcher Chip with Rich Custom Vector Icon + Altitude Rank badge
                val actColor = try {
                    Color(android.graphics.Color.parseColor(currentActivity?.color ?: "#1CB0F6"))
                } catch (e: Exception) {
                    SkyBlue
                }

                StatChip(
                    modifier = Modifier.weight(1.15f),
                    onClick = onActivityClick,
                    leading = {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(actColor),
                            contentAlignment = Alignment.Center
                        ) {
                            ActivityIcon(
                                iconKey = currentActivity?.icon ?: "ball",
                                tint = Color.White,
                                size = 18.dp
                            )
                        }
                    },
                    text = currentActivity?.name ?: "Activity"
                )

                // 2. Weekly Rhythm Chip (🔥 Flame)
                StatChip(
                    modifier = Modifier.weight(0.9f),
                    onClick = onWeeklyRhythmClick,
                    leading = {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = "Weekly rhythm",
                            tint = AccentOrange,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    text = "$weeklyRhythmCount/$weeklyTarget"
                )

                // 3. Current Aim Chip (⭐ Focus goals)
                StatChip(
                    modifier = Modifier.weight(0.9f),
                    onClick = onCurrentAimClick,
                    leading = {
                        Icon(
                            imageVector = Icons.Default.Stars,
                            contentDescription = "Current aim",
                            tint = SkyBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    text = "$currentAimCount/3"
                )

                // 4. Next Session Chip (📅 Calendar countdown)
                StatChip(
                    modifier = Modifier.weight(0.9f),
                    onClick = onNextSessionClick,
                    leading = {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Next session",
                            tint = AccentViolet,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    text = nextSessionCountdown
                )
            }
        }
    }
}

@Composable
private fun StatChip(
    onClick: () -> Unit,
    leading: @Composable () -> Unit,
    text: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        color = SkyCloud,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            leading()
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp,
                    color = SkyNavy
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
