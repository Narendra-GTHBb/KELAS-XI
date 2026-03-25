package com.gymecommerce.musclecart.domain.model

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

data class CartItem(
    val productId: Int,
    val product: Product,
    val quantity: Int
) {
    fun getTotalPrice(): Double = product.price * quantity
    fun getFormattedTotalPrice(): String {
        val symbols = DecimalFormatSymbols(Locale("id", "ID"))
        val formatter = DecimalFormat("#,###", symbols)
        return "Rp ${formatter.format(getTotalPrice().toLong())}"
    }
    
    fun canIncreaseQuantity(): Boolean = quantity < product.stock
    fun canDecreaseQuantity(): Boolean = quantity > 1
    
    fun withUpdatedQuantity(newQuantity: Int): CartItem {
        return copy(quantity = newQuantity.coerceIn(1, product.stock))
    }
}

data class Cart(
    val items: List<CartItem> = emptyList()
) {
    fun getSubtotal(): Double = items.sumOf { it.getTotalPrice() }
    fun getTotalPrice(): Double = getSubtotal()
    fun getTaxAmount(): Double = getSubtotal() * 0.10
    fun getShippingAmount(): Double = 0.0
    fun getGrandTotal(): Double = getSubtotal() + getTaxAmount() + getShippingAmount()

    fun getFormattedTotalPrice(): String = formatRp(getTotalPrice())
    fun getFormattedSubtotal(): String = formatRp(getSubtotal())
    fun getFormattedTax(): String = formatRp(getTaxAmount())
    fun getFormattedShipping(): String = if (getShippingAmount() == 0.0) "Gratis" else formatRp(getShippingAmount())
    fun getFormattedGrandTotal(): String = formatRp(getGrandTotal())

    private fun formatRp(amount: Double): String {
        val symbols = DecimalFormatSymbols(Locale("id", "ID"))
        val formatter = DecimalFormat("#,###", symbols)
        return "Rp ${formatter.format(amount.toLong())}"
    }

    fun getTotalItems(): Int = items.sumOf { it.quantity }
    fun isEmpty(): Boolean = items.isEmpty()
    fun isNotEmpty(): Boolean = items.isNotEmpty()

    fun getItemByProductId(productId: Int): CartItem? = items.find { it.productId == productId }
    fun hasProduct(productId: Int): Boolean = items.any { it.productId == productId }
}