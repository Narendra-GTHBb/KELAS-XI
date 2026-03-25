# 📊 PERBANDINGAN DATABASE - ANALISIS DETAIL

## 🔍 RINGKASAN EKSEKUTIF

Database yang baru didownload dari laptop **HARUS DIGUNAKAN** karena memiliki fitur dan data yang lebih lengkap. Database lama di project hanya file setup awal dan akan menyebabkan crash jika digunakan.

---

## 📁 FILE LOCATIONS

| Database | Path | Status |
|----------|------|--------|
| **Database Baru (GUNAKAN)** | `C:\XAAMP\htdocs\UKOMP CODING RPL SMK\MuscleCart Mobile App\database\musclecart_db_latest.sql` | ✅ **READY TO IMPORT** |
| **Database Lama (JANGAN)** | `C:\XAAMP\htdocs\UKOMP CODING RPL SMK\MuscleCart Mobile App\database\musclecart_mysql.sql` | ❌ **OUTDATED** |

---

## 🆚 PERBANDINGAN STRUKTUR TABEL

### ✅ Database Baru (musclecart_db_latest.sql)

| # | Tabel | Records | Keterangan |
|---|-------|---------|------------|
| 1 | `cache` | 0 | Laravel cache system |
| 2 | `cache_locks` | 0 | Laravel cache locks |
| 3 | `cart_items` | **3** | Cart aktif user_id 8 |
| 4 | `categories` | **8** | Categories lengkap (ID: 25-32) |
| 5 | `failed_jobs` | 0 | Laravel queue system |
| 6 | **`favorites`** | **4** | ⭐ **TABEL BARU! Mobile app butuh ini** |
| 7 | `jobs` | 0 | Laravel jobs |
| 8 | `job_batches` | 0 | Laravel job batches |
| 9 | `migrations` | **15** | Laravel migrations history |
| 10 | `orders` | **35** | Order history lengkap |
| 11 | `order_items` | 0 | Order items detail |
| 12 | `password_reset_tokens` | 0 | Password reset |
| 13 | `personal_access_tokens` | **1** | API authentication tokens |
| 14 | `products` | **4** | Products Evolene (ID: 45-48) |
| 15 | `sessions` | **2** | Active sessions |
| 16 | `users` | **7** | Users lengkap + test user |

**Total: 16 tabel**

---

### ❌ Database Lama (musclecart_mysql.sql)

| # | Tabel | Records | Keterangan |
|---|-------|---------|------------|
| 1 | `users` | Sample | Struktur sederhana, tidak ada role/city/postal |
| 2 | `categories` | **4** | Sample data (ID: 1-4) |
| 3 | `products` | **5** | Sample data (ID: 1-5) |
| 4 | `orders` | Sample | Struktur sederhana |
| 5 | `order_items` | Sample | Struktur sederhana |
| 6 | `cart_items` | Sample | Struktur sederhana |

**Total: 6 tabel (basic setup only)**

❌ **TIDAK ADA:**
- `favorites` table (CRITICAL!)
- `migrations` table
- `personal_access_tokens` table
- `sessions` table
- `cache` tables
- Laravel system tables

---

## 🔴 MASALAH JIKA PAKAI DATABASE LAMA

### 1. **CRASH: Tabel `favorites` tidak ada**
```
SQLSTATE[42S02]: Base table or view not found: 1146 Table 'musclecart_db.favorites' doesn't exist
```
Mobile app akan crash karena fitur favorites tidak bisa jalan.

### 2. **LOGIN ERROR: Token tidak valid**
```
Unauthenticated
```
Personal access tokens berbeda, user tidak bisa login.

### 3. **PRODUCTS KOSONG: ID berbeda**
```
Database lama: Products ID 1-5
Database baru: Products ID 45-48
```
Query akan mencari product yang tidak ada.

### 4. **CART HILANG**
```
Cart items user akan hilang
```
Data cart yang sudah ada akan hilang.

### 5. **MIGRATION ERROR**
```
php artisan migrate:status
Migration file not found
```
Laravel migrations tidak match dengan database.

---

## 📋 DATA DETAIL COMPARISON

### **USERS**

#### Database Baru (7 users):
```
ID | Name              | Email                   | Role     | Status
---|-------------------|-------------------------|----------|--------
1  | Admin MuscleCart  | admin@musclecart.com    | admin    | Active
2  | Customer 1        | customer1@example.com   | customer | Active
3  | Customer 2        | customer2@example.com   | customer | Active
4  | Customer 3        | customer3@example.com   | customer | Active
5  | Customer 4        | customer4@example.com   | customer | Active
6  | Customer 5        | customer5@example.com   | customer | Active
8  | Test User         | test@test.com           | customer | Active ←NEW
```

#### Database Lama:
```
Sample users only (tidak ada data real)
```

---

### **PRODUCTS**

#### Database Baru (4 products - REAL DATA):
```
ID | Name                                     | Price      | Stock | Category
---|------------------------------------------|------------|-------|----------
45 | Evolene Evomass 2lbs/912gr              | 274,000    | 10    | 30
46 | Evolene Isolene 12 Sachet/396gr         | 294,000    | 20    | 30
47 | Evolene Evowhey Protein 50S/1750gr      | 884,000    | 35    | 30
48 | Evolene Crevolene Creapure              | 224,000    | 15    | 30
```

