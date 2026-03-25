# Script untuk Cek API Status
# Jalankan dengan: .\check-api.ps1

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  API Status Checker                   " -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Check 1: Laravel server from host
Write-Host "[1/3] Checking Laravel server at localhost:8000..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8000/api/v1/products" -Method GET -TimeoutSec 5
    $json = $response.Content | ConvertFrom-Json
    Write-Host "✓ Server is RUNNING!" -ForegroundColor Green
    Write-Host "  - Status: $($json.status)" -ForegroundColor White
    Write-Host "  - Products count: $($json.data.Count)" -ForegroundColor White
} catch {
    Write-Host "✗ Server is NOT RUNNING!" -ForegroundColor Red
    Write-Host "  Please start with: php artisan serve" -ForegroundColor Yellow
}

Write-Host ""

# Check 2: Categories endpoint
Write-Host "[2/3] Checking categories endpoint..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8000/api/v1/categories" -Method GET -TimeoutSec 5
    $json = $response.Content | ConvertFrom-Json
    Write-Host "✓ Categories endpoint OK!" -ForegroundColor Green
    Write-Host "  - Status: $($json.status)" -ForegroundColor White
    Write-Host "  - Categories count: $($json.data.Count)" -ForegroundColor White
} catch {
    Write-Host "✗ Categories endpoint FAILED!" -ForegroundColor Red
}

Write-Host ""

# Check 3: Emulator can access server
Write-Host "[3/3] Checking if emulator can access server..." -ForegroundColor Yellow
$emulatorCheck = adb shell curl -s http://10.0.2.2:8000/api/v1/products 2>&1

if ($emulatorCheck -match "success") {
    Write-Host "✓ Emulator CAN access API!" -ForegroundColor Green
} elseif ($emulatorCheck -match "Connection refused") {
    Write-Host "✗ Connection REFUSED!" -ForegroundColor Red
    Write-Host "  Please start Laravel server: php artisan serve" -ForegroundColor Yellow
} elseif ($emulatorCheck -match "error: no devices") {
    Write-Host "✗ No emulator running!" -ForegroundColor Red
    Write-Host "  Please start Android emulator first" -ForegroundColor Yellow
} else {
    Write-Host "✗ Unknown error!" -ForegroundColor Red
    Write-Host "  Response: $emulatorCheck" -ForegroundColor Gray
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Check complete!" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
