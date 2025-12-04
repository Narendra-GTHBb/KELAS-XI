package com.apkfood.wavesoffood.data.model

import com.google.firebase.Timestamp

/**
 * Data class untuk Food item
 */
data class Food(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "",
    val categoryId: String = "",
    val categoryName: String = "",
    val rating: Double = 0.0,
    val reviewCount: Int = 0,
    val deliveryTime: String = "",
    val isPopular: Boolean = false,
    val isRecommended: Boolean = false,
    val isAvailable: Boolean = true,
    val ingredients: List<String> = emptyList(),
    val nutritionInfo: NutritionInfo? = null,
    val createdAt: Any? = null, // Can be Timestamp or Long
    val updatedAt: Any? = null, // Can be Timestamp or Long
    val preparationTime: Int = 0 // Add missing field from Firebase
) {
    // No-argument constructor for Firebase
    constructor() : this("", "", "", 0.0, "", "", "", 0.0, 0, "", false, false, true, emptyList(), null, null, null, 0)
    
    // Helper function to get createdAt as Long
    fun getCreatedAtLong(): Long {
        return when (createdAt) {
            is Timestamp -> createdAt.seconds * 1000
            is Long -> createdAt
            else -> System.currentTimeMillis()
        }
    }
    
    // Helper function to get updatedAt as Long  
    fun getUpdatedAtLong(): Long {
        return when (updatedAt) {
            is Timestamp -> updatedAt.seconds * 1000
            is Long -> updatedAt
            else -> System.currentTimeMillis()
        }
    }
}

/**
 * Data class untuk informasi nutrisi
 */
data class NutritionInfo(
    val calories: Int = 0,
    val protein: Double = 0.0,
    val carbs: Double = 0.0,
    val fat: Double = 0.0,
    val fiber: Double = 0.0
) {
    constructor() : this(0, 0.0, 0.0, 0.0, 0.0)
}
