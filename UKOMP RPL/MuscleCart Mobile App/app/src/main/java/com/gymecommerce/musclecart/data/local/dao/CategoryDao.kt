package com.gymecommerce.musclecart.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.gymecommerce.musclecart.data.local.entity.CategoryEntity

@Dao
interface CategoryDao {

    @Query("SELECT * FROM categories ORDER BY id ASC")
    fun getAllCategoriesFlow(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE id = :categoryId")
    fun getCategoryByIdFlow(categoryId: Int): Flow<CategoryEntity?>

    @Query("SELECT * FROM categories WHERE name = :name LIMIT 1")
    fun getCategoryByNameFlow(name: String): Flow<CategoryEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)

    @Update
    suspend fun updateCategory(category: CategoryEntity)

    @Query("DELETE FROM categories WHERE id = :categoryId")
    suspend fun deleteCategory(categoryId: Int)

    @Query("DELETE FROM categories")
    suspend fun clearAllCategories()

    @Query("SELECT COUNT(*) FROM categories")
    suspend fun getCategoryCount(): Int

    @Query("SELECT COUNT(*) FROM categories")
    fun getCategoryCountFlow(): Flow<Int>
}