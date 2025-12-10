import React, { useState } from "react";
import { ref, get } from "firebase/database";
import { database } from "../firebase/config";
import { remove } from "firebase/database";
import { useNavigate } from "react-router-dom";

function UpdateRead({ setSelectedForUpdate }) {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  // eslint-disable-next-line no-unused-vars
  const deleteData = async (recordId) => {
    if (!recordId) return;
    // eslint-disable-next-line no-restricted-globals
    if (!confirm("Anda pasti mahu memadam rekod ini?")) return;

    try {
      const targetRef = ref(database, `nature/fruits/${recordId}`);
      await remove(targetRef);
      alert("Data berjaya dipadam.");
      // Refresh list
      fetchData();
    } catch (err) {
      // eslint-disable-next-line no-console
      console.error("Gagal memadam data:", err);
      alert("Ralat ketika memadam data. Semak konsol.");
    }
  };

  const fetchData = async () => {
    setLoading(true);
    try {
      const db = database;
      const dataRef = ref(db, "nature/fruits");
      const snapshot = await get(dataRef);

      if (snapshot.exists()) {
        const myData = snapshot.val();
        // Get array of keys (unique IDs)
        const ids = Object.keys(myData);

        // Combine id with value into a new array
        const finalData = ids.map((recordId) => {
          const recordValue = myData[recordId];
          return {
            ...recordValue,
            fruitID: recordId,
          };
        });

        setItems(finalData);
      } else {
        setItems([]);
      }
    } catch (err) {
      // eslint-disable-next-line no-console
      console.error("Gagal fetch data (update read):", err);
      alert("Ralat ketika mengambil data. Semak konsol.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div
      style={{ padding: 24, maxWidth: 900, margin: "40px auto", color: "#fff" }}
    >
      <h1>Read With IDs (UpdateRead)</h1>
      <p>
        Tekan butang untuk mengambil data dari `nature/fruits` dan sertakan ID
        unik.
      </p>
      <div style={{ marginBottom: 12 }}>
        <button
          onClick={fetchData}
          disabled={loading}
          style={{ padding: "8px 14px", borderRadius: 6 }}
        >
          {loading ? "Memuat..." : "Display Data"}
        </button>
      </div>

      {items.length === 0 ? (
        <p style={{ color: "rgba(255,255,255,0.85)" }}>
          Tiada data. Tekan "Display Data" untuk cuba.
        </p>
      ) : (
        <ul
          style={{
            background: "rgba(255,255,255,0.08)",
            padding: 12,
            borderRadius: 8,
          }}
        >
          {items.map((item) => (
            <li
              key={item.fruitID}
              style={{
                padding: 8,
                borderBottom: "1px solid rgba(255,255,255,0.04)",
              }}
            >
              <div>
                <strong>Nama:</strong>{" "}
                {item.fruitName || item.title || "Unnamed"}
              </div>
              <div style={{ fontSize: 14, opacity: 0.9 }}>
                <strong>Definisi:</strong>{" "}
                {item.fruitDefinition || item.description || ""}
              </div>
              <div style={{ fontSize: 12, opacity: 0.8, marginTop: 6 }}>
                <strong>ID:</strong> {item.fruitID}
              </div>
              <div style={{ marginTop: 8 }}>
                <button
                  onClick={() => {
                    if (setSelectedForUpdate) setSelectedForUpdate(item);
                    navigate("/update");
                  }}
                  style={{ padding: "6px 10px", borderRadius: 6, marginTop: 6 }}
                >
                  Update
                </button>
              </div>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}

export default UpdateRead;
