package com.gymecommerce.musclecart.domain.model

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<out T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
    
    val isLoading: Boolean get() = this is Loading
    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error
    
    fun getOrNull(): T? = if (this is Success) data else null
}

// Common UI states for different screens
data class ProductListUiState(
    val products: List<Product> = emptyList(),
    val categories: List<Category> = emptyList(),
    val selectedCategory: Category? = null,
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isRefreshing: Boolean = false
)

data class ProductDetailUiState(
    val product: Product? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAddingToCart: Boolean = false,
    val addToCartSuccess: Boolean = false
)

data class CartUiState(
    val cart: Cart = Cart(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isUpdating: Boolean = false
)

data class CheckoutUiState(
    val cart: Cart = Cart(),
    val shippingAddress: String = "",
    val isProcessing: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
    val orderId: Int? = null
)

data class OrderHistoryUiState(
    val orders: List<Order> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isRefreshing: Boolean = false
)

data class AdminDashboardUiState(
    val totalProducts: Int = 0,
    val totalOrders: Int = 0,
    val totalUsers: Int = 0,
    val totalRevenue: Double = 0.0,
    val pendingOrders: Int = 0,
    val lowStockProducts: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)