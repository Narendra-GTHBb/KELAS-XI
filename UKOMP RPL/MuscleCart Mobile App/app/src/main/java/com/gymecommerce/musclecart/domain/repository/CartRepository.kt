package com.gymecommerce.musclecart.domain.repository

import com.gymecommerce.musclecart.domain.model.Cart
import com.gymecommerce.musclecart.domain.model.CartItem
import com.gymecommerce.musclecart.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    
    suspend fun getCart(): Flow<Cart>
    
    suspend fun addToCart(productId: Int, quantity: Int): Result<Unit>
    
    suspend fun removeFromCart(productId: Int): Result<Unit>
    
    suspend fun updateCartItemQuantity(productId: Int, quantity: Int): Result<Unit>
    
    suspend fun clearCart(): Result<Unit>
    
    suspend fun getCartItemCount(): Flow<Int>
    
    suspend fun getCartTotal(): Flow<Double>
    
    suspend fun isProductInCart(productId: Int): Flow<Boolean>
    
    suspend fun getCartItem(productId: Int): Result<CartItem?>
    
    suspend fun syncCart(): Result<Unit>
}