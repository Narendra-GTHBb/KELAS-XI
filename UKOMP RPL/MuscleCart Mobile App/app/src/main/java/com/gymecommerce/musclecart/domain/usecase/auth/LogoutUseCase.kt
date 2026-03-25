package com.gymecommerce.musclecart.domain.usecase.auth

import com.gymecommerce.musclecart.domain.model.NetworkResult
import com.gymecommerce.musclecart.domain.repository.AuthRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): NetworkResult<Unit> {
        return authRepository.logout()
    }
}