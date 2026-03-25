package com.gymecommerce.musclecart.domain.usecase.product

import com.gymecommerce.musclecart.domain.repository.ProductRepository
import com.gymecommerce.musclecart.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SyncProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    operator fun invoke(): Flow<Resource<Unit>> = flow {
        val result = productRepository.syncProducts()
        when (result) {
            is com.gymecommerce.musclecart.domain.model.Result.Success -> emit(Resource.Success(Unit))
            is com.gymecommerce.musclecart.domain.model.Result.Error -> emit(Resource.Error(result.message ?: "Unknown error"))
            is com.gymecommerce.musclecart.domain.model.Result.Loading -> emit(Resource.Loading())
        }
    }
}