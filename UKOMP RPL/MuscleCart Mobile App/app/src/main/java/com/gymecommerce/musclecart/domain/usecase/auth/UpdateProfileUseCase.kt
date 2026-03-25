package com.gymecommerce.musclecart.domain.usecase.auth

import com.gymecommerce.musclecart.domain.model.NetworkResult
import com.gymecommerce.musclecart.domain.model.User
import com.gymecommerce.musclecart.domain.repository.AuthRepository
import javax.inject.Inject

class UpdateProfileUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        name: String,
        email: String,
        phone: String? = null,
        address: String? = null,
        city: String? = null,
        postalCode: String? = null,
        provinceId: String? = null,
        cityId: String? = null,
        password: String? = null
    ): NetworkResult<User> {
        if (name.isBlank()) return NetworkResult.Error("Name cannot be empty")
        if (email.isBlank()) return NetworkResult.Error("Email cannot be empty")
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return NetworkResult.Error("Invalid email address")
        }
        return authRepository.updateProfile(
            name = name.trim(),
            email = email.trim(),
            phone = phone?.trim()?.takeIf { it.isNotBlank() },
            address = address?.trim()?.takeIf { it.isNotBlank() },
            city = city?.trim()?.takeIf { it.isNotBlank() },
            postalCode = postalCode?.trim()?.takeIf { it.isNotBlank() },
            provinceId = provinceId?.trim()?.takeIf { it.isNotBlank() },
            cityId = cityId?.trim()?.takeIf { it.isNotBlank() },
            password = password?.trim()?.takeIf { it.isNotBlank() }
        )
    }
}
