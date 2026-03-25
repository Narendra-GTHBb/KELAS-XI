package com.gymecommerce.musclecart.data.repository

import com.gymecommerce.musclecart.data.mapper.ProductMapper
import com.gymecommerce.musclecart.data.remote.api.FavoriteApiService
import com.gymecommerce.musclecart.domain.model.Product
import com.gymecommerce.musclecart.domain.repository.FavoriteRepository
import com.gymecommerce.musclecart.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteRepositoryImpl @Inject constructor(
    private val favoriteApiService: FavoriteApiService,
    private val productMapper: ProductMapper
) : FavoriteRepository {

    override fun getFavorites(): Flow<Resource<List<Product>>> = flow {
        emit(Resource.Loading())
        
        try {
            val response = favoriteApiService.getFavorites()
            
            if (response.isSuccessful) {
                val apiResponse = response.body()
                
                if (apiResponse?.status == "success" && apiResponse.data != null) {
                    val products = apiResponse.data.map { productMapper.dtoToDomain(it) }
                    emit(Resource.Success(products))
                } else {
                    emit(Resource.Error(apiResponse?.message ?: "Failed to load favorites"))
                }
            } else {
                emit(Resource.Error("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown error occurred"))
        }
    }

    override suspend fun toggleFavorite(productId: Int): Resource<Boolean> {
        return try {
            val response = favoriteApiService.toggleFavorite(productId)
            
            if (response.isSuccessful) {
                val apiResponse = response.body()
                
                if (apiResponse?.status == "success" && apiResponse.data != null) {
                    Resource.Success(apiResponse.data.isFavorite)
                } else {
                    Resource.Error(apiResponse?.message ?: "Failed to toggle favorite")
                }
            } else {
                Resource.Error("HTTP ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error occurred")
        }
    }

    override suspend fun checkFavorite(productId: Int): Resource<Boolean> {
        return try {
            val response = favoriteApiService.checkFavorite(productId)
            
            if (response.isSuccessful) {
                val apiResponse = response.body()
                
                if (apiResponse?.status == "success" && apiResponse.data != null) {
                    Resource.Success(apiResponse.data.isFavorite)
                } else {
                    Resource.Success(false)
                }
            } else {
                Resource.Success(false)
            }
        } catch (e: Exception) {
            Resource.Success(false)
        }
    }
}
