package com.gymecommerce.musclecart.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String
)

data class RegisterRequest(
    @SerializedName("name")
    val name: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("password_confirmation")
    val passwordConfirmation: String,
    @SerializedName("phone")
    val phone: String? = null,
    @SerializedName("address")
    val address: String? = null
)

data class LoginResponse(
    @SerializedName("user")
    val user: UserDto,
    @SerializedName("token")
    val token: String
)

// Wrapper for GET /user and PUT /user/profile responses (data.user)
data class GetUserResponse(
    @SerializedName("user")
    val user: UserDto
)

data class UpdateProfileRequest(
    @SerializedName("name")
    val name: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("phone")
    val phone: String? = null,
    @SerializedName("address")
    val address: String? = null,
    @SerializedName("city")
    val city: String? = null,
    @SerializedName("postal_code")
    val postalCode: String? = null,
    @SerializedName("province_id")
    val provinceId: String? = null,
    @SerializedName("city_id")
    val cityId: String? = null,
    @SerializedName("password")
    val password: String? = null,
    @SerializedName("password_confirmation")
    val passwordConfirmation: String? = null
)

data class UserDto(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("email")
    val email: String? = null,
    @SerializedName("phone")
    val phone: String? = null,
    @SerializedName("address")
    val address: String? = null,
    @SerializedName("city")
    val city: String? = null,
    @SerializedName("postal_code")
    val postalCode: String? = null,
    @SerializedName("province_id")
    val provinceId: String? = null,
    @SerializedName("city_id")
    val cityId: String? = null,
    @SerializedName("avatar")
    val avatar: String? = null,
    @SerializedName("role")
    val role: String? = null,
    @SerializedName("is_active")
    val isActive: Boolean = true,
    @SerializedName("points")
    val points: Int = 0
)

data class GoogleLoginRequest(
    @SerializedName("id_token")
    val idToken: String
)

data class FcmTokenRequest(
    @SerializedName("fcm_token")
    val fcmToken: String
)
