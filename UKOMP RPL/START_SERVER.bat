@echo off
echo ========================================
echo  MuscleCart - Starting Backend Server
echo ========================================
echo.

:: Check if XAMPP MySQL is running
echo [1/2] Checking XAMPP MySQL...
sc query MySQL 2>nul | find "RUNNING" >nul
if errorlevel 1 (
    echo     Starting MySQL...
    net start MySQL >nul 2>&1
    timeout /t 2 /nobreak >nul
) else (
    echo     MySQL already running.
)

echo.
echo [2/2] Starting Laravel API server...
echo     URL: http://127.0.0.1:8000 (browser)
echo     URL: http://10.0.2.2:8000  (Android emulator)
echo     Press Ctrl+C to stop the server
echo.
cd /d "C:\XAAMP\htdocs\UKOMP CODING RPL SMK\musclecart-admin"
php artisan serve --host=0.0.0.0 --port=8000
