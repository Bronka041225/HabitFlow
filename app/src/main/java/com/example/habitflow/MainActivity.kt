package com.example.habitflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.habitflow.ui.screens.HabitDetailScreen
import com.example.habitflow.ui.screens.HomeScreen
import com.example.habitflow.ui.theme.HabitFlowTheme
import com.example.habitflow.ui.viewmodel.HabitViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: HabitViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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