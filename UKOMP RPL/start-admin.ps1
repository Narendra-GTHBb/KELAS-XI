# Start Admin Panel Script
# Menjalankan server admin panel yang benar (biru-ungu)

Write-Host "=== Starting MuscleCart Admin Panel ===" -ForegroundColor Cyan
Write-Host ""

# Stop server lain yang mungkin menggunakan port 8001
Write-Host "Stopping any existing server on port 8001..." -ForegroundColor Yellow
Get-Process -Name php -ErrorAction SilentlyContinue | ForEach-Object {
    $portCheck = netstat -ano | Select-String $_.Id | Select-String "8001"
    if ($portCheck) {
        Write-Host "  Stopping PHP process on port 8001 (PID: $($_.Id))" -ForegroundColor Yellow
        Stop-Process -Id $_.Id -Force
    }
}

Write-Host ""
Write-Host "Starting Admin Panel Server..." -ForegroundColor Green
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
Write-Host ""
Write-Host "  🌐 Admin Panel URL:" -ForegroundColor Yellow
Write-Host "     http://127.0.0.1:8001" -ForegroundColor White
Write-Host ""
Write-Host "  🔐 Login Credentials:" -ForegroundColor Yellow
Write-Host "     Email:    admin@musclecart.com" -ForegroundColor White
Write-Host "     Password: admin123" -ForegroundColor White
Write-Host ""
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
Write-Host ""
Write-Host "Press Ctrl+C to stop server" -ForegroundColor Gray
Write-Host ""

Set-Location "musclecart-admin"
php artisan serve --port=8001
