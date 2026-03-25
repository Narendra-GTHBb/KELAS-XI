package com.gymecommerce.musclecart.data.remote.api

import com.gymecommerce.musclecart.data.remote.dto.ApiResponse
import com.gymecommerce.musclecart.data.remote.dto.FcmTokenRequest
import com.gymecommerce.musclecart.data.remote.dto.GetUserResponse
import com.gymecommerce.musclecart.data.remote.dto.GoogleLoginRequest
import com.gymecommerce.musclecart.data.remote.dto.LoginRequest
import com.gymecommerce.musclecart.data.remote.dto.LoginResponse
import com.gymecommerce.musclecart.data.remote.dto.RegisterRequest
import com.gymecommerce.musclecart.data.remote.dto.UpdateProfileRequest
import com.gymecommerce.musclecart.data.remote.dto.UserDto
import retrofit2.Response
import retrofit2.http.*

interface AuthApiService {
    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<LoginResponse>>

    @POST("register")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<LoginResponse>>

    @POST("auth/google")
    suspend fun loginWithGoogle(@Body request: GoogleLoginRequest): Response<ApiResponse<LoginResponse>>

    @GET("user")
    suspend fun getUser(): Response<ApiResponse<GetUserResponse>>

    @PUT("user/profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<ApiResponse<GetUserResponse>>

    @POST("logout")
    suspend fun logout(): Response<ApiResponse<Unit>>

    @POST("user/fcm-token")
    suspend fun registerFcmToken(@Body request: FcmTokenRequest): Response<ApiResponse<Unit>>
}
