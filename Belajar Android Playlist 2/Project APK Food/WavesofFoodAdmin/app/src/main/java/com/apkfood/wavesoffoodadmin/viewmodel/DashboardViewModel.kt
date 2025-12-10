package com.apkfood.wavesoffoodadmin.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apkfood.wavesoffoodadmin.model.Analytics
import com.apkfood.wavesoffoodadmin.repository.AdminRepository
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {
    private val repository = AdminRepository()
    
    private val _analytics = MutableLiveData<Analytics>()
    val analytics: LiveData<Analytics> = _analytics
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error
    
    init {
        loadAnalytics()
    }
    
    fun loadAnalytics(period: String = "today") {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val data = repository.getAnalytics(period)
                _analytics.value = data
            } catch (e: Exception) {
                _error.value = e.message ?: "Error loading analytics"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun refreshData() {
        loadAnalytics()
    }
}
