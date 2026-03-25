package com.gymecommerce.musclecart.domain.model

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

data class Product(
    val id: Int,
    val name: String,
    val price: Double,
    val stock: Int,
    val description: String,
    val imageUrl: String,
    val categoryId: Int,
    val category: Category? = null,
    val createdAt: Long,
    val updatedAt: Long,
    val lastSyncTime: Long,
    val avgRating: Double = 0.0,
    val totalReviews: Int = 0
) {
    fun isInStock(quantity: Int = 1): Boolean = stock >= quantity
    fun isOutOfStock(): Boolean = stock <= 0
    fun isLowStock(threshold: Int = 5): Boolean = stock <= threshold
    
    fun getFormattedPrice(): String {
        val symbols = DecimalFormatSymbols(Locale("id", "ID"))
        val formatter = DecimalFormat("#,###", symbols)
        return "Rp ${formatter.format(price.toLong())}"
    }
    
    fun getStockStatus(): StockStatus {
        return when {
            isOutOfStock() -> StockStatus.OUT_OF_STOCK
            isLowStock() -> StockStatus.LOW_STOCK
            else -> StockStatus.IN_STOCK
        }
    }
}

enum class StockStatus {
    IN_STOCK,
    LOW_STOCK,
    OUT_OF_STOCK
}