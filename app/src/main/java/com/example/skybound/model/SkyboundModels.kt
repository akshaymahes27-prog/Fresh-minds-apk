package com.example.skybound.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * Activity preset for quick creation.
 */
data class ActivityPreset(
    val name: String,
    val iconKey: String,
    val colorHex: String
)

val DEFAULT_ACTIVITY_PRESETS = listOf(
    ActivityPreset("Sport", "ball", "#FF6B6B"),
    ActivityPreset("Music", "note", "#A78BFA"),
    ActivityPreset("Gym", "bell", "#FF9F43"),
    ActivityPreset("Dance", "heart", "#FF7EB6"),
    ActivityPreset("Study", "book", "#3DDC97"),
    ActivityPreset("Other", "star", "#FFC800")
)

/**
 * Goal priority enumeration.
 */
enum class GoalPriority(val code: String, val label: String) {
    HIGH("H", "High"),
    MEDIUM("M", "Medium"),
    LOW("L", "Low");

    companion object {
        fun fromCode(code: String): GoalPriority = when (code.uppercase()) {
            "H" -> HIGH
            "L" -> LOW
            else -> MEDIUM
        }
    }
}

/**
 * A sub-step of a goal.
 */
data class GoalStep(
    val title: String,
    val isDone: Boolean = false
)

/**
 * Timeline entry for goal audit history.
 */
data class GoalTimelineEntry(
    val timestamp: Long = System.currentTimeMillis(),
    val description: String
)

/**
 * Altitude ranks based solely on finished goals (personal progression, never compared).
 */
data class AltitudeRank(
    val level: Int,
    val title: String,
    val goalsRequired: Int,
    val badgeColorHex: String
)

val ALTITUDE_RANKS = listOf(
    AltitudeRank(1, "Hatchling", 0, "#CFE9FB"),
    AltitudeRank(2, "Fledgling", 1, "#3DDC97"),
    AltitudeRank(3, "Take-off", 3, "#1CB0F6"),
    AltitudeRank(4, "Climber", 6, "#FF9F43"),
    AltitudeRank(5, "Glider", 10, "#A78BFA"),
    AltitudeRank(6, "Soarer", 15, "#FF7EB6"),
    AltitudeRank(7, "Skyrider", 25, "#FFC800"),
    AltitudeRank(8, "Cloud Breaker", 40, "#FF6B6B"),
    AltitudeRank(9, "Stratosphere", 60, "#1899D6"),
    AltitudeRank(10, "Summit", 100, "#FFC800")
)

fun calculateAltitude(finishedGoalCount: Int): AltitudeInfo {
    var rankIndex = 0
    for (i in ALTITUDE_RANKS.indices) {
        if (finishedGoalCount >= ALTITUDE_RANKS[i].goalsRequired) {
            rankIndex = i
        }
    }
    val currentRank = ALTITUDE_RANKS[rankIndex]
    val nextRank = if (rankIndex + 1 < ALTITUDE_RANKS.size) ALTITUDE_RANKS[rankIndex + 1] else null

    val goalsInLevel = if (nextRank != null) nextRank.goalsRequired - currentRank.goalsRequired else 1
    val progressInLevel = if (nextRank != null) finishedGoalCount - currentRank.goalsRequired else goalsInLevel
    val progressPct = if (nextRank != null) {
        ((progressInLevel.toFloat() / goalsInLevel.toFloat()) * 100).toInt().coerceIn(0, 100)
    } else 100

    val goalsToNext = if (nextRank != null) (nextRank.goalsRequired - finishedGoalCount).coerceAtLeast(0) else 0

    return AltitudeInfo(
        currentRank = currentRank,
        nextRank = nextRank,
        totalFinished = finishedGoalCount,
        progressPercent = progressPct,
        goalsToNextRank = goalsToNext
    )
}

data class AltitudeInfo(
    val currentRank: AltitudeRank,
    val nextRank: AltitudeRank?,
    val totalFinished: Int,
    val progressPercent: Int,
    val goalsToNextRank: Int
)

/**
 * Milestones celebrating user effort and reflection.
 */
data class MilestoneDefinition(
    val id: String,
    val title: String,
    val description: String
)

val MILESTONES = listOf(
    MilestoneDefinition("fb", "Recorded your first feedback", "Captured advice from a coach or teacher"),
    MilestoneDefinition("goal1", "Completed your first goal", "Turned feedback into a finished result"),
    MilestoneDefinition("goal5", "Completed 5 goals", "Building real momentum in your training"),
    MilestoneDefinition("rev", "Reviewed a goal", "Reflected on progress and adapted next steps"),
    MilestoneDefinition("ses", "Attended your first session", "Showed up and practiced your craft"),
    MilestoneDefinition("hab10", "Logged habits 10 times", "Building steady daily discipline"),
    MilestoneDefinition("rec", "Added your first record", "Saved a personal achievement to remember"),
    MilestoneDefinition("wk", "Reached your weekly rhythm", "Completed your planned sessions this week")
)

/**
 * Appearance settings for the app.
 */
data class AppearanceSettings(
    val appName: String = "Skybound",
    val accentKey: String = "sky",
    val themeMode: String = "auto", // auto, light, dark
    val cornerStyle: String = "round", // round, soft, sharp
    val fontStyle: String = "nunito", // nunito, system
    val logoStyle: String = "bird", // bird, letter
    val customMascotName: String = "Sky",
    val customLogoPath: String? = null
)

val ACCENT_PALETTE = mapOf(
    "sky" to Triple("#1CB0F6", "#1899D6", "Sky"),
    "violet" to Triple("#A78BFA", "#7A55E0", "Violet"),
    "mint" to Triple("#3DDC97", "#1E8F61", "Mint"),
    "coral" to Triple("#FF6B6B", "#C93D3D", "Coral"),
    "orange" to Triple("#FF9F43", "#B5651D", "Orange")
)

/**
 * Helper to generate simple date keys (yyyy-MM-dd)
 */
fun currentDateKey(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return sdf.format(Date())
}

fun formatDateKey(dateKey: String): String {
    return try {
        val sdfIn = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = sdfIn.parse(dateKey) ?: return dateKey
        val sdfOut = SimpleDateFormat("EEE, d MMM", Locale.getDefault())
        sdfOut.format(date)
    } catch (e: Exception) {
        dateKey
    }
}
