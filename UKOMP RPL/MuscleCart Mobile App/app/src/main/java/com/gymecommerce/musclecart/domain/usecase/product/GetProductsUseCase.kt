package com.gymecommerce.musclecart.domain.usecase.product


import com.gymecommerce.musclecart.domain.model.Product
import com.gymecommerce.musclecart.domain.repository.ProductRepository
import com.gymecommerce.musclecart.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(
        categoryId: Int? = null,
        forceRefresh: Boolean = false
    ): Flow<Resource<List<Product>>> {
        return productRepository.getProducts(categoryId, forceRefresh)
    }
}
