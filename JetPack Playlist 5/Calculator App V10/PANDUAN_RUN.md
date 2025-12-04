# Panduan Menjalankan Calculator App

## Cara Build dan Run

### Menggunakan Android Studio

1. Buka project di Android Studio
2. Tunggu Gradle sync selesai
3. Pilih emulator atau device
4. Klik tombol Run (▶️) atau tekan Shift+F10

### Menggunakan Command Line

1. Build debug APK:

   ```bash
   ./gradlew assembleDebug
   ```

2. Install ke device yang terhubung:

   ```bash
   ./gradlew installDebug
   ```

3. Atau build dan install sekaligus:
   ```bash
   ./gradlew assembleDebug installDebug
   ```

## Testing Fungsionalitas

### Test Case 1: Perhitungan Sederhana

- Input: `8 + 9 + 5 + 6`
- Hasil yang diharapkan: `28`
- Pada screenshot terlihat: `8956` (sedang mengetik)

### Test Case 2: Operasi Perkalian

- Input: `7 × 8`
- Hasil yang diharapkan: `56`

### Test Case 3: Operasi Pembagian

- Input: `9 ÷ 3`
- Hasil yang diharapkan: `3`

### Test Case 4: Operasi Kompleks

- Input: `(2 + 3) × 4`
- Hasil yang diharapkan: `20`

### Test Case 5: Tombol C (Clear)

- Input: `123`
- Tekan C
- Hasil: `12` (menghapus 1 karakter)

### Test Case 6: Tombol AC (All Clear)

- Input: `123`
- Tekan AC
- Hasil: `` (kosong, semua dihapus)

### Test Case 7: Tombol Equals

- Input: `5 + 5`
- Tekan =
- Hasil: Equation berubah menjadi `10`, result kosong

## Fitur yang Dapat Dicoba

1. **Real-time Calculation**: Saat mengetik, hasil akan langsung muncul di atas
2. **Operator Chaining**: `2 + 3 × 4` akan mengikuti urutan operasi matematika
3. **Kurung**: `(2 + 3) × 4` untuk mengatur prioritas
4. **Desimal**: `3.14 × 2`
5. **Clear Character**: Tombol C untuk menghapus 1 karakter
6. **Clear All**: Tombol AC untuk reset semua

## Troubleshooting

### Build Error

Jika terjadi error saat build:

```bash
./gradlew clean
./gradlew assembleDebug
```

### Gradle Sync Issues

1. File → Invalidate Caches / Restart
2. Restart Android Studio

### Emulator Tidak Muncul

1. Tools → Device Manager
2. Create Virtual Device
3. Pilih Pixel 4 atau yang lain
4. System Image: API 26 atau lebih tinggi

## Persyaratan Sistem

- **Android SDK**: API 26 (Android 8.0) atau lebih tinggi
- **Gradle**: 8.13
- **Kotlin**: 1.9.0 atau lebih tinggi
- **Jetpack Compose**: Latest BOM

## Output Build

APK hasil build akan tersimpan di:

```
app/build/outputs/apk/debug/app-debug.apk
```

Anda dapat install manual dengan:

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```
