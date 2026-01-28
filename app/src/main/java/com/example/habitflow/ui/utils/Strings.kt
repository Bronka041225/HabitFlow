package com.example.habitflow.ui.utils

object Strings {
    // 语言设置
    fun language(lang: String) = when (lang) {
        "zh" -> "中文"
        else -> "English"
    }
    
    fun switchLanguage(lang: String) = when (lang) {
        "zh" -> "Switch Language"
        else -> "切换语言"
    }
    
    // 主页
    fun title(lang: String) = when (lang) {
        "zh" -> "专注"
        else -> "Focus"
    }
    
    fun generateTestData(lang: String) = when (lang) {
        "zh" -> "生成测试数据"
        else -> "Generate Test Data"
    }
    
    fun testDataGenerated(lang: String) = when (lang) {
        "zh" -> "已生成测试数据"
        else -> "Test data generated"
    }
    
    fun noHabitsYet(lang: String) = when (lang) {
        "zh" -> "还没有习惯"
        else -> "No habits yet"
    }
    
    fun noHabitsHint(lang: String) = when (lang) {
        "zh" -> "点击右上角蓝色按钮生成测试数据\n或点击 + 号创建你的第一个习惯"
        else -> "Tap the blue button to generate test data\nor tap + to create your first habit"
    }
    
    // Bottom Sheet
    fun newHabit(lang: String) = when (lang) {
        "zh" -> "新习惯"
        else -> "New Habit"
    }
    
    fun habitName(lang: String) = when (lang) {
        "zh" -> "习惯名称"
        else -> "Habit Name"
    }
    
    fun habitNamePlaceholder(lang: String) = when (lang) {
        "zh" -> "例如：俯卧撑"
        else -> "e.g., Morning Pushups"
    }
    
    fun dailyGoal(lang: String) = when (lang) {
        "zh" -> "每日目标"
        else -> "Daily Goal"
    }
    
    fun dailyGoalPlaceholder(lang: String) = when (lang) {
        "zh" -> "例如：50"
        else -> "e.g., 50"
    }
    
    fun themeColor(lang: String) = when (lang) {
        "zh" -> "主题颜色"
        else -> "Theme Color"
    }
    
    fun icon(lang: String) = when (lang) {
        "zh" -> "图标"
        else -> "Icon"
    }
    
    fun createHabit(lang: String) = when (lang) {
        "zh" -> "创建习惯"
        else -> "Create Habit"
    }
    
    // Trend Chart
    fun trendTitle(lang: String) = when (lang) {
        "zh" -> "30天趋势"
        else -> "30-Day Trend"
    }
    
    fun maxValue(lang: String, value: Int) = when (lang) {
        "zh" -> "最大值: $value"
        else -> "Max: $value"
    }
    
    // Heatmap
    fun consistency(lang: String) = when (lang) {
        "zh" -> "坚持记录"
        else -> "Consistency"
    }
    
    fun viewAll(lang: String) = when (lang) {
        "zh" -> "查看全部"
        else -> "View All"
    }
    
    fun collapse(lang: String) = when (lang) {
        "zh" -> "收起"
        else -> "Collapse"
    }
    
    fun less(lang: String) = when (lang) {
        "zh" -> "少"
        else -> "Less"
    }
    
    fun more(lang: String) = when (lang) {
        "zh" -> "多"
        else -> "More"
    }
    
    // Stats
    fun currentStreak(lang: String) = when (lang) {
        "zh" -> "当前连续"
        else -> "Current Streak"
    }
    
    fun bestStreak(lang: String) = when (lang) {
        "zh" -> "最佳连续"
        else -> "Best Streak"
    }
    
    // Weekdays
    fun monday(lang: String) = when (lang) {
        "zh" -> "一"
        else -> "Mon"
    }
    
    fun tuesday(lang: String) = when (lang) {
        "zh" -> "二"
        else -> "Tue"
    }
    
    fun wednesday(lang: String) = when (lang) {
        "zh" -> "三"
        else -> "Wed"
    }
    
    fun thursday(lang: String) = when (lang) {
        "zh" -> "四"
        else -> "Thu"
    }
    
    fun friday(lang: String) = when (lang) {
        "zh" -> "五"
        else -> "Fri"
    }
    
    fun saturday(lang: String) = when (lang) {
        "zh" -> "六"
        else -> "Sat"
    }
    
    fun sunday(lang: String) = when (lang) {
        "zh" -> "日"
        else -> "Sun"
    }
    
    fun weekdays(lang: String) = listOf(
        monday(lang),
        tuesday(lang),
        wednesday(lang),
        thursday(lang),
        friday(lang),
        saturday(lang),
        sunday(lang)
    )
}
