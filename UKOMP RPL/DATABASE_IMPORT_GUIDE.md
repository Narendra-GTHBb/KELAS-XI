# 🔧 PANDUAN IMPORT DATABASE YANG AMAN

## ⚠️ MASALAH YANG DITEMUKAN

Database baru dari laptop (`musclecart_db (1).sql`) **LEBIH LENGKAP** daripada database lama di project. Jika tidak di-import dengan benar, project akan crash.

---

## 📊 PERBANDINGAN DATABASE

### ✅ Database Baru (musclecart_db_latest.sql) - **GUNAKAN INI**
- **Export Date:** 25 Feb 2026, 15:36
- **Tabel Baru:** `favorites` ← Fitur baru yang ditambahkan di laptop
- **Total Users:** 7 users (termasuk user test)
- **Products:** 4 products (ID: 45, 46, 47, 48)
- **Categories:** 8 categories (ID: 25-32)
- **Orders:** 35 orders dengan data lengkap
- **Cart Items:** 3 items aktif
- **Auth Tokens:** 1 token aktif
- **Laravel Migrations:** 15 migrations lengkap

### ❌ Database Lama (musclecart_mysql.sql) - **JANGAN GUNAKAN**
- Database setup awal saja
- **Tidak ada tabel `favorites`** ← Mobile app akan error!
- Sample data saja (5 products, 4 categories)
- Product ID berbeda (1-5)
- Tidak ada data real

---

## 🚨 RISIKO JIKA PAKAI DATABASE LAMA

1. ❌ **App akan crash** - tabel `favorites` tidak ada
2. ❌ **Login gagal** - personal_access_tokens berbeda
3. ❌ **Products tidak muncul** - data berbeda
4. ❌ **Cart kosong** - data cart hilang
5. ❌ **Favorites error** - fitur hilang

---

## ✅ LANGKAH-LANGKAH IMPORT YANG BENAR

### **OPSI 1: Import via phpMyAdmin (DIREKOMENDASIKAN)**

1. **Backup Database Lama (Opsional)**
   ```
   - Buka phpMyAdmin: http://localhost/phpmyadmin
   - Pilih database `musclecart_db` (jika ada)
   - Klik tab "Export"
   - Klik "Go" untuk download backup
   - Simpan sebagai `musclecart_db_backup_[tanggal].sql`
   ```

2. **Hapus Database Lama**
   ```
   - Di phpMyAdmin, pilih database `musclecart_db`
   - Klik tab "Operations"
   - Scroll ke bawah
   - Klik "Drop the database (DROP)" 
   - Konfirmasi "OK"
   ```

3. **Import Database Baru**
   ```
   - Klik "New" di sidebar kiri
   - Nama database: musclecart_db
   - Collation: utf8mb4_unicode_ci
   - Klik "Create"
   - Pilih database `musclecart_db` yang baru dibuat
   - Klik tab "Import"
   - Klik "Choose File"
   - Pilih: C:\XAAMP\htdocs\UKOMP CODING RPL SMK\MuscleCart Mobile App\database\musclecart_db_latest.sql
   - Klik "Go"
   - Tunggu sampai selesai
   ```

4. **Verifikasi Import Berhasil**
   ```
   - Cek tabel: pastikan ada tabel `favorites`
   - Cek data users: harus ada 7 users
   - Cek products: harus ada 4 products
   - Cek favorites: harus ada 4 records
   ```

---

### **OPSI 2: Import via Command Line (CEPAT)**

1. **Backup Database Lama**
   ```powershell
   cd C:\xampp\mysql\bin
   .\mysqldump.exe -u root -p musclecart_db > "C:\XAAMP\htdocs\UKOMP CODING RPL SMK\backup_old_db.sql"
   ```

2. **Drop & Import Database Baru**
   ```powershell
   # Drop database lama
   .\mysql.exe -u root -p -e "DROP DATABASE IF EXISTS musclecart_db; CREATE DATABASE musclecart_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
   
   # Import database baru
   .\mysql.exe -u root -p musclecart_db < "C:\XAAMP\htdocs\UKOMP CODING RPL SMK\MuscleCart Mobile App\database\musclecart_db_latest.sql"
   ```

