package com.gymecommerce.musclecart.data.remote.api

import com.gymecommerce.musclecart.data.remote.dto.ApiResponse
import com.gymecommerce.musclecart.data.remote.dto.CategoryDto
import com.gymecommerce.musclecart.data.remote.dto.ProductDto
import retrofit2.Response
import retrofit2.http.*

interface CategoryApiService {
    @GET("categories")
    suspend fun getCategories(): Response<ApiResponse<List<CategoryDto>>>

    @GET("categories/{id}/products")
    suspend fun getCategoryProducts(@Path("id") id: Int): Response<ApiResponse<List<ProductDto>>>

    @POST("categories")
    suspend fun createCategory(@Body category: CategoryDto): Response<ApiResponse<CategoryDto>>

    @PUT("categories/{id}")
    suspend fun updateCategory(@Path("id") id: Int, @Body category: CategoryDto): Response<ApiResponse<CategoryDto>>

    @DELETE("categories/{id}")
    suspend fun deleteCategory(@Path("id") id: Int): Response<ApiResponse<Unit>>
}
