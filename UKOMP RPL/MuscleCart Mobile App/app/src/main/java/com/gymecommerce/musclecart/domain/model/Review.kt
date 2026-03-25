package com.gymecommerce.musclecart.domain.model

data class Review(
    val id: Int,
    val rating: Int,
    val comment: String?,
    val userName: String,
    val createdAt: Long
)
