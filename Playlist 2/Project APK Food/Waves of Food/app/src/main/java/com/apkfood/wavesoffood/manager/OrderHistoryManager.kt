package com.apkfood.wavesoffood.manager

import android.content.Context
import android.content.SharedPreferences
import com.apkfood.wavesoffood.model.Order
import com.apkfood.wavesoffood.model.OrderStatus
import com.apkfood.wavesoffood.utils.UserSessionManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * OrderHistoryManager - Mengelola riwayat pesanan per user
 * Hanya tersedia untuk registered users, tidak untuk guest
 */
object OrderHistoryManager {
    
    private const val PREF_NAME = "order_history_pref"
    private val gson = Gson()
    
    /**
     * Check if user can access order history (only registered users)
     */
    fun canAccessOrderHistory(context: Context): Boolean {
        return !UserSessionManager.isGuest(context) && 
               UserSessionManager.getCurrentUser(context) != null
    }
    
    /**
     * Get order history key for current user
     */
    private fun getOrderHistoryKey(context: Context): String? {
        val currentUser = UserSessionManager.getCurrentUser(context)
        return if (!UserSessionManager.isGuest(context) && currentUser != null) {
            "order_history_${currentUser.uid}"
        } else {
            null
        }
    }
    
    /**
     * Get SharedPreferences
     */
    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }
    
    /**
     * Add order to history
     */
    fun addOrderToHistory(context: Context, order: Order): Boolean {
        if (!canAccessOrderHistory(context)) {
            return false
        }
        
        val key = getOrderHistoryKey(context) ?: return false
        val orders = getOrderHistory(context).toMutableList()
        
        // Add new order at the beginning (most recent first)
        orders.add(0, order)
        
        // Limit to 50 orders to prevent storage issues
        if (orders.size > 50) {
            orders.removeAt(orders.size - 1)
        }
        
        saveOrderHistory(context, key, orders)
        return true
    }
    
    /**
     * Get all orders for current user
     */
    fun getOrderHistory(context: Context): List<Order> {
        if (!canAccessOrderHistory(context)) {
            return emptyList()
        }
        
        val key = getOrderHistoryKey(context) ?: return emptyList()
        val prefs = getPreferences(context)
        val ordersJson = prefs.getString(key, null) ?: return emptyList()
        
        return try {
            val type = object : TypeToken<List<Order>>() {}.type
            gson.fromJson(ordersJson, type) ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
    
    /**
     * Get orders by status
     */
    fun getOrdersByStatus(context: Context, status: OrderStatus): List<Order> {
        return getOrderHistory(context).filter { it.orderStatus == status }
    }
    
    /**
     * Get order by ID
     */
    fun getOrderById(context: Context, orderId: String): Order? {
        return getOrderHistory(context).find { it.id == orderId }
    }
    
    /**
     * Update order in history
     */
    fun updateOrderInHistory(context: Context, updatedOrder: Order): Boolean {
        if (!canAccessOrderHistory(context)) {
            return false
        }
        
        val key = getOrderHistoryKey(context) ?: return false
        val orders = getOrderHistory(context).toMutableList()
        
        val index = orders.indexOfFirst { it.id == updatedOrder.id }
        if (index != -1) {
            orders[index] = updatedOrder
            saveOrderHistory(context, key, orders)
            return true
        }
        
        return false
    }
    
    /**
     * Save order history to SharedPreferences
     */
    private fun saveOrderHistory(context: Context, key: String, orders: List<Order>) {
        try {
            val prefs = getPreferences(context)
            val ordersJson = gson.toJson(orders)
            prefs.edit().putString(key, ordersJson).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Clear order history for current user
     */
    fun clearOrderHistory(context: Context): Boolean {
        if (!canAccessOrderHistory(context)) {
            return false
        }
        
        val key = getOrderHistoryKey(context) ?: return false
        val prefs = getPreferences(context)
        prefs.edit().remove(key).apply()
        return true
    }
    
    /**
     * Get order count for current user
     */
    fun getOrderCount(context: Context): Int {
        return getOrderHistory(context).size
    }
    
    /**
     * Get completed orders count
     */
    fun getCompletedOrdersCount(context: Context): Int {
        return getOrdersByStatus(context, OrderStatus.DELIVERED).size
    }
    
    /**
     * Get pending orders count
     */
    fun getPendingOrdersCount(context: Context): Int {
        return getOrderHistory(context).count { 
            it.orderStatus in listOf(OrderStatus.PENDING, OrderStatus.CONFIRMED, OrderStatus.PREPARING, OrderStatus.ON_THE_WAY) 
        }
    }
    
    /**
     * Get recent orders (last 10)
     */
    fun getRecentOrders(context: Context, limit: Int = 10): List<Order> {
        return getOrderHistory(context).take(limit)
    }
    
    /**
     * Calculate total spent by user
     */
    fun getTotalSpent(context: Context): Double {
        return getOrdersByStatus(context, OrderStatus.DELIVERED).sumOf { it.totalAmount }
    }
}
