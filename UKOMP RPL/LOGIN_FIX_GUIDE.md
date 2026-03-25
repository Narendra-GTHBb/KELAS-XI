# Panduan Perbaikan Login MuscleCart Mobile App

## Masalah
Tidak bisa login di mobile app MuscleCart.

## Penyebab
1. MySQL/Database belum berjalan
2. User test belum dibuat di database
3. Kolom password yang salah di UserSeeder
4. Backend server belum berjalan

## Solusi

### Langkah 1: Pastikan XAMPP MySQL Berjalan

1. Buka **XAMPP Control Panel**
2. Start **Apache** dan **MySQL**
3. Klik **Admin** di samping MySQL untuk membuka phpMyAdmin
4. Pastikan database `musclecart_db` sudah ada

   **Jika belum ada:**
   - Buat database baru dengan nama `musclecart_db`
   - Import file database: `MuscleCart Mobile App\database\musclecart_db_latest.sql`
   
### Langkah 2: Konfigurasi Backend Laravel

1. Buka file `.env` di folder `MuscleCart Mobile App\backend\`
   
   **Jika file `.env` tidak ada:**
   ```bash
   cd "MuscleCart Mobile App\backend"
   copy .env.example .env
   ```

2. Pastikan konfigurasi database di `.env`:
   ```
   DB_CONNECTION=mysql
   DB_HOST=127.0.0.1
   DB_PORT=3306
   DB_DATABASE=musclecart_db
   DB_USERNAME=root
   DB_PASSWORD=
   ```

3. Generate application key (jika belum):
   ```bash
   php artisan key:generate
   ```

4. Run migrations (jika perlu):
   ```bash
   php artisan migrate:fresh --seed
   ```

### Langkah 3: Test Login API

Jalankan script test untuk memastikan API bekerja:

```powershell
.\test-login.ps1
```

Atau manual:
```bash
cd "MuscleCart Mobile App\backend"
php test_login_api.php
```

**Output yang diharapkan:**
```
=== MuscleCart Login API Test ===

1. Checking if test user exists...
   ✓ Test user found (ID: 8)
   ✓ Password updated to 'password123'

2. Verifying password...
   ✓ Password verification successful

3. Testing token generation...
   ✓ Token generated successfully
   Token: 1|xxx...

4. Simulating login API call...
   ✓ Login successful!
   User ID: 8
   User Name: Test User
   User Email: test@test.com
   User Role: customer

=== Test Summary ===
✓ All tests passed!
```

### Langkah 4: Start Backend Server

Jalankan Laravel development server:

```powershell
cd "MuscleCart Mobile App\backend"
php artisan serve
```

**Server akan berjalan di:**
- `http://localhost:8000` (untuk browser)
- `http://10.0.2.2:8000` (untuk Android emulator)

**Biarkan terminal ini tetap terbuka selama development!**

### Langkah 5: Test di Mobile App

1. Pastikan backend server sudah berjalan (Langkah 4)
2. Build dan jalankan mobile app:
   ```bash
   cd "MuscleCart Mobile App"
   .\gradlew installDebug
   ```

3. Buka app di emulator/device
4. Login dengan credentials:
   - **Email:** `test@test.com`
   - **Password:** `password123`

## Credentials Test

### Customer Account
- Email: `test@test.com`
- Password: `password123`

### Admin Account
- Email: `admin@musclecart.com`
- Password: `admin123`

## Troubleshooting

### Problem: "No connection could be made"
**Solusi:** MySQL belum berjalan
- Buka XAMPP Control Panel
- Start MySQL
- Tunggu hingga status menjadi hijau

### Problem: "Database 'musclecart_db' doesn't exist"
**Solusi:** Database belum dibuat
- Buka phpMyAdmin (http://localhost/phpmyadmin)
- Buat database baru: `musclecart_db`
- Import file: `MuscleCart Mobile App\database\musclecart_db_latest.sql`

### Problem: "Table 'users' doesn't exist"
**Solusi:** Run migrations
```bash
cd "MuscleCart Mobile App\backend"
php artisan migrate:fresh --seed
```

### Problem: Login gagal dengan "Invalid credentials"
**Solusi:** Run test script untuk update password
```bash
cd "MuscleCart Mobile App\backend"
php test_login_api.php
```

### Problem: "Network error" di mobile app
**Solusi:** Backend server belum jalan
- Pastikan `php artisan serve` berjalan
- Pastikan tidak ada error di terminal server
- Cek URL di `NetworkModule.kt`: `http://10.0.2.2:8000/api/v1/`

### Problem: Emulator tidak bisa connect ke localhost
**Solusi:** Gunakan IP khusus emulator
- Android Emulator menggunakan `10.0.2.2` untuk mengakses `localhost` host machine
- Jangan gunakan `localhost` atau `127.0.0.1` di mobile app
- Sudah dikonfigurasi di `NetworkModule.kt`

## File yang Sudah Diperbaiki

1. ✅ `backend/database/seeders/UserSeeder.php` - Fixed password field
2. ✅ `backend/test_login_api.php` - New test script
3. ✅ `fix-login.ps1` - Setup and start script
4. ✅ `test-login.ps1` - Quick test script
5. ✅ Network security config sudah benar
6. ✅ AndroidManifest sudah support clear text traffic

## Quick Start (Singkat)

```powershell
# 1. Start MySQL di XAMPP Control Panel
# 2. Test backend:
.\test-login.ps1

# 3. Start server (buka terminal baru):
cd "MuscleCart Mobile App\backend"
php artisan serve

# 4. Build & install app (terminal baru):
cd "MuscleCart Mobile App"
.\gradlew installDebug

# 5. Login di app:
#    Email: test@test.com
#    Password: password123
```

## Catatan Penting

1. **Backend server harus selalu berjalan** saat testing mobile app
2. **MySQL harus running** di XAMPP
3. **Gunakan emulator** untuk development (karena IP 10.0.2.2)
4. Untuk device fisik, ubah BASE_URL di `NetworkModule.kt` ke IP WiFi komputer (misal: `192.168.1.3`)

## Kontak Support

Jika masih ada masalah, kirimkan:
1. Screenshot error di mobile app
2. Error log dari terminal backend server
3. Output dari `php test_login_api.php`
