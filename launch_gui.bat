@echo off
REM ========================================
REM   Face Recognition Attendance System
REM   Complete GUI-Based Application
REM ========================================

echo.
echo Starting Face Recognition Attendance System...
echo Please wait while the GUI loads...
echo.

REM Launch the GUI application
mvn compile exec:java -Dexec.mainClass="com.myapp.Main" -q

echo.
echo Application closed.
pause
