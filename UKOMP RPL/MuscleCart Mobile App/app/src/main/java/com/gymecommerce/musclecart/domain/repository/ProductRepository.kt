package com.gymecommerce.musclecart.domain.repository

import com.gymecommerce.musclecart.domain.model.Category
import com.gymecommerce.musclecart.domain.model.Product
import com.gymecommerce.musclecart.domain.model.Result
import com.gymecommerce.musclecart.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface ProductRepository {

    // Ambil semua produk
    suspend fun getProducts(categoryId: Int? = null, forceRefresh: Boolean = false): Flow<Resource<List<Product>>>

    // Ambil produk berdasarkan ID
    suspend fun getProductById(productId: Int): Result<Product?>

    // Ambil produk berdasarkan kategori
    suspend fun getProductsByCategory(categoryId: Int): Flow<List<Product>>

    // Cari produk berdasarkan query
    suspend fun searchProducts(query: String, categoryId: Int? = null): Flow<Resource<List<Product>>>

    // Ambil semua kategori (suspend + default value)
    suspend fun getCategories(forceRefresh: Boolean = false): Flow<Resource<List<Category>>>

    // Ambil produk unggulan
    suspend fun getFeaturedProducts(limit: Int = 10): Flow<List<Product>>

    // Ambil produk yang masih tersedia di stok
    suspend fun getProductsInStock(): Flow<List<Product>>

    // Ambil produk dengan stok rendah
    suspend fun getLowStockProducts(threshold: Int = 5): Flow<List<Product>>

    // Buat produk baru
    suspend fun createProduct(product: Product): Result<Product>

    // Update produk
    suspend fun updateProduct(product: Product): Result<Product>

    // Hapus produk berdasarkan ID
    suspend fun deleteProduct(productId: Int): Result<Unit>

    // Kurangi stok produk
    suspend fun reduceStock(productId: Int, quantity: Int): Result<Boolean>

    // Tambah stok produk
    suspend fun increaseStock(productId: Int, quantity: Int): Result<Unit>

    // Hitung total produk
    suspend fun getProductCount(): Result<Int>

    // Hitung total produk yang tersedia di stok
    suspend fun getInStockProductCount(): Result<Int>

    // Sinkronisasi produk dengan server
    suspend fun syncProducts(): Result<Unit>

    // Refresh semua produk (hapus + sync)
    suspend fun refreshProducts(): Result<Unit>
}