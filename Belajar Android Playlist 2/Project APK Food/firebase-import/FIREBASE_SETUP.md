# Firebase Setup Guide - Waves of Food

Panduan lengkap untuk setup Firebase database untuk aplikasi Waves of Food.

## 📋 Prerequisites

- ✅ Node.js (versi 16 atau lebih baru)
- ✅ Firebase account
- ✅ Firebase project sudah dibuat

## 🚀 Quick Start

### 1. Setup Firebase Project

1. **Buka Firebase Console**

   - Kunjungi https://console.firebase.google.com/
   - Login dengan Google account

2. **Buat Project Baru**

   ```
   Project Name: Waves of Food
   Project ID: waves-of-food-xxxxx (akan di-generate otomatis)
   Location: Asia Southeast 2 (Jakarta)
   ```

3. **Enable Firebase Services**
   - ✅ Authentication (Email/Password)
   - ✅ Firestore Database
   - ✅ Storage

### 2. Download Service Account Key

1. **Project Settings**

   - Klik gear icon ⚙️ > Project settings
   - Tab "Service accounts"

2. **Generate Key**
   - Klik "Generate new private key"
   - Download file JSON
   - Rename menjadi `serviceAccountKey.json`

### 3. Setup Import Tool

1. **Navigate ke Firebase Import Directory**

   ```bash
   cd "c:\Project APK Food\firebase-import"
   ```

2. **Install Dependencies**

   ```bash
   npm install
   ```

3. **Setup Environment**

   ```bash
   # Copy example env file
   copy .env.example .env

   # Edit .env file dengan konfigurasi Anda
   notepad .env
   ```

4. **Configure .env File**
   ```env
   FIREBASE_PROJECT_ID=your-waves-of-food-project-id
   FIREBASE_DATABASE_URL=https://your-project-id-default-rtdb.firebaseio.com/
   COLLECTION_CATEGORIES=categories
   COLLECTION_FOODS=foods
   COLLECTION_USERS=users
   COLLECTION_ORDERS=orders
   CLEAR_EXISTING_DATA=false
   BATCH_SIZE=10
   ```

### 4. Import Sample Data

1. **Test Connection (Optional)**

   ```bash
   # Windows
   test.bat

   # Or manually
   node test-connection.js
   ```

2. **Run Import**

   ```bash
   # Windows (Easy way)
   import.bat

   # Or manually
   node import-data.js
   ```

## 📊 Sample Data Overview

### Categories (8 items)

- 🍕 Pizza
- 🍔 Burger
- 🍜 Noodles
- 🍚 Rice Dishes
- 🥤 Drinks
- 🍰 Desserts
- 🍿 Snacks
- 🍗 Chicken

### Food Items (20+ items)

- Margherita Pizza - Rp 85.000
- Classic Beef Burger - Rp 65.000
- Tonkotsu Ramen - Rp 72.000
- Nasi Gudeg Yogya - Rp 45.000
- Buffalo Chicken Wings - Rp 48.000
- Dan banyak lagi...

### Users (5 sample users)

- John Doe (john.doe@example.com)
- Jane Smith (jane.smith@example.com)
- Admin User (admin@wavesoffood.com)
- Mike Wilson (mike.wilson@example.com)
- Sarah Johnson (sarah.johnson@example.com)

### Orders (5 sample orders)

- Various order statuses (delivered, preparing, confirmed)
- Realistic order data dengan items dan pricing

## 🔧 Firebase Security Rules

### Firestore Rules

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Allow authenticated users to read/write their own data
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }

    // Allow authenticated users to read categories and foods
    match /categories/{document} {
      allow read: if request.auth != null;
      allow write: if request.auth != null; // Admin only in production
    }

    match /foods/{document} {
      allow read: if request.auth != null;
      allow write: if request.auth != null; // Admin only in production
    }

    // Orders can only be accessed by the owner
    match /orders/{orderId} {
      allow read, write: if request.auth != null &&
        resource.data.userId == request.auth.uid;
      allow create: if request.auth != null &&
        request.resource.data.userId == request.auth.uid;
    }
  }
}
```

### Storage Rules

```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    // Images can be uploaded by authenticated users
    match /images/{allPaths=**} {
      allow read: if true;
      allow write: if request.auth != null
        && request.resource.size < 5 * 1024 * 1024; // 5MB limit
    }
  }
}
```

## 🛠️ Troubleshooting

### Common Issues

1. **"Permission denied" error**

   ```
   Solusi:
   - Periksa Firebase Security Rules
   - Pastikan serviceAccountKey.json valid
   - Pastikan project ID benar di .env
   ```

2. **"Module not found" error**

   ```bash
   # Install dependencies
   npm install
   ```

3. **"Project not found" error**

   ```
   Solusi:
   - Periksa FIREBASE_PROJECT_ID di .env
   - Pastikan project exists di Firebase Console
   ```

4. **Network/Connection issues**
   ```
   Solusi:
   - Periksa koneksi internet
   - Periksa firewall settings
   - Coba test-connection.js untuk debug
   ```

### Debug Commands

```bash
# Test Firebase connection
node test-connection.js

# Generate additional sample data
node generate-data.js

# Import with verbose logging
node import-data.js

# Check Node.js version
node --version

# Check npm version
npm --version
```

## 📱 Android App Integration

Setelah data berhasil di-import, aplikasi Android Anda sudah bisa:

1. ✅ Login/Register user
2. ✅ Browse categories dan food items
3. ✅ View food details dengan harga
4. ✅ Add items to cart
5. ✅ Place orders
6. ✅ View order history

## 🔄 Data Management

### Update Data

```bash
# Clear existing data and re-import
# Set CLEAR_EXISTING_DATA=true in .env
node import-data.js
```

### Backup Data

```bash
# Export current Firestore data
# (Gunakan Firebase Admin SDK atau Console)
```

### Add More Data

```bash
# Generate additional sample data
node generate-data.js

# Import extended data
# Edit import-data.js to use foods-extended.json
```

## 📞 Support

Jika mengalami kesulitan:

1. 📖 Baca dokumentasi Firebase: https://firebase.google.com/docs
2. 🔍 Check troubleshooting section di atas
3. 🐛 Run test-connection.js untuk debug
4. 📧 Contact developer

---

**🎉 Setup Firebase completed!** Aplikasi Waves of Food siap digunakan dengan data lengkap.
