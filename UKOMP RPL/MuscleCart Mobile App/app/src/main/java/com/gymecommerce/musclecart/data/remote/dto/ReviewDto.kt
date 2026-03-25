package com.gymecommerce.musclecart.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ReviewDto(
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("rating")
    val rating: Int = 0,

    @SerializedName("comment")
    val comment: String? = null,

    @SerializedName("user_name")
    val userName: String = "",

    @SerializedName("created_at")
    val createdAt: String? = null
)

data class ProductReviewsResponse(
    @SerializedName("status")
    val status: String = "",

    @SerializedName("avg_rating")
    val avgRating: Double = 0.0,

    @SerializedName("total_reviews")
    val totalReviews: Int = 0,

    @SerializedName("data")
    val data: List<ReviewDto> = emptyList()
)

data class SubmitReviewRequest(
    @SerializedName("product_id")
    val productId: Int,

    @SerializedName("order_id")
    val orderId: Int,

    @SerializedName("rating")
    val rating: Int,

    @SerializedName("comment")
    val comment: String? = null
)

data class SubmitReviewResponse(
    @SerializedName("status")
    val status: String = "",

    @SerializedName("message")
    val message: String = "",

    @SerializedName("data")
    val data: ReviewDto? = null
)

data class CheckReviewResponse(
    @SerializedName("status")
    val status: String = "",

    @SerializedName("has_reviewed")
    val hasReviewed: Boolean = false
)
