package com.gymecommerce.musclecart.domain.usecase.admin

import com.gymecommerce.musclecart.domain.model.OrderStatus
import com.gymecommerce.musclecart.domain.model.Result
import com.gymecommerce.musclecart.domain.repository.OrderRepository
import com.gymecommerce.musclecart.domain.repository.ProductRepository
import com.gymecommerce.musclecart.domain.repository.UserRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

data class AdminStats(
    val totalProducts: Int,
    val inStockProducts: Int,
    val lowStockProducts: Int,
    val totalOrders: Int,
    val pendingOrders: Int,
    val completedOrders: Int,
    val totalRevenue: Double,
    val totalUsers: Int
) {
    fun getFormattedRevenue(): String = "$%.2f".format(totalRevenue)

    fun getStockPercentage(): Float =
        if (totalProducts > 0)
            (inStockProducts.toFloat() / totalProducts) * 100
        else 0f

    fun getOrderCompletionRate(): Float =
        if (totalOrders > 0)
            (completedOrders.toFloat() / totalOrders) * 100
        else 0f
}

class GetAdminStatsUseCase @Inject constructor(
    private val productRepository: ProductRepository,
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(): Result<AdminStats> {
        return try {

            // PRODUCT STATS
            val totalProductsResult = productRepository.getProductCount()
            val inStockProductsResult = productRepository.getInStockProductCount()
            val lowStockProductsFlow = productRepository.getLowStockProducts(5)

            // ORDER STATS
            val totalOrdersResult = orderRepository.getOrderCount()
            val pendingOrdersResult =
                orderRepository.getOrderCountByStatus(OrderStatus.PENDING)
            val completedOrdersResult =
                orderRepository.getOrderCountByStatus(OrderStatus.COMPLETED)
            val totalRevenueResult = orderRepository.getTotalRevenue()

            // USER STATS
            val totalUsersResult = userRepository.getUserCount()

            // CHECK ERROR RESULT
            val results = listOf(
                totalProductsResult,
                inStockProductsResult,
                totalOrdersResult,
                pendingOrdersResult,
                completedOrdersResult,
                totalRevenueResult,
                totalUsersResult
            )

            val firstError =
                results.firstOrNull { it is Result.Error } as? Result.Error

            if (firstError != null) {
                return Result.Error(
                    "Failed to load admin statistics: ${firstError.message}"
                )
            }

            // HANDLE FLOW (LOW STOCK)
            val lowStockCount = try {
                lowStockProductsFlow.first().size
            } catch (e: Exception) {
                0
            }

            val stats = AdminStats(
                totalProducts = (totalProductsResult as Result.Success).data,
                inStockProducts = (inStockProductsResult as Result.Success).data,
                lowStockProducts = lowStockCount,
                totalOrders = (totalOrdersResult as Result.Success).data,
                pendingOrders = (pendingOrdersResult as Result.Success).data,
                completedOrders = (completedOrdersResult as Result.Success).data,
                totalRevenue = (totalRevenueResult as Result.Success).data,
                totalUsers = (totalUsersResult as Result.Success).data
            )

            Result.Success(stats)

        } catch (e: Exception) {
            Result.Error(
                "Failed to calculate admin statistics: ${e.message}"
            )
        }
    }

    suspend fun getRevenueByDateRange(
        startDate: Long,
        endDate: Long
    ): Result<Double> {
        return orderRepository.getRevenueByDateRange(startDate, endDate)
    }
}