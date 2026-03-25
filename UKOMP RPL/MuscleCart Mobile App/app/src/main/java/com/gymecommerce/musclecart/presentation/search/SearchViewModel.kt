package com.gymecommerce.musclecart.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gymecommerce.musclecart.domain.model.Product
import com.gymecommerce.musclecart.domain.usecase.product.SearchProductsUseCase
import com.gymecommerce.musclecart.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val query: String = "",
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val hasSearched: Boolean = false
)

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchProductsUseCase: SearchProductsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val queryFlow = MutableStateFlow("")

    init {
        queryFlow
            .debounce(400)
            .distinctUntilChanged()
            .onEach { q ->
                if (q.isBlank()) {
                    _uiState.update {
                        it.copy(
                            products = emptyList(),
                            isLoading = false,
                            error = null,
                            hasSearched = false
                        )
                    }
                } else {
                    performSearch(q)
                }
            }
            .launchIn(viewModelScope)
    }

    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query) }
        queryFlow.value = query
    }

    private fun performSearch(query: String) {
        viewModelScope.launch {
            searchProductsUseCase(query).collect { resource ->
                when (resource) {
                    is Resource.Loading -> _uiState.update {
                        it.copy(isLoading = true, error = null)
                    }
                    is Resource.Success -> _uiState.update {
                        it.copy(
                            products = resource.data ?: emptyList(),
                            isLoading = false,
                            error = null,
                            hasSearched = true
                        )
                    }
                    is Resource.Error -> _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = resource.message,
                            hasSearched = true
                        )
                    }
                }
            }
        }
    }
}
