# Quick Test - MuscleCart Image Loading
# Test apakah server dan image URLs berfungsi dengan benar

Write-Host "=== MuscleCart Image Loading - Quick Test ===" -ForegroundColor Cyan
Write-Host ""

# Get IP
$ip = (Get-NetIPAddress -AddressFamily IPv4 -InterfaceAlias "Wi-Fi" -ErrorAction SilentlyContinue).IPAddress
If (-not $ip) {
    $ip = Read-Host "Enter your WiFi IP address"
}

Write-Host "Testing with IP: $ip" -ForegroundColor Yellow
Write-Host ""

# Test 1: API Endpoint
Write-Host "Test 1: API Endpoint" -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "http://${ip}:8000/api/v1/products" -TimeoutSec 5
    if ($response.status -eq "success" -and $response.data) {
        Write-Host "  [PASS] API returning $($response.data.Count) products" -ForegroundColor Green
        $firstProduct = $response.data[0]
        if ($firstProduct.full_image_url -like "http://${ip}:8000/storage/products/*") {
            Write-Host "  [PASS] full_image_url uses correct IP and path" -ForegroundColor Green
        } else {
            Write-Host "  [WARN] full_image_url format: $($firstProduct.full_image_url)" -ForegroundColor Yellow
        }
    }
} catch {
    Write-Host "  [FAIL] Cannot connect to API" -ForegroundColor Red
    Write-Host "  Make sure server is running with: php artisan serve --host=0.0.0.0" -ForegroundColor Yellow
}
Write-Host ""

# Test 2: Image URL
Write-Host "Test 2: Image File Access" -ForegroundColor Yellow
try {
    $imageUrl = "http://${ip}:8000/storage/products/0C7C1oepAZ8WBJY3Rkgo7nwNdxuPLcOb5f6Q74WT.webp"
    $imageResponse = Invoke-WebRequest -Uri $imageUrl -TimeoutSec 5 -Method Head
    if ($imageResponse.StatusCode -eq 200) {
        Write-Host "  [PASS] Image file accessible" -ForegroundColor Green
    }
} catch {
    Write-Host "  [FAIL] Cannot access image file" -ForegroundColor Red
    Write-Host "  Check storage link: php artisan storage:link" -ForegroundColor Yellow
}
Write-Host ""

# Test 3: NetworkModule.kt
Write-Host "Test 3: Mobile App Configuration" -ForegroundColor Yellow
$networkModulePath = "C:\XAAMP\htdocs\UKOMP CODING RPL SMK\MuscleCart Mobile App\app\src\main\java\com\gymecommerce\musclecart\di\NetworkModule.kt"
if (Test-Path $networkModulePath) {
    $networkContent = Get-Content $networkModulePath -Raw
    if ($networkContent -match 'BASE_URL\s*=\s*"http://([^:]+):8000/api/v1/"') {
        $appBaseUrl = $matches[1]
        if ($appBaseUrl -eq $ip) {
            Write-Host "  [PASS] NetworkModule uses correct IP ($ip)" -ForegroundColor Green
        } else {
            Write-Host "  [WARN] NetworkModule uses: $appBaseUrl (expected: $ip)" -ForegroundColor Yellow
        }
    }
}
Write-Host ""

# Test 4: .env
Write-Host "Test 4: Backend .env Configuration" -ForegroundColor Yellow
$envPath = "C:\XAAMP\htdocs\UKOMP CODING RPL SMK\MuscleCart Mobile App\backend\.env"
if (Test-Path $envPath) {
    $envContent = Get-Content $envPath -Raw
    if ($envContent -match 'APP_URL=(.+)') {
        $appUrl = $matches[1].Trim()
        $expectedUrl = "http://${ip}:8000"
        if ($appUrl -eq $expectedUrl) {
            Write-Host "  [PASS] APP_URL = $appUrl" -ForegroundColor Green
        } else {
            Write-Host "  [WARN] APP_URL = $appUrl (expected: $expectedUrl)" -ForegroundColor Yellow
        }
    }
}
Write-Host ""

Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "Next Steps:" -ForegroundColor Yellow
Write-Host "1. Test URL di browser HP: http://${ip}:8000/api/v1/products" -ForegroundColor White
Write-Host "2. Buka aplikasi MuscleCart di HP" -ForegroundColor White
Write-Host "3. Check Wishlist dan Cart - gambar harus muncul!" -ForegroundColor White
Write-Host ""
