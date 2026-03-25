package com.gymecommerce.musclecart.domain.usecase.cart

import com.gymecommerce.musclecart.domain.model.Result
import com.gymecommerce.musclecart.domain.repository.CartRepository
import javax.inject.Inject

class UpdateCartQuantityUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(productId: Int, quantity: Int): Result<Unit> {
        if (productId <= 0) {
            return Result.Error("Invalid product ID")
        }

        if (quantity < 0) {
            return Result.Error("Quantity cannot be negative")
        }

        // If quantity is 0, remove the item
        if (quantity == 0) {
            return cartRepository.removeFromCart(productId)
        }

        // Directly update via repository - item is visible in cart so it exists on server
        return cartRepository.updateCartItemQuantity(productId, quantity)
    }
}