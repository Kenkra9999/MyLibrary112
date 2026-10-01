@echo off
setlocal
title He Thong Quan Ly Thu Vien OOP - Java LMS

set "JAVA_EXE=C:\Program Files\Android\Android Studio\jbr\bin\java.exe"
if not exist "%JAVA_EXE%" set "JAVA_EXE=java"

if not exist bin (
    echo [*] Thu muc bin chua ton tai, dang tu dong bien dich...
    call build.bat noninteractive
)

echo ========================================================================
echo   DANG KHOI DONG HE THONG QUAN LY THU VIEN OOP...
echo ========================================================================
echo [*] Java Executable: %JAVA_EXE%
echo [*] Dang khoi chay Giao dien Do hoa (Swing GUI)...
echo.

"%JAVA_EXE%" "-Dfile.encoding=UTF-8" -cp bin com.library.Main

if %ERRORLEVEL% neq 0 (
    echo.
    echo [-] CO LOI XAY RA KHI CHAY CHUONG TRINH!
    pause
)
