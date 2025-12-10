# Firebase Import Tool untuk Waves of Food

Script Node.js untuk mengimpor sample data ke Firestore database.

## Setup

1. Install dependencies:

```bash
npm install
```

2. Download Firebase Admin SDK key:

   - Buka Firebase Console
   - Project Settings > Service Accounts
   - Generate new private key
   - Simpan sebagai `serviceAccountKey.json`

3. Setup environment variables:

```bash
cp .env.example .env
# Edit .env dengan konfigurasi Firebase Anda
```

4. Run import:

```bash
npm run import
```

## Data yang Diimpor

- ✅ Categories (Pizza, Burger, Drinks, dll)
- ✅ Food Items (50+ makanan dengan detail lengkap)
- ✅ Sample Users
- ✅ Sample Orders

## File Structure

```
firebase-import/
├── package.json
├── import-data.js (main script)
├── data/
│   ├── categories.json
│   ├── foods.json
│   ├── users.json
│   └── orders.json
├── serviceAccountKey.json (your Firebase key)
└── .env (environment config)
```
