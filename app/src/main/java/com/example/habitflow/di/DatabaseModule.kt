package com.example.habitflow.di

import android.content.Context
import com.example.habitflow.data.AppDatabase
import com.example.habitflow.data.dao.HabitDao
import com.example.habitflow.data.dao.RecordDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    fun provideHabitDao(database: AppDatabase): HabitDao {
        return database.habitDao()
    }

    @Provides
    fun provideRecordDao(database: AppDatabase): RecordDao {
        return database.recordDao()
    }

    @Provides
    fun provideAchievementDao(database: AppDatabase): com.example.habitflow.data.dao.AchievementDao {
        return database.achievementDao()
    }
}
