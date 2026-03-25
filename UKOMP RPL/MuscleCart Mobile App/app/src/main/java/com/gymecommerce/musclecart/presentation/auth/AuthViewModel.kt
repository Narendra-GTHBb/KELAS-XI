package com.gymecommerce.musclecart.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gymecommerce.musclecart.domain.model.NetworkResult
import com.gymecommerce.musclecart.domain.model.User
import com.gymecommerce.musclecart.domain.repository.AuthRepository
import com.gymecommerce.musclecart.domain.usecase.auth.GetCurrentUserUseCase
import com.gymecommerce.musclecart.domain.usecase.auth.LoginUseCase
import com.gymecommerce.musclecart.domain.usecase.auth.LogoutUseCase
import com.gymecommerce.musclecart.domain.usecase.auth.RegisterUseCase
import com.gymecommerce.musclecart.domain.usecase.auth.UpdateProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val currentUser: User? = null,
    val isLoginSuccess: Boolean = false,
    val isRegisterSuccess: Boolean = false,
    val isUpdateProfileSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()
    
    init {
        checkCurrentUser()
        observeCurrentUser()
    }
    
    private fun checkCurrentUser() {
        viewModelScope.launch {
            val currentUser = getCurrentUserUseCase.getCurrentUser()
            _uiState.value = _uiState.value.copy(currentUser = currentUser)
        }
    }
    
    private fun observeCurrentUser() {
        viewModelScope.launch {
            getCurrentUserUseCase.getCurrentUserFlow().collect { user ->
                _uiState.value = _uiState.value.copy(currentUser = user)
            }
        }
    }
    
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                isLoginSuccess = false
            )
            
            when (val result = loginUseCase(email, password)) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        currentUser = result.data,
                        isLoginSuccess = true,
                        error = null
                    )
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message,
                        isLoginSuccess = false
                    )
                }
                is NetworkResult.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }
    
    fun register(name: String, email: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                isRegisterSuccess = false
            )
            
            when (val result = registerUseCase(name, email, password, confirmPassword)) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        currentUser = result.data,
                        isRegisterSuccess = true,
                        error = null
                    )
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message,
                        isRegisterSuccess = false
                    )
                }
                is NetworkResult.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }
    
    fun logout() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            when (val result = logoutUseCase()) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        currentUser = null,
                        isLoginSuccess = false,
                        isRegisterSuccess = false,
                        error = null
                    )
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                is NetworkResult.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun refreshUser() {
        viewModelScope.launch {
            when (val result = getCurrentUserUseCase.refreshUser()) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(currentUser = result.data)
                }
                else -> { /* Silently fail — user sees stale data */ }
            }
        }
    }
    
    fun clearSuccessStates() {
        _uiState.value = _uiState.value.copy(
            isLoginSuccess = false,
            isRegisterSuccess = false,
            isUpdateProfileSuccess = false
        )
    }

    fun updateProfile(
        name: String,
        email: String,
        phone: String? = null,
        address: String? = null,
        city: String? = null,
        postalCode: String? = null,
        provinceId: String? = null,
        cityId: String? = null,
        password: String? = null
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, isUpdateProfileSuccess = false)
            when (val result = updateProfileUseCase(name, email, phone, address, city, postalCode, provinceId, cityId, password)) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        currentUser = result.data,
                        isUpdateProfileSuccess = true,
                        error = null
                    )
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message,
                        isUpdateProfileSuccess = false
                    )
                }
                is NetworkResult.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }
    
    suspend fun isLoggedIn(): Boolean {
        return getCurrentUserUseCase.isLoggedIn()
    }

    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                isLoginSuccess = false
            )
            when (val result = authRepository.loginWithGoogle(idToken)) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        currentUser = result.data,
                        isLoginSuccess = true,
                        error = null
                    )
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message,
                        isLoginSuccess = false
                    )
                }
                is NetworkResult.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }
}