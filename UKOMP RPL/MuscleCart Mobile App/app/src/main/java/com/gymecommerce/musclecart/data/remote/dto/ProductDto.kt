package com.gymecommerce.musclecart.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ProductDto(
    @SerializedName("id") 
    val id: Int = 0,
    
    @SerializedName("name") 
    val name: String = "",
    
    @SerializedName("description") 
    val description: String = "",
    
    @SerializedName("price") 
    val price: Double = 0.0,
    
    @SerializedName("stock_quantity") 
    val stockQuantity: Int = 0,
    
    @SerializedName("stock") // For backward compatibility
    val stock: Int = 0,
    
    @SerializedName("image_url") 
    val imageUrl: String? = null,
    
    @SerializedName("image") // For backward compatibility
    val image: String? = null,
    
    @SerializedName("category_id") 
    val categoryId: Int = 0,
    
    @SerializedName("category")
    val category: CategoryDto? = null,
    
    @SerializedName("brand")
    val brand: String? = null,
    
    @SerializedName("weight")
    val weight: Double? = null,
    
    @SerializedName("specifications")
    val specifications: String? = null,
    
    @SerializedName("is_featured")
    val isFeatured: Boolean = false,
    
    @SerializedName("is_active")
    val isActive: Boolean = true,
    
    @SerializedName("full_image_url") 
    val fullImageUrl: String? = null,
    
    @SerializedName("created_at") 
    val createdAt: String? = null,
    
    @SerializedName("updated_at") 
    val updatedAt: String? = null,

    @SerializedName("avg_rating")
    val avgRating: Double = 0.0,

    @SerializedName("total_reviews")
    val totalReviews: Int = 0
)

// Wrapper untuk response GET /products/{id}
// Backend returns: { "status":"success", "data": { "product":{...}, "related_products":[...], "reviews":[...] } }
data class ProductDetailData(
    @SerializedName("product")
    val product: ProductDto? = null,

    @SerializedName("related_products")
    val relatedProducts: List<ProductDto> = emptyList(),

    @SerializedName("reviews")
    val reviews: List<ReviewDto> = emptyList()
)
