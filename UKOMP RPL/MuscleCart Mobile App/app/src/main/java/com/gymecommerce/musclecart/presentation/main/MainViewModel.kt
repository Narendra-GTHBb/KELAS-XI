package com.gymecommerce.musclecart.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gymecommerce.musclecart.domain.usecase.cart.GetCartItemsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getCartItemsUseCase: GetCartItemsUseCase
) : ViewModel() {

    private val _cartItemCount = MutableStateFlow(0)
    val cartItemCount: StateFlow<Int> = _cartItemCount.asStateFlow()

    init {
        observeCartCount()
    }

    private fun observeCartCount() {
        viewModelScope.launch {
            getCartItemsUseCase.getCartItemCount().collect { count ->
                _cartItemCount.value = count
            }
        }
    }
}
