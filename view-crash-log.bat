@echo off
REM 查看应用崩溃日志

echo ====================================
echo   HabitFlow 崩溃日志查看工具
echo ====================================
echo.

echo 检查设备连接...
adb devices
echo.

echo 清空旧日志...
adb logcat -c

echo.
echo ====================================
echo 请在手机上启动应用，如果闪退会自动捕获日志
echo 按 Ctrl+C 停止日志记录
echo ====================================
echo.

REM 过滤关键崩溃信息
adb logcat -v time *:E | findstr /i "habitflow AndroidRuntime FATAL Exception crash"

pause
