package com.gymecommerce.musclecart.data.repository

import com.gymecommerce.musclecart.data.local.dao.CategoryDao
import com.gymecommerce.musclecart.data.mapper.CategoryMapper
import com.gymecommerce.musclecart.data.remote.api.CategoryApiService
import com.gymecommerce.musclecart.domain.model.Category
import com.gymecommerce.musclecart.domain.model.Result
import com.gymecommerce.musclecart.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao,
    private val categoryApiService: CategoryApiService,
    private val categoryMapper: CategoryMapper
) : CategoryRepository {

    override suspend fun getCategories(): Flow<List<Category>> {
        return categoryDao.getAllCategoriesFlow().map { entities ->
            entities.map { categoryMapper.entityToDomain(it) }
        }
    }

    override suspend fun getCategoryById(categoryId: Int): Result<Category?> {
        return try {
            val entity = categoryDao.getCategoryByIdFlow(categoryId).first()
            val category = entity?.let { categoryMapper.entityToDomain(it) }
            Result.Success(category)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to get category")
        }
    }

    override suspend fun getCategoryByName(name: String): Result<Category?> {
        return try {
            val entity = categoryDao.getCategoryByNameFlow(name).first()
            val category = entity?.let { categoryMapper.entityToDomain(it) }
            Result.Success(category)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to get category by name")
        }
    }

    override suspend fun createCategory(category: Category): Result<Category> {
        return try {
            // Try server first
            val response = categoryApiService.createCategory(
                categoryMapper.domainToDto(category)
            )

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.status == "success" && apiResponse.data != null) {
                    val createdCategory = categoryMapper.dtoToDomain(apiResponse.data!!)

                    // Save locally
                    categoryDao.insertCategory(
                        categoryMapper.domainToEntity(createdCategory)
                    )

                    Result.Success(createdCategory)
                } else {
                    Result.Error(apiResponse.message ?: "Failed to create category on server")
                }
            } else {
                Result.Error("Failed to create category on server")
            }

        } catch (e: Exception) {

            // Offline fallback
            try {
                val entity = categoryMapper.domainToEntity(category)

                categoryDao.insertCategory(entity)

                Result.Success(category)

            } catch (localException: Exception) {
                Result.Error(localException.message ?: "Failed to create category")
            }
        }
    }

    override suspend fun updateCategory(category: Category): Result<Category> {
        return try {
            val response = categoryApiService.updateCategory(
                category.id,
                categoryMapper.domainToDto(category)
            )

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.status == "success" && apiResponse.data != null) {
                    val updatedCategory = categoryMapper.dtoToDomain(apiResponse.data!!)

                    categoryDao.updateCategory(
                        categoryMapper.domainToEntity(updatedCategory)
                    )

                    Result.Success(updatedCategory)
                } else {
                    Result.Error(apiResponse.message ?: "Failed to update category on server")
                }
            } else {
                Result.Error("Failed to update category on server")
            }

        } catch (e: Exception) {
            try {
                categoryDao.updateCategory(
                    categoryMapper.domainToEntity(category)
                )
                Result.Success(category)
            } catch (localException: Exception) {
                Result.Error(localException.message ?: "Failed to update category")
            }
        }
    }

    override suspend fun deleteCategory(categoryId: Int): Result<Unit> {
        return try {
            val response = categoryApiService.deleteCategory(categoryId)

            if (response.isSuccessful) {
                categoryDao.deleteCategory(categoryId)
                Result.Success(Unit)
            } else {
                Result.Error("Failed to delete category on server")
            }

        } catch (e: Exception) {
            try {
                categoryDao.deleteCategory(categoryId)
                Result.Success(Unit)
            } catch (localException: Exception) {
                Result.Error(localException.message ?: "Failed to delete category")
            }
        }
    }

    override suspend fun getCategoryCount(): Result<Int> {
        return try {
            val count = categoryDao.getCategoryCountFlow().first()
            Result.Success(count)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to get category count")
        }
    }

    override suspend fun syncCategories(): Result<Unit> {
        return try {
            val response = categoryApiService.getCategories()

            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.status == "success" && apiResponse.data != null) {
                    val categories = apiResponse.data!!.map {
                        categoryMapper.dtoToDomain(it)
                    }

                    val entities = categories.map {
                        categoryMapper.domainToEntity(it)
                    }

                    categoryDao.insertCategories(entities)

                    Result.Success(Unit)
                } else {
                    Result.Error(apiResponse.message ?: "Failed to sync categories from server")
                }
            } else {
                Result.Error("Failed to sync categories from server")
            }

        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to sync categories")
        }
    }

    override suspend fun refreshCategories(): Result<Unit> {
        return try {
            categoryDao.clearAllCategories()
            syncCategories()
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to refresh categories")
        }
    }
}