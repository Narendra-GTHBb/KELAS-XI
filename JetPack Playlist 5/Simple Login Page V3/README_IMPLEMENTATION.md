# Simple Login Page V3 - Jetpack Compose

## Overview

Aplikasi Android login sederhana yang dibangun menggunakan Jetpack Compose, mengimplementasikan semua konsep utama yang disebutkan dalam requirements.

## Komponen dan Konsep Utama yang Diimplementasikan

### 1. Pembuatan Composable Khusus ✅

- **LoginScreen()**: Fungsi Composable utama yang memisahkan UI dari MainActivity
- Dipanggil di MainActivity untuk modularitas yang baik

### 2. Tata Letak Dasar ✅

- **Column**: Wadah utama untuk mengatur semua elemen UI secara vertikal
- **Box**: Container luar untuk layout yang lebih fleksibel
- **Row**: Untuk social login buttons yang tersusun horizontal

### 3. Penggunaan Modifier ✅

- **fillMaxSize()**: Membuat Composable mengisi ukuran layar penuh
- **Arrangement.Center & Alignment.CenterHorizontally**: Mengatur posisi elemen ke tengah layar
- **size()**: Mengatur ukuran gambar ilustrasi (180dp)
- **Spacer dengan height()**: Mengatur jarak antar elemen melalui padding
- **clickable()**: Membuat elemen dapat diklik (Forgot Password, social buttons)

### 4. Input Pengguna ✅

- **OutlinedTextField**: Untuk kolom email dan password dengan styling modern
- **remember & mutableStateOf**: Mengelola nilai input secara dinamis
- **PasswordVisualTransformation**: Menyembunyikan karakter password

### 5. Tombol dan Teks Khusus ✅

- **Button**: Tombol Login utama dengan styling custom
- **TextButton implisit**: Teks "Forgot Password" yang dapat diklik

### 6. Tata Letak Horizontal Lanjutan ✅

- **Row dengan Arrangement.SpaceEvenly**: Menampilkan ikon social login (Facebook, Google, Twitter/X) secara sejajar

## Fitur UI yang Diimplementasikan

### Visual Components

- **Ilustrasi Login**: Vector drawable custom dengan tema login
- **Welcome Text**: Typography yang hierarkis dan menarik
- **Input Fields**: Email dan password dengan icons dan styling modern
- **Login Button**: Button dengan rounded corners dan shadow
- **Social Login**: Circular buttons untuk Facebook, Google, dan Twitter/X

### Styling & Design

- **Colors**: Purple primary (#6366F1), dengan accent colors sesuai brand
- **Typography**: Berbagai ukuran font untuk hierarki informasi
- **Spacing**: Consistent spacing menggunakan dp units
- **Shapes**: Rounded corners untuk modern appearance

### State Management

- Email field dengan default value "test@gmail.com"
- Password field dengan state management untuk user input
- Reactive UI yang merespons perubahan state

## Struktur File

```
app/src/main/
├── java/com/apk/mylogin/
│   └── MainActivity.kt        # Main activity dengan LoginScreen composable
├── res/
│   └── drawable/
│       └── login_illustration.xml  # Custom vector illustration
└── ...
```

## Dependencies yang Ditambahkan

```kotlin
// Untuk extended material icons
implementation(libs.androidx.material.icons.extended)
```

## Key Learning Points

1. **Composable Functions**: Pemisahan UI logic dalam functions yang reusable
2. **State Management**: Menggunakan remember dan mutableStateOf untuk reactive UI
3. **Layout Composition**: Kombinasi Column, Row, dan Box untuk layout yang kompleks
4. **Modifier Chaining**: Penggunaan multiple modifiers untuk styling
5. **Material Design 3**: Implementasi modern Material Design components
6. **Vector Graphics**: Custom vector drawables untuk illustrations
7. **User Input Handling**: TextField dengan proper keyboard types dan transformations

## Cara Menjalankan

1. Buka project di Android Studio
2. Sync project dengan Gradle
3. Run aplikasi di emulator atau device fisik
4. Interface akan menampilkan login screen sesuai dengan desain yang diminta

## Screenshot Reference

Aplikasi ini mengimplementasikan design sesuai dengan gambar referensi yang diberikan, dengan:

- Login illustration di bagian atas
- Welcome text dan subtitle
- Email dan password input fields
- Login button dengan styling modern
- Forgot password link
- Social login buttons (Facebook, Google, Twitter)

Aplikasi sudah siap untuk development lebih lanjut dengan penambahan backend integration dan navigation logic.
