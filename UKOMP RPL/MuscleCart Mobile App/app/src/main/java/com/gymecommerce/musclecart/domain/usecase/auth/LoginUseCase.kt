package com.gymecommerce.musclecart.domain.usecase.auth

import com.gymecommerce.musclecart.domain.model.NetworkResult
import com.gymecommerce.musclecart.domain.model.User
import com.gymecommerce.musclecart.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): NetworkResult<User> {
        return try {
            // Validate input
            if (email.isBlank()) {
                return NetworkResult.Error("Email cannot be empty")
            }
            
            if (password.isBlank()) {
                return NetworkResult.Error("Password cannot be empty")
            }
            
            if (!isValidEmail(email)) {
                return NetworkResult.Error("Please enter a valid email address")
            }
            
            if (password.length < 6) {
                return NetworkResult.Error("Password must be at least 6 characters")
            }
            
            // Attempt authentication
            authRepository.login(email, password)
        } catch (e: Exception) {
            NetworkResult.Error("Login failed: ${e.message}", e)
        }
    }
    
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}