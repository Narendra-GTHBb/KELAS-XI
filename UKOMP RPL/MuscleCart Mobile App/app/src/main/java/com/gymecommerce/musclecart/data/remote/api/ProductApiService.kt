package com.gymecommerce.musclecart.data.remote.api

import com.gymecommerce.musclecart.data.remote.dto.ApiResponse
import com.gymecommerce.musclecart.data.remote.dto.ProductDetailData
import com.gymecommerce.musclecart.data.remote.dto.ProductDto
import retrofit2.Response
import retrofit2.http.*

interface ProductApiService {
    @GET("products")
    suspend fun getProducts(
        @Query("search") search: String? = null,
        @Query("category_id") categoryId: Int? = null,
        @Query("featured") featured: Boolean? = null
    ): Response<ApiResponse<List<ProductDto>>>

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id: Int): Response<ApiResponse<ProductDetailData>>

    @POST("products")
    suspend fun createProduct(@Body product: ProductDto): Response<ApiResponse<ProductDto>>

    @PUT("products/{id}")
    suspend fun updateProduct(@Path("id") id: Int, @Body product: ProductDto): Response<ApiResponse<ProductDto>>

    @DELETE("products/{id}")
    suspend fun deleteProduct(@Path("id") id: Int): Response<ApiResponse<Unit>>
}
