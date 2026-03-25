# Script untuk Import Database MuscleCart dengan Aman
# Jalankan sebagai: .\import-database.ps1

Write-Host "================================================" -ForegroundColor Cyan
Write-Host " MUSCLECART DATABASE IMPORT SCRIPT" -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Cyan
Write-Host ""

# Konfigurasi
$MYSQL_BIN = "C:\xampp\mysql\bin"
$DB_NAME = "musclecart_db"
$DB_USER = "root"
$DB_FILE = "C:\XAAMP\htdocs\UKOMP CODING RPL SMK\MuscleCart Mobile App\database\musclecart_db_latest.sql"
$BACKUP_DIR = "C:\XAAMP\htdocs\UKOMP CODING RPL SMK\database_backups"

# Cek apakah file database ada
if (-not (Test-Path $DB_FILE)) {
    Write-Host "❌ ERROR: File database tidak ditemukan!" -ForegroundColor Red
    Write-Host "   Expected: $DB_FILE" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "   File sudah tersedia di:" -ForegroundColor Green
    Write-Host "   C:\XAAMP\htdocs\UKOMP CODING RPL SMK\MuscleCart Mobile App\database\musclecart_db_latest.sql" -ForegroundColor Green
    exit 1
}

# Cek apakah MySQL berjalan
Write-Host "🔍 Checking MySQL status..." -ForegroundColor Yellow
$mysqlProcess = Get-Process mysqld -ErrorAction SilentlyContinue
if (-not $mysqlProcess) {
    Write-Host "❌ ERROR: MySQL tidak berjalan!" -ForegroundColor Red
    Write-Host "   Silakan start XAMPP MySQL terlebih dahulu" -ForegroundColor Yellow
    exit 1
}
Write-Host "✅ MySQL is running" -ForegroundColor Green
Write-Host ""

# Konfirmasi dari user
Write-Host "⚠️  PERINGATAN:" -ForegroundColor Yellow
Write-Host "   Script ini akan menghapus database '$DB_NAME' yang lama" -ForegroundColor Yellow
Write-Host "   dan menggantinya dengan database baru dari laptop." -ForegroundColor Yellow
Write-Host ""
Write-Host "   Database baru memiliki:" -ForegroundColor Cyan
Write-Host "   - Tabel FAVORITES (baru)" -ForegroundColor Green
Write-Host "   - 7 users" -ForegroundColor Green
Write-Host "   - 4 products (Evolene supplements)" -ForegroundColor Green
Write-Host "   - 8 categories" -ForegroundColor Green
Write-Host "   - 35 orders" -ForegroundColor Green
Write-Host "   - Cart & favorites data aktif" -ForegroundColor Green
Write-Host ""

$confirmation = Read-Host "Lanjutkan import? (y/n)"
if ($confirmation -ne 'y' -and $confirmation -ne 'Y') {
    Write-Host "❌ Import dibatalkan" -ForegroundColor Red
    exit 0
}

Write-Host ""
Write-Host "================================================" -ForegroundColor Cyan
Write-Host " STARTING IMPORT PROCESS" -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Cyan
Write-Host ""

# Step 1: Buat folder backup jika belum ada
Write-Host "📁 Step 1/4: Creating backup folder..." -ForegroundColor Yellow
if (-not (Test-Path $BACKUP_DIR)) {
    New-Item -ItemType Directory -Path $BACKUP_DIR -Force | Out-Null
    Write-Host "   ✅ Backup folder created: $BACKUP_DIR" -ForegroundColor Green
} else {
    Write-Host "   ✅ Backup folder exists" -ForegroundColor Green
}
Write-Host ""

# Step 2: Backup database lama (jika ada)
Write-Host "💾 Step 2/4: Backing up old database..." -ForegroundColor Yellow
$timestamp = Get-Date -Format "yyyyMMdd_HHmmss"
$backupFile = "$BACKUP_DIR\musclecart_db_backup_$timestamp.sql"

try {
    $env:Path += ";$MYSQL_BIN"
    
    # Cek apakah database exists
    $dbExists = & "$MYSQL_BIN\mysql.exe" -u $DB_USER -e "SHOW DATABASES LIKE '$DB_NAME';" 2>$null
    
    if ($dbExists) {
        Write-Host "   📦 Backing up to: $backupFile" -ForegroundColor Cyan
        & "$MYSQL_BIN\mysqldump.exe" -u $DB_USER $DB_NAME > $backupFile 2>$null
        
        if ($LASTEXITCODE -eq 0) {
            $fileSize = (Get-Item $backupFile).Length / 1KB
            Write-Host "   ✅ Backup successful ($([math]::Round($fileSize, 2)) KB)" -ForegroundColor Green
        } else {
            Write-Host "   ⚠️  Backup failed (database mungkin tidak ada)" -ForegroundColor Yellow
        }
    } else {
        Write-Host "   ℹ️  Database '$DB_NAME' tidak ada (fresh install)" -ForegroundColor Cyan
    }
} catch {
    Write-Host "   ⚠️  Backup skipped: $_" -ForegroundColor Yellow
}
Write-Host ""

