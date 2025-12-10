package com.apkfood.wavesoffood.model

/**
 * Data class untuk Category
 * Merepresentasikan kategori makanan
 */
data class Category(
    val id: String = "",
    val name: String = "",
    val imageUrl: String = "",
    val description: String = "",
    val isActive: Boolean = true,
    val sortOrder: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
) {
    // Constructor tanpa parameter untuk Firebase
    constructor() : this("", "", "", "", true, 0, 0)
}
