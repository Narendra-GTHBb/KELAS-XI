package com.gymecommerce.musclecart.domain.usecase.cart

import com.gymecommerce.musclecart.domain.model.Result
import com.gymecommerce.musclecart.domain.repository.CartRepository
import com.gymecommerce.musclecart.domain.repository.ProductRepository
import javax.inject.Inject

class AddToCartUseCase @Inject constructor(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository
) {

    suspend operator fun invoke(
        productId: Int,
        quantity: Int
    ): Result<Unit> {

        // Basic validation
        if (productId <= 0) {
            return Result.Error("Invalid product ID")
        }

        if (quantity <= 0) {
            return Result.Error("Quantity must be greater than 0")
        }

        // Let backend handle stock validation and other business logic
        // This is more reliable than client-side validation
        return cartRepository.addToCart(productId, quantity)
    }
}