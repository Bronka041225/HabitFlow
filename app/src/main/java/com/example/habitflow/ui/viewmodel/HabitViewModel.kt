package com.example.habitflow.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.habitflow.data.entity.HabitEntity
import com.example.habitflow.data.entity.HabitRecordEntity
import com.example.habitflow.data.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.random.Random

class HabitViewModel(private val repository: HabitRepository) : ViewModel() {

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
                // We use first() to get the current snapshot. 
                // Note: This collects one item from the Flow.
                val currentHabits = repository.allHabits.first()
                if (currentHabits.isEmpty()) {
                    generateTestData()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addHabit(name: String, target: Int, color: String = "#FF6D00") {
        viewModelScope.launch {
            val newHabit = HabitEntity(
                name = name,
                dailyTarget = target,
                unit = "次",
                colorHex = color
            )
            repository.insertHabit(newHabit)
        }
    }

    fun deleteHabit(habit: HabitEntity) {
        viewModelScope.launch {
            repository.deleteHabit(habit)
        }
    }

    // Now accepts an 'amount' parameter
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
        }
    }

    fun getTodayCount(habitId: Long): Flow<Int> {
        val today = LocalDate.now().toEpochDay()
        return repository.getRecordsForDay(habitId, today).map { records ->
            records.sumOf { it.count }
        }
    }

    fun getHabitById(id: Long): Flow<HabitEntity> = repository.getHabitById(id)
    fun getRecordsForHabit(id: Long): Flow<List<HabitRecordEntity>> = repository.getAllRecordsForHabit(id)
    fun getLast30DaysRecords(habitId: Long): Flow<List<HabitRecordEntity>> {
        val end = LocalDate.now().toEpochDay()
        val start = end - 29
        return repository.getRecordsByDateRange(habitId, start, end)
    }

    // Generate Mock Data for visualization
    fun generateTestData() {
        viewModelScope.launch {
            // 1. Pushups
            val h1 = HabitEntity(name = "俯卧撑", dailyTarget = 50, unit = "次", colorHex = "#FF6D00")
            val id1 = repository.insertHabit(h1)
            generateHistory(id1, 50, 0.7f) // 70% consistency

            // 2. Reading
            val h2 = HabitEntity(name = "阅读", dailyTarget = 30, unit = "分钟", colorHex = "#2196F3")
            val id2 = repository.insertHabit(h2)
            generateHistory(id2, 30, 0.4f) // 40% consistency

            // 3. Water
            val h3 = HabitEntity(name = "喝水", dailyTarget = 8, unit = "杯", colorHex = "#00BCD4")
            val id3 = repository.insertHabit(h3)
            generateHistory(id3, 8, 0.9f) // 90% consistency
        }
    }

    private suspend fun generateHistory(habitId: Long, target: Int, consistency: Float) {
        val today = LocalDate.now().toEpochDay()
        val start = today - 90 // Past 3 months

        for (day in start..today) {
            if (Random.nextFloat() < consistency) {
                // Randomize count around target
                val base = (target * Random.nextFloat() * 1.5).toInt()
                val count = base.coerceAtLeast(1)
                repository.upsertRecord(HabitRecordEntity(habitId = habitId, date = day, count = count))
            }
        }
    }

    class Factory(private val repository: HabitRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HabitViewModel::class.java)) {
                return HabitViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}