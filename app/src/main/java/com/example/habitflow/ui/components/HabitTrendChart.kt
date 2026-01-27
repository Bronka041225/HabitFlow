package com.example.habitflow.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.example.habitflow.data.entity.HabitRecordEntity
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollState
import com.patrykandpatrick.vico.compose.component.textComponent
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun HabitTrendChart(
    records: List<HabitRecordEntity>,
    colorHex: String
) {
    // 1. Prepare Data: Last 30 days
    val (entries, startEpoch) = remember(records) {
        val today = LocalDate.now().toEpochDay()
        val start = today - 29
        val map = records.associate { it.date to it.count }
        
        val list = (0 until 30).map { offset ->
            val day = start + offset
            FloatEntry(x = offset.toFloat(), y = map[day]?.toFloat() ?: 0f)
        }
        list to start
    }

    val modelProducer = remember(entries) { ChartEntryModelProducer(entries) }
    
    // 2. Color Parsing
    val lineColor = try {
        Color(android.graphics.Color.parseColor(colorHex))
    } catch (e: Exception) {
        MaterialTheme.colorScheme.primary
    }
    
    // 3. Axis Styling
    val axisLabelColor = MaterialTheme.colorScheme.onSurface
    
    // Axis Formatter
    val bottomAxisFormatter = AxisValueFormatter<com.patrykandpatrick.vico.core.axis.AxisPosition.Horizontal.Bottom> { value, _ ->
        val date = LocalDate.ofEpochDay(startEpoch + value.toLong())
        date.dayOfMonth.toString()
    }
    
    val scrollState = rememberChartScrollState()

    Chart(
        chart = lineChart(
            lines = listOf(
                LineChart.LineSpec(
                    lineColor = lineColor.toArgb(),
                    lineBackgroundShader = null
                )
            )
        ),
        chartModelProducer = modelProducer,
        startAxis = rememberStartAxis(
            valueFormatter = { value, _ -> value.toInt().toString() },
            label = textComponent(
                color = axisLabelColor
            )
        ),
        bottomAxis = rememberBottomAxis(
            valueFormatter = bottomAxisFormatter,
            label = textComponent(
                color = axisLabelColor
            )
        ),
        chartScrollState = scrollState,
        isZoomEnabled = true,
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
    )
}
