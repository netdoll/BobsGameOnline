@echo off
setlocal
title BobsGameOnline Java
cd /d "%~dp0"

echo [BobsGameOnline Java] Starting...
where java >nul 2>nul
if errorlevel 1 (
    echo [BobsGameOnline Java] java not found. Please install it.
    pause
    exit /b 1
)

call gradlew run

if errorlevel 1 (
    echo [BobsGameOnline Java] Exited with error code %errorlevel%.
    pause
)
endlocal
