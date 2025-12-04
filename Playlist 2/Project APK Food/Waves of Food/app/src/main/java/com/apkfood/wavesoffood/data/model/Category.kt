package com.apkfood.wavesoffood.data.model

/**
 * Data class untuk Category makanan
 */
data class Category(
    val id: String = "",
    val name: String = "",
    val icon: String = "",
    val description: String = "",
    val isActive: Boolean = true,
    val sortOrder: Int = 0
) {
    // No-argument constructor for Firebase
    constructor() : this("", "", "", "", true, 0)
}
