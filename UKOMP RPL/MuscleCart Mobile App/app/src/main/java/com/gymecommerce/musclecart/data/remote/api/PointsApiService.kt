package com.gymecommerce.musclecart.data.remote.api

import com.gymecommerce.musclecart.data.remote.dto.UserPointsResponse
import retrofit2.Response
import retrofit2.http.GET

interface PointsApiService {
    @GET("points")
    suspend fun getPoints(): Response<UserPointsResponse>
}
