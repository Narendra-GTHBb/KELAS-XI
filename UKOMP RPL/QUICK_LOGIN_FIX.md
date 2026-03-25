# Quick Start Guide - Login Fix

## 🚀 Cara Tercepat (Quick Fix)

### 1️⃣ Start MySQL
1. Buka **XAMPP Control Panel**
2. Klik **Start** pada **MySQL**
3. Tunggu hingga berwarna hijau

### 2️⃣ Test Backend
```powershell
.\test-login.ps1
```

✅ Jika semua test passed, lanjut ke step 3  
❌ Jika error, lihat **Troubleshooting** di bawah

### 3️⃣ Start Server (Terminal Baru)
```powershell
cd "MuscleCart Mobile App\backend"
php artisan serve
```

**Jangan tutup terminal ini!** Server harus tetap berjalan.

### 4️⃣ Test Login di Mobile App
1. Build & install app:
   ```powershell
   cd "MuscleCart Mobile App"
   .\gradlew installDebug
   ```

2. Buka app di emulator

3. Login dengan:
   - **Email:** test@test.com
   - **Password:** password123

---

## ❌ Troubleshooting

### MySQL tidak bisa start
- Cek apakah port 3306 sudah dipakai aplikasi lain
- Restart XAMPP
- Install ulang XAMPP jika perlu

### Test gagal: "No connection could be made"
```powershell
# 1. Pastikan MySQL running di XAMPP
# 2. Cek database sudah ada
# - Buka: http://localhost/phpmyadmin
# - Database: musclecart_db harus ada
# - Jika belum, import: MuscleCart Mobile App\database\musclecart_db_latest.sql
```

### Server tidak bisa start
```powershell
# Port 8000 sudah dipakai, gunakan port lain:
cd "MuscleCart Mobile App\backend"
php artisan serve --port=8080

# JANGAN LUPA ubah BASE_URL di NetworkModule.kt:
# private const val BASE_URL = "http://10.0.2.2:8080/api/v1/"
```

### Mobile app: "Network error"
1. ✅ Pastikan server Laravel berjalan (`php artisan serve`)
2. ✅ Cek URL di logcat/console
3. ✅ Gunakan emulator (bukan device fisik)
4. ✅ BASE_URL harus: `http://10.0.2.2:8000/api/v1/`

---

## 📝 Test Credentials

| Type     | Email                 | Password    |
|----------|-----------------------|-------------|
| Customer | test@test.com         | password123 |
| Admin    | admin@musclecart.com  | admin123    |

---

## 🔧 Full Guide

Lihat **LOGIN_FIX_GUIDE.md** untuk panduan lengkap.
