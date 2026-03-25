package com.gymecommerce.musclecart.data.remote.api

import com.gymecommerce.musclecart.data.remote.dto.ApiResponse
import com.gymecommerce.musclecart.data.remote.dto.NotificationsResponse
import retrofit2.Response
import retrofit2.http.*

interface NotificationApiService {
    @GET("notifications")
    suspend fun getNotifications(): Response<NotificationsResponse>

    @GET("notifications/unread-count")
    suspend fun getUnreadCount(): Response<ApiResponse<Map<String, Int>>>

    @POST("notifications/read-all")
    suspend fun readAll(): Response<ApiResponse<Unit>>

    @PUT("notifications/{id}/read")
    suspend fun read(@Path("id") id: Int): Response<ApiResponse<Unit>>
}
