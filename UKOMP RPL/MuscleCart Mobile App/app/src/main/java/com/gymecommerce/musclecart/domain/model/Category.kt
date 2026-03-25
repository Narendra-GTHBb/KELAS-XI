package com.gymecommerce.musclecart.domain.model

data class Category(
    val id: Int,
    val name: String,
    val description: String,
    val imageUrl: String,
    val createdAt: Long,
    val updatedAt: Long
)