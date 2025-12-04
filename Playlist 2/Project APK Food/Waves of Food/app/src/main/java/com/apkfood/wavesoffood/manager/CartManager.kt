package com.apkfood.wavesoffood.manager

import android.content.Context
import android.content.SharedPreferences
import com.apkfood.wavesoffood.data.model.Food
import com.apkfood.wavesoffood.model.CartItem
import com.apkfood.wavesoffood.utils.UserSessionManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * CartManager - Enhanced cart management with persistence
 * Mengelola keranjang belanja dalam aplikasi dengan penyimpanan lokal
 */
class CartManager private constructor() {
    
    interface CartUpdateListener {
        fun onCartUpdated()
    }
    
    companion object {
        @Volatile
        private var INSTANCE: CartManager? = null
        private const val PREF_NAME = "cart_pref"
        
        fun getInstance(): CartManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: CartManager().also { INSTANCE = it }
            }
        }
    }
    
    private val cartItems = mutableListOf<CartItem>()
    private val listeners = mutableListOf<CartUpdateListener>()
    private val gson = Gson()
    private var currentContext: Context? = null
    
    /**
     * Initialize cart with context for persistence
     */
    fun initialize(context: Context) {
        currentContext = context.applicationContext
        loadCartFromPreferences()
    }
    
    /**
     * Get user-specific cart key
     */
    private fun getCartKey(context: Context): String? {
        val currentUser = UserSessionManager.getCurrentUser(context)
        return if (!UserSessionManager.isGuest(context) && currentUser != null) {
            "cart_${currentUser.uid}"
        } else {
            "cart_guest"
        }
    }
    
    /**
     * Load cart items from SharedPreferences
     */
    private fun loadCartFromPreferences() {
        currentContext?.let { context ->
            try {
                val key = getCartKey(context) ?: return
                val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                val cartJson = prefs.getString(key, null)
                
                if (!cartJson.isNullOrEmpty()) {
                    val type = object : TypeToken<List<CartItem>>() {}.type
                    val loadedItems: List<CartItem> = gson.fromJson(cartJson, type) ?: emptyList()
                    cartItems.clear()
                    cartItems.addAll(loadedItems)
                    android.util.Log.d("CartManager", "Loaded ${cartItems.size} items from cache for key: $key")
                }
            } catch (e: Exception) {
                android.util.Log.e("CartManager", "Error loading cart from preferences", e)
                cartItems.clear()
            }
        }
    }
    
    /**
     * Save cart items to SharedPreferences
     */
    private fun saveCartToPreferences() {
        currentContext?.let { context ->
            try {
                val key = getCartKey(context) ?: return
                val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                val cartJson = gson.toJson(cartItems)
                prefs.edit().putString(key, cartJson).apply()
                android.util.Log.d("CartManager", "Saved ${cartItems.size} items to cache for key: $key")
            } catch (e: Exception) {
                android.util.Log.e("CartManager", "Error saving cart to preferences", e)
            }
        }
    }
    
    fun addListener(listener: CartUpdateListener) {
        listeners.add(listener)
    }
    
    fun removeListener(listener: CartUpdateListener) {
        listeners.remove(listener)
    }
    
    private fun notifyListeners() {
        listeners.forEach { it.onCartUpdated() }
    }
    
    /**
     * Menambah item ke keranjang
     */
    fun addToCart(food: Food, quantity: Int = 1) {
        val existingItem = cartItems.find { it.food.id == food.id }
        if (existingItem != null) {
            existingItem.quantity += quantity
        } else {
            cartItems.add(CartItem(food, quantity))
        }
        saveCartToPreferences()
        notifyListeners()
    }
    
    /**
     * Menghapus item dari keranjang
     */
    fun removeFromCart(foodId: String) {
        cartItems.removeAll { it.food.id == foodId }
        saveCartToPreferences()
        notifyListeners()
    }
    
    /**
     * Update quantity item
     */
    fun updateQuantity(foodId: String, quantity: Int) {
        val item = cartItems.find { it.food.id == foodId }
        item?.let {
            if (quantity <= 0) {
                removeFromCart(foodId)
            } else {
                it.quantity = quantity
                saveCartToPreferences()
                notifyListeners()
            }
        }
    }
    
    /**
     * Mendapatkan semua item di keranjang
     */
    fun getCartItems(): List<CartItem> {
        return cartItems.toList()
    }
    
    /**
     * Mendapatkan total item
     */
    fun getTotalItems(): Int {
        return cartItems.sumOf { it.quantity }
    }
    
    /**
     * Mendapatkan total harga
     */
    fun getTotalPrice(): Double {
        return cartItems.sumOf { it.getTotalPrice() }
    }
    
    /**
     * Mengosongkan keranjang
     */
    fun clearCart() {
        cartItems.clear()
        saveCartToPreferences()
        notifyListeners()
    }
    
    /**
     * Check if item exists in cart
     */
    fun isInCart(foodId: String): Boolean {
        return cartItems.any { it.food.id == foodId }
    }
    
    /**
     * Get quantity of specific item in cart
     */
    fun getQuantity(foodId: String): Int {
        return cartItems.find { it.food.id == foodId }?.quantity ?: 0
    }
    
    /**
     * Check apakah keranjang kosong
     */
    fun isEmpty(): Boolean {
        return cartItems.isEmpty()
    }
}
