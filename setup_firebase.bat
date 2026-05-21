@echo off
title CornAI Firebase Setup Script
echo.
echo =============================================
echo    CornAI - Firebase Setup Helper
echo =============================================
echo.

:: Check if google-services.json exists
if exist "app\google-services.json" (
    echo [OK] google-services.json found
    echo.
    goto :check_gradle
)

echo [ERROR] google-services.json not found!
echo.
echo Please follow these steps:
echo 1. Go to: https://console.firebase.google.com/
echo 2. Create a new project or use existing
echo 3. Add Android app with package: com.cornai
echo 4. Download google-services.json
echo 5. Place it in this folder (CornAI\app\)
echo.
echo Press any key to continue...
pause >nul
exit /b 1

:check_gradle
echo [OK] Firebase config present
echo.

:: Check for Gradle
gradle --version >nul 2>&1
if errorlevel 1 (
    echo [INFO] Gradle not found in PATH
    echo.
    echo Please open this project in Android Studio
    echo and let it download Gradle automatically.
    echo.
    goto :open_android_studio
)

echo [OK] Gradle found
echo.

:: Try to sync
echo Attempting Gradle sync...
call gradle wrapper --quiet 2>nul

if errorlevel 1 (
    echo [INFO] Gradle sync needs Android Studio
    goto :open_android_studio
)

echo.
echo =============================================
echo    Setup Complete! Next Steps:
echo =============================================
echo.
echo 1. Open project in Android Studio:
echo    File -^> Open -^> Select CornAI folder
echo.
echo 2. Wait for Gradle sync to complete
echo.
echo 3. Click Run (Shift+F10) to test
echo.
echo 4. Test Login:
echo    - Click "Masuk sebagai Tamu" OR
echo    - Register new account
echo.
echo 5. Test Scanner:
echo    - Click "Mulai Scan Sekarang"
echo    - Grant camera permission
echo    - Click "Mulai Pemindaian"
echo.
echo =============================================
echo.

:open_android_studio
echo Would you like to open Android Studio?
echo Press Y for Yes, N for No...
set /p choice=
if /i "%choice%"=="Y" start "" "C:\Program Files\Android Studio\bin\studio64.exe"
if /i "%choice%"=="Y" goto :end

echo.
echo You can open Android Studio manually:
echo 1. Open Android Studio
echo 2. File -^> Open
echo 3. Select: %CD%
echo.

:end
echo.
echo =============================================
echo    Firebase Setup Helper Complete
echo =============================================
echo.
pause