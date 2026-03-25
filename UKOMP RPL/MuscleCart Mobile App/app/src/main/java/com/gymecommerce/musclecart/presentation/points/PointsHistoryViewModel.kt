package com.gymecommerce.musclecart.presentation.points

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gymecommerce.musclecart.domain.repository.PointsHistoryItem
import com.gymecommerce.musclecart.domain.repository.PointsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PointsHistoryUiState(
    val isLoading: Boolean = false,
    val balance: Int = 0,
    val history: List<PointsHistoryItem> = emptyList()
)

@HiltViewModel
class PointsHistoryViewModel @Inject constructor(
    private val repo: PointsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PointsHistoryUiState())
    val uiState: StateFlow<PointsHistoryUiState> = _uiState.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            repo.getPoints().onSuccess { (balance, history) ->
                _uiState.value = PointsHistoryUiState(isLoading = false, balance = balance, history = history)
            }.onError {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }
}
