package com.gymecommerce.musclecart.domain.model

/**
 * Sealed class for network/auth operation results.
 * Used by AuthRepository and UserRepository auth methods.
 */
sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error(val message: String, val exception: Exception? = null) : NetworkResult<Nothing>()
    object Loading : NetworkResult<Nothing>()
}
