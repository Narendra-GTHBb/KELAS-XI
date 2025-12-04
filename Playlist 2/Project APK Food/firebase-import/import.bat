@echo off
title Waves of Food - Firebase Data Import

echo.
echo ===============================================
echo   Waves of Food - Firebase Data Import Tool
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

:: Check if package.json exists
if not exist package.json (
    echo ❌ package.json not found!
    echo Please run this script from the firebase-import directory
    pause
    exit /b 1
)

:: Install dependencies if node_modules doesn't exist
if not exist node_modules (
    echo 📦 Installing dependencies...
    call npm install
    if errorlevel 1 (
        echo ❌ Failed to install dependencies!
        pause
        exit /b 1
    )
    echo ✅ Dependencies installed successfully!
    echo.
)

:: Check if .env file exists
if not exist .env (
    echo ⚠️  .env file not found!
    echo Please copy .env.example to .env and configure it
    echo.
    if exist .env.example (
        echo Copying .env.example to .env...
        copy .env.example .env
        echo.
        echo 📝 Please edit .env file with your Firebase configuration
        echo Then run this script again
    )
    pause
    exit /b 1
)

:: Check if serviceAccountKey.json exists
if not exist serviceAccountKey.json (
    echo ❌ serviceAccountKey.json not found!
    echo.
    echo Please download your Firebase service account key:
    echo 1. Go to Firebase Console
    echo 2. Project Settings ^> Service Accounts
    echo 3. Generate new private key
    echo 4. Save as 'serviceAccountKey.json' in this directory
    echo.
    pause
    exit /b 1
)

echo 🔥 Starting Firebase data import...
echo.

:: Run the import script
call node import-data.js

if errorlevel 1 (
    echo.
    echo ❌ Import failed! Check the error messages above.
) else (
    echo.
    echo 🎉 Import completed successfully!
)

echo.
pause
