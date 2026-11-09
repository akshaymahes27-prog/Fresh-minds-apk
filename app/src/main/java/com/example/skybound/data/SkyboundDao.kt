package com.example.skybound.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SkyboundDao {

    // --- Activities ---
    @Query("SELECT * FROM activities ORDER BY createdAt ASC")
    fun getAllActivities(): Flow<List<ActivityEntity>>

    @Query("SELECT * FROM activities WHERE id = :id LIMIT 1")
    suspend fun getActivityById(id: String): ActivityEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: ActivityEntity)

    @Update
    suspend fun updateActivity(activity: ActivityEntity)

    @Query("DELETE FROM activities WHERE id = :id")
    suspend fun deleteActivity(id: String)

    @Query("DELETE FROM activities WHERE isExample = 1")
    suspend fun deleteExampleActivities()

    // --- Goals ---
    @Query("SELECT * FROM goals ORDER BY createdAt DESC")
    fun getAllGoals(): Flow<List<GoalEntity>>

    @Query("SELECT * FROM goals WHERE activityId = :activityId ORDER BY createdAt DESC")
    fun getGoalsForActivity(activityId: String): Flow<List<GoalEntity>>

    @Query("SELECT * FROM goals WHERE id = :id LIMIT 1")
    suspend fun getGoalById(id: String): GoalEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: GoalEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoals(goals: List<GoalEntity>)

    @Update
    suspend fun updateGoal(goal: GoalEntity)

    @Query("DELETE FROM goals WHERE id = :id")
    suspend fun deleteGoal(id: String)

    @Query("DELETE FROM goals WHERE activityId = :activityId")
    suspend fun deleteGoalsForActivity(activityId: String)

    @Query("DELETE FROM goals WHERE isExample = 1")
    suspend fun deleteExampleGoals()

    // --- Feedback ---
    @Query("SELECT * FROM feedback WHERE activityId = :activityId ORDER BY createdAt DESC")
    fun getFeedbackForActivity(activityId: String): Flow<List<FeedbackEntity>>

    @Query("SELECT * FROM feedback ORDER BY createdAt DESC")
    fun getAllFeedback(): Flow<List<FeedbackEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedback(feedback: FeedbackEntity)

    @Query("DELETE FROM feedback WHERE activityId = :activityId")
    suspend fun deleteFeedbackForActivity(activityId: String)

    @Query("DELETE FROM feedback WHERE isExample = 1")
    suspend fun deleteExampleFeedback()

    // --- Sessions ---
    @Query("SELECT * FROM sessions WHERE activityId = :activityId ORDER BY dateKey ASC, time ASC")
    fun getSessionsForActivity(activityId: String): Flow<List<SessionEntity>>

    @Query("SELECT * FROM sessions ORDER BY dateKey ASC")
    fun getAllSessions(): Flow<List<SessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: SessionEntity)

    @Update
    suspend fun updateSession(session: SessionEntity)

    @Query("DELETE FROM sessions WHERE id = :id")
    suspend fun deleteSession(id: String)

    @Query("DELETE FROM sessions WHERE activityId = :activityId")
    suspend fun deleteSessionsForActivity(activityId: String)

    @Query("DELETE FROM sessions WHERE isExample = 1")
    suspend fun deleteExampleSessions()

    // --- Habits ---
    @Query("SELECT * FROM habits WHERE activityId = :activityId ORDER BY createdAt ASC")
    fun getHabitsForActivity(activityId: String): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits ORDER BY createdAt ASC")
    fun getAllHabits(): Flow<List<HabitEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitEntity)

    @Update
    suspend fun updateHabit(habit: HabitEntity)

    @Query("DELETE FROM habits WHERE id = :id")
    suspend fun deleteHabit(id: String)

    @Query("DELETE FROM habits WHERE activityId = :activityId")
    suspend fun deleteHabitsForActivity(activityId: String)

    @Query("DELETE FROM habits WHERE isExample = 1")
    suspend fun deleteExampleHabits()

    // --- Records ---
    @Query("SELECT * FROM records WHERE activityId = :activityId ORDER BY dateKey DESC")
    fun getRecordsForActivity(activityId: String): Flow<List<RecordEntity>>

    @Query("SELECT * FROM records ORDER BY dateKey DESC")
    fun getAllRecords(): Flow<List<RecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: RecordEntity)

    @Query("DELETE FROM records WHERE id = :id")
    suspend fun deleteRecord(id: String)

    @Query("DELETE FROM records WHERE activityId = :activityId")
    suspend fun deleteRecordsForActivity(activityId: String)

    @Query("DELETE FROM records WHERE isExample = 1")
    suspend fun deleteExampleRecords()

    // --- Milestones ---
    @Query("SELECT * FROM milestones")
    fun getAllMilestones(): Flow<List<MilestoneEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun unlockMilestone(milestone: MilestoneEntity)

    // --- App Settings ---
    @Query("SELECT * FROM app_settings WHERE id = 1 LIMIT 1")
    fun getAppSettings(): Flow<AppSettingsEntity?>

    @Query("SELECT * FROM app_settings WHERE id = 1 LIMIT 1")
    suspend fun getAppSettingsDirect(): AppSettingsEntity?

    @Query("SELECT * FROM app_settings WHERE id = 1 LIMIT 1")
    fun getAppSettingsDirectSync(): AppSettingsEntity?

    @Query("SELECT * FROM activities ORDER BY createdAt ASC")
    fun getAllActivitiesDirect(): List<ActivityEntity>

    @Query("SELECT * FROM goals WHERE activityId = :activityId ORDER BY createdAt DESC")
    fun getGoalsForActivityDirect(activityId: String): List<GoalEntity>

    @Query("SELECT * FROM sessions WHERE activityId = :activityId ORDER BY dateKey ASC, time ASC")
    fun getSessionsForActivityDirect(activityId: String): List<SessionEntity>

    @Query("SELECT * FROM habits WHERE activityId = :activityId ORDER BY createdAt ASC")
    fun getHabitsForActivityDirect(activityId: String): List<HabitEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAppSettings(settings: AppSettingsEntity)

    // --- Bulk Operations ---
    @Query("DELETE FROM activities")
    suspend fun clearActivities()

    @Query("DELETE FROM goals")
    suspend fun clearGoals()

    @Query("DELETE FROM feedback")
    suspend fun clearFeedback()

    @Query("DELETE FROM sessions")
    suspend fun clearSessions()

    @Query("DELETE FROM habits")
    suspend fun clearHabits()

    @Query("DELETE FROM records")
    suspend fun clearRecords()

    @Query("DELETE FROM milestones")
    suspend fun clearMilestones()
}
