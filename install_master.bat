@echo off
setlocal EnableDelayedExpansion

:: Weather Station App - Master Installer
:: Main entry point with all installation options

title Weather Station - Master Installer

:main_menu
cls
echo.
echo ╔══════════════════════════════════════════════════╗
echo ║              Weather Station App                 ║
echo ║                Master Installer                  ║
echo ║                                                  ║
echo ║         Choose your installation method          ║
echo ╚══════════════════════════════════════════════════╝
echo.
echo Installation Options:
echo.
echo [1] 🚀 Quick Install         - One-click installation (recommended)
echo [2] 🔧 Advanced Install      - Full options and troubleshooting
echo [3] 👨‍💻 Developer Setup       - Development environment
echo [4] 📦 Release Builder       - Create distribution APKs
echo [5] ✅ Verify Build          - Test all configurations
echo [6] 📚 View Documentation    - Installation guides
echo [7] ❌ Exit                  - Close installer
echo.
set /p choice="Select option (1-7): "

if "%choice%"=="1" goto quick_install
if "%choice%"=="2" goto advanced_install
if "%choice%"=="3" goto dev_setup
if "%choice%"=="4" goto release_builder
if "%choice%"=="5" goto verify_build
if "%choice%"=="6" goto documentation
if "%choice%"=="7" goto exit_installer
echo Invalid choice, please try again.
timeout /t 2 /nobreak >nul
goto main_menu

:quick_install
echo.
echo Starting Quick Install...
call quick_install.bat
pause
goto main_menu

:advanced_install
echo.
echo Starting Advanced Install...
call install_advanced.bat
pause
goto main_menu

:dev_setup
echo.
echo Starting Developer Setup...
call setup_dev.bat
pause
goto main_menu

:release_builder
echo.
echo Starting Release Builder...
call build_release.bat
pause
goto main_menu

:verify_build
echo.
echo Starting Build Verification...
call verify_build.bat
pause
goto main_menu

:documentation
cls
echo.
echo ╔══════════════════════════════════════════════════╗
echo ║                 Documentation                    ║
echo ╚══════════════════════════════════════════════════╝
echo.
echo Available Documentation:
echo.
if exist "README.md" echo ✓ README.md - Project overview and features
if exist "INSTALL.md" echo ✓ INSTALL.md - Detailed installation guide  
if exist "BUILD_SCRIPTS.md" echo ✓ BUILD_SCRIPTS.md - Script documentation
if exist "SETUP_GUIDE.md" echo ✓ SETUP_GUIDE.md - ESP32 setup instructions
echo.
echo Installation Scripts:
echo ✓ quick_install.bat - Fastest installation method
echo ✓ install_advanced.bat - Full-featured installer
echo ✓ setup_dev.bat - Development environment setup
echo ✓ build_release.bat - Production build creator
echo ✓ verify_build.bat - Quality assurance testing
echo ✓ Install-WeatherStation.ps1 - PowerShell version
echo.
echo ESP32 Files:
if exist "ESP32_Weather_Server.ino" (
    echo ✓ ESP32_Weather_Server.ino - Upload this to your ESP32
) else (
    echo ⚠️  ESP32_Weather_Server.ino - Not found, may need to be created
)
echo.
echo Quick Start:
echo 1. Connect Android device with USB debugging
echo 2. Upload ESP32 code to your ESP32 device  
echo 3. Run quick_install.bat for app installation
echo 4. Configure ESP32 IP address in app settings
echo.
pause
goto main_menu

:exit_installer
echo.
echo ╔══════════════════════════════════════╗
echo ║         Thank you for using          ║
echo ║        Weather Station App!          ║
echo ║                                      ║
echo ║      Have a great day! 🌤️            ║
echo ╚══════════════════════════════════════╝
echo.
timeout /t 3 /nobreak >nul
exit /b 0
