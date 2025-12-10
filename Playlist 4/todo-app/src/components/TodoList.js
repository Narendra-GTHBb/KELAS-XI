import React, { useState, useEffect } from "react";
import { database } from "../firebase/config";
import { ref, push, onValue, update, remove } from "firebase/database";
import TodoItem from "./TodoItem";
import TodoForm from "./TodoForm";
import "./TodoList.css";

function TodoList() {
  const [todos, setTodos] = useState([]);
  const [editingTodo, setEditingTodo] = useState(null);

  // Baca data dari Firebase (Realtime)
  useEffect(() => {
    const todosRef = ref(database, "todos");

    const unsubscribe = onValue(todosRef, (snapshot) => {
      const data = snapshot.val();
      if (data) {
        const todoList = Object.keys(data).map((key) => ({
          id: key,
          ...data[key],
        }));
        setTodos(todoList);
      } else {
        setTodos([]);
      }
    });

    // Cleanup subscription
    return () => unsubscribe();
  }, []);

  // Cipta tugasan baharu (Create)
  const handleAddTodo = (todoText) => {
    const todosRef = ref(database, "todos");
    push(todosRef, {
      text: todoText,
      completed: false,
      createdAt: Date.now(),
    });
  };

  // Kemas kini tugasan (Update)
  const handleUpdateTodo = (id, newText) => {
    const todoRef = ref(database, `todos/${id}`);
    update(todoRef, {
      text: newText,
      updatedAt: Date.now(),
    });
    setEditingTodo(null);
  };

  // Tukar status selesai
  const handleToggleComplete = (id, completed) => {
    const todoRef = ref(database, `todos/${id}`);
    update(todoRef, {
      completed: !completed,
    });
  };

  // Padam tugasan (Delete)
  const handleDeleteTodo = (id) => {
    const todoRef = ref(database, `todos/${id}`);
    remove(todoRef);
  };

  // Set tugasan untuk diedit
  const handleEditClick = (todo) => {
    setEditingTodo(todo);
  };

  // Batal edit
  const handleCancelEdit = () => {
    setEditingTodo(null);
  };

  return (
    <div className="todo-container">
      <h1>📝 Senarai Tugasan</h1>

      <TodoForm
        onSubmit={
          editingTodo
            ? (text) => handleUpdateTodo(editingTodo.id, text)
            : handleAddTodo
        }
        editingTodo={editingTodo}
        onCancel={handleCancelEdit}
      />

      <div className="todo-list">
        {todos.length === 0 ? (
          <p className="empty-message">
            Tiada tugasan. Tambah tugasan pertama anda!
          </p>
        ) : (
          todos.map((todo) => (
            <TodoItem
              key={todo.id}
              todo={todo}
              onToggle={handleToggleComplete}
              onEdit={handleEditClick}
              onDelete={handleDeleteTodo}
            />
          ))
        )}
      </div>

      <div className="todo-stats">
        <p>
          Jumlah: {todos.length} | Selesai:{" "}
          {todos.filter((t) => t.completed).length}
        </p>
      </div>
    </div>
  );
}

export default TodoList;
