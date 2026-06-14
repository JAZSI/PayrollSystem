@echo off
REM Run the single-artifact jar. UI + API both served on http://localhost:8080
setlocal
cd /d "%~dp0\.."

if not exist "target\PayrollSystem-1.0-SNAPSHOT.jar" (
    echo Jar not found. Build first: scripts\build.bat
    exit /b 1
)

echo PayrollPal running at http://localhost:8080  (Ctrl+C to stop)
java -jar "target\PayrollSystem-1.0-SNAPSHOT.jar"
