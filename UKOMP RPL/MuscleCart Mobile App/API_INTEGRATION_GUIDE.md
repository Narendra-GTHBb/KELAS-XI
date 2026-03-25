# ✅ API Integration Fixed!

## 🎉 Yang Sudah Diperbaiki:

### 1. **Network Configuration** ✓
- ✅ Base URL updated: `http://10.0.2.2:8000/api/v1/`
- ✅ Timeout extended: 30 seconds (dari 5 detik)
- ✅ Auth Interceptor ditambahkan untuk token management

### 2. **API Services** ✓
- ✅ **ProductApiService** - dengan search & filter
- ✅ **CategoryApiService** - dengan get products by category
- ✅ **OrderApiService** - dengan order details
- ✅ **AuthApiService** - NEW! (login, register, profile)
- ✅ **CartApiService** - NEW! (cart management)

### 3. **DTOs Updated** ✓
- ✅ **ProductDto** - match dengan Laravel (stock_quantity, category, is_featured, dll)
- ✅ **CategoryDto** - match dengan Laravel (is_active)
- ✅ **OrderDto** - lengkap dengan order_number, payment, shipping
- ✅ **AuthDto** - NEW! (LoginRequest, RegisterRequest, LoginResponse, UserDto)
- ✅ **CartDto** - NEW! (cart items dengan subtotal)
- ✅ **ApiResponse** - NEW! (generic response wrapper)

### 4. **Authentication** ✓
- ✅ **TokenManager** - untuk simpan/retrieve auth token
- ✅ **AuthInterceptor** - otomatis inject token ke header

---

## 🚀 Cara Test Mobile App

### Step 1: Jalankan Backend API

Buka **Terminal/CMD**:
```bash
cd "c:\XAAMP\htdocs\UKOMP CODING RPL SMK\MuscleCart Mobile App\backend"
php artisan serve --host=0.0.0.0 --port=8000
```

**PENTING:** Gunakan `0.0.0.0` bukan `127.0.0.1` supaya bisa diakses dari emulator!

### Step 2: Verify Backend Running

Test di browser:
```
http://localhost:8000/api/v1/products
```

Harusnya return JSON list products.

### Step 3: Open Project di Android Studio

1. Buka **Android Studio**
2. Open Project: `MuscleCart Mobile App/`
3. Wait for Gradle sync
4. Pastikan tidak ada error

### Step 4: Run di Emulator

1. Klik **Run** (▶️) button
2. Pilih emulator atau device
3. Wait for app to install & launch

### Step 5: Test Features

#### **A. Test Products**
- Buka app → Home screen
- Products dari database harusnya muncul
- Coba click product untuk detail

#### **B. Test Login**
- Klik Login
- Gunakan credentials:
  - Email: `customer1@example.com`
  - Password: `password123`
- Harusnya bisa login dan dapat token

#### **C. Test Cart** (setelah login)
- Add product to cart
- View cart
- Update quantity

#### **D. Test Orders** (setelah login)
- Place order dari cart
- View order history

---

## 🔍 Troubleshooting

### Error: "Unable to resolve host" atau Connection Refused

**Problem:** Emulator tidak bisa connect ke backend

**Solution:**
```bash
# 1. Pastikan backend running dengan 0.0.0.0:
php artisan serve --host=0.0.0.0 --port=8000

# 2. Verify dari komputer:
curl http://localhost:8000/api/v1/products

# 3. Di Android Emulator, gunakan:
#    - 10.0.2.2 untuk localhost (sudah diset di NetworkModule)
```

### Error: HTTP 404 Not Found

**Problem:** Route tidak ditemukan

**Solution:**
```bash
# Check routes di backend:
cd backend
php artisan route:list --path=api

# Pastikan ada:
# GET|HEAD  api/v1/products
# GET|HEAD  api/v1/categories
# POST      api/v1/login
# etc.
```

### Error: Unauthorized (401)

**Problem:** Token tidak valid atau expired

**Solution:**
- Login ulang untuk get fresh token
- Check AuthInterceptor sudah inject token dengan benar
- Check di Logcat untuk lihat request headers

### Data tidak muncul / Empty List

**Problem:** Database kosong atau API error

**Solution:**
```bash
# Re-seed database:
cd musclecart-admin
php artisan migrate:fresh --seed
```

---

## 📊 API Endpoints Available

### **Public** (Tidak perlu login):
```
GET  /api/v1/products
GET  /api/v1/products/{id}
GET  /api/v1/categories
GET  /api/v1/categories/{id}/products
POST /api/v1/register
POST /api/v1/login
```

### **Protected** (Perlu auth token):
```
GET    /api/v1/user
PUT    /api/v1/user/profile
POST   /api/v1/logout
GET    /api/v1/cart
POST   /api/v1/cart/add
PUT    /api/v1/cart/update/{id}
DELETE /api/v1/cart/remove/{id}
DELETE /api/v1/cart/clear
GET    /api/v1/orders
POST   /api/v1/orders
GET    /api/v1/orders/{id}
```

---

## 📱 Testing Checklist

Setelah app running, test ini:

- [ ] App launch tanpa crash
- [ ] Home screen load products
- [ ] Product list tampil dengan benar
- [ ] Product detail bisa dibuka
- [ ] Register account baru
- [ ] Login dengan account
- [ ] Token tersimpan (cek di Logcat)
- [ ] Add to cart working
- [ ] Cart count update
- [ ] View cart items
- [ ] Update cart quantity
- [ ] Remove from cart
- [ ] Checkout process
- [ ] View order history
- [ ] Profile screen
- [ ] Logout working

---

## 🎯 Next Steps

Setelah basic API integration working:

1. **Polish UI/UX** - Loading states, error messages
2. **Add Validation** - Form validation
3. **Image Handling** - Product images dari server
4. **Offline Support** - Room database caching
5. **Push Notifications** - Order updates
6. **Payment Integration** - Midtrans or other

---

## 💡 Tips Development

### Debug Network Calls

Di Android Studio **Logcat**, filter by:
```
OkHttp
```

Akan terlihat semua API request/response.

### Check Token

Di Logcat, cari:
```
Authorization: Bearer
```

### Monitor Backend

Di terminal backend, akan terlihat setiap request masuk:
```
[2026-02-19 19:00:00] local.INFO: GET /api/v1/products
```

---

**Status:** ✅ READY TO TEST!

Silakan run app di Android Studio dan test koneksi ke backend! 🚀
