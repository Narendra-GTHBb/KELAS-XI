# Calculator App - Implementation Summary

## Fitur yang Diimplementasikan

### 1. Desain UI dan Tata Letak ✅

#### Struktur Dasar

- ✅ Aplikasi dibangun menggunakan `Column` sebagai wadah utama
- ✅ Berisi output persamaan dan hasil, serta grid tombol

#### Tampilan Hasil

- ✅ **Equation (Persamaan)**: Menampilkan angka dan operator yang diketik pengguna (fontSize 64sp)
- ✅ **Result (Hasil)**: Menampilkan hasil perhitungan real-time (fontSize 32sp, opacity 0.6)

#### Button Grid

- ✅ Tombol disusun menggunakan `LazyVerticalGrid` dengan 4 kolom tetap `GridCells.Fixed(4)`
- ✅ Tata letak tombol rapi dengan spacing 16dp

#### Tombol Kustom

- ✅ Tombol dibuat menggunakan `FloatingActionButton`
- ✅ Bentuk lingkaran (`CircleShape`)
- ✅ Ukuran 80dp dengan efek bayangan (elevation)

#### Pewarnaan Dinamis

- ✅ **Merah** (`#FF5252`): Tombol C dan AC
- ✅ **Orange** (`#FF9800`): Operator (÷, ×, +, -, =)
- ✅ **Abu-abu** (`#757575`): Tombol kurung (, )
- ✅ **Cyan** (`#00BCD4`): Angka (0-9) dan titik (.)

### 2. Pengelolaan Logika dan Perhitungan ✅

#### State Management

- ✅ Logika dikelola dalam `CalculatorViewModel`
- ✅ Menggunakan `LiveData` untuk equation dan result
- ✅ UI diperbarui secara real-time menggunakan `observeAsState()`

#### Fungsionalitas Tombol

- ✅ **Angka/Operator**: Menambahkan karakter ke string persamaan
- ✅ **C (Clear)**: Menghapus satu karakter terakhir dari persamaan
- ✅ **AC (All Clear)**: Menghapus seluruh persamaan dan hasil
- ✅ **= (Equals)**: Menggantikan persamaan dengan hasil akhir

#### Logika Perhitungan

- ✅ Menggunakan pustaka **Rhino (Mozilla)** untuk evaluasi ekspresi matematika
- ✅ Mendukung operasi: perkalian (×), pembagian (÷), penjumlahan (+), pengurangan (-)
- ✅ Menangani urutan operasi dengan benar
- ✅ Perhitungan real-time saat mengetik

## Struktur File

```
app/src/main/java/com/example/calculator/
├── MainActivity.kt              # Activity utama dengan CalculatorTheme
├── CalculatorViewModel.kt       # ViewModel untuk state management
├── CalculatorScreen.kt          # UI Composable untuk kalkulator
└── ui/theme/
    ├── Theme.kt                 # Dark theme configuration
    ├── Color.kt                 # Definisi warna
    └── Type.kt                  # Typography
```

## Dependencies yang Ditambahkan

```kotlin
// ViewModel and LiveData
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
implementation("androidx.compose.runtime:runtime-livedata:1.6.0")

// Expression Evaluator (Rhino)
implementation("org.mozilla:rhino:1.7.14")
```

## Cara Menjalankan

1. Sync Gradle dependencies
2. Build project: `./gradlew assembleDebug`
3. Run di emulator atau device fisik

## Fitur Utama

1. ✅ Dark theme dengan background #1C1C1C
2. ✅ Tampilan dua baris (equation di bawah, result di atas)
3. ✅ Grid 4x5 tombol dengan warna berbeda per kategori
4. ✅ Perhitungan real-time
5. ✅ Support operasi matematika lengkap
6. ✅ Tombol floating dengan shadow effect
7. ✅ UI sesuai dengan screenshot yang diberikan

## Layout Tombol

```
C   (   )   ÷
7   8   9   ×
4   5   6   +
1   2   3   -
AC  0   .   =
```

Warna:

- Row 1: Merah, Abu-abu, Abu-abu, Orange
- Row 2-4: Cyan, Cyan, Cyan, Orange
- Row 5: Merah, Cyan, Cyan, Orange
