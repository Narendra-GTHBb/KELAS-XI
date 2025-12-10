@echo off
title Firebase Connection Test

echo.
echo ===============================================
echo     Firebase Connection Test
echo ===============================================
echo.

:: Check if Node.js is installed
node --version >nul 2>&1
if errorlevel 1 (
    echo ❌ Node.js is not installed!
    echo Please install Node.js from https://nodejs.org/
    pause
    exit /b 1
)

:: Install dependencies if needed
if not exist node_modules (
    echo 📦 Installing dependencies...
    call npm install
    if errorlevel 1 (
        echo ❌ Failed to install dependencies!
        pause
        exit /b 1
    )
)

:: Run connection test
echo 🔥 Testing Firebase connection...
echo.
call node test-connection.js

echo.
pause
