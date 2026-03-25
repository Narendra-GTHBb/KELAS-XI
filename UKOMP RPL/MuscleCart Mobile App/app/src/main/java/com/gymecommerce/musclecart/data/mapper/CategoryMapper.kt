package com.gymecommerce.musclecart.data.mapper

import com.gymecommerce.musclecart.data.local.entity.CategoryEntity
import com.gymecommerce.musclecart.data.remote.dto.CategoryDto
import com.gymecommerce.musclecart.domain.model.Category
import com.gymecommerce.musclecart.util.DateUtils
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryMapper @Inject constructor() {

    fun entityToDomain(entity: CategoryEntity): Category {
        return Category(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            imageUrl = entity.imageUrl,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    fun domainToEntity(category: Category): CategoryEntity {
        return CategoryEntity(
            id = category.id,
            name = category.name,
            description = category.description,
            imageUrl = category.imageUrl,
            createdAt = category.createdAt,
            updatedAt = category.updatedAt
        )
    }

    fun domainToDto(category: Category): CategoryDto {
        return CategoryDto(
            id = category.id,
            name = category.name,
            description = category.description,
            imageUrl = category.imageUrl,
            createdAt = DateUtils.formatTimestampToIso8601(category.createdAt),
            updatedAt = DateUtils.formatTimestampToIso8601(category.updatedAt)
        )
    }

    fun dtoToDomain(dto: CategoryDto): Category {
        return Category(
            id = dto.id,
            name = dto.name,
            description = dto.description,
            imageUrl = dto.imageUrl ?: "",
            createdAt = DateUtils.parseIso8601ToTimestamp(dto.createdAt),
            updatedAt = DateUtils.parseIso8601ToTimestamp(dto.updatedAt)
        )
    }
}