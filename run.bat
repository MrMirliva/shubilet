@echo off
REM =======================
REM shubilet - Build & Run
REM =======================
setlocal EnableExtensions EnableDelayedExpansion
chcp 65001 >NUL

REM Go to script directory
cd /d "%~dp0"
echo [INFO] Project root: %CD%
echo.

REM ---------- Pre-flight checks ----------
where mvn >NUL 2>&1
if errorlevel 1 (
  echo [ERROR] Maven not found in PATH. Please install Maven or add it to PATH.
  exit /b 1
)

where docker >NUL 2>&1
if errorlevel 1 (
  echo [WARN] Docker not found. Trying to start Docker Desktop...
  set "DOCKER_DESKTOP_EXE=C:\Program Files\Docker\Docker\Docker Desktop.exe"
  if exist "%DOCKER_DESKTOP_EXE%" (
    start "" "%DOCKER_DESKTOP_EXE%"
  ) else (
    echo [ERROR] Docker Desktop not found at default path:
    echo         %DOCKER_DESKTOP_EXE%
    exit /b 1
  )
)

REM ---------- Build all modules ----------
set MODULES=eureka-server api-gateway member-service expedition-service payment-service security-service

for %%M in (%MODULES%) do (
  if exist "%%M\pom.xml" (
    echo [BUILD] Building %%M ...
    call mvn -f "%%M" clean package -DskipTests
    if errorlevel 1 (
      echo [ERROR] Build failed in module: %%M
      exit /b 1
    )
  ) else (
    echo [WARN] Skipping %%M (no pom.xml)
  )
)

echo.
echo [INFO] All modules built successfully.
echo.

REM ---------- Wait for Docker to be ready ----------
echo [INFO] Checking Docker engine status...
docker info >NUL 2>&1
if errorlevel 1 (
  echo [INFO] Waiting for Docker engine to start (up to 90 seconds)...
  set /a retries=30
  :WAIT_DOCKER
  timeout /t 3 >NUL
  docker info >NUL 2>&1
  if errorlevel 1 (
    set /a retries-=1
    if !retries! GTR 0 (
      echo [INFO] Still waiting... (!retries! tries left)
      goto :WAIT_DOCKER
    ) else (
      echo [ERROR] Docker engine did not start. Exiting.
      exit /b 1
    )
  )
)
echo [INFO] Docker engine is ready.
echo.

REM ---------- Run docker compose ----------
echo [UP] Starting shubilet stack...
docker compose up --build -d

if errorlevel 1 (
  echo [ERROR] Docker Compose failed.
  exit /b 1
)

echo.
echo [SUCCESS] Rocket shubilet is up and running!
echo          Access services at:
echo              - Eureka: http://localhost:8761
echo              - API Gateway: http://localhost:8080
echo.

endlocal
exit /b 0

