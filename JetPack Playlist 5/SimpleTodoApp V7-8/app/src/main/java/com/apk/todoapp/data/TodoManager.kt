package com.apk.todoapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object TodoManager {
    private val _todoList = MutableLiveData<List<Todo>>()
    val todoList: LiveData<List<Todo>> = _todoList
    
    init {
        // Initialize with some sample todos for demonstration
        _todoList.value = listOf(
            Todo(title = "asaaq"),
            Todo(title = "88995"),
            Todo(title = "90900"),
            Todo(title = "asfafs")
        )
    }

    fun addTodo(title: String) {
        val currentList = _todoList.value ?: emptyList()
        val newTodo = Todo(title = title)
        _todoList.value = listOf(newTodo) + currentList // Add new todo at the beginning
    }

    fun deleteTodo(todoId: String) {
        val currentList = _todoList.value ?: emptyList()
        _todoList.value = currentList.filter { it.id != todoId }
    }

    fun getTodoList(): List<Todo> {
        return _todoList.value ?: emptyList()
    }
}