import React, { useState } from 'react';
import { ref, push, set } from 'firebase/database';
import { database } from '../firebase/config';

function Write() {
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [saving, setSaving] = useState(false);

  const saveData = async (e) => {
    e.preventDefault();

    if (title.trim() === '') {
      alert('Sila masukkan nama item (Title).');
      return;
    }

    setSaving(true);
    try {
      const dataRef = ref(database, 'items/list-of-items');
      const newItemRef = push(dataRef);
      await set(newItemRef, {
        title: title.trim(),
        description: description.trim(),
        createdAt: Date.now(),
      });

      alert('Data berjaya disimpan.');
      setTitle('');
      setDescription('');
    } catch (err) {
      // eslint-disable-next-line no-console
      console.error('Gagal menyimpan data:', err);
      alert('Terdapat ralat semasa menyimpan data. Sila semak konsol.');
    } finally {
      setSaving(false);
    }
  };

  return (
    <div style={{ padding: 24, maxWidth: 800, margin: '40px auto', color: '#fff' }}>
      <h1>Halaman Tulis / Tambah Data</h1>
      <form onSubmit={saveData} style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
        <label style={{ display: 'flex', flexDirection: 'column', gap: 6 }}>
          Nama Item
          <input
            type="text"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            placeholder="Contoh: Beli Susu"
            style={{ padding: 10, borderRadius: 6, border: '1px solid #ccc' }}
            disabled={saving}
          />
        </label>

        <label style={{ display: 'flex', flexDirection: 'column', gap: 6 }}>
          Penerangan Item
          <input
            type="text"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            placeholder="Contoh: 2 liter, jenama X"
            style={{ padding: 10, borderRadius: 6, border: '1px solid #ccc' }}
            disabled={saving}
          />
        </label>

        <div>
          <button
            type="submit"
            disabled={saving}
            style={{ padding: '10px 16px', borderRadius: 6, cursor: saving ? 'not-allowed' : 'pointer' }}
          >
            {saving ? 'Menyimpan…' : 'Save Data'}
          </button>
        </div>
      </form>
    </div>
  );
}

export default Write;
import React from "react";

function Write() {
  return (
    <div
      style={{ padding: 24, maxWidth: 800, margin: "40px auto", color: "#fff" }}
    >
      <h1>Halaman Tulis / Tambah Data</h1>
      <p>
        Ini adalah halaman untuk Tambah Data. (Komponen `Write` berjaya
        dimuatkan.)
      </p>
    </div>
  );
}

import React, { useState } from 'react';
import { ref, push, set } from 'firebase/database';
import { database } from '../firebase/config';

function Write() {
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [saving, setSaving] = useState(false);

  const saveData = async (e) => {
    e.preventDefault();

    if (title.trim() === '') {
      alert('Sila masukkan nama item (Title).');
      return;
    }

    setSaving(true);
    try {
      // Tentukan rujukan lokasi penyimpanan
      const dataRef = ref(database, 'items/list-of-items');

      // Cipta key baru secara automatik
      const newItemRef = push(dataRef);

      // Simpan data ke Realtime Database
      await set(newItemRef, {
        title: title.trim(),
        description: description.trim(),
        createdAt: Date.now(),
      });

      alert('Data berjaya disimpan.');
      // Kosongkan borang
      setTitle('');
      setDescription('');
    } catch (err) {
      // eslint-disable-next-line no-console
      console.error('Gagal menyimpan data:', err);
      alert('Terdapat ralat semasa menyimpan data. Sila semak konsol.');
    } finally {
      setSaving(false);
    }
  };

  return (
    <div style={{ padding: 24, maxWidth: 800, margin: '40px auto', color: '#fff' }}>
      <h1>Halaman Tulis / Tambah Data</h1>
      <form onSubmit={saveData} style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
        <label style={{ display: 'flex', flexDirection: 'column', gap: 6 }}>
          Nama Item
          <input
            type="text"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            placeholder="Contoh: Beli Susu"
            style={{ padding: 10, borderRadius: 6, border: '1px solid #ccc' }}
            disabled={saving}
          />
        </label>

        <label style={{ display: 'flex', flexDirection: 'column', gap: 6 }}>
          Penerangan Item
          <input
            type="text"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            placeholder="Contoh: 2 liter, jenama X"
            style={{ padding: 10, borderRadius: 6, border: '1px solid #ccc' }}
            disabled={saving}
          />
        </label>

        <div>
          <button
            type="submit"
            disabled={saving}
            style={{ padding: '10px 16px', borderRadius: 6, cursor: saving ? 'not-allowed' : 'pointer' }}
          >
            {saving ? 'Menyimpan…' : 'Save Data'}
          </button>
        </div>
      </form>
    </div>
  );
}

export default Write;
