# ⚠️ DATABASE URGENT NOTICE

## 🚨 MASALAH TERDETEKSI!

Database yang baru didownload dari laptop **BERBEDA** dengan database lama di project!

---

## 📊 QUICK COMPARISON

| Aspect | Database BARU (dari laptop) | Database LAMA (di project) |
|--------|----------------------------|---------------------------|
| **File** | `musclecart_db (1).sql` | `musclecart_mysql.sql` |
| **Export Date** | ✅ 25 Feb 2026, 15:36 | ❌ Setup file awal |
| **Tables** | ✅ 16 tables | ❌ 6 tables |
| **Favorites Table** | ✅ **ADA** (PENTING!) | ❌ **TIDAK ADA** |
| **Products** | ✅ 4 products (ID: 45-48) | ❌ 5 sample products (ID: 1-5) |
| **Users** | ✅ 7 users + test user | ❌ Sample users |
| **Orders** | ✅ 35 orders real | ❌ Sample data |
| **Cart Data** | ✅ 3 items aktif | ❌ No data |
| **Auth Tokens** | ✅ Valid tokens | ❌ No tokens |
| **Migrations** | ✅ 15 migrations | ❌ No migrations |
| **Status** | ✅ **GUNAKAN INI!** | ❌ **JANGAN GUNAKAN!** |

---

## 🔴 APA YANG TERJADI JIKA PAKAI DATABASE LAMA?

1. ❌ **Mobile App CRASH** → Tabel `favorites` tidak ada
2. ❌ **Login GAGAL** → Token tidak valid
3. ❌ **Products KOSONG** → ID tidak match
4. ❌ **Cart HILANG** → Data cart hilang
5. ❌ **Laravel ERROR** → Migrations tidak match

---

## ✅ SOLUSI: IMPORT DATABASE BARU!

### 🎯 **LANGKAH CEPAT** (Rekomendasi):

1. **Buka PowerShell di folder project**
2. **Jalankan script otomatis:**
   ```powershell
   cd "C:\XAAMP\htdocs\UKOMP CODING RPL SMK"
   .\import-database.ps1
   ```
3. **Ikuti instruksi di layar**
4. **Done!** ✅

### 📖 **ATAU: Import Manual via phpMyAdmin**

1. Buka: http://localhost/phpmyadmin
2. Drop database lama: `musclecart_db`
3. Create database baru: `musclecart_db`
4. Import file: `MuscleCart Mobile App\database\musclecart_db_latest.sql`
5. Done! ✅

---

## 📍 LOKASI FILE

Database baru sudah dicopy ke:
```
C:\XAAMP\htdocs\UKOMP CODING RPL SMK\MuscleCart Mobile App\database\musclecart_db_latest.sql
```

---

## 🔐 LOGIN SETELAH IMPORT

### Admin Panel:
- URL: http://localhost:8000/admin
- Email: `admin@musclecart.com`
- Password: `admin123`

### Mobile App:
- Email: `test@test.com`
- Password: `password123`

---

## 📚 DOKUMENTASI LENGKAP

- **Panduan Import:** [DATABASE_IMPORT_GUIDE.md](DATABASE_IMPORT_GUIDE.md)
- **Perbandingan Detail:** [DATABASE_COMPARISON.md](DATABASE_COMPARISON.md)

---

## ⏱️ ETA: ~2 menit

Import database hanya butuh 2 menit dengan script otomatis!

---

## 🆘 BUTUH BANTUAN?

Jika ada error setelah import:
```powershell
cd "C:\XAAMP\htdocs\UKOMP CODING RPL SMK\MuscleCart Mobile App\backend"
php artisan cache:clear
php artisan config:clear
php artisan migrate:status
```

---

**🚀 JANGAN TUNDA! Import database baru sekarang untuk hindari crash!**

**Status:** ✅ Database baru siap digunakan  
**Action Required:** ⚠️ Import sekarang!
