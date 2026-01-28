package com.example.habitflow.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.habitflow.data.entity.HabitRecordEntity
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun HabitTrendChart(
    records: List<HabitRecordEntity>,
    colorHex: String,
    modifier: Modifier = Modifier
) {
    val themeColor = remember(colorHex) {
        try {
            Color(android.graphics.Color.parseColor(colorHex))
        } catch (e: Exception) {
            Color(0xFF2196F3)
        }
    }

    // 获取最近30天的数据
    val last30Days = remember(records) {
        val today = LocalDate.now()
        val thirtyDaysAgo = today.minusDays(29)
        
        // 创建30天的完整数据集（包括0值的天）
        val dateMap = records.associate { 
            LocalDate.ofEpochDay(it.date) to it.count.toFloat() 
        }
        
        (0 until 30).map { daysAgo ->
            val date = today.minusDays(29 - daysAgo.toLong())
            val value = dateMap[date] ?: 0f
            date to value
        }
    }

    if (last30Days.isEmpty()) return

    Column(modifier = modifier.fillMaxWidth()) {
        // 图表标题
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "30-Day Trend",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Max: ${last30Days.maxOf { it.second }.toInt()}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))

        // 图表区域
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .padding(start = 40.dp, bottom = 30.dp, end = 10.dp, top = 10.dp)
        ) {
            val width = size.width
            val height = size.height
            val dataPoints = last30Days.map { it.second }
            val dates = last30Days.map { it.first }
            val maxVal = (dataPoints.maxOrNull() ?: 0f).coerceAtLeast(1f)
            val spacing = width / (dataPoints.size - 1).coerceAtLeast(1)

            // 绘制Y轴网格线和标签
            val ySteps = 5
            for (i in 0..ySteps) {
                val y = height - (i * (height / ySteps))
                val value = (maxVal * i / ySteps).toInt()
                
                // 网格线
                drawLine(
                    color = Color.LightGray.copy(alpha = 0.2f),
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                )
                
                // Y轴标签
                drawContext.canvas.nativeCanvas.apply {
                    val paint = android.graphics.Paint().apply {
                        color = android.graphics.Color.GRAY
                        textSize = 28f
                        textAlign = android.graphics.Paint.Align.RIGHT
                    }
                    drawText(
                        value.toString(),
                        -15f,
                        y + 10f,
                        paint
                    )
                }
            }

            // 绘制X轴日期标签（每5天显示一个）
            val showEvery = 5
            dates.forEachIndexed { index, date ->
                if (index % showEvery == 0 || index == dates.lastIndex) {
                    val x = index * spacing
                    val label = date.format(DateTimeFormatter.ofPattern("M/d"))
                    
                    drawContext.canvas.nativeCanvas.apply {
                        val paint = android.graphics.Paint().apply {
                            color = android.graphics.Color.GRAY
                            textSize = 28f
                            textAlign = android.graphics.Paint.Align.CENTER
                        }
                        drawText(
                            label,
                            x,
                            height + 40f,
                            paint
                        )
                    }
                }
            }

            // 数据点坐标
            val points = dataPoints.mapIndexed { index, value ->
                val x = index * spacing
                val y = height - (value / maxVal * height * 0.9f)
                Offset(x, y)
            }

            // 绘制曲线路径
            val path = Path().apply {
                if (points.isNotEmpty()) {
                    moveTo(points[0].x, points[0].y)
                    for (i in 1 until points.size) {
                        val prev = points[i - 1]
                        val current = points[i]
                        val control1 = Offset(prev.x + (current.x - prev.x) / 2, prev.y)
                        val control2 = Offset(prev.x + (current.x - prev.x) / 2, current.y)
                        cubicTo(control1.x, control1.y, control2.x, control2.y, current.x, current.y)
                    }
                }
            }

            // 填充渐变
            val fillPath = Path().apply {
                addPath(path)
                if (points.isNotEmpty()) {
                    lineTo(points.last().x, height)
                    lineTo(points.first().x, height)
                    close()
                }
            }

            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(themeColor.copy(alpha = 0.3f), Color.Transparent),
                    startY = points.minOfOrNull { it.y } ?: 0f,
                    endY = height
                )
            )

            // 绘制曲线
            drawPath(
                path = path,
                color = themeColor,
                style = Stroke(width = 3.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
            )

            // 绘制数据点
            points.forEach { point ->
                drawCircle(
                    color = themeColor,
                    radius = 4.dp.toPx(),
                    center = point
                )
                drawCircle(
                    color = Color.White,
                    radius = 2.dp.toPx(),
                    center = point
                )
            }
        }
    }
}