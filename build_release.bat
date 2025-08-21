@echo off
setlocal EnableDelayedExpansion

title Weather Station - Release Builder

echo.
echo ================================================
echo        Weather Station Release Builder
echo ================================================
echo.

if not exist "gradlew.bat" (
    echo ERROR: Run from project root directory
    pause
    exit /b 1
)

echo Select release type:
echo [1] Debug APK (for testing)
echo [2] Release APK (for distribution)  
echo [3] App Bundle AAB (for Play Store)
echo [4] Complete Package (everything)
echo.
set /p choice="Enter choice (1-4): "

if "%choice%"=="1" goto debug_build
if "%choice%"=="2" goto release_build  
if "%choice%"=="3" goto bundle_build
if "%choice%"=="4" goto complete_build
goto debug_build

:debug_build
echo.
echo Building Debug APK...
call gradlew.bat clean assembleDebug
if %errorlevel% neq 0 goto build_error
set APK_FILE=app\build\outputs\apk\debug\app-debug.apk
goto apk_success

:release_build
echo.
echo Building Release APK...
call gradlew.bat clean assembleRelease
if %errorlevel% neq 0 goto build_error
set APK_FILE=app\build\outputs\apk\release\app-release-unsigned.apk
goto apk_success

:bundle_build
echo.
echo Building App Bundle...
call gradlew.bat clean bundleRelease
if %errorlevel% neq 0 goto build_error
set AAB_FILE=app\build\outputs\bundle\release\app-release.aab
goto bundle_success

:complete_build
echo.
echo Building Complete Package...
call gradlew.bat clean assembleRelease bundleRelease
if %errorlevel% neq 0 goto build_error

set PACKAGE_DIR=dist\WeatherStation_Package
if exist "%PACKAGE_DIR%" rmdir /s /q "%PACKAGE_DIR%"
mkdir "%PACKAGE_DIR%"
mkdir "%PACKAGE_DIR%\Android"
mkdir "%PACKAGE_DIR%\ESP32"

copy "app\build\outputs\apk\release\app-release-unsigned.apk" "%PACKAGE_DIR%\Android\WeatherStation.apk" >nul
copy "app\build\outputs\bundle\release\app-release.aab" "%PACKAGE_DIR%\Android\WeatherStation.aab" >nul
if exist "ESP32_Weather_Server.ino" copy "ESP32_Weather_Server.ino" "%PACKAGE_DIR%\ESP32\" >nul
copy "quick_install.bat" "%PACKAGE_DIR%\Android\" >nul

echo @echo off > "%PACKAGE_DIR%\INSTALL.bat"
echo adb install -r Android\WeatherStation.apk >> "%PACKAGE_DIR%\INSTALL.bat"
echo echo Weather Station installed! >> "%PACKAGE_DIR%\INSTALL.bat"
echo pause >> "%PACKAGE_DIR%\INSTALL.bat"

goto package_success

:apk_success
if not exist "%APK_FILE%" (
    echo ERROR: APK not found
    goto build_error
)
echo.
echo ================================================
echo           APK Created Successfully!
echo ================================================
echo.
echo File: %APK_FILE%
for %%A in ("%APK_FILE%") do set SIZE=%%~zA
set /a SIZE_MB=!SIZE!/1024/1024
echo Size: !SIZE_MB! MB
echo.
set /p install="Install now? (y/N): "
if /i "!install!"=="y" (
    adb install -r "%APK_FILE%"
    if %errorlevel% neq 0 (
        echo Installation failed
    ) else (
        echo Installation successful!
    )
)
goto finish

:bundle_success
if not exist "%AAB_FILE%" (
    echo ERROR: AAB not found
    goto build_error
)
echo.
echo ================================================
echo         App Bundle Created Successfully!
echo ================================================
echo.
echo File: %AAB_FILE%
for %%A in ("%AAB_FILE%") do set SIZE=%%~zA
set /a SIZE_MB=!SIZE!/1024/1024
echo Size: !SIZE_MB! MB
echo.
echo Ready for Google Play Store upload!
goto finish

:package_success
echo.
echo ================================================
echo      Complete Package Created Successfully!
echo ================================================
echo.
echo Package: %PACKAGE_DIR%\
echo.
echo Contents:
echo   Android\WeatherStation.apk
echo   Android\WeatherStation.aab
echo   ESP32\ESP32_Weather_Server.ino
echo   INSTALL.bat
echo.
echo Distribution package ready!
goto finish

:build_error
echo.
echo ================================================
echo               Build Failed
echo ================================================
echo.
echo Check the error messages above
pause
exit /b 1

:finish
echo.
echo Release build completed!
pause
exit /b 0
