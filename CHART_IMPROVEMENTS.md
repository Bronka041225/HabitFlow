# 图表优化 - 坐标轴 & 月度视图

## ✅ 完成的改进

### 1. 趋势图添加坐标轴

**文件：** `HabitTrendChart.kt`

#### 新增功能：

**Y轴（纵轴）**
- ✅ 显示数值标签（0 到最大值，分5档）
- ✅ 右对齐显示在图表左侧
- ✅ 淡灰色虚线网格
- ✅ 自动根据数据最大值缩放

**X轴（横轴）**
- ✅ 显示日期标签（格式：M/d，如 1/28）
- ✅ 每5天显示一个标签 + 最后一天
- ✅ 居中对齐在图表底部
- ✅ 显示最近30天数据

**图表标题**
- ✅ "30天趋势" 标题
- ✅ 显示最大值统计

#### 视觉效果：
```
30天趋势                     最大值: 50
  
50  ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─
40  ─ ─ ─ ─ ─ ─ ─╱─ ─ ─ ─ ─ ─ ─ ─
30  ─ ─ ─ ─ ─ ─╱ ╲ ─ ─ ─ ─ ─ ─ ─
20  ─ ─ ─ ─ ─╱   ╲─ ─ ─ ─ ─ ─ ─
10  ─ ─ ─ ─╱       ╲ ─ ─ ─ ─ ─ ─
0   ━━━━━━━━━━━━━━━━━━━━━━━━━━
   1/1  1/6  1/11 1/16 1/21 1/26
```

---

### 2. 月度热力图视图

**文件：** `HabitHeatmap.kt`

#### 核心改进：

**月度布局**
- ✅ 按月份分组显示
- ✅ 每月显示完整日历网格
- ✅ 星期标题（一、二、三...）
- ✅ 日期数字显示在每个格子中

**数据范围**
- ✅ 默认显示最近6个月
- ✅ "查看全部"按钮查看所有历史数据
- ✅ "收起"按钮折叠到6个月
- ✅ 自动检测是否有6个月以上数据

**视觉设计**
- ✅ 月份标题（如：2026年 1月）
- ✅ 日期在格子中心显示
- ✅ 有数据的日期加粗显示
- ✅ 高强度日期用白色文字
- ✅ 空白日期透明处理

#### 月度视图示例：
```
坚持记录                          [查看全部]

2026年 1月
一  二  三  四  五  六  日
        1   2   3   4   5
6   7   8   9  10  11  12
13  14  15  16  17  18  19
20  21  22  23  24  25  26
27  28  29  30  31

2025年 12月
一  二  三  四  五  六  日
                    1
2   3   4   5   6   7   8
9  10  11  12  13  14  15
16  17  18  19  20  21  22
23  24  25  26  27  28  29
30  31

少 ▢ ▢ ▢ ▢ 多
```

---

## 🎨 数据可视化特性

### 趋势图 (TrendChart)

| 特性 | 说明 |
|------|------|
| **时间范围** | 最近30天 |
| **数据点** | 包含0值的完整30天数据 |
| **Y轴刻度** | 5档，从0到最大值 |
| **X轴标签** | 每5天 + 最后一天 |
| **曲线** | 贝塞尔平滑曲线 |
| **填充** | 渐变填充（30% → 透明）|
| **网格线** | 虚线，20%透明度 |

### 月度热力图 (Monthly Heatmap)

| 特性 | 说明 |
|------|------|
| **默认显示** | 最近6个月 |
| **扩展查看** | 全部历史数据 |
| **布局方式** | 日历网格（7列） |
| **周起始** | 周一 |
| **日期显示** | 格子中心，1-31 |
| **强度计算** | count / target |
| **颜色范围** | 20% → 100% alpha |

---

## 📊 强度计算公式

### 颜色透明度
```kotlin
if (count > 0 && target > 0) {
    intensity = (count / target).coerceIn(0f, 1f)
    alpha = 0.2f + (intensity * 0.8f)  // 范围：20% - 100%
} else {
    // 无数据：灰色 30% 透明
    surfaceVariant.copy(alpha = 0.3f)
}
```

### 文字颜色
```kotlin
textColor = if (intensity > 0.5f) {
    Color.White  // 高强度：白色文字
} else {
    onSurface.copy(alpha = 0.6f)  // 低强度：灰色文字
}
```

---

## 🔧 使用示例

### 在 HabitDetailScreen 中

