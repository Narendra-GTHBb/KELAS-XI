package com.gymecommerce.musclecart.domain.repository

import com.gymecommerce.musclecart.domain.model.Category
import com.gymecommerce.musclecart.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    
    suspend fun getCategories(): Flow<List<Category>>
    
    suspend fun getCategoryById(categoryId: Int): Result<Category?>
    
    suspend fun getCategoryByName(name: String): Result<Category?>
    
    suspend fun createCategory(category: Category): Result<Category>
    
    suspend fun updateCategory(category: Category): Result<Category>
    
    suspend fun deleteCategory(categoryId: Int): Result<Unit>
    
    suspend fun getCategoryCount(): Result<Int>
    
    suspend fun syncCategories(): Result<Unit>
    
    suspend fun refreshCategories(): Result<Unit>
}