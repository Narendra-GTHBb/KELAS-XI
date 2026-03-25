package com.gymecommerce.musclecart.domain.model

data class Favorite(
    val id: Int,
    val userId: Int,
    val productId: Int,
    val product: Product,
    val createdAt: Long
)
