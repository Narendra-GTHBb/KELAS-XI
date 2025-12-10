package com.apkfood.wavesoffood.utils

import android.content.Context
import android.content.SharedPreferences
import com.apkfood.wavesoffood.model.User

/**
 * UserSessionManager - Mengelola session user yang sedang login
 */
object UserSessionManager {
    
    private const val PREF_NAME = "user_session"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_USER_EMAIL = "user_email"
    private const val KEY_USER_PHONE = "user_phone"
    private const val KEY_USER_ADDRESS = "user_address"
    private const val KEY_IS_ADMIN = "is_admin"
    private const val KEY_IS_GUEST = "is_guest"
    
    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }
    
    /**
     * Save user session after login
     */
    fun saveUserSession(context: Context, user: User, isGuest: Boolean = false) {
        getPreferences(context).edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, true)
            putBoolean(KEY_IS_GUEST, isGuest)
            putString(KEY_USER_ID, user.uid)
            putString(KEY_USER_NAME, user.name)
            putString(KEY_USER_EMAIL, user.email)
            putString(KEY_USER_PHONE, user.phone)
            putString(KEY_USER_ADDRESS, user.address)
            putBoolean(KEY_IS_ADMIN, user.isAdmin)
            apply()
        }
    }
    
    /**
     * Save guest session
     */
    fun saveGuestSession(context: Context) {
        getPreferences(context).edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, true)
            putBoolean(KEY_IS_GUEST, true)
            putString(KEY_USER_ID, "guest_${System.currentTimeMillis()}")
            putString(KEY_USER_NAME, "Guest User")
            putString(KEY_USER_EMAIL, "guest@example.com")
            putString(KEY_USER_PHONE, "")
            putString(KEY_USER_ADDRESS, "")
            putBoolean(KEY_IS_ADMIN, false)
            apply()
        }
    }
    
    /**
     * Get current user data
     */
    fun getCurrentUser(context: Context): User? {
        val prefs = getPreferences(context)
        return if (prefs.getBoolean(KEY_IS_LOGGED_IN, false)) {
            User(
                uid = prefs.getString(KEY_USER_ID, "") ?: "",
                name = prefs.getString(KEY_USER_NAME, "") ?: "",
                email = prefs.getString(KEY_USER_EMAIL, "") ?: "",
                phone = prefs.getString(KEY_USER_PHONE, "") ?: "",
                address = prefs.getString(KEY_USER_ADDRESS, "") ?: "",
                isAdmin = prefs.getBoolean(KEY_IS_ADMIN, false)
            )
        } else null
    }
    
    /**
     * Check if user is logged in
     */
    fun isLoggedIn(context: Context): Boolean {
        return getPreferences(context).getBoolean(KEY_IS_LOGGED_IN, false)
    }
    
    /**
     * Check if current user is guest
     */
    fun isGuest(context: Context): Boolean {
        return getPreferences(context).getBoolean(KEY_IS_GUEST, false)
    }
    
    /**
     * Check if current user is admin
     */
    fun isAdmin(context: Context): Boolean {
        return getPreferences(context).getBoolean(KEY_IS_ADMIN, false)
    }
    
    /**
     * Get user name
     */
    fun getUserName(context: Context): String {
        return getPreferences(context).getString(KEY_USER_NAME, "Guest") ?: "Guest"
    }
    
    /**
     * Get user email
     */
    fun getUserEmail(context: Context): String {
        return getPreferences(context).getString(KEY_USER_EMAIL, "guest@example.com") ?: "guest@example.com"
    }
    
    /**
     * Update user data
     */
    fun updateUserData(context: Context, user: User) {
        getPreferences(context).edit().apply {
            putString(KEY_USER_NAME, user.name)
            putString(KEY_USER_EMAIL, user.email)
            putString(KEY_USER_PHONE, user.phone)
            putString(KEY_USER_ADDRESS, user.address)
            apply()
        }
    }
    
    /**
     * Clear user session (logout)
     */
    fun clearSession(context: Context) {
        getPreferences(context).edit().clear().apply()
    }
    
    /**
     * Get display name for UI
     */
    fun getDisplayName(context: Context): String {
        return if (isGuest(context)) {
            "Guest User"
        } else {
            getUserName(context)
        }
    }
}
