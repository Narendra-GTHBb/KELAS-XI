package com.gymecommerce.musclecart.domain.usecase.cart

import com.gymecommerce.musclecart.domain.model.Cart
import com.gymecommerce.musclecart.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCartItemsUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(): Flow<Cart> {
        return cartRepository.getCart()
    }
    
    suspend fun getCartItemCount(): Flow<Int> {
        return cartRepository.getCartItemCount()
    }
    
    suspend fun getCartTotal(): Flow<Double> {
        return cartRepository.getCartTotal()
    }
    
    suspend fun isProductInCart(productId: Int): Flow<Boolean> {
        return cartRepository.isProductInCart(productId)
    }
}