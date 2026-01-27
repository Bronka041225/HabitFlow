package com.example.habitflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.habitflow.data.AppDatabase
import com.example.habitflow.data.repository.HabitRepository
import com.example.habitflow.ui.screens.HabitDetailScreen
import com.example.habitflow.ui.screens.HomeScreen
import com.example.habitflow.ui.theme.HabitFlowTheme
import com.example.habitflow.ui.viewmodel.HabitViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Manual Dependency Injection
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = HabitRepository(database.habitDao(), database.recordDao())
        val factory = HabitViewModel.Factory(repository)
        val viewModel = ViewModelProvider(this, factory)[HabitViewModel::class.java]

        setContent {
            HabitFlowTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        HomeScreen(
                            viewModel = viewModel,
                            onHabitClick = { habitId ->
                                navController.navigate("detail/$habitId")
                            }
                        )
                    }
                    composable(
                        route = "detail/{habitId}",
                        arguments = listOf(navArgument("habitId") { type = NavType.LongType })
                    ) { backStackEntry ->
                        val habitId = backStackEntry.arguments?.getLong("habitId") ?: return@composable
                        HabitDetailScreen(
                            habitId = habitId,
                            viewModel = viewModel,
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}