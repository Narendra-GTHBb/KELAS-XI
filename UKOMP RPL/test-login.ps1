# Quick Test Login API
# This script quickly tests if the login API is working

$ErrorActionPreference = "Continue"

Set-Location "MuscleCart Mobile App\backend"

Write-Host "Testing Login API..." -ForegroundColor Cyan
php test_login_api.php

Write-Host ""
Write-Host "If all tests passed, your login API is ready!" -ForegroundColor Green
Write-Host ""
Write-Host "Test Credentials:" -ForegroundColor Yellow
Write-Host "  Email: test@test.com" -ForegroundColor White
Write-Host "  Password: password123" -ForegroundColor White
Write-Host ""
Write-Host "API Endpoint:" -ForegroundColor Yellow
Write-Host "  http://10.0.2.2:8000/api/v1/login (for emulator)" -ForegroundColor White
Write-Host ""
