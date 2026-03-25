# Script untuk menampilkan IP Address lokal komputer
# Gunakan IP ini untuk config di Android app jika pakai device fisik

Write-Host "================================================" -ForegroundColor Cyan
Write-Host " MUSCLECART - GET LOCAL IP ADDRESS" -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Cyan
Write-Host ""

# Get all network adapters
$adapters = Get-NetIPAddress -AddressFamily IPv4 | Where-Object {
    $_.InterfaceAlias -notlike "*Loopback*" -and
    $_.IPAddress -ne "127.0.0.1" -and
    $_.InterfaceAlias -notlike "*VirtualBox*" -and
    $_.InterfaceAlias -notlike "*VMware*"
}

if ($adapters) {
    Write-Host "📡 IP ADDRESS KOMPUTER ANDA:" -ForegroundColor Green
    Write-Host ""
    
    foreach ($adapter in $adapters) {
        $interface = $adapter.InterfaceAlias
        $ip = $adapter.IPAddress
        
        Write-Host "   Interface: $interface" -ForegroundColor Yellow
        Write-Host "   IP Address: $ip" -ForegroundColor White
        Write-Host "   Status: " -NoNewline
        
        if ($adapter.PrefixOrigin -eq "Dhcp" -or $adapter.PrefixOrigin -eq "Manual") {
            Write-Host "Active ✅" -ForegroundColor Green
        } else {
            Write-Host "Inactive" -ForegroundColor Gray
        }
        Write-Host ""
    }
    
    # Get primary network IP
    $primaryIP = ($adapters | Where-Object { $_.PrefixOrigin -eq "Dhcp" -or $_.PrefixOrigin -eq "Manual" } | Select-Object -First 1).IPAddress
    
    if ($primaryIP) {
        Write-Host "================================================" -ForegroundColor Cyan
        Write-Host " 🎯 GUNAKAN IP INI:" -ForegroundColor Yellow
        Write-Host "    $primaryIP" -ForegroundColor Green
        Write-Host "================================================" -ForegroundColor Cyan
        Write-Host ""
        
        Write-Host "📱 LANGKAH SELANJUTNYA:" -ForegroundColor Cyan
        Write-Host ""
        Write-Host "1. Edit file ProductMapper.kt:" -ForegroundColor White
        Write-Host "   Location: MuscleCart Mobile App\app\src\main\java\com\gymecommerce\musclecart\data\mapper\ProductMapper.kt" -ForegroundColor Gray
        Write-Host ""
        Write-Host "2. Cari baris:" -ForegroundColor White
        Write-Host '   val backendUrl = "http://127.0.0.1:8000/storage/products/$filename"' -ForegroundColor Gray
        Write-Host ""
        Write-Host "3. Ganti dengan:" -ForegroundColor White
        Write-Host "   val backendUrl = ""http://$primaryIP:8000/storage/products/`$filename""" -ForegroundColor Green
        Write-Host ""
        Write-Host "4. Rebuild app:" -ForegroundColor White
        Write-Host "   .\gradlew clean assembleDebug" -ForegroundColor Gray
        Write-Host ""
        Write-Host "5. Test akses gambar di browser HP:" -ForegroundColor White
        Write-Host "   http://$primaryIP`:8000/storage/products/0C7C1oepAZ8WBJY3Rkgo7nwNdxuPLcOb5f6Q74WT.webp" -ForegroundColor Gray
        Write-Host ""
        
        # Test if Laravel server is running
        try {
            $testUrl = "http://127.0.0.1:8000/api/products"
            $response = Invoke-WebRequest -Uri $testUrl -TimeoutSec 2 -ErrorAction Stop
            Write-Host "✅ Laravel server is running on port 8000" -ForegroundColor Green
            Write-Host "   Your API URL: http://$primaryIP`:8000/api" -ForegroundColor Cyan
        } catch {
            Write-Host "⚠️  Laravel server not running on port 8000" -ForegroundColor Yellow
            Write-Host "   Start server: php artisan serve --host=0.0.0.0" -ForegroundColor Gray
        }
        Write-Host ""
        
        # Copy to clipboard
        try {
            $primaryIP | Set-Clipboard
            Write-Host "📋 IP Address sudah di-copy ke clipboard!" -ForegroundColor Green
        } catch {
            # Clipboard tidak available
        }
    }
} else {
    Write-Host "❌ Tidak ada network adapter aktif ditemukan!" -ForegroundColor Red
    Write-Host "   Pastikan WiFi atau Ethernet terkoneksi" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "💡 TIPS:" -ForegroundColor Yellow
Write-Host "   - Pastikan HP dan komputer di WiFi yang sama" -ForegroundColor White
Write-Host "   - Firewall mungkin perlu dibuka untuk port 8000" -ForegroundColor White
Write-Host "   - Untuk production, gunakan proper domain/server" -ForegroundColor White
Write-Host ""
