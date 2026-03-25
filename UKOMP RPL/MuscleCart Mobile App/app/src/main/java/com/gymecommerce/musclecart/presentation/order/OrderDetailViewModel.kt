package com.gymecommerce.musclecart.presentation.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gymecommerce.musclecart.domain.model.Order
import com.gymecommerce.musclecart.domain.model.OrderStatus
import com.gymecommerce.musclecart.domain.model.Result
import com.gymecommerce.musclecart.domain.repository.OrderRepository
import com.gymecommerce.musclecart.domain.usecase.order.GetOrderHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OrderDetailUiState(
    val order: Order? = null,
    val isLoading: Boolean = false,
    val isConfirmingReceived: Boolean = false,
    val isCancelling: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class OrderDetailViewModel @Inject constructor(
    private val getOrderHistoryUseCase: GetOrderHistoryUseCase,
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrderDetailUiState())
    val uiState: StateFlow<OrderDetailUiState> = _uiState.asStateFlow()

    private var currentOrderId: Int = -1
    private var pollingJob: Job? = null

    fun startPolling(orderId: Int) {
        currentOrderId = orderId
        loadOrder(orderId, showLoading = true)
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (isActive) {
                delay(5_000L)
                if (currentOrderId != -1) {
                    loadOrder(currentOrderId, showLoading = false)
                }
            }
        }
    }

    fun stopPolling() {
        pollingJob?.cancel()
        pollingJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stopPolling()
    }

    fun loadOrder(orderId: Int, showLoading: Boolean = true) {
        viewModelScope.launch {
            if (showLoading) {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            }

            when (val result = getOrderHistoryUseCase.getOrderById(orderId)) {
                is Result.Success -> {
                    if (result.data != null) {
                        _uiState.value = _uiState.value.copy(
                            order = result.data,
                            isLoading = false,
                            error = null
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Order not found"
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = if (showLoading) result.message else _uiState.value.error
                    )
                }
                is Result.Loading -> Unit
            }
        }
    }

    fun confirmReceived() {
        val orderId = currentOrderId.takeIf { it != -1 } ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isConfirmingReceived = true, error = null)
            when (val result = orderRepository.confirmOrderReceived(orderId)) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        order = result.data,
                        isConfirmingReceived = false,
                        successMessage = "Pesanan dikonfirmasi selesai!"
                    )
                    stopPolling()
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isConfirmingReceived = false,
                        error = result.message
                    )
                }
                is Result.Loading -> Unit
            }
        }
    }

    fun cancelOrder() {
        val orderId = currentOrderId.takeIf { it != -1 } ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCancelling = true, error = null)
            when (val result = orderRepository.cancelOrder(orderId)) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        order = result.data,
                        isCancelling = false,
                        successMessage = "Pesanan berhasil dibatalkan"
                    )
                    stopPolling()
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isCancelling = false,
                        error = result.message
                    )
                }
                is Result.Loading -> Unit
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearSuccessMessage() {
        _uiState.value = _uiState.value.copy(successMessage = null)
    }

    fun getOrderSummary(): OrderSummary? {
        val order = _uiState.value.order ?: return null
        return OrderSummary(
            orderId = order.id,
            totalItems = order.getTotalItems(),
            totalPrice = order.totalPrice,
            status = order.status,
            canBeCancelled = order.canBeCancelled(),
            daysSinceOrder = ((System.currentTimeMillis() - order.createdAt) / (1000 * 60 * 60 * 24)).toInt()
        )
    }
}

data class OrderSummary(
    val orderId: Int,
    val totalItems: Int,
    val totalPrice: Double,
    val status: OrderStatus,
    val canBeCancelled: Boolean,
    val daysSinceOrder: Int
) {
    fun getStatusDescription(): String = when (status) {
        OrderStatus.PENDING    -> "Menunggu pembayaran"
        OrderStatus.PAID       -> "Pembayaran dikonfirmasi, sedang disiapkan"
        OrderStatus.PROCESSING -> "Pesanan sedang diproses"
        OrderStatus.SHIPPED    -> "Pesanan sedang dikirim"
        OrderStatus.DELIVERED  -> "Pesanan tiba, konfirmasi penerimaan"
        OrderStatus.COMPLETED  -> "Pesanan selesai"
        OrderStatus.CANCELLED  -> "Pesanan dibatalkan"
    }
}
