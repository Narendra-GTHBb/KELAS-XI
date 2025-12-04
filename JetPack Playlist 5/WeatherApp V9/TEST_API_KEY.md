# Test API Key - Weather App

## Cara Cek API Key Anda Valid atau Tidak

### Method 1: Test di Browser

Buka URL ini di browser (ganti YOUR_API_KEY dengan API key Anda):

```
https://api.weatherapi.com/v1/current.json?key=YOUR_API_KEY&q=London
```

**Jika BERHASIL**, Anda akan melihat JSON response seperti:

```json
{
  "location": {
    "name": "London",
    "country": "United Kingdom",
    ...
  },
  "current": {
    "temp_c": 8.0,
    ...
  }
}
```

**Jika GAGAL**, Anda akan melihat error:

```json
{
  "error": {
    "code": 2006,
    "message": "API key is invalid."
  }
}
```

---

## Contoh API Key yang BENAR dari WeatherAPI.com

Format API key weatherapi.com biasanya:

- Panjang: 32 karakter
- Kombinasi huruf dan angka
- Contoh: `a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6`

---

## API Key yang Anda Pakai Sekarang

```
4ebe1b6b5ad5d2e327aec81480a44743
```

**❌ INI BUKAN API KEY WEATHERAPI.COM!**

Ini terlihat seperti API key dari **OpenWeatherMap** atau layanan lain.

---

## Cara Mendapatkan API Key yang BENAR

### Step 1: Daftar

1. Kunjungi: https://www.weatherapi.com/signup.aspx
2. Isi form (email + password)
3. Klik "Sign Up"

### Step 2: Login & Copy API Key

1. Login di: https://www.weatherapi.com/login.aspx
2. Di dashboard, Anda akan langsung melihat **"Your API Key"**
3. Copy API key tersebut

### Step 3: Paste ke Aplikasi

1. Buka `WeatherViewModel.kt`
2. Ganti baris ini:
   ```kotlin
   private val API_KEY = "4ebe1b6b5ad5d2e327aec81480a44743"
   ```
   Menjadi:
   ```kotlin
   private val API_KEY = "paste_api_key_baru_disini"
   ```

### Step 4: Test

1. Build ulang aplikasi
2. Jalankan
3. Cari "London"
4. Seharusnya data muncul!

---

## Alternatif: Gunakan API Key Demo (Terbatas)

Untuk testing cepat, Anda bisa coba API key demo ini (mungkin sudah tidak aktif):

```
b8c9e7a0123456789abcdef012345678
```

⚠️ **CATATAN:** API key demo punya limit sangat kecil, sebaiknya buat account sendiri.

---

## Lihat Error Detail di Logcat

Setelah perbaikan, cek Logcat untuk melihat error detail:

1. Buka Android Studio
2. Klik tab **"Logcat"** di bagian bawah
3. Filter dengan keyword: **"WeatherViewModel"**
4. Jalankan aplikasi dan cari kota
5. Lihat log error yang muncul

Error message akan menunjukkan masalah sebenarnya (API key invalid, network error, dll)

---

**Good Luck! 🚀**
