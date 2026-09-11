@echo off
chcp 65001 >nul
setlocal

cd /d "%~dp0.."
if not defined JAVA_OPTS set "JAVA_OPTS=-Xms128m -Xmx3g -Xss256k"

java %JAVA_OPTS% -Dfile.encoding=UTF-8 -jar "backend\target\game-backend.jar" %*
