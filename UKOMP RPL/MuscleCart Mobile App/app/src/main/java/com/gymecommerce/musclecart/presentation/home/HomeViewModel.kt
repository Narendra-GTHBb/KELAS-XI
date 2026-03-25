package com.gymecommerce.musclecart.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gymecommerce.musclecart.domain.model.Product
import com.gymecommerce.musclecart.domain.usecase.cart.AddToCartUseCase
import com.gymecommerce.musclecart.domain.usecase.product.GetProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val featuredProduct: Product? = null,
    val recommendedProducts: List<Product> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val addToCartUseCase: AddToCartUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    private fun loadHomeData(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { 
                it.copy(
                    isLoading = !forceRefresh,
                    isRefreshing = forceRefresh
                )
            }
            
            try {
                getProductsUseCase(
                    categoryId = null,
                    forceRefresh = forceRefresh
                ).collect { result ->
                    when (result) {
                        is com.gymecommerce.musclecart.domain.util.Resource.Success -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    isRefreshing = false,
                                    featuredProduct = result.data?.firstOrNull(),
                                    recommendedProducts = result.data?.take(5) ?: emptyList(),
                                    error = null
                                )
                            }
                        }
                        is com.gymecommerce.musclecart.domain.util.Resource.Error -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    isRefreshing = false,
                                    error = result.message
                                )
                            }
                        }
                        is com.gymecommerce.musclecart.domain.util.Resource.Loading -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = !forceRefresh,
                                    isRefreshing = forceRefresh
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        error = e.message
                    )
                }
            }
        }
    }

    fun addToCart(product: Product) {
        viewModelScope.launch {
            try {
                addToCartUseCase(product.id, 1)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun refreshHomeData() {
        loadHomeData(forceRefresh = true)
    }
}
