# Script chay du an Quan ly Thu vien tren PowerShell
$javaBin = "C:\Program Files\Android\Android Studio\jbr\bin"
if (Test-Path "$javaBin\javac.exe") {
    $env:Path = "$javaBin;" + $env:Path
}

# Luon bien dich lai (xoa ban cu) de chay dung code moi nhat
if (Test-Path "bin") { Remove-Item -Recurse -Force "bin" }
New-Item -ItemType Directory -Path "bin" | Out-Null
Write-Host "[*] Dang bien dich ma nguon Java..." -ForegroundColor Cyan
& javac -encoding UTF-8 -d bin (Get-ChildItem -Path "src" -Recurse -Filter "*.java" | Select-Object -ExpandProperty FullName)
if ($LASTEXITCODE -ne 0) {
    Write-Host "[-] Bien dich that bai!" -ForegroundColor Red
    exit 1
}

Write-Host "[*] Khoi dong chuong trinh (du lieu: thu muc data\)..." -ForegroundColor Green
& java "-Dfile.encoding=UTF-8" -cp bin com.library.Main
