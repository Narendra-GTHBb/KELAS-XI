package com.gymecommerce.musclecart.domain.usecase.order

import com.gymecommerce.musclecart.domain.model.Order
import com.gymecommerce.musclecart.domain.model.OrderItem
import com.gymecommerce.musclecart.domain.model.OrderStatus
import com.gymecommerce.musclecart.domain.model.Result
import com.gymecommerce.musclecart.domain.repository.AuthRepository
import com.gymecommerce.musclecart.domain.repository.CartRepository
import com.gymecommerce.musclecart.domain.repository.OrderRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

data class CheckoutRequest(
    val shippingAddress: String,
    val paymentMethod: String = "Cash on Delivery",
    val shippingCost: Int = 0,
    val courier: String? = null,
    val courierService: String? = null,
    val destinationCityId: String? = null,
    val voucherCode: String? = null,
    val discountAmount: Int = 0,
    val pointsUsed: Int = 0
)

class ProcessCheckoutUseCase @Inject constructor(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(checkoutRequest: CheckoutRequest): Result<Order> {
        try {
            if (checkoutRequest.shippingAddress.isBlank()) {
                return Result.Error("Shipping address is required")
            }

            val currentUser = authRepository.getCurrentUser()
                ?: return Result.Error("User not logged in")

            val cart = cartRepository.getCart().first()
            if (cart.isEmpty()) {
                return Result.Error("Cart is empty")
            }

            val orderItems = cart.items.map { cartItem ->
                OrderItem(
                    id = 0,
                    orderId = 0,
                    productId = cartItem.productId,
                    product = cartItem.product,
                    quantity = cartItem.quantity,
                    price = cartItem.product.price,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
            }

            val order = Order(
                id = 0,
                userId = currentUser.id,
                totalPrice = cart.getTotalPrice(),
                status = OrderStatus.PENDING,
                shippingAddress = checkoutRequest.shippingAddress,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
                isSynced = false,
                items = orderItems,
                shippingCost = checkoutRequest.shippingCost,
                courier = checkoutRequest.courier,
                courierService = checkoutRequest.courierService,
                destinationCityId = checkoutRequest.destinationCityId,
                voucherCode = checkoutRequest.voucherCode,
                discountAmount = checkoutRequest.discountAmount,
                pointsUsed = checkoutRequest.pointsUsed
            )

            // Backend handles stock validation, stock reduction, and cart clearing server-side
            return when (val orderResult = orderRepository.createOrder(order)) {
                is Result.Success -> {
                    cartRepository.clearCart() // clear local cache
                    Result.Success(orderResult.data)
                }
                is Result.Error -> Result.Error("Failed to create order: ${orderResult.message}")
                else -> Result.Error("Order creation in progress, try again later")
            }
        } catch (e: Exception) {
            return Result.Error("Checkout failed: ${e.message}")
        }
    }
}