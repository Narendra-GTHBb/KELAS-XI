package com.gymecommerce.musclecart.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ApplyVoucherRequest(
    @SerializedName("code")
    val code: String,

    @SerializedName("subtotal")
    val subtotal: Double
)

data class VoucherResultDto(
    @SerializedName("code")
    val code: String,

    @SerializedName("description")
    val description: String?,

    @SerializedName("type")
    val type: String,

    @SerializedName("value")
    val value: Double,

    @SerializedName("discount_amount")
    val discountAmount: Double
)

data class ApplyVoucherResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: VoucherResultDto?
)
