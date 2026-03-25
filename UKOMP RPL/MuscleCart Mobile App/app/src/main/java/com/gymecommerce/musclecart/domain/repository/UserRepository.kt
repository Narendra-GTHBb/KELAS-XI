package com.gymecommerce.musclecart.domain.repository

import com.gymecommerce.musclecart.domain.model.NetworkResult
import com.gymecommerce.musclecart.domain.model.Result
import com.gymecommerce.musclecart.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun authenticateUser(email: String, password: String): NetworkResult<User>

    suspend fun registerUser(name: String, email: String, password: String): NetworkResult<User>
    
    suspend fun login(email: String, password: String): Result<User>
    
    suspend fun register(name: String, email: String, password: String): Result<User>
    
    suspend fun logout(): Result<Unit>
    
    suspend fun getCurrentUser(): Result<User?>
    
    fun getCurrentUserFlow(): Flow<User?>
    
    suspend fun getUserById(userId: Int): Result<User?>
    
    suspend fun getUserByEmail(email: String): Result<User?>
    
    suspend fun updateUser(user: User): Result<User>
    
    suspend fun deleteUser(userId: Int): Result<Unit>
    
    suspend fun getAllUsers(): Result<List<User>>
    
    fun getAllUsersFlow(): Flow<List<User>>
    
    suspend fun getUserCount(): Result<Int>
    
    suspend fun getAdminCount(): Result<Int>
    
    suspend fun isLoggedIn(): Boolean
    
    suspend fun saveUserSession(user: User): Result<Unit>
    
    suspend fun clearUserSession(): Result<Unit>
}