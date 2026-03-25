package com.gymecommerce.musclecart.domain.usecase.product

import com.gymecommerce.musclecart.domain.model.Category
import com.gymecommerce.musclecart.domain.repository.ProductRepository
import com.gymecommerce.musclecart.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(forceRefresh: Boolean = false): Flow<Resource<List<Category>>> {
        return productRepository.getCategories(forceRefresh)
    }
}