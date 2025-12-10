import React from "react";
import "./TodoItem.css";

function TodoItem({ todo, onToggle, onEdit, onDelete }) {
  return (
    <div className={`todo-item ${todo.completed ? "completed" : ""}`}>
      <div className="todo-content">
        <input
          type="checkbox"
          checked={todo.completed}
          onChange={() => onToggle(todo.id, todo.completed)}
          className="todo-checkbox"
        />
        <span className="todo-text">{todo.text}</span>
      </div>
      <div className="todo-actions">
        <button
          className="btn-edit"
          onClick={() => onEdit(todo)}
          title="Edit tugasan"
        >
          ✏️ Edit
        </button>
        <button
          className="btn-delete"
          onClick={() => onDelete(todo.id)}
          title="Padam tugasan"
        >
          🗑️ Padam
        </button>
      </div>
    </div>
  );
}

export default TodoItem;
