package com.gymecommerce.musclecart.domain.usecase.favorite

import com.gymecommerce.musclecart.domain.model.Product
import com.gymecommerce.musclecart.domain.repository.FavoriteRepository
import com.gymecommerce.musclecart.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavoritesUseCase @Inject constructor(
    private val repository: FavoriteRepository
) {
    operator fun invoke(): Flow<Resource<List<Product>>> {
        return repository.getFavorites()
    }
}
