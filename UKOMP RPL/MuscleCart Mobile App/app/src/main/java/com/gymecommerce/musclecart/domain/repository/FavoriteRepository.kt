package com.gymecommerce.musclecart.domain.repository

import com.gymecommerce.musclecart.domain.model.Product
import com.gymecommerce.musclecart.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun getFavorites(): Flow<Resource<List<Product>>>
    suspend fun toggleFavorite(productId: Int): Resource<Boolean>
    suspend fun checkFavorite(productId: Int): Resource<Boolean>
}
