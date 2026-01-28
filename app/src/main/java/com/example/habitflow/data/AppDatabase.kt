package com.example.habitflow.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.habitflow.data.dao.AchievementDao
import com.example.habitflow.data.dao.HabitDao
import com.example.habitflow.data.dao.RecordDao
import com.example.habitflow.data.entity.AchievementEntity
import com.example.habitflow.data.entity.HabitEntity
import com.example.habitflow.data.entity.HabitRecordEntity

@Database(
    entities = [HabitEntity::class, HabitRecordEntity::class, AchievementEntity::class], 
    version = 2, 
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun habitDao(): HabitDao
    abstract fun recordDao(): RecordDao
    abstract fun achievementDao(): AchievementDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "habit_flow_database"
                )
                .fallbackToDestructiveMigration() // Simplified for initial dev
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
