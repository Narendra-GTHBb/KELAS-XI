package com.gymecommerce.musclecart.domain.usecase.order

import com.gymecommerce.musclecart.domain.model.Order
import com.gymecommerce.musclecart.domain.model.OrderStatus
import com.gymecommerce.musclecart.domain.model.Result
import com.gymecommerce.musclecart.domain.repository.AuthRepository
import com.gymecommerce.musclecart.domain.repository.OrderRepository
import javax.inject.Inject

class GetOrderHistoryUseCase @Inject constructor(
    private val orderRepository: OrderRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<List<Order>> {
        val currentUser = authRepository.getCurrentUser()
            ?: return Result.Error("User belum login")
        return when (val result = orderRepository.fetchOrdersFromApi()) {
            is Result.Success -> Result.Success(
                result.data.sortedByDescending { it.createdAt }
            )
            is Result.Error -> result
            is Result.Loading -> Result.Error("Loading...")
        }
    }

    suspend fun getOrderById(orderId: Int): Result<Order?> {
        return orderRepository.fetchOrderByIdFromApi(orderId)
    }
}