package com.example.skybound.data

import android.content.Context
import com.example.skybound.model.*
import com.example.skybound.widget.WidgetUpdater
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class SkyboundRepository(
    private val dao: SkyboundDao,
    private val context: Context
) {
    val allActivities: Flow<List<ActivityEntity>> = dao.getAllActivities()
    val allMilestones: Flow<List<MilestoneEntity>> = dao.getAllMilestones()
    val appSettings: Flow<AppSettingsEntity?> = dao.getAppSettings()

    fun getGoalsForActivity(activityId: String): Flow<List<GoalEntity>> =
        dao.getGoalsForActivity(activityId)

    fun getFeedbackForActivity(activityId: String): Flow<List<FeedbackEntity>> =
        dao.getFeedbackForActivity(activityId)

    fun getSessionsForActivity(activityId: String): Flow<List<SessionEntity>> =
        dao.getSessionsForActivity(activityId)

    fun getHabitsForActivity(activityId: String): Flow<List<HabitEntity>> =
        dao.getHabitsForActivity(activityId)

    fun getRecordsForActivity(activityId: String): Flow<List<RecordEntity>> =
        dao.getRecordsForActivity(activityId)

    suspend fun initializeDefaultsIfNeeded() {
        var settings = dao.getAppSettingsDirect()
        if (settings == null) {
            settings = AppSettingsEntity()
            dao.saveAppSettings(settings)
        }

        val activities = dao.getAllActivities().firstOrNull() ?: emptyList()
        if (activities.isEmpty()) {
            // Seed first activity if user has none
            val initialActivity = ActivityEntity(
                id = UUID.randomUUID().toString(),
                name = "Sport",
                icon = "ball",
                color = "#FF6B6B"
            )
            dao.insertActivity(initialActivity)
            dao.saveAppSettings(settings.copy(currentActivityId = initialActivity.id))
        }
    }

    suspend fun saveActivity(name: String, icon: String, color: String, id: String? = null): ActivityEntity {
        val activity = if (id != null) {
            val existing = dao.getActivityById(id)
            existing?.copy(name = name, icon = icon, color = color)
                ?: ActivityEntity(id = id, name = name, icon = icon, color = color)
        } else {
            ActivityEntity(
                id = UUID.randomUUID().toString(),
                name = name,
                icon = icon,
                color = color
            )
        }
        dao.insertActivity(activity)
        notifyWidget()
        return activity
    }

    suspend fun deleteActivity(activityId: String) {
        dao.deleteActivity(activityId)
        dao.deleteGoalsForActivity(activityId)
        dao.deleteFeedbackForActivity(activityId)
        dao.deleteSessionsForActivity(activityId)
        dao.deleteHabitsForActivity(activityId)
        dao.deleteRecordsForActivity(activityId)

        val settings = dao.getAppSettingsDirect()
        if (settings?.currentActivityId == activityId) {
            val remaining = dao.getAllActivities().firstOrNull() ?: emptyList()
            val nextId = remaining.firstOrNull()?.id
            dao.saveAppSettings(settings.copy(currentActivityId = nextId))
        }
        notifyWidget()
    }

    suspend fun setCurrentActivity(activityId: String) {
        val settings = dao.getAppSettingsDirect() ?: AppSettingsEntity()
        dao.saveAppSettings(settings.copy(currentActivityId = activityId))
        notifyWidget()
    }

    suspend fun saveGoal(
        activityId: String,
        title: String,
        priority: GoalPriority = GoalPriority.MEDIUM,
        nextAction: String = "",
        successDefinition: String = "",
        feedbackId: String? = null,
        steps: List<GoalStep> = emptyList(),
        putInCurrentAim: Boolean = false
    ): GoalEntity {
        val initialTimeline = listOf(
            GoalTimelineEntry(
                timestamp = System.currentTimeMillis(),
                description = "Goal created"
            )
        )
        val goal = GoalEntity(
            id = UUID.randomUUID().toString(),
            activityId = activityId,
            title = title,
            priorityCode = priority.code,
            nextAction = nextAction,
            successDefinition = successDefinition,
            isInCurrentAim = putInCurrentAim,
            feedbackId = feedbackId,
            stepsJson = Converters.stepsToJson(steps),
            timelineJson = Converters.timelineToJson(initialTimeline)
        )
        dao.insertGoal(goal)
        notifyWidget()
        return goal
    }

    suspend fun toggleGoalCompletion(goalId: String): GoalEntity? {
        val goal = dao.getGoalById(goalId) ?: return null
        val newCompleted = if (goal.completedAt == null) System.currentTimeMillis() else null

        val currentSteps = Converters.parseSteps(goal.stepsJson)
        val updatedSteps = if (newCompleted != null) {
            currentSteps.map { it.copy(isDone = true) }
        } else {
            currentSteps
        }

        val timeline = Converters.parseTimeline(goal.timelineJson).toMutableList()
        timeline.add(
            0,
            GoalTimelineEntry(
                timestamp = System.currentTimeMillis(),
                description = if (newCompleted != null) "Completed" else "Marked not done"
            )
        )

        val updatedGoal = goal.copy(
            completedAt = newCompleted,
            stepsJson = Converters.stepsToJson(updatedSteps),
            timelineJson = Converters.timelineToJson(timeline)
        )
        dao.updateGoal(updatedGoal)

        // Update peak altitude on activity
        if (newCompleted != null) {
            val activity = dao.getActivityById(goal.activityId)
            if (activity != null) {
                val allGoals = dao.getGoalsForActivity(goal.activityId).firstOrNull() ?: emptyList()
                val finishedCount = allGoals.count { it.completedAt != null }
                val newPeak = maxOf(activity.peakFinishedGoals, finishedCount)
                val alt = calculateAltitude(newPeak)
                dao.updateActivity(
                    activity.copy(
                        peakFinishedGoals = newPeak,
                        lastRankReached = maxOf(activity.lastRankReached, alt.currentRank.level)
                    )
                )
            }
        }

        notifyWidget()
        return updatedGoal
    }

    suspend fun toggleCurrentAim(goalId: String): Boolean {
        val goal = dao.getGoalById(goalId) ?: return false
        if (!goal.isInCurrentAim) {
            // Check count of focus goals in Current Aim (max 3)
            val goals = dao.getGoalsForActivity(goal.activityId).firstOrNull() ?: emptyList()
            val activeAimCount = goals.count { it.isInCurrentAim && it.completedAt == null && it.retiredAt == null }
            if (activeAimCount >= 3) {
                return false // Full, cannot add without removing one first
            }
        }
        dao.updateGoal(goal.copy(isInCurrentAim = !goal.isInCurrentAim))
        notifyWidget()
        return true
    }

    suspend fun updateGoalDetails(
        goalId: String,
        title: String,
        nextAction: String,
        successDefinition: String,
        priority: GoalPriority
    ) {
        val goal = dao.getGoalById(goalId) ?: return
        val timeline = Converters.parseTimeline(goal.timelineJson).toMutableList()

        if (goal.title != title) {
            timeline.add(0, GoalTimelineEntry(description = "Reworded from: ${goal.title}"))
        }
        if (goal.nextAction != nextAction) {
            timeline.add(0, GoalTimelineEntry(description = if (nextAction.isNotBlank()) "Next action: $nextAction" else "Next action cleared"))
        }
        if (goal.successDefinition != successDefinition) {
            timeline.add(0, GoalTimelineEntry(description = "Success description updated"))
        }

        val updated = goal.copy(
            title = title,
            nextAction = nextAction,
            successDefinition = successDefinition,
            priorityCode = priority.code,
            timelineJson = Converters.timelineToJson(timeline)
        )
        dao.updateGoal(updated)
        notifyWidget()
    }

    suspend fun toggleGoalStep(goalId: String, stepIndex: Int) {
        val goal = dao.getGoalById(goalId) ?: return
        val steps = Converters.parseSteps(goal.stepsJson).toMutableList()
        if (stepIndex in steps.indices) {
            val step = steps[stepIndex]
            val toggled = step.copy(isDone = !step.isDone)
            steps[stepIndex] = toggled

            val allDone = steps.isNotEmpty() && steps.all { it.isDone }
            val newCompleted = if (allDone) (goal.completedAt ?: System.currentTimeMillis()) else null

            val timeline = Converters.parseTimeline(goal.timelineJson).toMutableList()
            timeline.add(0, GoalTimelineEntry(description = if (toggled.isDone) "Step done: ${toggled.title}" else "Step reopened: ${toggled.title}"))

            dao.updateGoal(
                goal.copy(
                    stepsJson = Converters.stepsToJson(steps),
                    completedAt = newCompleted,
                    timelineJson = Converters.timelineToJson(timeline)
                )
            )
            notifyWidget()
        }
    }

    suspend fun addGoalStep(goalId: String, stepTitle: String) {
        val goal = dao.getGoalById(goalId) ?: return
        val steps = Converters.parseSteps(goal.stepsJson).toMutableList()
        steps.add(GoalStep(title = stepTitle, isDone = false))
        dao.updateGoal(goal.copy(stepsJson = Converters.stepsToJson(steps), completedAt = null))
        notifyWidget()
    }

    suspend fun deleteGoalStep(goalId: String, stepIndex: Int) {
        val goal = dao.getGoalById(goalId) ?: return
        val steps = Converters.parseSteps(goal.stepsJson).toMutableList()
        if (stepIndex in steps.indices) {
            steps.removeAt(stepIndex)
            val allDone = steps.isNotEmpty() && steps.all { it.isDone }
            val newCompleted = if (allDone) (goal.completedAt ?: System.currentTimeMillis()) else null
            dao.updateGoal(
                goal.copy(
                    stepsJson = Converters.stepsToJson(steps),
                    completedAt = newCompleted
                )
            )
            notifyWidget()
        }
    }

    suspend fun logTodayAction(goalId: String) {
        val goal = dao.getGoalById(goalId) ?: return
        val timeline = Converters.parseTimeline(goal.timelineJson).toMutableList()
        timeline.add(0, GoalTimelineEntry(description = "Did: ${goal.nextAction}"))
        dao.updateGoal(goal.copy(timelineJson = Converters.timelineToJson(timeline)))
    }

    suspend fun reviewGoal(
        goalId: String,
        action: String, // "keep", "edit", "shrink", "change", "retire"
        newValue: String? = null,
        note: String? = null
    ) {
        val goal = dao.getGoalById(goalId) ?: return
        val timeline = Converters.parseTimeline(goal.timelineJson).toMutableList()

        if (!note.isNullOrBlank()) {
            timeline.add(0, GoalTimelineEntry(description = "Review note: $note"))
        }

        when (action) {
            "keep" -> {
                timeline.add(0, GoalTimelineEntry(description = "Reviewed: keep going"))
                dao.updateGoal(goal.copy(timelineJson = Converters.timelineToJson(timeline)))
            }
            "edit" -> {
                val newTitle = newValue?.trim() ?: goal.title
                timeline.add(0, GoalTimelineEntry(description = "Reworded from: ${goal.title}"))
                dao.updateGoal(goal.copy(title = newTitle, timelineJson = Converters.timelineToJson(timeline)))
            }
            "shrink" -> {
                val newTitle = newValue?.trim() ?: goal.title
                timeline.add(0, GoalTimelineEntry(description = "Made smaller from: ${goal.title}"))
                dao.updateGoal(goal.copy(title = newTitle, timelineJson = Converters.timelineToJson(timeline)))
            }
            "change" -> {
                val newTitle = newValue?.trim() ?: return
                timeline.add(0, GoalTimelineEntry(description = "Swapped for: $newTitle"))
                dao.updateGoal(goal.copy(retiredAt = System.currentTimeMillis(), isInCurrentAim = false, timelineJson = Converters.timelineToJson(timeline)))

                // Insert the new swapped-in goal
                val swappedGoal = GoalEntity(
                    id = UUID.randomUUID().toString(),
                    activityId = goal.activityId,
                    title = newTitle,
                    priorityCode = goal.priorityCode,
                    isInCurrentAim = true,
                    feedbackId = goal.feedbackId,
                    timelineJson = Converters.timelineToJson(
                        listOf(GoalTimelineEntry(description = "Swapped in for: ${goal.title}"))
                    )
                )
                dao.insertGoal(swappedGoal)
            }
            "retire" -> {
                timeline.add(0, GoalTimelineEntry(description = "Retired"))
                dao.updateGoal(
                    goal.copy(
                        retiredAt = System.currentTimeMillis(),
                        isInCurrentAim = false,
                        timelineJson = Converters.timelineToJson(timeline)
                    )
                )
            }
        }
        notifyWidget()
    }

    suspend fun reopenGoal(goalId: String) {
        val goal = dao.getGoalById(goalId) ?: return
        val timeline = Converters.parseTimeline(goal.timelineJson).toMutableList()
        timeline.add(0, GoalTimelineEntry(description = "Reopened"))
        dao.updateGoal(goal.copy(retiredAt = null, completedAt = null, timelineJson = Converters.timelineToJson(timeline)))
        notifyWidget()
    }

    suspend fun deleteGoal(goalId: String) {
        dao.deleteGoal(goalId)
        notifyWidget()
    }

    // --- Feedback Flow ---
    suspend fun saveFeedbackWithGoals(
        activityId: String,
        coachName: String,
        note: String,
        goals: List<Pair<String, GoalPriority>>,
        aimSelection: Set<Int>
    ) {
        val feedbackId = UUID.randomUUID().toString()
        val feedback = FeedbackEntity(
            id = feedbackId,
            activityId = activityId,
            coachName = coachName.trim(),
            note = note.trim()
        )
        dao.insertFeedback(feedback)

        val newGoals = goals.mapIndexed { index, (title, priority) ->
            GoalEntity(
                id = UUID.randomUUID().toString(),
                activityId = activityId,
                title = title.trim(),
                priorityCode = priority.code,
                isInCurrentAim = aimSelection.contains(index),
                feedbackId = feedbackId,
                timelineJson = Converters.timelineToJson(
                    listOf(GoalTimelineEntry(description = "Created from feedback"))
                )
            )
        }
        dao.insertGoals(newGoals)
        dao.unlockMilestone(MilestoneEntity("fb"))
        notifyWidget()
    }

    // --- Sessions ---
    suspend fun saveSession(
        activityId: String,
        title: String,
        dateKey: String,
        time: String = "",
        isRepeatingWeekly: Boolean = false
    ) {
        val session = SessionEntity(
            id = UUID.randomUUID().toString(),
            activityId = activityId,
            title = title.trim(),
            dateKey = dateKey,
            time = time.trim(),
            isRepeatingWeekly = isRepeatingWeekly
        )
        dao.insertSession(session)
        notifyWidget()
    }

    suspend fun toggleSessionDate(sessionId: String, dateKey: String) {
        val session = dao.getAllSessions().firstOrNull()?.find { it.id == sessionId } ?: return
        val completedList = Converters.parseStringList(session.completedDatesJson).toMutableList()
        if (completedList.contains(dateKey)) {
            completedList.remove(dateKey)
        } else {
            completedList.add(dateKey)
            dao.unlockMilestone(MilestoneEntity("ses"))
        }
        dao.updateSession(session.copy(completedDatesJson = Converters.stringListToJson(completedList)))
        notifyWidget()
    }

    suspend fun deleteSession(sessionId: String) {
        dao.deleteSession(sessionId)
        notifyWidget()
    }

    // --- Habits ---
    suspend fun saveHabit(
        activityId: String,
        title: String,
        targetDaysPerWeek: Int,
        linkedGoalId: String? = null
    ) {
        val habit = HabitEntity(
            id = UUID.randomUUID().toString(),
            activityId = activityId,
            title = title.trim(),
            targetDaysPerWeek = targetDaysPerWeek,
            linkedGoalId = linkedGoalId
        )
        dao.insertHabit(habit)
        notifyWidget()
    }

    suspend fun toggleHabitLog(habitId: String, dateKey: String) {
        val habit = dao.getAllHabits().firstOrNull()?.find { it.id == habitId } ?: return
        val logs = Converters.parseStringList(habit.loggedDatesJson).toMutableList()
        if (logs.contains(dateKey)) {
            logs.remove(dateKey)
        } else {
            logs.add(dateKey)
        }
        dao.updateHabit(habit.copy(loggedDatesJson = Converters.stringListToJson(logs)))

        // Check 10 logs milestone
        val allHabits = dao.getAllHabits().firstOrNull() ?: emptyList()
        val totalLogs = allHabits.sumOf { Converters.parseStringList(it.loggedDatesJson).size }
        if (totalLogs >= 10) {
            dao.unlockMilestone(MilestoneEntity("hab10"))
        }

        notifyWidget()
    }

    suspend fun deleteHabit(habitId: String) {
        dao.deleteHabit(habitId)
        notifyWidget()
    }

    // --- Personal Records ---
    suspend fun saveRecord(activityId: String, title: String, note: String, dateKey: String) {
        val record = RecordEntity(
            id = UUID.randomUUID().toString(),
            activityId = activityId,
            title = title.trim(),
            note = note.trim(),
            dateKey = dateKey
        )
        dao.insertRecord(record)
        dao.unlockMilestone(MilestoneEntity("rec"))
    }

    suspend fun deleteRecord(recordId: String) {
        dao.deleteRecord(recordId)
    }

    // --- Settings & Appearance ---
    suspend fun updateSettings(transform: (AppSettingsEntity) -> AppSettingsEntity) {
        val current = dao.getAppSettingsDirect() ?: AppSettingsEntity()
        val updated = transform(current)
        dao.saveAppSettings(updated)
        notifyWidget()
    }

    // --- Example Data Injection ---
    suspend fun loadExampleData() {
        val exampleActId = UUID.randomUUID().toString()
        val act = ActivityEntity(
            id = exampleActId,
            name = "Football (Example)",
            icon = "ball",
            color = "#FF6B6B",
            isExample = true,
            peakFinishedGoals = 1,
            lastRankReached = 2
        )
        dao.insertActivity(act)

        val fbId = UUID.randomUUID().toString()
        val fb = FeedbackEntity(
            id = fbId,
            activityId = exampleActId,
            coachName = "Coach Sam (example)",
            note = "Use your weaker foot more in drills, and warm up properly before sessions.",
            isExample = true
        )
        dao.insertFeedback(fb)

        val g1 = GoalEntity(
            id = UUID.randomUUID().toString(),
            activityId = exampleActId,
            title = "Use my weaker foot in every drill",
            priorityCode = "H",
            nextAction = "Pass 20 times with my left foot",
            successDefinition = "I pick my left foot without thinking",
            isInCurrentAim = true,
            feedbackId = fbId,
            stepsJson = Converters.stepsToJson(
                listOf(
                    GoalStep("Left-foot passing, 10 minutes", true),
                    GoalStep("Left-foot shooting, 10 minutes", false)
                )
            ),
            timelineJson = Converters.timelineToJson(
                listOf(GoalTimelineEntry(description = "Goal created from coach feedback"))
            ),
            isExample = true
        )
        val g2 = GoalEntity(
            id = UUID.randomUUID().toString(),
            activityId = exampleActId,
            title = "Warm up properly before every session",
            priorityCode = "M",
            nextAction = "10 min dynamic mobility",
            isInCurrentAim = true,
            feedbackId = fbId,
            isExample = true
        )
        val g3 = GoalEntity(
            id = UUID.randomUUID().toString(),
            activityId = exampleActId,
            title = "Learn the new set-piece routine",
            priorityCode = "L",
            completedAt = System.currentTimeMillis() - 86400000L * 2,
            isExample = true
        )
        dao.insertGoals(listOf(g1, g2, g3))

        val ses = SessionEntity(
            id = UUID.randomUUID().toString(),
            activityId = exampleActId,
            title = "Weekly training",
            dateKey = currentDateKey(),
            time = "18:00",
            isRepeatingWeekly = true,
            isExample = true
        )
        dao.insertSession(ses)

        val hab = HabitEntity(
            id = UUID.randomUUID().toString(),
            activityId = exampleActId,
            title = "10 minutes of ball control",
            targetDaysPerWeek = 3,
            loggedDatesJson = Converters.stringListToJson(listOf(currentDateKey())),
            isExample = true
        )
        dao.insertHabit(hab)

        val rec = RecordEntity(
            id = UUID.randomUUID().toString(),
            activityId = exampleActId,
            title = "Ran the whole session without stopping",
            note = "High energy throughout",
            dateKey = currentDateKey(),
            isExample = true
        )
        dao.insertRecord(rec)

        setCurrentActivity(exampleActId)
    }

    suspend fun removeExampleData() {
        dao.deleteExampleActivities()
        dao.deleteExampleGoals()
        dao.deleteExampleFeedback()
        dao.deleteExampleSessions()
        dao.deleteExampleHabits()
        dao.deleteExampleRecords()

        val activities = dao.getAllActivities().firstOrNull() ?: emptyList()
        val settings = dao.getAppSettingsDirect()
        if (activities.isNotEmpty()) {
            dao.saveAppSettings(settings?.copy(currentActivityId = activities.first().id) ?: AppSettingsEntity())
        }
        notifyWidget()
    }

    // --- JSON Backup Export & Import ---
    suspend fun exportBackupJson(): String {
        val root = JSONObject()
        root.put("version", "0.98")
        root.put("exportedAt", System.currentTimeMillis())

        val acts = JSONArray()
        dao.getAllActivities().firstOrNull()?.forEach {
            val a = JSONObject()
            a.put("id", it.id)
            a.put("name", it.name)
            a.put("icon", it.icon)
            a.put("color", it.color)
            a.put("peak", it.peakFinishedGoals)
            acts.put(a)
        }
        root.put("acts", acts)

        val goals = JSONArray()
        dao.getAllGoals().firstOrNull()?.forEach {
            val g = JSONObject()
            g.put("id", it.id)
            g.put("act", it.activityId)
            g.put("title", it.title)
            g.put("pri", it.priorityCode)
            g.put("na", it.nextAction)
            g.put("succ", it.successDefinition)
            g.put("aim", it.isInCurrentAim)
            g.put("done", it.completedAt)
            g.put("ret", it.retiredAt)
            g.put("steps", JSONArray(it.stepsJson))
            goals.put(g)
        }
        root.put("goals", goals)

        // Update backup timestamp
        val settings = dao.getAppSettingsDirect()
        if (settings != null) {
            dao.saveAppSettings(settings.copy(lastBackupTimestamp = System.currentTimeMillis()))
        }

        return root.toString(2)
    }

    suspend fun restoreBackupJson(jsonString: String): Boolean {
        return try {
            val root = JSONObject(jsonString)
            if (!root.has("acts")) return false

            dao.clearActivities()
            dao.clearGoals()
            dao.clearFeedback()
            dao.clearSessions()
            dao.clearHabits()
            dao.clearRecords()

            val actsArray = root.getJSONArray("acts")
            var firstActId: String? = null
            for (i in 0 until actsArray.length()) {
                val obj = actsArray.getJSONObject(i)
                val id = obj.optString("id", UUID.randomUUID().toString())
                if (firstActId == null) firstActId = id
                dao.insertActivity(
                    ActivityEntity(
                        id = id,
                        name = obj.optString("name", "Activity"),
                        icon = obj.optString("icon", "star"),
                        color = obj.optString("color", "#1CB0F6"),
                        peakFinishedGoals = obj.optInt("peak", 0)
                    )
                )
            }

            if (root.has("goals")) {
                val goalsArray = root.getJSONArray("goals")
                for (i in 0 until goalsArray.length()) {
                    val obj = goalsArray.getJSONObject(i)
                    val stepsStr = obj.optJSONArray("steps")?.toString() ?: "[]"
                    dao.insertGoal(
                        GoalEntity(
                            id = obj.optString("id", UUID.randomUUID().toString()),
                            activityId = obj.optString("act", firstActId ?: ""),
                            title = obj.optString("title", "Goal"),
                            priorityCode = obj.optString("pri", "M"),
                            nextAction = obj.optString("na", ""),
                            successDefinition = obj.optString("succ", ""),
                            isInCurrentAim = obj.optBoolean("aim", false),
                            completedAt = if (obj.has("done") && !obj.isNull("done")) obj.getLong("done") else null,
                            retiredAt = if (obj.has("ret") && !obj.isNull("ret")) obj.getLong("ret") else null,
                            stepsJson = stepsStr
                        )
                    )
                }
            }

            if (firstActId != null) {
                setCurrentActivity(firstActId)
            }
            notifyWidget()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteAllData() {
        dao.clearActivities()
        dao.clearGoals()
        dao.clearFeedback()
        dao.clearSessions()
        dao.clearHabits()
        dao.clearRecords()
        dao.clearMilestones()
        dao.saveAppSettings(AppSettingsEntity())
        notifyWidget()
    }

    private fun notifyWidget() {
        try {
            WidgetUpdater.updateAllWidgets(context)
        } catch (e: Exception) {
            // Safe ignore if widget not yet bound
        }
    }
}
