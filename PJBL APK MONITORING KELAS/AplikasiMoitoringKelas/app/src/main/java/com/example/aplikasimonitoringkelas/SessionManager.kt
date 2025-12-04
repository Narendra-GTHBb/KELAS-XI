package com.example.aplikasimonitoringkelas

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("app_session", Context.MODE_PRIVATE)
    
    companion object {
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USERNAME = "username"
        private const val KEY_USER_ROLE = "user_role"
        private const val KEY_KELAS_ID = "kelas_id"
        private const val KEY_KELAS_NAME = "kelas_name"
    }
    
    fun saveAuthToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }
    
    fun getAuthToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }
    
    fun saveUserData(id: Int, name: String, username: String, role: String, kelasId: Int? = null, kelasName: String? = null) {
        prefs.edit().apply {
            putInt(KEY_USER_ID, id)
            putString(KEY_USER_NAME, name)
            putString(KEY_USERNAME, username)
            putString(KEY_USER_ROLE, role)
            if (kelasId != null) {
                putInt(KEY_KELAS_ID, kelasId)
            }
            if (kelasName != null) {
                putString(KEY_KELAS_NAME, kelasName)
            }
            apply()
        }
    }
    
    fun getUserId(): Int {
        return prefs.getInt(KEY_USER_ID, 0)
    }
    
    fun getUserName(): String? {
        return prefs.getString(KEY_USER_NAME, null)
    }
    
    fun getUsername(): String? {
        return prefs.getString(KEY_USERNAME, null)
    }
    
    fun getUserRole(): String? {
        return prefs.getString(KEY_USER_ROLE, null)
    }
    
    fun getKelasId(): Int? {
        val id = prefs.getInt(KEY_KELAS_ID, 0)
        return if (id > 0) id else null
    }
    
    fun getKelasName(): String? {
        return prefs.getString(KEY_KELAS_NAME, null)
    }
    
    fun clearSession() {
        prefs.edit().clear().apply()
    }
    
    fun isLoggedIn(): Boolean {
        return getAuthToken() != null
    }
}
