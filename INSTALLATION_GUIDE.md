# HabitFlow 安装与测试指南

## 📋 修改内容总结

### 已完成的关键修复

#### 1. **ProGuard 混淆规则完善** (`app/proguard-rules.pro`)
从 22 行扩展到 75 行，添加了：
- ✅ Gson 序列化保护
- ✅ Kotlin 协程保护  
- ✅ Jetpack Compose 保护
- ✅ Hilt 依赖注入保护
- ✅ Room 数据库保护
- ✅ Vico 图表库保护

#### 2. **禁用自动测试数据生成** (`HabitViewModel.kt`)
临时注释了 `init` 块中的自动数据生成代码，避免初始化崩溃：
```kotlin
init {
    // 临时禁用自动测试数据生成，避免启动崩溃
    // viewModelScope.launch { ... }
}
```

#### 3. **主题配置修复** (`res/values/themes.xml`)
从 `Light.NoActionBar` 改为 `NoActionBar`（暗色主题）：
```xml
<style name="Theme.HabitFlow" parent="android:Theme.Material.NoActionBar">
    <item name="android:windowBackground">#121212</item>
    <item name="android:statusBarColor">#121212</item>
    <item name="android:navigationBarColor">#121212</item>
    <item name="android:windowLayoutInDisplayCutoutMode">shortEdges</item>
</style>
```

#### 4. **SDK 路径配置** (`local.properties`)
修正为 Windows 格式：
```properties
sdk.dir=C:/Users/26211/AppData/Local/Android/Sdk
```

---

## 🚀 安装步骤

### 方法 1：使用 ADB 安装（推荐）

#### 步骤 1：配置 ADB 路径
在 PowerShell 中运行：
```powershell
# 临时添加 ADB 到环境变量
$env:Path += ";C:\Users\26211\AppData\Local\Android\Sdk\platform-tools"

# 验证 ADB 可用
adb version
```

#### 步骤 2：编译 APK
```powershell
cd D:\GitHub\HabitFlow
.\gradlew.bat clean assembleDebug
```

#### 步骤 3：安装到手机
```powershell
# 检查设备连接
adb devices

# 卸载旧版本
adb uninstall com.example.habitflow

# 安装新版本
adb install app\build\outputs\apk\debug\app-debug.apk
```

---

### 方法 2：手动安装（无需 ADB）

#### 步骤 1：编译 APK
```powershell
cd D:\GitHub\HabitFlow
.\gradlew.bat clean assembleDebug
```

#### 步骤 2：传输 APK 到手机
APK 文件位置：
```
D:\GitHub\HabitFlow\app\build\outputs\apk\debug\app-debug.apk
```

传输方式（任选一种）：
- 📱 通过微信/QQ 发送文件到手机
- 💾 USB 数据线复制到手机存储
- ☁️ 上传到云盘后在手机下载

#### 步骤 3：在手机上安装
1. 找到 `app-debug.apk` 文件
2. 点击安装
3. 如果提示"禁止安装未知应用"，需要在设置中允许

---

## 🧪 测试步骤

### 1. 启动测试
- ✅ 应用能否正常启动（不闪退）
- ✅ 首页是否显示空白界面（已禁用自动数据）

### 2. 手动创建习惯
- ✅ 点击"添加习惯"按钮
- ✅ 输入习惯名称和目标
- ✅ 选择颜色
- ✅ 点击"创建"

### 3. 功能测试
- ✅ 打卡功能（点击 + 号）
- ✅ 查看习惯详情
- ✅ 查看图表
- ✅ 删除习惯（长按）

---

## 🔍 如果还是闪退

### 步骤 1：使用完整路径获取日志

```powershell
cd D:\GitHub\HabitFlow
$adb = "C:\Users\26211\AppData\Local\Android\Sdk\platform-tools\adb.exe"

# 清空日志
& $adb logcat -c

# 在手机上打开应用让它闪退

# 获取日志
& $adb logcat -d > crash_full.log

# 查看关键错误
Get-Content crash_full.log | Select-String -Pattern "FATAL|AndroidRuntime|Exception" -Context 2, 5
```

