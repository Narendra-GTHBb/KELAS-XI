package com.gymecommerce.musclecart.domain.usecase.product


import com.gymecommerce.musclecart.domain.model.Product
import com.gymecommerce.musclecart.domain.repository.ProductRepository
import com.gymecommerce.musclecart.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SearchProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(
        query: String,
        categoryId: Int? = null
    ): Flow<Resource<List<Product>>> {
        return productRepository.searchProducts(query, categoryId)
    }
}
