@echo off
echo ========================================
echo   MuscleCart Backend Health Check
echo ========================================
echo.

REM Check if MySQL is running
netstat -ano | findstr ":3306" | findstr "LISTENING" > nul
if %errorlevel% equ 0 (
    echo [OK] MySQL is running on port 3306
) else (
    echo [ERROR] MySQL is NOT running!
    echo Please start MySQL from XAMPP Control Panel
    pause
    exit /b 1
)

REM Check if Apache is running
netstat -ano | findstr ":8000" | findstr "LISTENING" > nul
if %errorlevel% equ 0 (
    echo [OK] Backend server is running on port 8000
) else (
    echo [ERROR] Backend server is NOT running on port 8000!
    echo Please check Apache XAMPP
    pause
    exit /b 1
)

echo [OK] All services are running
echo.
pause
