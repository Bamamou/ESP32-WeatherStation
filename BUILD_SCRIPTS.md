# Weather Station Build & Installation Scripts

## 📋 Available Scripts Overview

| Script | Purpose | Target Users | Features |
|--------|---------|--------------|----------|
| `quick_install.bat` | One-click installation | End users | Simple, fast, automatic |
| `install.bat` | Standard installation | Regular users | Step-by-step, guided |
| `install_advanced.bat` | Full-featured installer | Power users | Multiple options, troubleshooting |
| `setup_dev.bat` | Development environment | Developers | Dev tools, shortcuts, debugging |
| `release.bat` | Production builds | Distributors | APK/AAB creation, packaging |
| `verify_build.bat` | Build testing | QA/Testing | Comprehensive verification |
| `Install-WeatherStation.ps1` | PowerShell installer | PowerShell users | Modern PS script |

## 🚀 Quick Start Guide

### For Most Users:
```bash
# 1. Connect Android device
# 2. Enable USB debugging  
# 3. Double-click:
quick_install.bat
```

### For Developers:
```bash
# Set up development environment:
setup_dev.bat

# Then use these shortcuts:
dev_build.bat      # Quick build only
dev_install.bat    # Quick install only  
dev_logs.bat       # View app logs
```

### For Distribution:
```bash
# Create release builds:
release.bat

# Verify everything works:
verify_build.bat
```

## 🔧 Script Details

### `quick_install.bat`
**Best for: First-time users**
- ✅ Automatic requirement checking
- ✅ One-click build and install
- ✅ Error handling with helpful messages
- ✅ Auto-launch option
- ⏱️ ~2-3 minutes

### `install.bat`
**Best for: Regular installations**
- ✅ Step-by-step process
- ✅ Device verification
- ✅ ESP32 setup guidance
- ✅ Manual launch option
- ⏱️ ~3-4 minutes

### `install_advanced.bat`
**Best for: Power users and troubleshooting**
- ✅ Multiple installation modes
- ✅ System requirements checker
- ✅ Uninstall option
- ✅ Development mode with logging
- ✅ Comprehensive error handling
- ⏱️ ~2-5 minutes (depending on mode)

### `setup_dev.bat`
**Best for: Developers and contributors**
- ✅ Complete development environment setup
- ✅ Creates dev shortcuts (dev_build.bat, dev_install.bat, dev_logs.bat)
- ✅ Dependency management
- ✅ Test execution
- ✅ Development tools verification
- ⏱️ ~5-10 minutes (first run)

### `release.bat`
**Best for: Creating distribution packages**
- ✅ Debug/Release/AAB builds
- ✅ Complete distribution packages
- ✅ Checksum generation
- ✅ Documentation bundling
- ✅ Play Store ready bundles
- ⏱️ ~3-8 minutes (depending on build type)

### `verify_build.bat`
**Best for: Quality assurance**
- ✅ Tests all build configurations
- ✅ Verifies installation scripts
- ✅ Runs unit tests
- ✅ Code quality checks (lint)
- ✅ Comprehensive reporting
- ⏱️ ~5-10 minutes

### `Install-WeatherStation.ps1`
**Best for: PowerShell users**
- ✅ Modern PowerShell script
- ✅ Parameter support: `-InstallType`, `-LaunchApp`
- ✅ Colorized output
- ✅ Professional error handling
- ⏱️ ~2-3 minutes

## 🎯 Usage Examples

### Basic Installation:
```bash
# Method 1: Simplest
quick_install.bat

# Method 2: With options
install_advanced.bat
# Choose option 1 (Quick Install)
```

### Development Setup:
```bash
# Full setup
setup_dev.bat

# Then for daily development:
dev_build.bat      # Build only
dev_install.bat    # Install only
dev_logs.bat       # Monitor logs
```

### Production Release:
```bash
# Create release APK
release.bat
# Choose option 2 (Signed Release)

# Verify everything
verify_build.bat

# Create distribution package
release.bat  
# Choose option 4 (Complete Distribution Package)
```

### PowerShell Users:
```powershell
# Quick install
.\Install-WeatherStation.ps1

# Development install with auto-launch
.\Install-WeatherStation.ps1 -InstallType dev -LaunchApp

# Release build
.\Install-WeatherStation.ps1 -InstallType release
```

## 🛠️ Prerequisites

### Required for All Scripts:
- Windows PC
- Android device with USB debugging
- USB cable

### Auto-Downloaded/Checked:
- Android SDK Platform-Tools (ADB)
- Java 11+
- Gradle dependencies
- Android build tools

## 📱 Build Outputs

### Debug Builds:
- Location: `app\build\outputs\apk\debug\app-debug.apk`
- Purpose: Testing and development
- Size: ~15-25 MB
- Features: Full logging, debugging symbols

### Release Builds:
- Location: `app\build\outputs\apk\release\app-release-unsigned.apk`
- Purpose: Distribution
- Size: ~8-15 MB (optimized)
- Features: R8 optimized, no debug info

### App Bundles:
- Location: `app\build\outputs\bundle\release\app-release.aab`
- Purpose: Google Play Store
- Size: ~6-12 MB (dynamic delivery)
- Features: Split APKs, optimized delivery

## 🏆 Recommended Workflow

### For End Users:
1. `quick_install.bat` → Done! 🎉

### For Developers:
1. `setup_dev.bat` → Set up environment
2. `dev_build.bat` → Daily development builds
3. `verify_build.bat` → Pre-release testing
4. `release.bat` → Production builds

### For Distribution:
1. `verify_build.bat` → Ensure quality
2. `release.bat` (option 4) → Create package
3. Share `dist\WeatherStation_vX.X_YYYYMMDD\` folder

---

*Choose the script that best fits your needs and experience level!* 🌤️
