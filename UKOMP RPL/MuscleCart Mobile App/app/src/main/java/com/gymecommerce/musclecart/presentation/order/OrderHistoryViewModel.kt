package com.gymecommerce.musclecart.presentation.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gymecommerce.musclecart.domain.model.Order
import com.gymecommerce.musclecart.domain.model.Result
import com.gymecommerce.musclecart.domain.usecase.order.GetOrderHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OrderHistoryUiState(
    val orders: List<Order> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class OrderHistoryViewModel @Inject constructor(
    private val getOrderHistoryUseCase: GetOrderHistoryUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(OrderHistoryUiState())
    val uiState: StateFlow<OrderHistoryUiState> = _uiState.asStateFlow()
    
    init {
        loadOrders()
    }
    
    fun loadOrders() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = getOrderHistoryUseCase()) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        orders = result.data,
                        isLoading = false,
                        error = null
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                is Result.Loading -> {
                    // no-op
                }
            }
        }
    }
    
    fun refreshOrders() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshing = true, error = null)
            when (val result = getOrderHistoryUseCase()) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        orders = result.data,
                        isRefreshing = false,
                        error = null
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isRefreshing = false,
                        error = result.message
                    )
                }
                is Result.Loading -> {
                    _uiState.value = _uiState.value.copy(isRefreshing = false)
                }
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun getOrderStats(): OrderStats {
        val orders = _uiState.value.orders
        return OrderStats(
            totalOrders = orders.size,
            pendingOrders = orders.count { it.isPending() },
            completedOrders = orders.count { it.isCompleted() },
            totalSpent = orders.sumOf { it.totalPrice }
        )
    }
}

data class OrderStats(
    val totalOrders: Int,
    val pendingOrders: Int,
    val completedOrders: Int,
    val totalSpent: Double
) {
    fun getFormattedTotalSpent(): String = "$%.2f".format(totalSpent)
}