package com.example.skybound.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skybound.data.Converters
import com.example.skybound.data.GoalEntity
import com.example.skybound.model.GoalPriority
import com.example.ui.theme.*

@Composable
fun GoalItemCard(
    goal: GoalEntity,
    onToggleDone: () -> Unit,
    onToggleAim: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showAimStar: Boolean = true
) {
    val isDone = goal.completedAt != null
    val isRetired = goal.retiredAt != null
    val priority = GoalPriority.fromCode(goal.priorityCode)
    val steps = Converters.parseSteps(goal.stepsJson)
    val haptic = LocalHapticFeedback.current

    val priorityColor = when (priority) {
        GoalPriority.HIGH -> AccentCoral
        GoalPriority.MEDIUM -> AccentSun
        GoalPriority.LOW -> SkyMist
    }

    val tickScale by animateFloatAsState(
        targetValue = if (isDone) 1.18f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "tick_scale"
    )

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val cardScale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1.0f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "card_press_scale"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .scale(cardScale)
            .clip(RoundedCornerShape(18.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .testTag("goal_card_${goal.id}"),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(2.dp, if (isDone) AccentMint.copy(alpha = 0.5f) else SkyMist),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Circular Spring Tick Button with Haptics
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .scale(tickScale)
                    .clip(CircleShape)
                    .background(if (isDone) AccentMint else Color.Transparent)
                    .border(
                        width = 3.dp,
                        color = if (isDone) AccentMint else SkyMist,
                        shape = CircleShape
                    )
                    .clickable {
                        haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                        onToggleDone()
                    }
                    .testTag("goal_tick_${goal.id}"),
                contentAlignment = Alignment.Center
            ) {
                if (isDone) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Mark not done",
                        tint = SkyNavy,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Title, Next Action & Steps
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = goal.title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = if (isDone || isRetired) SkyTextMuted else SkyNavy,
                        textDecoration = if (isDone) TextDecoration.LineThrough else TextDecoration.None
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (goal.nextAction.isNotBlank() && !isDone) {
                    Text(
                        text = "Next: ${goal.nextAction}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = SkyTextSubtle,
                            fontSize = 12.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                // Segmented Step Bar with Animated Width Transitions
                if (steps.isNotEmpty()) {
                    val doneStepCount = steps.count { it.isDone }
                    val allDone = doneStepCount == steps.size

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            steps.forEach { step ->
                                val stepColor = if (step.isDone) (if (allDone) AccentMint else SkyBlue) else SkyMist
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(stepColor)
                                )
                            }
                        }

                        Text(
                            text = "$doneStepCount/${steps.size} steps",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = SkyTextMuted,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Priority Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(99.dp))
                    .background(if (isRetired) SkyMist else priorityColor)
                    .padding(horizontal = 8.dp, vertical = 3.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isRetired) "Retired" else priority.label,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Black,
                        color = if (isRetired) SkyNavy else DeepNavy,
                        fontSize = 11.sp
                    )
                )
            }

            // Star Button (Toggle Current Aim)
            if (showAimStar && !isDone && !isRetired) {
                IconButton(
                    onClick = onToggleAim,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("aim_toggle_${goal.id}")
                ) {
                    Icon(
                        imageVector = if (goal.isInCurrentAim) Icons.Default.Star else Icons.Outlined.StarBorder,
                        contentDescription = if (goal.isInCurrentAim) "Remove from Current Aim" else "Add to Current Aim",
                        tint = if (goal.isInCurrentAim) AccentSun else Color(0xFF9DB4CB),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
