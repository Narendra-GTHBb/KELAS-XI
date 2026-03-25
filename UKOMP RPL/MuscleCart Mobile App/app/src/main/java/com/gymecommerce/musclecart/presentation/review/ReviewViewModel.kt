package com.gymecommerce.musclecart.presentation.review

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gymecommerce.musclecart.domain.model.Result
import com.gymecommerce.musclecart.domain.model.Review
import com.gymecommerce.musclecart.domain.repository.ReviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReviewUiState(
    val productId: Int = 0,
    val orderId: Int = 0,
    val productName: String = "",
    val selectedRating: Int = 0,
    val comment: String = "",
    val reviews: List<Review> = emptyList(),
    val avgRating: Double = 0.0,
    val totalReviews: Int = 0,
    val hasReviewed: Boolean = false,
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val submitSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewUiState())
    val uiState: StateFlow<ReviewUiState> = _uiState.asStateFlow()

    fun init(productId: Int, orderId: Int, productName: String) {
        _uiState.value = _uiState.value.copy(
            productId = productId,
            orderId = orderId,
            productName = productName
        )
        loadReviews(productId)
        checkAlreadyReviewed(productId, orderId)
    }

    private fun loadReviews(productId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val result = reviewRepository.getProductReviews(productId)) {
                is Result.Success -> _uiState.value = _uiState.value.copy(
                    reviews = result.data,
                    avgRating = if (result.data.isEmpty()) 0.0
                                else result.data.map { it.rating }.average(),
                    totalReviews = result.data.size,
                    isLoading = false
                )
                is Result.Error -> _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = result.message
                )
                else -> _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    private fun checkAlreadyReviewed(productId: Int, orderId: Int) {
        viewModelScope.launch {
            val hasReviewed = reviewRepository.hasReviewed(productId, orderId)
            _uiState.value = _uiState.value.copy(hasReviewed = hasReviewed)
        }
    }

    fun onRatingSelected(rating: Int) {
        _uiState.value = _uiState.value.copy(selectedRating = rating, error = null)
    }

    fun onCommentChanged(comment: String) {
        _uiState.value = _uiState.value.copy(comment = comment)
    }

    fun submitReview() {
        val state = _uiState.value
        if (state.selectedRating == 0) {
            _uiState.value = state.copy(error = "Pilih bintang terlebih dahulu")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmitting = true, error = null)
            val result = reviewRepository.submitReview(
                productId = state.productId,
                orderId = state.orderId,
                rating = state.selectedRating,
                comment = state.comment.ifBlank { null }
            )
            when (result) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isSubmitting = false,
                        submitSuccess = true,
                        hasReviewed = true
                    )
                    loadReviews(state.productId)
                }
                is Result.Error -> _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    error = result.message
                )
                else -> _uiState.value = _uiState.value.copy(isSubmitting = false)
            }
        }
    }

    fun clearError() { _uiState.value = _uiState.value.copy(error = null) }
    fun clearSuccess() { _uiState.value = _uiState.value.copy(submitSuccess = false) }
}