#### Database Lama (5 products - SAMPLE DATA):
```
ID | Name                      | Price      | Stock | Category
---|---------------------------|------------|-------|----------
1  | Professional Treadmill    | 1,299.99   | 15    | 1
2  | Exercise Bike             | 599.99     | 25    | 1
3  | Olympic Barbell Set       | 899.99     | 10    | 2
4  | Adjustable Dumbbells      | 399.99     | 30    | 2
5  | Power Rack                | 1,599.99   | 5     | 2
```

⚠️ **KONFLIK:** Product ID tidak match! Mobile app akan error.

---

### **CATEGORIES**

#### Database Baru (8 categories):
```
ID | Name                 | Description                                    | Active
---|----------------------|------------------------------------------------|--------
25 | Cardio Equipment     | Cardiovascular exercise machines...            | Yes
26 | Strength Training    | Weight training equipment...                   | Yes
27 | Free Weights         | Dumbbells, barbells, and weight plates...      | Yes
28 | Fitness Accessories  | Supporting equipment...                        | Yes
29 | Home Gym             | Complete gym solutions...                      | Yes
30 | Supplements          | Nutritional supplements...                     | Yes
31 | Yoga & Pilates       | Equipment for mindful movement...              | Yes
32 | Outdoor Fitness      | Equipment for outdoor workouts...              | Yes
```

#### Database Lama (4 categories):
```
ID | Name               | Description
---|--------------------|---------------------------------
1  | Cardio Equipment   | Treadmills, bikes...
2  | Strength Training  | Weights, barbells...
3  | Supplements        | Protein powders...
4  | Accessories        | Gym bags...
```

⚠️ **KONFLIK:** Category ID tidak match!

---

### **FAVORITES** (⭐ TABEL BARU!)

#### Database Baru:
```
ID | User ID | Product ID | Created At
---|---------|------------|------------
7  | 8       | 45         | NULL
8  | 8       | 46         | NULL
9  | 8       | 47         | NULL
10 | 8       | 48         | NULL
```

#### Database Lama:
```
❌ TABEL TIDAK ADA!
```

---

### **CART ITEMS**

#### Database Baru:
```
ID | User ID | Product ID | Quantity | Price      | Updated
---|---------|------------|----------|------------|------------------
1  | 8       | 45         | 5        | 274,000    | 2026-02-24 22:26
2  | 8       | 46         | 1        | 294,000    | 2026-02-25 00:00
3  | 8       | 47         | 2        | 884,000    | 2026-02-25 00:07
```

#### Database Lama:
```
No data (struktur basic saja)
```

---

### **ORDERS**

#### Database Baru: **35 orders** dengan detail lengkap
```
- Order IDs: 1-35
- Date range: Dec 2025 - Feb 2026
- Various status: pending, processing, shipped, delivered, cancelled
- Complete shipping/billing addresses
- Payment methods: cash, transfer, credit_card, e_wallet
```

#### Database Lama:
```
No data (struktur basic saja)
```

---

## 🔄 MIGRATIONS HISTORY

### Database Baru (15 migrations):
```
1.  0001_01_01_000000_create_users_table
2.  0001_01_01_000001_create_cache_table
3.  0001_01_01_000002_create_jobs_table
4.  2026_02_18_115434_create_categories_table
5.  2026_02_18_115441_create_products_table
6.  2026_02_18_115442_create_orders_table
7.  2026_02_18_115443_create_cart_items_table
8.  2026_02_18_115444_create_order_items_table
9.  2026_02_18_120416_create_personal_access_tokens_table
10. 2026_02_20_074026_remove_image_from_categories_table
11. 2026_02_21_074250_add_low_stock_threshold_to_products_table
12. 2026_02_21_190517_update_products_weight_precision
13. 2026_02_18_115441_create_cart_items_table (re-run)
14. 2026_02_18_115442_create_order_items_table (re-run)
15. 2026_02_25_000004_create_favorites_table ← BARU!
```

### Database Lama:
```
❌ Tidak ada migration history
```

---

## 🎯 KESIMPULAN & REKOMENDASI

### ✅ **WAJIB PAKAI DATABASE BARU** karena:

1. ✅ **Tabel `favorites` ada** → Mobile app tidak crash
2. ✅ **Data real dari laptop** → Products lengkap
3. ✅ **Auth tokens valid** → Login berfungsi
4. ✅ **Cart data preserved** → Data tidak hilang
5. ✅ **Migrations lengkap** → Laravel sync
6. ✅ **Orders history** → Data historis tersimpan
7. ✅ **Sessions aktif** → Admin panel berfungsi

### ❌ **JANGAN PAKAI DATABASE LAMA** karena:

1. ❌ **Tidak ada tabel `favorites`** → App crash
2. ❌ **Sample data saja** → Tidak real
3. ❌ **Struktur outdated** → Tidak match dengan code
4. ❌ **ID tidak match** → Query error
5. ❌ **Tidak ada migrations** → Laravel error

---

## 🚀 CARA IMPORT

### **OPSI 1: Otomatis (DIREKOMENDASIKAN)**
```powershell
cd "C:\XAAMP\htdocs\UKOMP CODING RPL SMK"
.\import-database.ps1
```

### **OPSI 2: Manual**
Ikuti panduan di: [DATABASE_IMPORT_GUIDE.md](DATABASE_IMPORT_GUIDE.md)

---

## 📞 SUPPORT

Jika ada masalah setelah import:
- Check Laravel logs: `backend/storage/logs/laravel.log`
- Run: `php artisan migrate:status`
- Clear cache: `php artisan cache:clear`

---

**Last Updated:** 25 Feb 2026, 15:36
**Status:** ✅ Database baru siap digunakan
