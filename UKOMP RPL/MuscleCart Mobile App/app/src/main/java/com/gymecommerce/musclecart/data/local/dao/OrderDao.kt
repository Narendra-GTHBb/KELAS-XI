package com.gymecommerce.musclecart.data.local.dao

import androidx.room.*
import com.gymecommerce.musclecart.data.local.entity.OrderEntity
import com.gymecommerce.musclecart.data.local.entity.OrderItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {

    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAllOrdersFlow(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE userId = :userId ORDER BY createdAt DESC")
    fun getOrdersByUserFlow(userId: Int): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE id = :orderId")
    fun getOrderByIdFlow(orderId: Int): Flow<OrderEntity?>

    @Query("SELECT * FROM orders WHERE status = :status ORDER BY createdAt DESC")
    fun getOrdersByStatusFlow(status: String): Flow<List<OrderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrders(orders: List<OrderEntity>)

    @Update
    suspend fun updateOrder(order: OrderEntity)

    @Query("UPDATE orders SET status = :status, updatedAt = :updatedAt WHERE id = :orderId")
    suspend fun updateOrderStatus(
        orderId: Int,
        status: String,
        updatedAt: Long = System.currentTimeMillis()
    ): Int

    @Query("UPDATE orders SET isSynced = 0 WHERE id = :orderId")
    suspend fun markOrderAsUnsynced(orderId: Int): Int

    @Query("UPDATE orders SET isSynced = 1 WHERE id = :orderId")
    suspend fun markOrderAsSynced(orderId: Int): Int

    @Query("DELETE FROM orders WHERE id = :orderId")
    suspend fun deleteOrderById(orderId: Int): Int

    @Query("DELETE FROM orders")
    suspend fun clearAllOrders(): Int

    @Query("SELECT COUNT(*) FROM orders")
    fun getOrderCountFlow(): Flow<Int>

    @Query("SELECT COUNT(*) FROM orders WHERE status = :status")
    fun getOrderCountByStatusFlow(status: String): Flow<Int>

    @Query("SELECT SUM(totalAmount) FROM orders WHERE status = 'DELIVERED'")
    fun getTotalRevenueFlow(): Flow<Double?>

    @Query("SELECT SUM(totalAmount) FROM orders WHERE createdAt BETWEEN :startDate AND :endDate AND status = 'DELIVERED'")
    fun getRevenueByDateRangeFlow(startDate: Long, endDate: Long): Flow<Double?>

    @Query("SELECT * FROM orders WHERE isSynced = 0")
    fun getUnsyncedOrders(): Flow<List<OrderEntity>>

    // ===== ORDER ITEMS =====

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    fun getOrderItemsFlow(orderId: Int): Flow<List<OrderItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItems(orderItems: List<OrderItemEntity>): List<Long>

    @Query("DELETE FROM order_items WHERE orderId = :orderId")
    suspend fun deleteOrderItemsByOrderId(orderId: Int): Int
}