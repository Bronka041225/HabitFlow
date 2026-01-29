# HabitFlow 故障排查与修复指南 (Troubleshooting & Fixes)

本文档整合了项目开发过程中的故障排查记录、崩溃修复指南以及紧急调试步骤。

---

## 🚨 闪退问题快速修复 (Quick Fix)

**问题描述**：应用在手机上打开立即闪退。
**主要原因**：Release 版本 ProGuard 代码混淆配置不完整。

### 自动化修复（推荐）
在项目根目录运行以下脚本：
```powershell
.\rebuild-release.bat
```
该脚本会自动清理、重新编译 Release 版本并尝试安装到连接的设备。

### 手动修复
```powershell
.\gradlew.bat clean assembleRelease
adb uninstall com.example.habitflow
adb install app\build\outputs\apk\release\app-release.apk
```

---

## 🛠️ 详细修复指南 (Crash Fix Guide)

### 1. 核心问题诊断
经过代码分析，发现导致闪退的根源是 **ProGuard 混淆配置不完整**：
- **Gson**: 序列化时反射失败（数据导出功能）。
- **Kotlin Coroutines**: 异步任务调度器被混淆。
- **Jetpack Compose**: UI 组件反射名称不匹配。
- **Hilt**: 依赖注入类丢失。

### 2. 已实施的修复
在 `app/proguard-rules.pro` 中添加了以下保护规则：
```proguard
#Gson
-keep class com.google.gson.** { *; }
-keepclassmembers class com.example.habitflow.data.entity.** { <fields>; }

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keep class kotlinx.coroutines.** { *; }

# Compose & Vico
-keep class androidx.compose.** { *; }
-keep class com.patrykandpatrick.vico.** { *; }

# Hilt
-keep class dagger.hilt.** { *; }
```

此外，**临时禁用了自动测试数据生成**（`HabitViewModel.kt` 中的 `init` 块），以防止数据库初始化导致的启动崩溃。

---

## 🐞 紧急调试指南 (Emergency Debugging)

如果应用仍然无法运行，请按照以下步骤操作：

### 步骤 1：获取崩溃日志
使用项目提供的脚本工具：
```powershell
.\view-crash-log.bat
```
或者手动获取：
```powershell

```

### 步骤 2：常见错误对照
| 关键字 | 可能原因 | 解决方案 |
|---|---|---|
| `ClassNotFoundException` | Hilt 配置错误 | 检查 Manifest 中的 Application 注册 |
| `SQLiteException` | 数据库版本冲突 | 清除应用数据 `adb shell pm clear com.example.habitflow` |
| `JsonSyntaxException` | Gson 混淆问题 | 确认 ProGuard 规则已生效 |

### 步骤 3：回退方案
如果 Release 版本持续有问题，请使用 Debug 版本开发和测试（Debug 版本默认不混淆）：
```powershell
.\gradlew.bat installDebug
```

---

## 📜 历史故障排查日志 (Historical Troubleshooting Log)

以下是项目开发过程中记录的历史问题与解决方案。

### 1. 环境与构建配置
- **SDK 路径丢失**: 创建 `local.properties` 指向 SDK。
- **内存不足**: 增加 Gradle JVM 内存至 2048m。
- **Android 15 适配**: 升级 `compileSdk` 为 35。

### 2. UI/UX 优化
- **屏幕旋转白屏**: 修改 `themes.xml` 背景色为 `#121212`。
- **热力图卡顿**: 将 `FlowRow` 重构为 `Canvas` 绘制，性能显著提升。
- **图表文字看不清**: 强制设置图表文字颜色为 `MaterialTheme.colorScheme.onSurface`。

### 3. 功能迭代
- **数据导出**: 使用 SAF (Storage Access Framework) 替代文件权限申请。
- **国际化**: 移除 XML 中的转义字符，修复资源合并错误。
- **Hilt 集成**: 修复 Application 类未添加 `@HiltAndroidApp` 注解的问题。

*(更多详细历史记录请查阅 Git 提交历史)*