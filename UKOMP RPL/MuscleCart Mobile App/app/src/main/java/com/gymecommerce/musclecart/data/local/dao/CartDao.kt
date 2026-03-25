package com.gymecommerce.musclecart.data.local.dao

import androidx.room.*
import com.gymecommerce.musclecart.data.local.entity.CartItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    
    @Query("SELECT * FROM cart_items WHERE userId = :userId ORDER BY addedAt DESC")
    @RewriteQueriesToDropUnusedColumns
    fun getCartItemsFlow(userId: Int): Flow<List<CartItemEntity>>
    
    @Query("SELECT * FROM cart_items WHERE userId = :userId AND productId = :productId")
    @RewriteQueriesToDropUnusedColumns
    fun getCartItemFlow(userId: Int, productId: Int): Flow<CartItemEntity?>
    
    @Query("SELECT COUNT(*) FROM cart_items WHERE userId = :userId")
    fun getCartItemCountFlow(userId: Int): Flow<Int>
    
    @Query("SELECT SUM(quantity) FROM cart_items WHERE userId = :userId")
    fun getTotalQuantityFlow(userId: Int): Flow<Int?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(cartItem: CartItemEntity)
    
    @Update
    suspend fun updateCartItem(cartItem: CartItemEntity)
    
    @Query("UPDATE cart_items SET quantity = :quantity, updatedAt = :updatedAt WHERE userId = :userId AND productId = :productId")
    suspend fun updateCartItemQuantity(
        userId: Int, 
        productId: Int, 
        quantity: Int, 
        updatedAt: Long = System.currentTimeMillis()
    )
    
    @Delete
    suspend fun deleteCartItem(cartItem: CartItemEntity)
    
    @Query("DELETE FROM cart_items WHERE userId = :userId AND productId = :productId")
    suspend fun deleteCartItem(userId: Int, productId: Int)
    
    @Query("DELETE FROM cart_items WHERE userId = :userId")
    suspend fun clearCart(userId: Int)
    
    @Query("DELETE FROM cart_items")
    suspend fun deleteAllCartItems()
}