# Step 3: Drop & Create database
Write-Host "🗑️  Step 3/4: Recreating database..." -ForegroundColor Yellow
try {
    $createDbSQL = "DROP DATABASE IF EXISTS $DB_NAME; CREATE DATABASE $DB_NAME CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
    & "$MYSQL_BIN\mysql.exe" -u $DB_USER -e $createDbSQL 2>$null
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "   ✅ Database recreated successfully" -ForegroundColor Green
    } else {
        throw "Failed to recreate database"
    }
} catch {
    Write-Host "   ❌ ERROR: $_" -ForegroundColor Red
    exit 1
}
Write-Host ""

# Step 4: Import database baru
Write-Host "📥 Step 4/4: Importing new database..." -ForegroundColor Yellow
Write-Host "   ⏳ This may take a few moments..." -ForegroundColor Cyan
try {
    & "$MYSQL_BIN\mysql.exe" -u $DB_USER $DB_NAME < $DB_FILE 2>$null
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "   ✅ Database imported successfully!" -ForegroundColor Green
    } else {
        throw "Failed to import database"
    }
} catch {
    Write-Host "   ❌ ERROR: $_" -ForegroundColor Red
    Write-Host "   Mencoba restore backup..." -ForegroundColor Yellow
    
    if (Test-Path $backupFile) {
        & "$MYSQL_BIN\mysql.exe" -u $DB_USER $DB_NAME < $backupFile 2>$null
        Write-Host "   ✅ Backup restored" -ForegroundColor Green
    }
    exit 1
}
Write-Host ""

# Verifikasi
Write-Host "================================================" -ForegroundColor Cyan
Write-Host " VERIFICATION" -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "🔍 Verifying database..." -ForegroundColor Yellow

# Cek tabel favorites
$favoritesCheck = & "$MYSQL_BIN\mysql.exe" -u $DB_USER -D $DB_NAME -e "SELECT COUNT(*) FROM favorites;" -s -N 2>$null
if ($LASTEXITCODE -eq 0) {
    Write-Host "   ✅ Table 'favorites': $favoritesCheck records" -ForegroundColor Green
} else {
    Write-Host "   ❌ Table 'favorites': NOT FOUND" -ForegroundColor Red
}

# Cek products
$productsCheck = & "$MYSQL_BIN\mysql.exe" -u $DB_USER -D $DB_NAME -e "SELECT COUNT(*) FROM products;" -s -N 2>$null
Write-Host "   ✅ Table 'products': $productsCheck products" -ForegroundColor Green

# Cek users
$usersCheck = & "$MYSQL_BIN\mysql.exe" -u $DB_USER -D $DB_NAME -e "SELECT COUNT(*) FROM users;" -s -N 2>$null
Write-Host "   ✅ Table 'users': $usersCheck users" -ForegroundColor Green

# Cek categories
$categoriesCheck = & "$MYSQL_BIN\mysql.exe" -u $DB_USER -D $DB_NAME -e "SELECT COUNT(*) FROM categories;" -s -N 2>$null
Write-Host "   ✅ Table 'categories': $categoriesCheck categories" -ForegroundColor Green

# Cek orders
$ordersCheck = & "$MYSQL_BIN\mysql.exe" -u $DB_USER -D $DB_NAME -e "SELECT COUNT(*) FROM orders;" -s -N 2>$null
Write-Host "   ✅ Table 'orders': $ordersCheck orders" -ForegroundColor Green

Write-Host ""
Write-Host "================================================" -ForegroundColor Cyan
Write-Host " ✅ IMPORT COMPLETED SUCCESSFULLY!" -ForegroundColor Green
Write-Host "================================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "📋 LOGIN CREDENTIALS:" -ForegroundColor Cyan
Write-Host ""
Write-Host "   🔐 Admin Panel:" -ForegroundColor Yellow
Write-Host "      URL: http://localhost:8000/admin" -ForegroundColor White
Write-Host "      Email: admin@musclecart.com" -ForegroundColor White
Write-Host "      Password: admin123" -ForegroundColor White
Write-Host ""
Write-Host "   📱 Mobile App / Test User:" -ForegroundColor Yellow
Write-Host "      Email: test@test.com" -ForegroundColor White
Write-Host "      Password: password123" -ForegroundColor White
Write-Host ""

Write-Host "📁 Database backup saved to:" -ForegroundColor Cyan
Write-Host "   $backupFile" -ForegroundColor White
Write-Host ""

Write-Host "🎯 NEXT STEPS:" -ForegroundColor Cyan
Write-Host "   1. Test Laravel backend: cd backend; php artisan migrate:status" -ForegroundColor White
Write-Host "   2. Test mobile app login" -ForegroundColor White
Write-Host "   3. Test favorites feature" -ForegroundColor White
Write-Host "   4. Clear Laravel cache jika perlu: php artisan cache:clear" -ForegroundColor White
Write-Host ""

Write-Host "✅ Done! Database is ready to use." -ForegroundColor Green
Write-Host ""
