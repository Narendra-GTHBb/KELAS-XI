package com.gymecommerce.musclecart.data.repository

import com.gymecommerce.musclecart.data.remote.api.ReviewApiService
import com.gymecommerce.musclecart.data.remote.dto.SubmitReviewRequest
import com.gymecommerce.musclecart.domain.model.Result
import com.gymecommerce.musclecart.domain.model.Review
import com.gymecommerce.musclecart.domain.repository.ReviewRepository
import com.gymecommerce.musclecart.util.DateUtils
import javax.inject.Inject

class ReviewRepositoryImpl @Inject constructor(
    private val reviewApiService: ReviewApiService
) : ReviewRepository {

    override suspend fun getProductReviews(productId: Int): Result<List<Review>> {
        return try {
            val response = reviewApiService.getProductReviews(productId)
            if (response.isSuccessful && response.body()?.status == "success") {
                val reviews = response.body()!!.data.map { dto ->
                    Review(
                        id = dto.id,
                        rating = dto.rating,
                        comment = dto.comment,
                        userName = dto.userName,
                        createdAt = DateUtils.parseIso8601ToTimestamp(dto.createdAt)
                    )
                }
                Result.Success(reviews)
            } else {
                Result.Error("Gagal memuat ulasan")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Gagal memuat ulasan")
        }
    }

    override suspend fun submitReview(
        productId: Int,
        orderId: Int,
        rating: Int,
        comment: String?
    ): Result<String> {
        return try {
            val response = reviewApiService.submitReview(
                SubmitReviewRequest(
                    productId = productId,
                    orderId = orderId,
                    rating = rating,
                    comment = comment
                )
            )
            if (response.isSuccessful && response.body()?.status == "success") {
                Result.Success(response.body()!!.message)
            } else {
                val errorMsg = response.body()?.message ?: "Gagal mengirim ulasan"
                Result.Error(errorMsg)
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Gagal mengirim ulasan")
        }
    }

    override suspend fun hasReviewed(productId: Int, orderId: Int): Boolean {
        return try {
            val response = reviewApiService.checkReview(productId, orderId)
            response.isSuccessful && response.body()?.hasReviewed == true
        } catch (e: Exception) {
            false
        }
    }
}
