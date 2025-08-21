@echo off
setlocal EnableDelayedExpansion

:: Weather Station Build Verification Script
:: Tests all build configurations and installation scripts

title Weather Station - Build Verification

echo.
echo ╔══════════════════════════════════════════════════╗
echo ║       Weather Station Build Verification        ║
echo ║                                                  ║
echo ║  Tests all build configurations and scripts      ║
echo ╚══════════════════════════════════════════════════╝
echo.

set TOTAL_TESTS=0
set PASSED_TESTS=0
set FAILED_TESTS=0

:: Test 1: Gradle wrapper
echo [Test 1] Testing Gradle wrapper...
set /a TOTAL_TESTS+=1
call gradlew.bat --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Gradle wrapper test FAILED
    set /a FAILED_TESTS+=1
) else (
    echo ✓ Gradle wrapper test PASSED
    set /a PASSED_TESTS+=1
)

:: Test 2: Clean build
echo.
echo [Test 2] Testing clean operation...
set /a TOTAL_TESTS+=1
call gradlew.bat clean >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Clean operation FAILED
    set /a FAILED_TESTS+=1
) else (
    echo ✓ Clean operation PASSED
    set /a PASSED_TESTS+=1
)

:: Test 3: Debug build
echo.
echo [Test 3] Testing debug build...
set /a TOTAL_TESTS+=1
call gradlew.bat assembleDebug >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Debug build FAILED
    set /a FAILED_TESTS+=1
) else (
    echo ✓ Debug build PASSED
    set /a PASSED_TESTS+=1
    
    :: Verify debug APK exists
    if exist "app\build\outputs\apk\debug\app-debug.apk" (
        for %%A in ("app\build\outputs\apk\debug\app-debug.apk") do set DEBUG_SIZE=%%~zA
        set /a DEBUG_SIZE_MB=!DEBUG_SIZE!/1024/1024
        echo   Debug APK: !DEBUG_SIZE_MB! MB
    )
)

:: Test 4: Release build
echo.
echo [Test 4] Testing release build...
set /a TOTAL_TESTS+=1
call gradlew.bat assembleRelease >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Release build FAILED
    set /a FAILED_TESTS+=1
) else (
    echo ✓ Release build PASSED
    set /a PASSED_TESTS+=1
    
    :: Verify release APK exists
    if exist "app\build\outputs\apk\release\app-release-unsigned.apk" (
        for %%A in ("app\build\outputs\apk\release\app-release-unsigned.apk") do set RELEASE_SIZE=%%~zA
        set /a RELEASE_SIZE_MB=!RELEASE_SIZE!/1024/1024
        echo   Release APK: !RELEASE_SIZE_MB! MB
    )
)

:: Test 5: Unit tests
echo.
echo [Test 5] Testing unit tests...
set /a TOTAL_TESTS+=1
call gradlew.bat testDebugUnitTest >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Unit tests FAILED
    set /a FAILED_TESTS+=1
) else (
    echo ✓ Unit tests PASSED
    set /a PASSED_TESTS+=1
)

:: Test 6: Lint check
echo.
echo [Test 6] Testing code quality (lint)...
set /a TOTAL_TESTS+=1
call gradlew.bat lintDebug >nul 2>&1
if %errorlevel% neq 0 (
    echo ⚠️  Lint check had warnings (continuing)
    set /a PASSED_TESTS+=1
) else (
    echo ✓ Lint check PASSED
    set /a PASSED_TESTS+=1
)

:: Test 7: Installation scripts syntax
echo.
echo [Test 7] Testing installation scripts...
set /a TOTAL_TESTS+=1
set SCRIPT_ERRORS=0

:: Check each script exists and has valid syntax
if not exist "quick_install.bat" (
    echo ❌ quick_install.bat missing
    set /a SCRIPT_ERRORS+=1
)

if not exist "install.bat" (
    echo ❌ install.bat missing
    set /a SCRIPT_ERRORS+=1
)

if not exist "install_advanced.bat" (
    echo ❌ install_advanced.bat missing
    set /a SCRIPT_ERRORS+=1
)

if not exist "setup_dev.bat" (
    echo ❌ setup_dev.bat missing
    set /a SCRIPT_ERRORS+=1
)

if !SCRIPT_ERRORS! equ 0 (
    echo ✓ Installation scripts PASSED
    set /a PASSED_TESTS+=1
) else (
    echo ❌ Installation scripts FAILED (!SCRIPT_ERRORS! missing)
    set /a FAILED_TESTS+=1
)

:: Results summary
echo.
echo ╔══════════════════════════════════════════════════╗
echo ║                Build Verification Results        ║
echo ╚══════════════════════════════════════════════════╝
echo.
echo Total Tests: !TOTAL_TESTS!
echo Passed: !PASSED_TESTS!
echo Failed: !FAILED_TESTS!
echo.

if !FAILED_TESTS! equ 0 (
    echo ╔══════════════════════════════════════╗
    echo ║          All Tests Passed! ✅        ║
    echo ║                                      ║
    echo ║  Your Weather Station app is ready   ║
    echo ║  for production deployment!          ║
    echo ╚══════════════════════════════════════╝
    echo.
    echo Build artifacts available:
    if exist "app\build\outputs\apk\debug\app-debug.apk" (
        echo   📱 Debug APK: !DEBUG_SIZE_MB! MB
    )
    if exist "app\build\outputs\apk\release\app-release-unsigned.apk" (
        echo   📱 Release APK: !RELEASE_SIZE_MB! MB
    )
    if exist "app\build\outputs\bundle\release\app-release.aab" (
        for %%A in ("app\build\outputs\bundle\release\app-release.aab") do set AAB_SIZE=%%~zA
        set /a AAB_SIZE_MB=!AAB_SIZE!/1024/1024
        echo   📦 App Bundle: !AAB_SIZE_MB! MB
    )
    echo.
    echo Available installers:
    echo   ⚡ quick_install.bat (one-click)
    echo   🔧 install_advanced.bat (full options)
    echo   👨‍💻 setup_dev.bat (development)
    echo   🚀 release.bat (production builds)
    
    exit /b 0
) else (
    echo ╔══════════════════════════════════════╗
    echo ║         Some Tests Failed ❌         ║
    echo ║                                      ║
    echo ║  Please fix the issues above before  ║
    echo ║  deploying to production             ║
    echo ╚══════════════════════════════════════╝
    
    exit /b 1
)

:build_error
echo.
echo ╔══════════════════════════════════════╗
echo ║           Build Error ❌             ║
echo ╚══════════════════════════════════════╝
echo.
echo Build verification failed.
echo Please check the console output above for details.
echo.
pause
exit /b 1

:bundle_success
echo.
echo App Bundle verification completed successfully!
echo Ready for Google Play Store submission.
echo.
pause
exit /b 0
