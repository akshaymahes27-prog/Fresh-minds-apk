package com.example.skybound.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skybound.data.GoalEntity
import com.example.skybound.model.ALTITUDE_RANKS
import com.example.skybound.model.AltitudeInfo
import com.example.ui.theme.*

/**
 * Draws the Altitude Shield Badge.
 */
@Composable
fun AltitudeBadge(
    rankLevel: Int,
    badgeColorHex: String,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp
) {
    val badgeColor = try {
        Color(android.graphics.Color.parseColor(badgeColorHex))
    } catch (e: Exception) {
        SkyBlue
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height

            // Shield path
            val shield = Path().apply {
                moveTo(w * 0.5f, h * 0.05f)
                lineTo(w * 0.88f, h * 0.18f)
                lineTo(w * 0.88f, h * 0.50f)
                cubicTo(w * 0.88f, h * 0.75f, w * 0.65f, h * 0.92f, w * 0.5f, h * 0.98f)
                cubicTo(w * 0.35f, h * 0.92f, w * 0.12f, h * 0.75f, w * 0.12f, h * 0.50f)
                lineTo(w * 0.12f, h * 0.18f)
                close()
            }
            drawPath(shield, color = badgeColor)
            drawPath(shield, color = SkyNavy, style = Stroke(width = w * 0.06f))
        }

        Text(
            text = "$rankLevel",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Black,
                color = SkyNavy,
                fontSize = (size.value * 0.38f).sp
            )
        )
    }
}

/**
 * Modal bottom sheet presenting the user's Altitude details, progression, and all ranks.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AltitudeSheet(
    activityName: String,
    altitudeInfo: AltitudeInfo,
    latestFinishedGoals: List<GoalEntity>,
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Altitude: $activityName",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Black,
                    color = SkyNavy
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Large Shield Badge
            AltitudeBadge(
                rankLevel = altitudeInfo.currentRank.level,
                badgeColorHex = altitudeInfo.currentRank.badgeColorHex,
                size = 96.dp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = altitudeInfo.currentRank.title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 24.sp,
                    color = SkyNavy
                )
            )

            Text(
                text = "${altitudeInfo.totalFinished} goals finished",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = SkyTextSubtle
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Thick Animated Progress Bar
            val animatedAltitudeProgress by androidx.compose.animation.core.animateFloatAsState(
                targetValue = (altitudeInfo.progressPercent / 100f).coerceIn(0f, 1f),
                animationSpec = androidx.compose.animation.core.spring(
                    dampingRatio = androidx.compose.animation.core.Spring.DampingRatioLowBouncy,
                    stiffness = androidx.compose.animation.core.Spring.StiffnessMediumLow
                ),
                label = "altitude_sheet_progress"
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .clip(RoundedCornerShape(99.dp))
                    .background(SkyMist)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(animatedAltitudeProgress)
                        .clip(RoundedCornerShape(99.dp))
                        .background(if (altitudeInfo.nextRank == null) AccentMint else SkyBlue)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            val nextRankText = if (altitudeInfo.nextRank != null) {
                "${altitudeInfo.goalsToNextRank} more goal${if (altitudeInfo.goalsToNextRank == 1) "" else "s"} to ${altitudeInfo.nextRank.title}"
            } else {
                "You have reached the Summit! Top rank achieved."
            }

            Text(
                text = nextRankText,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = SkyTextSubtle
                )
            )

            Spacer(modifier = Modifier.height(18.dp))

            // All Ranks Table
            Text(
                text = "All Ranks",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = SkyNavy
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ALTITUDE_RANKS.forEach { rank ->
                    val isReached = altitudeInfo.currentRank.level >= rank.level
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AltitudeBadge(
                            rankLevel = rank.level,
                            badgeColorHex = rank.badgeColorHex,
                            size = 32.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = rank.title,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = SkyNavy
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = if (isReached) "Reached" else "${rank.goalsRequired} goals",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = if (isReached) AccentMint else SkyTextMuted
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SkyBlue)
            ) {
                Text(
                    text = "Done",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = DeepNavy
                    )
                )
            }
        }
    }
}
