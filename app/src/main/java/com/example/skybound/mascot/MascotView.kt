package com.example.skybound.mascot

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * Enhanced, modular, and highly animated MascotView for Skybound.
 * Supports:
 *  - Dynamic eye blinking
 *  - Breathing squash & stretch animation
 *  - Wing flapping oscillation
 *  - Tap-to-hop interaction with spring squash
 *  - Full modular parts (crest, eyes, beak, wings, accessory, palette)
 *  - Dedicated "Flying Launcher" mode with wind trails and cloud puffs
 */
@Composable
fun MascotView(
    pose: MascotPose = MascotPose.HI,
    modifier: Modifier = Modifier,
    size: Dp = 80.dp,
    animateBob: Boolean = true,
    isInteractive: Boolean = true,
    overrideCrest: MascotCrest? = null,
    overrideEyes: MascotEyes? = null,
    overrideBeak: MascotBeak? = null,
    overrideWings: MascotWings? = null,
    overrideAccessory: MascotAccessory? = null,
    overridePalette: MascotColorPalette? = null,
    forceFlyingLauncher: Boolean = false,
    onSpeechChange: ((String) -> Unit)? = null
) {
    val coroutineScope = rememberCoroutineScope()
    var hopOffset by remember { mutableStateOf(0f) }
    var squashScaleY by remember { mutableStateOf(1f) }
    var squashScaleX by remember { mutableStateOf(1f) }

    // Natural autonomous eye blink state
    var isBlinking by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(Random.nextLong(2800, 4800))
            isBlinking = true
            delay(130)
            isBlinking = false
        }
    }

    // Breathing squash & stretch
    val infiniteTransition = rememberInfiniteTransition(label = "mascot_idle")
    val breathScaleY by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.025f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mascot_breath_y"
    )

    val bobOffset by infiniteTransition.animateFloat(
        initialValue = -3.5f,
        targetValue = 3.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mascot_bob_y"
    )

    // Wing flapping animation for flapping wings
    val wingFlutterAngle by infiniteTransition.animateFloat(
        initialValue = -12f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(320, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wing_flutter"
    )

    // Wind trails motion for flying launcher
    val windStreakOffset by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 110f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wind_streak"
    )

    val isFlying = forceFlyingLauncher || MascotModularState.isFlyingLauncherMode
    val currentYOffset = if (animateBob && !isFlying) bobOffset + hopOffset else hopOffset

    val activeCrest = overrideCrest ?: MascotModularState.crest
    val activeEyes = overrideEyes ?: MascotModularState.eyes
    val activeBeak = overrideBeak ?: MascotModularState.beak
    val activeWings = overrideWings ?: MascotModularState.wings
    val activeAccessory = overrideAccessory ?: MascotModularState.accessory
    val activePalette = overridePalette ?: MascotModularState.palette

    Box(
        modifier = modifier
            .size(size)
            .offset(y = currentYOffset.dp)
            .graphicsLayer {
                scaleY = squashScaleY * (if (animateBob && !isFlying) breathScaleY else 1f)
                scaleX = squashScaleX
                if (isFlying) {
                    rotationZ = -8f + (bobOffset * 0.8f)
                }
            }
            .then(
                if (isInteractive) {
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        coroutineScope.launch {
                            // Squash before launch
                            squashScaleY = 0.82f
                            squashScaleX = 1.15f
                            delay(70)
                            // Jump high
                            squashScaleY = 1.15f
                            squashScaleX = 0.88f
                            hopOffset = -18f

                            val quotes = pose.getInteractiveQuotes()
                            val quote = quotes[Random.nextInt(quotes.size)]
                            onSpeechChange?.invoke(quote)

                            delay(220)
                            // Touchdown bounce
                            hopOffset = 0f
                            squashScaleY = 0.92f
                            squashScaleX = 1.06f
                            delay(90)
                            squashScaleY = 1f
                            squashScaleX = 1f
                        }
                    }
                } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawModularMascot(
                pose = pose,
                crest = activeCrest,
                eyes = activeEyes,
                beak = activeBeak,
                wings = activeWings,
                accessory = activeAccessory,
                palette = activePalette,
                isBlinking = isBlinking,
                wingFlutterAngle = wingFlutterAngle,
                isFlying = isFlying,
                windStreakProgress = windStreakOffset
            )
        }
    }
}

