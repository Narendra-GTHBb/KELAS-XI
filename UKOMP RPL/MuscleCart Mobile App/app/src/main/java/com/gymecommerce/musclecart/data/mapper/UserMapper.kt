package com.gymecommerce.musclecart.data.mapper

import com.gymecommerce.musclecart.data.local.entity.UserEntity
import com.gymecommerce.musclecart.domain.model.User
import com.gymecommerce.musclecart.domain.model.UserRole

// ===============================
// Entity -> Domain
// ===============================
fun UserEntity.toDomain(): User {
    return User(
        id = id,
        name = name,
        email = email,
        role = if (isAdmin) UserRole.ADMIN else UserRole.USER,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

// ===============================
// Domain -> Entity
// ===============================
fun User.toEntity(): UserEntity {
    return UserEntity(
        id = id,
        name = name,
        email = email,
        password = "", // password harus di-handle terpisah
        phone = null,  // karena domain User tidak punya phone
        address = null, // karena domain User tidak punya address
        isAdmin = role == UserRole.ADMIN,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

// ===============================
// List Mapper
// ===============================
fun List<UserEntity>.toDomain(): List<User> {
    return map { it.toDomain() }
}

fun List<User>.toEntity(): List<UserEntity> {
    return map { it.toEntity() }
}