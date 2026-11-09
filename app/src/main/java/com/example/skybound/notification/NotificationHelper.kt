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

object NotificationHelper {
    const val CHANNEL_REMINDERS = "skybound_reminders"
    const val CHANNEL_CELEBRATIONS = "skybound_celebrations"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val remindersChannel = NotificationChannel(
                CHANNEL_REMINDERS,
                "Gentle Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Pre-session prompts and post-training reflections"
                enableVibration(true)
            }

            val celebrationsChannel = NotificationChannel(
                CHANNEL_CELEBRATIONS,
                "Celebrations & Milestones",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Goal completion, rank ups, and weekly rhythm milestones"
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
            101,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun showPreSessionReminder(context: Context, activityName: String, focusGoalTitle: String) {
        val pendingIntent = getLaunchIntent(context, "home")
        val builder = NotificationCompat.Builder(context, CHANNEL_REMINDERS)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Skybound · Before $activityName")
            .setContentText("Session coming up! Your focus today: $focusGoalTitle")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Session coming up! Your focus today:\n\"$focusGoalTitle\"\nTake a deep breath and give it your best.")
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            NotificationManagerCompat.from(context).notify(201, builder.build())
        } catch (e: SecurityException) {
            // Notifications permission not granted
        }
    }

    fun showPostSessionReflection(context: Context, activityName: String) {
        val pendingIntent = getLaunchIntent(context, "home")
        val builder = NotificationCompat.Builder(context, CHANNEL_REMINDERS)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Skybound · After $activityName")
            .setContentText("How did training go? A quick 2-minute review keeps your progress.")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("How did training go?\nA 2-minute review locks in what you learned and adapts your goals.")
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            NotificationManagerCompat.from(context).notify(202, builder.build())
        } catch (e: SecurityException) {
            // Notifications permission not granted
        }
    }

    fun showDailyAimNudge(context: Context, goalTitle: String, nextAction: String) {
        val pendingIntent = getLaunchIntent(context, "home")
        val actionText = if (nextAction.isNotBlank()) "Next step: $nextAction" else "One small step on your Current Aim is plenty."
        val builder = NotificationCompat.Builder(context, CHANNEL_REMINDERS)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Skybound · Today's Step")
            .setContentText("$goalTitle · $actionText")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            NotificationManagerCompat.from(context).notify(203, builder.build())
        } catch (e: SecurityException) {
            // Notifications permission not granted
        }
    }

    fun sendTestNotification(context: Context) {
        val pendingIntent = getLaunchIntent(context, "home")
        val builder = NotificationCompat.Builder(context, CHANNEL_REMINDERS)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Skybound · Gentle Notification Test")
            .setContentText("Your notifications are working perfectly! You'll receive kind, non-nagging reminders.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            NotificationManagerCompat.from(context).notify(299, builder.build())
        } catch (e: SecurityException) {
            // Handled
        }
    }
}
