package com.gymecommerce.musclecart.data.repository

import com.gymecommerce.musclecart.data.local.entity.CartItemEntity
import com.gymecommerce.musclecart.data.local.dao.CartDao
import com.gymecommerce.musclecart.data.local.dao.ProductDao
import com.gymecommerce.musclecart.data.mapper.ProductMapper
import com.gymecommerce.musclecart.data.remote.api.CartApiService
import com.gymecommerce.musclecart.data.remote.api.AddToCartRequest
import com.gymecommerce.musclecart.data.remote.api.UpdateCartRequest
import com.gymecommerce.musclecart.domain.repository.AuthRepository
import com.gymecommerce.musclecart.domain.model.Cart
import com.gymecommerce.musclecart.domain.model.CartItem
import com.gymecommerce.musclecart.domain.model.Result
import com.gymecommerce.musclecart.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepositoryImpl @Inject constructor(
    private val cartDao: CartDao,
    private val productDao: ProductDao,
    private val productMapper: ProductMapper,
    private val cartApiService: CartApiService,
    private val authRepository: AuthRepository
) : CartRepository {

    // Maps productId → cart item's own DB id (needed for update/remove API calls)
    private val productIdToCartItemId = mutableMapOf<Int, Int>()

    private suspend fun currentUserId(): Int = authRepository.getCurrentUser()?.id ?: 1

    /**
     * Returns a local Room-based cart Flow used as fallback when the API is unavailable.
     */
    private suspend fun localCartFlow(): Flow<Cart> {
        val userId = currentUserId()
        return cartDao.getCartItemsFlow(userId).combine(
            productDao.getAllProductsFlow()
        ) { cartEntities, productEntities ->
            val productMap = productEntities.associateBy { it.id }
            val cartItems = cartEntities.mapNotNull { cartEntity ->
                val productEntity = productMap[cartEntity.productId]
                if (productEntity != null) {
                    CartItem(
                        productId = cartEntity.productId,
                        product = productMapper.entityToDomain(productEntity),
                        quantity = cartEntity.quantity
                    )
                } else null
            }
            Cart(cartItems)
        }
    }

    override suspend fun getCart(): Flow<Cart> {
        // Do the API call as a normal suspend call (outside any flow builder)
        // so that AbortFlowException from .first() doesn't cause transparency violations.
        return try {
            val response = cartApiService.getCart()
            val apiResponse = response.body()

            if (response.isSuccessful && apiResponse?.status == "success" && apiResponse.data != null) {
                val cartDto = apiResponse.data
                android.util.Log.d("CartRepo", "getCart: ${cartDto.items.size} items from API")
                val cartItems = cartDto.items.mapNotNull { cartItemDto ->
                    val resolvedPid = cartItemDto.resolvedProductId()
                    cartItemDto.product?.let { productDto ->
                        productIdToCartItemId[resolvedPid] = cartItemDto.id
                        CartItem(
                            productId = resolvedPid,
                            product = productMapper.dtoToDomain(productDto),
                            quantity = cartItemDto.quantity
                        )
                    }
                }
                android.util.Log.d("CartRepo", "getCart: ${cartItems.size} mapped items")
                flowOf(Cart(cartItems))
            } else {
                localCartFlow()
            }
        } catch (e: Exception) {
            localCartFlow()
        }
    }

    override suspend fun addToCart(productId: Int, quantity: Int): Result<Unit> {
        return try {
            android.util.Log.d("CartRepo", "addToCart API call: productId=$productId, quantity=$quantity")
            // Call API to add to cart
            val response = cartApiService.addToCart(
                AddToCartRequest(
                    product_id = productId,
                    quantity = quantity
                )
            )
            
            android.util.Log.d("CartRepo", "addToCart response code: ${response.code()}")
            
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                android.util.Log.d("CartRepo", "addToCart API status: ${apiResponse.status}, message: ${apiResponse.message}")
                
                if (apiResponse.status == "success") {
                    // Try to also save to local cache for offline support
                    // Wrapped in try-catch: local FK constraint may fail if product
                    // isn't cached locally yet, but API already succeeded so that's fine
                    try {
                        val userId = currentUserId()
                        val existingCartItem = cartDao.getCartItemFlow(userId, productId).first()
                        if (existingCartItem != null) {
                            val newQuantity = existingCartItem.quantity + quantity
                            cartDao.updateCartItemQuantity(userId, productId, newQuantity)
                        } else {
                            cartDao.insertCartItem(
                                CartItemEntity(
                                    userId = userId,
                                    productId = productId,
                                    quantity = quantity
                                )
                            )
                        }
                    } catch (localEx: Exception) {
                        // Ignore local DB error - cart is already saved on server
                        android.util.Log.w("CartRepo", "Local cache update failed (ignored): ${localEx.message}")
                    }
                    
                    Result.Success(Unit)
                } else {
                    Result.Error(apiResponse.message ?: "Failed to add item to cart")
                }
            } else {
                val errorBody = response.errorBody()?.string()
                android.util.Log.e("CartRepo", "addToCart HTTP error ${response.code()}: $errorBody")
                Result.Error("HTTP ${response.code()}: ${errorBody ?: "Failed to add item to cart"}")
            }
        } catch (e: Exception) {
            android.util.Log.e("CartRepo", "addToCart exception: ${e.javaClass.simpleName}: ${e.message}", e)
            Result.Error("${e.javaClass.simpleName}: ${e.message ?: "Network error"}")
        }
    }

    override suspend fun removeFromCart(productId: Int): Result<Unit> {
        return try {
            val userId = currentUserId()
            
            // Use cached cart item id (backend expects cart item id, not product id)
            val cartItemId = productIdToCartItemId[productId]
                ?: return Result.Error("Cart item id not found for productId=$productId. Load the cart first.")
            
            val response = cartApiService.removeFromCart(cartItemId)
            
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                
                if (apiResponse.status == "success") {
                    // Also remove from local cache
                    cartDao.deleteCartItem(userId, productId)
                    Result.Success(Unit)
                } else {
                    Result.Error(apiResponse.message ?: "Failed to remove item from cart")
                }
            } else {
                Result.Error("Failed to remove item from cart")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to remove item from cart")
        }
    }

    override suspend fun updateCartItemQuantity(productId: Int, quantity: Int): Result<Unit> {
        return try {
            if (quantity <= 0) {
                return removeFromCart(productId)
            }
            
            // Use cached cart item id (backend PUT /cart/update/{id} expects cart item id, not product id)
            val cartItemId = productIdToCartItemId[productId]
                ?: return Result.Error("Cart item id not found for productId=$productId. Load the cart first.")
            
            val response = cartApiService.updateCartItem(
                id = cartItemId,
                request = UpdateCartRequest(quantity = quantity)
            )
            
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                
                if (apiResponse.status == "success") {
                    // Update local cache (best effort)
                    try {
                        val userId = currentUserId()
                        cartDao.updateCartItemQuantity(userId, productId, quantity)
                    } catch (ignored: Exception) {}
                    Result.Success(Unit)
                } else {
                    Result.Error(apiResponse.message ?: "Failed to update cart item")
                }
            } else {
                Result.Error("Failed to update cart item")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to update cart item quantity")
        }
    }

    override suspend fun clearCart(): Result<Unit> {
        return try {
            // Call API to clear cart
            val response = cartApiService.clearCart()
            
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                
                if (apiResponse.status == "success") {
                    // Clear local cache
                    val userId = currentUserId()
                    cartDao.clearCart(userId)
                    Result.Success(Unit)
                } else {
                    Result.Error(apiResponse.message ?: "Failed to clear cart")
                }
            } else {
                Result.Error("Failed to clear cart")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to clear cart")
        }
    }

    override suspend fun getCartItemCount(): Flow<Int> {
        return cartDao.getCartItemCountFlow(currentUserId())
    }

    override suspend fun getCartTotal(): Flow<Double> {
        val userId = currentUserId()
        return cartDao.getCartItemsFlow(userId).combine(
            productDao.getAllProductsFlow()
        ) { cartEntities, productEntities ->
            val productMap = productEntities.associateBy { it.id }
            cartEntities.sumOf { cartEntity ->
                val productEntity = productMap[cartEntity.productId]
                if (productEntity != null) productEntity.price * cartEntity.quantity else 0.0
            }
        }
    }

    override suspend fun isProductInCart(productId: Int): Flow<Boolean> {
        val userId = currentUserId()
        return cartDao.getCartItemsFlow(userId).map { list ->
            list.any { it.productId == productId }
        }
    }

    override suspend fun getCartItem(productId: Int): Result<CartItem?> {
        val userId = currentUserId()
        return try {
            val cartEntity = cartDao.getCartItemFlow(userId, productId).first()
            if (cartEntity != null) {
                val productEntity = productDao.getProductByIdFlow(productId).first()
                if (productEntity != null) {
                    Result.Success(
                        CartItem(
                            productId = cartEntity.productId,
                            product = productMapper.entityToDomain(productEntity),
                            quantity = cartEntity.quantity
                        )
                    )
                } else Result.Success(null)
            } else Result.Success(null)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to get cart item")
        }
    }
    
    override suspend fun syncCart(): Result<Unit> {
        return try {
            // For now, cart is local-only
            // In a real app, you might sync cart with server for logged-in users
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to sync cart")
        }
    }
}