package com.example.skybound.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activities")
data class ActivityEntity(
    @PrimaryKey val id: String,
    val name: String,
    val icon: String,
    val color: String,
    val isExample: Boolean = false,
    val peakFinishedGoals: Int = 0,
    val lastRankReached: Int = 1,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey val id: String,
    val activityId: String,
    val title: String,
    val priorityCode: String = "M",
    val nextAction: String = "",
    val successDefinition: String = "",
    val isInCurrentAim: Boolean = false,
    val completedAt: Long? = null,
    val retiredAt: Long? = null,
    val feedbackId: String? = null,
    val stepsJson: String = "[]",
    val timelineJson: String = "[]",
    val createdAt: Long = System.currentTimeMillis(),
    val isExample: Boolean = false
)

@Entity(tableName = "feedback")
data class FeedbackEntity(
    @PrimaryKey val id: String,
    val activityId: String,
    val coachName: String = "",
    val note: String,
    val createdAt: Long = System.currentTimeMillis(),
    val isExample: Boolean = false
)

@Entity(tableName = "sessions")
data class SessionEntity(
    @PrimaryKey val id: String,
    val activityId: String,
    val title: String,
    val dateKey: String, // yyyy-MM-dd
    val time: String = "", // HH:mm
    val isRepeatingWeekly: Boolean = false,
    val completedDatesJson: String = "[]", // JSON array of completed date keys
    val createdAt: Long = System.currentTimeMillis(),
    val isExample: Boolean = false
)

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey val id: String,
    val activityId: String,
    val title: String,
    val targetDaysPerWeek: Int = 3,
    val linkedGoalId: String? = null,
    val loggedDatesJson: String = "[]", // JSON array of date keys
    val createdAt: Long = System.currentTimeMillis(),
    val isExample: Boolean = false
)

@Entity(tableName = "records")
data class RecordEntity(
    @PrimaryKey val id: String,
    val activityId: String,
    val title: String,
    val note: String = "",
    val dateKey: String,
    val createdAt: Long = System.currentTimeMillis(),
    val isExample: Boolean = false
)

@Entity(tableName = "milestones")
data class MilestoneEntity(
    @PrimaryKey val milestoneId: String,
    val unlockedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val currentActivityId: String? = null,
    val weeklyTarget: Int = 3,
    val isMascotQuiet: Boolean = false,
    val isTeenTechBannerDismissed: Boolean = false,
    val appName: String = "Skybound",
    val accentKey: String = "sky",
    val themeMode: String = "auto", // auto, light, dark
    val cornerStyle: String = "round", // round, soft, sharp
    val fontStyle: String = "nunito",
    val logoStyle: String = "bird",
    val notificationsEnabled: Boolean = true,
    val preSessionReminderEnabled: Boolean = true,
    val postSessionReflectionEnabled: Boolean = true,
    val dailyAimReminderEnabled: Boolean = true,
    val quietHoursEnabled: Boolean = true,
    val quietHoursStart: String = "21:00",
    val quietHoursEnd: String = "08:00",
    val lastBackupTimestamp: Long = 0L
)
