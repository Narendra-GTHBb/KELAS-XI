# PENTING: README - Penjelasan 2 Project

## 🏗️ Struktur Project

Workspace ini memiliki **2 project Laravel yang BERBEDA**:

### 1. 📱 Mobile App Backend
- **Folder:** `MuscleCart Mobile App/backend/`
- **Fungsi:** Backend API untuk mobile app Android
- **Tampilan:** Warna **ORANGE** (jika punya web interface)
- **Port default:** 8000
- **Status:** Aktif sejak login fix

### 2. 💼 Admin Panel (Web Admin)
- **Folder:** `musclecart-admin/`
- **Fungsi:** Dashboard admin untuk kelola produk, order, dll
- **Tampilan:** Warna **BIRU-UNGU gradient** ✨
- **Port:** 8001 (atau port lain selain 8000)
- **Credentials:**
  - Email: `admin@musclecart.com`
  - Password: `admin123`

---

## ❌ Masalah yang Terjadi

Anda membuka **URL yang salah**:
- ❌ `http://127.0.0.1:8000` → Mobile backend (ORANGE)
- ✅ `http://127.0.0.1:8001` → Admin panel (BIRU-UNGU) ← **INI YANG BENAR!**

---

## ✅ Cara Menjalankan Admin Panel

### Opsi 1: Menggunakan Script (RECOMMENDED)
```powershell
.\start-admin.ps1
```
Lalu buka: **http://127.0.0.1:8001**

### Opsi 2: Manual
```powershell
cd musclecart-admin
php artisan serve --port=8001
```
Lalu buka: **http://127.0.0.1:8001**

---

## 🔧 Cara Stop Server yang Salah

Jika ada server lain yang menggunakan port 8001:
```powershell
# Lihat semua PHP process
Get-Process php

# Stop semua PHP server
Get-Process php | Stop-Process -Force

# Lalu start admin panel lagi
.\start-admin.ps1
```

---

## 📊 Port Assignment

| Project | Port | URL | Warna |
|---------|------|-----|-------|
| Mobile Backend | 8000 | http://127.0.0.1:8000 | 🟠 Orange |
| Admin Panel | 8001 | http://127.0.0.1:8001 | 🔵 Biru-Ungu |

---

## 🎯 Quick Access

**Admin Panel (BIRU-UNGU):**
```
URL: http://127.0.0.1:8001/login
Email: admin@musclecart.com
Password: admin123
```

**Mobile Backend API:**
```
URL: http://127.0.0.1:8000/api/v1/
Test user: test@test.com
Password: password123
```

---

## ⚠️ CATATAN PENTING

**TIDAK ADA FILE YANG BERUBAH!**
- ✅ Admin panel Anda tetap sama (biru-ungu)
- ✅ Desain tidak hilang
- ✅ Yang berubah hanya server yang sedang berjalan

Masalahnya cuma **URL yang salah dibuka!**

Gunakan **port 8001** untuk admin panel Anda! 🎯
