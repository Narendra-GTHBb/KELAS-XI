package com.apkfood.wavesoffood.utils

import android.content.Context
import android.util.Log
import com.apkfood.wavesoffood.model.Order
import com.apkfood.wavesoffood.model.OrderStatus
import com.apkfood.wavesoffood.manager.OrderHistoryManager
import com.apkfood.wavesoffood.manager.CartManager
import com.apkfood.wavesoffood.manager.FirebaseOrderManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.Job
import java.util.UUID

/**
 * Singleton untuk mengelola pesanan dengan integrasi OrderHistoryManager
 */
object OrderManager {
    
    private val orders = mutableListOf<Order>()
    private val listeners = mutableListOf<OrderUpdateListener>()
    private var firebaseOrderManager: FirebaseOrderManager? = null
    private var currentUserId: String? = null
    private var pollingJob: Job? = null
    
    init {
        // Stop any existing automatic status progressions on initialization
        OrderStatusManager.cancelAllProgressions()
    }
    
    interface OrderUpdateListener {
        fun onOrderAdded(order: Order)
        fun onOrderUpdated(order: Order)
    }
    
    fun addListener(listener: OrderUpdateListener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener)
        }
    }
    
    fun removeListener(listener: OrderUpdateListener) {
        listeners.remove(listener)
    }
    
    private fun notifyOrderAdded(order: Order) {
        listeners.forEach { it.onOrderAdded(order) }
    }
    
    private fun notifyOrderUpdated(order: Order) {
        listeners.forEach { it.onOrderUpdated(order) }
    }
    
    private val orderCreationInProgress = mutableSetOf<String>()
    
    /**
     * Membuat pesanan baru dari keranjang
     */
    fun createOrder(
        context: Context,
        userName: String,
        userEmail: String,
        userPhone: String,
        deliveryAddress: String,
        paymentMethod: String,
        notes: String = ""
    ): Order {
        val currentUser = UserSessionManager.getCurrentUser(context)
        val userId = currentUser?.uid ?: "guest_${System.currentTimeMillis()}"
        
        // Prevent duplicate order creation for same user
        if (orderCreationInProgress.contains(userId)) {
            Log.w("OrderManager", "🚨 Order creation already in progress for user: $userId")
            throw IllegalStateException("Pembuatan pesanan sedang berlangsung, harap tunggu...")
        }
        
        try {
            orderCreationInProgress.add(userId)
            
            val cartItems = CartManager.getInstance().getCartItems()
            if (cartItems.isEmpty()) {
                throw IllegalStateException("Keranjang kosong")
            }
        
        // Get current user data from session
        val currentUser = UserSessionManager.getCurrentUser(context)
        
        // CALCULATE EXACT TOTAL PRICE - NO MARKUP
        val exactTotalPrice = cartItems.sumOf { item ->
            val itemTotal = item.quantity * item.food.price
            Log.d("OrderManager", "💰 Item: ${item.food.name} - Qty: ${item.quantity} - Price: ${item.food.price} - Total: $itemTotal")
            itemTotal
        }
        
        Log.d("OrderManager", "💰 EXACT TOTAL CALCULATED: $exactTotalPrice")
        Log.d("OrderManager", "💰 CartManager Total: ${CartManager.getInstance().getTotalPrice()}")
        
        val order = Order(
            id = UUID.randomUUID().toString(),
            userId = userId,
            userName = userName,
            userEmail = userEmail,
            userPhone = userPhone,
            orderNumber = "WOF${System.currentTimeMillis()}${(100..999).random()}", // Generate WOF format immediately
            deliveryAddress = deliveryAddress,
            items = cartItems.toList(),
            totalAmount = exactTotalPrice, // Use calculated exact total
            orderStatus = OrderStatus.PENDING,
            orderDate = System.currentTimeMillis(),
            estimatedDeliveryTime = System.currentTimeMillis() + (45 * 60 * 1000), // 45 menit
            paymentMethod = paymentMethod,
            notes = notes
        )
        
        Log.d("OrderManager", "💰 Created order with EXACT total: ${order.totalAmount}")
        Log.d("OrderManager", "📱 Order Number: ${order.orderNumber}")
        
        // Check if similar order already exists (same user, same items, recent timestamp)
        val recentOrders = orders.filter { 
            it.userId == userId && 
            Math.abs(it.orderDate - order.orderDate) < 60000 && // Within 1 minute
            it.items.size == order.items.size 
        }
        
        if (recentOrders.isNotEmpty()) {
            Log.w("OrderManager", "🚨 Similar order detected, skipping duplicate creation")
            orderCreationInProgress.remove(userId)
            return recentOrders.first()
        }
        
        orders.add(0, order) // Add to beginning of list (most recent first)
        
        // Save to order history for registered users
        OrderHistoryManager.addOrderToHistory(context, order)
        
        // Submit order to Firebase for admin confirmation
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d("OrderManager", "Starting Firebase submission for order: ${order.id} with total: ${order.totalAmount}")
                val firebaseOrderManager = FirebaseOrderManager()
                val result = firebaseOrderManager.submitOrderToFirebase(order)
                if (result.isSuccess) {
                    Log.d("OrderManager", "✅ Order submitted to Firebase successfully: ${order.id}")
                    Log.d("OrderManager", "Firebase document ID: ${result.getOrNull()}")
                } else {
                    Log.e("OrderManager", "❌ Failed to submit order to Firebase: ${result.exceptionOrNull()?.message}")
                    result.exceptionOrNull()?.printStackTrace()
                }
                } catch (e: Exception) {
                    Log.e("OrderManager", "💥 Exception submitting order to Firebase", e)
                    e.printStackTrace()
                } finally {
                    // Release lock after Firebase submission
                    orderCreationInProgress.remove(userId)
                }
            }
            
            notifyOrderAdded(order)
            
            // Removed automatic status progression - status will be managed manually
            // OrderStatusManager.startOrderProgression(context, order)
            
            // Stop any existing automatic progressions to prevent unwanted status changes
            OrderStatusManager.cancelAllProgressions()
            
            // Clear cart after order creation
            CartManager.getInstance().clearCart()
            
            return order
            
        } catch (e: Exception) {
            // Release lock on error
            orderCreationInProgress.remove(userId)
            throw e
        }
    }    /**
     * Mendapatkan semua pesanan untuk user saat ini
     * Returns cached orders immediately, triggers background sync
     */
    fun getAllOrders(context: Context): List<Order> {
        val currentUser = UserSessionManager.getCurrentUser(context)
        
        if (currentUser != null) {
            // Return cached orders immediately 
            val userOrders = orders.filter { it.userId == currentUser.uid }
            Log.d("OrderManager", "📱 Returning ${userOrders.size} cached orders for user: ${currentUser.uid}")
            
            // Also return orders from OrderHistoryManager 
            val historyOrders = OrderHistoryManager.getOrderHistory(context)
            Log.d("OrderManager", "📂 Found ${historyOrders.size} orders in OrderHistoryManager")
            
            // ENHANCED DEDUPLICATION: Check both ID and orderNumber to prevent duplicates
            val allOrders = (userOrders + historyOrders)
                .distinctBy { "${it.id}_${it.orderNumber}" } // Deduplicate by ID AND orderNumber
                .groupBy { it.orderNumber } // Group by orderNumber  
                .mapValues { (orderNumber, orderList) ->
                    if (orderList.size > 1) {
                        Log.w("OrderManager", "🚨 Found ${orderList.size} duplicates for $orderNumber - keeping latest")
                        // Keep the one with latest orderDate
                        orderList.maxByOrNull { it.orderDate }!!
                    } else {
                        orderList.first()
                    }
                }
                .values.toList()
            
            Log.d("OrderManager", "📋 Total deduplicated orders: ${allOrders.size}")
            
            return allOrders.sortedByDescending { it.orderDate }
        } else {
            // Guest user - filter by guest ID pattern  
            return orders.filter { it.userId.startsWith("guest_") }.sortedByDescending { it.orderDate }
        }
    }
    
    /**
     * Refresh orders from Firebase (async) - for manual refresh
     */
    fun refreshOrdersFromFirebase(context: Context) {
        val currentUser = UserSessionManager.getCurrentUser(context)
        
        if (currentUser != null) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val firebaseOrders = getFirebaseOrders(context)
                    
                    if (firebaseOrders.isNotEmpty()) {
                        Log.d("OrderManager", "✅ Refreshed ${firebaseOrders.size} orders from Firebase")
                        
                        // Update cached orders with Firebase data
                        val userOrderIndices = orders.indices.filter { orders[it].userId == currentUser.uid }
                        userOrderIndices.reversed().forEach { orders.removeAt(it) }
                        orders.addAll(0, firebaseOrders)
                        
                        // Update OrderHistoryManager with latest data
                        firebaseOrders.forEach { order ->
                            OrderHistoryManager.updateOrderInHistory(context, order)
                        }
                        
                        // Notify listeners
                        firebaseOrders.forEach { order ->
                            notifyOrderUpdated(order)
                        }
                    } else {
                        Log.w("OrderManager", "🔄 Manual refresh: No Firebase orders found")
                    }
                } catch (e: Exception) {
                    Log.e("OrderManager", "🔄 Error refreshing orders from Firebase", e)
                    // Still notify with local orders to update UI
                    val localOrders = OrderHistoryManager.getOrderHistory(context)
                    if (localOrders.isNotEmpty()) {
                        Log.d("OrderManager", "📱 Using ${localOrders.size} local orders as fallback")
                        localOrders.forEach { order -> notifyOrderUpdated(order) }
                    }
                }
            }
        }
    }
    
    /**
     * Get orders from Firebase for current user (async)
     * This is used by the manual refresh function to sync latest status
     */
    suspend fun getFirebaseOrders(context: Context): List<Order> {
        val currentUser = UserSessionManager.getCurrentUser(context)
        return if (currentUser != null) {
            try {
                val firebaseManager = FirebaseOrderManager()
                val result = firebaseManager.getUserOrders(currentUser.uid)
                if (result.isSuccess) {
                    val firebaseOrders = result.getOrNull() ?: emptyList()
                    Log.d("OrderManager", "🔥 Retrieved ${firebaseOrders.size} orders from Firebase")
                    firebaseOrders.forEach { order ->
                        Log.d("OrderManager", "🔥 Firebase order: ${order.orderNumber} - ${order.orderStatus}")
                    }
                    firebaseOrders
                } else {
                    Log.e("OrderManager", "🔥 Failed to get Firebase orders: ${result.exceptionOrNull()?.message}")
                    emptyList()
                }
            } catch (e: Exception) {
                Log.e("OrderManager", "🔥 Exception getting Firebase orders", e)
                emptyList()
            }
        } else {
            emptyList()
        }
    }
    
    /**
     * Mendapatkan pesanan berdasarkan ID
     */
    fun getOrderById(context: Context, orderId: String): Order? {
        // First check in-memory orders
        val memoryOrder = orders.find { it.id == orderId }
        if (memoryOrder != null) {
            Log.d("OrderManager", "📱 Found order in memory: ${memoryOrder.orderNumber}")
            return memoryOrder
        }
        
        // Then check persistent storage for registered users
        if (OrderHistoryManager.canAccessOrderHistory(context)) {
            val persistentOrder = OrderHistoryManager.getOrderById(context, orderId)
            if (persistentOrder != null) {
                Log.d("OrderManager", "💾 Found order in persistent storage: ${persistentOrder.orderNumber}")
                return persistentOrder
            }
        }
        
        Log.w("OrderManager", "❌ Order not found anywhere: $orderId")
        return null
    }
    
    /**
     * Update status pesanan
     */
    fun updateOrderStatus(context: Context, orderId: String, newStatus: OrderStatus): Boolean {
        Log.d("OrderManager", "🔄 updateOrderStatus called - OrderId: $orderId, NewStatus: $newStatus")
        
        // Update in memory first
        val orderIndex = orders.indexOfFirst { it.id == orderId }
        var updatedOrder: Order? = null
        
        Log.d("OrderManager", "📱 Memory search result - Index: $orderIndex, Total orders: ${orders.size}")
        
        if (orderIndex >= 0) {
            val originalOrder = orders[orderIndex]
            Log.d("OrderManager", "📱 Found in memory: ${originalOrder.orderNumber} - Status: ${originalOrder.orderStatus} -> $newStatus")
            
            updatedOrder = originalOrder.copy(orderStatus = newStatus)
            orders[orderIndex] = updatedOrder
            
            // Update in persistent storage for registered users
            val persistentUpdate = OrderHistoryManager.updateOrderInHistory(context, updatedOrder)
            Log.d("OrderManager", "💾 Persistent storage update result: $persistentUpdate")
            
            notifyOrderUpdated(updatedOrder)
        }
        
        // If not found in memory, try to get from persistent storage and update
        if (orderIndex < 0 && OrderHistoryManager.canAccessOrderHistory(context)) {
            Log.d("OrderManager", "💾 Searching in persistent storage...")
            val order = OrderHistoryManager.getOrderById(context, orderId)
            if (order != null) {
                Log.d("OrderManager", "💾 Found in persistent: ${order.orderNumber} - Status: ${order.orderStatus} -> $newStatus")
                updatedOrder = order.copy(orderStatus = newStatus)
                val success = OrderHistoryManager.updateOrderInHistory(context, updatedOrder)
                Log.d("OrderManager", "💾 Persistent update success: $success")
                if (success) {
                    notifyOrderUpdated(updatedOrder)
                }
            } else {
                Log.w("OrderManager", "💾 Order not found in persistent storage")
            }
        }
        
        // CRITICAL: Also update status in Firebase to prevent revert
        if (updatedOrder != null) {
            Log.d("OrderManager", "🔄 Syncing status update to Firebase: ${updatedOrder.orderNumber} -> $newStatus")
            
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val firebaseManager = FirebaseOrderManager()
                    val statusString = when (newStatus) {
                        OrderStatus.PENDING -> "pending"
                        OrderStatus.CONFIRMED -> "confirmed"
                        OrderStatus.PREPARING -> "preparing"
                        OrderStatus.ON_THE_WAY -> "on_the_way"
                        OrderStatus.DELIVERED -> "delivered"
                        OrderStatus.CANCELLED -> "cancelled"
                    }
                    
                    val result = firebaseManager.updateOrderStatus(updatedOrder.orderNumber, statusString)
                    if (result.isSuccess) {
                        Log.d("OrderManager", "✅ Successfully synced status to Firebase: ${updatedOrder.orderNumber}")
                    } else {
                        Log.e("OrderManager", "❌ Failed to sync status to Firebase: ${result.exceptionOrNull()?.message}")
                    }
                } catch (e: Exception) {
                    Log.e("OrderManager", "💥 Exception syncing status to Firebase", e)
                }
            }
            
            Log.d("OrderManager", "✅ updateOrderStatus returning true for order: ${updatedOrder.orderNumber}")
            return true
        }
        
        Log.e("OrderManager", "❌ updateOrderStatus returning false - Order not found: $orderId")
        return false
    }
    
    /**
     * Update status pesanan secara manual
     * Untuk digunakan oleh admin atau sistem manual status management
     */
    fun manualUpdateOrderStatus(context: Context, orderId: String, newStatus: OrderStatus): Boolean {
        // Cancel any automatic progression for this order first
        OrderStatusManager.cancelOrderProgression(orderId)
        
        // Update the status manually
        return updateOrderStatus(context, orderId, newStatus)
    }
    
    /**
     * Mendapatkan jumlah total pesanan untuk user saat ini
     */
    fun getTotalOrderCount(context: Context): Int {
        return getAllOrders(context).size
    }
    
    /**
     * Mendapatkan pesanan berdasarkan status untuk user saat ini
     */
    fun getOrdersByStatus(context: Context, status: OrderStatus): List<Order> {
        return getAllOrders(context).filter { it.orderStatus == status }
    }
    
    /**
     * Membatalkan pesanan
     */
    fun cancelOrder(context: Context, orderId: String): Boolean {
        Log.d("OrderManager", "🚫 Attempting to cancel order: $orderId")
        
        val order = getOrderById(context, orderId)
        if (order == null) {
            Log.e("OrderManager", "❌ Order not found for cancellation: $orderId")
            return false
        }
        
        Log.d("OrderManager", "📋 Cancelling order: ${order.orderNumber} - Current Status: ${order.orderStatus}")
        
        // Stop any automatic progression for this order
        OrderStatusManager.cancelOrderProgression(orderId)
        
        // Update status to CANCELLED (will also sync to Firebase)
        val success = updateOrderStatus(context, orderId, OrderStatus.CANCELLED)
        
        if (success) {
            Log.d("OrderManager", "✅ Order successfully cancelled: ${order.orderNumber}")
        } else {
            Log.e("OrderManager", "❌ Failed to cancel order: ${order.orderNumber}")
        }
        
        return success
    }
    
    /**
     * Clear all orders (for testing purposes and logout)
     */
    fun clearAllOrders() {
        orders.clear()
    }
    
    /**
     * Clear orders for current user only
     */
    fun clearUserOrders(context: Context) {
        val currentUser = UserSessionManager.getCurrentUser(context)
        if (currentUser != null) {
            orders.removeAll { it.userId == currentUser.uid }
        } else {
            // Clear guest orders
            orders.removeAll { it.userId.startsWith("guest_") }
        }
    }
    
    /**
     * Start listening for Firebase order updates for the current user
     */
    fun startFirebaseOrderListener(context: Context) {
        val currentUser = UserSessionManager.getCurrentUser(context)
        if (currentUser != null) {
            // STOP PREVIOUS LISTENER FIRST TO PREVENT DUPLICATES!
            stopFirebaseOrderListener()
            
            currentUserId = currentUser.uid
            firebaseOrderManager = FirebaseOrderManager()
            
            Log.d("OrderManager", "🔄 Starting Firebase order listener for user: ${currentUser.uid} (Previous listener stopped)")
            firebaseOrderManager?.listenForOrderUpdates(currentUser.uid) { updatedOrder ->
                Log.d("OrderManager", "� FIREBASE UPDATE: ${updatedOrder.orderNumber} - Status: ${updatedOrder.orderStatus} - Price: ${updatedOrder.totalAmount}")
                
                // HARDCORE ORDER MATCHING - Zero tolerance for duplicates
                var localOrderIndex = -1
                val currentOrderCount = orders.size
                
                Log.d("OrderManager", "📊 Current orders count: $currentOrderCount")
                orders.forEachIndexed { index, order ->
                    Log.d("OrderManager", "📋 Local order [$index]: ${order.orderNumber} - Status: ${order.orderStatus} - Price: ${order.totalAmount}")
                }
                
                // 1. ABSOLUTE PRIMARY: Match by orderNumber
                if (updatedOrder.orderNumber.isNotEmpty()) {
                    localOrderIndex = orders.indexOfFirst { it.orderNumber == updatedOrder.orderNumber }
                    Log.d("OrderManager", "🎯 PRIMARY match by orderNumber '${updatedOrder.orderNumber}': index = $localOrderIndex")
                }
                
                // 2. EMERGENCY FALLBACK: Match by Firebase document ID
                if (localOrderIndex < 0) {
                    localOrderIndex = orders.indexOfFirst { it.id == updatedOrder.id }
                    Log.d("OrderManager", "🎯 FALLBACK match by ID '${updatedOrder.id}': index = $localOrderIndex")
                }
                
                if (localOrderIndex >= 0) {
                    val existingOrder = orders[localOrderIndex]
                    Log.d("OrderManager", "✅ FOUND EXISTING ORDER: ${existingOrder.orderNumber} at index $localOrderIndex")
                    Log.d("OrderManager", "🔄 OLD: Status=${existingOrder.orderStatus}, Price=${existingOrder.totalAmount}")
                    Log.d("OrderManager", "🔄 NEW: Status=${updatedOrder.orderStatus}, Price=${updatedOrder.totalAmount}")
                    
                    // DIRECT UPDATE - no price modifications
                    val updatedOrderFinal = updatedOrder.copy(
                        orderNumber = existingOrder.orderNumber, // Keep original orderNumber
                        id = existingOrder.id // Keep original local ID
                    )
                    orders[localOrderIndex] = updatedOrderFinal
                    
                    Log.d("OrderManager", "✅ UPDATED ORDER: ${updatedOrderFinal.orderNumber} - Status: ${updatedOrderFinal.orderStatus} - Price: ${updatedOrderFinal.totalAmount}")
                    
                    // Update history and notify
                    try {
                        OrderHistoryManager.updateOrderInHistory(context, updatedOrderFinal)
                        Log.d("OrderManager", "� Updated order history for: ${updatedOrderFinal.orderNumber}")
                    } catch (e: Exception) {
                        Log.e("OrderManager", "❌ Failed to update order history", e)
                    }
                    
                    // FORCE UI UPDATE
                    notifyOrderUpdated(updatedOrderFinal)
                    Log.d("OrderManager", "📢 FORCED UI UPDATE for ${listeners.size} listeners")
                    
                } else {
                    // HARDCORE REJECTION - NEVER ALLOW NEW ORDERS FROM FIREBASE!
                    Log.e("OrderManager", "🚨🚨🚨 ABSOLUTELY REJECTED: ${updatedOrder.orderNumber}")
                    Log.e("OrderManager", "🚨 Order ID: ${updatedOrder.id}, Status: ${updatedOrder.orderStatus}, Price: ${updatedOrder.totalAmount}")
                    Log.e("OrderManager", "🚨 This Firebase update should ONLY be for existing orders!")
                    Log.e("OrderManager", "🚨 Current local orders count: ${orders.size}")
                    return@listenForOrderUpdates // BLOCK IT COMPLETELY!
                }
            }
        }
    }
    
    /**
     * Stop Firebase order listener
     */
    fun stopFirebaseOrderListener() {
        Log.d("OrderManager", "🛑 Stopping Firebase order listener")
        firebaseOrderManager?.stopListening()
        firebaseOrderManager = null
        currentUserId = null
    }
    
    /**
     * Manually refresh order statuses from Firebase (for testing/debugging)
     */
    fun manualRefreshOrderStatus(context: Context) {
        val currentUser = UserSessionManager.getCurrentUser(context)
        if (currentUser != null) {
            Log.d("OrderManager", "🔄 Manual refresh: Getting Firebase orders for user: ${currentUser.uid}")
            
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val firebaseOrders = getFirebaseOrders(context)
                    
                    if (firebaseOrders.isNotEmpty()) {
                        Log.d("OrderManager", "🔄 Manual refresh: Retrieved ${firebaseOrders.size} Firebase orders")
                        
                        // Replace local orders with Firebase orders completely
                        orders.clear()
                        orders.addAll(firebaseOrders)
                        
                        // Update order history with Firebase orders
                        firebaseOrders.forEach { firebaseOrder ->
                            Log.d("OrderManager", "🔄 Syncing Firebase order: ${firebaseOrder.orderNumber} - ${firebaseOrder.orderStatus}")
                            OrderHistoryManager.updateOrderInHistory(context, firebaseOrder)
                            
                            // Notify listeners
                            notifyOrderUpdated(firebaseOrder)
                        }
                        
                        Log.d("OrderManager", "🔄 Manual refresh completed successfully - replaced with ${firebaseOrders.size} Firebase orders")
                    } else {
                        Log.w("OrderManager", "🔄 Manual refresh: No Firebase orders found")
                    }
                } catch (e: Exception) {
                    Log.e("OrderManager", "🔄 Manual refresh exception", e)
                }
            }
        } else {
            Log.w("OrderManager", "🔄 Cannot refresh - no current user")
        }
    }
    
    /**
     * Test Firebase connectivity and order retrieval (for debugging)
     */
    fun testFirebaseConnection(context: Context) {
        val currentUser = UserSessionManager.getCurrentUser(context)
        if (currentUser != null) {
            Log.d("OrderManager", "🧪 Testing Firebase connection for user: ${currentUser.uid}")
            
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val firebaseManager = FirebaseOrderManager()
                    val result = firebaseManager.testFirebaseOrderRetrieval(currentUser.uid)
                    
                    if (result.isSuccess) {
                        Log.d("OrderManager", "🧪 Firebase test completed successfully")
                    } else {
                        Log.e("OrderManager", "🧪 Firebase test failed: ${result.exceptionOrNull()?.message}")
                    }
                } catch (e: Exception) {
                    Log.e("OrderManager", "🧪 Firebase test exception", e)
                }
            }
        } else {
            Log.w("OrderManager", "🧪 Cannot test Firebase - no current user")
        }
    }
    
    /**
     * Remove sample data generation - users will create real orders
     */
    @Deprecated("No longer using dummy data")
    fun generateSampleOrders() {
        // Removed - we now use real orders from user actions
    }
    
    /**
     * Start automatic Firebase polling for real-time updates
     */
    fun startFirebasePolling(context: Context) {
        stopFirebasePolling() // Stop any existing polling
        
        val currentUser = UserSessionManager.getCurrentUser(context)
        if (currentUser != null) {
            Log.d("OrderManager", "🔄 Starting Firebase polling every 5 seconds")
            
            pollingJob = CoroutineScope(Dispatchers.IO).launch {
                while (true) {
                    try {
                        val firebaseOrders = getFirebaseOrders(context)
                        
                        if (firebaseOrders.isNotEmpty()) {
                            // Replace all local orders with Firebase orders
                            orders.clear()
                            orders.addAll(firebaseOrders)
                            
                            Log.d("OrderManager", "🔄 Polling: Updated with ${firebaseOrders.size} Firebase orders")
                            
                            // Notify listeners about updates
                            firebaseOrders.forEach { order ->
                                notifyOrderUpdated(order)
                            }
                        }
                        
                        delay(5000) // Wait 5 seconds before next poll
                    } catch (e: Exception) {
                        Log.e("OrderManager", "🔄 Polling error", e)
                        delay(10000) // Wait longer on error
                    }
                }
            }
        }
    }
    
    /**
     * Stop Firebase polling
     */
    fun stopFirebasePolling() {
        pollingJob?.cancel()
        pollingJob = null
        Log.d("OrderManager", "🛑 Stopped Firebase polling")
    }
}
