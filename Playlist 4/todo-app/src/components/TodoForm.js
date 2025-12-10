import React, { useState, useEffect } from "react";
import "./TodoForm.css";

function TodoForm({ onSubmit, editingTodo, onCancel }) {
  const [inputText, setInputText] = useState("");

  // Muatkan teks apabila mengedit
  useEffect(() => {
    if (editingTodo) {
      setInputText(editingTodo.text);
    } else {
      setInputText("");
    }
  }, [editingTodo]);

  const handleSubmit = (e) => {
    e.preventDefault();

    if (inputText.trim() === "") {
      alert("Sila masukkan teks tugasan!");
      return;
    }

    onSubmit(inputText.trim());
    setInputText("");
  };

  return (
    <form className="todo-form" onSubmit={handleSubmit}>
      <input
        type="text"
        className="todo-input"
        placeholder="Masukkan tugasan baharu..."
        value={inputText}
        onChange={(e) => setInputText(e.target.value)}
      />
      <button type="submit" className="btn-submit">
        {editingTodo ? "💾 Simpan" : "➕ Tambah"}
      </button>
      {editingTodo && (
        <button type="button" className="btn-cancel" onClick={onCancel}>
          ❌ Batal
        </button>
      )}
    </form>
  );
}

export default TodoForm;
