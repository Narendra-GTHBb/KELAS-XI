package com.gymecommerce.musclecart.domain.usecase.favorite

import com.gymecommerce.musclecart.domain.repository.FavoriteRepository
import com.gymecommerce.musclecart.domain.util.Resource
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val repository: FavoriteRepository
) {
    suspend operator fun invoke(productId: Int): Resource<Boolean> {
        return repository.toggleFavorite(productId)
    }
}
