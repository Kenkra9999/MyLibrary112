@echo off
set JAVAC_EXE=C:\Program Files\Android\Android Studio\jbr\bin\javac.exe
if not exist "%JAVAC_EXE%" set JAVAC_EXE=javac

if not exist bin mkdir bin

echo [*] Dang bien dich ma nguon Java...
"%JAVAC_EXE%" -encoding UTF-8 -d bin src\com\library\enums\*.java src\com\library\utils\*.java src\com\library\models\*.java src\com\library\users\*.java src\com\library\search\*.java src\com\library\notifications\*.java src\com\library\collections\*.java src\com\library\services\*.java src\com\library\cli\*.java src\com\library\*.java

if %ERRORLEVEL% equ 0 (
    echo [+] BIEN DICH THANH CONG!
) else (
    echo [-] BIEN DICH THAT BAI!
)

if "%1"=="" pause
