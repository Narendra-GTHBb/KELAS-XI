package com.gymecommerce.musclecart.data.remote.dto

import com.google.gson.annotations.SerializedName

data class OrderDto(
    @SerializedName("id") 
    val id: Int = 0,
    
    @SerializedName("order_number")
    val orderNumber: String = "",
    
    @SerializedName("user_id") 
    val userId: Int = 0,
    
    @SerializedName("user")
    val user: UserDto? = null,
    
    @SerializedName("total_amount") 
    val totalAmount: Double = 0.0,
    
    @SerializedName("tax_amount")
    val taxAmount: Double = 0.0,
    
    @SerializedName("shipping_amount")
    val shippingAmount: Double = 0.0,

    @SerializedName("discount_amount")
    val discountAmount: Double = 0.0,

    @SerializedName("voucher_code")
    val voucherCode: String? = null,

    @SerializedName("points_earned")
    val pointsEarned: Int = 0,

    @SerializedName("points_used")
    val pointsUsed: Int = 0,

    @SerializedName("final_price")
    val finalPrice: Double = 0.0,
    
    @SerializedName("status") 
    val status: String = "pending",
    
    @SerializedName("payment_status")
    val paymentStatus: String = "pending",
    
    @SerializedName("payment_method")
    val paymentMethod: String = "cash",
    
    @SerializedName("shipping_address") 
    val shippingAddress: Any? = null, // Can be String or Object
    
    @SerializedName("billing_address")
    val billingAddress: Any? = null,
    
    @SerializedName("notes")
    val notes: String? = null,
    
    @SerializedName("order_items")
    val orderItems: List<OrderItemDto>? = null,
    
    @SerializedName("tracking_number")
    val trackingNumber: String? = null,

    @SerializedName("courier")
    val courier: String? = null,

    @SerializedName("shipped_at")
    val shippedAt: String? = null,

    @SerializedName("delivered_at")
    val deliveredAt: String? = null,

    @SerializedName("paid_at")
    val paidAt: String? = null,

    @SerializedName("completed_at")
    val completedAt: String? = null,

    @SerializedName("status_history")
    val statusHistory: List<OrderStatusHistoryDto>? = null,

    @SerializedName("created_at")
    val createdAt: String? = null,

    @SerializedName("updated_at")
    val updatedAt: String? = null
)

data class OrderStatusHistoryDto(
    @SerializedName("status")
    val status: String = "",

    @SerializedName("previous_status")
    val previousStatus: String? = null,

    @SerializedName("note")
    val note: String? = null,

    @SerializedName("changed_by_role")
    val changedByRole: String? = null,

    @SerializedName("created_at")
    val createdAt: String? = null
)

data class OrderItemDto(
    @SerializedName("id")
    val id: Int = 0,
    
    @SerializedName("order_id")
    val orderId: Int = 0,
    
    @SerializedName("product_id")
    val productId: Int = 0,
    
    @SerializedName("product")
    val product: ProductDto? = null,
    
    @SerializedName("product_name")
    val productName: String = "",
    
    @SerializedName("quantity")
    val quantity: Int = 0,
    
    @SerializedName("price")
    val price: Double = 0.0,
    
    @SerializedName("total")
    val total: Double = 0.0
)
