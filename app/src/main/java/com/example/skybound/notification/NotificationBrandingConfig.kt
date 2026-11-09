package com.example.skybound.notification

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF

/**
 * Centralized branding configuration for Skybound notifications.
 *
 * Designed for extreme ease of modification when changing the mascot, logo, colors, or copy:
 * - Modify [APP_NAME], [MASCOT_NAME], [BRAND_TAGLINE] to change the brand identity.
 * - Modify [BRAND_COLOR_PRIMARY], [BRAND_COLOR_ACCENT] to change notification tint colors.
 * - Replace or adjust [getMascotNotificationBitmap] to update the notification avatar.
 */
object NotificationBrandingConfig {
    const val APP_NAME = "Skybound"
    const val MASCOT_NAME = "Pip"
    const val BRAND_TAGLINE = "Reach new heights · One step at a time"
    const val CHANNEL_REMINDERS_NAME = "Gentle Reminders"
    const val CHANNEL_REMINDERS_DESC = "Pre-session prompts and post-training reflections from Pip"
    const val CHANNEL_CELEBRATIONS_NAME = "Celebrations & Milestones"
    const val CHANNEL_CELEBRATIONS_DESC = "Goal completion, rank ups, and weekly rhythm milestones"

    // Primary Brand Colors for Notification Accent Tint
    const val BRAND_COLOR_PRIMARY = 0xFF1CB0F6.toInt() // Sky Blue
    const val BRAND_COLOR_ACCENT = 0xFFFFC800.toInt()  // Gold Milestone Sun
    const val BRAND_COLOR_NAVY = 0xFF0F2A4A.toInt()    // Deep Navy

    // Cached bitmap so we don't redraw repeatedly
    private var cachedMascotBitmap: Bitmap? = null

    /**
     * Generates a rich, crisp branded Mascot Bitmap (Pip the Bird) for use in notifications.
     * When permanent brand assets/logos are ready, simply replace the drawing logic below
     * or return BitmapFactory.decodeResource(context.resources, R.drawable.permanent_mascot).
     */
    fun getMascotNotificationBitmap(context: Context): Bitmap {
        cachedMascotBitmap?.let { return it }

        val size = 192
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // 1. Vibrant Circular Badge Background
        paint.color = BRAND_COLOR_PRIMARY
        canvas.drawCircle(size / 2f, size / 2f, size * 0.46f, paint)

        // Subtle darker rim for 3D depth
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 6f
        paint.color = 0xFF1899D6.toInt()
        canvas.drawCircle(size / 2f, size / 2f, size * 0.44f, paint)
        paint.style = Paint.Style.FILL

        // 2. Pip Golden Crest Tuft on top
        paint.color = 0xFFFFC800.toInt()
        val crestPath = Path().apply {
            moveTo(size * 0.50f, size * 0.22f)
            quadTo(size * 0.60f, size * 0.10f, size * 0.70f, size * 0.12f)
            quadTo(size * 0.62f, size * 0.22f, size * 0.58f, size * 0.26f)
            close()
        }
        canvas.drawPath(crestPath, paint)

        // 3. Pip Cheek / Wing Accent
        paint.color = 0xFF65D2FF.toInt()
        val wingRect = RectF(size * 0.16f, size * 0.48f, size * 0.46f, size * 0.78f)
        canvas.drawOval(wingRect, paint)

        // 4. Cheerful Big Mascot Eye
        paint.color = 0xFFFFFFFF.toInt()
        canvas.drawCircle(size * 0.54f, size * 0.44f, size * 0.17f, paint)

        // Eye Pupil (Deep Navy)
        paint.color = BRAND_COLOR_NAVY
        canvas.drawCircle(size * 0.57f, size * 0.44f, size * 0.10f, paint)

        // Eye Specular Highlight
        paint.color = 0xFFFFFFFF.toInt()
        canvas.drawCircle(size * 0.61f, size * 0.40f, size * 0.045f, paint)

        // 5. Friendly Mascot Beak (Golden Orange)
        paint.color = 0xFFFF9F43.toInt()
        val beakPath = Path().apply {
            moveTo(size * 0.68f, size * 0.46f)
            lineTo(size * 0.88f, size * 0.52f)
            lineTo(size * 0.68f, size * 0.58f)
            close()
        }
        canvas.drawPath(beakPath, paint)

        cachedMascotBitmap = bitmap
        return bitmap
    }
}
