package com.example.habitflow.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class HabitIcon(
    val name: String,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHabitBottomSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onConfirm: (String, Int, String, String) -> Unit
) {
    var habitName by remember { mutableStateOf("") }
    var dailyGoal by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf("#FF6D00") }
    var selectedIcon by remember { mutableStateOf("Star") }
    
    val availableColors = listOf(
        "#FF6D00" to "Orange",
        "#2196F3" to "Blue",
        "#4CAF50" to "Green",
        "#00BCD4" to "Cyan",
        "#9C27B0" to "Purple",
        "#E91E63" to "Pink",
        "#FF5722" to "Red"
    )
    
    val availableIcons = listOf(
        HabitIcon("Star", Icons.Default.Star),
        HabitIcon("Water", Icons.Default.WaterDrop),
        HabitIcon("Book", Icons.Default.MenuBook),
        HabitIcon("Fitness", Icons.Default.FitnessCenter),
        HabitIcon("Run", Icons.AutoMirrored.Filled.DirectionsRun),
        HabitIcon("Meditation", Icons.Default.SelfImprovement),
        HabitIcon("Music", Icons.Default.MusicNote),
        HabitIcon("Code", Icons.Default.Code),
        HabitIcon("Coffee", Icons.Default.LocalCafe),
        HabitIcon("Bed", Icons.Default.Bedtime),
        HabitIcon("Favorite", Icons.Default.Favorite),
        HabitIcon("Timer", Icons.Default.Timer)
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header
            Text(
                text = "New Habit",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            // Habit Name Input
            OutlinedTextField(
                value = habitName,
                onValueChange = { habitName = it },
                label = { Text("Habit Name") },
                placeholder = { Text("e.g., Morning Pushups") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Habit Name",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
                singleLine = true
            )
            
            // Daily Goal Input
            OutlinedTextField(
                value = dailyGoal,
                onValueChange = { if (it.all { char -> char.isDigit() }) dailyGoal = it },
                label = { Text("Daily Goal") },
                placeholder = { Text("e.g., 50") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Flag,
                        contentDescription = "Daily Goal",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
                singleLine = true
            )
            
            // Color Picker Section
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Theme Color",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    availableColors.forEach { (hex, _) ->
                        ColorSwatch(
                            color = Color(android.graphics.Color.parseColor(hex)),
                            isSelected = selectedColor == hex,
                            onClick = { selectedColor = hex }
                        )
                    }
                }
            }
            
            // Icon Picker Section
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Icon",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(availableIcons) { habitIcon ->
                        IconSwatch(
                            icon = habitIcon.icon,
                            iconName = habitIcon.name,
                            isSelected = selectedIcon == habitIcon.name,
                            onClick = { selectedIcon = habitIcon.name },
                            selectedColor = Color(android.graphics.Color.parseColor(selectedColor))
                        )
                    }
                }
            }
            
            // Create Button
            Button(
                onClick = {
                    if (habitName.isNotBlank() && dailyGoal.isNotBlank()) {
                        onConfirm(
                            habitName,
                            dailyGoal.toIntOrNull() ?: 1,
                            selectedColor,
                            selectedIcon
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = MaterialTheme.shapes.medium,
                enabled = habitName.isNotBlank() && dailyGoal.isNotBlank()
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Create Habit",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ColorSwatch(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(color)
            .border(
                width = if (isSelected) 3.dp else 0.dp,
                color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                shape = CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun IconSwatch(
    icon: ImageVector,
    iconName: String,
    isSelected: Boolean,
    selectedColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(
                if (isSelected) selectedColor.copy(alpha = 0.2f) 
                else MaterialTheme.colorScheme.surfaceVariant
            )
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) selectedColor else Color.Transparent,
                shape = MaterialTheme.shapes.medium
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = iconName,
            tint = if (isSelected) selectedColor else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(28.dp)
        )
    }
}

