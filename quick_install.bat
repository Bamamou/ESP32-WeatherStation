@echo off

:: One-Click Weather Station App Installer
:: Simple installation script for end users

title Installing Weather Station App...

echo.
echo ╔══════════════════════════════════════╗
echo ║     Weather Station App Installer    ║
echo ║              One-Click Install       ║
echo ╚══════════════════════════════════════╝
echo.

:: Quick system check
echo Checking requirements...
adb version >nul 2>&1
if %errorlevel% neq 0 (
    echo.
    echo ❌ Android tools not found!
    echo.
    echo Please install Android SDK Platform-Tools:
    echo https://developer.android.com/studio/releases/platform-tools
    echo.
    pause
    exit /b 1
)

echo ✓ Android tools found

:: Check for devices
adb devices | findstr /r "device$" >nul
if %errorlevel% neq 0 (
    echo.
    echo ⚠️  No Android device detected
    echo.
    echo Please:
    echo 1. Connect your Android device via USB
    echo 2. Enable USB debugging in Developer Options
    echo 3. Allow computer access on your device
    echo.
    pause
    exit /b 1
)

echo ✓ Android device connected

:: Build and install
echo.
echo Building app...
call gradlew.bat clean assembleDebug >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Build failed. Please check your setup.
    pause
    exit /b 1
)

echo ✓ App built successfully

echo.
echo Installing on device...
adb install -r "app\build\outputs\apk\debug\app-debug.apk" >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Installation failed
    echo Please enable "Install from unknown sources" on your device
    pause
    exit /b 1
)

echo ✓ Installation completed!

:: Launch app
echo.
echo 🚀 Launching Weather Station app...
adb shell am start -n com.example.weatherstation/.MainActivity >nul 2>&1

echo.
echo ╔══════════════════════════════════════╗
echo ║            Success! 🎉               ║
echo ║                                      ║
echo ║  Weather Station app is installed    ║
echo ║  and ready to use!                   ║
echo ║                                      ║
echo ║  Next: Configure your ESP32 IP       ║
echo ║  address in the app settings         ║
echo ╚══════════════════════════════════════╝
echo.

timeout /t 5 /nobreak >nul
exit /b 0
