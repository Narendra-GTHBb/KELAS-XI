package com.gymecommerce.musclecart.data.remote.dto

import com.google.gson.annotations.SerializedName

data class FavoriteDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("product_id")
    val productId: Int,
    @SerializedName("product")
    val product: ProductDto? = null,
    @SerializedName("created_at")
    val createdAt: Long?,
    @SerializedName("updated_at")
    val updatedAt: Long?
)

data class FavoriteToggleResponse(
    @SerializedName("is_favorite")
    val isFavorite: Boolean
)
