# Weather Station App Installer (PowerShell)
# Professional installation script with error handling and logging

param(
    [Parameter(HelpMessage="Installation type: quick, dev, release")]
    [ValidateSet("quick", "dev", "release")]
    [string]$InstallType = "quick",
    
    [Parameter(HelpMessage="Skip device check")]
    [switch]$SkipDeviceCheck,
    
    [Parameter(HelpMessage="Launch app after installation")]
    [switch]$LaunchApp
)

# Colors for output
$Red = "Red"
$Green = "Green"
$Yellow = "Yellow"
$Cyan = "Cyan"

function Write-Status {
    param([string]$Message, [string]$Color = "White")
    Write-Host $Message -ForegroundColor $Color
}

function Write-Success {
    param([string]$Message)
    Write-Host "✓ $Message" -ForegroundColor Green
}

function Write-Error {
    param([string]$Message)
    Write-Host "❌ $Message" -ForegroundColor Red
}

function Write-Warning {
    param([string]$Message)
    Write-Host "⚠️  $Message" -ForegroundColor Yellow
}

# Header
Clear-Host
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   Weather Station App Installer (PS)" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Check prerequisites
Write-Status "[1/6] Checking prerequisites..."

# Check Java
try {
    $javaVersion = java -version 2>&1 | Select-Object -First 1
    Write-Success "Java found: $javaVersion"
} catch {
    Write-Error "Java not found. Please install Java 11+"
    exit 1
}

# Check ADB
try {
    $adbVersion = adb version 2>&1 | Select-Object -First 1
    Write-Success "ADB found: $adbVersion"
} catch {
    Write-Error "ADB not found. Please install Android SDK Platform-Tools"
    Write-Host "Download: https://developer.android.com/studio/releases/platform-tools" -ForegroundColor Yellow
    exit 1
}

# Check project structure
Write-Status "`n[2/6] Checking project structure..."
if (-not (Test-Path "gradlew.bat")) {
    Write-Error "gradlew.bat not found. Please run from project root directory."
    exit 1
}
Write-Success "Project structure verified"

# Check connected devices
if (-not $SkipDeviceCheck) {
    Write-Status "`n[3/6] Checking connected devices..."
    $devices = adb devices | Where-Object { $_ -match "device$" }
    if ($devices.Count -eq 0) {
        Write-Warning "No Android devices connected"
        Write-Host "Please connect device with USB debugging enabled" -ForegroundColor Yellow
        
        $continue = Read-Host "Continue anyway? (y/N)"
        if ($continue.ToLower() -ne "y") {
            Write-Host "Installation cancelled"
            exit 1
        }
    } else {
        Write-Success "Connected devices:"
        $devices | ForEach-Object { Write-Host "  $_" -ForegroundColor Green }
    }
} else {
    Write-Status "`n[3/6] Skipping device check (as requested)"
}

# Clean build
Write-Status "`n[4/6] Cleaning previous builds..."
try {
    & .\gradlew.bat clean | Out-Null
    Write-Success "Clean completed"
} catch {
    Write-Error "Clean failed: $($_.Exception.Message)"
    exit 1
}

# Build APK
Write-Status "`n[5/6] Building APK..."
$buildCommand = switch ($InstallType) {
    "release" { "assembleRelease" }
    default { "assembleDebug" }
}

try {
    Write-Host "Running: gradlew $buildCommand" -ForegroundColor Gray
    & .\gradlew.bat $buildCommand
    if ($LASTEXITCODE -ne 0) {
        throw "Build failed with exit code $LASTEXITCODE"
    }
    Write-Success "APK built successfully"
} catch {
    Write-Error "Build failed: $($_.Exception.Message)"
    exit 1
}

# Install APK
Write-Status "`n[6/6] Installing APK..."
$apkPath = if ($InstallType -eq "release") {
    "app\build\outputs\apk\release\app-release-unsigned.apk"
} else {
    "app\build\outputs\apk\debug\app-debug.apk"
}

if (-not (Test-Path $apkPath)) {
    Write-Error "APK not found at: $apkPath"
    exit 1
}

try {
    Write-Host "Installing: $apkPath" -ForegroundColor Gray
    adb install -r $apkPath
    if ($LASTEXITCODE -ne 0) {
        throw "Installation failed with exit code $LASTEXITCODE"
    }
    Write-Success "Installation completed"
} catch {
    Write-Error "Installation failed: $($_.Exception.Message)"
    Write-Host "`nTroubleshooting:" -ForegroundColor Yellow
    Write-Host "1. Enable USB debugging on device" -ForegroundColor Yellow
    Write-Host "2. Allow installation from unknown sources" -ForegroundColor Yellow
    Write-Host "3. Check device authorization" -ForegroundColor Yellow
    exit 1
}

# Success message
Write-Host ""
Write-Host "╔══════════════════════════════════════╗" -ForegroundColor Green
Write-Host "║         Installation Success! 🎉     ║" -ForegroundColor Green
Write-Host "║                                      ║" -ForegroundColor Green
Write-Host "║  Weather Station app installed       ║" -ForegroundColor Green
Write-Host "║  Build type: $($InstallType.ToUpper().PadRight(20)) ║" -ForegroundColor Green
Write-Host "║                                      ║" -ForegroundColor Green
Write-Host "║  Next steps:                         ║" -ForegroundColor Green
Write-Host "║  1. Configure ESP32 IP address       ║" -ForegroundColor Green
Write-Host "║  2. Upload ESP32_Weather_Server.ino  ║" -ForegroundColor Green
Write-Host "║  3. Connect ESP32 to WiFi            ║" -ForegroundColor Green
Write-Host "║  4. Start monitoring weather! 🌤️     ║" -ForegroundColor Green
Write-Host "╚══════════════════════════════════════╝" -ForegroundColor Green

# Launch app option
if ($LaunchApp -or (Read-Host "`nLaunch app now? (y/N)").ToLower() -eq "y") {
    Write-Status "`nLaunching Weather Station app..."
    try {
        adb shell am start -n com.example.weatherstation/.MainActivity | Out-Null
        Write-Success "App launched successfully"
    } catch {
        Write-Warning "Could not auto-launch app. Please open manually."
    }
}

Write-Host "`nInstallation completed successfully!" -ForegroundColor Cyan