### 步骤 2：分析日志

打开 `crash_full.log` 文件，查找：
- `FATAL EXCEPTION` - 致命错误
- `Caused by:` - 错误原因
- `at com.example.habitflow` - 应用代码中的错误位置

### 步骤 3：常见错误对照

| 错误信息 | 可能原因 | 解决方案 |
|---------|---------|---------|
| `ClassNotFoundException: HiltAndroidApp` | Hilt 未正确配置 | 检查 `HabitFlowApplication` 在 Manifest 中是否注册 |
| `SQLiteException` | 数据库初始化失败 | 清除应用数据：`adb shell pm clear com.example.habitflow` |
| `NullPointerException in ViewModel` | 依赖注入失败 | 确保使用了最新编译的 APK |
| `InflateException` | 主题或布局错误 | 已修复，确保使用最新代码 |

---

## 📁 生成的辅助文件

项目根目录下已创建以下工具文件：

### 编译脚本
- `rebuild-release.bat` - 编译 Release 版本
- `quick-fix-rebuild.bat` - 快速编译 Debug 版本

### 诊断工具
- `get-crash-log.bat` - 获取崩溃日志（CMD）
- `get-crash-log.ps1` - 获取崩溃日志（PowerShell）
- `diagnose-crash.bat` - 完整诊断工具
- `view-crash-log.bat` - 实时查看日志

### 文档
- `CRASH_FIX_GUIDE.md` - 详细修复指南
- `CRASH_FIX_SUMMARY.md` - 技术分析总结
- `EMERGENCY_DEBUG.md` - 紧急调试指南
- `README_CRASH_FIX.txt` - 快速参考

---

## 💡 重要提示

### 当前配置状态
- ✅ **自动测试数据已禁用** - 首次打开应用界面为空
- ✅ **ProGuard 规则已完善** - Release 版本可用
- ✅ **主题配置已修复** - 暗色主题
- ✅ **错误日志已增强** - 包含详细堆栈信息

### 恢复自动测试数据
如果应用运行正常，想恢复自动生成测试数据，编辑 `HabitViewModel.kt`：
```kotlin
init {
    viewModelScope.launch {
        try {
            val currentHabits = repository.allHabits.first()
            if (currentHabits.isEmpty()) {
                generateTestData()  // 取消注释
            }
        } catch (e: Exception) {
            android.util.Log.e("HabitViewModel", "Init failed", e)
            e.printStackTrace()
        }
    }
}
```

---

## 🛠️ PowerShell ADB 快捷命令

将以下内容保存为 `adb-setup.ps1`，每次使用前运行：

```powershell
# ADB 环境配置脚本
$env:Path += ";C:\Users\26211\AppData\Local\Android\Sdk\platform-tools"

# 定义常用命令别名
function Install-HabitFlow {
    adb uninstall com.example.habitflow
    adb install app\build\outputs\apk\debug\app-debug.apk
}

function Get-CrashLog {
    adb logcat -d > crash.log
    Get-Content crash.log | Select-String -Pattern "FATAL|Exception" | Select-Object -Last 20
}

function Clear-AppData {
    adb shell pm clear com.example.habitflow
}

Write-Host "ADB 已配置！可用命令：" -ForegroundColor Green
Write-Host "  Install-HabitFlow  - 安装应用" -ForegroundColor Cyan
Write-Host "  Get-CrashLog       - 获取崩溃日志" -ForegroundColor Cyan
Write-Host "  Clear-AppData      - 清除应用数据" -ForegroundColor Cyan
```

使用方式：
```powershell
. .\adb-setup.ps1
Install-HabitFlow
```

---

## 📞 技术支持

如需进一步帮助，请提供：
1. `crash_full.log` 完整日志
2. 手机型号和 Android 版本
3. 具体操作步骤（何时闪退）
4. 使用的 APK 版本（Debug/Release）

---

**最后更新：** 2026-01-28  
**状态：** ✅ 已修复编译问题，等待安装测试反馈
