package com.gymecommerce.musclecart.data.mapper

import android.util.Log
import com.gymecommerce.musclecart.data.local.entity.ProductEntity
import com.gymecommerce.musclecart.data.local.entity.CategoryEntity
import com.gymecommerce.musclecart.data.remote.dto.ProductDto
import com.gymecommerce.musclecart.domain.model.Product
import com.gymecommerce.musclecart.domain.model.Category
import com.gymecommerce.musclecart.util.DateUtils
import com.gymecommerce.musclecart.util.ServerConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductMapper @Inject constructor() {

    companion object {
        private const val TAG = "ProductMapper"
    }


    // ===== PRODUCT =====
    fun entityToDomain(entity: ProductEntity, category: Category? = null): Product {
        return Product(
            id = entity.id,
            name = entity.name,
            price = entity.price,
            stock = entity.stockQuantity,
            description = entity.description,
            imageUrl = entity.imageUrl,
            categoryId = entity.categoryId,
            category = category,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            lastSyncTime = entity.updatedAt,
            avgRating = entity.avgRating,
            totalReviews = entity.totalReviews
        )
    }

    fun domainToEntity(product: Product): ProductEntity {
        return ProductEntity(
            id = product.id,
            name = product.name,
            description = product.description,
            price = product.price,
            imageUrl = product.imageUrl,
            categoryId = product.categoryId,
            stockQuantity = product.stock,
            isActive = product.stock > 0,
            avgRating = product.avgRating,
            totalReviews = product.totalReviews,
            createdAt = product.createdAt,
            updatedAt = product.updatedAt
        )
    }

    fun domainToDto(product: Product): ProductDto {
        return ProductDto(
            id = product.id,
            name = product.name,
            price = product.price,
            stock = product.stock,
            description = product.description,
            imageUrl = product.imageUrl,
            categoryId = product.categoryId,
            createdAt = DateUtils.formatTimestampToIso8601(product.createdAt),
            updatedAt = DateUtils.formatTimestampToIso8601(product.updatedAt)
        )
    }

    fun dtoToDomain(dto: ProductDto): Product {
        // Use full_image_url first (from backend API), then fallback to imageUrl or image
        val rawImageUrl = when {
            !dto.fullImageUrl.isNullOrEmpty() -> dto.fullImageUrl
            !dto.imageUrl.isNullOrEmpty() -> dto.imageUrl
            !dto.image.isNullOrEmpty() -> dto.image
            else -> null
        }
        
        Log.d(TAG, "Product ID ${dto.id}: rawImageUrl = $rawImageUrl")
        
        // Convert to full URL for Android
        val finalImageUrl = when {
            rawImageUrl.isNullOrEmpty() -> {
                // Fallback to avatar placeholder if no image
                val initials = dto.name.split(" ")
                    .take(2)
                    .mapNotNull { it.firstOrNull()?.toString() }
                    .joinToString("")
                    .ifEmpty { "MC" }
                val encodedInitials = java.net.URLEncoder.encode(initials, "UTF-8")
                val fallbackUrl = "https://ui-avatars.com/api/?name=$encodedInitials&background=1976D2&color=fff&size=400&bold=true&format=png"
                Log.d(TAG, "  -> Using fallback: $fallbackUrl")
                fallbackUrl
            }
            rawImageUrl.startsWith("http://") || rawImageUrl.startsWith("https://") -> {
                Log.d(TAG, "  -> Already full URL (from backend): $rawImageUrl")
                val fixedUrl = ServerConfig.fixImageUrl(rawImageUrl)
                Log.d(TAG, "  -> Fixed URL: $fixedUrl")
                fixedUrl
            }
            else -> {
                // Convert relative path to full URL
                val filename = rawImageUrl.substringAfterLast("/")
                val backendUrl = "${ServerConfig.SERVER_URL}/storage/products/$filename"
                Log.d(TAG, "  -> Using backend storage URL: $backendUrl")
                backendUrl
            }
        }

        
        return Product(
            id = dto.id,
            name = dto.name,
            price = dto.price,
            stock = if (dto.stockQuantity > 0) dto.stockQuantity else dto.stock,
            description = dto.description,
            imageUrl = finalImageUrl,
            categoryId = dto.categoryId,
            category = null,
            createdAt = DateUtils.parseIso8601ToTimestamp(dto.createdAt),
            updatedAt = DateUtils.parseIso8601ToTimestamp(dto.updatedAt),
            lastSyncTime = DateUtils.parseIso8601ToTimestamp(dto.updatedAt),
            avgRating = dto.avgRating,
            totalReviews = dto.totalReviews
        )
    }

    // ===== CATEGORY =====
    fun categoryEntityToDomain(entity: CategoryEntity): Category {
        return Category(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            imageUrl = entity.imageUrl,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
}