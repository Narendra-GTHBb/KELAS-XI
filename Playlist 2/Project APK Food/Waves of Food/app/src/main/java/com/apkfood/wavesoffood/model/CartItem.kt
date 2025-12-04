package com.apkfood.wavesoffood.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import com.apkfood.wavesoffood.data.model.Food

/**
 * Data class untuk Cart Item
 * Merepresentasikan item dalam keranjang belanja
 */
@Parcelize
data class CartItem(
    val food: @kotlinx.parcelize.RawValue Food,
    var quantity: Int = 1
) : Parcelable {
    // Constructor tanpa parameter untuk Firebase
    constructor() : this(Food(), 1)
    
    /**
     * Mendapatkan total harga item (harga x quantity)
     */
    fun getTotalPrice(): Double {
        return food.price * quantity
    }
    
    /**
     * Mendapatkan ID dari food
     */
    fun getFoodId(): String {
        return food.id
    }
}
