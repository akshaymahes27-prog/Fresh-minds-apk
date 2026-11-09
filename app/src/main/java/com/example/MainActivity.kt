package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.skybound.data.*
import com.example.skybound.model.*
import com.example.skybound.ui.components.*
import com.example.skybound.ui.screens.*
import com.example.skybound.viewmodel.SkyboundViewModel
import com.example.ui.theme.*
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val viewModel: SkyboundViewModel = viewModel()
            val appSettings by viewModel.appSettings.collectAsStateWithLifecycle()
            val allActivities by viewModel.allActivities.collectAsStateWithLifecycle()
            val currentActivity by viewModel.currentActivity.collectAsStateWithLifecycle()
            val goals by viewModel.goals.collectAsStateWithLifecycle()
            val currentAimGoals by viewModel.currentAimGoals.collectAsStateWithLifecycle()
            val feedbackList by viewModel.feedbackList.collectAsStateWithLifecycle()
            val sessions by viewModel.sessions.collectAsStateWithLifecycle()
            val habits by viewModel.habits.collectAsStateWithLifecycle()
            val records by viewModel.records.collectAsStateWithLifecycle()
            val milestones by viewModel.milestones.collectAsStateWithLifecycle()
            val altitudeInfo by viewModel.altitudeInfo.collectAsStateWithLifecycle()
            val weeklyRhythmCount by viewModel.weeklyRhythmCount.collectAsStateWithLifecycle()
            val nextSessionCountdown by viewModel.nextSessionCountdown.collectAsStateWithLifecycle()
            val statusFilter by viewModel.statusFilter.collectAsStateWithLifecycle()
            val priorityFilter by viewModel.priorityFilter.collectAsStateWithLifecycle()
            val celebration by viewModel.activeCelebration.collectAsStateWithLifecycle()
            val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()

            // Accent Color from Settings
            val accentTriple = ACCENT_PALETTE[appSettings.accentKey] ?: ACCENT_PALETTE["sky"]!!
            val accentColor = Color(android.graphics.Color.parseColor(accentTriple.first))

            SkyboundTheme(
                themeMode = appSettings.themeMode,
                accentPrimary = accentColor,
                cornerStyle = appSettings.cornerStyle
            ) {
                val snackbarHostState = remember { SnackbarHostState() }
                val coroutineScope = rememberCoroutineScope()

                // Display snackbars
                LaunchedEffect(toastMessage) {
                    toastMessage?.let { msg ->
                        snackbarHostState.showSnackbar(msg)
                        viewModel.clearToast()
                    }
                }

                // Navigation State
                var currentTab by remember { mutableStateOf(SkyboundTab.HOME) }
                var activeSubScreen by remember { mutableStateOf<String?>(null) }
                var selectedGoalId by remember { mutableStateOf<String?>(null) }

                // Dialog and Sheet State
                var showActivitySwitcher by remember { mutableStateOf(false) }
                var showAltitudeSheet by remember { mutableStateOf(false) }
                var showQuickAddSheet by remember { mutableStateOf(false) }
                var showAddGoalDialog by remember { mutableStateOf(false) }
                var showNewActivityDialog by remember { mutableStateOf(false) }
                var showScheduleSessionDialog by remember { mutableStateOf(false) }
                var showAddHabitDialog by remember { mutableStateOf(false) }
                var showBackupDialog by remember { mutableStateOf(false) }
                var showRestoreDialog by remember { mutableStateOf(false) }
                var backupJsonContent by remember { mutableStateOf("") }
                var restoreJsonInput by remember { mutableStateOf("") }

                // BackHandler
                BackHandler(enabled = activeSubScreen != null || showQuickAddSheet || showActivitySwitcher || showAltitudeSheet) {
                    if (showQuickAddSheet) showQuickAddSheet = false
                    else if (showActivitySwitcher) showActivitySwitcher = false
                    else if (showAltitudeSheet) showAltitudeSheet = false
                    else activeSubScreen = null
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    topBar = {
                        if (activeSubScreen == null) {
                            DuolingoTopBar(
                                appName = appSettings.appName,
                                currentActivity = currentActivity,
                                altitudeInfo = altitudeInfo,
                                weeklyRhythmCount = weeklyRhythmCount,
                                weeklyTarget = appSettings.weeklyTarget,
                                currentAimCount = currentAimGoals.size,
                                nextSessionCountdown = nextSessionCountdown,
                                onActivityClick = { showActivitySwitcher = true },
                                onWeeklyRhythmClick = { currentTab = SkyboundTab.HOME },
                                onCurrentAimClick = { currentTab = SkyboundTab.HOME },
                                onNextSessionClick = { currentTab = SkyboundTab.CALENDAR },
                                onSettingsClick = { activeSubScreen = "more" }
                            )
                        }
                    },
                    bottomBar = {
                        if (activeSubScreen == null) {
                            val today = currentDateKey()
                            val hasSessionToday = sessions.any { it.dateKey == today }
                            val hasUnreviewedGoals = currentAimGoals.any { it.completedAt == null && it.retiredAt == null }

                            DuolingoBottomBar(
                                currentTab = currentTab,
                                onTabSelected = { selected ->
                                    currentTab = selected
                                    activeSubScreen = null
                                },
                                onQuickAddClick = { showQuickAddSheet = true },
                                hasGoalsAttention = hasUnreviewedGoals,
                                hasCalendarAttention = hasSessionToday
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        // Routing Logic
                        when (activeSubScreen) {
                            "goal_detail" -> {
                                val currentGoal = goals.find { it.id == selectedGoalId }
                                if (currentGoal != null) {
                                    val linkedFb = feedbackList.find { it.id == currentGoal.feedbackId }
                                    GoalDetailScreen(
                                        goal = currentGoal,
                                        linkedFeedback = linkedFb,
                                        onBack = { activeSubScreen = null },
                                        onSaveDetails = { title, nextAction, successDef, priority ->
                                            viewModel.updateGoalDetails(currentGoal.id, title, nextAction, successDef, priority)
                                        },
                                        onToggleAim = { viewModel.toggleCurrentAim(currentGoal.id) },
                                        onToggleStep = { idx -> viewModel.toggleGoalStep(currentGoal.id, idx) },
                                        onAddStep = { text -> viewModel.addGoalStep(currentGoal.id, text) },
                                        onDeleteStep = { idx -> viewModel.deleteGoalStep(currentGoal.id, idx) },
                                        onStartReview = { activeSubScreen = "review_flow" },
                                        onReopenGoal = {
                                            viewModel.reopenGoal(currentGoal.id)
                                            activeSubScreen = null
                                        },
                                        onDeleteGoal = {
                                            viewModel.deleteGoal(currentGoal.id)
                                            activeSubScreen = null
                                        }
                                    )
                                } else {
                                    activeSubScreen = null
                                }
                            }
                            "feedback_flow" -> {
                                FeedbackFlowScreen(
                                    activityName = currentActivity?.name ?: "Activity",
                                    onComplete = { coach, note, newGoals, aimSelection ->
                                        viewModel.saveFeedbackFlow(coach, note, newGoals, aimSelection)
                                        activeSubScreen = null
                                        currentTab = SkyboundTab.HOME
                                    },
                                    onDismiss = { activeSubScreen = null }
                                )
                            }
                            "review_flow" -> {
                                val reviewTargetGoal = goals.find { it.id == selectedGoalId } ?: currentAimGoals.firstOrNull()
                                if (reviewTargetGoal != null) {
                                    ReviewFlowScreen(
                                        goal = reviewTargetGoal,
                                        onReviewAction = { action, newVal, note ->
                                            viewModel.reviewGoal(reviewTargetGoal.id, action, newVal, note)
                                            activeSubScreen = null
                                        },
                                        onDismiss = { activeSubScreen = null }
                                    )
                                } else {
                                    activeSubScreen = null
                                }
                            }
                            "tour" -> {
                                TeenTechTourScreen(onDismiss = { activeSubScreen = null })
                            }
                            "about" -> {
                                AboutScreen(onBack = { activeSubScreen = null })
                            }
                            "records" -> {
                                RecordsScreen(
                                    activityName = currentActivity?.name ?: "Activity",
                                    records = records,
                                    milestones = milestones,
                                    onAddRecord = { t, n, d -> viewModel.saveRecord(t, n, d) },
                                    onDeleteRecord = { id -> viewModel.deleteRecord(id) },
                                    onBack = { activeSubScreen = null }
                                )
                            }
                            "templates" -> {
                                TemplatesScreen(
                                    activityName = currentActivity?.name ?: "Activity",
                                    onUseTemplate = { title, priority ->
                                        viewModel.saveGoalOnly(title, priority, putInCurrentAim = currentAimGoals.size < 3)
                                        activeSubScreen = null
                                        currentTab = SkyboundTab.GOALS
                                    },
                                    onBack = { activeSubScreen = null }
                                )
                            }
                            "report" -> {
                                ProgressReportScreen(
                                    activityName = currentActivity?.name ?: "Activity",
                                    goals = goals,
                                    feedbackList = feedbackList,
                                    sessions = sessions,
                                    habits = habits,
                                    records = records,
                                    onBack = { activeSubScreen = null }
                                )
                            }
                            "widget" -> {
                                WidgetConfigScreen(
                                    currentActivity = currentActivity,
                                    altitudeInfo = altitudeInfo,
                                    currentAimGoals = currentAimGoals,
                                    weeklyRhythmCount = weeklyRhythmCount,
                                    weeklyTarget = appSettings.weeklyTarget,
                                    nextSessionCountdown = nextSessionCountdown,
                                    onBack = { activeSubScreen = null }
                                )
                            }
                            "notifications" -> {
                                NotificationSettingsScreen(
                                    appSettings = appSettings,
                                    onToggleMaster = { viewModel.updateNotificationsEnabled(it) },
                                    onTogglePreSession = { viewModel.updatePreSessionReminder(it) },
                                    onTogglePostSession = { viewModel.updatePostSessionReflection(it) },
                                    onToggleDailyAim = { viewModel.updateDailyAimReminder(it) },
                                    onToggleQuietHours = { viewModel.updateQuietHours(it) },
                                    onSendTestNotification = { viewModel.sendTestNotification() },
                                    onTriggerPreSessionReminder = { viewModel.triggerPreSessionReminder() },
                                    onTriggerPostSessionReminder = { viewModel.triggerPostSessionReminder() },
                                    onBack = { activeSubScreen = null }
                                )
                            }
                            "appearance" -> {
                                AppearanceScreen(
                                    appSettings = appSettings,
                                    onUpdateAppearance = { name, acc, th, cr, fn ->
                                        viewModel.updateAppearance(name, acc, th, cr, fn)
                                    },
                                    onBack = { activeSubScreen = null }
                                )
                            }
                            "more" -> {
                                MoreScreen(
                                    appSettings = appSettings,
                                    activities = allActivities,
                                    onNavigate = { route -> activeSubScreen = route },
                                    onWeeklyTargetChange = { delta -> viewModel.updateWeeklyTarget(delta) },
                                    onToggleMascotQuiet = { viewModel.toggleMascotQuiet() },
                                    onAddNewActivity = { showNewActivityDialog = true },
                                    onDeleteActivity = { id -> viewModel.deleteActivity(id) },
                                    onLoadExampleData = { viewModel.loadExampleData() },
                                    onRemoveExampleData = { viewModel.removeExampleData() },
                                    onExportBackup = {
                                        coroutineScope.launch {
                                            backupJsonContent = viewModel.exportJson()
                                            showBackupDialog = true
                                        }
                                    },
                                    onRestoreBackup = {
                                        restoreJsonInput = ""
                                        showRestoreDialog = true
                                    },
                                    onWipeData = { viewModel.wipeAllData() }
                                )
                            }
                            null -> {
                                when (currentTab) {
                                    SkyboundTab.HOME -> {
                                        HomeScreen(
                                            currentActivity = currentActivity,
                                            currentAimGoals = currentAimGoals,
                                            feedbackList = feedbackList,
                                            sessions = sessions,
                                            habits = habits,
                                            weeklyTarget = appSettings.weeklyTarget,
                                            isTeenTechBannerDismissed = appSettings.isTeenTechBannerDismissed,
                                            isMascotQuiet = appSettings.isMascotQuiet,
                                            onGoalClick = { id ->
                                                selectedGoalId = id
                                                activeSubScreen = "goal_detail"
                                            },
                                            onToggleGoalDone = { id -> viewModel.toggleGoalDone(id) },
                                            onToggleGoalAim = { id -> viewModel.toggleCurrentAim(id) },
                                            onLogTodayStep = { id -> viewModel.logTodayAction(id) },
                                            onReviewCurrentAim = {
                                                selectedGoalId = currentAimGoals.firstOrNull()?.id
                                                activeSubScreen = "review_flow"
                                            },
                                            onRecordFeedback = { activeSubScreen = "feedback_flow" },
                                            onScheduleSession = { showScheduleSessionDialog = true },
                                            onAddHabit = { showAddHabitDialog = true },
                                            onToggleHabitLog = { id, d -> viewModel.toggleHabitLog(id, d) },
                                            onToggleSessionDate = { id, d -> viewModel.toggleSessionDate(id, d) },
                                            onStartTour = { activeSubScreen = "tour" },
                                            onDismissTourBanner = { viewModel.dismissTeenTechBanner() },
                                            onOpenGoalsTab = { currentTab = SkyboundTab.GOALS }
                                        )
                                    }
                                    SkyboundTab.GOALS -> {
                                        GoalsScreen(
                                            currentActivity = currentActivity,
                                            goals = goals,
                                            statusFilter = statusFilter,
                                            priorityFilter = priorityFilter,
                                            onStatusFilterChange = { viewModel.setStatusFilter(it) },
                                            onPriorityFilterChange = { viewModel.setPriorityFilter(it) },
                                            onGoalClick = { id ->
                                                selectedGoalId = id
                                                activeSubScreen = "goal_detail"
                                            },
                                            onToggleGoalDone = { id -> viewModel.toggleGoalDone(id) },
                                            onToggleGoalAim = { id -> viewModel.toggleCurrentAim(id) },
                                            onAddGoal = { showAddGoalDialog = true },
                                            onRecordFeedback = { activeSubScreen = "feedback_flow" }
                                        )
                                    }
                                    SkyboundTab.CALENDAR -> {
                                        CalendarScreen(
                                            sessions = sessions,
                                            feedbackList = feedbackList,
                                            habits = habits,
                                            onToggleSession = { id, d -> viewModel.toggleSessionDate(id, d) },
                                            onDeleteSession = { id -> viewModel.deleteSession(id) },
                                            onScheduleSession = { showScheduleSessionDialog = true }
                                        )
                                    }
                                    SkyboundTab.HISTORY -> {
                                        HistoryScreen(
                                            currentActivity = currentActivity,
                                            feedbackList = feedbackList,
                                            goals = goals,
                                            sessions = sessions,
                                            onGoalClick = { id ->
                                                selectedGoalId = id
                                                activeSubScreen = "goal_detail"
                                            },
                                            onToggleGoalDone = { id -> viewModel.toggleGoalDone(id) },
                                            onToggleGoalAim = { id -> viewModel.toggleCurrentAim(id) }
                                        )
                                    }
                                }
                            }
                        }

                        // Celebration Modal Dialog
                        celebration?.let { cel ->
                            CelebrationDialog(
                                data = cel,
                                onDismiss = { viewModel.dismissCelebration() }
                            )
                        }

                        // Activity Switcher Modal Bottom Sheet
                        if (showActivitySwitcher) {
                            ActivitySwitcherSheet(
                                activities = allActivities,
                                selectedActivity = currentActivity,
                                altitudeInfo = altitudeInfo,
                                onSelectActivity = { id -> viewModel.switchActivity(id) },
                                onAddNewActivity = { showNewActivityDialog = true },
                                onViewAltitudeDetails = {
                                    showActivitySwitcher = false
                                    showAltitudeSheet = true
                                },
                                onDismiss = { showActivitySwitcher = false }
                            )
                        }

                        // Altitude Details Modal Bottom Sheet
                        if (showAltitudeSheet) {
                            val finishedGoals = goals.filter { it.completedAt != null }
                            AltitudeSheet(
                                activityName = currentActivity?.name ?: "Activity",
                                altitudeInfo = altitudeInfo,
                                latestFinishedGoals = finishedGoals,
                                onDismiss = { showAltitudeSheet = false }
                            )
                        }

                        // Quick Add Modal Bottom Sheet
                        if (showQuickAddSheet) {
                            QuickAddSheet(
                                onRecordFeedback = { activeSubScreen = "feedback_flow" },
                                onAddGoalOnly = { showAddGoalDialog = true },
                                onScheduleSession = { showScheduleSessionDialog = true },
                                onAddHabit = { showAddHabitDialog = true },
                                onDismiss = { showQuickAddSheet = false }
                            )
                        }

                        // Dialog: Add Single Goal Only
                        if (showAddGoalDialog) {
                            var goalInput by remember { mutableStateOf("") }
                            var priSelection by remember { mutableStateOf(GoalPriority.MEDIUM) }
                            AlertDialog(
                                onDismissRequest = { showAddGoalDialog = false },
                                title = { Text("New Goal for ${currentActivity?.name}", fontWeight = FontWeight.Black) },
                                text = {
                                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                        OutlinedTextField(
                                            value = goalInput,
                                            onValueChange = { goalInput = it },
                                            label = { Text("What do you want to work on?") },
                                            placeholder = { Text("e.g. Warm up properly before every session") },
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        Text("Priority:", fontWeight = FontWeight.Bold, color = DeepSky)
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            GoalPriority.values().forEach { p ->
                                                val isP = priSelection == p
                                                Surface(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .height(36.dp)
                                                        .clip(RoundedCornerShape(10.dp))
                                                        .clickable { priSelection = p },
                                                    shape = RoundedCornerShape(10.dp),
                                                    color = if (isP) SkyBlue else SkyCloud
                                                ) {
                                                    Box(contentAlignment = Alignment.Center) {
                                                        Text(p.label, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = SkyNavy)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                },
                                confirmButton = {
                                    Button(
                                        onClick = {
                                            if (goalInput.isNotBlank()) {
                                                viewModel.saveGoalOnly(goalInput.trim(), priSelection, putInCurrentAim = currentAimGoals.size < 3)
                                                showAddGoalDialog = false
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = SkyBlue)
                                    ) {
                                        Text("Save Goal", fontWeight = FontWeight.Black, color = SkyNavy)
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showAddGoalDialog = false }) { Text("Cancel") }
                                }
                            )
                        }

                        // Dialog: New Activity
                        if (showNewActivityDialog) {
                            var actNameInput by remember { mutableStateOf("") }
                            var selectedPreset by remember { mutableStateOf(DEFAULT_ACTIVITY_PRESETS.first()) }
                            AlertDialog(
                                onDismissRequest = { showNewActivityDialog = false },
                                title = { Text("New Activity", fontWeight = FontWeight.Black) },
                                text = {
                                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                        Text("Pick a preset or name it yourself:", fontSize = 12.sp, color = Color(0xFF4A6584))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            DEFAULT_ACTIVITY_PRESETS.take(3).forEach { p ->
                                                val isP = selectedPreset == p
                                                val pColor = try {
                                                    Color(android.graphics.Color.parseColor(p.colorHex))
                                                } catch (e: Exception) {
                                                    SkyBlue
                                                }
                                                Surface(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .height(42.dp)
                                                        .clip(RoundedCornerShape(10.dp))
                                                        .clickable {
                                                            selectedPreset = p
                                                            actNameInput = p.name
                                                        },
                                                    color = if (isP) SkyBlue else SkyCloud,
                                                    border = androidx.compose.foundation.BorderStroke(1.5.dp, if (isP) DeepSky else SkyMist)
                                                ) {
                                                    Row(
                                                        modifier = Modifier.fillMaxSize().padding(horizontal = 4.dp),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.Center
                                                    ) {
                                                        ActivityIcon(iconKey = p.iconKey, tint = if (isP) SkyNavy else pColor, size = 18.dp)
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text(p.name, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = SkyNavy, maxLines = 1)
                                                    }
                                                }
                                            }
                                        }
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            DEFAULT_ACTIVITY_PRESETS.drop(3).forEach { p ->
                                                val isP = selectedPreset == p
                                                val pColor = try {
                                                    Color(android.graphics.Color.parseColor(p.colorHex))
                                                } catch (e: Exception) {
                                                    SkyBlue
                                                }
                                                Surface(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .height(42.dp)
                                                        .clip(RoundedCornerShape(10.dp))
                                                        .clickable {
                                                            selectedPreset = p
                                                            actNameInput = p.name
                                                        },
                                                    color = if (isP) SkyBlue else SkyCloud,
                                                    border = androidx.compose.foundation.BorderStroke(1.5.dp, if (isP) DeepSky else SkyMist)
                                                ) {
                                                    Row(
                                                        modifier = Modifier.fillMaxSize().padding(horizontal = 4.dp),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.Center
                                                    ) {
                                                        ActivityIcon(iconKey = p.iconKey, tint = if (isP) SkyNavy else pColor, size = 18.dp)
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text(p.name, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = SkyNavy, maxLines = 1)
                                                    }
                                                }
                                            }
                                        }

                                        OutlinedTextField(
                                            value = actNameInput,
                                            onValueChange = { actNameInput = it },
                                            label = { Text("Activity Name") },
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                    }
                                },
                                confirmButton = {
                                    Button(
                                        onClick = {
                                            val name = if (actNameInput.isNotBlank()) actNameInput.trim() else selectedPreset.name
                                            viewModel.createActivity(name, selectedPreset.iconKey, selectedPreset.colorHex)
                                            showNewActivityDialog = false
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = SkyBlue)
                                    ) {
                                        Text("Create", fontWeight = FontWeight.Black, color = SkyNavy)
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showNewActivityDialog = false }) { Text("Cancel") }
                                }
                            )
                        }

                        // Dialog: Schedule Session
                        if (showScheduleSessionDialog) {
                            var sessionTitle by remember { mutableStateOf("") }
                            var sessionDate by remember { mutableStateOf(currentDateKey()) }
                            var sessionTime by remember { mutableStateOf("18:00") }
                            var isRepeating by remember { mutableStateOf(false) }

                            AlertDialog(
                                onDismissRequest = { showScheduleSessionDialog = false },
                                title = { Text("Schedule Session", fontWeight = FontWeight.Black) },
                                text = {
                                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                        OutlinedTextField(
                                            value = sessionTitle,
                                            onValueChange = { sessionTitle = it },
                                            label = { Text("Session Title") },
                                            placeholder = { Text("e.g. Tuesday Training") },
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        OutlinedTextField(
                                            value = sessionDate,
                                            onValueChange = { sessionDate = it },
                                            label = { Text("Date (yyyy-MM-dd)") },
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        OutlinedTextField(
                                            value = sessionTime,
                                            onValueChange = { sessionTime = it },
                                            label = { Text("Time (optional, e.g. 18:00)") },
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                    }
                                },
                                confirmButton = {
                                    Button(
                                        onClick = {
                                            if (sessionTitle.isNotBlank()) {
                                                viewModel.saveSession(sessionTitle.trim(), sessionDate.trim(), sessionTime.trim(), isRepeating)
                                                showScheduleSessionDialog = false
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = SkyBlue)
                                    ) {
                                        Text("Save", fontWeight = FontWeight.Black, color = SkyNavy)
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showScheduleSessionDialog = false }) { Text("Cancel") }
                                }
                            )
                        }

                        // Dialog: Add Habit
                        if (showAddHabitDialog) {
                            var habitTitle by remember { mutableStateOf("") }
                            var targetDays by remember { mutableStateOf(3) }

                            AlertDialog(
                                onDismissRequest = { showAddHabitDialog = false },
                                title = { Text("Add a Habit", fontWeight = FontWeight.Black) },
                                text = {
                                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                        OutlinedTextField(
                                            value = habitTitle,
                                            onValueChange = { habitTitle = it },
                                            label = { Text("What will you do?") },
                                            placeholder = { Text("e.g. 10 minutes of ball control") },
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        Text("Days per week target:", fontWeight = FontWeight.Bold, color = DeepSky)
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            (1..7).forEach { days ->
                                                val isD = targetDays == days
                                                Surface(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .height(34.dp)
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .clickable { targetDays = days },
                                                    color = if (isD) SkyBlue else SkyCloud
                                                ) {
                                                    Box(contentAlignment = Alignment.Center) {
                                                        Text("$days", fontWeight = FontWeight.Bold, color = SkyNavy)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                },
                                confirmButton = {
                                    Button(
                                        onClick = {
                                            if (habitTitle.isNotBlank()) {
                                                viewModel.saveHabit(habitTitle.trim(), targetDays, linkedGoalId = null)
                                                showAddHabitDialog = false
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = SkyBlue)
                                    ) {
                                        Text("Save", fontWeight = FontWeight.Black, color = SkyNavy)
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showAddHabitDialog = false }) { Text("Cancel") }
                                }
                            )
                        }

                        // Dialog: Export Backup
                        if (showBackupDialog) {
                            AlertDialog(
                                onDismissRequest = { showBackupDialog = false },
                                title = { Text("Skybound Backup JSON", fontWeight = FontWeight.Black) },
                                text = {
                                    OutlinedTextField(
                                        value = backupJsonContent,
                                        onValueChange = {},
                                        readOnly = true,
                                        modifier = Modifier.fillMaxWidth().height(200.dp),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                },
                                confirmButton = {
                                    Button(
                                        onClick = { showBackupDialog = false },
                                        colors = ButtonDefaults.buttonColors(containerColor = SkyBlue)
                                    ) {
                                        Text("Done", fontWeight = FontWeight.Black, color = SkyNavy)
                                    }
                                }
                            )
                        }

                        // Dialog: Restore Backup
                        if (showRestoreDialog) {
                            AlertDialog(
                                onDismissRequest = { showRestoreDialog = false },
                                title = { Text("Restore Skybound JSON", fontWeight = FontWeight.Black) },
                                text = {
                                    OutlinedTextField(
                                        value = restoreJsonInput,
                                        onValueChange = { restoreJsonInput = it },
                                        placeholder = { Text("Paste JSON backup here...") },
                                        modifier = Modifier.fillMaxWidth().height(200.dp),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                },
                                confirmButton = {
                                    Button(
                                        onClick = {
                                            if (restoreJsonInput.isNotBlank()) {
                                                coroutineScope.launch {
                                                    viewModel.restoreJson(restoreJsonInput.trim())
                                                    showRestoreDialog = false
                                                }
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = SkyBlue)
                                    ) {
                                        Text("Restore", fontWeight = FontWeight.Black, color = SkyNavy)
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showRestoreDialog = false }) { Text("Cancel") }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
