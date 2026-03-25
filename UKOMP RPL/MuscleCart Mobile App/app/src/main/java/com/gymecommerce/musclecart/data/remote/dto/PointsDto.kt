package com.gymecommerce.musclecart.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PointsHistoryItemDto(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("points")
    val points: Int = 0,
    @SerializedName("type")
    val type: String = "earn",   // "earn" or "redeem"
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("order_id")
    val orderId: Int? = null,
    @SerializedName("order_number")
    val orderNumber: String? = null,
    @SerializedName("created_at")
    val createdAt: String? = null
)

data class UserPointsDataDto(
    @SerializedName("balance")
    val balance: Int = 0,
    @SerializedName("history")
    val history: List<PointsHistoryItemDto> = emptyList()
)

data class UserPointsResponse(
    @SerializedName("status")
    val status: String = "",
    @SerializedName("data")
    val data: UserPointsDataDto? = null
)
