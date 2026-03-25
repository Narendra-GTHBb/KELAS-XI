package com.gymecommerce.musclecart.data.remote.api

import com.gymecommerce.musclecart.data.remote.dto.ApiResponse
import com.gymecommerce.musclecart.data.remote.dto.CartDto
import com.gymecommerce.musclecart.data.remote.dto.CartItemDto
import retrofit2.Response
import retrofit2.http.*

interface CartApiService {
    @GET("cart")
    suspend fun getCart(): Response<ApiResponse<CartDto>>

    @POST("cart/add")
    suspend fun addToCart(@Body request: AddToCartRequest): Response<ApiResponse<CartDto>>

    @PUT("cart/update/{id}")
    suspend fun updateCartItem(@Path("id") id: Int, @Body request: UpdateCartRequest): Response<ApiResponse<CartDto>>

    @DELETE("cart/remove/{id}")
    suspend fun removeFromCart(@Path("id") id: Int): Response<ApiResponse<CartDto>>

    @DELETE("cart/clear")
    suspend fun clearCart(): Response<ApiResponse<Unit>>
}

data class AddToCartRequest(
    val product_id: Int,
    val quantity: Int = 1
)

data class UpdateCartRequest(
    val quantity: Int
)
