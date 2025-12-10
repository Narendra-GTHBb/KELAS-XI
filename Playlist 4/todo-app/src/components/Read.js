import React, { useState } from "react";
import { ref, get } from "firebase/database";
import { database } from "../firebase/config";

function Read() {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(false);

  const fetchData = async () => {
    setLoading(true);
    try {
      // Per prompt, read from 'nature/fruits'
      const db = database; // already initialized in config
      const dataRef = ref(db, "nature/fruits");
      const snapshot = await get(dataRef);

      if (snapshot.exists()) {
        const obj = snapshot.val();
        // Turn into array of values (drop push keys)
        const arr = Object.values(obj);
        setItems(arr);
      } else {
        // If nothing at 'nature/fruits', set empty
        setItems([]);
      }
    } catch (err) {
      // eslint-disable-next-line no-console
      console.error("Gagal fetch data:", err);
      alert("Ralat ketika mengambil data. Semak konsol.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div
      style={{ padding: 24, maxWidth: 900, margin: "40px auto", color: "#fff" }}
    >
      <h1>Baca Data (Read)</h1>
      <p>Tekan butang untuk mengambil data dari `nature/fruits`.</p>
      <div style={{ marginBottom: 12 }}>
        <button
          onClick={fetchData}
          disabled={loading}
          style={{ padding: "8px 14px", borderRadius: 6 }}
        >
          {loading ? "Memuat..." : "Fetch Data"}
        </button>
      </div>

      {items.length === 0 ? (
        <p style={{ color: "rgba(255,255,255,0.85)" }}>
          Tiada data. Tekan "Fetch Data" untuk cuba.
        </p>
      ) : (
        <ul
          style={{
            background: "rgba(255,255,255,0.08)",
            padding: 12,
            borderRadius: 8,
          }}
        >
          {items.map((item, idx) => (
            <li
              key={idx}
              style={{
                padding: 8,
                borderBottom: "1px solid rgba(255,255,255,0.04)",
              }}
            >
              <strong>{item.fruitName || item.title || "Unnamed"}</strong>
              <div style={{ fontSize: 14, opacity: 0.9 }}>
                {item.fruitDefinition || item.description || ""}
              </div>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}

export default Read;
