package com.example.skybound.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import com.example.skybound.data.AppDatabase
import com.example.skybound.data.Converters
import com.example.skybound.model.calculateAltitude
import com.example.skybound.model.currentDateKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

/**
 * AppWidgetProvider for the Skybound Home Screen Widget.
 * Highly customizable with both Small and Medium layout support.
 */
class SkyboundWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        updateWidgets(context, appWidgetManager, appWidgetIds)
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle?
    ) {
        updateWidgets(context, appWidgetManager, intArrayOf(appWidgetId))
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
    }

    companion object {
        fun updateWidgets(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
            CoroutineScope(Dispatchers.IO).launch {
                val db = AppDatabase.getDatabase(context)
                val dao = db.skyboundDao()

                val settings = dao.getAppSettingsDirect()
                val activities = dao.getAllActivities().firstOrNull() ?: emptyList()
                val currentActivity = activities.find { it.id == settings?.currentActivityId } ?: activities.firstOrNull()

                val activityName = currentActivity?.name ?: "Skybound"
                val allGoals = if (currentActivity != null) dao.getGoalsForActivity(currentActivity.id).firstOrNull() ?: emptyList() else emptyList()
                val focusGoals = allGoals.filter { it.isInCurrentAim && it.completedAt == null && it.retiredAt == null }

                val finishedGoals = allGoals.count { it.completedAt != null }
                val altitude = calculateAltitude(maxOf(finishedGoals, currentActivity?.peakFinishedGoals ?: 0))

                // Sessions & Habits for rhythm
                val today = currentDateKey()
                val habits = if (currentActivity != null) dao.getHabitsForActivity(currentActivity.id).firstOrNull() ?: emptyList() else emptyList()
                val habitHits = habits.count { Converters.parseStringList(it.loggedDatesJson).contains(today) }

                val sessions = if (currentActivity != null) dao.getSessionsForActivity(currentActivity.id).firstOrNull() ?: emptyList() else emptyList()
                val nextSession = sessions.firstOrNull { it.dateKey >= today }

                val weeklyTarget = settings?.weeklyTarget ?: 3
                val rhythmText = "🔥 $habitHits/$weeklyTarget"

                for (widgetId in appWidgetIds) {
                    val options = appWidgetManager.getAppWidgetOptions(widgetId)
                    val minWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 140)
                    val isMedium = minWidth >= 220

                    val views = if (isMedium) {
                        RemoteViews(context.packageName, R.layout.widget_medium).apply {
                            setTextViewText(R.id.widget_activity_name, "$activityName")
                            setTextViewText(R.id.widget_rank_label, "🛡️ ${altitude.currentRank.title}")
                            setTextViewText(R.id.widget_rhythm_badge, rhythmText)

                            if (focusGoals.isNotEmpty()) {
                                setTextViewText(R.id.widget_goal_1, "1. ${focusGoals[0].title}")
                            } else {
                                setTextViewText(R.id.widget_goal_1, "No focus goals selected")
                            }

                            if (focusGoals.size > 1) {
                                setTextViewText(R.id.widget_goal_2, "2. ${focusGoals[1].title}")
                            } else {
                                setTextViewText(R.id.widget_goal_2, "")
                            }

                            if (focusGoals.size > 2) {
                                setTextViewText(R.id.widget_goal_3, "3. ${focusGoals[2].title}")
                            } else {
                                setTextViewText(R.id.widget_goal_3, "")
                            }

                            val sessionText = if (nextSession != null) {
                                "📅 Next: ${nextSession.title} (${nextSession.dateKey} ${nextSession.time})"
                            } else {
                                "📅 Next: No upcoming sessions"
                            }
                            setTextViewText(R.id.widget_session_info, sessionText)
                        }
                    } else {
                        RemoteViews(context.packageName, R.layout.widget_small).apply {
                            setTextViewText(R.id.widget_activity_name, activityName)
                            setTextViewText(R.id.widget_rhythm_badge, rhythmText)
                            setTextViewText(R.id.widget_rank_label, "${altitude.currentRank.title} · ${altitude.totalFinished} done")

                            val topGoal = focusGoals.firstOrNull()
                            if (topGoal != null) {
                                setTextViewText(R.id.widget_goal_title, topGoal.title)
                                val nextAction = if (topGoal.nextAction.isNotBlank()) "Next: ${topGoal.nextAction}" else "Tap to open goal"
                                setTextViewText(R.id.widget_next_action, nextAction)
                            } else {
                                setTextViewText(R.id.widget_goal_title, "No Current Aim yet")
                                setTextViewText(R.id.widget_next_action, "Tap to pick focus goals")
                            }
                        }
                    }

                    // Click opens MainActivity
                    val launchIntent = Intent(context, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        putExtra("EXTRA_TAB", "home")
                    }
                    val pendingIntent = PendingIntent.getActivity(
                        context,
                        widgetId,
                        launchIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

                    appWidgetManager.updateAppWidget(widgetId, views)
                }
            }
        }
    }
}
