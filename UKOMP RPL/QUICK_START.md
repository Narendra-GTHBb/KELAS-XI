# ⚡ QUICK START GUIDE - MuscleCart

## 🎯 Cara Menjalankan Sistem (3 Langkah)

### Step 1: Start MySQL
1. Buka **XAMPP Control Panel**
2. Klik **Start** pada MySQL
3. Pastikan status menjadi **Running** (hijau)

---

### Step 2: Start Admin Panel

**Buka Terminal/CMD** dan jalankan:

```bash
cd "c:\XAAMP\htdocs\UKOMP CODING RPL SMK\musclecart-admin"
php artisan serve --host=127.0.0.1 --port=8001
```

✅ **Admin Panel:** http://127.0.0.1:8001/login

**Login Credentials:**
- **Email:** admin@musclecart.com
- **Password:** admin123

---

### Step 3: Start Mobile Backend API

**Buka Terminal/CMD BARU** dan jalankan:

```bash
cd "c:\XAAMP\htdocs\UKOMP CODING RPL SMK\MuscleCart Mobile App\backend"
php artisan serve --host=127.0.0.1 --port=8000
```

✅ **API Base URL:** http://127.0.0.1:8000/api/v1

---

## 🧪 Test Admin Panel Features

Setelah login, coba:

### ✅ Dashboard
- Lihat statistik: Total Products, Orders, Revenue
- Cek Recent Orders
- Lihat Popular Products

### ✅ Products Management
1. Klik **Products** di sidebar
2. Coba **Add New Product**
3. Edit product yang sudah ada
4. Test search & filter

### ✅ Orders Management
1. Klik **Orders** di sidebar
2. Lihat order list
3. Klik **View** pada salah satu order
4. Update status order: pending → processing → delivered

### ✅ Categories Management
1. Klik **Categories** di sidebar
2. Add new category
3. Edit existing category

### ✅ Users Management
1. Klik **Users** di sidebar
2. Lihat customer list
3. View customer details & order history

---

## 📱 Test Mobile Backend API

### Using Browser/Thunder Client/Postman:

**1. Get All Products:**
```
GET http://127.0.0.1:8000/api/v1/products
```

**2. Get Categories:**
```
GET http://127.0.0.1:8000/api/v1/categories
```

**3. Login (untuk dapat token):**
```
POST http://127.0.0.1:8000/api/v1/login
Body (JSON):
{
    "email": "customer1@example.com",
    "password": "password123"
}
```

**4. Get Cart (dengan token):**
```
GET http://127.0.0.1:8000/api/v1/cart
Header:
Authorization: Bearer {your_token_here}
```

---

## ⚠️ PENTING!

### Kedua Server Harus Berjalan:
- ✅ **Admin Panel** di port **8001**
- ✅ **Mobile API** di port **8000**

### Jangan Tutup Terminal!
Kedua terminal harus tetap terbuka selama development.

### XAMPP MySQL Running
Pastikan MySQL di XAMPP selalu running!

---

## 🎉 What's Next?

Setelah semua berjalan dengan baik:

1. ✅ **Explore Admin Panel** - Familiar dengan semua fitur CRUD
2. ✅ **Test API Endpoints** - Gunakan Postman/Thunder Client
3. 🚀 **Develop Mobile App** - Integrate API ke Android app
4. 🎨 **Customize** - Sesuaikan design & features

---

## 🆘 Quick Troubleshooting

**Server tidak mau start?**
```bash
# Cek port usage
netstat -ano | findstr :8001
netstat -ano | findstr :8000
```

**Database error?**
```bash
# Re-migrate database
php artisan migrate:fresh --seed
```

**Cache issues?**
```bash
php artisan cache:clear
php artisan config:clear
php artisan route:clear
```

---

**Happy Coding! 🚀**
