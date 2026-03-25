package com.gymecommerce.musclecart.domain.usecase.cart

import com.gymecommerce.musclecart.domain.model.Result
import com.gymecommerce.musclecart.domain.repository.CartRepository
import javax.inject.Inject

class ClearCartUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return cartRepository.clearCart()
    }
}