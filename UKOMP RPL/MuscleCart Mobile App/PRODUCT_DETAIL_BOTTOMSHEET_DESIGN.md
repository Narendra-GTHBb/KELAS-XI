# Product Detail Bottom Sheet Design

## Overview
Berdasarkan permintaan untuk membuat desain product detail yang muncul dari bawah ke atas saat produk di etalase ditekan, telah diimplementasikan `ProductDetailBottomSheet` yang memberikan pengalaman user yang lebih modern dan intuitif.

## Features

### 🎨 Design Elements
- **Slide Animation**: Bottom sheet muncul dengan animasi slide dari bawah ke atas
- **Dark Overlay**: Background gelap (60% opacity) untuk membelakangi page sebelumnya
- **Partial Screen**: Tidak full screen - menyisakan ruang di atas (100dp) untuk visibility page sebelumnya
- **Rounded Corners**: Corner radius 24dp di bagian atas untuk design yang modern
- **Handle Bar**: Indikator drag di bagian atas untuk menunjukkan bisa di-dismiss

### 🎯 Interactive Elements
- **Heart/Favorite Button**: Floating di pojok kanan atas gambar produk
- **Rating Display**: Menampilkan rating produk dengan bintang
- **Flavor Selection**: Chips untuk memilih variant rasa
- **Quantity Selector**: Counter dengan tombol +/- untuk memilih jumlah
- **Add to Cart**: Primary button dengan loading state

### 🔄 Navigation & Interaction
- **Tap Outside**: Tap di area overlay gelap untuk menutup
- **Close Button**: X button di header untuk menutup
- **Drag Indicator**: Handle bar yang dapat di-drag (future enhancement)
- **Back to Shop**: Kembali ke halaman shop saat bottom sheet ditutup

## Implementation Details

### 1. ProductDetailBottomSheet.kt
- Animation dengan `animateDpAsState` dan `animateFloatAsState`
- Dark overlay dengan opacity animation
- Responsive design yang menyesuaikan tinggi konten
- Integrated dengan existing ProductDetailViewModel

### 2. Updated HomeScreen.kt
- Bottom sheet state management internal di HomeScreen
- Tidak lagi menggunakan navigation untuk product detail
- Menampilkan overlay dan bottom sheet di atas home screen

### 3. Updated MainScreen.kt
- Navigation callback dikosongkan karena handled internal di HomeScreen
- Tetap mempertahankan route untuk deep linking (backward compatibility)

## User Experience Flow

1. **User browses products** di home screen
2. **Tap pada product** untuk melihat detail
3. **Bottom sheet slides up** dengan animasi smooth
4. **Background darkened** menunjukkan konteks page sebelumnya
5. **User interacts** dengan product detail (quantity, add to cart, etc.)
6. **Tap outside atau close** untuk kembali ke shop
7. **Bottom sheet slides down** dengan animasi smooth

## Design Specifications

- **Height**: Maximum 700dp, minimum content-based
- **Top Margin**: 100dp from screen top
- **Corner Radius**: 24dp (top corners only)
- **Overlay Opacity**: 0.6 (60% black)
- **Animation Duration**: 400ms for slide, 300ms for overlay
- **Colors**: Primary brand color #4285F4

## Future Enhancements

- [ ] Drag to dismiss gesture
- [ ] Haptic feedback
- [ ] Image carousel/gallery
- [ ] Share product functionality
- [ ] Product reviews section
- [ ] Related products suggestions