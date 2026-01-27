package com.example.habitflow.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val dailyTarget: Int,
    val unit: String, // e.g., 'reps', 'mins', 'ml'
    val colorHex: String, // Hex color code for UI charts
    val createdAt: Long = System.currentTimeMillis()
)
