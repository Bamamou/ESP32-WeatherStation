@echo off
setlocal EnableDelayedExpansion

:: Weather Station Development Environment Setup
:: Sets up everything needed for development

title Weather Station - Development Setup

echo.
echo ╔══════════════════════════════════════════════════╗
echo ║        Weather Station Development Setup          ║
echo ║                                                  ║
echo ║  This script will set up your development        ║
echo ║  environment for the Weather Station project     ║
echo ╚══════════════════════════════════════════════════╝
echo.

:: Check if running from correct directory
if not exist "gradlew.bat" (
    echo ERROR: Please run this script from the project root directory
    pause
    exit /b 1
)

echo [1/8] Checking Java installation...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Java not found
    echo Please install Java 11 or higher from:
    echo https://adoptium.net/
    pause
    exit /b 1
) else (
    echo ✓ Java installation found
    java -version 2>&1 | findstr /i "version"
)

echo.
echo [2/8] Checking Android SDK...
if defined ANDROID_HOME (
    echo ✓ ANDROID_HOME set to: %ANDROID_HOME%
) else (
    echo ⚠️  ANDROID_HOME not set
    echo You may need to set this environment variable
)

adb version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ ADB not found
    echo Please install Android SDK Platform-Tools
) else (
    echo ✓ ADB found
)

echo.
echo [3/8] Checking Gradle...
call gradlew.bat --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Gradle wrapper test failed
    pause
    exit /b 1
) else (
    echo ✓ Gradle wrapper working
)

echo.
echo [4/8] Downloading dependencies...
call gradlew.bat --refresh-dependencies >nul 2>&1
if %errorlevel% neq 0 (
    echo ⚠️  Dependency refresh had issues, continuing...
) else (
    echo ✓ Dependencies refreshed
)

echo.
echo [5/8] Building debug APK...
call gradlew.bat assembleDebug
if %errorlevel% neq 0 (
    echo ❌ Debug build failed
    pause
    exit /b 1
) else (
    echo ✓ Debug APK built successfully
)

echo.
echo [6/8] Running tests...
call gradlew.bat testDebugUnitTest >nul 2>&1
if %errorlevel% neq 0 (
    echo ⚠️  Some tests failed, check output for details
) else (
    echo ✓ All tests passed
)

echo.
echo [7/8] Checking ESP32 server code...
if exist "ESP32_Weather_Server.ino" (
    echo ✓ ESP32 server code found: ESP32_Weather_Server.ino
    echo   Upload this to your ESP32 device
) else (
    echo ⚠️  ESP32 server code not found
    echo   You'll need to create the ESP32 Arduino sketch
)

echo.
echo [8/8] Setting up development shortcuts...

:: Create development batch files
echo @echo off > dev_build.bat
echo echo Building debug APK... >> dev_build.bat
echo call gradlew.bat assembleDebug >> dev_build.bat
echo echo Build completed! >> dev_build.bat
echo pause >> dev_build.bat

echo @echo off > dev_install.bat
echo echo Installing debug APK... >> dev_install.bat
echo adb install -r app\build\outputs\apk\debug\app-debug.apk >> dev_install.bat
echo echo Installation completed! >> dev_install.bat
echo pause >> dev_install.bat

echo @echo off > dev_logs.bat
echo echo Starting ADB logcat for Weather Station... >> dev_logs.bat
echo echo Press Ctrl+C to stop >> dev_logs.bat
echo adb logcat ^| findstr "WeatherStation" >> dev_logs.bat

echo ✓ Development shortcuts created:
echo   - dev_build.bat (build only)
echo   - dev_install.bat (install only)  
echo   - dev_logs.bat (view app logs)

echo.
echo ╔══════════════════════════════════════════════════╗
echo ║              Setup Complete! 🎉                  ║
echo ║                                                  ║
echo ║  Your development environment is ready!          ║
echo ║                                                  ║
echo ║  Development files created:                      ║
echo ║  - dev_build.bat (quick build)                   ║
echo ║  - dev_install.bat (quick install)               ║
echo ║  - dev_logs.bat (view logs)                      ║
echo ║                                                  ║
echo ║  Project structure:                              ║
echo ║  - Android app in /app directory                 ║
echo ║  - ESP32 code: ESP32_Weather_Server.ino          ║
echo ║                                                  ║
echo ║  Next steps:                                     ║
echo ║  1. Upload ESP32 code to your ESP32              ║
echo ║  2. Install app: run dev_install.bat             ║
echo ║  3. Configure ESP32 IP in app settings           ║
echo ╚══════════════════════════════════════════════════╝
echo.

pause
exit /b 0
