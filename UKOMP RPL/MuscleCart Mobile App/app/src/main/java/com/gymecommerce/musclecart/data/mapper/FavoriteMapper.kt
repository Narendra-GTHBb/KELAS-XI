package com.gymecommerce.musclecart.data.mapper

import com.gymecommerce.musclecart.data.remote.dto.FavoriteDto
import com.gymecommerce.musclecart.domain.model.Favorite

object FavoriteMapper {
    fun toFavorite(dto: FavoriteDto, productMapper: ProductMapper): Favorite {
        return Favorite(
            id = dto.id,
            userId = dto.userId,
            productId = dto.productId,
            product = dto.product?.let { productMapper.dtoToDomain(it) }
                ?: throw IllegalStateException("Product data not available"),
            createdAt = dto.createdAt ?: System.currentTimeMillis()
        )
    }
    
    fun toFavoriteList(dtos: List<FavoriteDto>, productMapper: ProductMapper): List<Favorite> {
        return dtos.mapNotNull { dto ->
            try {
                toFavorite(dto, productMapper)
            } catch (e: Exception) {
                null // Skip invalid favorites
            }
        }
    }
}
