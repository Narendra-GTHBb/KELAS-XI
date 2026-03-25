# MuscleCart Login Fix Script
# This script helps fix and test the login system

$ErrorActionPreference = "Continue"

Write-Host "=== MuscleCart Login Fix Script ===" -ForegroundColor Cyan
Write-Host ""

# Navigate to backend directory
Set-Location "MuscleCart Mobile App\backend"

Write-Host "Step 1: Testing Database Connection..." -ForegroundColor Yellow
php -r "require 'vendor/autoload.php'; `$app = require 'bootstrap/app.php'; `$kernel = `$app->make(Illuminate\Contracts\Console\Kernel::class); `$kernel->bootstrap(); echo 'Database connection: OK' . PHP_EOL;"

if ($LASTEXITCODE -ne 0) {
    Write-Host "✗ Database connection failed!" -ForegroundColor Red
    Write-Host "Please ensure:" -ForegroundColor Yellow
    Write-Host "  1. XAMPP MySQL is running" -ForegroundColor Yellow
    Write-Host "  2. Database 'musclecart_db' exists" -ForegroundColor Yellow
    Write-Host "  3. .env file is configured correctly" -ForegroundColor Yellow
    exit 1
}

Write-Host ""
Write-Host "Step 2: Running Login API Test..." -ForegroundColor Yellow
php test_login_api.php

if ($LASTEXITCODE -ne 0) {
    Write-Host "✗ Login API test failed!" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "Step 3: Starting Laravel Development Server..." -ForegroundColor Yellow
Write-Host "Server will start at: http://localhost:8000" -ForegroundColor Green
Write-Host "For Android emulator, use: http://10.0.2.2:8000" -ForegroundColor Green
Write-Host ""
Write-Host "Press Ctrl+C to stop the server" -ForegroundColor Gray
Write-Host ""

# Start the Laravel development server
php artisan serve
