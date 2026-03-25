package com.gymecommerce.musclecart.presentation.track

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gymecommerce.musclecart.domain.model.Order
import com.gymecommerce.musclecart.domain.model.OrderStatus
import com.gymecommerce.musclecart.domain.model.Result
import com.gymecommerce.musclecart.domain.usecase.order.GetOrderHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TrackPackagesUiState(
    val activeOrders: List<Order> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class TrackPackagesViewModel @Inject constructor(
    private val getOrderHistoryUseCase: GetOrderHistoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TrackPackagesUiState())
    val uiState: StateFlow<TrackPackagesUiState> = _uiState.asStateFlow()

    // Statuses considered "in transit" / actively trackable
    private val activeStatuses = setOf(
        OrderStatus.PAID,
        OrderStatus.PROCESSING,
        OrderStatus.SHIPPED,
        OrderStatus.DELIVERED
    )

    init {
        loadPackages()
    }

    fun loadPackages() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = getOrderHistoryUseCase()) {
                is Result.Success -> _uiState.value = _uiState.value.copy(
                    activeOrders = result.data.filter { it.status in activeStatuses },
                    isLoading = false
                )
                is Result.Error -> _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = result.message
                )
                else -> _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshing = true, error = null)
            when (val result = getOrderHistoryUseCase()) {
                is Result.Success -> _uiState.value = _uiState.value.copy(
                    activeOrders = result.data.filter { it.status in activeStatuses },
                    isRefreshing = false
                )
                is Result.Error -> _uiState.value = _uiState.value.copy(
                    isRefreshing = false,
                    error = result.message
                )
                else -> _uiState.value = _uiState.value.copy(isRefreshing = false)
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
