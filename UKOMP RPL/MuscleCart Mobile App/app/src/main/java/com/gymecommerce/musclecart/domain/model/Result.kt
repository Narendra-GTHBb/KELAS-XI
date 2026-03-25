package com.gymecommerce.musclecart.domain.model

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable, val message: String = exception.message ?: "Unknown error") : Result<Nothing>() {
        constructor(message: String) : this(Exception(message), message)
    }
    object Loading : Result<Nothing>()
    
    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error
    val isLoading: Boolean get() = this is Loading
    
    fun getOrNull(): T? = if (this is Success) data else null
    
    fun exceptionOrNull(): Throwable? = if (this is Error) exception else null
    
    inline fun onSuccess(action: (T) -> Unit): Result<T> {
        if (this is Success) action(data)
        return this
    }
    
    inline fun onError(action: (Throwable) -> Unit): Result<T> {
        if (this is Error) action(exception)
        return this
    }
    
    inline fun onLoading(action: () -> Unit): Result<T> {
        if (this is Loading) action()
        return this
    }
}

// Extension functions for easier handling
inline fun <T> Result<T>.fold(
    onSuccess: (T) -> Unit,
    onError: (Throwable) -> Unit,
    onLoading: () -> Unit = {}
) {
    when (this) {
        is Result.Success -> onSuccess(data)
        is Result.Error -> onError(exception)
        is Result.Loading -> onLoading()
    }
}

fun <T> Result<T>.toUiState(): UiState<T> {
    return when (this) {
        is Result.Success -> UiState.Success(data)
        is Result.Error -> UiState.Error(message)
        is Result.Loading -> UiState.Loading
    }
}