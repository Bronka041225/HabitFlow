package com.example.habitflow.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.habitflow.data.entity.AchievementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AchievementDao {
    @Query("SELECT * FROM achievements WHERE habitId = :habitId ORDER BY dateEarned DESC")
    fun getAchievementsForHabit(habitId: Long): Flow<List<AchievementEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAchievement(achievement: AchievementEntity)
    
    @Query("SELECT COUNT(*) FROM achievements WHERE habitId = :habitId AND title = :title")
    suspend fun hasAchievement(habitId: Long, title: String): Int
}
