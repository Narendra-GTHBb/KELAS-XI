package com.gymecommerce.musclecart.data.repository

import com.gymecommerce.musclecart.data.local.dao.ProductDao
import com.gymecommerce.musclecart.data.mapper.ProductMapper
import com.gymecommerce.musclecart.data.mapper.CategoryMapper
import com.gymecommerce.musclecart.data.remote.api.ProductApiService
import com.gymecommerce.musclecart.data.remote.api.CategoryApiService
import com.gymecommerce.musclecart.domain.model.Category
import com.gymecommerce.musclecart.domain.model.Product
import com.gymecommerce.musclecart.domain.model.Result
import com.gymecommerce.musclecart.domain.repository.ProductRepository
import com.gymecommerce.musclecart.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val productDao: ProductDao,
    private val productApiService: ProductApiService,
    private val categoryApiService: CategoryApiService,
    private val productMapper: ProductMapper,
    private val categoryMapper: CategoryMapper
) : ProductRepository {

    override suspend fun getProducts(categoryId: Int?, forceRefresh: Boolean): Flow<Resource<List<Product>>> = flow {
        try {
            emit(Resource.Loading())

            // Always fetch from API first for fresh data
            try {
                val response = productApiService.getProducts(categoryId = categoryId)
                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    if (apiResponse.status == "success" && apiResponse.data != null) {
                        val products = apiResponse.data!!.map { productMapper.dtoToDomain(it) }
                        // Clear old products first, then save new ones
                        productDao.clearAllProducts()
                        productDao.insertProducts(products.map { productMapper.domainToEntity(it) })
                    }
                }
            } catch (e: Exception) {
                // If API fails, continue with local data (if any)
                e.printStackTrace()
            }

            // Get products from database (use first() instead of collect to avoid blocking)
            val entities = if (categoryId != null) {
                productDao.getProductsByCategoryFlow(categoryId).first()
            } else {
                productDao.getAllProductsFlow().first()
            }

            val domainProducts = entities.map { productMapper.entityToDomain(it) }
            emit(Resource.Success(domainProducts))
        } catch (e: Exception) {
            emit(Resource.Error("Failed to get products: ${e.message}"))
        }
    }

    override suspend fun getProductById(productId: Int): Result<Product?> {
        return try {
            // Try to get from API first
            try {
                val response = productApiService.getProductById(productId)
                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    val productDto = apiResponse.data?.product
                    if (apiResponse.status == "success" && productDto != null && productDto.id > 0) {
                        val product = productMapper.dtoToDomain(productDto)
                        // Cache it locally
                        productDao.insertProduct(productMapper.domainToEntity(product))
                        return Result.Success(product)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Continue to local fallback
            }
            
            // Fallback to local database
            val entity = productDao.getProductByIdFlow(productId).first()
            Result.Success(entity?.let { productMapper.entityToDomain(it) })
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to get product")
        }
    }

    override suspend fun getProductsByCategory(categoryId: Int): Flow<List<Product>> {
        return productDao.getProductsByCategoryFlow(categoryId).map { list ->
            list.map { productMapper.entityToDomain(it) }
        }
    }

    override suspend fun searchProducts(query: String, categoryId: Int?): Flow<Resource<List<Product>>> = flow {
        try {
            emit(Resource.Loading())

            // Try API search first
            try {
                val response = productApiService.getProducts(search = query, categoryId = categoryId)
                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    if (apiResponse.status == "success" && apiResponse.data != null) {
                        val products = apiResponse.data!!.map { productMapper.dtoToDomain(it) }
                        emit(Resource.Success(products))
                        return@flow
                    }
                }
            } catch (_: Exception) {
                // API failed, fall back to local search
            }

            // Fallback: search local Room database
            val list = productDao.searchProductsFlow(query).first()
            val filteredList = if (categoryId != null) {
                list.filter { it.categoryId == categoryId }
            } else {
                list
            }
            val domainProducts = filteredList.map { productMapper.entityToDomain(it) }
            emit(Resource.Success(domainProducts))
        } catch (e: Exception) {
            emit(Resource.Error("Failed to search products: ${e.message}"))
        }
    }

    override suspend fun getFeaturedProducts(limit: Int): Flow<List<Product>> {
        return productDao.getAllProductsFlow().map { list ->
            list.filter { it.isActive }
                .take(limit)
                .map { productMapper.entityToDomain(it) }
        }
    }

    override suspend fun getProductsInStock(): Flow<List<Product>> {
        return productDao.getAllProductsFlow().map { list ->
            list.filter { it.stockQuantity > 0 }
                .map { productMapper.entityToDomain(it) }
        }
    }

    override suspend fun getLowStockProducts(threshold: Int): Flow<List<Product>> {
        return productDao.getAllProductsFlow().map { list ->
            list.filter { it.stockQuantity in 1..threshold }
                .map { productMapper.entityToDomain(it) }
        }
    }

    override suspend fun createProduct(product: Product): Result<Product> {
        return try {
            val response = productApiService.createProduct(productMapper.domainToDto(product))
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.status == "success" && apiResponse.data != null) {
                    val created = productMapper.dtoToDomain(apiResponse.data!!)
                    productDao.insertProduct(productMapper.domainToEntity(created))
                    Result.Success(created)
                } else {
                    Result.Error(apiResponse.message ?: "Failed to create product on server")
                }
            } else {
                Result.Error("Failed to create product on server")
            }
        } catch (e: Exception) {
            try {
                productDao.insertProduct(productMapper.domainToEntity(product))
                Result.Success(product)
            } catch (ex: Exception) {
                Result.Error(ex.message ?: "Failed to create product")
            }
        }
    }

    override suspend fun updateProduct(product: Product): Result<Product> {
        return try {
            val response = productApiService.updateProduct(
                product.id,
                productMapper.domainToDto(product)
            )
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.status == "success" && apiResponse.data != null) {
                    val updated = productMapper.dtoToDomain(apiResponse.data!!)
                    productDao.updateProduct(productMapper.domainToEntity(updated))
                    Result.Success(updated)
                } else {
                    Result.Error(apiResponse.message ?: "Failed to update product on server")
                }
            } else {
                Result.Error("Failed to update product on server")
            }
        } catch (e: Exception) {
            try {
                productDao.updateProduct(productMapper.domainToEntity(product))
                Result.Success(product)
            } catch (ex: Exception) {
                Result.Error(ex.message ?: "Failed to update product")
            }
        }
    }

    override suspend fun deleteProduct(productId: Int): Result<Unit> {
        return try {
            val response = productApiService.deleteProduct(productId)
            if (response.isSuccessful) {
                productDao.deleteProductById(productId)
                Result.Success(Unit)
            } else {
                Result.Error("Failed to delete product on server")
            }
        } catch (e: Exception) {
            try {
                productDao.deleteProductById(productId)
                Result.Success(Unit)
            } catch (ex: Exception) {
                Result.Error(ex.message ?: "Failed to delete product")
            }
        }
    }

    override suspend fun reduceStock(productId: Int, quantity: Int): Result<Boolean> {
        return try {
            val entity = productDao.getProductByIdFlow(productId).first()
            if (entity != null && entity.stockQuantity >= quantity) {
                productDao.updateProduct(entity.copy(stockQuantity = entity.stockQuantity - quantity))
                Result.Success(true)
            } else {
                Result.Success(false)
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to reduce stock")
        }
    }

    override suspend fun increaseStock(productId: Int, quantity: Int): Result<Unit> {
        return try {
            val entity = productDao.getProductByIdFlow(productId).first()
            if (entity != null) {
                productDao.updateProduct(entity.copy(stockQuantity = entity.stockQuantity + quantity))
                Result.Success(Unit)
            } else {
                Result.Error("Product not found")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to increase stock")
        }
    }

    override suspend fun getProductCount(): Result<Int> {
        return try {
            Result.Success(productDao.getProductCountFlow().first())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to get product count")
        }
    }

    override suspend fun getInStockProductCount(): Result<Int> {
        return try {
            val products = productDao.getAllProductsFlow().first()
            val count = products.count { it.stockQuantity > 0 }
            Result.Success(count)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to get in-stock count")
        }
    }

    override suspend fun syncProducts(): Result<Unit> {
        return try {
            val response = productApiService.getProducts()
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.status == "success" && apiResponse.data != null) {
                    val products = apiResponse.data!!.map { productMapper.dtoToDomain(it) }
                    productDao.insertProducts(products.map { productMapper.domainToEntity(it) })
                    Result.Success(Unit)
                } else {
                    Result.Error(apiResponse.message ?: "Failed to sync products from server")
                }
            } else {
                Result.Error("Failed to sync products from server")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to sync products")
        }
    }

    override suspend fun refreshProducts(): Result<Unit> {
        return try {
            productDao.clearAllProducts()
            syncProducts()
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to refresh products")
        }
    }

    // ===========================
    // Final Implementation for getCategories
    // ===========================
    override suspend fun getCategories(forceRefresh: Boolean): Flow<Resource<List<Category>>> = flow {
        try {
            emit(Resource.Loading())

            // Check if database is empty or force refresh requested
            val localCategories = productDao.getAllCategoriesFlow().first()
            val shouldFetchFromApi = localCategories.isEmpty() || forceRefresh

            if (shouldFetchFromApi) {
                // Fetch from API
                try {
                    val response = categoryApiService.getCategories()
                    if (response.isSuccessful && response.body() != null) {
                        val apiResponse = response.body()!!
                        if (apiResponse.status == "success" && apiResponse.data != null) {
                            val categories = apiResponse.data!!.map { categoryMapper.dtoToDomain(it) }
                            // Clear old categories first, then save new ones
                            productDao.clearAllCategories()
                            val entities = categories.map { categoryMapper.domainToEntity(it) }
                            productDao.insertCategories(entities)
                        }
                    }
                } catch (e: Exception) {
                    // If API fails, continue with local data (if any)
                    e.printStackTrace()
                }
            }

            // Ambil kategori dari DAO (now updated)
            val categoryEntities = productDao.getAllCategoriesFlow().first()
            val categories = categoryEntities.map { productMapper.categoryEntityToDomain(it) }

            emit(Resource.Success(categories))
        } catch (e: Exception) {
            emit(Resource.Error("Failed to get categories: ${e.message}"))
        }
    }
}