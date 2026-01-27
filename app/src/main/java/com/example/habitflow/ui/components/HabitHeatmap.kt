package com.example.habitflow.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.habitflow.data.entity.HabitRecordEntity
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@Composable
fun HabitHeatmap(
    records: List<HabitRecordEntity>,
    target: Int,
    colorHex: String,
    modifier: Modifier = Modifier
) {
    // 1. Generate List of Months (Past 12 Months)
    val months = remember {
        val current = YearMonth.now()
        (0 until 12).map { current.minusMonths(it.toLong()) }.reversed()
    }
    
    val listState = rememberLazyListState()

    // Scroll to the last month (current month)
    LaunchedEffect(Unit) {
        listState.scrollToItem(months.size - 1)
    }

    // Map records for fast lookup
    val recordsMap = remember(records) {
        records.associate { it.date to it.count }
    }

    val baseColor = try {
        Color(android.graphics.Color.parseColor(colorHex))
    } catch (e: Exception) {
        MaterialTheme.colorScheme.primary
    }

    LazyRow(
        state = listState,
        modifier = modifier.height(260.dp), // Adjusted height
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(months.size) { index ->
            val month = months[index]
            MonthHeatmapCard(
                yearMonth = month,
                recordsMap = recordsMap,
                target = target,
                baseColor = baseColor
            )
        }
    }
}

@Composable
fun MonthHeatmapCard(
    yearMonth: YearMonth,
    recordsMap: Map<Long, Int>,
    target: Int,
    baseColor: Color
) {
    // UI Constants
    val cellSize = 24.dp
    val spacing = 4.dp
    val cols = 7
    
    // Performance: Pre-calculate layout info
    val daysInMonth = yearMonth.lengthOfMonth()
    val rows = (daysInMonth + cols - 1) / cols // Ceiling division
    val height = (cellSize * rows) + (spacing * (rows - 1))
    
    val emptyColor = Color.Gray.copy(alpha = 0.15f)

    Column(
        modifier = Modifier
            .width(220.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp)
    ) {
        // Month Label
        Text(
            text = yearMonth.format(DateTimeFormatter.ofPattern("yyyy年 MM月")),
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(12.dp))

        // Canvas Rendering for High Performance
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
        ) {
            val cellPx = cellSize.toPx()
            val spacingPx = spacing.toPx()
            val radiusPx = 4.dp.toPx()

            for (day in 1..daysInMonth) {
                // 0-based index
                val index = day - 1
                val row = index / cols
                val col = index % cols

                val x = col * (cellPx + spacingPx)
                val y = row * (cellPx + spacingPx)

                // Logic
                val date = yearMonth.atDay(day)
                val epochDay = date.toEpochDay()
                val count = recordsMap[epochDay] ?: 0
                
                val intensity = if (target > 0) (count.toFloat() / target).coerceIn(0f, 1f) else 0f
                
                val cellColor = if (count > 0) {
                    baseColor.copy(alpha = 0.2f + (0.8f * intensity))
                } else {
                    emptyColor
                }

                drawRoundRect(
                    color = cellColor,
                    topLeft = Offset(x, y),
                    size = Size(cellPx, cellPx),
                    cornerRadius = CornerRadius(radiusPx)
                )
            }
        }
    }
}
