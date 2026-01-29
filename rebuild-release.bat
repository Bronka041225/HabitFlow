@echo off
REM HabitFlow 重新编译脚本（修复闪退问题）
REM 使用方法：在项目根目录双击运行此文件

echo ====================================
echo   HabitFlow 闪退修复 - 重新编译
echo ====================================
echo.

echo [1/4] 清理旧的构建产物...
call gradlew.bat clean
if %ERRORLEVEL% NEQ 0 (
    echo 错误：清理失败！
    pause
    exit /b 1
)

echo.
echo [2/4] 编译 Release 版本（已修复 ProGuard 配置）...
call gradlew.bat assembleRelease
if %ERRORLEVEL% NEQ 0 (
    echo 错误：编译失败！请检查错误信息。
    pause
    exit /b 1
)

echo.
echo [3/4] 检查 APK 文件...
if exist "app\build\outputs\apk\release\app-release.apk" (
    echo ✓ APK 编译成功！
    echo.
    echo 文件位置：app\build\outputs\apk\release\app-release.apk
    dir "app\build\outputs\apk\release\app-release.apk"
) else (
    echo ✗ 未找到 APK 文件！
    pause
    exit /b 1
)

echo.
echo [4/4] 是否安装到手机？（需要连接手机并开启 USB 调试）
echo.
set /p INSTALL="输入 Y 安装，N 跳过: "

if /i "%INSTALL%"=="Y" (
    echo.
    echo 卸载旧版本...
    adb uninstall com.example.habitflow 2>nul
    
    echo 安装新版本...
    adb install -r "app\build\outputs\apk\release\app-release.apk"
    
    if %ERRORLEVEL% EQU 0 (
        echo.
        echo ====================================
        echo   ✓ 安装成功！请测试应用
        echo ====================================
    ) else (
        echo.
        echo ✗ 安装失败！请检查：
        echo   1. 手机是否连接
        echo   2. USB 调试是否开启
        echo   3. 是否手动卸载旧版本
    )
) else (
    echo.
    echo 跳过安装。你可以手动安装 APK 文件。
)

echo.
echo ====================================
echo   操作完成
echo ====================================
echo.
pause
