@echo off
setlocal
title He Thong Quan Ly Thu Vien OOP - Console

set "JAVA_EXE=C:\Program Files\Android\Android Studio\jbr\bin\java.exe"
if not exist "%JAVA_EXE%" set "JAVA_EXE=java"

rem Luon bien dich lai truoc khi chay de chac chan chay dung code moi nhat
call build.bat noninteractive
if %ERRORLEVEL% neq 0 (
    pause
    exit /b 1
)

echo ========================================================================
echo   DANG KHOI DONG HE THONG QUAN LY THU VIEN OOP...
echo   Du lieu duoc doc / ghi trong cac file text tai thu muc data\
echo ========================================================================

"%JAVA_EXE%" "-Dfile.encoding=UTF-8" -cp bin com.library.Main

if %ERRORLEVEL% neq 0 (
    echo.
    echo [-] CO LOI XAY RA KHI CHAY CHUONG TRINH!
)
pause
