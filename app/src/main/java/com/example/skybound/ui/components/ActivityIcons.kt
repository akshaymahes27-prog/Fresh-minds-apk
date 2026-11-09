package com.example.skybound.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Rich, handcrafted vector icons for activities (Sport, Music, Gym, Dance, Study, Other).
 * Replaces generic single-letter placeholders with premium athletic and creative illustrations.
 */
@Composable
fun ActivityIcon(
    iconKey: String,
    tint: Color = Color.White,
    modifier: Modifier = Modifier,
    size: Dp = 24.dp
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height
            val strokeW = (w * 0.08f).coerceAtLeast(1.5f)

            when (iconKey.lowercase()) {
                "ball", "sport", "football" -> {
                    // Detailed soccer ball with pentagon center and seam lines
                    val radius = w * 0.44f
                    val center = Offset(w * 0.5f, h * 0.5f)
                    drawCircle(color = tint, radius = radius, center = center, style = Stroke(width = strokeW))

                    // Center pentagon
                    val pRadius = radius * 0.45f
                    val pentagon = Path()
                    for (i in 0 until 5) {
                        val angle = Math.toRadians((i * 72.0) - 90.0)
                        val px = (center.x + pRadius * Math.cos(angle)).toFloat()
                        val py = (center.y + pRadius * Math.sin(angle)).toFloat()
                        if (i == 0) pentagon.moveTo(px, py) else pentagon.lineTo(px, py)
                    }
                    pentagon.close()
                    drawPath(pentagon, color = tint)

                    // Seam lines radiating from pentagon vertices to circle edge
                    for (i in 0 until 5) {
                        val angle = Math.toRadians((i * 72.0) - 90.0)
                        val p1x = (center.x + pRadius * Math.cos(angle)).toFloat()
                        val p1y = (center.y + pRadius * Math.sin(angle)).toFloat()
                        val p2x = (center.x + radius * Math.cos(angle)).toFloat()
                        val p2y = (center.y + radius * Math.sin(angle)).toFloat()
                        drawLine(tint, Offset(p1x, p1y), Offset(p2x, p2y), strokeWidth = strokeW * 0.8f, cap = StrokeCap.Round)
                    }
                }
                "note", "music" -> {
                    // Beamed eighth notes with vibrant musical noteheads
                    val noteRadius = w * 0.16f
                    val leftHead = Offset(w * 0.28f, h * 0.72f)
                    val rightHead = Offset(w * 0.72f, h * 0.60f)

                    // Filled tilted noteheads
                    drawOval(tint, topLeft = Offset(leftHead.x - noteRadius * 1.2f, leftHead.y - noteRadius * 0.8f), size = Size(noteRadius * 2.2f, noteRadius * 1.5f))
                    drawOval(tint, topLeft = Offset(rightHead.x - noteRadius * 1.2f, rightHead.y - noteRadius * 0.8f), size = Size(noteRadius * 2.2f, noteRadius * 1.5f))

                    // Stems
                    val leftStemTop = Offset(leftHead.x + noteRadius * 0.8f, h * 0.22f)
                    val rightStemTop = Offset(rightHead.x + noteRadius * 0.8f, h * 0.12f)
                    drawLine(tint, Offset(leftHead.x + noteRadius * 0.8f, leftHead.y), leftStemTop, strokeWidth = strokeW * 1.2f, cap = StrokeCap.Round)
                    drawLine(tint, Offset(rightHead.x + noteRadius * 0.8f, rightHead.y), rightStemTop, strokeWidth = strokeW * 1.2f, cap = StrokeCap.Round)

                    // Connecting thick beam
                    val beam = Path().apply {
                        moveTo(leftStemTop.x - strokeW * 0.6f, leftStemTop.y)
                        lineTo(rightStemTop.x + strokeW * 0.6f, rightStemTop.y)
                        lineTo(rightStemTop.x + strokeW * 0.6f, rightStemTop.y + strokeW * 1.8f)
                        lineTo(leftStemTop.x - strokeW * 0.6f, leftStemTop.y + strokeW * 1.8f)
                        close()
                    }
                    drawPath(beam, tint)
                }
                "bell", "gym", "fitness" -> {
                    // Heavy workout dumbbell with plates & knurled bar
                    val barY = h * 0.5f
                    // Center grip bar
                    drawLine(tint, Offset(w * 0.25f, barY), Offset(w * 0.75f, barY), strokeWidth = strokeW * 1.4f, cap = StrokeCap.Round)

                    // Left weight plates
                    drawRoundRect(tint, Offset(w * 0.20f, h * 0.26f), Size(w * 0.08f, h * 0.48f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.03f))
                    drawRoundRect(tint, Offset(w * 0.10f, h * 0.34f), Size(w * 0.08f, h * 0.32f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.03f))

                    // Right weight plates
                    drawRoundRect(tint, Offset(w * 0.72f, h * 0.26f), Size(w * 0.08f, h * 0.48f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.03f))
                    drawRoundRect(tint, Offset(w * 0.82f, h * 0.34f), Size(w * 0.08f, h * 0.32f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.03f))
                }
                "heart", "dance" -> {
                    // Elegant heart with rhythmic ribbon flow
                    val heartPath = Path().apply {
                        moveTo(w * 0.5f, h * 0.82f)
                        cubicTo(w * 0.15f, h * 0.55f, w * 0.08f, h * 0.22f, w * 0.32f, h * 0.18f)
                        cubicTo(w * 0.44f, h * 0.16f, w * 0.5f, h * 0.28f, w * 0.5f, h * 0.32f)
                        cubicTo(w * 0.5f, h * 0.28f, w * 0.56f, h * 0.16f, w * 0.68f, h * 0.18f)
                        cubicTo(w * 0.92f, h * 0.22f, w * 0.85f, h * 0.55f, w * 0.5f, h * 0.82f)
                        close()
                    }
                    drawPath(heartPath, color = tint)

                    // Sparkle on top-right
                    drawCircle(color = Color.White, radius = w * 0.06f, center = Offset(w * 0.68f, h * 0.32f))
                }
                "book", "study" -> {
                    // Open study book with pages and spine
                    val bookPath = Path().apply {
                        // Left page
                        moveTo(w * 0.5f, h * 0.78f)
                        cubicTo(w * 0.35f, h * 0.72f, w * 0.2f, h * 0.75f, w * 0.12f, h * 0.80f)
                        lineTo(w * 0.12f, h * 0.30f)
                        cubicTo(w * 0.22f, h * 0.24f, w * 0.38f, h * 0.22f, w * 0.5f, h * 0.28f)
                        // Right page
                        cubicTo(w * 0.62f, h * 0.22f, w * 0.78f, h * 0.24f, w * 0.88f, h * 0.30f)
                        lineTo(w * 0.88f, h * 0.80f)
                        cubicTo(w * 0.80f, h * 0.75f, w * 0.65f, h * 0.72f, w * 0.5f, h * 0.78f)
                        close()
                    }
                    drawPath(bookPath, color = tint, style = Stroke(width = strokeW * 1.1f, join = StrokeJoin.Round))
                    // Center spine
                    drawLine(tint, Offset(w * 0.5f, h * 0.28f), Offset(w * 0.5f, h * 0.82f), strokeWidth = strokeW * 1.1f, cap = StrokeCap.Round)
                }
                else -> {
                    // Star / Achievement badge
                    val starPath = Path()
                    val outerR = w * 0.46f
                    val innerR = outerR * 0.48f
                    val cx = w * 0.5f
                    val cy = h * 0.52f

                    for (i in 0 until 10) {
                        val r = if (i % 2 == 0) outerR else innerR
                        val angle = Math.toRadians((i * 36.0) - 90.0)
                        val x = (cx + r * Math.cos(angle)).toFloat()
                        val y = (cy + r * Math.sin(angle)).toFloat()
                        if (i == 0) starPath.moveTo(x, y) else starPath.lineTo(x, y)
                    }
                    starPath.close()
                    drawPath(starPath, color = tint)
                }
            }
        }
    }
}
