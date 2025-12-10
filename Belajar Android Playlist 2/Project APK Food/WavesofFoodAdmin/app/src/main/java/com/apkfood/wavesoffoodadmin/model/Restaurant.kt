package com.apkfood.wavesoffoodadmin.model

data class Restaurant(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val address: String = "",
    val phone: String = "",
    val email: String = "",
    val category: String = "",
    val rating: Double = 0.0,
    val isActive: Boolean = true,
    val ownerId: String = "",
    val ownerName: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
