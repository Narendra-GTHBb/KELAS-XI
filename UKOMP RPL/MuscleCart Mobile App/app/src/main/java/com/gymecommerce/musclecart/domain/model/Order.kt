package com.gymecommerce.musclecart.domain.model

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

data class OrderStatusHistoryItem(
    val status: String,
    val previousStatus: String?,
    val note: String?,
    val changedByRole: String?,
    val createdAt: Long
)

data class Order(
    val id: Int,
    val userId: Int,
    val totalPrice: Double,
    val status: OrderStatus,
    val shippingAddress: String,
    val createdAt: Long,
    val updatedAt: Long,
    val isSynced: Boolean,
    val items: List<OrderItem> = emptyList(),
    val trackingNumber: String? = null,
    val courier: String? = null,
    val statusHistory: List<OrderStatusHistoryItem> = emptyList(),
    // checkout fields (not persisted on backend model, used only during order creation)
    val shippingCost: Int = 0,
    val taxAmount: Int = 0,
    val discountAmount: Int = 0,
    val voucherCode: String? = null,
    val pointsEarned: Int = 0,
    val pointsUsed: Int = 0,
    val finalPrice: Double = 0.0,
    val courierService: String? = null,
    val destinationCityId: String? = null
) {
    /** Returns the actual amount paid (after points & voucher discounts). */
    fun getActualTotal(): Double = if (finalPrice > 0.0) finalPrice else totalPrice

    fun getFormattedTotalPrice(): String {
        val symbols = DecimalFormatSymbols(Locale("id", "ID"))
        val formatter = DecimalFormat("#,###", symbols)
        return "Rp ${formatter.format(getActualTotal().toLong())}"
    }
    fun getTotalItems(): Int = items.sumOf { it.quantity }

    fun canBeCancelled(): Boolean = status == OrderStatus.PENDING
    fun isCompleted(): Boolean = status == OrderStatus.COMPLETED
    fun isPending(): Boolean = status == OrderStatus.PENDING
    fun isPaid(): Boolean = status == OrderStatus.PAID
    fun isShipped(): Boolean = status == OrderStatus.SHIPPED
    fun isDelivered(): Boolean = status == OrderStatus.DELIVERED
    fun canConfirmReceived(): Boolean = status == OrderStatus.DELIVERED

    fun getStatusColor(): String {
        return when (status) {
            OrderStatus.PENDING    -> "#FFA500"
            OrderStatus.PAID       -> "#2563EB"
            OrderStatus.PROCESSING -> "#6366F1"
            OrderStatus.SHIPPED    -> "#7C3AED"
            OrderStatus.DELIVERED  -> "#059669"
            OrderStatus.COMPLETED  -> "#065F46"
            OrderStatus.CANCELLED  -> "#DC2626"
        }
    }
}

data class OrderItem(
    val id: Int,
    val orderId: Int,
    val productId: Int,
    val product: Product? = null,
    val quantity: Int,
    val price: Double,
    val createdAt: Long,
    val updatedAt: Long
) {
    fun getTotalPrice(): Double = price * quantity
    fun getFormattedTotalPrice(): String {
        val symbols = DecimalFormatSymbols(Locale("id", "ID"))
        val formatter = DecimalFormat("#,###", symbols)
        return "Rp ${formatter.format(getTotalPrice().toLong())}"
    }
    fun getFormattedPrice(): String {
        val symbols = DecimalFormatSymbols(Locale("id", "ID"))
        val formatter = DecimalFormat("#,###", symbols)
        return "Rp ${formatter.format(price.toLong())}"
    }
}

enum class OrderStatus {
    PENDING,
    PAID,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    COMPLETED,
    CANCELLED;

    companion object {
        fun fromString(status: String): OrderStatus {
            return when (status.lowercase()) {
                "pending"    -> PENDING
                "paid"       -> PAID
                "processing" -> PROCESSING
                "shipped"    -> SHIPPED
                "delivered"  -> DELIVERED
                "completed"  -> COMPLETED
                "cancelled"  -> CANCELLED
                else         -> PENDING
            }
        }
    }

    override fun toString(): String = name.lowercase()

    fun getDisplayName(): String = when (this) {
        PENDING    -> "Menunggu Pembayaran"
        PAID       -> "Sudah Dibayar"
        PROCESSING -> "Diproses"
        SHIPPED    -> "Dikirim"
        DELIVERED  -> "Tiba di Tujuan"
        COMPLETED  -> "Selesai"
        CANCELLED  -> "Dibatalkan"
    }
}