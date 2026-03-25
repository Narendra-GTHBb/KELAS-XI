package com.gymecommerce.musclecart.data.remote.dto

data class ProvinceDto(
    val province_id: String,
    val province: String
)

data class CityDto(
    val city_id: String,
    val province_id: String,
    val province: String,
    val type: String,
    val city_name: String,
    val postal_code: String
)

data class CourierServiceDto(
    val courier: String,
    val service: String,
    val description: String,
    val cost: Int,
    val etd: String
)

data class ShippingListResponse<T>(
    val status: String,
    val data: List<T>
)

data class PostalCodeResponse(
    val status: String,
    val postal_code: String
)
