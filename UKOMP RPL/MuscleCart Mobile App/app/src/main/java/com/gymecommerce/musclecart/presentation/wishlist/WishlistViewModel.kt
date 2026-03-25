package com.gymecommerce.musclecart.presentation.wishlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gymecommerce.musclecart.domain.model.Product
import com.gymecommerce.musclecart.domain.util.Resource
import com.gymecommerce.musclecart.domain.repository.FavoriteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WishlistUiState(
    val isLoading: Boolean = false,
    val allProducts: List<Product> = emptyList(),
    val selectedCategory: String = "All Items",
    val error: String? = null
)

@HiltViewModel
class WishlistViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WishlistUiState())
    val uiState: StateFlow<WishlistUiState> = _uiState.asStateFlow()

    // Reactive filtered products — updates immediately when allProducts changes
    val filteredProducts: StateFlow<List<Product>> = _uiState
        .map { state -> applyFilter(state) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private var loadJob: Job? = null

    init {
        loadFavorites()
    }

    fun loadFavorites() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            favoriteRepository.getFavorites().collect { result ->
                when (result) {
        is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            allProducts = result.data ?: emptyList(),
                            error = null
                        )
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                    else -> {}
                }
            }
        }
    }

    fun selectCategory(category: String) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }

    private fun applyFilter(state: WishlistUiState): List<Product> {
        return when (state.selectedCategory) {
            "All Items" -> state.allProducts
            "Supplements" -> state.allProducts.filter {
                it.category?.name?.contains("Supplement", ignoreCase = true) == true ||
                it.category?.name?.contains("Protein", ignoreCase = true) == true ||
                it.category?.name?.contains("Pre-Workout", ignoreCase = true) == true ||
                it.category?.name?.contains("Recovery", ignoreCase = true) == true
            }
            "Apparel" -> state.allProducts.filter {
                it.category?.name?.contains("Apparel", ignoreCase = true) == true ||
                it.category?.name?.contains("Clothing", ignoreCase = true) == true
            }
            "Equip" -> state.allProducts.filter {
                it.category?.name?.contains("Equipment", ignoreCase = true) == true ||
                it.category?.name?.contains("Accessories", ignoreCase = true) == true
            }
            else -> state.allProducts
        }
    }

    fun removeFavorite(productId: Int) {
        // Cancel any in-flight loadFavorites so it cannot overwrite our optimistic state
        loadJob?.cancel()
        // Optimistically remove from UI immediately
        _uiState.value = _uiState.value.copy(
            allProducts = _uiState.value.allProducts.filter { it.id != productId }
        )
        viewModelScope.launch {
            val result = favoriteRepository.toggleFavorite(productId)
            if (result is Resource.Error) {
                // If API call failed, reload to restore correct state
                loadFavorites()
            }
        }
    }
}
