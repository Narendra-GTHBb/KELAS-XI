// Konfigurasi Firebase
// PENTING: Gantikan nilai-nilai di bawah dengan konfigurasi Firebase anda sendiri
// Anda boleh dapatkan maklumat ini dari Firebase Console > Project Settings > Your apps

import { initializeApp } from "firebase/app";
import { getDatabase } from "firebase/database";

// Baca konfigurasi dari pembolehubah persekitaran (fail .env)
const firebaseConfig = {
  apiKey: process.env.REACT_APP_API_KEY,
  authDomain: process.env.REACT_APP_AUTH_DOMAIN,
  databaseURL: process.env.REACT_APP_DATABASE_URL,
  projectId: process.env.REACT_APP_PROJECT_ID,
  storageBucket: process.env.REACT_APP_STORAGE_BUCKET,
  messagingSenderId: process.env.REACT_APP_MESSAGING_SENDER_ID,
  appId: process.env.REACT_APP_APP_ID,
};

// Inisialisasi Firebase
const app = initializeApp(firebaseConfig);

// Dapatkan rujukan ke Firebase Realtime Database
const database = getDatabase(app);

export { database };
export default app;
