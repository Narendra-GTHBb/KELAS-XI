package com.gymecommerce.musclecart.presentation.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gymecommerce.musclecart.domain.model.Product
import com.gymecommerce.musclecart.domain.usecase.cart.AddToCartUseCase
import com.gymecommerce.musclecart.domain.usecase.product.GetProductByIdUseCase
import com.gymecommerce.musclecart.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProductDetailUiState(
    val product: Product? = null,
    val isLoading: Boolean = false,
    val isAddingToCart: Boolean = false,
    val error: String? = null,
    val addToCartSuccess: Boolean = false
)

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val getProductByIdUseCase: GetProductByIdUseCase,
    private val addToCartUseCase: AddToCartUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()
    
    fun setProduct(product: Product) {
        _uiState.update { 
            it.copy(
                product = product,
                isLoading = false,
                error = null
            )
        }
    }
    
    fun loadProduct(productId: Int, forceRefresh: Boolean = false) {
        viewModelScope.launch {
            getProductByIdUseCase(productId, forceRefresh).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _uiState.update { 
                            it.copy(
                                isLoading = true,
                                error = null
                            )
                        }
                    }
                    is Resource.Success -> {
                        _uiState.update { 
                            it.copy(
                                product = resource.data,
                                isLoading = false,
                                error = null
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                error = resource.message
                            )
                        }
                    }
                }
            }
        }
    }
    
    fun addToCart(product: Product, quantity: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isAddingToCart = true) }
            
            when (val result = addToCartUseCase(product.id, quantity)) {
                is com.gymecommerce.musclecart.domain.model.Result.Success -> {
                    _uiState.update { 
                        it.copy(
                            isAddingToCart = false,
                            addToCartSuccess = true
                        )
                    }
                }
                is com.gymecommerce.musclecart.domain.model.Result.Error -> {
                    _uiState.update { 
                        it.copy(
                            isAddingToCart = false,
                            error = result.message
                        )
                    }
                }
                is com.gymecommerce.musclecart.domain.model.Result.Loading -> {
                    // Already handling loading state above
                }
            }
        }
    }
    
    fun clearAddToCartSuccess() {
        _uiState.update { it.copy(addToCartSuccess = false) }
    }

    // Simple suspend function - caller waits for result directly
    // Returns Pair(success, errorMessage)
    suspend fun addToCartSuspend(productId: Int, quantity: Int): Pair<Boolean, String?> {
        android.util.Log.d("BuyNow", "addToCartSuspend called: productId=$productId, quantity=$quantity")
        return when (val result = addToCartUseCase(productId, quantity)) {
            is com.gymecommerce.musclecart.domain.model.Result.Success -> {
                android.util.Log.d("BuyNow", "addToCartSuspend SUCCESS")
                Pair(true, null)
            }
            is com.gymecommerce.musclecart.domain.model.Result.Error -> {
                android.util.Log.e("BuyNow", "addToCartSuspend ERROR: ${result.message}")
                Pair(false, result.message)
            }
            is com.gymecommerce.musclecart.domain.model.Result.Loading -> {
                Pair(false, "Unexpected loading state")
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}