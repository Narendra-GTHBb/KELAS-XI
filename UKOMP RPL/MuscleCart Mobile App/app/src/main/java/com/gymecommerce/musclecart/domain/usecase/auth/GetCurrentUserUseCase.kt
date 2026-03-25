package com.gymecommerce.musclecart.domain.usecase.auth

import com.gymecommerce.musclecart.domain.model.NetworkResult
import com.gymecommerce.musclecart.domain.model.User
import com.gymecommerce.musclecart.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend fun getCurrentUser(): User? {
        return authRepository.getCurrentUser()
    }
    
    fun getCurrentUserFlow(): Flow<User?> {
        return authRepository.getCurrentUserFlow()
    }
    
    suspend fun isLoggedIn(): Boolean {
        return authRepository.isLoggedIn()
    }

    suspend fun refreshUser(): NetworkResult<User> {
        return authRepository.refreshUser()
    }
}