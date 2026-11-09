package com.example.skybound.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.skybound.data.*
import com.example.skybound.model.*
import com.example.skybound.notification.NotificationHelper
import com.example.skybound.ui.components.CelebrationData
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

class SkyboundViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    val repository = SkyboundRepository(db.skyboundDao(), application)

    // Raw database flows
    val allActivities: StateFlow<List<ActivityEntity>> = repository.allActivities
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val appSettings: StateFlow<AppSettingsEntity> = repository.appSettings
        .map { it ?: AppSettingsEntity() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppSettingsEntity())

    val currentActivity: StateFlow<ActivityEntity?> = combine(allActivities, appSettings) { acts, sets ->
        acts.find { it.id == sets.currentActivityId } ?: acts.firstOrNull()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Goals for current activity
    val goals: StateFlow<List<GoalEntity>> = currentActivity.flatMapLatest { act ->
        if (act != null) repository.getGoalsForActivity(act.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Feedback for current activity
    val feedbackList: StateFlow<List<FeedbackEntity>> = currentActivity.flatMapLatest { act ->
        if (act != null) repository.getFeedbackForActivity(act.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Sessions for current activity
    val sessions: StateFlow<List<SessionEntity>> = currentActivity.flatMapLatest { act ->
        if (act != null) repository.getSessionsForActivity(act.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Habits for current activity
    val habits: StateFlow<List<HabitEntity>> = currentActivity.flatMapLatest { act ->
        if (act != null) repository.getHabitsForActivity(act.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Records for current activity
    val records: StateFlow<List<RecordEntity>> = currentActivity.flatMapLatest { act ->
        if (act != null) repository.getRecordsForActivity(act.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val milestones: StateFlow<List<MilestoneEntity>> = repository.allMilestones
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Derived: Current Aim (1-3 focus goals)
    val currentAimGoals: StateFlow<List<GoalEntity>> = goals.map { list ->
        list.filter { it.isInCurrentAim && it.completedAt == null && it.retiredAt == null }
            .sortedBy { GoalPriority.fromCode(it.priorityCode).ordinal }
            .take(3)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Derived: Altitude Info
    val altitudeInfo: StateFlow<AltitudeInfo> = combine(goals, currentActivity) { goalList, act ->
        val finishedCount = goalList.count { it.completedAt != null }
        val peak = maxOf(finishedCount, act?.peakFinishedGoals ?: 0)
        calculateAltitude(peak)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), calculateAltitude(0))

    // Derived: Weekly Rhythm count
    val weeklyRhythmCount: StateFlow<Int> = combine(habits, sessions) { habList, sesList ->
        val today = currentDateKey()
        val habitHits = habList.count { Converters.parseStringList(it.loggedDatesJson).contains(today) }
        val sessionHits = sesList.count { Converters.parseStringList(it.completedDatesJson).contains(today) }
        habitHits + sessionHits
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Derived: Next Session info
    val nextSessionCountdown: StateFlow<String> = sessions.map { list ->
        val today = currentDateKey()
        val next = list.filter { it.dateKey >= today }.minByOrNull { it.dateKey }
        if (next == null) {
            "–"
        } else if (next.dateKey == today) {
            "Today"
        } else {
            // calculate day difference
            formatDateKey(next.dateKey)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "–")

    // Filter states for Goals Screen
    private val _statusFilter = MutableStateFlow("active") // "active", "done", "retired", "all"
    val statusFilter: StateFlow<String> = _statusFilter.asStateFlow()

    private val _priorityFilter = MutableStateFlow("all") // "all", "H", "M", "L"
    val priorityFilter: StateFlow<String> = _priorityFilter.asStateFlow()

    // Celebration events
    private val _activeCelebration = MutableStateFlow<CelebrationData?>(null)
    val activeCelebration: StateFlow<CelebrationData?> = _activeCelebration.asStateFlow()

    // Toast message event
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    init {
        viewModelScope.launch {
            repository.initializeDefaultsIfNeeded()
            NotificationHelper.createNotificationChannels(getApplication())
        }
    }

    fun setStatusFilter(filter: String) {
        _statusFilter.value = filter
    }

    fun setPriorityFilter(filter: String) {
        _priorityFilter.value = filter
    }

    fun dismissCelebration() {
        _activeCelebration.value = null
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    fun showToast(msg: String) {
        _toastMessage.value = msg
    }

    fun switchActivity(activityId: String) {
        viewModelScope.launch {
            repository.setCurrentActivity(activityId)
        }
    }

    fun createActivity(name: String, icon: String, color: String) {
        viewModelScope.launch {
            val act = repository.saveActivity(name, icon, color)
            repository.setCurrentActivity(act.id)
            showToast("Activity ${act.name} created!")
        }
    }

    fun deleteActivity(activityId: String) {
        viewModelScope.launch {
            repository.deleteActivity(activityId)
            showToast("Activity deleted")
        }
    }

    fun toggleGoalDone(goalId: String) {
        viewModelScope.launch {
            val updated = repository.toggleGoalCompletion(goalId)
            if (updated?.completedAt != null) {
                _activeCelebration.value = CelebrationData(
                    type = "goal",
                    title = "Goal Complete!",
                    subtitle = updated.title
                )
                showToast("Nice one! Goal finished.")
            }
        }
    }

    fun toggleCurrentAim(goalId: String) {
        viewModelScope.launch {
            val success = repository.toggleCurrentAim(goalId)
            if (!success) {
                showToast("Current Aim is full! 3 focus goals is the limit.")
            }
        }
    }

    fun logTodayAction(goalId: String) {
        viewModelScope.launch {
            repository.logTodayAction(goalId)
            showToast("Nice one! Today's step logged.")
        }
    }

    fun saveGoalOnly(title: String, priority: GoalPriority, putInCurrentAim: Boolean) {
        viewModelScope.launch {
            val act = currentActivity.value ?: return@launch
            repository.saveGoal(
                activityId = act.id,
                title = title,
                priority = priority,
                putInCurrentAim = putInCurrentAim
            )
            showToast("Goal saved!")
        }
    }

    fun saveFeedbackFlow(
        coachName: String,
        note: String,
        goals: List<Pair<String, GoalPriority>>,
        aimIndices: Set<Int>
    ) {
        viewModelScope.launch {
            val act = currentActivity.value ?: return@launch
            repository.saveFeedbackWithGoals(
                activityId = act.id,
                coachName = coachName,
                note = note,
                goals = goals,
                aimSelection = aimIndices
            )
            _activeCelebration.value = CelebrationData(
                type = "first",
                title = "Feedback Saved!",
                subtitle = "${goals.size} goals created from your coach note."
            )
        }
    }

    fun reviewGoal(
        goalId: String,
        action: String,
        newValue: String? = null,
        note: String? = null
    ) {
        viewModelScope.launch {
            repository.reviewGoal(goalId, action, newValue, note)
            _activeCelebration.value = CelebrationData(
                type = "review",
                title = "Review Done",
                subtitle = "Reflecting is how progress sticks."
            )
        }
    }

    fun toggleGoalStep(goalId: String, index: Int) {
        viewModelScope.launch {
            repository.toggleGoalStep(goalId, index)
        }
    }

    fun addGoalStep(goalId: String, text: String) {
        viewModelScope.launch {
            repository.addGoalStep(goalId, text)
        }
    }

    fun deleteGoalStep(goalId: String, index: Int) {
        viewModelScope.launch {
            repository.deleteGoalStep(goalId, index)
        }
    }

    fun updateGoalDetails(
        goalId: String,
        title: String,
        nextAction: String,
        successDefinition: String,
        priority: GoalPriority
    ) {
        viewModelScope.launch {
            repository.updateGoalDetails(goalId, title, nextAction, successDefinition, priority)
            showToast("Goal details updated")
        }
    }

    fun deleteGoal(goalId: String) {
        viewModelScope.launch {
            repository.deleteGoal(goalId)
            showToast("Goal deleted")
        }
    }

    fun reopenGoal(goalId: String) {
        viewModelScope.launch {
            repository.reopenGoal(goalId)
            showToast("Goal reopened")
        }
    }

    fun saveSession(title: String, dateKey: String, time: String, isRepeating: Boolean) {
        viewModelScope.launch {
            val act = currentActivity.value ?: return@launch
            repository.saveSession(act.id, title, dateKey, time, isRepeating)
            showToast("Session scheduled!")
        }
    }

    fun toggleSessionDate(sessionId: String, dateKey: String) {
        viewModelScope.launch {
            repository.toggleSessionDate(sessionId, dateKey)
        }
    }

    fun deleteSession(sessionId: String) {
        viewModelScope.launch {
            repository.deleteSession(sessionId)
            showToast("Session deleted")
        }
    }

    fun saveHabit(title: String, targetDays: Int, linkedGoalId: String?) {
        viewModelScope.launch {
            val act = currentActivity.value ?: return@launch
            repository.saveHabit(act.id, title, targetDays, linkedGoalId)
            showToast("Habit added!")
        }
    }

    fun toggleHabitLog(habitId: String, dateKey: String) {
        viewModelScope.launch {
            repository.toggleHabitLog(habitId, dateKey)
        }
    }

    fun deleteHabit(habitId: String) {
        viewModelScope.launch {
            repository.deleteHabit(habitId)
            showToast("Habit deleted")
        }
    }

    fun saveRecord(title: String, note: String, dateKey: String) {
        viewModelScope.launch {
            val act = currentActivity.value ?: return@launch
            repository.saveRecord(act.id, title, note, dateKey)
            _activeCelebration.value = CelebrationData(
                type = "record",
                title = "Personal Best!",
                subtitle = title
            )
        }
    }

    fun deleteRecord(recordId: String) {
        viewModelScope.launch {
            repository.deleteRecord(recordId)
            showToast("Record deleted")
        }
    }

    fun updateNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateSettings { it.copy(notificationsEnabled = enabled) }
        }
    }

    fun updatePreSessionReminder(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateSettings { it.copy(preSessionReminderEnabled = enabled) }
        }
    }

    fun updatePostSessionReflection(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateSettings { it.copy(postSessionReflectionEnabled = enabled) }
        }
    }

    fun updateDailyAimReminder(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateSettings { it.copy(dailyAimReminderEnabled = enabled) }
        }
    }

    fun updateQuietHours(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateSettings { it.copy(quietHoursEnabled = enabled) }
        }
    }

    fun updateWeeklyTarget(delta: Int) {
        viewModelScope.launch {
            repository.updateSettings { current ->
                val newTarget = (current.weeklyTarget + delta).coerceIn(1, 14)
                current.copy(weeklyTarget = newTarget)
            }
        }
    }

    fun toggleMascotQuiet() {
        viewModelScope.launch {
            repository.updateSettings { it.copy(isMascotQuiet = !it.isMascotQuiet) }
        }
    }

    fun dismissTeenTechBanner() {
        viewModelScope.launch {
            repository.updateSettings { it.copy(isTeenTechBannerDismissed = true) }
        }
    }

    fun updateAppearance(
        appName: String? = null,
        accentKey: String? = null,
        themeMode: String? = null,
        cornerStyle: String? = null,
        fontStyle: String? = null
    ) {
        viewModelScope.launch {
            repository.updateSettings { current ->
                current.copy(
                    appName = appName ?: current.appName,
                    accentKey = accentKey ?: current.accentKey,
                    themeMode = themeMode ?: current.themeMode,
                    cornerStyle = cornerStyle ?: current.cornerStyle,
                    fontStyle = fontStyle ?: current.fontStyle
                )
            }
            showToast("Appearance updated")
        }
    }

    fun loadExampleData() {
        viewModelScope.launch {
            repository.loadExampleData()
            showToast("Example football activity loaded!")
        }
    }

    fun removeExampleData() {
        viewModelScope.launch {
            repository.removeExampleData()
            showToast("Example data removed")
        }
    }

    fun sendTestNotification() {
        NotificationHelper.sendTestNotification(getApplication())
        showToast("Test notification sent!")
    }

    fun triggerPreSessionReminder() {
        val act = currentActivity.value ?: return
        val aimGoal = currentAimGoals.value.firstOrNull()?.title ?: "Practice drills"
        NotificationHelper.showPreSessionReminder(getApplication(), act.name, aimGoal)
        showToast("Pre-session reminder notification sent!")
    }

    fun triggerPostSessionReminder() {
        val act = currentActivity.value ?: return
        NotificationHelper.showPostSessionReflection(getApplication(), act.name)
        showToast("Post-session reflection notification sent!")
    }

    suspend fun exportJson(): String {
        return repository.exportBackupJson()
    }

    suspend fun restoreJson(json: String): Boolean {
        val result = repository.restoreBackupJson(json)
        if (result) {
            showToast("Backup restored successfully!")
        } else {
            showToast("Invalid backup format")
        }
        return result
    }

    fun wipeAllData() {
        viewModelScope.launch {
            repository.deleteAllData()
            showToast("All data cleared")
        }
    }
}
