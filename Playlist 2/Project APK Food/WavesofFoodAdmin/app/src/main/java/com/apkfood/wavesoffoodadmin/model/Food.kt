package com.apkfood.wavesoffoodadmin.model

import com.google.firebase.Timestamp

data class Food(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "", // Base64 encoded image
    val categoryId: String = "",
    val ingredients: List<String> = emptyList(),
    val isAvailable: Boolean = true,
    val isPopular: Boolean = false,
    val preparationTime: Int = 0, // dalam menit
    val rating: Double = 0.0,
    val createdAt: Any? = null, // Can be Long or Timestamp
    val updatedAt: Any? = null // Can be Long or Timestamp
) {
    // No-argument constructor for Firebase
    constructor() : this("", "", "", 0.0, "", "", emptyList(), true, false, 0, 0.0, null, null)
    
    // Helper functions to get timestamps as Long
    fun getCreatedAtLong(): Long {
        return when (createdAt) {
            is Long -> createdAt
            is Timestamp -> createdAt.toDate().time
            else -> System.currentTimeMillis()
        }
    }
    
    fun getUpdatedAtLong(): Long {
        return when (updatedAt) {
            is Long -> updatedAt
            is Timestamp -> updatedAt.toDate().time
            else -> System.currentTimeMillis()
        }
    }
}

data class FoodCategory(
    val id: String = "",
    val name: String = "",
    val imageUrl: String = "",
    val isActive: Boolean = true
)
