package com.gymecommerce.musclecart.domain.usecase.product

import com.gymecommerce.musclecart.domain.model.Product
import com.gymecommerce.musclecart.domain.repository.ProductRepository
import com.gymecommerce.musclecart.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

import kotlinx.coroutines.flow.flow

class GetProductByIdUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(
        productId: Int,
        forceRefresh: Boolean = false
    ): Flow<Resource<Product?>> = flow {
        emit(Resource.Loading())
        val result = productRepository.getProductById(productId)
        when (result) {
            is com.gymecommerce.musclecart.domain.model.Result.Success -> emit(Resource.Success(result.data))
            is com.gymecommerce.musclecart.domain.model.Result.Error -> emit(Resource.Error(result.message ?: "Unknown error"))
            is com.gymecommerce.musclecart.domain.model.Result.Loading -> emit(Resource.Loading())
        }
    }
}
