package com.gymecommerce.musclecart.presentation.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gymecommerce.musclecart.domain.model.Cart
import com.gymecommerce.musclecart.domain.model.CourierService
import com.gymecommerce.musclecart.domain.model.Result
import com.gymecommerce.musclecart.domain.model.User
import com.gymecommerce.musclecart.domain.repository.VoucherRepository
import com.gymecommerce.musclecart.domain.repository.VoucherResult
import com.gymecommerce.musclecart.domain.repository.PointsRepository
import com.gymecommerce.musclecart.domain.repository.AuthRepository
import com.gymecommerce.musclecart.domain.repository.ShippingRepository
import com.gymecommerce.musclecart.domain.usecase.cart.GetCartItemsUseCase
import com.gymecommerce.musclecart.domain.usecase.order.CheckoutRequest
import com.gymecommerce.musclecart.domain.usecase.order.ProcessCheckoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CheckoutUiState(
    val cart: Cart = Cart(),
    val isLoading: Boolean = false,
    val isProcessingOrder: Boolean = false,
    val error: String? = null,
    val orderSuccess: Int? = null,

    // Saved address from user profile
    val savedUser: User? = null,
    val needsAddress: Boolean = false,

    // Shipping cost options
    val courierServices: List<CourierService> = emptyList(),
    val selectedCourierService: CourierService? = null,
    val isLoadingCost: Boolean = false,
    val shippingError: String? = null,

    // Voucher
    val voucherCode: String = "",
    val appliedVoucher: VoucherResult? = null,
    val isApplyingVoucher: Boolean = false,
    val voucherError: String? = null,

    // Loyalty Points
    val pointsBalance: Int = 0,
    val pointsInput: String = "",
    val appliedPoints: Int = 0,
    val isLoadingPoints: Boolean = false,
    val pointsError: String? = null,

    // Payment Method
    val selectedPaymentMethod: String = "cash" // cash | transfer | e_wallet
)

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val getCartItemsUseCase: GetCartItemsUseCase,
    private val processCheckoutUseCase: ProcessCheckoutUseCase,
    private val shippingRepository: ShippingRepository,
    private val authRepository: AuthRepository,
    private val voucherRepository: VoucherRepository,
    private val pointsRepository: PointsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    private val DEFAULT_WEIGHT = 1000
    private val COURIERS = listOf("jne", "jnt", "tiki", "pos")

    init {
        loadCart()
        observeSavedAddress()
        loadPointsBalance()
    }

    private fun loadCart() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                getCartItemsUseCase().catch { e ->
                    _uiState.value = _uiState.value.copy(isLoading = false, error = e.message ?: "Failed to load cart")
                }.collect { cart ->
                    _uiState.value = _uiState.value.copy(cart = cart, isLoading = false, error = null)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message ?: "Failed to load cart")
            }
        }
    }

    private fun observeSavedAddress() {
        viewModelScope.launch {
            // Ensure user is loaded from DataStore into the flow first
            authRepository.getCurrentUser()
            // Then observe live — updates automatically when address is saved in EditAddressScreen
            authRepository.getCurrentUserFlow().collect { user ->
                val prevCityId = _uiState.value.savedUser?.cityId
                if (user == null || !user.hasCompleteAddress) {
                    _uiState.value = _uiState.value.copy(savedUser = user, needsAddress = true)
                } else {
                    _uiState.value = _uiState.value.copy(savedUser = user, needsAddress = false)
                    // Reload shipping cost if city changed
                    if (prevCityId != user.cityId) {
                        _uiState.value = _uiState.value.copy(
                            courierServices = emptyList(),
                            selectedCourierService = null
                        )
                        loadShippingCost(user.cityId!!)
                    }
                }
            }
        }
    }

    private fun loadShippingCost(destinationCityId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingCost = true, shippingError = null)
            when (val result = shippingRepository.getCost(destinationCityId, DEFAULT_WEIGHT, COURIERS)) {
                is Result.Success -> _uiState.value = _uiState.value.copy(
                    courierServices = result.data,
                    isLoadingCost = false
                )
                is Result.Error -> _uiState.value = _uiState.value.copy(
                    isLoadingCost = false,
                    shippingError = result.message
                )
                else -> _uiState.value = _uiState.value.copy(isLoadingCost = false)
            }
        }
    }

    fun onCourierServiceSelected(service: CourierService) {
        _uiState.value = _uiState.value.copy(selectedCourierService = service)
    }

    fun onVoucherCodeChanged(code: String) {
        _uiState.value = _uiState.value.copy(voucherCode = code, voucherError = null)
        // Auto-remove applied voucher if user clears the field
        if (code.isBlank()) {
            _uiState.value = _uiState.value.copy(appliedVoucher = null)
        }
    }

    fun applyVoucher() {
        val state = _uiState.value
        val code = state.voucherCode.trim()
        if (code.isBlank()) return
        val subtotal = state.cart.getSubtotal().toDouble()
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isApplyingVoucher = true, voucherError = null)
            when (val result = voucherRepository.applyVoucher(code, subtotal)) {
                is Result.Success -> _uiState.value = _uiState.value.copy(
                    isApplyingVoucher = false,
                    appliedVoucher = result.data
                )
                is Result.Error -> _uiState.value = _uiState.value.copy(
                    isApplyingVoucher = false,
                    voucherError = result.message
                )
                else -> _uiState.value = _uiState.value.copy(isApplyingVoucher = false)
            }
        }
    }

    fun removeVoucher() {
        _uiState.value = _uiState.value.copy(
            appliedVoucher = null,
            voucherCode = "",
            voucherError = null
        )
    }

    fun loadPointsBalance() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingPoints = true)
            when (val result = pointsRepository.getPoints()) {
                is Result.Success -> _uiState.value = _uiState.value.copy(
                    pointsBalance = result.data.first,
                    isLoadingPoints = false
                )
                is Result.Error -> _uiState.value = _uiState.value.copy(
                    isLoadingPoints = false
                )
                else -> _uiState.value = _uiState.value.copy(isLoadingPoints = false)
            }
        }
    }

    fun onPointsInputChanged(value: String) {
        val digits = value.filter { it.isDigit() }
        _uiState.value = _uiState.value.copy(pointsInput = digits, pointsError = null)
    }

    fun applyPoints() {
        val state = _uiState.value
        val requested = state.pointsInput.toIntOrNull() ?: 0
        if (requested <= 0) {
            _uiState.value = state.copy(pointsError = "Masukkan jumlah poin yang valid")
            return
        }
        if (requested > state.pointsBalance) {
            _uiState.value = state.copy(pointsError = "Poin tidak cukup (saldo: ${state.pointsBalance})")
            return
        }
        val subtotal = state.cart.getSubtotal()
        val voucherDiscount = state.appliedVoucher?.discountAmount ?: 0
        val shippingCost = state.selectedCourierService?.cost ?: 0
        val tax = state.cart.getTaxAmount()
        val orderTotal = subtotal + tax + shippingCost - voucherDiscount
        // Rate: 10 pts = Rp 1 discount
        val maxByRule = (orderTotal * 0.20 * 10).toInt()
        val allowed = minOf(requested, state.pointsBalance, maxByRule)
        if (allowed <= 0) {
            _uiState.value = state.copy(pointsError = "Poin tidak bisa digunakan untuk pesanan ini")
            return
        }
        _uiState.value = state.copy(appliedPoints = allowed, pointsError = null)
    }

    fun removePoints() {
        _uiState.value = _uiState.value.copy(
            appliedPoints = 0,
            pointsInput = "",
            pointsError = null
        )
    }

    fun placeOrder() {
        viewModelScope.launch {
            val state = _uiState.value
            _uiState.value = state.copy(isProcessingOrder = true, error = null, orderSuccess = null)

            val user = state.savedUser
            if (user == null || !user.hasCompleteAddress) {
                _uiState.value = _uiState.value.copy(isProcessingOrder = false, error = "Lengkapi alamat pengiriman terlebih dahulu")
                return@launch
            }
            if (state.cart.isEmpty()) {
                _uiState.value = _uiState.value.copy(isProcessingOrder = false, error = "Keranjang kosong")
                return@launch
            }

            val selectedService = state.selectedCourierService
            val request = CheckoutRequest(
                shippingAddress   = user.formattedAddress,
                paymentMethod     = state.selectedPaymentMethod,
                shippingCost      = selectedService?.cost ?: 0,
                courier           = selectedService?.let { "${it.courier} ${it.service}" },
                courierService    = selectedService?.service,
                destinationCityId = user.cityId,
                voucherCode       = state.appliedVoucher?.code,
                discountAmount    = state.appliedVoucher?.discountAmount ?: 0,
                pointsUsed        = state.appliedPoints
            )

            when (val result = processCheckoutUseCase(request)) {
                is Result.Success -> _uiState.value = _uiState.value.copy(
                    isProcessingOrder = false,
                    orderSuccess = result.data.id,
                    error = null
                )
                is Result.Error -> _uiState.value = _uiState.value.copy(
                    isProcessingOrder = false,
                    error = result.message
                )
                else -> {}
            }
        }
    }

    fun selectPaymentMethod(method: String) {
        _uiState.value = _uiState.value.copy(selectedPaymentMethod = method)
    }

    fun clearError() { _uiState.value = _uiState.value.copy(error = null) }
    fun clearOrderSuccess() { _uiState.value = _uiState.value.copy(orderSuccess = null) }
}
