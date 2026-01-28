package com.example.habitflow.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val habitId: Long,
    val title: String,
    val description: String,
    val iconName: String, // "streak_3", "streak_7", "streak_30", "total_100"
    val dateEarned: Long = System.currentTimeMillis()
)
