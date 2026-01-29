package com.example.habitflow.data.repository

import com.example.habitflow.data.dao.AchievementDao
import com.example.habitflow.data.dao.HabitDao
import com.example.habitflow.data.dao.RecordDao
import com.example.habitflow.data.entity.AchievementEntity
import com.example.habitflow.data.entity.HabitEntity
import com.example.habitflow.data.entity.HabitRecordEntity
import kotlinx.coroutines.flow.Flow

import javax.inject.Inject

class HabitRepository @Inject constructor(
    private val habitDao: HabitDao,
    private val recordDao: RecordDao,
    private val achievementDao: AchievementDao
) {

    // Habit Operations
    val allHabits: Flow<List<HabitEntity>> = habitDao.getAllHabits()
    val allArchivedHabits: Flow<List<HabitEntity>> = habitDao.getArchivedHabits()
    
    suspend fun getAllHabitsSync(): List<HabitEntity> = habitDao.getAllHabitsSync()

    fun getHabitById(id: Long): Flow<HabitEntity> {
        return habitDao.getHabitById(id)
    }

    suspend fun insertHabit(habit: HabitEntity): Long {
        return habitDao.insertHabit(habit)
    }
    
    suspend fun updateHabit(habit: HabitEntity) {
        habitDao.updateHabit(habit)
    }

    suspend fun archiveHabit(habit: HabitEntity) {
        habitDao.updateHabit(habit.copy(isArchived = true))
    }

    suspend fun restoreHabit(habit: HabitEntity) {
        habitDao.updateHabit(habit.copy(isArchived = false))
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

    suspend fun getAllRecordsSync(): List<HabitRecordEntity> {
        return recordDao.getAllRecordsSync()
    }

    suspend fun upsertRecord(record: HabitRecordEntity) {
        recordDao.upsertRecord(record)
    }

    suspend fun deleteRecord(record: HabitRecordEntity) {
        recordDao.deleteRecord(record)
    }
    
    // Achievement Operations
    fun getAchievementsForHabit(habitId: Long): Flow<List<AchievementEntity>> {
        return achievementDao.getAchievementsForHabit(habitId)
    }
    
    suspend fun insertAchievement(achievement: AchievementEntity) {
        // Prevent duplicate (Dao has OnConflictStrategy.IGNORE but only if ID matches, 
        // we want to check logic dupes manually or rely on business logic)
        // Here we rely on logic check 'hasAchievement' before inserting
        if (achievementDao.hasAchievement(achievement.habitId, achievement.title) == 0) {
            achievementDao.insertAchievement(achievement)
        }
    }
}
