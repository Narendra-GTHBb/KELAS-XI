package com.apkfood.wavesoffood.model

/**
 * Data class untuk Food Item
 * Merepresentasikan item makanan dalam aplikasi
 */
data class FoodItem(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "",
    val category: String = "",
    val ingredients: List<String> = emptyList(),
    val isPopular: Boolean = false,
    val isAvailable: Boolean = true,
    val rating: Double = 0.0,
    val preparationTime: Int = 0, // dalam menit
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    // Constructor tanpa parameter untuk Firebase
    constructor() : this("", "", "", 0.0, "", "", emptyList(), false, true, 0.0, 0, 0, 0)
}
