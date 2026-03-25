package com.gymecommerce.musclecart.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "orders",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["userId"]),
        Index(value = ["status"]),
        Index(value = ["createdAt"])
    ]
)
data class OrderEntity(
    @PrimaryKey
    val id: Int,
    val userId: Int,
    val totalAmount: Double,
    val status: String,
    val shippingAddress: String,
    val isSynced: Boolean = true,   // ✅ TAMBAH KOMA DI SINI
    val paymentMethod: String,
    val notes: String?,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)