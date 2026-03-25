package com.gymecommerce.musclecart.domain.usecase.auth

import com.gymecommerce.musclecart.domain.model.NetworkResult
import com.gymecommerce.musclecart.domain.model.User
import com.gymecommerce.musclecart.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ): NetworkResult<User> {
        return try {
            // Validate input
            if (name.isBlank()) {
                return NetworkResult.Error("Name cannot be empty")
            }
            
            if (email.isBlank()) {
                return NetworkResult.Error("Email cannot be empty")
            }
            
            if (password.isBlank()) {
                return NetworkResult.Error("Password cannot be empty")
            }
            
            if (confirmPassword.isBlank()) {
                return NetworkResult.Error("Please confirm your password")
            }
            
            if (!isValidEmail(email)) {
                return NetworkResult.Error("Please enter a valid email address")
            }
            
            if (password.length < 6) {
                return NetworkResult.Error("Password must be at least 6 characters")
            }
            
            if (password != confirmPassword) {
                return NetworkResult.Error("Passwords do not match")
            }
            
            if (name.length < 2) {
                return NetworkResult.Error("Name must be at least 2 characters")
            }
            
            // Attempt registration
            authRepository.register(name, email, password)
        } catch (e: Exception) {
            NetworkResult.Error("Registration failed: ${e.message}", e)
        }
    }
    
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}