```kotlin
// 趋势图（已包含坐标轴）
Text(
    text = "近期趋势",
    style = MaterialTheme.typography.titleMedium,
    fontWeight = FontWeight.Bold
)
Spacer(modifier = Modifier.height(16.dp))
HabitTrendChart(
    records = allRecords,
    colorHex = habit.colorHex
)

Spacer(modifier = Modifier.height(32.dp))

// 月度热力图（默认6个月，可展开全部）
HabitHeatmap(
    records = allRecords,
    target = habit.dailyTarget,
    colorHex = habit.colorHex
)
```

---

## 📱 交互功能

### 月度视图交互

1. **默认状态**
   - 显示最近6个月
   - 如果总数据 ≤ 6个月，不显示按钮

2. **展开状态**
   - 点击"查看全部"
   - 显示所有历史月份
   - 按钮变为"收起"

3. **收起状态**
   - 点击"收起"
   - 返回显示最近6个月

---

## 🎯 数据处理逻辑

### 趋势图数据准备

```kotlin
// 1. 获取最近30天的日期范围
val today = LocalDate.now()
val thirtyDaysAgo = today.minusDays(29)

// 2. 创建完整的30天数据（包括0值）
val last30Days = (0 until 30).map { daysAgo ->
    val date = today.minusDays(29 - daysAgo.toLong())
    val value = recordMap[date] ?: 0f
    date to value
}
```

### 月度热力图数据准备

```kotlin
// 1. 获取所有有数据的月份范围
val oldestMonth = YearMonth.from(oldestDate)
val newestMonth = YearMonth.from(newestDate)

// 2. 生成月份序列（从新到旧）
val allMonths = generateSequence(newestMonth) { month ->
    if (month > oldestMonth) month.minusMonths(1) else null
}.toList()

// 3. 每月生成完整日历网格（1-31）
val daysList = (1..daysInMonth).map { dayOfMonth ->
    val date = yearMonth.atDay(dayOfMonth)
    val count = recordMap[date] ?: 0
    val intensity = (count / target).coerceIn(0f, 1f)
    DayData(date, count, intensity)
}
```

---

## 🌟 视觉改进对比

### 趋势图

#### 之前 ❌
- 无坐标轴
- 不知道具体数值
- 不知道日期
- 只显示10个点

#### 现在 ✅
- Y轴显示数值（0-最大值）
- X轴显示日期（M/d格式）
- 完整30天数据
- 网格线辅助阅读

### 坚持记录

#### 之前 ❌
- 84天小格子密集
- 无日期标识
- 不知道具体哪一天
- 无法查看历史

#### 现在 ✅
- 月度日历视图
- 清晰的日期数字
- 星期标题
- 可展开查看全部历史

---

## 📁 修改的文件

1. ✅ `HabitTrendChart.kt` - 添加完整坐标轴系统
2. ✅ `HabitHeatmap.kt` - 重构为月度日历视图

---

## 🚀 编译与测试

```powershell
cd D:\GitHub\HabitFlow
.\gradlew.bat clean assembleDebug
```

### 测试步骤

1. **生成测试数据**
   - 点击右上角蓝色按钮

2. **查看习惯详情**
   - 点击任意习惯卡片

3. **验证趋势图**
   - 检查Y轴数值标签
   - 检查X轴日期标签
   - 查看30天完整曲线

4. **验证月度视图**
   - 查看最近6个月展示
   - 点击"查看全部"查看历史
   - 验证日期数字显示
   - 检查颜色强度变化

---

## 💡 技术亮点

### Canvas 绘制优化

```kotlin
// 使用 nativeCanvas 绘制文字
drawContext.canvas.nativeCanvas.apply {
    val paint = android.graphics.Paint().apply {
        color = android.graphics.Color.GRAY
        textSize = 28f
        textAlign = android.graphics.Paint.Align.CENTER
    }
    drawText(label, x, y, paint)
}
```

### 日期格式化

```kotlin
// Y轴标签
val formatter = DateTimeFormatter.ofPattern("M/d")
date.format(formatter)  // 输出：1/28

// 月份标题
val formatter = DateTimeFormatter.ofPattern("yyyy年 M月")
yearMonth.format(formatter)  // 输出：2026年 1月
```

### 状态管理

```kotlin
var showAllMonths by remember { mutableStateOf(false) }

val displayMonths = if (showAllMonths) {
    monthlyData  // 全部
} else {
    monthlyData.take(6)  // 最近6个月
}
```

---

**状态：** ✅ 完成  
**更新时间：** 2026-01-28  
**兼容性：** Android 8.0+ (API 26+)
