# Test Add Product - Debugging Guide

## Langkah Debugging:

### 1. Pastikan Storage Link Sudah Dibuat
Jalankan command ini di terminal (di folder musclecart-admin):
```bash
php artisan storage:link
```

### 2. Buka Browser Console
- Tekan **F12** di browser
- Pilih tab **Console**

### 3. Test Add Product
1. Klik tombol **Add Product**
2. Isi semua field REQUIRED (bertanda *):
   - **Product Name**: Test Product
   - **Category**: Pilih salah satu
   - **Price**: 100000
   - **Description**: Test description
   - **Stock Quantity**: 10

3. Klik tombol **Add Product**

### 4. Lihat Console Log
Di console akan muncul log lengkap:
- Form data yang dikirim
- Response status dari server
- Response content
- Error jika ada

### 5. Lihat Alert
Akan muncul alert yang memberitahu:
- **Success**: Product berhasil dibuat
- **Validation Error**: Field apa yang error
- **Network Error**: Ada masalah koneksi
- **JSON Error**: Server tidak mengembalikan JSON yang valid

### 6. Cek Laravel Log (Jika Masih Error)
Buka file: `musclecart-admin/storage/logs/laravel.log`

Atau jalankan command:
```bash
Get-Content storage/logs/laravel.log -Tail 100
```

### 7. Possible Issues & Solutions:

#### Issue: "CSRF token mismatch"
**Solution**: Refresh halaman dan coba lagi

#### Issue: "Validation failed on category_id"
**Solution**: Pastikan ada categories di database
```bash
php artisan db:seed --class=CategorySeeder
```

#### Issue: "Server returned HTML instead of JSON"
**Solution**: Ada error 500 di Laravel, cek laravel.log

#### Issue: "Network error"
**Solution**: 
- Pastikan Apache/XAMPP running
- Cek URL di browser masih bisa diakses

### 8. Manual Test via Postman (Alternative)
Jika form masih error, test manual:

**URL**: `http://localhost/admin/products`
**Method**: POST
**Headers**:
- Accept: application/json
- X-Requested-With: XMLHttpRequest

**Body** (form-data):
- name: Test Product
- description: Test description
- price: 100000
- stock_quantity: 10
- category_id: 1
- is_active: 1

Lihat response nya, harus return JSON dengan success: true
