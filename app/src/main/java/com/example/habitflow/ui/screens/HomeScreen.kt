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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.example.habitflow.R
import com.example.habitflow.data.entity.HabitEntity
import com.example.habitflow.ui.components.AddHabitBottomSheet
import com.example.habitflow.ui.components.ExportBottomSheet
import com.example.habitflow.ui.components.HabitCard
import com.example.habitflow.ui.viewmodel.HabitViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HabitViewModel,
    onHabitClick: (Long) -> Unit,
    onNavigateToArchive: () -> Unit
) {
    val habits by viewModel.habits.collectAsState()
    var showAddSheet by remember { mutableStateOf(false) }
    val addSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    
    var habitToDelete by remember { mutableStateOf<HabitEntity?>(null) }
    var habitToArchive by remember { mutableStateOf<HabitEntity?>(null) }
    var habitToLog by remember { mutableStateOf<HabitEntity?>(null) }
    var showOptionsForHabit by remember { mutableStateOf<HabitEntity?>(null) }
    
    // Export State
    var showExportSheet by remember { mutableStateOf(false) }
    val exportSheetState = rememberModalBottomSheetState()
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
                onClick = { showAddSheet = true },
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
                    text = "Focus",
                    style = MaterialTheme.typography.displayLarge.copy(fontSize = 40.sp),
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Row {
                    // 生成测试数据按钮
                    IconButton(onClick = { 
                        viewModel.generateTestData()
                        Toast.makeText(context, "Test data generated", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Generate test data",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                    
                    // 导出按钮
                    IconButton(onClick = { showExportSheet = true }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = stringResource(R.string.export_data),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    // 归档页面按钮
                    IconButton(onClick = onNavigateToArchive) {
                        Icon(
                            imageVector = Icons.Default.Email, // Using Email as Archive placeholder
                            contentDescription = "Archived Habits",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            
            // 空状态提示
            if (habits.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "No habits yet",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Tap the blue button above to generate test data\nor tap + to create your first habit",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
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
                            onLongClick = { showOptionsForHabit = habit },
                            onLogClick = { habitToLog = habit }
                        )
                    }
                }
            }
        }

        if (showAddSheet) {
            AddHabitBottomSheet(
                sheetState = addSheetState,
                onDismiss = { showAddSheet = false },
                onConfirm = { name, target, color, icon ->
                    viewModel.addHabit(name, target, color, icon)
                    scope.launch { addSheetState.hide() }.invokeOnCompletion {
                        if (!addSheetState.isVisible) {
                            showAddSheet = false
                        }
                    }
                }
            )
        }

        if (showExportSheet) {
            ExportBottomSheet(
                sheetState = exportSheetState,
                onDismiss = { showExportSheet = false },
                onConfirm = { format ->
                    exportFormat = format
                    val fileName = "habitflow_backup_${System.currentTimeMillis()}.${format.lowercase()}"
                    exportLauncher.launch(fileName)
                    scope.launch { exportSheetState.hide() }.invokeOnCompletion {
                        if (!exportSheetState.isVisible) {
                            showExportSheet = false
                        }
                    }
                }
            )
        }
        
        // Habit Options Menu (Simulated with Dialog)
        showOptionsForHabit?.let { habit ->
            AlertDialog(
                onDismissRequest = { showOptionsForHabit = null },
                title = { Text(stringResource(R.string.manage_habit, habit.name)) }, 
                text = { 
                    Column {
                        TextButton(
                            onClick = {
                                habitToArchive = habit
                                showOptionsForHabit = null
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(stringResource(R.string.archive_habit))
                        }
                        TextButton(
                            onClick = {
                                habitToDelete = habit
                                showOptionsForHabit = null
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text(stringResource(R.string.delete_habit))
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = { showOptionsForHabit = null }) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }

        // Archive Confirmation
        habitToArchive?.let { habit ->
            AlertDialog(
                onDismissRequest = { habitToArchive = null },
                title = { Text(stringResource(R.string.archive_habit)) },
                text = { Text(stringResource(R.string.archive_habit_confirm, habit.name)) },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.archiveHabit(habit)
                            habitToArchive = null
                        }
                    ) {
                        Text(stringResource(R.string.archive))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { habitToArchive = null }) {
                        Text(stringResource(R.string.cancel))
                    }
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
                onConfirm = { amount, note ->
                    viewModel.incrementHabit(habit, amount, note)
                    habitToLog = null
                }
            )
        }
    }
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
    val themeColor = try {
        Color(android.graphics.Color.parseColor(habit.colorHex))
    } catch (e: Exception) {
        MaterialTheme.colorScheme.primary
    }

    HabitCard(
        habitName = habit.name,
        currentCount = currentCount,
        targetCount = habit.dailyTarget,
        streak = habit.currentStreak,
        iconName = habit.iconName,  // 传递图标名称
        themeColor = themeColor,
        onIncrement = onLogClick,
        modifier = Modifier.combinedClickable(
            onClick = onClick,
            onLongClick = onLongClick
        )
    )
}

@Composable
fun LogProgressDialog(
    habitName: String,
    onDismiss: () -> Unit,
    onConfirm: (Int, String?) -> Unit
) {
    var customAmount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.log_habit, habitName)) },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Text(stringResource(R.string.quick_add), style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    OutlinedButton(onClick = { onConfirm(1, note.ifBlank { null }) }) { Text("+1") }
                    OutlinedButton(onClick = { onConfirm(5, note.ifBlank { null }) }) { Text("+5") }
                    OutlinedButton(onClick = { onConfirm(10, note.ifBlank { null }) }) { Text("+10") }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(stringResource(R.string.custom_amount), style = MaterialTheme.typography.labelMedium)
                OutlinedTextField(
                    value = customAmount,
                    onValueChange = { customAmount = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    placeholder = { Text(stringResource(R.string.input_number_placeholder)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp) // More prominent shape
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                Text(stringResource(R.string.note_optional), style = MaterialTheme.typography.labelMedium)
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    placeholder = { Text(stringResource(R.string.note_placeholder)) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            val amount = customAmount.toIntOrNull()
            val isEnabled = amount != null && amount > 0
            
            Button(
                onClick = {
                    if (isEnabled) {
                        onConfirm(amount!!, note.ifBlank { null })
                    }
                },
                enabled = isEnabled,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.add_custom))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}