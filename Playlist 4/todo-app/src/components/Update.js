import React, { useState, useEffect } from "react";
import { ref, update } from "firebase/database";
import { database } from "../firebase/config";
import { useNavigate } from "react-router-dom";

function Update({ selectedItem, setSelectedItem }) {
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [saving, setSaving] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    if (selectedItem) {
      setTitle(selectedItem.title || selectedItem.fruitName || "");
      setDescription(
        selectedItem.description || selectedItem.fruitDefinition || ""
      );
    }
  }, [selectedItem]);

  const updateData = async (e) => {
    e.preventDefault();
    if (!selectedItem || !selectedItem.fruitID) {
      alert("Tiada item dipilih untuk dikemas kini.");
      return;
    }

    setSaving(true);
    try {
      const targetRef = ref(database, `nature/fruits/${selectedItem.fruitID}`);
      await update(targetRef, {
        fruitName: title.trim(),
        fruitDefinition: description.trim(),
      });

      alert("Data berjaya dikemas kini.");
      // Clear selection and navigate back
      if (setSelectedItem) setSelectedItem(null);
      navigate("/update-read");
    } catch (err) {
      // eslint-disable-next-line no-console
      console.error("Gagal update data:", err);
      alert("Ralat ketika mengemas kini data. Semak konsol.");
    } finally {
      setSaving(false);
    }
  };

  return (
    <div
      style={{ padding: 24, maxWidth: 800, margin: "40px auto", color: "#fff" }}
    >
      <h1>Update Item</h1>
      <form
        onSubmit={updateData}
        style={{ display: "flex", flexDirection: "column", gap: 12 }}
      >
        <label style={{ display: "flex", flexDirection: "column", gap: 6 }}>
          Nama Item
          <input
            type="text"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            placeholder="Nama item"
            style={{ padding: 10, borderRadius: 6, border: "1px solid #ccc" }}
            disabled={saving}
          />
        </label>

        <label style={{ display: "flex", flexDirection: "column", gap: 6 }}>
          Penerangan Item
          <input
            type="text"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            placeholder="Penerangan"
            style={{ padding: 10, borderRadius: 6, border: "1px solid #ccc" }}
            disabled={saving}
          />
        </label>

        <div>
          <button
            type="submit"
            disabled={saving}
            style={{ padding: "10px 16px", borderRadius: 6 }}
          >
            {saving ? "Menyimpan…" : "Save Changes"}
          </button>
          <button
            type="button"
            onClick={() => {
              if (setSelectedItem) setSelectedItem(null);
              navigate("/update-read");
            }}
            style={{ marginLeft: 8, padding: "10px 16px", borderRadius: 6 }}
          >
            Cancel
          </button>
        </div>
      </form>
    </div>
  );
}

export default Update;
