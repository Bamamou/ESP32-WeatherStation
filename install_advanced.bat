@echo off
setlocal EnableDelayedExpansion

:: Advanced Weather Station App Installer
:: Includes development tools setup and multiple installation options

title Weather Station App - Advanced Installer

echo.
echo ========================================
echo   Weather Station App - Advanced Installer
echo ========================================
echo.
echo This installer will:
echo 1. Check system requirements
echo 2. Set up development environment (optional)
echo 3. Build and install the Weather Station app
echo 4. Configure ESP32 connection
echo.

:: Menu for installation type
echo Select installation type:
echo [1] Quick Install (APK only)
echo [2] Development Install (with debugging)
echo [3] Release Install (optimized APK)
echo [4] Check Requirements Only
echo [5] Uninstall App
echo.
set /p choice="Enter choice (1-5): "

if "%choice%"=="1" goto quick_install
if "%choice%"=="2" goto dev_install
if "%choice%"=="3" goto release_install
if "%choice%"=="4" goto check_requirements
if "%choice%"=="5" goto uninstall
echo Invalid choice. Exiting.
pause
exit /b 1

:check_requirements
echo.
echo ========================================
echo    System Requirements Check
echo ========================================
echo.

:: Check Java
echo Checking Java installation...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Java not found
    echo Please install Java 11 or higher
) else (
    echo ✓ Java found
    java -version 2>&1 | findstr /i "version"
)

:: Check Android SDK
echo.
echo Checking Android SDK...
adb version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Android SDK Platform-Tools not found
    echo Download from: https://developer.android.com/studio/releases/platform-tools
) else (
    echo ✓ Android SDK Platform-Tools found
    adb version | findstr /i "version"
)

:: Check connected devices
echo.
echo Checking connected devices...
adb devices | findstr /r "device$" >nul
if %errorlevel% neq 0 (
    echo ⚠️  No Android devices connected
    echo Connect device with USB debugging enabled
) else (
    echo ✓ Android device(s) connected:
    adb devices | findstr /r "device$"
)

echo.
echo Requirements check completed.
pause
exit /b 0

:uninstall
echo.
echo ========================================
echo       Uninstalling Weather Station
echo ========================================
echo.
adb uninstall com.example.weatherstation
if %errorlevel% neq 0 (
    echo App was not installed or uninstall failed
) else (
    echo ✓ Weather Station app uninstalled successfully
)
pause
exit /b 0

:quick_install
echo.
echo ========================================
echo        Quick Install Mode
echo ========================================
goto build_and_install

:dev_install
echo.
echo ========================================
echo      Development Install Mode
echo ========================================
echo.
echo Development features:
echo - Debug APK with logging enabled
echo - ADB logging setup
echo - Development shortcuts
echo.
goto build_and_install

:release_install
echo.
echo ========================================
echo       Release Install Mode
echo ========================================
echo.
echo Building optimized release APK...
goto build_release

:build_and_install
:: Standard debug build process
echo.
echo [Step 1] Checking project structure...
if not exist "gradlew.bat" (
    echo ERROR: Not in project root directory
    pause
    exit /b 1
)

echo [Step 2] Cleaning previous builds...
call gradlew.bat clean
if %errorlevel% neq 0 goto build_error

echo [Step 3] Building debug APK...
call gradlew.bat assembleDebug
if %errorlevel% neq 0 goto build_error

echo [Step 4] Installing APK...
set APK_PATH=app\build\outputs\apk\debug\app-debug.apk
goto install_apk

:build_release
echo [Step 1] Cleaning previous builds...
call gradlew.bat clean
if %errorlevel% neq 0 goto build_error

echo [Step 2] Building release APK...
call gradlew.bat assembleRelease
if %errorlevel% neq 0 goto build_error

echo [Step 3] Installing APK...
set APK_PATH=app\build\outputs\apk\release\app-release-unsigned.apk
goto install_apk

:install_apk
if not exist "%APK_PATH%" (
    echo ERROR: APK not found at %APK_PATH%
    pause
    exit /b 1
)

echo Installing: %APK_PATH%
adb install -r "%APK_PATH%"
if %errorlevel% neq 0 (
    echo ERROR: Installation failed
    echo.
    echo Troubleshooting:
    echo 1. Enable USB debugging on your device
    echo 2. Allow installation from unknown sources
    echo 3. Check device authorization in developer options
    pause
    exit /b 1
)

goto success

:build_error
echo.
echo ========================================
echo           Build Failed
echo ========================================
echo.
echo Common solutions:
echo 1. Check internet connection for dependency downloads
echo 2. Ensure Android SDK is properly configured
echo 3. Check for syntax errors in the code
echo 4. Try running: gradlew clean
echo.
pause
exit /b 1

:success
echo.
echo ========================================
echo     Installation Successful! 🎉
echo ========================================
echo.
echo The Weather Station app is now installed!
echo.

:: Show installed app info
echo App Information:
adb shell dumpsys package com.example.weatherstation | findstr "versionName\|versionCode\|targetSdk" 2>nul

echo.
echo ========================================
echo        Setup Instructions
echo ========================================
echo.
echo 1. ESP32 Setup:
echo    - Upload ESP32_Weather_Server.ino to your ESP32
echo    - Connect ESP32 to your WiFi network
echo    - Note the ESP32's IP address
echo.
echo 2. App Configuration:
echo    - Open the Weather Station app on your device
echo    - Go to Settings and enter your ESP32 IP address
echo    - Test the connection
echo.
echo 3. Usage:
echo    - The app will automatically fetch weather data
echo    - Swipe to refresh for latest readings
echo    - Monitor real-time weather conditions
echo.

:: Option to launch app
set /p launch="Launch the Weather Station app now? (y/N): "
if /i "!launch!" equ "y" (
    echo Launching app...
    adb shell am start -n com.example.weatherstation/.MainActivity
    if %errorlevel% neq 0 (
        echo Could not auto-launch. Please open the app manually.
    )
)

:: Development mode specific features
if "%choice%"=="2" (
    echo.
    echo ========================================
    echo      Development Mode Features
    echo ========================================
    echo.
    echo Starting ADB log monitoring...
    echo Press Ctrl+C to stop logging
    echo.
    adb logcat | findstr "WeatherStation\|ESP32\|Weather"
)

echo.
echo Installation completed successfully!
echo Thank you for using Weather Station! 🌤️
pause
