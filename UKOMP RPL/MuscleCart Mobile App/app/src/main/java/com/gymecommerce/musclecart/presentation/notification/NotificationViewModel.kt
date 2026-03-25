package com.gymecommerce.musclecart.presentation.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gymecommerce.musclecart.domain.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NotificationUiState(
    val isLoading: Boolean = false,
    val notifications: List<NotificationRepository.NotificationItem> = emptyList(),
    val unreadCount: Int = 0
)

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val repo: NotificationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            repo.getNotifications().onSuccess { (unread, items) ->
                _uiState.value = NotificationUiState(isLoading = false, notifications = items, unreadCount = unread)
            }.onFailure {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun readAll() {
        viewModelScope.launch {
            repo.readAll()
            _uiState.value = _uiState.value.copy(
                unreadCount = 0,
                notifications = _uiState.value.notifications.map { it.copy(isRead = true) }
            )
        }
    }

    fun read(id: Int) {
        viewModelScope.launch {
            repo.read(id)
            _uiState.value = _uiState.value.copy(
                notifications = _uiState.value.notifications.map {
                    if (it.id == id) it.copy(isRead = true) else it
                },
                unreadCount = maxOf(0, _uiState.value.unreadCount - 1)
            )
        }
    }
}
