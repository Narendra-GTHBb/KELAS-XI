package com.apkfood.wavesoffood.model

/**
 * Data class untuk User
 * Merepresentasikan data pengguna aplikasi
 */
data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val address: String = "",
    val profileImageUrl: String = "",
    val isAdmin: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    // Constructor tanpa parameter untuk Firebase
    constructor() : this("", "", "", "", "", "", false, 0)
}
