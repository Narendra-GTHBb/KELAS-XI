package com.gymecommerce.musclecart.domain.usecase.cart

import com.gymecommerce.musclecart.domain.model.Result
import com.gymecommerce.musclecart.domain.repository.CartRepository
import javax.inject.Inject

class RemoveFromCartUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {

    suspend operator fun invoke(productId: Int): Result<Unit> {

        if (productId <= 0) {
            return Result.Error("Invalid product ID")
        }

        // Directly remove via repository - no need to pre-check local DB
        return cartRepository.removeFromCart(productId)
    }
}