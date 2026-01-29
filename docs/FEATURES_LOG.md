# HabitFlow 功能更新日志 (Features & Updates)

本文档记录了项目近期的功能更新、模块实现细节及优化内容。

---

## 🚀 最新状态 (Status: Final Updates)

### 2026-01-28 更新概览
1.  **日历视图优化**: 修复了月度热力图的日期对齐问题，默认只显示当前月份，支持展开查看历史。
2.  **界面国际化**: 全面英文化 (English First)，统一了界面语言，包括日期、标题和统计信息。
3.  **UI 细节**: 修复了 Stats 卡片文字居中问题，优化了主页空状态提示。

---

## 🛠️ 功能模块详解

### 1. 语言切换 (Language Switch)
已搭建轻量级双语框架 (`LanguageManager` + `Strings.kt`)。
- **实现原理**: 使用 DataStore 存储语言偏好，通过 CompositionLocal 分发语言状态。
- **当前状态**: 框架已就绪，主页已实现基础切换逻辑，待全面接入各组件。
- **特性**: 支持中英切换，即时刷新，无需重启应用。

### 2. 底部弹窗 (Bottom Sheet)
升级为 Material 3 `ModalBottomSheet`。
- **AddHabitBottomSheet**:
    - 支持 7 种主题色选择。
    - 新增 12 种习惯图标选择器 (Star, Water, Fitness 等)。
    - 输入校验与动态按钮状态。
- **ExportBottomSheet**:
    - 提供 JSON 和 CSV 两种导出格式。
    - 采用卡片式布局，视觉效果更佳。

### 3. 图表改进 (Chart Improvements)
**趋势图 (Trend Chart)**:
- 新增 Y 轴数值标签（0-最大值）。
- 新增 X 轴日期标签（M/d 格式）。
- 绘制 30 天完整曲线，增加网格线辅助阅读。

**月度热力图 (Heatmap)**:
- 重构为日历视图，显示具体日期数字。
- 支持 "View All" / "Collapse" 折叠交互。
- 只有数据超过 1 个月时才显示展开按钮。

### 4. 图标与测试数据 (Icons & Test Data)
- **图标映射**: 创建了 `IconMapper` 工具类，将字符串名称映射为 Material Icons。
- **测试数据按钮**:
    - 主页右上角新增蓝色测试按钮。
    - 点击可一键生成 3 个典型习惯（俯卧撑、阅读、喝水）及其 90 天的历史数据。
    - 方便开发调试和新用户体验。

---

## 📁 主要文件变更

- `HabitHeatmap.kt`: 日历对齐与折叠逻辑。
- `HabitTrendChart.kt`: 坐标轴绘制。
- `HabitDetailScreen.kt`: 统计卡片布局优化。
- `IconMapper.kt`: 图标资源管理。
- `LanguageManager.kt`: 国际化状态管理。
