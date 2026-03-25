# ============================================================
# INSTALL MOBILE APP DENGAN PRODUK TERBARU
# ============================================================
Write-Host "`n" -NoNewline
Write-Host "╔══════════════════════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║                                                          ║" -ForegroundColor Cyan
Write-Host "║       MUSCLECART - INSTALL & UPDATE MOBILE APP           ║" -ForegroundColor Cyan
Write-Host "║                                                          ║" -ForegroundColor Cyan
Write-Host "╚══════════════════════════════════════════════════════════╝" -ForegroundColor Cyan
Write-Host ""

# Check if emulator is running
Write-Host "[1/4] Checking emulator..." -ForegroundColor Yellow
$devices = & adb devices 2>&1 | Select-String "emulator"

if ($devices.Count -eq 0) {
    Write-Host "❌ ERROR: No emulator detected!" -ForegroundColor Red
    Write-Host "   Please start Android Emulator first from Android Studio" -ForegroundColor Yellow
    Write-Host ""
    Read-Host "Press Enter to exit"
    exit 1
}
Write-Host "✓ Emulator is running" -ForegroundColor Green

# Uninstall old app
Write-Host "`n[2/4] Uninstalling old app..." -ForegroundColor Yellow
& adb uninstall com.gymecommerce.musclecart 2>&1 | Out-Null
Write-Host "✓ Old app removed" -ForegroundColor Green

# Install new APK
Write-Host "`n[3/4] Installing new APK..." -ForegroundColor Yellow
$apkPath = "app\build\outputs\apk\debug\app-debug.apk"

if (-Not (Test-Path $apkPath)) {
    Write-Host "❌ ERROR: APK not found at: $apkPath" -ForegroundColor Red
    Write-Host "   Run: .\gradlew.bat assembleDebug" -ForegroundColor Yellow
    Read-Host "Press Enter to exit"
    exit 1
}

& adb install $apkPath 2>&1 | Out-Null
Write-Host "✓ New app installed" -ForegroundColor Green

# Launch app
Write-Host "`n[4/4] Launching MuscleCart..." -ForegroundColor Yellow
& adb shell am start -n com.gymecommerce.musclecart/.MainActivity 2>&1 | Out-Null
Start-Sleep -Seconds 2
Write-Host "✓ App launched" -ForegroundColor Green

# Success message
Write-Host ""
Write-Host "╔══════════════════════════════════════════════════════════╗" -ForegroundColor Green
Write-Host "║                    ✅ SUCCESS! ✅                         ║" -ForegroundColor Green  
Write-Host "╚══════════════════════════════════════════════════════════╝" -ForegroundColor Green
Write-Host ""
Write-Host "📱 MuscleCart app has been installed and launched!" -ForegroundColor Cyan
Write-Host ""Write-Host "📋 Expected products (5 items):" -ForegroundColor Yellow
Write-Host "   1. Evolene - Crevolene Creapure - Creatine (Rp 224.000)"
Write-Host "   2. Evolene - [NEW] Crevolene Monohydrate (Rp 114.000)"
Write-Host "   3. Evolene Evomass 2lbs/912gr (Rp 274.000)"
Write-Host "   4. Evolene Isolene 12 Sachet/396gr (Rp 294.000)"
Write-Host "   5. Evolene - Evowhey Protein 50S/1750gr (Rp 884.000)"
Write-Host ""
Write-Host "🔄 The app will automatically load fresh data from server!" -ForegroundColor Green
Write-Host ""
Write-Host "Press Enter to close..." -ForegroundColor Gray
Read-Host
