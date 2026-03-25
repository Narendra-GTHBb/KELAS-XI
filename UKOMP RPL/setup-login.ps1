# MuscleCart - Setup dan Start Semua Service
# Script ini membantu setup dan menjalankan semua yang dibutuhkan

$ErrorActionPreference = "Continue"

function Write-Step {
    param($Message)
    Write-Host "`n=== $Message ===" -ForegroundColor Cyan
}

function Write-Success {
    param($Message)
    Write-Host "✓ $Message" -ForegroundColor Green
}

function Write-Error-Custom {
    param($Message)
    Write-Host "✗ $Message" -ForegroundColor Red
}

function Write-Info {
    param($Message)
    Write-Host "  $Message" -ForegroundColor Yellow
}

Clear-Host
Write-Host "╔═══════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║   MuscleCart Login System Setup          ║" -ForegroundColor Cyan
Write-Host "╚═══════════════════════════════════════════╝" -ForegroundColor Cyan

# Step 1: Check MySQL
Write-Step "Checking MySQL Connection"
Write-Info "Mencoba koneksi ke MySQL..."

$mysqlRunning = $false
try {
    $null = New-Object System.Net.Sockets.TcpClient("127.0.0.1", 3306)
    $mysqlRunning = $true
    Write-Success "MySQL is running on port 3306"
} catch {
    Write-Error-Custom "MySQL tidak berjalan atau tidak bisa diakses"
    Write-Info "Silakan:"
    Write-Info "1. Buka XAMPP Control Panel"
    Write-Info "2. Klik 'Start' pada MySQL"
    Write-Info "3. Tunggu hingga status menjadi hijau"
    Write-Info "4. Jalankan script ini lagi`n"
    
    $response = Read-Host "Apakah Anda sudah menjalankan MySQL? (y/n)"
    if ($response -ne 'y') {
        exit 1
    }
}

# Step 2: Check Database
Write-Step "Checking Database"
Set-Location "MuscleCart Mobile App\backend"

Write-Info "Testing database connection..."
$dbTest = php -r "require 'vendor/autoload.php'; try { `$app = require 'bootstrap/app.php'; `$kernel = `$app->make(Illuminate\Contracts\Console\Kernel::class); `$kernel->bootstrap(); `$db = `$app->make('db'); `$db->connection()->getPdo(); echo 'OK'; } catch (Exception `$e) { echo 'ERROR:' . `$e->getMessage(); }" 2>&1

if ($dbTest -like "*OK*") {
    Write-Success "Database connection successful"
} else {
    Write-Error-Custom "Database connection failed"
    Write-Info "Error: $dbTest"
    Write-Info ""
    Write-Info "Perbaikan:"
    Write-Info "1. Pastikan database 'musclecart_db' sudah dibuat"
    Write-Info "2. Import database:"
    Write-Info "   - Buka http://localhost/phpmyadmin"
    Write-Info "   - Import file: MuscleCart Mobile App\database\musclecart_db_latest.sql"
    Write-Info "3. Cek file .env, pastikan:"
    Write-Info "   DB_DATABASE=musclecart_db"
    Write-Info "   DB_USERNAME=root"
    Write-Info "   DB_PASSWORD="
    
    $response = Read-Host "`nCoba setup database otomatis? (y/n)"
    if ($response -eq 'y') {
        Write-Info "Running migrations..."
        php artisan migrate:fresh --seed --force
        if ($LASTEXITCODE -eq 0) {
            Write-Success "Database setup complete"
        } else {
            Write-Error-Custom "Migration failed"
            exit 1
        }
    } else {
        exit 1
    }
}

# Step 3: Setup Test Users
Write-Step "Setting Up Test Users"
Write-Info "Creating/updating test users..."

php test_login_api.php

if ($LASTEXITCODE -ne 0) {
    Write-Error-Custom "Failed to setup test users"
    Write-Info "Coba jalankan manual: php test_login_api.php"
    exit 1
}

# Step 4: Check if server already running
Write-Step "Checking Backend Server"
$serverRunning = $false
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8000/api/v1/products" -TimeoutSec 2 -ErrorAction SilentlyContinue
    $serverRunning = $true
    Write-Success "Backend server sudah berjalan di http://localhost:8000"
} catch {
    Write-Info "Backend server belum berjalan"
}

if (-not $serverRunning) {
    Write-Info "Starting Laravel development server..."
    Write-Info "Server akan berjalan di:"
    Write-Info "  - http://localhost:8000 (browser)"
    Write-Info "  - http://10.0.2.2:8000 (Android emulator)"
    Write-Info ""
    Write-Info "Press Ctrl+C untuk stop server`n"
    
    Start-Sleep -Seconds 2
    php artisan serve
} else {
    Write-Info "Server sudah berjalan, skip start server"
    Write-Info ""
    Write-Host "Setup selesai! Kini Anda bisa:" -ForegroundColor Green
    Write-Host "  1. Build mobile app: .\gradlew installDebug" -ForegroundColor White
    Write-Host "  2. Login dengan:" -ForegroundColor White
    Write-Host "     Email: test@test.com" -ForegroundColor Yellow
    Write-Host "     Password: password123" -ForegroundColor Yellow
    Write-Host ""
}
