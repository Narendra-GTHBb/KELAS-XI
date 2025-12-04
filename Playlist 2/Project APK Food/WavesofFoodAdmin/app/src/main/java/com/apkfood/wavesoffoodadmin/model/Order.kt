package com.apkfood.wavesoffoodadmin.model

import com.google.firebase.Timestamp

data class Order(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val userPhone: String = "",
    val phoneNumber: String = "", // Firebase field
    val orderNumber: String = "", // Firebase field
    val restaurantId: String = "",
    val restaurantName: String = "",
    val items: List<OrderItem> = emptyList(),
    val subtotal: Double = 0.0, // Firebase field
    val deliveryFee: Double = 0.0,
    val tax: Double = 0.0, // Firebase field
    val totalAmount: Double = 0.0,
    val status: String = "", // Firebase uses string
    val orderStatus: OrderStatus = OrderStatus.PENDING, // For app logic
    val paymentMethod: String = "",
    val paymentStatus: PaymentStatus = PaymentStatus.PENDING,
    val deliveryAddress: String = "",
    val notes: String = "",
    val orderDate: String = "", // Firebase uses ISO string
    val orderDateTimestamp: Long = System.currentTimeMillis(), // For app logic
    val estimatedDeliveryTime: String = "",
    val actualDeliveryTime: String = "",
    val deliveryTime: Long = 0L,
    val completedAt: Long = 0L,
    val createdAt: Any? = null, // Can be Long or Timestamp
    val updatedAt: Any? = null
) {
    // Helper functions for consistent data access
    fun getPhoneDisplay(): String = if (phoneNumber.isNotEmpty()) phoneNumber else userPhone
    
    fun getOrderStatusEnum(): OrderStatus {
        return when (status.lowercase()) {
            "pending" -> OrderStatus.PENDING
            "confirmed" -> OrderStatus.CONFIRMED
            "preparing" -> OrderStatus.PREPARING
            "ready" -> OrderStatus.READY
            "out_for_delivery" -> OrderStatus.OUT_FOR_DELIVERY
            "delivered" -> OrderStatus.DELIVERED
            "cancelled" -> OrderStatus.CANCELLED
            else -> OrderStatus.PENDING
        }
    }
    
    fun getOrderDateLong(): Long {
        return when {
            orderDateTimestamp > 0 -> orderDateTimestamp
            orderDate.isNotEmpty() -> {
                try {
                    // Parse ISO date string to timestamp
                    java.time.Instant.parse(orderDate).toEpochMilli()
                } catch (e: Exception) {
                    System.currentTimeMillis()
                }
            }
            createdAt is Long -> createdAt
            createdAt is Timestamp -> createdAt.seconds * 1000
            else -> System.currentTimeMillis()
        }
    }
    
    fun getFormattedTotal(): String {
        val formatter = java.text.NumberFormat.getCurrencyInstance(java.util.Locale("id", "ID"))
        return formatter.format(totalAmount).replace("IDR", "Rp")
    }
}

data class OrderItem(
    val foodId: String = "",
    val foodName: String = "",
    val price: Double = 0.0,
    val quantity: Int = 0,
    val subtotal: Double = 0.0, // Firebase field
    val totalPrice: Double = 0.0, // App field
    val imageUrl: String = ""
) {
    // Helper to get consistent total
    fun getItemTotal(): Double = if (subtotal > 0) subtotal else totalPrice
}

enum class OrderStatus {
    PENDING,
    CONFIRMED,
    PREPARING,
    READY,
    OUT_FOR_DELIVERY,
    DELIVERED,
    CANCELLED
}

enum class PaymentStatus {
    PENDING,
    PAID,
    FAILED,
    REFUNDED
}
