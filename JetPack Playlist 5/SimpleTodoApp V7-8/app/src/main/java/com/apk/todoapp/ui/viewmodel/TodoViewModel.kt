package com.apk.todoapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.apk.todoapp.data.Todo
import com.apk.todoapp.data.TodoManager

class TodoViewModel : ViewModel() {
    val todoList: LiveData<List<Todo>> = TodoManager.todoList

    fun addTodo(title: String) {
        if (title.isNotBlank()) {
            TodoManager.addTodo(title.trim())
        }
    }

    fun deleteTodo(todoId: String) {
        TodoManager.deleteTodo(todoId)
    }
}