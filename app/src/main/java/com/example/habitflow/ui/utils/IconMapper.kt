package com.example.habitflow.ui.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * 图标名称到 Material Icon 的映射
 */
object IconMapper {
    fun getIconForName(name: String): ImageVector {
        return when (name) {
            "Star" -> Icons.Default.Star
            "Water" -> Icons.Default.WaterDrop
            "Book" -> Icons.Default.MenuBook
            "Fitness" -> Icons.Default.FitnessCenter
            "Run" -> Icons.AutoMirrored.Filled.DirectionsRun
            "Meditation" -> Icons.Default.SelfImprovement
            "Music" -> Icons.Default.MusicNote
            "Code" -> Icons.Default.Code
            "Coffee" -> Icons.Default.LocalCafe
            "Bed" -> Icons.Default.Bedtime
            "Favorite" -> Icons.Default.Favorite
            "Timer" -> Icons.Default.Timer
            "Drink" -> Icons.Default.WaterDrop
            else -> Icons.Default.Star // 默认图标
        }
    }
}
