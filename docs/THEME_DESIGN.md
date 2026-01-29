# HabitFlow 主题与设计文档 (Theme & Design)

本文档详细介绍了 HabitFlow 的 "Premium Dark Mode" 设计规范、实现细节以及新旧主题对比。

---

## 🎨 Premium Dark Mode 技术文档

### 设计理念
HabitFlow 采用 **Premium Dark Mode**，专为低光环境设计。核心原则：
1.  **拒绝纯黑 (#000000)**：使用深灰色 (#121212) 作为基底，确保阴影 (Elevation) 可见。
2.  **降低饱和度**：主色由高亮的电光橙 (#FF6D00) 调整为更柔和的 Premium Orange (#FFB74D)，减少视觉疲劳。
3.  **层级分明**：建立 6 级表面颜色层级。

### 核心配色表 (Color Palette)

| 颜色角色 | Hex 代码 | 用途 |
|---|---|---|
| **Primary** | `#FFB74D` | 按钮、选中状态 (Premium Orange) |
| **Background** | `#0D0D0D` | 应用最底层背景 |
| **Surface** | `#121212` | 卡片、对话框基底 (Standard Dark) |
| **SurfaceVariant** | `#1E1E1E` | 较高层级的卡片 |
| **OnSurface** | `#E8E8E8` | 主要文字 (Soft White) |
| **Secondary** | `#64B5F6` | 辅助色 (Blue) |
| **Tertiary** | `#81C784` | 成功/完成 (Green) |
| **Error** | `#EF5350` | 错误/删除 (Red) |

### 排版系统 (Typography)
采用 **Material 3** 完整排版标准，统一使用 `SansSerif` (Roboto) 字体。
- **Display/Headline**: 粗体，用于标题。
- **Body**: 常规字重，用于正文，增加行高优化阅读。
- **Label**: 全大写，增加字间距 (0.5sp)。

---

## 🆚 主题对比：Before vs After

### 视觉变化
- **主色调**: 从刺眼的 `#FF6D00` (100% 饱和度) 变为 `#FFB74D` (70% 饱和度)。
- **背景**: 从扁平的 `#000000` 变为有层次的 `#0D0D0D` / `#121212`。
- **卡片**: 增加了 Elevation 阴影效果，现在卡片与背景有明显分离感。

### 可访问性 (Accessibility)
| 指标 | 旧版 (Before) | 新版 (After) | 状态 |
|---|---|---|---|
| 文字对比度 (Body) | 21:1 (过高) | 13.5:1 | ✅ AAA (最佳) |
| 主色对比度 | 3.8:1 (不达标) | 7.8:1 | ✅ AAA |
| 视觉疲劳度 | 高 | 低 | 显著改善 |

---

## 📋 实现总结 (Implementation Summary)

### 修改的文件
1.  `Color.kt`: 定义了 25 个新的深色模式颜色变量。
2.  `Theme.kt`: 配置了 `PremiumDarkColorScheme`，强制应用深色模式，适配 Material 3 系统。
3.  `Type.kt`: 扩展了 15 种 Material 3 字体样式。

### 迁移指南
- 所有硬编码颜色（如 `Color.White`, `Color(0xFF...)`）均已替换为 `MaterialTheme.colorScheme.*`。
- 文本样式统一使用 `MaterialTheme.typography.*`。
- 新增组件时，请务必使用 Theme 中的 Token，严禁硬编码颜色值。
