@echo off
setlocal EnableDelayedExpansion

:: Weather Station App Installer
:: This script builds and installs the Weather Station Android app
:: Requires: Android SDK, ADB, and connected Android device

echo.
echo ========================================
echo    Weather Station App Installer
echo ========================================
echo.

:: Check if ADB is available
echo [1/6] Checking ADB availability...
adb version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: ADB not found in PATH
    echo Please install Android SDK Platform-Tools and add to PATH
    echo Download from: https://developer.android.com/studio/releases/platform-tools
    pause
    exit /b 1
)
echo ✓ ADB found

:: Check if Gradle wrapper exists
echo.
echo [2/6] Checking Gradle wrapper...
if not exist "gradlew.bat" (
    echo ERROR: gradlew.bat not found
    echo Please run this script from the project root directory
    pause
    exit /b 1
)
echo ✓ Gradle wrapper found

:: Check for connected devices
echo.
echo [3/6] Checking for connected Android devices...
adb devices | findstr /r "device$" >nul
if %errorlevel% neq 0 (
    echo WARNING: No Android devices found
    echo Please connect an Android device with USB debugging enabled
    echo.
    echo Available devices:
    adb devices
    echo.
    set /p continue="Continue anyway? (y/N): "
    if /i "!continue!" neq "y" (
        echo Installation cancelled
        pause
        exit /b 1
    )
) else (
    echo ✓ Android device(s) connected:
    adb devices | findstr /r "device$"
)

:: Clean previous builds
echo.
echo [4/6] Cleaning previous builds...
call gradlew.bat clean
if %errorlevel% neq 0 (
    echo ERROR: Clean failed
    pause
    exit /b 1
)
echo ✓ Clean completed

:: Build the APK
echo.
echo [5/6] Building Weather Station APK...
call gradlew.bat assembleDebug
if %errorlevel% neq 0 (
    echo ERROR: Build failed
    echo Please check the error messages above
    pause
    exit /b 1
)
echo ✓ APK built successfully

:: Install the APK
echo.
echo [6/6] Installing Weather Station app...
set APK_PATH=app\build\outputs\apk\debug\app-debug.apk

if not exist "%APK_PATH%" (
    echo ERROR: APK file not found at %APK_PATH%
    echo Build may have failed silently
    pause
    exit /b 1
)

echo Installing APK: %APK_PATH%
adb install -r "%APK_PATH%"
if %errorlevel% neq 0 (
    echo ERROR: Installation failed
    echo Make sure USB debugging is enabled and device is authorized
    pause
    exit /b 1
)

echo.
echo ========================================
echo    Installation Completed Successfully!
echo ========================================
echo.
echo The Weather Station app has been installed on your device.
echo.
echo Next steps:
echo 1. Open the app on your Android device
echo 2. Configure your ESP32 IP address in settings
echo 3. Make sure your ESP32 is running the weather server code
echo 4. Enjoy monitoring your weather station!
echo.
echo ESP32 Server Code: ESP32_Weather_Server.ino
echo.

:: Option to launch the app
set /p launch="Launch the app now? (y/N): "
if /i "!launch!" equ "y" (
    echo Launching Weather Station app...
    adb shell am start -n com.example.weatherstation/.MainActivity
    if %errorlevel% neq 0 (
        echo Note: Could not auto-launch app, please open it manually
    ) else (
        echo ✓ App launched successfully
    )
)

echo.
echo Installation script completed.
pause
