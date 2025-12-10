# Firebase setup (Fasa 2) — Panduan ringkas

1. Buka https://console.firebase.google.com/ dan cipta projek baru (atau guna projek sedia ada).
2. Daftar aplikasi Web (ikon web) untuk mendapatkan konfigurasi Firebase (apiKey, authDomain, projectId, appId, dll.).

- Salin konfigurasi yang diberikan oleh Firebase — anda akan memasukkannya ke dalam fail `.env`.

3. Untuk Realtime Database: pergi ke "Realtime Database" → "Create database" dan ikut arahan.

- Pilih lokasi yang sesuai.
- Semasa pembangunan, anda boleh gunakan rules sementara berikut untuk membenarkan akses dari frontend:

```json
{
  "rules": {
    ".read": true,
    ".write": true
  }
}
```

Amaran: jangan biarkan rules di atas dalam persekitaran produksi — ia membuka semua data kepada sesiapa sahaja.

4. Salin nilai konfigurasi Firebase ke dalam fail `.env` di akar projek. Cara pantas:

```powershell
cd "c:\Playlist 4\todo-app"
copy .env.example .env
```

Kemudian buka `.env` dan gantikan placeholder dengan nilai sebenar daripada Firebase Console. Pastikan pembolehubah menggunakan awalan `REACT_APP_` (contoh: `REACT_APP_API_KEY`, `REACT_APP_DATABASE_URL`).

5. Mulakan semula dev server (jika sedang berjalan) supaya pembolehubah persekitaran baru dibaca:

```powershell
npm start
```

6. Semak konsol pelayar untuk amaran: kami telah menambah pemeriksaan ringkas pada permulaan aplikasi yang akan memberi peringatan jika mana-mana `REACT_APP_` yang penting tidak ditetapkan.

Nota keselamatan:

- Fail `.env` ditambahkan ke `.gitignore` supaya kunci tidak tersimpan dalam repo. Gunakan `.env.example` untuk berkongsi nama pembolehubah sahaja.
- Sebelum deploy ke produksi, tukar peraturan Realtime Database kepada peraturan yang lebih ketat (contoh: memerlukan pengesahan pengguna).
  Sekiranya anda mahu, saya telah menyediakan contoh peraturan lebih selamat di `firebase.rules.json`.

Contoh langkah ringkas untuk menggunakan peraturan ini dan Firebase CLI:

1. Pasang Firebase CLI jika belum ada:

```powershell
npm install -g firebase-tools
```

2. Login dan pilih projek:

```powershell
firebase login
firebase use --add
```

3. Untuk deploy rules (yang terletak di `firebase.rules.json`):

```powershell
firebase deploy --only database
```

4. Contoh peraturan di `firebase.rules.json` meminta pengguna untuk masuk (auth != null) dan menambah beberapa validasi medan bagi simpanan `todos`.

Panduan ringkas untuk enabling Authentication (Email/Password):

1. Pergi ke Firebase Console → Authentication → Get started.
2. Pada Sign-in method, aktifkan Email/Password.

Selepas itu, dalam aplikasi anda, anda boleh mendaftar / log masuk pengguna (menggunakan paket `firebase/auth`) dan simpan `ownerId: auth.currentUser.uid` pada setiap todo yang dibuat untuk menguatkuasakan pemilikan.

Jika anda mahu, saya boleh:

- Tambah contoh integrasi `firebase/auth` ringkas ke projek (daftar / login pengguna).
- Update kod `TodoList` supaya menyimpan `ownerId` semasa menambah tugasan dan hanya memaparkan tugasan yang dimiliki oleh pengguna yang sedang log masuk.
  Sekiranya anda mahu, saya boleh membantu dengan contoh peraturan lebih selamat yang menggunakan Firebase Authentication.
