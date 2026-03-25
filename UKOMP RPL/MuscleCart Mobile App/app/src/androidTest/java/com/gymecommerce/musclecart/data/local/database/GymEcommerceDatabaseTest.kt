package com.gymecommerce.musclecart.data.local.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.gymecommerce.musclecart.data.local.entity.CategoryEntity
import com.gymecommerce.musclecart.data.local.entity.ProductEntity
import com.gymecommerce.musclecart.data.local.entity.UserEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GymEcommerceDatabaseTest {
    
    private lateinit var database: GymEcommerceDatabase
    
    @Before
    fun createDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            GymEcommerceDatabase::class.java
        ).build()
    }
    
    @After
    fun closeDb() {
        database.close()
    }
    
    @Test
    fun insertAndGetUser() = runBlocking {
        val user = UserEntity(
            id = 1,
            name = "Test User",
            email = "test@example.com",
            password = "password123",
            phone = null,
            address = null,
            isAdmin = false
        )
        
        database.userDao().insertUser(user)
        val retrievedUser = database.userDao().getUserByIdFlow(user.id).first()
        
        assertNotNull(retrievedUser)
        assertEquals(user.name, retrievedUser?.name)
        assertEquals(user.email, retrievedUser?.email)
    }
    
    @Test
    fun insertAndGetCategory() = runBlocking {
        val category = CategoryEntity(
            id = 1,
            name = "Equipment",
            description = "Fitness equipment",
            imageUrl = "https://example.com/equipment.jpg"
        )
        
        database.categoryDao().insertCategory(category)
        val retrievedCategory = database.categoryDao().getCategoryByIdFlow(category.id).first()
        
        assertNotNull(retrievedCategory)
        assertEquals(category.name, retrievedCategory?.name)
    }
    
    @Test
    fun insertProductWithCategory() = runBlocking {
        // First insert a category
        val category = CategoryEntity(
            id = 1,
            name = "Equipment",
            description = "Fitness equipment",
            imageUrl = "https://example.com/equipment.jpg"
        )
        database.categoryDao().insertCategory(category)
        
        // Then insert a product with that category
        val product = ProductEntity(
            id = 1,
            name = "Dumbbell Set",
            price = 299.99,
            stockQuantity = 10,
            description = "Professional dumbbell set",
            imageUrl = "https://example.com/dumbbell.jpg",
            categoryId = category.id,
            isActive = true
        )
        
        database.productDao().insertProduct(product)
        val retrievedProduct = database.productDao().getProductByIdFlow(product.id).first()
        
        assertNotNull(retrievedProduct)
        assertEquals(product.name, retrievedProduct?.name)
        assertEquals(product.categoryId, retrievedProduct?.categoryId)
    }
    
    @Test
    fun getProductsByCategory() = runBlocking {
        // Insert category
        val category = CategoryEntity(
            id = 1,
            name = "Supplements",
            description = "Fitness supplements",
            imageUrl = "https://example.com/supplements.jpg"
        )
        database.categoryDao().insertCategory(category)
        
        // Insert products
        val products = listOf(
            ProductEntity(
                id = 1,
                name = "Protein Powder",
                price = 49.99,
                stockQuantity = 20,
                description = "Whey protein powder",
                imageUrl = "https://example.com/protein.jpg",
                categoryId = category.id,
                isActive = true
            ),
            ProductEntity(
                id = 2,
                name = "Creatine",
                price = 29.99,
                stockQuantity = 15,
                description = "Pure creatine monohydrate",
                imageUrl = "https://example.com/creatine.jpg",
                categoryId = category.id,
                isActive = true
            )
        )
        
        database.productDao().insertProducts(products)
        
        val categoryProducts = database.productDao().getProductsByCategoryFlow(category.id).first()
        
        assertEquals(2, categoryProducts.size)
        assertTrue(categoryProducts.all { it.categoryId == category.id })
    }
}