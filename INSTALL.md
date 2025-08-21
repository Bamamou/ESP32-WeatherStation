# Weather Station App - Installation Guide

## 🚀 Quick Start

### For End Users (Easiest):
```
Double-click: quick_install.bat
```
This will automatically build and install the app on your connected Android device.

### For Developers:
```
Double-click: setup_dev.bat
```
This sets up the complete development environment with debugging tools.

### For Advanced Users:
```
Double-click: install_advanced.bat
```
Choose from multiple installation options and configurations.

## 📋 Installation Files

| File | Purpose | Best For |
|------|---------|----------|
| `quick_install.bat` | One-click installation | End users |
| `install.bat` | Standard installation with options | Most users |
| `install_advanced.bat` | Full-featured installer with menu | Power users |
| `setup_dev.bat` | Development environment setup | Developers |
| `Install-WeatherStation.ps1` | PowerShell version | PowerShell users |

## 🔧 Prerequisites

### Required:
- Android device with USB debugging enabled
- USB cable to connect device to computer
- Windows PC

### Optional (will be downloaded automatically):
- Android SDK Platform-Tools
- Java 11+

## 📱 Installation Steps

### Method 1: One-Click Install
1. Connect your Android device via USB
2. Enable USB debugging in Developer Options
3. Double-click `quick_install.bat`
4. Follow the prompts
5. The app will be built and installed automatically

### Method 2: Manual Install
1. Open Command Prompt in project directory
2. Run: `gradlew clean assembleDebug`
3. Run: `adb install -r app\build\outputs\apk\debug\app-debug.apk`

### Method 3: Android Studio
1. Open project in Android Studio
2. Connect Android device
3. Click "Run" button or press Ctrl+R

## 🌐 ESP32 Setup

1. **Upload ESP32 Code**:
   - Open `ESP32_Weather_Server.ino` in Arduino IDE
   - Install required libraries (WiFi, DHT, BMP280)
   - Upload to your ESP32

2. **WiFi Configuration**:
   - Connect ESP32 to your WiFi network
   - Note the IP address shown in Serial Monitor

3. **App Configuration**:
   - Open Weather Station app on Android
   - Go to Settings
   - Enter ESP32 IP address
   - Test connection

## 🔍 Troubleshooting

### Common Issues:

**"ADB not found"**
- Install Android SDK Platform-Tools
- Add to system PATH
- Restart command prompt

**"No devices found"**
- Enable USB debugging on Android device
- Allow computer access when prompted
- Try different USB cable/port

**"Installation failed"**
- Enable "Install unknown apps" for the installer
- Check available storage space
- Restart ADB: `adb kill-server && adb start-server`

**"Build failed"**
- Check internet connection (for dependencies)
- Ensure Java 11+ is installed
- Try: `gradlew clean` then rebuild

### ESP32 Issues:

**"Connection failed"**
- Verify ESP32 and Android device on same WiFi
- Check ESP32 IP address
- Ensure ESP32 code is uploaded and running

**"No sensor data"**
- Check sensor wiring connections
- Verify sensor power supply (3.3V)
- Check ESP32 serial output for errors

## 🆘 Support

If you encounter issues:
1. Check the troubleshooting section above
2. Review the console output for error messages
3. Ensure all prerequisites are installed
4. Try the development installer for detailed logging

## 🎯 What's Next?

After successful installation:
1. **Configure ESP32**: Set up your weather station hardware
2. **Connect to WiFi**: Ensure both devices are networked
3. **Set IP Address**: Configure the ESP32 IP in app settings
4. **Monitor Weather**: Enjoy real-time weather monitoring!

---

*Weather Station App v1.0 - Professional weather monitoring solution* 🌤️
