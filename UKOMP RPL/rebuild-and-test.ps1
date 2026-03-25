# Script untuk Rebuild dan Install Ulang MuscleCart Mobile App
# Jalankan dengan: .\rebuild-and-test.ps1

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  MuscleCart - Rebuild & Install Tool  " -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Step 1: Navigate to project
Write-Host "[1/7] Navigating to project folder..." -ForegroundColor Yellow
cd "c:\XAAMP\htdocs\UKOMP CODING RPL SMK\MuscleCart Mobile App"

# Step 2: Clean build
Write-Host "[2/7] Cleaning old build..." -ForegroundColor Yellow
.\gradlew.bat clean

# Step 3: Build APK
Write-Host "[3/7] Building APK (this may take 3-5 minutes)..." -ForegroundColor Yellow
.\gradlew.bat assembleDebug

if ($LASTEXITCODE -ne 0) {
    Write-Host "Build FAILED! Please check errors above." -ForegroundColor Red
    exit 1
}

Write-Host "[4/7] Build SUCCESS!" -ForegroundColor Green

# Step 4: Check if emulator is running
Write-Host "[5/7] Checking emulator..." -ForegroundColor Yellow
$devices = adb devices
if ($devices -match "emulator") {
    Write-Host "Emulator detected!" -ForegroundColor Green
} else {
    Write-Host "WARNING: No emulator detected. Please start emulator first!" -ForegroundColor Red
    Read-Host "Press Enter after starting emulator"
}

# Step 5: Uninstall old app
Write-Host "[6/7] Uninstalling old app..." -ForegroundColor Yellow
adb uninstall com.gymecommerce.musclecart 2>&1 | Out-Null
Write-Host "Old app removed (if existed)" -ForegroundColor Green

# Step 6: Install new APK
Write-Host "[7/7] Installing new APK..." -ForegroundColor Yellow
adb install -r "app\build\outputs\apk\debug\app-debug.apk"

if ($LASTEXITCODE -ne 0) {
    Write-Host "Installation FAILED!" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "  Installation COMPLETE!               " -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""
Write-Host "Next steps:" -ForegroundColor Cyan
Write-Host "1. Make sure Laravel server is running (php artisan serve)" -ForegroundColor White
Write-Host "2. Open MuscleCart app on emulator" -ForegroundColor White
Write-Host "3. Swipe down to refresh if data doesn't appear" -ForegroundColor White
Write-Host ""