/**
 * Pure Canvas Vector drawing for all modular Mascot parts.
 */
private fun DrawScope.drawModularMascot(
    pose: MascotPose,
    crest: MascotCrest,
    eyes: MascotEyes,
    beak: MascotBeak,
    wings: MascotWings,
    accessory: MascotAccessory,
    palette: MascotColorPalette,
    isBlinking: Boolean,
    wingFlutterAngle: Float,
    isFlying: Boolean,
    windStreakProgress: Float
) {
    val w = size.width
    val h = size.height
    val scale = w / 120f

    val bodyCol = palette.body
    val wingCol = palette.wing
    val tummyCol = palette.tummy
    val beakCol = palette.beak
    val feetCol = palette.feet
    val blushCol = Color(0xFFFF7EB6).copy(alpha = 0.8f)
    val navyCol = Color(0xFF0F2A4A)

    // 0. Wind Streaks & Speed Lines if Flying Launcher mode
    if (isFlying) {
        val streakCol = Color.White.copy(alpha = 0.55f)
        drawLine(
            color = streakCol,
            start = Offset((windStreakProgress - 30f) * scale, 24f * scale),
            end = Offset((windStreakProgress) * scale, 24f * scale),
            strokeWidth = 3f * scale,
            cap = StrokeCap.Round
        )
        drawLine(
            color = streakCol,
            start = Offset((windStreakProgress - 45f) * scale, 86f * scale),
            end = Offset((windStreakProgress - 10f) * scale, 86f * scale),
            strokeWidth = 2.5f * scale,
            cap = StrokeCap.Round
        )
        drawLine(
            color = streakCol,
            start = Offset((windStreakProgress - 20f) * scale, 56f * scale),
            end = Offset((windStreakProgress + 20f) * scale, 56f * scale),
            strokeWidth = 3.5f * scale,
            cap = StrokeCap.Round
        )
        // Trailing cloud puffs
        drawCircle(
            color = Color.White.copy(alpha = 0.4f),
            radius = 7f * scale,
            center = Offset(14f * scale, 82f * scale)
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.3f),
            radius = 5f * scale,
            center = Offset(4f * scale, 86f * scale)
        )
    }

    // 1. Pose overlays (Thinking bubbles, sparkles, wow lines)
    if (!isFlying) {
        when (pose) {
            MascotPose.THINKING -> {
                drawCircle(color = Color(0xFFCFE9FB), radius = 3.5f * scale, center = Offset(94f * scale, 30f * scale))
                drawCircle(color = Color(0xFFCFE9FB), radius = 5.5f * scale, center = Offset(102f * scale, 22f * scale))
                drawOval(
                    color = Color(0xFFCFE9FB),
                    topLeft = Offset(96f * scale, 4f * scale),
                    size = Size(22f * scale, 18f * scale)
                )
                val qPath = Path().apply {
                    moveTo(104f * scale, 9f * scale)
                    cubicTo(104f * scale, 6f * scale, 110f * scale, 6f * scale, 110f * scale, 11f * scale)
                    lineTo(107f * scale, 14f * scale)
                    lineTo(107f * scale, 16f * scale)
                }
                drawPath(qPath, color = wingCol, style = Stroke(width = 2.5f * scale, cap = StrokeCap.Round))
                drawCircle(color = wingCol, radius = 1.2f * scale, center = Offset(107f * scale, 19f * scale))
            }
            MascotPose.CHEER -> {
                drawSparkle(16f * scale, 26f * scale, 6.5f * scale, beakCol)
                drawSparkle(104f * scale, 18f * scale, 5.5f * scale, beakCol)
            }
            MascotPose.WOW -> {
                drawLine(beakCol, Offset(20f * scale, 34f * scale), Offset(28f * scale, 38f * scale), strokeWidth = 3f * scale, cap = StrokeCap.Round)
                drawLine(beakCol, Offset(100f * scale, 34f * scale), Offset(92f * scale, 38f * scale), strokeWidth = 3f * scale, cap = StrokeCap.Round)
                drawLine(beakCol, Offset(60f * scale, 8f * scale), Offset(60f * scale, 18f * scale), strokeWidth = 3f * scale, cap = StrokeCap.Round)
            }
            MascotPose.WAVE -> {
                val waveMotion = Path().apply {
                    moveTo(102f * scale, 28f * scale)
                    quadraticBezierTo(108f * scale, 24f * scale, 114f * scale, 28f * scale)
                }
                drawPath(waveMotion, color = beakCol, style = Stroke(width = 3f * scale, cap = StrokeCap.Round))
            }
            MascotPose.SLEEP, MascotPose.TIRED -> {
                val dropPath = Path().apply {
                    moveTo(92f * scale, 38f * scale)
                    quadraticBezierTo(88f * scale, 45f * scale, 92f * scale, 48f * scale)
                    quadraticBezierTo(96f * scale, 45f * scale, 92f * scale, 38f * scale)
                }
                drawPath(dropPath, color = Color(0xFF8FD3FA))
            }
            else -> Unit
        }
    }

    // 2. Jetpack accessory thrusters behind the body
    if (accessory == MascotAccessory.JETPACK) {
        // Twin metal canisters
        drawRoundRect(
            color = Color(0xFF78909C),
            topLeft = Offset(16f * scale, 62f * scale),
            size = Size(14f * scale, 26f * scale),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f * scale)
        )
        // Jet flame
        val flamePath = Path().apply {
            moveTo(19f * scale, 88f * scale)
            lineTo(23f * scale, 102f * scale)
            lineTo(27f * scale, 88f * scale)
            close()
        }
        drawPath(flamePath, color = Color(0xFFFF5722))
        val innerFlame = Path().apply {
            moveTo(21f * scale, 88f * scale)
            lineTo(23f * scale, 96f * scale)
            lineTo(25f * scale, 88f * scale)
            close()
        }
        drawPath(innerFlame, color = Color(0xFFFFD54F))
    }

    // 3. Tail feathers
    val tailPath = Path().apply {
        moveTo(24f * scale, 78f * scale)
        lineTo(8f * scale, 70f * scale)
        lineTo(14f * scale, 86f * scale)
        close()
    }
    drawPath(tailPath, color = wingCol)

    // 4. Main Oval Body
    drawOval(
        color = bodyCol,
        topLeft = Offset((60f - 40f) * scale, (70f - 36f) * scale),
        size = Size(80f * scale, 72f * scale)
    )

    // 5. Soft Tummy
    drawOval(
        color = tummyCol,
        topLeft = Offset((60f - 24f) * scale, (82f - 20f) * scale),
        size = Size(48f * scale, 40f * scale)
    )

    // 6. Modular Crest Parts
    when (crest) {
        MascotCrest.TUFT -> {
            drawLine(wingCol, Offset(52f * scale, 36f * scale), Offset(54f * scale, 18f * scale), strokeWidth = 4.5f * scale, cap = StrokeCap.Round)
            drawLine(wingCol, Offset(62f * scale, 34f * scale), Offset(70f * scale, 16f * scale), strokeWidth = 4.5f * scale, cap = StrokeCap.Round)
            drawLine(wingCol, Offset(72f * scale, 38f * scale), Offset(84f * scale, 26f * scale), strokeWidth = 4.5f * scale, cap = StrokeCap.Round)
        }
        MascotCrest.CROWN -> {
            // Golden 3-point crown
            val crownPath = Path().apply {
                moveTo(44f * scale, 36f * scale)
                lineTo(44f * scale, 20f * scale)
                lineTo(52f * scale, 28f * scale)
                lineTo(60f * scale, 16f * scale)
                lineTo(68f * scale, 28f * scale)
                lineTo(76f * scale, 20f * scale)
                lineTo(76f * scale, 36f * scale)
                close()
            }
            drawPath(crownPath, color = Color(0xFFFFD700))
            // Ruby gem in center
            drawCircle(Color(0xFFFF1744), radius = 2.5f * scale, center = Offset(60f * scale, 25f * scale))
        }
        MascotCrest.GOGGLES -> {
            // Pilot Goggles on forehead
            val strap = Path().apply {
                moveTo(34f * scale, 40f * scale)
                lineTo(86f * scale, 40f * scale)
            }
            drawPath(strap, color = Color(0xFF5D4037), style = Stroke(width = 4f * scale, cap = StrokeCap.Round))
            // Left lens
            drawRoundRect(
                color = Color(0xFF80DEEA),
                topLeft = Offset(42f * scale, 32f * scale),
                size = Size(15f * scale, 12f * scale),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f * scale)
            )
            drawRoundRect(
                color = Color(0xFF006064),
                topLeft = Offset(42f * scale, 32f * scale),
                size = Size(15f * scale, 12f * scale),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f * scale),
                style = Stroke(width = 2.5f * scale)
            )
            // Right lens
            drawRoundRect(
                color = Color(0xFF80DEEA),
                topLeft = Offset(63f * scale, 32f * scale),
                size = Size(15f * scale, 12f * scale),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f * scale)
            )
            drawRoundRect(
                color = Color(0xFF006064),
                topLeft = Offset(63f * scale, 32f * scale),
                size = Size(15f * scale, 12f * scale),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f * scale),
                style = Stroke(width = 2.5f * scale)
            )
        }
        MascotCrest.HEADBAND -> {
            // Crimson sports headband
            val band = Path().apply {
                moveTo(34f * scale, 38f * scale)
                lineTo(86f * scale, 38f * scale)
            }
            drawPath(band, color = Color(0xFFFF1744), style = Stroke(width = 6f * scale, cap = StrokeCap.Round))
            // Star on forehead
            drawSparkle(60f * scale, 38f * scale, 4f * scale, Color.White)
        }
        MascotCrest.STAR_HALO -> {
            // Glowing golden halo floating above
            drawOval(
                color = Color(0xFFFFD54F),
                topLeft = Offset(42f * scale, 14f * scale),
                size = Size(36f * scale, 10f * scale),
                style = Stroke(width = 3.5f * scale)
            )
        }
        MascotCrest.SMOOTH -> {
            // Smooth sleek head, no crest
        }
    }

    // 7. Cheeks (blush)
    drawCircle(color = blushCol, radius = 5f * scale, center = Offset(36f * scale, 66f * scale))
    drawCircle(color = blushCol, radius = 5f * scale, center = Offset(84f * scale, 66f * scale))

    // 8. Modular Eyes (with Blink logic and Pose overrides)
    if (isBlinking || pose == MascotPose.SLEEP) {
        // Natural blink arc
        val arc1 = Path().apply {
            moveTo(40f * scale, 55f * scale)
            quadraticBezierTo(47f * scale, 60f * scale, 54f * scale, 55f * scale)
        }
        val arc2 = Path().apply {
            moveTo(66f * scale, 55f * scale)
            quadraticBezierTo(73f * scale, 60f * scale, 80f * scale, 55f * scale)
        }
        drawPath(arc1, navyCol, style = Stroke(width = 3.5f * scale, cap = StrokeCap.Round))
        drawPath(arc2, navyCol, style = Stroke(width = 3.5f * scale, cap = StrokeCap.Round))
    } else {
        when (eyes) {
            MascotEyes.SUNGLASSES -> {
                // Sleek cool aviator dark glasses
                val bridge = Path().apply {
                    moveTo(54f * scale, 53f * scale)
                    lineTo(66f * scale, 53f * scale)
                }
                drawPath(bridge, color = Color(0xFF212121), style = Stroke(width = 2.5f * scale))
                // Left glass
                drawRoundRect(
                    color = Color(0xFF263238),
                    topLeft = Offset(38f * scale, 48f * scale),
                    size = Size(18f * scale, 14f * scale),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f * scale)
                )
                // Right glass
                drawRoundRect(
                    color = Color(0xFF263238),
                    topLeft = Offset(64f * scale, 48f * scale),
                    size = Size(18f * scale, 14f * scale),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f * scale)
                )
                // Glare line
                drawLine(Color.White.copy(alpha = 0.6f), Offset(40f * scale, 50f * scale), Offset(46f * scale, 58f * scale), strokeWidth = 2f * scale)
                drawLine(Color.White.copy(alpha = 0.6f), Offset(66f * scale, 50f * scale), Offset(72f * scale, 58f * scale), strokeWidth = 2f * scale)
            }
            MascotEyes.STAR_EYES -> {
                drawCircle(Color.White, radius = 9f * scale, center = Offset(47f * scale, 54f * scale))
                drawCircle(Color.White, radius = 9f * scale, center = Offset(73f * scale, 54f * scale))
                drawSparkle(47f * scale, 54f * scale, 6f * scale, Color(0xFFFFB300))
                drawSparkle(73f * scale, 54f * scale, 6f * scale, Color(0xFFFFB300))
            }
            MascotEyes.JOYFUL -> {
                val arc1 = Path().apply {
                    moveTo(40f * scale, 57f * scale)
                    quadraticBezierTo(47f * scale, 48f * scale, 54f * scale, 57f * scale)
                }
                val arc2 = Path().apply {
                    moveTo(66f * scale, 57f * scale)
                    quadraticBezierTo(73f * scale, 48f * scale, 80f * scale, 57f * scale)
                }
                drawPath(arc1, navyCol, style = Stroke(width = 3.5f * scale, cap = StrokeCap.Round))
                drawPath(arc2, navyCol, style = Stroke(width = 3.5f * scale, cap = StrokeCap.Round))
            }
            MascotEyes.WINK -> {
                // Left winking arc
                val arc = Path().apply {
                    moveTo(40f * scale, 55f * scale)
                    quadraticBezierTo(47f * scale, 48f * scale, 54f * scale, 55f * scale)
                }
                drawPath(arc, navyCol, style = Stroke(width = 3.5f * scale, cap = StrokeCap.Round))
                // Right starry open eye
                drawCircle(Color.White, radius = 8f * scale, center = Offset(73f * scale, 54f * scale))
                drawCircle(navyCol, radius = 4.5f * scale, center = Offset(75f * scale, 55f * scale))
                drawCircle(Color.White, radius = 1.6f * scale, center = Offset(73.5f * scale, 53.5f * scale))
            }
            MascotEyes.SLEEPY -> {
                val arc1 = Path().apply {
                    moveTo(40f * scale, 54f * scale)
                    quadraticBezierTo(47f * scale, 60f * scale, 54f * scale, 54f * scale)
                }
                val arc2 = Path().apply {
                    moveTo(66f * scale, 54f * scale)
                    quadraticBezierTo(73f * scale, 60f * scale, 80f * scale, 54f * scale)
                }
                drawPath(arc1, navyCol, style = Stroke(width = 3.5f * scale, cap = StrokeCap.Round))
                drawPath(arc2, navyCol, style = Stroke(width = 3.5f * scale, cap = StrokeCap.Round))
            }
            MascotEyes.SPARKLE -> {
                // Wide open friendly eyes with double light reflections
                drawCircle(Color.White, radius = 8.5f * scale, center = Offset(47f * scale, 54f * scale))
                drawCircle(Color.White, radius = 8.5f * scale, center = Offset(73f * scale, 54f * scale))
                drawCircle(navyCol, radius = 4.8f * scale, center = Offset(49f * scale, 55f * scale))
                drawCircle(navyCol, radius = 4.8f * scale, center = Offset(75f * scale, 55f * scale))
                // Big sparkle catch
                drawCircle(Color.White, radius = 1.8f * scale, center = Offset(47.5f * scale, 53f * scale))
                drawCircle(Color.White, radius = 1.8f * scale, center = Offset(73.5f * scale, 53f * scale))
                // Secondary micro-sparkle
                drawCircle(Color.White, radius = 0.9f * scale, center = Offset(51f * scale, 56.5f * scale))
                drawCircle(Color.White, radius = 0.9f * scale, center = Offset(77f * scale, 56.5f * scale))
            }
        }
    }

    // 9. Modular Beak Parts
    val beakPath = Path().apply {
        moveTo(53f * scale, 64f * scale)
        lineTo(67f * scale, 64f * scale)
        lineTo(60f * scale, 74f * scale)
        close()
    }
    drawPath(beakPath, color = beakCol)

    when (beak) {
        MascotBeak.OPEN_SMILE -> {
            val lowerBeak = Path().apply {
                moveTo(55f * scale, 71f * scale)
                lineTo(65f * scale, 71f * scale)
                lineTo(60f * scale, 78f * scale)
                close()
            }
            drawPath(lowerBeak, color = Color(0xFFFF9F43))
        }
        MascotBeak.CHIRP -> {
            // Little round open chirp
            drawCircle(Color(0xFFFF9F43), radius = 3.5f * scale, center = Offset(60f * scale, 72f * scale))
        }
        MascotBeak.WHISTLE -> {
            // Puckered beak with small musical note
            drawCircle(beakCol, radius = 3.5f * scale, center = Offset(60f * scale, 72f * scale))
            // Small music note
            drawCircle(wingCol, radius = 1.8f * scale, center = Offset(72f * scale, 70f * scale))
            drawLine(wingCol, Offset(73.5f * scale, 70f * scale), Offset(73.5f * scale, 64f * scale), strokeWidth = 1.5f * scale)
        }
        MascotBeak.CLASSIC -> {
            // Crisp classic closed beak
        }
    }

    // 10. Modular Wings & Pose Integration
    val isSoaring = isFlying || wings == MascotWings.SOARING
    val isCheering = pose == MascotPose.CHEER || pose == MascotPose.WOW || wings == MascotWings.CHEERING

    if (isSoaring) {
        // Broad outstretched aerodynamic glide wings
        val rightWing = Path().apply {
            moveTo(84f * scale, 64f * scale)
            cubicTo(114f * scale, 54f * scale, 118f * scale, 70f * scale, 90f * scale, 82f * scale)
            close()
        }
        val leftWing = Path().apply {
            moveTo(36f * scale, 64f * scale)
            cubicTo(6f * scale, 54f * scale, 2f * scale, 70f * scale, 30f * scale, 82f * scale)
            close()
        }
        drawPath(rightWing, color = wingCol)
        drawPath(leftWing, color = wingCol)
    } else if (isCheering) {
        // Wings raised up in victory!
        val rightWingUp = Path().apply {
            moveTo(88f * scale, 70f * scale)
            cubicTo(112f * scale, 66f * scale, 110f * scale, 40f * scale, 88f * scale, 44f * scale)
            close()
        }
        val leftWingUp = Path().apply {
            moveTo(32f * scale, 70f * scale)
            cubicTo(8f * scale, 66f * scale, 10f * scale, 40f * scale, 32f * scale, 44f * scale)
            close()
        }
        drawPath(rightWingUp, color = wingCol)
        drawPath(leftWingUp, color = wingCol)
    } else if (wings == MascotWings.FLAPPING) {
        // Oscillating flutter wing using animated wingFlutterAngle
        val flutterRad = Math.toRadians(wingFlutterAngle.toDouble()).toFloat()
        val cosA = kotlin.math.cos(flutterRad)
        val sinA = kotlin.math.sin(flutterRad)
        val rx = 90f + (20f * cosA)
        val ry = 74f + (15f * sinA)

        val rightWingFlap = Path().apply {
            moveTo(88f * scale, 72f * scale)
            cubicTo((rx.toFloat()) * scale, (ry.toFloat()) * scale, 106f * scale, 94f * scale, 86f * scale, 86f * scale)
            close()
        }
        drawPath(rightWingFlap, color = wingCol)
    } else {
        // Default resting wing
        val restingWing = Path().apply {
            moveTo(90f * scale, 74f * scale)
            cubicTo(106f * scale, 78f * scale, 100f * scale, 98f * scale, 86f * scale, 86f * scale)
            close()
        }
        drawPath(restingWing, color = wingCol)
    }

    // 11. Modular Accessories (Medal, Scarf, Badge)
    when (accessory) {
        MascotAccessory.CHAMPION_MEDAL -> {
            val ribbon = Path().apply {
                moveTo(52f * scale, 78f * scale)
                lineTo(60f * scale, 92f * scale)
                lineTo(68f * scale, 78f * scale)
                close()
            }
            drawPath(ribbon, color = Color(0xFFFF3D00))
            drawCircle(color = Color(0xFFFFD700), radius = 8f * scale, center = Offset(60f * scale, 95f * scale))
            drawCircle(color = Color(0xFFFFEA00), radius = 4f * scale, center = Offset(60f * scale, 95f * scale))
        }
        MascotAccessory.HERO_SCARF -> {
            // Red fluttering superhero / aviator scarf
            val scarfNeck = Path().apply {
                moveTo(44f * scale, 70f * scale)
                quadraticBezierTo(60f * scale, 74f * scale, 76f * scale, 70f * scale)
                quadraticBezierTo(60f * scale, 80f * scale, 44f * scale, 70f * scale)
            }
            drawPath(scarfNeck, color = Color(0xFFD50000))
            // Fluttering tail of scarf drifting back
            val scarfTail = Path().apply {
                moveTo(74f * scale, 73f * scale)
                quadraticBezierTo(88f * scale, 80f * scale, 102f * scale, 76f * scale)
                quadraticBezierTo(92f * scale, 86f * scale, 74f * scale, 77f * scale)
                close()
            }
            drawPath(scarfTail, color = Color(0xFFFF1744))
        }
        MascotAccessory.STAR_BADGE -> {
            drawSparkle(60f * scale, 86f * scale, 6.5f * scale, Color(0xFFFFD700))
        }
        MascotAccessory.JETPACK, MascotAccessory.NONE -> Unit
    }

    // 12. Little orange feet (tucked in flight)
    if (isFlying) {
        // Tucked streamlined aerodynamic feet
        drawCircle(feetCol, radius = 3.5f * scale, center = Offset(50f * scale, 100f * scale))
        drawCircle(feetCol, radius = 3.5f * scale, center = Offset(66f * scale, 100f * scale))
    } else {
        drawLine(feetCol, Offset(52f * scale, 104f * scale), Offset(52f * scale, 112f * scale), strokeWidth = 4.5f * scale, cap = StrokeCap.Round)
        drawLine(feetCol, Offset(68f * scale, 104f * scale), Offset(68f * scale, 112f * scale), strokeWidth = 4.5f * scale, cap = StrokeCap.Round)
    }
}

