# HabitFlow 开发故障排查与问题记录 (Troubleshooting Log)

本文档记录了 `HabitFlow` 项目从零构建过程中遇到的关键技术问题、错误日志分析以及最终解决方案。

## 1. 环境与构建配置 (Environment & Build)

### 1.1 SDK 路径丢失
- **问题现象**: Gradle 构建失败，提示 `SDK location not found`。
- **原因**: 这是一个纯净的 Git 仓库，缺少本地环境配置文件。
- **解决方案**: 创建 `local.properties` 文件，并指向 Windows 默认 SDK 路径。
    ```properties
    sdk.dir=C:/Users/26211/AppData/Local/Android/Sdk
    ```
    *注意：Windows 路径必须使用正斜杠 `/` 或转义的反斜杠 `\`，否则会报 `Syntax Error`。*

### 1.2 内存不足 (GC Thrashing)
- **问题现象**: 编译时 Gradle 守护进程频繁挂起，报错 `JVM garbage collector is thrashing`。
- **原因**: 项目依赖（尤其是 Compose 编译器和 KSP）较重，默认 512MB 堆内存不足。
- **解决方案**: 在 `gradle.properties` 中增加 JVM 内存分配。
    ```properties
    org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
    ```

### 1.3 依赖版本冲突 (CompileSdk 34 vs 35)
- **问题现象**: `checkDebugAarMetadata` 失败，提示 `androidx.core:core-ktx:1.15.0` 需要 API Level 35。
- **原因**: 引入的最新版 AndroidX 库强制要求编译 SDK 为 Android 15 (API 35)。
- **解决方案**: 将 `app/build.gradle.kts` 中的 `compileSdk` 和 `targetSdk` 升级为 **35**。

### 1.4 缺少 AndroidManifest
- **问题现象**: `processDebugMainManifest` 失败，找不到文件。
- **原因**: 手动搭建项目结构时遗漏了核心清单文件。
- **解决方案**: 在 `src/main` 下创建标准的 `AndroidManifest.xml`，定义 Application 和 Launcher Activity。

## 2. 代码逻辑与 API (Logic & API)

### 2.1 ViewModel 编译错误 (Unit vs Long)
- **问题现象**: `Type mismatch: inferred type is Unit but Long was expected`。
- **原因**: 在 `HabitRepository` 中，`insertHabit` 函数没有显式声明返回类型，默认返回 `Unit`，但 DAO 层返回的是 `Long` (Row ID)。
- **解决方案**: 显式修改 Repository 函数签名：
    ```kotlin
    suspend fun insertHabit(habit: HabitEntity): Long { ... }
    ```

### 2.2 Vico 图表 API 误用
- **问题现象**: 编译报错 `Cannot find a parameter with this name: startAxis`。
- **原因**: Vico 1.x 版本中，坐标轴配置应位于 `Chart` Composable 中，而不是内部的 `lineChart` 工厂函数中。
- **解决方案**: 将 `startAxis` 和 `bottomAxis` 移动到 `Chart(...)` 的参数列表中。

### 2.3 导入路径错误
- **问题现象**: `Unresolved reference: textComponent`。
- **原因**: 尝试使用了错误的子包路径。
- **解决方案**: 修正导入为 `com.patrykandpatrick.vico.compose.component.textComponent`。

## 3. UI/UX 与性能优化 (UI & Performance)

### 3.1 屏幕旋转白屏闪烁 (White Flash)
- **问题现象**: 在页面切换或旋转屏幕时，会出现短暂的白色闪光。
- **原因**: App 虽然是深色 UI，但 `WindowBackground` 默认为白色。Compose 渲染第一帧前会露出底色。
- **解决方案**: 修改 `themes.xml`，强制将 `android:windowBackground` 设置为深色 (`#121212`)。

### 3.2 列表滚动卡顿 (Jank)
- **问题现象**: 热力图区域在左右滑动时明显掉帧。
- **原因**: 原始实现使用了 `FlowRow` 嵌套大量的 `Box` 组件。每个月约 30 个方块，12 个月就是 360+ 个 Composable 节点，布局计算量巨大。
- **解决方案**: **重构为 Canvas 绘制**。将 `MonthHeatmapCard` 改为使用单一 `Canvas` 组件，通过 `drawRoundRect` 批量绘制方块。GPU 绘制效率极高，彻底解决了卡顿。

### 3.3 图表坐标轴不可见
- **问题现象**: 深色模式下，图表的 X/Y 轴文字默认为黑色，无法看清。
- **解决方案**: 使用 `textComponent(color = MaterialTheme.colorScheme.onSurface)` 显式将标签颜色设为白色/浅灰。

### 3.4 图表标签显示问题
- **问题现象**: X 轴日期显示为 "..." 且 45度旋转导致布局混乱。
- **解决方案**:
    1. 简化日期格式，只显示“日”（如 14, 26）。
    2. 移除旋转属性 (`labelRotationDegrees`)，恢复水平显示。

## 4. 功能迭代 (Feature Iteration)

- **空数据处理**: App 初次启动一片空白。 -> **修复**: 在 ViewModel `init` 块中增加检测，若数据库为空自动注入测试数据。
- **交互限制**: 无法删除习惯。 -> **修复**: 增加长按 (Long Press) 删除功能。
- **打卡繁琐**: 每次只能加1。 -> **修复**: 增加弹窗，提供 +1/+5/+10 及自定义输入。
- **颜色单一**: 默认全为橙色。 -> **修复**: 在创建习惯时增加 7 色可选的色盘组件。
