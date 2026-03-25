# MuscleCart Quick Start - Network Mode
# Script untuk start Laravel server yang bisa diakses dari HP via WiFi

Write-Host "🚀 MuscleCart Quick Start - Network Mode" -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host ""

# 1. Get IP Address
Write-Host "1️⃣ Checking IP Address..." -ForegroundColor Yellow
$ip = (Get-NetIPAddress -AddressFamily IPv4 -InterfaceAlias "Wi-Fi" -ErrorAction SilentlyContinue).IPAddress
If ($ip) {
    Write-Host "   ✅ WiFi IP: $ip" -ForegroundColor Green
} else {
    $ip = Read-Host "   ⚠️  WiFi not found. Enter your computer's IP manually"
}
Write-Host ""

# 2. Check backend folder
$backendPath = "C:\XAAMP\htdocs\UKOMP CODING RPL SMK\MuscleCart Mobile App\backend"
if (-not (Test-Path $backendPath)) {
    Write-Host "   ❌ Backend folder not found at: $backendPath" -ForegroundColor Red
    pause
    exit
}

# 3. Check APP_URL in .env
Write-Host "2️⃣ Checking .env configuration..." -ForegroundColor Yellow
$envFile = "$backendPath\.env"
$envContent = Get-Content $envFile -Raw

if ($envContent -match 'APP_URL=(.+)') {
    $currentAppUrl = $matches[1].Trim()
    $expectedAppUrl = "http://${ip}:8000"
    
    if ($currentAppUrl -eq $expectedAppUrl) {
        Write-Host "   ✅ APP_URL already set to: $currentAppUrl" -ForegroundColor Green
    } else {
        Write-Host "   ⚠️  APP_URL is: $currentAppUrl" -ForegroundColor Yellow
        Write-Host "   ⚠️  Should be: $expectedAppUrl" -ForegroundColor Yellow
        $updateEnv = Read-Host "   Update APP_URL? (Y/N)"
        
        if ($updateEnv -eq "Y" -or $updateEnv -eq "y") {
            $envContent = $envContent -replace 'APP_URL=.+', "APP_URL=$expectedAppUrl"
            Set-Content -Path $envFile -Value $envContent
            Write-Host "   ✅ APP_URL updated to: $expectedAppUrl" -ForegroundColor Green
            
            # Clear config cache
            Write-Host "   🔄 Clearing config cache..." -ForegroundColor Yellow
            Set-Location $backendPath
            php artisan config:clear
            Write-Host "   ✅ Config cache cleared" -ForegroundColor Green
        }
    }
}
Write-Host ""

# 4. Check storage link
Write-Host "3️⃣ Checking storage link..." -ForegroundColor Yellow
$storageLink = "$backendPath\public\storage"
if (Test-Path $storageLink) {
    Write-Host "   ✅ Storage link exists" -ForegroundColor Green
} else {
    Write-Host "   ⚠️  Storage link not found, creating..." -ForegroundColor Yellow
    Set-Location $backendPath
    php artisan storage:link --force
    Write-Host "   ✅ Storage link created" -ForegroundColor Green
}
Write-Host ""

# 5. Start Laravel server
Write-Host "4️⃣ Starting Laravel Server..." -ForegroundColor Yellow
Write-Host "   🌐 Server will be accessible at:" -ForegroundColor Cyan
Write-Host "      - Local:    http://127.0.0.1:8000" -ForegroundColor White
Write-Host "      - Network:  http://${ip}:8000" -ForegroundColor White
Write-Host ""
Write-Host "   📱 Test URLs for HP:" -ForegroundColor Cyan
Write-Host "      - API Test: http://${ip}:8000/api/v1/products" -ForegroundColor White
Write-Host "      - Image:    http://${ip}:8000/storage/products/0C7C1oepAZ8WBJY3Rkgo7nwNdxuPLcOb5f6Q74WT.webp" -ForegroundColor White
Write-Host ""
Write-Host "   ⚠️  IMPORTANT:" -ForegroundColor Red
Write-Host "      1. Pastikan HP dan Komputer di WiFi yang sama" -ForegroundColor Yellow
Write-Host "      2. Test URL di browser HP dulu sebelum buka app" -ForegroundColor Yellow
Write-Host "      3. Kalau firewall block, allow port 8000" -ForegroundColor Yellow
Write-Host ""
Write-Host "   Press Ctrl+C to stop server" -ForegroundColor Gray
Write-Host ""
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host ""

Set-Location $backendPath
php artisan serve --host=0.0.0.0 --port=8000
