package com.example.habitflow.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.habitflow.data.entity.HabitRecordEntity
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

data class MonthData(
    val yearMonth: YearMonth,
    val days: List<DayData>
)

data class DayData(
    val date: LocalDate,
    val count: Int,
    val intensity: Float
)

@Composable
fun HabitHeatmap(
    records: List<HabitRecordEntity>,
    target: Int,
    colorHex: String,
    days: Int = 84 // 默认保留参数兼容性
) {
    val themeColor = remember(colorHex) {
        try {
            Color(android.graphics.Color.parseColor(colorHex))
        } catch (e: Exception) {
            Color(0xFF2196F3)
        }
    }

    var showAllMonths by remember { mutableStateOf(false) }

    // 将记录转换为月度数据
    val monthlyData = remember(records, target) {
        val recordMap = records.associate { 
            LocalDate.ofEpochDay(it.date) to it.count 
        }
        
        val today = LocalDate.now()
        val currentMonth = YearMonth.from(today)
        
        // 获取所有有数据的月份
        val allMonths = if (records.isNotEmpty()) {
            val oldestDate = LocalDate.ofEpochDay(records.minOf { it.date })
            val newestDate = LocalDate.ofEpochDay(records.maxOf { it.date })
            
            val oldestMonth = YearMonth.from(oldestDate)
            val newestMonth = YearMonth.from(newestDate)
            
            generateSequence(newestMonth) { month ->
                if (month > oldestMonth) month.minusMonths(1) else null
            }.toList()
        } else {
            (0 until 6).map { currentMonth.minusMonths(it.toLong()) }
        }
        
        allMonths.map { yearMonth ->
            val firstDay = yearMonth.atDay(1)
            val lastDay = yearMonth.atEndOfMonth()
            val daysInMonth = yearMonth.lengthOfMonth()
            
            val daysList = (1..daysInMonth).map { dayOfMonth ->
                val date = yearMonth.atDay(dayOfMonth)
                val count = recordMap[date] ?: 0
                val intensity = if (target > 0 && count > 0) {
                    (count.toFloat() / target).coerceIn(0f, 1f)
                } else {
                    0f
                }
                
                DayData(date, count, intensity)
            }
            
            MonthData(yearMonth, daysList)
        }
    }

    val displayMonths = if (showAllMonths) monthlyData else monthlyData.take(1)

    Column(modifier = Modifier.fillMaxWidth()) {
        // 标题和切换按钮
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Consistency",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            if (monthlyData.size > 1) {
                OutlinedButton(
                    onClick = { showAllMonths = !showAllMonths },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (showAllMonths) "Collapse" else "View All",
                        fontSize = 12.sp
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 月份列表
        displayMonths.forEach { monthData ->
            MonthHeatmapView(
                monthData = monthData,
                themeColor = themeColor
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // 图例
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Less",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(4.dp))
            listOf(0f, 0.3f, 0.6f, 1f).forEach { intensity ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 2.dp)
                        .size(12.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            if (intensity == 0f) {
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            } else {
                                themeColor.copy(alpha = 0.2f + (intensity * 0.8f))
                            }
                        )
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "More",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun MonthHeatmapView(
    monthData: MonthData,
    themeColor: Color
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // 月份标题
        Text(
            text = monthData.yearMonth.format(DateTimeFormatter.ofPattern("yyyy年 M月")),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 星期标题
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach { day ->
                Text(
                    text = day,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // 日期网格 (按周排列) - 修复对齐问题
        // 获取本月第一天是星期几（1=周一, 7=周日）
        val firstDayOfWeek = monthData.days.first().date.dayOfWeek.value
        val paddingDays = firstDayOfWeek - 1  // 周一前需要的空白格数
        
        // 构建完整的日历网格
        val allDays = List(paddingDays) { null } + monthData.days
        val weeks = allDays.chunked(7)
        
        weeks.forEach { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // 确保每周都有7个格子
                val fullWeek = week + List((7 - week.size).coerceAtLeast(0)) { null }
                
                fullWeek.forEach { dayData ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                if (dayData == null) {
                                    Color.Transparent
                                } else if (dayData.intensity > 0) {
                                    themeColor.copy(alpha = 0.2f + (dayData.intensity * 0.8f))
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        dayData?.let {
                            Text(
                                text = it.date.dayOfMonth.toString(),
                                fontSize = 10.sp,
                                color = if (it.intensity > 0.5f) {
                                    Color.White
                                } else {
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                },
                                fontWeight = if (it.count > 0) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}