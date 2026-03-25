package com.gymecommerce.musclecart.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Generic API Response wrapper for Laravel API responses
 */
data class ApiResponse<T>(
    @SerializedName("status")
    val status: String,
    
    @SerializedName("data")
    val data: T? = null,
    
    @SerializedName("message")
    val message: String? = null,
    
    @SerializedName("errors")
    val errors: Map<String, List<String>>? = null,
    
    @SerializedName("pagination")
    val pagination: PaginationDto? = null
)

/**
 * Paginated response from Laravel
 */
data class PaginatedResponse<T>(
    @SerializedName("data")
    val data: List<T>,
    
    @SerializedName("current_page")
    val currentPage: Int,
    
    @SerializedName("last_page")
    val lastPage: Int,
    
    @SerializedName("per_page")
    val perPage: Int,
    
    @SerializedName("total")
    val total: Int,
    
    @SerializedName("from")
    val from: Int?,
    
    @SerializedName("to")
    val to: Int?
)

/**
 * Pagination data from Laravel API response
 */
data class PaginationDto(
    @SerializedName("current_page")
    val current_page: Int,
    
    @SerializedName("last_page")
    val last_page: Int,
    
    @SerializedName("per_page")
    val per_page: Int,
    
    @SerializedName("total")
    val total: Int
)
