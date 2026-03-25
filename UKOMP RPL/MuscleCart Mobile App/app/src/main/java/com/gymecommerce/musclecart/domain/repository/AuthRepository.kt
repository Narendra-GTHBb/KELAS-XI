package com.gymecommerce.musclecart.domain.repository

import com.gymecommerce.musclecart.domain.model.NetworkResult
import com.gymecommerce.musclecart.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    
    suspend fun login(email: String, password: String): NetworkResult<User>
    
    suspend fun register(name: String, email: String, password: String): NetworkResult<User>
    
    suspend fun logout(): NetworkResult<Unit>
    
    suspend fun getCurrentUser(): User?
    
    fun getCurrentUserFlow(): Flow<User?>
    
    suspend fun isLoggedIn(): Boolean
    
    suspend fun saveAuthToken(token: String)
    
    suspend fun getAuthToken(): String?
    
    suspend fun clearAuthToken()
    
    suspend fun refreshToken(): NetworkResult<String>
    
    suspend fun updateProfile(
        name: String,
        email: String,
        phone: String? = null,
        address: String? = null,
        city: String? = null,
        postalCode: String? = null,
        provinceId: String? = null,
        cityId: String? = null,
        password: String? = null
    ): NetworkResult<User>
    
    suspend fun saveUser(user: User)
    
    suspend fun clearUser()

    suspend fun refreshUser(): NetworkResult<User>

    suspend fun loginWithGoogle(idToken: String): NetworkResult<User>

    suspend fun registerFcmToken(token: String)
}