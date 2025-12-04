@echo off
echo ========================================
echo  START LARAVEL SERVER UNTUK EMULATOR
echo ========================================
echo.
echo Server akan berjalan di:
echo - Host: 0.0.0.0:8000
echo - Dari emulator akses: http://10.0.2.2:8000
echo.
echo TEKAN CTRL+C UNTUK STOP
echo ========================================
echo.

REM Ganti path ini sesuai lokasi project Laravel kamu
cd /d C:\Users\%USERNAME%\path\to\laravel\project

REM Start server dengan host 0.0.0.0 agar bisa diakses emulator
php artisan serve --host=0.0.0.0 --port=8000
