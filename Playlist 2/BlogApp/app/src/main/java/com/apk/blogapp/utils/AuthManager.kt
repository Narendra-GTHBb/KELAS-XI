package com.apk.blogapp.utils

import android.content.Context
import android.content.SharedPreferences

class AuthManager(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences("blog_auth", Context.MODE_PRIVATE)
    
    companion object {
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }
    
    fun saveUser(userId: String, email: String, name: String) {
        prefs.edit()
            .putString(KEY_USER_ID, userId)
            .putString(KEY_USER_EMAIL, email)
            .putString(KEY_USER_NAME, name)
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .apply()
    }
    
    fun getCurrentUserId(): String? = prefs.getString(KEY_USER_ID, null)
    fun getCurrentUserEmail(): String? = prefs.getString(KEY_USER_EMAIL, null)
    fun getCurrentUserName(): String? = prefs.getString(KEY_USER_NAME, null)
    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    
    fun logout() {
        prefs.edit().clear().apply()
    }
    
    // For test mode
    fun loginAsTestUser() {
        saveUser(
            userId = "test_user_${System.currentTimeMillis()}",
            email = "test@blogapp.com",
            name = "Test User"
        )
    }
}