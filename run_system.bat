@echo off
echo ========================================
echo   Face Recognition System - Quick Start
echo ========================================
echo.
echo Choose an option:
echo   1. Run GUI Application
echo   2. Capture Dataset (Webcam)
echo   3. Generate 500 Images from Existing
echo   4. Exit
echo.
set /p choice="Enter choice (1-4): "

if "%choice%"=="1" goto run_gui
if "%choice%"=="2" goto capture
if "%choice%"=="3" goto generate
if "%choice%"=="4" goto end

:run_gui
echo.
echo Starting GUI Application...
mvn exec:java -Dexec.mainClass="com.myapp.gui.DeepLearningGUI"
goto end

:capture
echo.
echo Starting Webcam Capture...
python capture_dataset.py
pause
goto end

:generate
echo.
echo Generating 500 images per person...
python generate_dataset.py
pause
goto end

:end
echo.
echo Goodbye!
