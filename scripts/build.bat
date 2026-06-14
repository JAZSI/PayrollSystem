@echo off
REM Build the full single-artifact jar (React UI bundled into Spring Boot).
setlocal
cd /d "%~dp0\.."

echo [1/2] Building frontend (bun)...
pushd frontend
call bun install || goto :error
call bun run build || goto :error
popd

echo [2/2] Building backend jar (bundles the UI under /static)...
call mvn -q clean package -DskipTests || goto :error

echo.
echo Done: target\PayrollSystem-1.0-SNAPSHOT.jar
echo Run it with: scripts\run.bat
exit /b 0

:error
echo BUILD FAILED.
exit /b 1
