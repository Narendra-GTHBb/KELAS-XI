package com.gymecommerce.musclecart.domain.repository

import com.gymecommerce.musclecart.domain.model.Result
import com.gymecommerce.musclecart.domain.model.Review

interface ReviewRepository {
    suspend fun getProductReviews(productId: Int): Result<List<Review>>
    suspend fun submitReview(productId: Int, orderId: Int, rating: Int, comment: String?): Result<String>
    suspend fun hasReviewed(productId: Int, orderId: Int): Boolean
}
