package com.gymecommerce.musclecart.data.remote.api

import com.gymecommerce.musclecart.data.remote.dto.ApiResponse
import com.gymecommerce.musclecart.data.remote.dto.CreateOrderRequest
import com.gymecommerce.musclecart.data.remote.dto.OrderDto
import retrofit2.Response
import retrofit2.http.*

interface OrderApiService {
    @GET("orders")
    suspend fun getOrders(): Response<ApiResponse<List<OrderDto>>>

    @GET("orders/{id}")
    suspend fun getOrderById(@Path("id") id: Int): Response<ApiResponse<OrderDto>>

    @POST("orders")
    suspend fun createOrder(@Body request: CreateOrderRequest): Response<ApiResponse<OrderDto>>

    @PUT("orders/{id}")
    suspend fun updateOrder(@Path("id") id: Int, @Body order: OrderDto): Response<ApiResponse<OrderDto>>

    @PUT("orders/{id}/status")
    suspend fun updateOrderStatus(@Path("id") id: Int, @Query("status") status: String): Response<ApiResponse<OrderDto>>

    @DELETE("orders/{id}")
    suspend fun deleteOrder(@Path("id") id: Int): Response<ApiResponse<Unit>>

    @PUT("orders/{id}/confirm-received")
    suspend fun confirmReceived(@Path("id") id: Int): Response<ApiResponse<OrderDto>>

    @PUT("orders/{id}/cancel")
    suspend fun cancelOrder(@Path("id") id: Int): Response<ApiResponse<OrderDto>>
}
