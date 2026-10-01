@echo off
setlocal
title He Thong Quan Ly Thu Vien OOP - Console CLI

set "JAVA_EXE=C:\Program Files\Android\Android Studio\jbr\bin\java.exe"
if not exist "%JAVA_EXE%" set "JAVA_EXE=java"

if not exist bin (
    echo [*] Thu muc bin chua ton tai, dang tu dong bien dich...
    call build.bat noninteractive
)

echo ========================================================================
echo   KHOI DONG HE THONG QUAN LY THU VIEN OOP (CONSOLE CLI)
echo ========================================================================

"%JAVA_EXE%" "-Dfile.encoding=UTF-8" -cp bin com.library.Main --cli
pause
