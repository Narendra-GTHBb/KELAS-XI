package com.apkfood.wavesoffood.utils

import android.content.Context
import android.content.SharedPreferences
import com.apkfood.wavesoffood.data.model.Food
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Singleton class untuk mengelola makanan favorit per user
 * Terintegrasi dengan UserSessionManager untuk penyimpanan per user
 */
object FavoriteManager {
    
    private const val PREF_NAME = "favorites_pref"
    private val gson = Gson()
    private val listeners = mutableListOf<FavoriteUpdateListener>()
    
    /**
     * Interface untuk mendengarkan perubahan favorite
     */
    interface FavoriteUpdateListener {
        fun onFavoriteUpdated()
    }
    
    /**
     * Menambahkan listener untuk perubahan favorite
     */
    fun addListener(listener: FavoriteUpdateListener) {
        listeners.add(listener)
    }
    
    /**
     * Menghapus listener
     */
    fun removeListener(listener: FavoriteUpdateListener) {
        listeners.remove(listener)
    }
    
    /**
     * Memberitahu semua listener bahwa favorite telah berubah
     */
    private fun notifyListeners() {
        listeners.forEach { it.onFavoriteUpdated() }
    }
    
    /**
     * Get user-specific favorites key
     */
    private fun getFavoritesKey(context: Context): String? {
        val currentUser = UserSessionManager.getCurrentUser(context)
        val key = if (!UserSessionManager.isGuest(context) && currentUser != null) {
            "favorites_${currentUser.uid}"
        } else {
            null
        }
        android.util.Log.d("FavoriteManager", "getFavoritesKey: $key for user: ${currentUser?.email}")
        return key
    }
    
    /**
     * Get SharedPreferences
     */
    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }
    
    /**
     * Check if user can access favorites (only registered users)
     */
    fun canAccessFavorites(context: Context): Boolean {
        return !UserSessionManager.isGuest(context) && 
               UserSessionManager.getCurrentUser(context) != null
    }
    
    /**
     * Menambahkan makanan ke favorit
     */
    /**
     * Menambahkan makanan ke favorit
     */
    fun addToFavorite(food: Food): Boolean {
        return false // Tidak bisa akses tanpa context
    }
    
    /**
     * Menambahkan makanan ke favorit dengan context
     */
    fun addToFavorite(context: Context, food: Food): Boolean {
        if (!canAccessFavorites(context)) {
            android.util.Log.d("FavoriteManager", "Cannot access favorites - user not logged in")
            return false
        }
        
        val key = getFavoritesKey(context) ?: return false
        val favoriteIds = getFavoriteIds(context).toMutableSet()
        
        if (favoriteIds.add(food.id)) {
            saveFavoriteIds(context, key, favoriteIds)
            notifyListeners()
            android.util.Log.d("FavoriteManager", "Added to favorites: ${food.name} for key: $key")
            return true
        }
        android.util.Log.d("FavoriteManager", "Already in favorites: ${food.name}")
        return false
    }
    
    /**
     * Menghapus makanan dari favorit
     */
    fun removeFromFavorite(foodId: String): Boolean {
        return false // Tidak bisa akses tanpa context
    }
    
    /**
     * Menghapus makanan dari favorit dengan context
     */
    fun removeFromFavorite(context: Context, foodId: String): Boolean {
        if (!canAccessFavorites(context)) {
            return false
        }
        
        val key = getFavoritesKey(context) ?: return false
        val favoriteIds = getFavoriteIds(context).toMutableSet()
        
        if (favoriteIds.remove(foodId)) {
            saveFavoriteIds(context, key, favoriteIds)
            notifyListeners()
            return true
        }
        return false
    }
    
    /**
     * Toggle status favorite makanan
     */
    fun toggleFavorite(food: Food): Boolean {
        return false // Tidak bisa akses tanpa context
    }
    
    /**
     * Toggle status favorite makanan dengan context
     */
    fun toggleFavorite(context: Context, food: Food): Boolean {
        return if (isFavorite(context, food.id)) {
            removeFromFavorite(context, food.id)
            false
        } else {
            addToFavorite(context, food)
            true
        }
    }
    
    /**
     * Mengecek apakah makanan ada di favorit
     */
    fun isFavorite(foodId: String): Boolean {
        return false // Tidak bisa akses tanpa context
    }
    
    /**
     * Mengecek apakah makanan ada di favorit dengan context
     */
    fun isFavorite(context: Context, foodId: String): Boolean {
        if (!canAccessFavorites(context)) {
            return false
        }
        
        return getFavoriteIds(context).contains(foodId)
    }
    
    /**
     * Mendapatkan semua ID makanan favorit
     */
    fun getFavoriteIds(): Set<String> {
        return emptySet() // Tidak bisa akses tanpa context
    }
    
    /**
     * Mendapatkan semua ID makanan favorit dengan context
     */
    fun getFavoriteIds(context: Context): Set<String> {
        if (!canAccessFavorites(context)) {
            android.util.Log.d("FavoriteManager", "Cannot access favorites - returning empty set")
            return emptySet()
        }
        
        val key = getFavoritesKey(context) ?: return emptySet()
        val prefs = getPreferences(context)
        val favoritesJson = prefs.getString(key, null) ?: return emptySet()
        
        return try {
            val type = object : TypeToken<Set<String>>() {}.type
            val favorites = gson.fromJson(favoritesJson, type) ?: emptySet<String>()
            android.util.Log.d("FavoriteManager", "Got favorites for $key: ${favorites.size} items")
            favorites
        } catch (e: Exception) {
            e.printStackTrace()
            android.util.Log.e("FavoriteManager", "Error loading favorites: ${e.message}")
            emptySet()
        }
    }
    
    /**
     * Save favorite IDs to SharedPreferences
     */
    private fun saveFavoriteIds(context: Context, key: String, favoriteIds: Set<String>) {
        try {
            val prefs = getPreferences(context)
            val favoritesJson = gson.toJson(favoriteIds)
            prefs.edit().putString(key, favoritesJson).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Mendapatkan jumlah makanan favorit
     */
    fun getFavoriteCount(): Int {
        return 0 // Tidak bisa akses tanpa context
    }
    
    /**
     * Mendapatkan jumlah makanan favorit dengan context
     */
    fun getFavoriteCount(context: Context): Int {
        return getFavoriteIds(context).size
    }
    
    /**
     * Membersihkan semua favorit
     */
    fun clearFavorites() {
        // Tidak bisa akses tanpa context
    }
    
    /**
     * Membersihkan semua favorit dengan context
     */
    fun clearFavorites(context: Context): Boolean {
        if (!canAccessFavorites(context)) {
            return false
        }
        
        val key = getFavoritesKey(context) ?: return false
        val prefs = getPreferences(context)
        prefs.edit().remove(key).apply()
        notifyListeners()
        return true
    }
    
    /**
     * RESET TOTAL - Hapus SEMUA favorites dari SEMUA user
     */
    fun resetAllFavorites(context: Context) {
        val prefs = getPreferences(context)
        prefs.edit().clear().apply()
        notifyListeners()
        android.util.Log.d("FavoriteManager", "RESET: All favorites cleared")
    }
    
    /**
     * Debug: Get all favorites keys
     */
    fun debugGetAllFavoritesKeys(context: Context): List<String> {
        val prefs = getPreferences(context)
        return prefs.all.keys.filter { it.startsWith("favorites_") }
    }
}