---

## 🔍 VERIFIKASI SETELAH IMPORT

Jalankan query ini di phpMyAdmin untuk memastikan semua benar:

```sql
-- 1. Cek tabel favorites ada
SELECT COUNT(*) as total_favorites FROM favorites;
-- Expected: 4

-- 2. Cek total products
SELECT COUNT(*) as total_products FROM products;
-- Expected: 4

-- 3. Cek total users
SELECT COUNT(*) as total_users FROM users;
-- Expected: 7

-- 4. Cek categories
SELECT COUNT(*) as total_categories FROM categories;
-- Expected: 8

-- 5. Cek migrations terakhir
SELECT * FROM migrations ORDER BY id DESC LIMIT 5;
-- Expected: Harus ada migration 'create_favorites_table'

-- 6. Cek cart items
SELECT * FROM cart_items;
-- Expected: 3 items untuk user_id = 8
```

---

## 🧪 TEST APLIKASI SETELAH IMPORT

### **1. Test Backend Laravel**
```powershell
cd "C:\XAAMP\htdocs\UKOMP CODING RPL SMK\MuscleCart Mobile App\backend"
php artisan migrate:status
# Semua migration harus "Ran"
```

### **2. Test Mobile App**
- Login dengan: test@test.com / password123
- Cek apakah products muncul
- Cek apakah favorites berfungsi
- Cek apakah cart berfungsi

### **3. Test Admin Panel**
```
- URL: http://localhost:8000/admin
- Login: admin@musclecart.com / admin123
- Cek products list
- Cek orders
```

---

## 🆘 TROUBLESHOOTING

### **Error: "Table 'favorites' doesn't exist"**
```sql
-- Jalankan migration manual
CREATE TABLE `favorites` (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) UNSIGNED NOT NULL,
  `product_id` bigint(20) UNSIGNED NOT NULL,
  `created_at` bigint(20) DEFAULT NULL,
  `updated_at` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `favorites_user_id_product_id_unique` (`user_id`,`product_id`),
  KEY `favorites_product_id_foreign` (`product_id`),
  KEY `favorites_user_id_index` (`user_id`),
  CONSTRAINT `favorites_product_id_foreign` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE,
  CONSTRAINT `favorites_user_id_foreign` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

### **Error: "Access denied for user"**
```
- Pastikan XAMPP MySQL sudah running
- Default user: root, password: (kosong)
- Atau cek config di: backend/.env
```

### **Error: Import timeout**
```
- Import file terlalu besar
- Edit php.ini:
  max_execution_time = 300
  post_max_size = 128M
  upload_max_filesize = 128M
- Restart Apache
```

---

## 📝 DATA USERS YANG ADA

Setelah import, gunakan kredensial ini untuk testing:

### **Admin:**
- Email: `admin@musclecart.com`
- Password: `admin123`

### **Test User (Mobile App):**
- Email: `test@test.com`
- Password: `password123`

### **Customers:**
- customer1@example.com sampai customer5@example.com
- Password: `password123`

---

## ✅ KESIMPULAN

**GUNAKAN DATABASE BARU** (`musclecart_db_latest.sql`) karena:
- ✅ Punya tabel `favorites` yang diperlukan mobile app
- ✅ Data lengkap dan terbaru dari laptop
- ✅ Struktur Laravel complete dengan migrations
- ✅ Ada auth tokens dan sessions aktif

**File Location:**
```
C:\XAAMP\htdocs\UKOMP CODING RPL SMK\MuscleCart Mobile App\database\musclecart_db_latest.sql
```

---

## 🔄 JIKA ADA MASALAH

Jika setelah import masih ada error:
1. Check Laravel logs: `backend/storage/logs/laravel.log`
2. Clear Laravel cache:
   ```powershell
   php artisan config:clear
   php artisan cache:clear
   php artisan route:clear
   ```
3. Regenerate Laravel key jika perlu:
   ```powershell
   php artisan key:generate
   ```

---

**Dibuat pada:** 25 Feb 2026
**Status:** ✅ Database baru sudah dicopy ke project folder
