package com.gymecommerce.musclecart.data.local.dao

import androidx.room.*
import com.gymecommerce.musclecart.data.local.entity.ProductEntity
import com.gymecommerce.musclecart.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    // ===== PRODUCT =====
    @Query("SELECT * FROM products WHERE isActive = 1 ORDER BY name ASC")
    fun getAllProductsFlow(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :productId")
    fun getProductByIdFlow(productId: Int): Flow<ProductEntity?>

    @Query("SELECT * FROM products WHERE categoryId = :categoryId AND isActive = 1 ORDER BY name ASC")
    fun getProductsByCategoryFlow(categoryId: Int): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE (name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%') AND isActive = 1")
    fun searchProductsFlow(query: String): Flow<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Delete
    suspend fun deleteProduct(product: ProductEntity)

    @Query("DELETE FROM products WHERE id = :productId")
    suspend fun deleteProductById(productId: Int)

    @Query("DELETE FROM products")
    suspend fun clearAllProducts()

    @Query("SELECT COUNT(*) FROM products WHERE isActive = 1")
    fun getProductCountFlow(): Flow<Int>

    @Query("SELECT COUNT(*) FROM products WHERE stockQuantity > 0 AND isActive = 1")
    fun getInStockProductCountFlow(): Flow<Int>

    // ===== CATEGORY =====
    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAllCategoriesFlow(): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)

    @Query("SELECT * FROM categories WHERE id = :categoryId")
    fun getCategoryByIdFlow(categoryId: Int): Flow<CategoryEntity?>

    @Query("DELETE FROM categories")
    suspend fun clearAllCategories()

    @Query("SELECT COUNT(*) FROM categories")
    fun getCategoryCountFlow(): Flow<Int>
}