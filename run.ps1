# Script chay du an Quan ly Thu vien tren PowerShell
$javaBin = "C:\Program Files\Android\Android Studio\jbr\bin"
if (Test-Path "$javaBin\javac.exe") {
    $env:Path = "$javaBin;" + $env:Path
}

if (!(Test-Path "bin")) {
    New-Item -ItemType Directory -Path "bin" | Out-Null
    Write-Host "[*] Dang bien dich ma nguon Java..." -ForegroundColor Cyan
    & javac -encoding UTF-8 -d bin (Get-ChildItem -Path "src" -Recurse -Filter "*.java" | Select-Object -ExpandProperty FullName)
}

if ($args.Count -gt 0 -and ($args[0] -eq "--cli" -or $args[0] -eq "-c")) {
    Write-Host "[*] Khoi dong che do Console CLI..." -ForegroundColor Green
    & java "-Dfile.encoding=UTF-8" -cp bin com.library.Main --cli
} else {
    Write-Host "[*] Khoi dong Giao dien Do hoa GUI..." -ForegroundColor Green
    & java "-Dfile.encoding=UTF-8" -cp bin com.library.Main
}
