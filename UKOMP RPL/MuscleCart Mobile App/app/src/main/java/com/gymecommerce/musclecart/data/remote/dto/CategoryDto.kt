package com.gymecommerce.musclecart.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CategoryDto(
    @SerializedName("id") 
    val id: Int = 0,
    
    @SerializedName("name") 
    val name: String = "",
    
    @SerializedName("description") 
    val description: String = "",
    
    @SerializedName("image_url") 
    val imageUrl: String? = null,
    
    @SerializedName("is_active")
    val isActive: Boolean = true,
    
    @SerializedName("created_at") 
    val createdAt: String? = null,
    
    @SerializedName("updated_at") 
    val updatedAt: String? = null
)