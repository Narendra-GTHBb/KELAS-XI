package com.gymecommerce.musclecart.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CartDto(
    @SerializedName("items")
    val items: List<CartItemDto> = emptyList(),
    @SerializedName("total")
    val total: Double = 0.0,
    @SerializedName("summary")
    val summary: CartSummaryDto? = null
)

data class CartSummaryDto(
    @SerializedName("total_items")
    val totalItems: Int = 0,
    @SerializedName("subtotal")
    val subtotal: Double = 0.0,
    @SerializedName("total")
    val total: Double = 0.0
)

data class CartItemDto(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("product_id")
    val productId: Int = 0,
    @SerializedName("product")
    val product: ProductDto? = null,
    @SerializedName("quantity")
    val quantity: Int = 0,
    @SerializedName("price")
    val price: Double = 0.0,
    @SerializedName("subtotal")
    val subtotal: Double = 0.0
) {
    /** productId from JSON or fallback to product.id */
    fun resolvedProductId(): Int = if (productId > 0) productId else (product?.id ?: 0)
}
