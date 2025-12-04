package com.apkfood.wavesoffood.utils

import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import com.apkfood.wavesoffood.model.Order
import com.apkfood.wavesoffood.model.OrderStatus

/**
 * Manager untuk mengupdate status order secara otomatis
 * Simulasi proses real-time restaurant order processing
 */
object OrderStatusManager {
    
    private const val PREFS_NAME = "order_status_prefs"
    private const val KEY_ORDER_TIMERS = "order_timers"
    
    private val activeTimers = mutableMapOf<String, Handler>()
    
    /**
     * Start automatic status progression for an order
     */
    fun startOrderProgression(context: Context, order: Order) {
        val orderId = order.id
        
        // Cancel any existing timer for this order
        cancelOrderProgression(orderId)
        
        val handler = Handler(Looper.getMainLooper())
        activeTimers[orderId] = handler
        
        // Status progression timeline:
        // PENDING (0s) -> CONFIRMED (10s) -> PREPARING (30s) -> ON_THE_WAY (60s) -> DELIVERED (90s)
        
        // After 10 seconds: PENDING -> CONFIRMED
        handler.postDelayed({
            updateOrderStatus(context, orderId, OrderStatus.CONFIRMED)
        }, 10000)
        
        // After 30 seconds: CONFIRMED -> PREPARING  
        handler.postDelayed({
            updateOrderStatus(context, orderId, OrderStatus.PREPARING)
        }, 30000)
        
        // After 60 seconds: PREPARING -> ON_THE_WAY
        handler.postDelayed({
            updateOrderStatus(context, orderId, OrderStatus.ON_THE_WAY)
        }, 60000)
        
        // After 90 seconds: ON_THE_WAY -> DELIVERED
        handler.postDelayed({
            updateOrderStatus(context, orderId, OrderStatus.DELIVERED)
            // Remove timer after completion
            activeTimers.remove(orderId)
        }, 90000)
    }
    
    /**
     * Update order status using OrderManager
     */
    private fun updateOrderStatus(context: Context, orderId: String, newStatus: OrderStatus) {
        try {
            val success = OrderManager.updateOrderStatus(context, orderId, newStatus)
            if (success) {
                // Notify that order status changed
                notifyOrderStatusChanged(context, orderId, newStatus)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Cancel order progression for specific order
     */
    fun cancelOrderProgression(orderId: String) {
        activeTimers[orderId]?.removeCallbacksAndMessages(null)
        activeTimers.remove(orderId)
    }
    
    /**
     * Cancel all active order progressions
     */
    fun cancelAllProgressions() {
        activeTimers.values.forEach { handler ->
            handler.removeCallbacksAndMessages(null)
        }
        activeTimers.clear()
    }
    
    /**
     * Notify that order status has changed (placeholder for future notifications)
     */
    private fun notifyOrderStatusChanged(context: Context, orderId: String, status: OrderStatus) {
        // In a real app, this would trigger push notifications
        // For now, we'll just log it
        android.util.Log.d("OrderStatusManager", 
            "Order ${orderId.take(8)} status changed to $status")
    }
    
    /**
     * Get status display name in Indonesian
     */
    fun getStatusDisplayName(status: OrderStatus): String {
        return when (status) {
            OrderStatus.PENDING -> "⏳ Menunggu Konfirmasi"
            OrderStatus.CONFIRMED -> "✅ Dikonfirmasi"
            OrderStatus.PREPARING -> "👨‍🍳 Sedang Dipreparasi"
            OrderStatus.ON_THE_WAY -> "🚗 Dalam Perjalanan"
            OrderStatus.DELIVERED -> "📦 Sudah Diantar"
            OrderStatus.CANCELLED -> "❌ Dibatalkan"
        }
    }
    
    /**
     * Get estimated time for status
     */
    fun getEstimatedTime(status: OrderStatus): String {
        return when (status) {
            OrderStatus.PENDING -> "Segera dikonfirmasi"
            OrderStatus.CONFIRMED -> "~20 menit lagi"
            OrderStatus.PREPARING -> "~30 menit lagi"
            OrderStatus.ON_THE_WAY -> "~10 menit lagi"
            OrderStatus.DELIVERED -> "Pesanan telah tiba"
            OrderStatus.CANCELLED -> "Pesanan dibatalkan"
        }
    }
}
