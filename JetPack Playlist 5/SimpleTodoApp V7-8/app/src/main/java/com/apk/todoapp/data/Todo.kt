package com.apk.todoapp.data

import java.text.SimpleDateFormat
import java.util.*

data class Todo(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun getFormattedDate(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault())
        return dateFormat.format(Date(createdAt))
    }
}