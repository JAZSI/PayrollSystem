@echo off
REM Package PayrollPal with jpackage: a self-contained portable app (bundled Java runtime).
setlocal
cd /d "%~dp0\.."
set JAR=PayrollSystem-1.0-SNAPSHOT.jar
set MAIN=org.springframework.boot.loader.launch.JarLauncher

if not exist "target\%JAR%" (
    echo Build first: scripts\build.bat
    exit /b 1
)

echo Staging jar...
if exist build\jpackage-input rmdir /s /q build\jpackage-input
mkdir build\jpackage-input
copy /y "target\%JAR%" "build\jpackage-input\" >nul

echo Building portable app-image...
if exist dist\PayrollPal rmdir /s /q dist\PayrollPal
if not exist dist mkdir dist
jpackage --type app-image --name PayrollPal --app-version 1.0 --input build\jpackage-input --main-jar %JAR% --main-class %MAIN% --java-options "-Dfile.encoding=UTF-8" --dest dist || goto :error

echo.
echo Portable app ready: dist\PayrollPal\PayrollPal.exe  (run it, then open http://localhost:8080)
echo.
echo For a Windows installer (.msi), install the WiX Toolset, then run:
echo   jpackage --type msi --name PayrollPal --app-version 1.0 --input build\jpackage-input --main-jar %JAR% --main-class %MAIN% --win-menu --win-shortcut --dest dist
exit /b 0

:error
echo jpackage FAILED.
exit /b 1
