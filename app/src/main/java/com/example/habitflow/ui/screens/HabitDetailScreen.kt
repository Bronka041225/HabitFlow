package com.example.habitflow.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.habitflow.R
import com.example.habitflow.data.entity.AchievementEntity
import com.example.habitflow.data.entity.HabitRecordEntity
import com.example.habitflow.ui.components.EditRecordDialog
import com.example.habitflow.ui.components.HabitHeatmap
import com.example.habitflow.ui.components.HabitTrendChart
import com.example.habitflow.ui.viewmodel.HabitViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitDetailScreen(
    habitId: Long,
    viewModel: HabitViewModel,
    navController: NavController
) {
    val habit by viewModel.getHabitById(habitId).collectAsState(initial = null)
    val allRecords by viewModel.getRecordsForHabit(habitId).collectAsState(initial = emptyList())
    val recentRecords by viewModel.getLast30DaysRecords(habitId).collectAsState(initial = emptyList())
    val achievements by viewModel.getAchievements(habitId).collectAsState(initial = emptyList())
    val stats by viewModel.getHabitStats(habitId).collectAsState(initial = com.example.habitflow.ui.viewmodel.HabitStats())
    
    var recordToEdit by remember { mutableStateOf<HabitRecordEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = habit?.name ?: stringResource(R.string.detail_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            habit?.let { h ->
                // Total Count & Streak
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        val totalCount = allRecords.sumOf { it.count }
                        Text(
                            text = "$totalCount",
                            style = MaterialTheme.typography.displayLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = stringResource(R.string.total_label, h.unit.uppercase()),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = stringResource(R.string.current_streak, h.currentStreak),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = stringResource(R.string.longest_streak, h.longestStreak),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                
                // Achievements Section
                if (achievements.isNotEmpty()) {
                    Text(
                        text = stringResource(R.string.achievements_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(achievements) { achievement ->
                            AchievementCard(achievement)
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Stats Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedCard(
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "${h.currentStreak}",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Current Streak",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    OutlinedCard(
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "${h.longestStreak}",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Best Streak",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Insights Section
                Text(
                    text = "Insights",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatCard(
                        title = "Total Volume",
                        value = "${stats.totalVolume} ${h.unit}",
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Best Day",
                        value = stats.bestDayOfWeek,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatCard(
                        title = "Daily Avg",
                        value = String.format("%.1f", stats.averagePerDay),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Completion",
                        value = String.format("%.0f%%", stats.completionRate * 100),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Heatmap Section
                Text(
                    text = stringResource(R.string.consistency_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                HabitHeatmap(
                    records = allRecords,
                    target = h.dailyTarget,
                    colorHex = h.colorHex,
                    onDayClick = { date, record ->
                        if (record != null) {
                            recordToEdit = record
                        } else {
                            // Create a temporary record for today or past date to start editing
                            // If user saves, it will be inserted.
                            recordToEdit = HabitRecordEntity(
                                habitId = habitId,
                                date = date.toEpochDay(),
                                count = 0,
                                note = null
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Trend Section
                Text(
                    text = stringResource(R.string.trend_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                HabitTrendChart(
                    records = recentRecords,
                    colorHex = h.colorHex
                )

                Spacer(modifier = Modifier.height(32.dp))

                // History Section (Merged into Heatmap)
                // Click on heatmap days to view/edit history
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        recordToEdit?.let { record ->
            EditRecordDialog(
                record = record,
                onDismiss = { recordToEdit = null },
                onSave = { count, note ->
                    viewModel.updateRecord(record.copy(count = count, note = note))
                    recordToEdit = null
                },
                onDelete = {
                    viewModel.deleteRecord(record)
                    recordToEdit = null
                }
            )
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun AchievementCard(achievement: AchievementEntity) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        modifier = Modifier.width(140.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = Color(0xFFFFD700), // Gold
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = achievement.title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = achievement.description,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                minLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 12.sp
            )
        }
    }
}