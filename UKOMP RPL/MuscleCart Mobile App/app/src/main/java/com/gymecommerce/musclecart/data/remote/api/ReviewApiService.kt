package com.gymecommerce.musclecart.data.remote.api

import com.gymecommerce.musclecart.data.remote.dto.CheckReviewResponse
import com.gymecommerce.musclecart.data.remote.dto.ProductReviewsResponse
import com.gymecommerce.musclecart.data.remote.dto.SubmitReviewRequest
import com.gymecommerce.musclecart.data.remote.dto.SubmitReviewResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ReviewApiService {
    @GET("products/{productId}/reviews")
    suspend fun getProductReviews(
        @Path("productId") productId: Int
    ): Response<ProductReviewsResponse>

    @POST("reviews")
    suspend fun submitReview(
        @Body request: SubmitReviewRequest
    ): Response<SubmitReviewResponse>

    @GET("reviews/check")
    suspend fun checkReview(
        @Query("product_id") productId: Int,
        @Query("order_id") orderId: Int
    ): Response<CheckReviewResponse>
}
