package com.apkfood.wavesoffoodadmin.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.apkfood.wavesoffoodadmin.model.AdminUser
import com.apkfood.wavesoffoodadmin.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepository = AuthRepository(application.applicationContext)
    
    private val _loginResult = MutableLiveData<Result<AdminUser>?>()
    val loginResult: LiveData<Result<AdminUser>?> = _loginResult
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _currentAdmin = MutableLiveData<AdminUser?>()
    val currentAdmin: LiveData<AdminUser?> = _currentAdmin
    
    private val _resetPasswordResult = MutableLiveData<Result<Unit>?>()
    val resetPasswordResult: LiveData<Result<Unit>?> = _resetPasswordResult
    
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = authRepository.loginAdmin(email, password)
                _loginResult.value = result
                if (result.isSuccess) {
                    _currentAdmin.value = result.getOrNull()
                }
            } catch (e: Exception) {
                _loginResult.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun getCurrentAdmin() {
        viewModelScope.launch {
            try {
                // First try to get cached admin
                val cachedAdmin = authRepository.getCachedAdmin()
                if (cachedAdmin != null && authRepository.isLoggedIn()) {
                    _currentAdmin.value = cachedAdmin
                } else {
                    // If no cache or not logged in, try to get from Firebase
                    val admin = authRepository.getCurrentAdmin()
                    _currentAdmin.value = admin
                    if (admin == null) {
                        // Clear invalid session
                        authRepository.logout()
                    }
                }
            } catch (e: Exception) {
                _currentAdmin.value = null
                authRepository.logout()
            }
        }
    }
    
    fun logout() {
        authRepository.logout()
        _currentAdmin.value = null
        _loginResult.value = null
    }
    
    fun resetPassword(email: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = authRepository.resetPassword(email)
                _resetPasswordResult.value = result
            } catch (e: Exception) {
                _resetPasswordResult.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun isLoggedIn(): Boolean {
        return authRepository.isLoggedIn()
    }
    
    fun clearLoginResult() {
        _loginResult.value = null
    }
    
    fun clearResetPasswordResult() {
        _resetPasswordResult.value = null
    }
}
