package com.gymecommerce.musclecart.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Request body for POST /orders
 * Backend reads cart server-side, so we only need checkout metadata.
 */
data class CreateOrderRequest(
    @SerializedName("shipping_address")
    val shippingAddress: String,

    @SerializedName("payment_method")
    val paymentMethod: String = "cod",

    @SerializedName("shipping_cost")
    val shippingCost: Int = 0,

    @SerializedName("courier")
    val courier: String? = null,

    @SerializedName("courier_service")
    val courierService: String? = null,

    @SerializedName("destination_city_id")
    val destinationCityId: String? = null,

    @SerializedName("notes")
    val notes: String? = null,

    @SerializedName("voucher_code")
    val voucherCode: String? = null,

    @SerializedName("points_used")
    val pointsUsed: Int = 0
)
