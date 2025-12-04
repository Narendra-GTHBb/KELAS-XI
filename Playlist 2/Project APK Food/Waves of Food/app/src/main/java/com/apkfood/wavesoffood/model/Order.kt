package com.apkfood.wavesoffood.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Data class untuk Order
 * Merepresentasikan pesanan pengguna
 */
@Parcelize
data class Order(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val userEmail: String = "",
    val userPhone: String = "",
    val orderNumber: String = "", // Firebase order number (WOF format)
    val deliveryAddress: String = "",
    val items: List<CartItem> = emptyList(),
    val totalAmount: Double = 0.0,
    val orderStatus: OrderStatus = OrderStatus.PENDING,
    val orderDate: Long = System.currentTimeMillis(),
    val estimatedDeliveryTime: Long = 0L,
    val paymentMethod: String = "",
    val notes: String = ""
) : Parcelable {
    
    // Constructor tanpa parameter untuk Firebase
    constructor() : this(
        id = "",
        userId = "",
        userName = "",
        userEmail = "",
        userPhone = "",
        orderNumber = "",
        deliveryAddress = "",
        items = emptyList(),
        totalAmount = 0.0
    )
}

/**
 * Enum untuk status pesanan
 */
@Parcelize
enum class OrderStatus : Parcelable {
    PENDING,      // Menunggu konfirmasi
    CONFIRMED,    // Dikonfirmasi
    PREPARING,    // Sedang dipreparasi
    ON_THE_WAY,   // Dalam perjalanan
    DELIVERED,    // Sudah diantar
    CANCELLED     // Dibatalkan
}
