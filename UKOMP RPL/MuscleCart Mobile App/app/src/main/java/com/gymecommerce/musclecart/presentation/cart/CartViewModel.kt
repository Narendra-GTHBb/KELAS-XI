package com.gymecommerce.musclecart.presentation.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gymecommerce.musclecart.domain.model.Cart
import com.gymecommerce.musclecart.domain.model.Result
import com.gymecommerce.musclecart.domain.usecase.cart.AddToCartUseCase
import com.gymecommerce.musclecart.domain.usecase.cart.ClearCartUseCase
import com.gymecommerce.musclecart.domain.usecase.cart.GetCartItemsUseCase
import com.gymecommerce.musclecart.domain.usecase.cart.RemoveFromCartUseCase
import com.gymecommerce.musclecart.domain.usecase.cart.UpdateCartQuantityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CartUiState(
    val cart: Cart = Cart(),
    val cartItemCount: Int = 0,
    val isLoading: Boolean = false,
    val isUpdating: Boolean = false,
    val updatingItems: Set<Int> = emptySet(), // Product IDs being updated
    val error: String? = null
)

@HiltViewModel
class CartViewModel @Inject constructor(
    private val getCartItemsUseCase: GetCartItemsUseCase,
    private val addToCartUseCase: AddToCartUseCase,
    private val removeFromCartUseCase: RemoveFromCartUseCase,
    private val updateCartQuantityUseCase: UpdateCartQuantityUseCase,
    private val clearCartUseCase: ClearCartUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()
    
    init {
        loadCart()
        observeCartItemCount()
    }
    
    // Public method to refresh cart data (called when screen becomes visible)
    fun refreshCart() {
        loadCart()
    }
    
    private fun loadCart() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                getCartItemsUseCase().catch { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load cart"
                    )
                }.collect { cart ->
                    _uiState.value = _uiState.value.copy(
                        cart = cart,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load cart"
                )
            }
        }
    }
    
    private fun observeCartItemCount() {
        viewModelScope.launch {
            try {
                getCartItemsUseCase.getCartItemCount().collect { count ->
                    _uiState.value = _uiState.value.copy(cartItemCount = count)
                }
            } catch (e: Exception) {
                // Handle error silently for cart count
            }
        }
    }
    
    fun addToCart(productId: Int, quantity: Int = 1) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                updatingItems = _uiState.value.updatingItems + productId,
                error = null
            )
            
            when (val result = addToCartUseCase(productId, quantity)) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        updatingItems = _uiState.value.updatingItems - productId
                    )
                    loadCart()
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        updatingItems = _uiState.value.updatingItems - productId,
                        error = result.message
                    )
                }
                is Result.Loading -> {
                    // Optional: handle loading state if needed
                }
            }
        }
    }
    
    fun removeFromCart(productId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                updatingItems = _uiState.value.updatingItems + productId,
                error = null
            )
            
            when (val result = removeFromCartUseCase(productId)) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        updatingItems = _uiState.value.updatingItems - productId
                    )
                    loadCart()
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        updatingItems = _uiState.value.updatingItems - productId,
                        error = result.message
                    )
                }
                is Result.Loading -> {
                    // Optional: handle loading state if needed
                }
            }
        }
    }
    
    fun updateQuantity(productId: Int, quantity: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                updatingItems = _uiState.value.updatingItems + productId,
                error = null
            )
            
            when (val result = updateCartQuantityUseCase(productId, quantity)) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        updatingItems = _uiState.value.updatingItems - productId
                    )
                    loadCart()
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        updatingItems = _uiState.value.updatingItems - productId,
                        error = result.message
                    )
                }
                is Result.Loading -> {
                    // Optional: handle loading state if needed
                }
            }
        }
    }
    
    fun clearCart() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUpdating = true, error = null)
            
            when (val result = clearCartUseCase()) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(isUpdating = false)
                    loadCart()
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isUpdating = false,
                        error = result.message
                    )
                }
                is Result.Loading -> {
                    // Optional: handle loading state if needed
                }
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    // Helper methods for integration with other screens
    suspend fun getCartItemCount(): Int {
        return _uiState.value.cartItemCount
    }
    
    fun isProductInCart(productId: Int): Boolean {
        return _uiState.value.cart.hasProduct(productId)
    }
    
    fun getCartTotal(): Double {
        return _uiState.value.cart.getTotalPrice()
    }
}