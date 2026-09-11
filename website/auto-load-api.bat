@echo off
title Auto Load API MBBank
echo ===============================
echo    AUTO LOAD API MBBANK
echo    Chay moi 30 giay
echo ===============================
echo.

:loop
echo [%date% %time%] Dang load API...
pushd "%~dp0"
if not defined PHP_BIN set "PHP_BIN=php"
"%PHP_BIN%" "%~dp0auto-load-api.php"
popd

echo [%date% %time%] Hoan thanh. Cho 30 giay...
echo.
timeout /t 30 /nobreak >nul
goto loop
