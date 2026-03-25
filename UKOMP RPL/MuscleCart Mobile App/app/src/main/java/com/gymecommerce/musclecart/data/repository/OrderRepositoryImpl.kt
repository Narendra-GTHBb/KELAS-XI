package com.gymecommerce.musclecart.data.repository

import com.gymecommerce.musclecart.data.local.dao.OrderDao
import com.gymecommerce.musclecart.data.mapper.OrderMapper
import com.gymecommerce.musclecart.data.remote.api.OrderApiService
import com.gymecommerce.musclecart.data.remote.dto.CreateOrderRequest
import com.gymecommerce.musclecart.domain.model.Order
import com.gymecommerce.musclecart.domain.model.OrderStatus
import com.gymecommerce.musclecart.domain.model.Result
import com.gymecommerce.musclecart.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepositoryImpl @Inject constructor(
    private val orderDao: OrderDao,
    private val orderApiService: OrderApiService,
    private val orderMapper: OrderMapper
) : OrderRepository {

    override suspend fun getOrders(): Flow<List<Order>> {
        return orderDao.getAllOrdersFlow().map { entities ->
            entities.map { orderMapper.entityToDomain(it) }
        }
    }

    override suspend fun getOrdersByUserId(userId: Int): Flow<List<Order>> {
        return try {
            val response = orderApiService.getOrders()
            val body = response.body()
            if (response.isSuccessful && body?.data != null) {
                val orders = body.data.map { orderMapper.dtoToDomain(it) }
                kotlinx.coroutines.flow.flowOf(orders)
            } else {
                // Fallback to local DB
                orderDao.getOrdersByUserFlow(userId).map { entities ->
                    entities.map { orderMapper.entityToDomain(it) }
                }
            }
        } catch (e: Exception) {
            // Fallback to local DB
            orderDao.getOrdersByUserFlow(userId).map { entities ->
                entities.map { orderMapper.entityToDomain(it) }
            }
        }
    }

    override suspend fun getOrderById(orderId: Int): Result<Order?> {
        return try {
            val entity = orderDao.getOrderByIdFlow(orderId).first()
            val order = entity?.let { orderMapper.entityToDomain(it) }
            Result.Success(order)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to get order")
        }
    }

    override suspend fun getOrdersByStatus(status: OrderStatus): Flow<List<Order>> {
        return orderDao.getOrdersByStatusFlow(status.toString()).map { entities ->
            entities.map { orderMapper.entityToDomain(it) }
        }
    }

    override suspend fun getPendingOrders(): Flow<List<Order>> {
        return getOrdersByStatus(OrderStatus.PENDING)
    }

    override suspend fun getCompletedOrders(): Flow<List<Order>> {
        return getOrdersByStatus(OrderStatus.COMPLETED)
    }

    override suspend fun createOrder(order: Order): Result<Order> {
        return try {
            val request = CreateOrderRequest(
                shippingAddress   = order.shippingAddress,
                paymentMethod     = "cod",
                shippingCost      = order.shippingCost,
                courier           = order.courier,
                courierService    = order.courierService,
                destinationCityId = order.destinationCityId,
                voucherCode       = order.voucherCode,
                pointsUsed        = order.pointsUsed
            )
            val response = orderApiService.createOrder(request)
            val body = response.body()

            if (response.isSuccessful && body?.data != null) {
                val createdOrder = orderMapper.dtoToDomain(body.data)
                // Best-effort local cache — ignore FK/SQLite errors since server is source of truth
                try { orderDao.insertOrder(orderMapper.domainToEntity(createdOrder)) } catch (_: Exception) {}
                Result.Success(createdOrder)
            } else {
                val errorMsg = body?.message ?: "Failed to create order on server (HTTP ${response.code()})"
                Result.Error(errorMsg)
            }

        } catch (e: Exception) {
            Result.Error("Checkout failed: ${e.message}")
        }
    }

    override suspend fun updateOrder(order: Order): Result<Order> {
        return try {
            val response =
                orderApiService.updateOrder(order.id, orderMapper.domainToDto(order))
            val body = response.body()

            if (response.isSuccessful && body?.data != null) {
                val updatedOrder = orderMapper.dtoToDomain(body.data)
                orderDao.updateOrder(orderMapper.domainToEntity(updatedOrder))
                Result.Success(updatedOrder)
            } else {
                Result.Error(body?.message ?: "Failed to update order on server")
            }

        } catch (e: Exception) {

            // Offline fallback
            try {
                val updatedOrder = order.copy(isSynced = false)
                orderDao.updateOrder(orderMapper.domainToEntity(updatedOrder))
                Result.Success(updatedOrder)
            } catch (localException: Exception) {
                Result.Error(localException.message ?: "Failed to update order")
            }
        }
    }

    override suspend fun updateOrderStatus(
        orderId: Int,
        status: OrderStatus
    ): Result<Unit> {

        return try {
            val response =
                orderApiService.updateOrderStatus(orderId, status.toString())

            if (response.isSuccessful) {
                orderDao.updateOrderStatus(orderId, status.toString())
                Result.Success(Unit)
            } else {
                Result.Error("Failed to update order status on server")
            }

        } catch (e: Exception) {

            // Offline fallback
            try {
                orderDao.updateOrderStatus(orderId, status.toString())
                orderDao.markOrderAsUnsynced(orderId)
                Result.Success(Unit)
            } catch (localException: Exception) {
                Result.Error(localException.message ?: "Failed to update order status")
            }
        }
    }

    override suspend fun deleteOrder(orderId: Int): Result<Unit> {
        return try {

            val response = orderApiService.deleteOrder(orderId)

            if (response.isSuccessful) {
                orderDao.deleteOrderById(orderId)   // ✅ FIX
                Result.Success(Unit)
            } else {
                Result.Error("Failed to delete order on server")
            }

        } catch (e: Exception) {

            try {
                orderDao.deleteOrderById(orderId)   // ✅ FIX
                Result.Success(Unit)
            } catch (localException: Exception) {
                Result.Error(localException.message ?: "Failed to delete order")
            }
        }
    }

    override suspend fun getOrderCount(): Result<Int> {
        return try {
            Result.Success(orderDao.getOrderCountFlow().first())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to get order count")
        }
    }

    override suspend fun getOrderCountByStatus(status: OrderStatus): Result<Int> {
        return try {
            Result.Success(orderDao.getOrderCountByStatusFlow(status.toString()).first())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to get order count by status")
        }
    }

    override suspend fun getTotalRevenue(): Result<Double> {
        return try {
            val revenue = orderDao.getTotalRevenueFlow().first() ?: 0.0   // ✅ FIX nullable
            Result.Success(revenue)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to get total revenue")
        }
    }

    override suspend fun getRevenueByDateRange(
        startDate: Long,
        endDate: Long
    ): Result<Double> {
        return try {
            val revenue =
                orderDao.getRevenueByDateRangeFlow(startDate, endDate).first() ?: 0.0   // ✅ FIX nullable
            Result.Success(revenue)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to get revenue by date range")
        }
    }

    override suspend fun syncOrders(): Result<Unit> {
        return try {

            val response = orderApiService.getOrders()

            val body = response.body()
            if (response.isSuccessful && body?.data != null) {
                val orders = body.data.map { orderMapper.dtoToDomain(it) }
                val entities = orders.map { orderMapper.domainToEntity(it) }
                orderDao.insertOrders(entities)
                Result.Success(Unit)
            } else {
                Result.Error("Failed to sync orders from server")
            }

        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to sync orders")
        }
    }

    override suspend fun refreshOrders(): Result<Unit> {
        return try {
            orderDao.clearAllOrders()   // ✅ Sinkron dengan DAO
            syncOrders()
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to refresh orders")
        }
    }

    override suspend fun fetchOrdersFromApi(): Result<List<Order>> {
        return try {
            val response = orderApiService.getOrders()
            val body = response.body()
            if (response.isSuccessful && body?.data != null) {
                val orders = body.data.map { orderMapper.dtoToDomain(it) }
                Result.Success(orders)
            } else {
                Result.Error("Gagal mengambil data order (HTTP ${response.code()})")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Gagal mengambil data order")
        }
    }

    override suspend fun fetchOrderByIdFromApi(orderId: Int): Result<Order?> {
        return try {
            val response = orderApiService.getOrderById(orderId)
            val body = response.body()
            if (response.isSuccessful && body?.data != null) {
                Result.Success(orderMapper.dtoToDomain(body.data))
            } else {
                Result.Error("Order tidak ditemukan (HTTP ${response.code()})")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Gagal mengambil detail order")
        }
    }

    override suspend fun getUnsyncedOrders(): Flow<List<Order>> {
        return orderDao.getUnsyncedOrders().map { entities ->
            entities.map { orderMapper.entityToDomain(it) }
        }
    }

    override suspend fun markOrderAsSynced(orderId: Int): Result<Unit> {
        return try {
            orderDao.markOrderAsSynced(orderId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to mark order as synced")
        }
    }

    override suspend fun confirmOrderReceived(orderId: Int): Result<Order> {
        return try {
            val response = orderApiService.confirmReceived(orderId)
            if (response.isSuccessful && response.body()?.status == "success") {
                val dto = response.body()!!.data!!
                Result.Success(orderMapper.dtoToDomain(dto))
            } else {
                Result.Error(response.body()?.message ?: "Failed to confirm order received")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }

    override suspend fun cancelOrder(orderId: Int): Result<Order> {
        return try {
            val response = orderApiService.cancelOrder(orderId)
            if (response.isSuccessful && response.body()?.status == "success") {
                val dto = response.body()!!.data!!
                Result.Success(orderMapper.dtoToDomain(dto))
            } else {
                Result.Error(response.body()?.message ?: "Gagal membatalkan pesanan")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }
}