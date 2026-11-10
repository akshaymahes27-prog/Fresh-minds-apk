package com.example.skybound.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.skybound.mascot.MascotPose
import com.example.skybound.mascot.MascotView
import com.example.ui.theme.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

data class CelebrationData(
    val type: String, // "goal", "climb", "review", "milestone", "first", "record", "week"
    val title: String,
    val subtitle: String = "",
    val rankLevel: Int? = null,
    val badgeColorHex: String? = null
)

private data class ConfettiParticle(
    val startX: Float,
    val speed: Float,
    val swayAmplitude: Float,
    val swayFrequency: Float,
    val size: Float,
    val isCircle: Boolean,
    val color: Color,
    val rotationSpeed: Float,
    val delayOffset: Float
)

@Composable
fun CelebrationDialog(
    data: CelebrationData,
    onDismiss: () -> Unit
) {
    val pose = when (data.type) {
        "goal", "record" -> MascotPose.PROUD
        "climb", "first" -> MascotPose.WOW
        "review", "week" -> MascotPose.HAPPY
        else -> MascotPose.CHEER
    }

    val isFlyingFlight = data.type == "climb"

    // Infinite rotation for the radiant sunburst rays
    val infiniteTransition = rememberInfiniteTransition(label = "celebration_sunburst")
    val sunburstRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(24000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sunburst_rot"
    )

    // Confetti falling progress 0f to 1f repeating
    val confettiProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "confetti_fall"
    )

    // Entrance pop scale with spring
    var popTrigger by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        popTrigger = true
    }
    val contentScale by animateFloatAsState(
        targetValue = if (popTrigger) 1.0f else 0.4f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "celebration_pop"
    )

    // Generate confetti particle list once
    val confettiParticles = remember {
        val colors = listOf(
            AccentMint,
            SkyBlue,
            AccentSun,
            AccentCoral,
            AccentViolet,
            Color(0xFFFFD700),
            Color(0xFF00E5FF)
        )
        List(42) {
            ConfettiParticle(
                startX = Random.nextFloat(),
                speed = Random.nextFloat() * 0.4f + 0.8f,
                swayAmplitude = Random.nextFloat() * 24f + 12f,
                swayFrequency = Random.nextFloat() * 3f + 1.5f,
                size = Random.nextFloat() * 6f + 6f,
                isCircle = Random.nextBoolean(),
                color = colors[Random.nextInt(colors.size)],
                rotationSpeed = Random.nextFloat() * 720f - 360f,
                delayOffset = Random.nextFloat()
            )
        }
    }

    val dialogBg = if (SkyboundThemeState.isDark) {
        when (data.type) {
            "goal" -> Color(0xFF132B20)
            "review" -> Color(0xFF221836)
            "climb" -> Color(0xFF14273E)
            "record" -> Color(0xFF2C2411)
            else -> DarkCard
        }
    } else {
        when (data.type) {
            "goal" -> Color(0xFFF0FDF4)
            "review" -> Color(0xFFFAF5FF)
            "climb" -> Color(0xFFEFF6FF)
            "record" -> Color(0xFFFFFBEB)
            else -> SkyCloud
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .scale(contentScale),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = dialogBg,
                tonalElevation = 8.dp,
                shadowElevation = 16.dp,
                border = androidx.compose.foundation.BorderStroke(2.5.dp, SkyMist),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    // 1. Radiant Sunburst Background Canvas
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(340.dp)
                            .rotate(sunburstRotation)
                    ) {
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val radius = size.width * 0.85f
                        val numRays = 16
                        val rayAngle = 360f / numRays

                        for (i in 0 until numRays step 2) {
                            val startAngleRad = Math.toRadians((i * rayAngle).toDouble()).toFloat()
                            val endAngleRad = Math.toRadians(((i + 1) * rayAngle).toDouble()).toFloat()

                            val rayPath = Path().apply {
                                moveTo(center.x, center.y)
                                lineTo(center.x + radius * cos(startAngleRad), center.y + radius * sin(startAngleRad))
                                lineTo(center.x + radius * cos(endAngleRad), center.y + radius * sin(endAngleRad))
                                close()
                            }
                            drawPath(
                                rayPath,
                                color = if (SkyboundThemeState.isDark) Color(0xFF8FD3FA).copy(alpha = 0.14f) else Color.White.copy(alpha = 0.45f)
                            )
                        }
                    }

                    // 2. Physics Confetti Rain
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(340.dp)
                    ) {
                        val w = size.width
                        val h = size.height

                        confettiParticles.forEach { p ->
                            val loopedProgress = (confettiProgress * p.speed + p.delayOffset) % 1f
                            val y = loopedProgress * (h + 30f) - 15f
                            val sway = sin(loopedProgress * p.swayFrequency * 2 * PI.toFloat()) * p.swayAmplitude
                            val x = (p.startX * w) + sway
                            val rot = (loopedProgress * p.rotationSpeed) % 360f
                            val tumbleScale = cos(loopedProgress * 6 * PI.toFloat()).coerceIn(-1f, 1f)

                            rotate(degrees = rot, pivot = Offset(x, y)) {
                                if (p.isCircle) {
                                    drawCircle(
                                        color = p.color,
                                        radius = (p.size / 2f) * kotlin.math.abs(tumbleScale),
                                        center = Offset(x, y)
                                    )
                                } else {
                                    drawRect(
                                        color = p.color,
                                        topLeft = Offset(x - p.size / 2f, y - (p.size * 1.5f) / 2f),
                                        size = Size(p.size * kotlin.math.abs(tumbleScale), p.size * 1.5f)
                                    )
                                }
                            }
                        }
                    }

                    // 3. Main Content
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // High-flying or cheering mascot
                        MascotView(
                            pose = pose,
                            size = if (isFlyingFlight) 110.dp else 100.dp,
                            animateBob = true,
                            forceFlyingLauncher = isFlyingFlight
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = data.title,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = SkyNavy,
                                fontSize = 23.sp
                            ),
                            textAlign = TextAlign.Center
                        )

                        if (data.type == "climb" && data.rankLevel != null && data.badgeColorHex != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            AltitudeBadge(
                                rankLevel = data.rankLevel,
                                badgeColorHex = data.badgeColorHex,
                                size = 68.dp
                            )
                        }

                        if (data.subtitle.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = data.subtitle,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SkyTextSubtle
                                ),
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(22.dp))

                        // Duolingo Tactile 3D Button (Beveled depth)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                        ) {
                            // Bottom 3D shadow layer
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(DeepSky)
                            )
                            // Top clickable button layer
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight()
                                    .padding(bottom = 5.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(SkyBlue)
                                    .clickable(onClick = onDismiss),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "CONTINUE",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        color = DeepNavy,
                                        fontSize = 16.sp,
                                        letterSpacing = 0.5.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
