package com.gymecommerce.musclecart.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "products",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["categoryId"]),
        Index(value = ["name"]),
        Index(value = ["price"])
    ]
)
data class ProductEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String,
    val categoryId: Int,
    val stockQuantity: Int,
    val isActive: Boolean = true,
    val avgRating: Double = 0.0,
    val totalReviews: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)