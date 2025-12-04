# Weather App - Setup Instructions

## ⚠️ PENTING: Cara Mendapatkan API Key

API key yang Anda masukkan saat ini **SALAH**. Ikuti langkah berikut:

### 1. Daftar di WeatherAPI.com (GRATIS)

1. Buka browser dan kunjungi: **https://www.weatherapi.com/signup.aspx**
2. Isi form pendaftaran:
   - Email address
   - Password
   - Confirm password
3. Klik tombol **"Sign Up"**
4. Cek email Anda untuk verifikasi (jika diminta)

### 2. Dapatkan API Key

1. Login ke dashboard: **https://www.weatherapi.com/login.aspx**
2. Setelah login, Anda akan langsung melihat **API Key** Anda di halaman dashboard
3. Copy API Key tersebut (format: huruf dan angka panjang, contoh: `a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6`)

### 3. Masukkan API Key ke Aplikasi

1. Buka file: `WeatherViewModel.kt`
2. Cari baris:
   ```kotlin
   private val API_KEY = "4ebe1b6b5ad5d2e327aec81480a44743"
   ```
3. Ganti dengan API key yang baru Anda dapatkan:
   ```kotlin
   private val API_KEY = "API_KEY_ANDA_DISINI"
   ```
4. Save file (Ctrl + S)

### 4. Build Ulang Aplikasi

1. Klik menu **Build → Rebuild Project**
2. Tunggu hingga selesai
3. Jalankan aplikasi dengan klik tombol **Run** (▶️)

### 5. Test Aplikasi

1. Ketik nama kota di search bar (contoh: "London", "Jakarta", "New York")
2. Klik icon search (🔍)
3. Data cuaca seharusnya muncul!

---

## 🐛 Troubleshooting

### Jika masih muncul "Failed to load data":

1. **Cek Logcat** di Android Studio:

   - Klik tab "Logcat" di bagian bawah
   - Filter dengan "WeatherViewModel"
   - Lihat error message detail

2. **Kemungkinan Masalah:**

   - ❌ API key salah atau tidak valid
   - ❌ Tidak ada koneksi internet di emulator/device
   - ❌ API key belum diverifikasi

3. **Solusi:**
   - Pastikan API key dari weatherapi.com (bukan dari API lain)
   - Cek koneksi internet di emulator
   - Coba city name dalam bahasa Inggris (London, Paris, Tokyo)

---

## 📱 Fitur Aplikasi

✅ Search bar untuk cari lokasi  
✅ Tampilan temperature dalam Celsius  
✅ Icon cuaca dinamis  
✅ Detail: Humidity, Wind Speed, UV, Precipitation  
✅ Local Time & Date  
✅ Loading indicator  
✅ Error handling

---

## 🔗 Link Penting

- WeatherAPI Signup: https://www.weatherapi.com/signup.aspx
- WeatherAPI Dashboard: https://www.weatherapi.com/my/
- WeatherAPI Documentation: https://www.weatherapi.com/docs/

---

**Catatan:** Free plan WeatherAPI memberikan:

- 1,000,000 calls per bulan
- Cukup untuk development dan testing
- Tidak perlu kartu kredit
