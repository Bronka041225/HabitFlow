package com.example.habitflow.data.repository

import com.example.habitflow.data.dao.HabitDao
import com.example.habitflow.data.dao.RecordDao
import com.example.habitflow.data.entity.HabitEntity
import com.example.habitflow.data.entity.HabitRecordEntity
import kotlinx.coroutines.flow.Flow

class HabitRepository(
    private val habitDao: HabitDao,
    private val recordDao: RecordDao
) {

    // Habit Operations
    val allHabits: Flow<List<HabitEntity>> = habitDao.getAllHabits()

    fun getHabitById(id: Long): Flow<HabitEntity> {
        return habitDao.getHabitById(id)
    }

    suspend fun insertHabit(habit: HabitEntity): Long {
        return habitDao.insertHabit(habit)
    }

    suspend fun deleteHabit(habit: HabitEntity) {
        habitDao.deleteHabit(habit)
    }

    // Record Operations
    fun getRecordsByDateRange(habitId: Long, startDate: Long, endDate: Long): Flow<List<HabitRecordEntity>> {
        return recordDao.getRecordsByDateRange(habitId, startDate, endDate)
    }

    fun getAllRecordsForHabit(habitId: Long): Flow<List<HabitRecordEntity>> {
        return recordDao.getAllRecordsForHabit(habitId)
    }

    fun getRecordsForDay(habitId: Long, date: Long): Flow<List<HabitRecordEntity>> {
        return recordDao.getRecordsForDay(habitId, date)
    }

    suspend fun upsertRecord(record: HabitRecordEntity) {
        recordDao.upsertRecord(record)
    }
}
