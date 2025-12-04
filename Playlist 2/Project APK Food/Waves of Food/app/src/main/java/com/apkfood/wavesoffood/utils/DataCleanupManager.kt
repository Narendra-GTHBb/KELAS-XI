package com.apkfood.wavesoffood.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.apkfood.wavesoffood.model.Order
import com.apkfood.wavesoffood.manager.OrderHistoryManager
import com.apkfood.wavesoffood.manager.CartManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Smart Data Cleanup Manager
 * PRESERVE semua data user, hanya LOAD data user aktif
 * TIDAK MENGHAPUS data user lain agar data tetap tersimpan
 */
object DataCleanupManager {
    
    private val gson = Gson()
    private const val TAG = "DataCleanupManager"
    
    /**
     * SMART ISOLATION: Isolasi data antar user TANPA MENGHAPUS
     * Fungsi ini akan dipanggil saat user login/switch account
     * Data semua user tetap tersimpan, hanya yang ditampilkan yang berbeda
     */
    fun cleanupOtherUsersData(context: Context) {
        try {
            val currentUser = UserSessionManager.getCurrentUser(context)
            val currentUserId = currentUser?.uid
            val isGuest = UserSessionManager.isGuest(context)
            
            Log.d(TAG, "Smart isolation: Current user ID = $currentUserId, isGuest = $isGuest")
            
            // Clean up any old data with empty UID (legacy fix)
            if (!isGuest && currentUserId != null && currentUserId.isNotEmpty()) {
                cleanupLegacyData(context)
            }
            
            // TIDAK MENGHAPUS DATA! 
            // Data isolation sudah ditangani oleh FavoriteManager dan OrderHistoryManager
            // dengan menggunakan user-specific keys
            
            if (isGuest) {
                Log.d(TAG, "Guest mode: Using guest-specific data isolation")
            } else if (currentUserId != null) {
                Log.d(TAG, "Registered user: Using user-specific data isolation for $currentUserId")
                // Verifikasi bahwa data user tersimpan dengan benar
                verifyUserDataIntegrity(context, currentUserId)
            }
            
            Log.d(TAG, "Smart isolation completed - ALL USER DATA PRESERVED")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error during smart isolation", e)
        }
    }
    
    /**
     * Clean up legacy data that might have empty UIDs
     */
    private fun cleanupLegacyData(context: Context) {
        try {
            Log.d(TAG, "Cleaning up legacy data with empty UIDs...")
            
            // Remove any favorites with empty user ID key
            val favoritesPrefs = context.getSharedPreferences("favorites_pref", Context.MODE_PRIVATE)
            val emptyKeyExists = favoritesPrefs.contains("favorites_")
            if (emptyKeyExists) {
                favoritesPrefs.edit().remove("favorites_").apply()
                Log.d(TAG, "Removed legacy favorites with empty UID")
            }
            
            // Remove any order history with empty user ID key
            val orderPrefs = context.getSharedPreferences("order_history_pref", Context.MODE_PRIVATE)
            val emptyOrderKeyExists = orderPrefs.contains("order_history_")
            if (emptyOrderKeyExists) {
                orderPrefs.edit().remove("order_history_").apply()
                Log.d(TAG, "Removed legacy order history with empty UID")
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error cleaning up legacy data", e)
        }
    }
    
    /**
     * Verifikasi integritas data user (untuk debugging)
     */
    /**
     * Verifikasi integritas data user (untuk debugging)
     */
    private fun verifyUserDataIntegrity(context: Context, currentUserId: String) {
        try {
            // Check favorites data
            val favoritesSharedPref = context.getSharedPreferences("favorites_pref", Context.MODE_PRIVATE)
            val favoritesKey = "favorites_$currentUserId"
            val hasFavorites = favoritesSharedPref.contains(favoritesKey)
            Log.d(TAG, "User $currentUserId favorites data exists: $hasFavorites")
            
            // Check order history data
            val orderHistorySharedPref = context.getSharedPreferences("order_history_pref", Context.MODE_PRIVATE)
            val orderHistoryKey = "order_history_$currentUserId"
            val hasOrderHistory = orderHistorySharedPref.contains(orderHistoryKey)
            Log.d(TAG, "User $currentUserId order history data exists: $hasOrderHistory")
            
            // Log all existing keys for debugging
            val allFavKeys = favoritesSharedPref.all.keys
            val allOrderKeys = orderHistorySharedPref.all.keys
            Log.d(TAG, "All favorites keys: $allFavKeys")
            Log.d(TAG, "All order history keys: $allOrderKeys")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error verifying user data integrity", e)
        }
    }
    
    /**
     * LOGOUT ONLY: Hapus HANYA data sementara saat logout
     * TIDAK MENGHAPUS data permanen user (favorites, order history)
     */
    fun performLogoutCleanup(context: Context) {
        try {
            Log.d(TAG, "LOGOUT CLEANUP: Clearing ONLY temporary data")
            
            // HANYA hapus data sementara, TIDAK hapus data permanen user
            
            // 1. Clear in-memory cache only
            OrderManager.clearAllOrders()
            Log.d(TAG, "Cleared temporary order cache on logout")
            
            // 2. Clear cart data
            CartManager.getInstance().clearCart()
            Log.d(TAG, "Cleared cart data on logout")
            
            // 3. TIDAK MENGHAPUS SharedPreferences data
            // Favorites dan Order History tetap tersimpan per user
            Log.d(TAG, "USER DATA PRESERVED: Favorites and Order History kept for future logins")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error during logout cleanup", e)
        }
    }
    
    /**
     * EMERGENCY ONLY: Hapus SEMUA data (untuk debugging/reset)
     * Hanya digunakan untuk debugging, bukan untuk logout normal
     */
    fun emergencyDataReset(context: Context) {
        try {
            Log.d(TAG, "EMERGENCY RESET: Clearing ALL data")
            
            // Clear all SharedPreferences
            val favoritesSharedPref = context.getSharedPreferences("favorites_pref", Context.MODE_PRIVATE)
            favoritesSharedPref.edit().clear().apply()
            
            val orderHistorySharedPref = context.getSharedPreferences("order_history_pref", Context.MODE_PRIVATE)
            orderHistorySharedPref.edit().clear().apply()
            
            // Clear in-memory cache
            OrderManager.clearAllOrders()
            FavoriteManager.clearFavorites(context)
            
            Log.d(TAG, "Emergency reset completed - ALL DATA CLEARED")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error during emergency reset", e)
        }
    }
    
    // ========== DEBUG FUNCTIONS ==========
    
    /**
     * Debug: Tampilkan semua data yang tersimpan
     */
    fun debugShowAllData(context: Context): String {
        val result = StringBuilder()
        
        try {
            // Show favorites
            val favPrefs = context.getSharedPreferences("favorites_pref", Context.MODE_PRIVATE)
            result.append("=== FAVORITES DATA ===\n")
            for (key in favPrefs.all.keys) {
                result.append("Key: $key\n")
            }
            
            // Show order history
            val orderPrefs = context.getSharedPreferences("order_history_pref", Context.MODE_PRIVATE)
            result.append("\n=== ORDER HISTORY DATA ===\n")
            for (key in orderPrefs.all.keys) {
                result.append("Key: $key\n")
            }
            
        } catch (e: Exception) {
            result.append("Error getting debug data: ${e.message}")
        }
        
        return result.toString()
    }
}
