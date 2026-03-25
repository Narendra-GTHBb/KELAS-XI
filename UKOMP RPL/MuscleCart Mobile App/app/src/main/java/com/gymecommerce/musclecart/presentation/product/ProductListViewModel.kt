package com.gymecommerce.musclecart.presentation.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gymecommerce.musclecart.domain.model.Category
import com.gymecommerce.musclecart.domain.model.Product
import com.gymecommerce.musclecart.domain.usecase.product.GetCategoriesUseCase
import com.gymecommerce.musclecart.domain.usecase.product.GetProductsUseCase
import com.gymecommerce.musclecart.domain.usecase.product.SearchProductsUseCase
import com.gymecommerce.musclecart.domain.repository.FavoriteRepository
import com.gymecommerce.musclecart.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SortOption {
    POPULARITY, PRICE_LOW_HIGH, PRICE_HIGH_LOW, NAME_AZ
}

data class ProductFilter(
    val minPrice: Double? = null,
    val maxPrice: Double? = null,
    val inStockOnly: Boolean = false
)

data class ProductListUiState(
    val allProducts: List<Product> = emptyList(), // raw from API
    val products: List<Product> = emptyList(),    // after sort+filter
    val categories: List<Category> = emptyList(),
    val selectedCategoryId: Int? = null,
    val searchQuery: String = "",
    val isSearchMode: Boolean = false,
    val sortOption: SortOption = SortOption.POPULARITY,
    val filter: ProductFilter = ProductFilter(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val showAddToCartSuccess: Boolean = false,
    val favoriteMessage: String? = null,
    val favoriteIsError: Boolean = false,
    val favoriteProductIds: Set<Int> = emptySet()
)

@OptIn(FlowPreview::class)
@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val searchProductsUseCase: SearchProductsUseCase,
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ProductListUiState())
    val uiState: StateFlow<ProductListUiState> = _uiState.asStateFlow()
    
    private val searchQueryFlow = MutableStateFlow("")
    
    init {
        // Load categories and products immediately on init
        viewModelScope.launch {
            // Set loading state
            _uiState.update { it.copy(isLoading = true) }
            
            // Load both categories and products
            loadCategories(forceRefresh = true)
            loadProducts(forceRefresh = true)
            loadFavoriteIds()
        }
        setupSearch()
    }

    fun loadFavoriteIds() {
        viewModelScope.launch {
            favoriteRepository.getFavorites().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val ids = result.data?.map { it.id }?.toSet() ?: emptySet()
                        _uiState.update { it.copy(favoriteProductIds = ids) }
                    }
                    else -> {}
                }
            }
        }
    }
    
    private fun setupSearch() {
        searchQueryFlow
            .debounce(300) // Wait 300ms after user stops typing
            .distinctUntilChanged()
            .onEach { query ->
                if (query.isNotEmpty() && _uiState.value.isSearchMode) {
                    searchProducts(query)
                } else if (query.isEmpty() && _uiState.value.isSearchMode) {
                    loadProducts()
                }
            }
            .launchIn(viewModelScope)
    }
    
    fun loadProducts(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            getProductsUseCase(
                categoryId = _uiState.value.selectedCategoryId,
                forceRefresh = forceRefresh
            ).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _uiState.update {
                            it.copy(
                                isLoading = !forceRefresh,
                                isRefreshing = forceRefresh,
                                error = null
                            )
                        }
                    }
                    is Resource.Success -> {
                        val raw = resource.data ?: emptyList()
                        _uiState.update {
                            it.copy(
                                allProducts = raw,
                                products = applyFilterAndSort(raw, it.filter, it.sortOption),
                                isLoading = false,
                                isRefreshing = false,
                                error = null
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isRefreshing = false,
                                error = resource.message
                            )
                        }
                    }
                }
            }
        }
    }
    
    private fun loadCategories(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            getCategoriesUseCase(forceRefresh).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _uiState.update { 
                            it.copy(categories = resource.data ?: emptyList())
                        }
                    }
                    is Resource.Error -> {
                        // Handle category loading error silently or show a toast
                    }
                    is Resource.Loading -> {
                        // Categories loading state can be handled if needed
                    }
                }
            }
        }
    }
    
    private fun searchProducts(query: String) {
        viewModelScope.launch {
            searchProductsUseCase(
                query = query
            ).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _uiState.update {
                            it.copy(isLoading = true, error = null)
                        }
                    }
                    is Resource.Success -> {
                        val raw = resource.data ?: emptyList()
                        _uiState.update {
                            it.copy(
                                allProducts = raw,
                                products = applyFilterAndSort(raw, it.filter, it.sortOption),
                                isLoading = false,
                                error = null
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(isLoading = false, error = resource.message)
                        }
                    }
                }
            }
        }
    }
    
    fun selectCategory(categoryId: Int?) {
        _uiState.update {
            it.copy(
                selectedCategoryId = categoryId,
                searchQuery = "",
                isSearchMode = false,
                isLoading = true
            )
        }
        searchQueryFlow.value = ""
        loadProducts(forceRefresh = true)
    }

    fun setSortOption(option: SortOption) {
        _uiState.update {
            val sorted = applyFilterAndSort(it.allProducts, it.filter, option)
            it.copy(sortOption = option, products = sorted)
        }
    }

    fun setFilter(filter: ProductFilter) {
        _uiState.update {
            val filtered = applyFilterAndSort(it.allProducts, filter, it.sortOption)
            it.copy(filter = filter, products = filtered)
        }
    }

    private fun applyFilterAndSort(
        products: List<Product>,
        filter: ProductFilter,
        sort: SortOption
    ): List<Product> {
        var result = products
        filter.minPrice?.let { min -> result = result.filter { it.price >= min } }
        filter.maxPrice?.let { max -> result = result.filter { it.price <= max } }
        if (filter.inStockOnly) result = result.filter { it.stock > 0 }
        return when (sort) {
            SortOption.PRICE_LOW_HIGH -> result.sortedBy { it.price }
            SortOption.PRICE_HIGH_LOW -> result.sortedByDescending { it.price }
            SortOption.NAME_AZ -> result.sortedBy { it.name }
            SortOption.POPULARITY -> result
        }
    }
    
    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        searchQueryFlow.value = query
    }
    
    fun toggleSearchMode() {
        val newSearchMode = !_uiState.value.isSearchMode
        _uiState.update { 
            it.copy(
                isSearchMode = newSearchMode,
                searchQuery = if (!newSearchMode) "" else it.searchQuery
            )
        }
        
        if (!newSearchMode) {
            searchQueryFlow.value = ""
            loadProducts()
        }
    }
    
    fun refreshProducts() {
        // Refresh both categories and products from API
        loadCategories(forceRefresh = true)
        
        if (_uiState.value.isSearchMode && _uiState.value.searchQuery.isNotEmpty()) {
            searchProducts(_uiState.value.searchQuery)
        } else {
            loadProducts(forceRefresh = true)
        }
    }
    
    fun showAddToCartSuccess() {
        _uiState.update { it.copy(showAddToCartSuccess = true) }
    }
    
    fun hideAddToCartSuccess() {
        _uiState.update { it.copy(showAddToCartSuccess = false) }
    }
    
    fun toggleFavorite(productId: Int) {
        viewModelScope.launch {
            when (val result = favoriteRepository.toggleFavorite(productId)) {
                is Resource.Success -> {
                    val isFavorite = result.data ?: false
                    val message = if (isFavorite) "Added to wishlist ❤️" else "Removed from wishlist"
                    // Update favoriteProductIds set immediately
                    _uiState.update { state ->
                        val newIds = if (isFavorite)
                            state.favoriteProductIds + productId
                        else
                            state.favoriteProductIds - productId
                        state.copy(
                            favoriteMessage = message,
                            favoriteIsError = false,
                            favoriteProductIds = newIds
                        )
                    }
                }
                is Resource.Error -> {
                    android.util.Log.e("ProductListVM", "Favorite error: ${result.message}")
                    _uiState.update { 
                        it.copy(
                            favoriteMessage = result.message ?: "Failed to update wishlist",
                            favoriteIsError = true
                        )
                    }
                }
                else -> {}
            }
        }
    }
    
    fun clearFavoriteMessage() {
        _uiState.update { it.copy(favoriteMessage = null) }
    }
}