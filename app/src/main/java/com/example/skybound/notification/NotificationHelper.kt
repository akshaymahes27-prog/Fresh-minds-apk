package com.example.skybound.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R

object NotificationHelper {
    const val CHANNEL_REMINDERS = "skybound_reminders"
    const val CHANNEL_CELEBRATIONS = "skybound_celebrations"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val remindersChannel = NotificationChannel(
                CHANNEL_REMINDERS,
                NotificationBrandingConfig.CHANNEL_REMINDERS_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = NotificationBrandingConfig.CHANNEL_REMINDERS_DESC
                enableVibration(true)
            }

            val celebrationsChannel = NotificationChannel(
                CHANNEL_CELEBRATIONS,
                NotificationBrandingConfig.CHANNEL_CELEBRATIONS_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = NotificationBrandingConfig.CHANNEL_CELEBRATIONS_DESC
                enableVibration(true)
            }

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(remindersChannel)
            manager.createNotificationChannel(celebrationsChannel)
        }
    }

    private fun getLaunchIntent(context: Context, extraTab: String = "home"): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("EXTRA_TAB", extraTab)
        }
        return PendingIntent.getActivity(
            context,
            extraTab.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun showPreSessionReminder(context: Context, activityName: String, focusGoalTitle: String) {
        val pendingIntent = getLaunchIntent(context, "home")
        val mascotBitmap = NotificationBrandingConfig.getMascotNotificationBitmap(context)

        val builder = NotificationCompat.Builder(context, CHANNEL_REMINDERS)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(mascotBitmap)
            .setColor(NotificationBrandingConfig.BRAND_COLOR_PRIMARY)
            .setSubText("${NotificationBrandingConfig.APP_NAME} · ${NotificationBrandingConfig.MASCOT_NAME}")
            .setContentTitle("Ready for $activityName?")
            .setContentText("Focus today: \"$focusGoalTitle\"")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Training is coming up! Your Current Aim for today:\n\n\"$focusGoalTitle\"\n\nTake a deep breath and give it your best effort! 🪶")
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .addAction(0, "Open Aim", pendingIntent)
            .setAutoCancel(true)

        try {
            NotificationManagerCompat.from(context).notify(201, builder.build())
        } catch (e: SecurityException) {
            // Notifications permission not granted
        }
    }

    fun showPostSessionReflection(context: Context, activityName: String) {
        val pendingIntent = getLaunchIntent(context, "home")
        val mascotBitmap = NotificationBrandingConfig.getMascotNotificationBitmap(context)

        val builder = NotificationCompat.Builder(context, CHANNEL_REMINDERS)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(mascotBitmap)
            .setColor(NotificationBrandingConfig.BRAND_COLOR_PRIMARY)
            .setSubText("${NotificationBrandingConfig.APP_NAME} · ${NotificationBrandingConfig.MASCOT_NAME}")
            .setContentTitle("How did $activityName go?")
            .setContentText("A quick 2-minute review locks in what you learned.")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Great work completing your session!\n\nA quick 2-minute reflection locks in what you learned and adapts your goals for next time. ✨")
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .addAction(0, "Log Reflection", pendingIntent)
            .setAutoCancel(true)

        try {
            NotificationManagerCompat.from(context).notify(202, builder.build())
        } catch (e: SecurityException) {
            // Notifications permission not granted
        }
    }

    fun showDailyAimNudge(context: Context, goalTitle: String, nextAction: String) {
        val pendingIntent = getLaunchIntent(context, "home")
        val mascotBitmap = NotificationBrandingConfig.getMascotNotificationBitmap(context)
        val actionText = if (nextAction.isNotBlank()) "Next step: $nextAction" else "One small step on your Current Aim is plenty."

        val builder = NotificationCompat.Builder(context, CHANNEL_REMINDERS)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(mascotBitmap)
            .setColor(NotificationBrandingConfig.BRAND_COLOR_PRIMARY)
            .setSubText("${NotificationBrandingConfig.APP_NAME} · ${NotificationBrandingConfig.MASCOT_NAME}")
            .setContentTitle("Today's Current Aim")
            .setContentText("$goalTitle · $actionText")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Goal: $goalTitle\n\n$actionText\n\nNo stress — steady progress over time wins the climb.")
            )
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
            .addAction(0, "View Goal", pendingIntent)
            .setAutoCancel(true)

        try {
            NotificationManagerCompat.from(context).notify(203, builder.build())
        } catch (e: SecurityException) {
            // Notifications permission not granted
        }
    }

    fun sendTestNotification(context: Context) {
        val pendingIntent = getLaunchIntent(context, "home")
        val mascotBitmap = NotificationBrandingConfig.getMascotNotificationBitmap(context)

        val builder = NotificationCompat.Builder(context, CHANNEL_REMINDERS)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(mascotBitmap)
            .setColor(NotificationBrandingConfig.BRAND_COLOR_PRIMARY)
            .setSubText("${NotificationBrandingConfig.APP_NAME} · ${NotificationBrandingConfig.MASCOT_NAME}")
            .setContentTitle("🪶 Pip says: Notifications are working!")
            .setContentText("Branded reminders are active. You'll receive kind, non-nagging prompts.")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("🎉 Welcome to Skybound!\n\nYour notifications are fully configured with custom branding. You'll receive gentle pre-session prompts, post-training reflections, and milestone celebrations — with zero guilt or streak stress.")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .addAction(0, "Open Skybound", pendingIntent)
            .setAutoCancel(true)

        try {
            NotificationManagerCompat.from(context).notify(299, builder.build())
        } catch (e: SecurityException) {
            // Permission missing
        }
    }

    fun showRankUpCelebration(context: Context, rankTitle: String) {
        val pendingIntent = getLaunchIntent(context, "home")
        val mascotBitmap = NotificationBrandingConfig.getMascotNotificationBitmap(context)

        val builder = NotificationCompat.Builder(context, CHANNEL_CELEBRATIONS)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(mascotBitmap)
            .setColor(NotificationBrandingConfig.BRAND_COLOR_ACCENT)
            .setSubText("${NotificationBrandingConfig.APP_NAME} · Altitude Rank Up!")
            .setContentTitle("🛡️ Altitude Unlocked: $rankTitle!")
            .setContentText("Congratulations! Your consistent training reached a new altitude.")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("🛡️ You reached $rankTitle altitude!\n\nEvery small step and reflection adds up. Keep soaring with Pip! 🪶")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .addAction(0, "View Rank", pendingIntent)
            .setAutoCancel(true)

        try {
            NotificationManagerCompat.from(context).notify(204, builder.build())
        } catch (e: SecurityException) {
            // Permission missing
        }
    }
}
