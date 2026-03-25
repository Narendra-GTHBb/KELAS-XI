package com.gymecommerce.musclecart.domain.model

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val phone: String? = null,
    val address: String? = null,
    val city: String? = null,
    val postalCode: String? = null,
    val provinceId: String? = null,
    val cityId: String? = null,
    val role: UserRole,
    val createdAt: Long,
    val updatedAt: Long,
    val points: Int = 0
) {
    fun isAdmin(): Boolean = role == UserRole.ADMIN
    fun isUser(): Boolean = role == UserRole.USER

    val hasCompleteAddress: Boolean
        get() = !address.isNullOrBlank() && !cityId.isNullOrBlank()

    val formattedAddress: String
        get() = buildString {
            if (!address.isNullOrBlank()) append(address)
            if (!city.isNullOrBlank()) append(if (isEmpty()) city else ", $city")
            if (!postalCode.isNullOrBlank()) append(" $postalCode")
        }
}

enum class UserRole {
    USER, ADMIN;
    
    companion object {
        fun fromString(role: String): UserRole {
            return when (role.lowercase()) {
                "admin" -> ADMIN
                "user", "customer" -> USER
                else -> USER
            }
        }
    }
    
    override fun toString(): String {
        return name.lowercase()
    }
}