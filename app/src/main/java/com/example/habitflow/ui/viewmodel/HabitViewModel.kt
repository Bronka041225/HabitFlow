package com.example.habitflow.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitflow.R
import com.example.habitflow.data.entity.AchievementEntity
import com.example.habitflow.data.entity.HabitEntity
import com.example.habitflow.data.entity.HabitRecordEntity
import com.example.habitflow.data.repository.HabitRepository
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject
import kotlin.math.max
import kotlin.random.Random

data class HabitExportData(
    val habit: HabitEntity,
    val records: List<HabitRecordEntity>
)

@HiltViewModel
class HabitViewModel @Inject constructor(
    private val repository: HabitRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _exportEvent = Channel<String>()
    val exportEvent = _exportEvent.receiveAsFlow()
    
    private val _achievementEvent = Channel<String>()
    val achievementEvent = _achievementEvent.receiveAsFlow()

    val habits: StateFlow<List<HabitEntity>> = repository.allHabits
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        // Auto-generate test data if empty on startup
        viewModelScope.launch {
            try {
                val currentHabits = repository.allHabits.first()
                if (currentHabits.isEmpty()) {
                    // generateTestData()  // 已禁用自动生成，改用手动按钮
                }
            } catch (e: Exception) {
                android.util.Log.e("HabitViewModel", "Init failed", e)
                e.printStackTrace()
            }
        }
    }

    fun addHabit(name: String, target: Int, color: String = "#FF6D00", icon: String = "Star") {
        viewModelScope.launch {
            val newHabit = HabitEntity(
                name = name,
                dailyTarget = target,
                unit = context.getString(R.string.unit_times),
                colorHex = color,
                iconName = icon
            )
            repository.insertHabit(newHabit)
        }
    }

    fun deleteHabit(habit: HabitEntity) {
        viewModelScope.launch {
            repository.deleteHabit(habit)
        }
    }

    fun incrementHabit(habit: HabitEntity, amount: Int = 1) {
        viewModelScope.launch {
            val today = LocalDate.now().toEpochDay()
            val records = repository.getRecordsForDay(habit.id, today).first()
            val existingRecord = records.firstOrNull()

            if (existingRecord != null) {
                repository.upsertRecord(existingRecord.copy(count = existingRecord.count + amount))
            } else {
                val newRecord = HabitRecordEntity(
                    habitId = habit.id,
                    date = today,
                    count = amount
                )
                repository.upsertRecord(newRecord)
            }
            
            // Recalculate Streak
            calculateStreaks(habit)
        }
    }
    
    private suspend fun calculateStreaks(habit: HabitEntity) {
        val allRecords = repository.getAllRecordsForHabit(habit.id).first()
            .sortedByDescending { it.date }
            
        if (allRecords.isEmpty()) return

        val today = LocalDate.now().toEpochDay()
        val distinctDates = allRecords.map { it.date }.distinct()
        
        var streak = 0
        var expectedDate = distinctDates.firstOrNull() ?: return
        
        // If the chain doesn't start today or yesterday, it's broken (unless we want to count strict continuity relative to today)
        // If expectedDate < today - 1, then the streak was broken before today.
        
        if (expectedDate < today - 1) {
            streak = 0
        } else {
            // Check consecutive days backwards
            var previousDate = expectedDate + 1 // Setup for loop
            for (date in distinctDates) {
                if (date == previousDate - 1) {
                    streak++
                    previousDate = date
                } else {
                    break
                }
            }
        }
        
        val newLongest = max(habit.longestStreak, streak)
        
        if (streak != habit.currentStreak || newLongest != habit.longestStreak) {
            val updatedHabit = habit.copy(currentStreak = streak, longestStreak = newLongest)
            repository.updateHabit(updatedHabit)
            checkAchievements(updatedHabit)
        }
    }
    
    private suspend fun checkAchievements(habit: HabitEntity) {
        val streak = habit.currentStreak
        val milestones = mapOf(
            3 to Pair(R.string.achievement_streak_3, R.string.achievement_streak_3_desc),
            7 to Pair(R.string.achievement_streak_7, R.string.achievement_streak_7_desc),
            30 to Pair(R.string.achievement_streak_30, R.string.achievement_streak_30_desc)
        )
        
        if (milestones.containsKey(streak)) {
            val (titleRes, descRes) = milestones[streak]!!
            val title = context.getString(titleRes)
            val desc = context.getString(descRes)
            
            val achievement = AchievementEntity(
                habitId = habit.id,
                title = title,
                description = desc,
                iconName = "streak_$streak"
            )
            
            repository.insertAchievement(achievement)
            _achievementEvent.send(context.getString(R.string.achievement_unlocked, title))
        }
    }

    fun getTodayCount(habitId: Long): Flow<Int> {
        val today = LocalDate.now().toEpochDay()
        return repository.getRecordsForDay(habitId, today).map {
            it.sumOf { it.count }
        }
    }
    
    fun getAchievements(habitId: Long): Flow<List<AchievementEntity>> = repository.getAchievementsForHabit(habitId)

    fun getHabitById(id: Long): Flow<HabitEntity> = repository.getHabitById(id)
    fun getRecordsForHabit(id: Long): Flow<List<HabitRecordEntity>> = repository.getAllRecordsForHabit(id)
    fun getLast30DaysRecords(habitId: Long): Flow<List<HabitRecordEntity>> {
        val end = LocalDate.now().toEpochDay()
        val start = end - 29
        return repository.getRecordsByDateRange(habitId, start, end)
    }

    fun generateTestData() {
        viewModelScope.launch {
            val h1 = HabitEntity(name = context.getString(R.string.habit_pushups), dailyTarget = 50, unit = context.getString(R.string.unit_times), colorHex = "#FF6D00")
            val id1 = repository.insertHabit(h1)
            generateHistory(id1, 50, 0.7f)

            val h2 = HabitEntity(name = context.getString(R.string.habit_reading), dailyTarget = 30, unit = context.getString(R.string.unit_minutes), colorHex = "#2196F3")
            val id2 = repository.insertHabit(h2)
            generateHistory(id2, 30, 0.4f)

            val h3 = HabitEntity(name = context.getString(R.string.habit_water), dailyTarget = 8, unit = context.getString(R.string.unit_cups), colorHex = "#00BCD4")
            val id3 = repository.insertHabit(h3)
            generateHistory(id3, 8, 0.9f)
        }
    }
    
    private suspend fun generateHistory(habitId: Long, target: Int, consistency: Float) {
        val today = LocalDate.now().toEpochDay()
        val start = today - 90 

        for (day in start..today) {
            if (Random.nextFloat() < consistency) {
                val base = (target * Random.nextFloat() * 1.5).toInt()
                val count = base.coerceAtLeast(1)
                repository.upsertRecord(HabitRecordEntity(habitId = habitId, date = day, count = count))
            }
        }
        
        // Calculate initial stats for generated data
        val habit = repository.getHabitById(habitId).first()
        calculateStreaks(habit)
    }

    fun exportData(uri: Uri, format: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val habits = repository.getAllHabitsSync()
                val records = repository.getAllRecordsSync()

                val content = if (format == "JSON") {
                    val exportList = habits.map { h ->
                        HabitExportData(h, records.filter { it.habitId == h.id })
                    }
                    Gson().toJson(exportList)
                } else {
                    val sb = StringBuilder()
                    sb.append("Habit ID,Habit Name,Date (Epoch),Count\n")
                    for (h in habits) {
                        val hRecords = records.filter { it.habitId == h.id }
                        for (r in hRecords) {
                            val name = h.name
                            val safeName = if (name.contains(",")) "\"$name\"" else name
                            sb.append("${h.id},$safeName,${r.date},${r.count}\n")
                        }
                    }
                    sb.toString()
                }

                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.write(content.toByteArray())
                }
                _exportEvent.send("Success")
            } catch (e: Exception) {
                e.printStackTrace()
                _exportEvent.send("Failed")
            }
        }
    }
}
