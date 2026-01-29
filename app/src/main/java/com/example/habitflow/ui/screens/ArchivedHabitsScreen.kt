package com.example.habitflow.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.habitflow.R
import com.example.habitflow.data.entity.HabitEntity
import com.example.habitflow.ui.components.HabitCard
import com.example.habitflow.ui.viewmodel.HabitViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArchivedHabitsScreen(
    viewModel: HabitViewModel,
    navController: NavController
) {
    val archivedHabits by viewModel.archivedHabits.collectAsState()
    var habitToRestore by remember { mutableStateOf<HabitEntity?>(null) }
    var habitToDelete by remember { mutableStateOf<HabitEntity?>(null) }
    var showOptionsForHabit by remember { mutableStateOf<HabitEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.archived_habits_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (archivedHabits.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.no_archived_habits),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(archivedHabits, key = { it.id }) { habit ->
                    ArchivedHabitRow(
                        habit = habit,
                        onLongClick = { showOptionsForHabit = habit }
                    )
                }
            }
        }
    }

    // Options Dialog
    showOptionsForHabit?.let { habit ->
        AlertDialog(
            onDismissRequest = { showOptionsForHabit = null },
            title = { Text(stringResource(R.string.manage_habit, habit.name)) },
            text = { Text(stringResource(R.string.manage_habit, habit.name)) }, // Using reuse for text, or just reuse manage_habit title concept
            confirmButton = {
                Button(
                    onClick = {
                        habitToRestore = habit
                        showOptionsForHabit = null
                    }
                ) {
                    Text(stringResource(R.string.restore))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        habitToDelete = habit
                        showOptionsForHabit = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(stringResource(R.string.delete_permanently))
                }
            }
        )
    }

    // Restore Confirmation
    habitToRestore?.let { habit ->
        AlertDialog(
            onDismissRequest = { habitToRestore = null },
            title = { Text(stringResource(R.string.restore)) },
            text = { Text(stringResource(R.string.restore_confirm, habit.name)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.restoreHabit(habit)
                        habitToRestore = null
                    }
                ) {
                    Text(stringResource(R.string.restore))
                }
            },
            dismissButton = {
                TextButton(onClick = { habitToRestore = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    // Delete Confirmation
    habitToDelete?.let { habit ->
        AlertDialog(
            onDismissRequest = { habitToDelete = null },
            title = { Text(stringResource(R.string.delete_permanently)) },
            text = { Text(stringResource(R.string.delete_permanently_confirm, habit.name)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteHabitPermanently(habit)
                        habitToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { habitToDelete = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ArchivedHabitRow(
    habit: HabitEntity,
    onLongClick: () -> Unit
) {
    val themeColor = try {
        Color(android.graphics.Color.parseColor(habit.colorHex))
    } catch (e: Exception) {
        MaterialTheme.colorScheme.primary
    }

    // Reuse HabitCard but make it look inactive
    Box(modifier = Modifier.alpha(0.6f)) {
        HabitCard(
            habitName = habit.name,
            currentCount = 0, // Don't show progress for archived
            targetCount = habit.dailyTarget,
            streak = habit.currentStreak,
            iconName = habit.iconName,
            themeColor = themeColor, // Convert to grayscale? Or just dim.
            onIncrement = {}, // Disable click
            modifier = Modifier.combinedClickable(
                onClick = {}, // No action on single click?
                onLongClick = onLongClick
            )
        )
    }
}
