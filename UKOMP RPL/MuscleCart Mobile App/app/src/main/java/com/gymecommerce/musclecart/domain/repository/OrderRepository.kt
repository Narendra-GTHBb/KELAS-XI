package com.gymecommerce.musclecart.domain.repository

import com.gymecommerce.musclecart.domain.model.Order
import com.gymecommerce.musclecart.domain.model.OrderStatus
import com.gymecommerce.musclecart.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    
    suspend fun getOrders(): Flow<List<Order>>
    
    suspend fun getOrdersByUserId(userId: Int): Flow<List<Order>>
    
    suspend fun getOrderById(orderId: Int): Result<Order?>
    
    suspend fun getOrdersByStatus(status: OrderStatus): Flow<List<Order>>
    
    suspend fun getPendingOrders(): Flow<List<Order>>
    
    suspend fun getCompletedOrders(): Flow<List<Order>>
    
    suspend fun createOrder(order: Order): Result<Order>
    
    suspend fun updateOrder(order: Order): Result<Order>
    
    suspend fun updateOrderStatus(orderId: Int, status: OrderStatus): Result<Unit>
    
    suspend fun deleteOrder(orderId: Int): Result<Unit>
    
    suspend fun getOrderCount(): Result<Int>
    
    suspend fun getOrderCountByStatus(status: OrderStatus): Result<Int>
    
    suspend fun getTotalRevenue(): Result<Double>
    
    suspend fun getRevenueByDateRange(startDate: Long, endDate: Long): Result<Double>
    
    suspend fun syncOrders(): Result<Unit>
    
    suspend fun refreshOrders(): Result<Unit>
    
    suspend fun fetchOrdersFromApi(): Result<List<Order>>
    
    suspend fun fetchOrderByIdFromApi(orderId: Int): Result<Order?>
    
    suspend fun getUnsyncedOrders(): Flow<List<Order>>
    
    suspend fun markOrderAsSynced(orderId: Int): Result<Unit>

    suspend fun confirmOrderReceived(orderId: Int): Result<Order>

    suspend fun cancelOrder(orderId: Int): Result<Order>
}