package com.example.habitflow.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.habitflow.R
import com.example.habitflow.data.entity.HabitEntity
import com.example.habitflow.ui.components.HabitCard
import com.example.habitflow.ui.viewmodel.HabitViewModel

@Composable
fun HomeScreen(
    viewModel: HabitViewModel,
    onHabitClick: (Long) -> Unit
) {
    val habits by viewModel.habits.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var habitToDelete by remember { mutableStateOf<HabitEntity?>(null) }
    var habitToLog by remember { mutableStateOf<HabitEntity?>(null) }
    
    // Export State
    var showExportDialog by remember { mutableStateOf(false) }
    var exportFormat by remember { mutableStateOf("JSON") }
    val context = LocalContext.current
    
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("*/*"),
        onResult = { uri ->
            uri?.let { viewModel.exportData(it, exportFormat) }
        }
    )

    LaunchedEffect(Unit) {
        viewModel.exportEvent.collect { status ->
            if (status == "Success") {
                Toast.makeText(context, R.string.export_success, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, R.string.export_failed, Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    LaunchedEffect(Unit) {
        viewModel.achievementEvent.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_habit))
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.title_focus),
                    style = MaterialTheme.typography.displayLarge.copy(fontSize = 40.sp),
                    color = MaterialTheme.colorScheme.onBackground
                )
                IconButton(onClick = { showExportDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = stringResource(R.string.export_data),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Habit List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(habits, key = { it.id }) { habit ->
                    HabitRow(
                        habit = habit,
                        viewModel = viewModel,
                        onClick = { onHabitClick(habit.id) },
                        onLongClick = { habitToDelete = habit },
                        onLogClick = { habitToLog = habit }
                    )
                }
            }
        }

        if (showAddDialog) {
            AddHabitDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { name, target, color ->
                    viewModel.addHabit(name, target, color)
                    showAddDialog = false
                },
                onGenerateTest = {
                    viewModel.generateTestData()
                    showAddDialog = false
                }
            )
        }

        habitToDelete?.let { habit ->
            AlertDialog(
                onDismissRequest = { habitToDelete = null },
                title = { Text(stringResource(R.string.delete_habit)) },
                text = { Text(stringResource(R.string.confirm_delete, habit.name)) },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.deleteHabit(habit)
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

        habitToLog?.let { habit ->
            LogProgressDialog(
                habitName = habit.name,
                onDismiss = { habitToLog = null },
                onConfirm = { amount ->
                    viewModel.incrementHabit(habit, amount)
                    habitToLog = null
                }
            )
        }

        if (showExportDialog) {
            ExportDialog(
                onDismiss = { showExportDialog = false },
                onConfirm = { format ->
                    exportFormat = format
                    val fileName = "habitflow_backup_${System.currentTimeMillis()}.${format.lowercase()}"
                    exportLauncher.launch(fileName)
                    showExportDialog = false
                }
            )
        }
    }
}

@Composable
fun ExportDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.export_data)) },
        text = {
            Column {
                Text(stringResource(R.string.choose_export_format))
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { onConfirm("JSON") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.export_json))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { onConfirm("CSV") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.export_csv))
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HabitRow(
    habit: HabitEntity,
    viewModel: HabitViewModel,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onLogClick: () -> Unit
) {
    val currentCount by viewModel.getTodayCount(habit.id).collectAsState(initial = 0)

    // We wrap HabitCard to intercept clicks but pass the 'onIncrement' to open the dialog
    HabitCard(
        habitName = habit.name,
        currentCount = currentCount,
        targetCount = habit.dailyTarget,
        streak = habit.currentStreak,
        onIncrement = onLogClick, // The '+' button opens the log dialog
        modifier = Modifier.combinedClickable(
            onClick = onClick,
            onLongClick = onLongClick
        )
    )
}

@Composable
fun AddHabitDialog(
    onDismiss: () -> Unit, 
    onConfirm: (String, Int, String) -> Unit,
    onGenerateTest: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var target by remember { mutableStateOf("") }
    
    // Preset Palette
    val colors = listOf(
        "#FF6D00", // Electric Orange
        "#2196F3", // Ocean Blue
        "#00E676", // Mint Green
        "#FF4081", // Hot Pink
        "#7C4DFF", // Violet
        "#FF5252", // Bright Red
        "#00BCD4"  // Cyan
    )
    var selectedColor by remember { mutableStateOf(colors[0]) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.create_habit)) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.habit_name_label)) },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = target,
                    onValueChange = { target = it },
                    label = { Text(stringResource(R.string.daily_target_label)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                Text(stringResource(R.string.select_color), style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(8.dp))
                
                // Color Palette Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    colors.forEach { colorHex ->
                        val isSelected = selectedColor == colorHex
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(android.graphics.Color.parseColor(colorHex)))
                                .border(
                                    width = if (isSelected) 2.dp else 0.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { selectedColor = colorHex },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                // Debug button
                TextButton(
                    onClick = onGenerateTest,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.debug_generate_data), color = Color.Gray)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && target.toIntOrNull() != null) {
                        onConfirm(name, target.toInt(), selectedColor)
                    }
                }
            ) {
                Text(stringResource(R.string.create))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
fun LogProgressDialog(
    habitName: String,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var customAmount by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.log_habit, habitName)) },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(stringResource(R.string.quick_add), style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    OutlinedButton(onClick = { onConfirm(1) }) { Text("+1") }
                    OutlinedButton(onClick = { onConfirm(5) }) { Text("+5") }
                    OutlinedButton(onClick = { onConfirm(10) }) { Text("+10") }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(stringResource(R.string.custom_amount), style = MaterialTheme.typography.labelMedium)
                OutlinedTextField(
                    value = customAmount,
                    onValueChange = { customAmount = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    placeholder = { Text(stringResource(R.string.input_number_placeholder)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = customAmount.toIntOrNull()
                    if (amount != null && amount > 0) {
                        onConfirm(amount)
                    }
                }
            ) {
                Text(stringResource(R.string.add_custom))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
