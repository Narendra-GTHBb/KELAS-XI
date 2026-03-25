package com.gymecommerce.musclecart.data.remote.api

import com.gymecommerce.musclecart.data.remote.dto.ApiResponse
import com.gymecommerce.musclecart.data.remote.dto.FavoriteToggleResponse
import com.gymecommerce.musclecart.data.remote.dto.ProductDto
import retrofit2.Response
import retrofit2.http.*

interface FavoriteApiService {
    @GET("favorites")
    suspend fun getFavorites(): Response<ApiResponse<List<ProductDto>>>
    
    @POST("favorites/{productId}/toggle")
    suspend fun toggleFavorite(@Path("productId") productId: Int): Response<ApiResponse<FavoriteToggleResponse>>
    
    @GET("favorites/{productId}/check")
    suspend fun checkFavorite(@Path("productId") productId: Int): Response<ApiResponse<FavoriteToggleResponse>>
}