private fun DrawScope.drawSparkle(cx: Float, cy: Float, radius: Float, color: Color) {
    val path = Path().apply {
        moveTo(cx, cy - radius)
        lineTo(cx + radius * 0.3f, cy - radius * 0.3f)
        lineTo(cx + radius, cy)
        lineTo(cx + radius * 0.3f, cy + radius * 0.3f)
        lineTo(cx, cy + radius)
        lineTo(cx - radius * 0.3f, cy + radius * 0.3f)
        lineTo(cx - radius, cy)
        lineTo(cx - radius * 0.3f, cy - radius * 0.3f)
        close()
    }
    drawPath(path, color)
}

/**
 * Signature Mascot Speech Hint component used across Skybound.
 */
@Composable
fun MascotHint(
    text: String,
    pose: MascotPose = MascotPose.HI,
    modifier: Modifier = Modifier
) {
    var bubbleText by remember(text) { mutableStateOf(text) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        MascotView(
            pose = pose,
            size = 68.dp,
            isInteractive = true,
            onSpeechChange = { quote ->
                bubbleText = quote
            }
        )

        Surface(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 1.dp,
            shadowElevation = 2.dp
        ) {
            Text(
                text = bubbleText,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp,
                    lineHeight = 19.sp,
                    fontWeight = FontWeight.Medium,
                    color = com.example.ui.theme.SkyNavy
                )
            )
        }
    }
}
