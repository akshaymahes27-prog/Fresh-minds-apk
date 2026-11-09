package com.example.skybound.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skybound.data.ActivityEntity
import com.example.skybound.model.AltitudeInfo
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivitySwitcherSheet(
    activities: List<ActivityEntity>,
    selectedActivity: ActivityEntity?,
    altitudeInfo: AltitudeInfo,
    onSelectActivity: (String) -> Unit,
    onAddNewActivity: () -> Unit,
    onViewAltitudeDetails: () -> Unit,
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
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "Switch Activity",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    color = SkyNavy
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Horizontal row of rounded activity tiles with rich vector icons
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(activities) { activity ->
                    val isSelected = activity.id == selectedActivity?.id
                    val actColor = try {
                        Color(android.graphics.Color.parseColor(activity.color))
                    } catch (e: Exception) {
                        SkyBlue
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(76.dp)
                            .clickable {
                                onSelectActivity(activity.id)
                                onDismiss()
                            }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(actColor)
                                .then(
                                    if (isSelected) {
                                        Modifier.border(3.dp, SkyNavy, RoundedCornerShape(20.dp))
                                    } else Modifier
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            ActivityIcon(
                                iconKey = activity.icon,
                                tint = Color.White,
                                size = 32.dp
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = activity.name,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                color = SkyNavy
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Add Activity Dashed Tile
                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(76.dp)
                            .clickable {
                                onDismiss()
                                onAddNewActivity()
                            }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(SkyCloud)
                                .border(2.dp, SkyTextMuted, RoundedCornerShape(20.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add activity",
                                tint = SkyTextMuted,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Add new",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = SkyTextMuted
                            ),
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Altitude Progress Card (Tappable for full Altitude Details)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .clickable(onClick = onViewAltitudeDetails),
                shape = RoundedCornerShape(18.dp),
                color = SkyCloud,
                border = androidx.compose.foundation.BorderStroke(2.dp, SkyMist)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AltitudeBadge(
                            rankLevel = altitudeInfo.currentRank.level,
                            badgeColorHex = altitudeInfo.currentRank.badgeColorHex,
                            size = 46.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = altitudeInfo.currentRank.title,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 17.sp,
                                    color = SkyNavy
                                )
                            )
                            Text(
                                text = "${altitudeInfo.totalFinished} goals finished",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = SkyTextSubtle
                                )
                            )
                        }
                        Text(
                            text = "Tap to view",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = SkyBlue
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Progress bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(14.dp)
                            .clip(RoundedCornerShape(99.dp))
                            .background(SkyMist)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(altitudeInfo.progressPercent / 100f)
                                .clip(RoundedCornerShape(99.dp))
                                .background(if (altitudeInfo.nextRank == null) AccentMint else SkyBlue)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    val rankNotice = if (altitudeInfo.nextRank != null) {
                        "${altitudeInfo.goalsToNextRank} to ${altitudeInfo.nextRank.title}"
                    } else {
                        "Top rank reached"
                    }

                    Text(
                        text = rankNotice,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = SkyTextSubtle,
                            fontSize = 12.sp
                        )
                    )
                }
            }
        }
    }
}
