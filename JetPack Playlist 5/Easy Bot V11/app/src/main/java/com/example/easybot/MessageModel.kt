package com.example.easybot

data class MessageModel(
    val message: String,
    val role: String
) {
    companion object {
        const val ROLE_USER = "user"
        const val ROLE_MODEL = "model"
    }
}
