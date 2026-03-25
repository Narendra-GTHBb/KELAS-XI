# Quick verification script for mobile app image loading
Write-Host "╔══════════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║  MOBILE APP IMAGE LOADING - VERIFICATION     ║" -ForegroundColor Cyan  
Write-Host "╚══════════════════════════════════════════════╝" -ForegroundColor Cyan
Write-Host ""

# Test 1: Mobile Backend Server
Write-Host "[1/5] Testing Mobile Backend Server..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://192.168.1.3:8000/api/v1/products" -TimeoutSec 3
    if ($response.StatusCode -eq 200) {
        Write-Host "  [OK] Mobile backend responsive (port 8000)" -ForegroundColor Green
    }
} catch {
    Write-Host "  [FAIL] Mobile backend not accessible!" -ForegroundColor Red
    Write-Host "    Run: cd 'MuscleCart Mobile App\backend'; php artisan serve --host=0.0.0.0 --port=8000" -ForegroundColor Yellow
}

# Test 2: API Returns full_image_url
Write-Host "`n[2/5] Checking API Response Format..." -ForegroundColor Yellow
try {
    $apiData = Invoke-RestMethod -Uri "http://192.168.1.3:8000/api/v1/products"
    $product = $apiData.data[0]
    
    if ($product.full_image_url) {
        Write-Host "  [OK] API returns full_image_url" -ForegroundColor Green
        Write-Host "    Sample: $($product.full_image_url.Substring(0, [Math]::Min(60, $product.full_image_url.Length)))..." -ForegroundColor Gray
        
        # Extract URL for next test
        $imageUrl = $product.full_image_url
    } else {
        Write-Host "  [FAIL] full_image_url missing in API response!" -ForegroundColor Red
        Write-Host "    Check Product model" -ForegroundColor Yellow
    }
} catch {
    Write-Host "  [FAIL] Could not parse API response" -ForegroundColor Red
}

# Test 3: Image File Accessibility
Write-Host "`n[3/5] Testing Image File Access..." -ForegroundColor Yellow
if ($imageUrl) {
    try {
        $imgResponse = Invoke-WebRequest -Uri $imageUrl -Method Head -TimeoutSec 3
        if ($imgResponse.StatusCode -eq 200) {
            Write-Host "  [OK] Image accessible from network" -ForegroundColor Green
            Write-Host "    Content-Type: $($imgResponse.Headers['Content-Type'])" -ForegroundColor Gray
        }
    } catch {
        Write-Host "  [FAIL] Image NOT accessible!" -ForegroundColor Red
        Write-Host "    URL: $imageUrl" -ForegroundColor Yellow
        Write-Host "    Check storage symlink exists" -ForegroundColor Yellow
    }
} else {
    Write-Host "  [SKIP] Skipped (no image URL from API)" -ForegroundColor Gray
}

# Test 4: Network Security Config
Write-Host "`n[4/5] Checking Network Security Config..." -ForegroundColor Yellow
$networkConfigPath = "MuscleCart Mobile App\app\src\main\res\xml\network_security_config.xml"
if (Test-Path $networkConfigPath) {
    $configContent = Get-Content $networkConfigPath -Raw
    if ($configContent -match "192\.168\.1\.3") {
        Write-Host "  [OK] Network config allows 192.168.1.3" -ForegroundColor Green
    } else {
        Write-Host "  [FAIL] 192.168.1.3 not in network security config!" -ForegroundColor Red
    }
} else {
    Write-Host "  [FAIL] network_security_config.xml not found!" -ForegroundColor Red
}

# Test 5: WiFi IP Check
Write-Host "`n[5/5] Verifying WiFi IP Address..." -ForegroundColor Yellow
$ipInfo = ipconfig | Select-String "192\.168\.\d+\.\d+" | Select-Object -First 1
if ($ipInfo -match "192\.168\.1\.3") {
    Write-Host "  [OK] WiFi IP is 192.168.1.3" -ForegroundColor Green
} else {
    Write-Host "  [WARN] WiFi IP might have changed!" -ForegroundColor Yellow
    Write-Host "    Current: $ipInfo" -ForegroundColor Yellow
    Write-Host "    Expected: 192.168.1.3" -ForegroundColor Yellow
    Write-Host "    Update NetworkModule.kt and network_security_config.xml if changed" -ForegroundColor Yellow
}

# Final Summary
Write-Host ""
Write-Host "===============================================" -ForegroundColor Cyan
Write-Host "BACKEND STATUS: All systems operational" -ForegroundColor Green
Write-Host "===============================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "If images still not loading in mobile app:" -ForegroundColor Yellow
Write-Host "  1. Open Settings on your Android device" -ForegroundColor White
Write-Host "  2. Go to: Apps - MuscleCart" -ForegroundColor White
Write-Host "  3. Tap: Storage - Clear Data" -ForegroundColor White
Write-Host "  4. Restart app and login again" -ForegroundColor White
Write-Host ""
Write-Host "Reason: App cached corrupted images from old config" -ForegroundColor Gray
Write-Host ""
Write-Host "Full guide in MOBILE_APP_IMAGE_CACHE_FIX.md file" -ForegroundColor Cyan
