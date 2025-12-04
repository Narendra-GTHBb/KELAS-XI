package com.apkfood.wavesoffood.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * Utility class untuk SharedPreferences
 */
object PreferenceHelper {
    
    private const val PREF_NAME = "waves_of_food_prefs"
    private const val KEY_IS_FIRST_LAUNCH = "is_first_launch"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_IS_ADMIN = "is_admin"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_USER_EMAIL = "user_email"
    
    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }
    
    /**
     * Check apakah ini first launch
     */
    fun isFirstLaunch(context: Context): Boolean {
        return getSharedPreferences(context).getBoolean(KEY_IS_FIRST_LAUNCH, true)
    }
    
    /**
     * Set first launch selesai
     */
    fun setFirstLaunchCompleted(context: Context) {
        getSharedPreferences(context).edit()
            .putBoolean(KEY_IS_FIRST_LAUNCH, false)
            .apply()
    }
    
    /**
     * Save user info
     */
    fun saveUserInfo(context: Context, userId: String, name: String, email: String, isAdmin: Boolean = false) {
        getSharedPreferences(context).edit()
            .putString(KEY_USER_ID, userId)
            .putString(KEY_USER_NAME, name)
            .putString(KEY_USER_EMAIL, email)
            .putBoolean(KEY_IS_ADMIN, isAdmin)
            .apply()
    }
    
    /**
     * Get user ID
     */
    fun getUserId(context: Context): String? {
        return getSharedPreferences(context).getString(KEY_USER_ID, null)
    }
    
    /**
     * Get user name
     */
    fun getUserName(context: Context): String? {
        return getSharedPreferences(context).getString(KEY_USER_NAME, null)
    }
    
    /**
     * Get user email
     */
    fun getUserEmail(context: Context): String? {
        return getSharedPreferences(context).getString(KEY_USER_EMAIL, null)
    }
    
    /**
     * Check apakah user adalah admin
     */
    fun isAdmin(context: Context): Boolean {
        return getSharedPreferences(context).getBoolean(KEY_IS_ADMIN, false)
    }
    
    /**
     * Clear semua data user (untuk logout)
     */
    fun clearUserData(context: Context) {
        getSharedPreferences(context).edit()
            .remove(KEY_USER_ID)
            .remove(KEY_USER_NAME)
            .remove(KEY_USER_EMAIL)
            .remove(KEY_IS_ADMIN)
            .apply()
    }
}
