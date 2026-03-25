package com.gymecommerce.musclecart.data.local.database

import com.gymecommerce.musclecart.data.local.entity.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseSeeder @Inject constructor(
    private val database: GymEcommerceDatabase
) {
    
    suspend fun seedDatabase() {
        // Check if database is already seeded
        if (database.categoryDao().getCategoryCount() > 0) {
            return // Already seeded
        }
        
        seedCategories()
        seedProducts()
        seedUsers()
    }
    
    private suspend fun seedCategories() {
        val categories = listOf(
            CategoryEntity(
                id = 1,
                name = "Cardio Equipment",
                description = "Treadmills, bikes, and other cardio machines",
                imageUrl = "https://example.com/cardio.jpg"
            ),
            CategoryEntity(
                id = 2,
                name = "Strength Training",
                description = "Weights, barbells, and strength equipment",
                imageUrl = "https://example.com/strength.jpg"
            ),
            CategoryEntity(
                id = 3,
                name = "Supplements",
                description = "Protein powders, vitamins, and supplements",
                imageUrl = "https://example.com/supplements.jpg"
            ),
            CategoryEntity(
                id = 4,
                name = "Accessories",
                description = "Gym bags, water bottles, and accessories",
                imageUrl = "https://example.com/accessories.jpg"
            )
        )
        
        database.categoryDao().insertCategories(categories)
    }
    
    private suspend fun seedProducts() {
        val products = listOf(
            // Cardio Equipment
            ProductEntity(
                id = 1,
                name = "Professional Treadmill",
                description = "High-quality treadmill with advanced features for home and commercial use",
                price = 1299.99,
                imageUrl = "https://example.com/treadmill.jpg",
                categoryId = 1,
                stockQuantity = 15
            ),
            ProductEntity(
                id = 2,
                name = "Exercise Bike",
                description = "Stationary bike with adjustable resistance and digital display",
                price = 599.99,
                imageUrl = "https://example.com/bike.jpg",
                categoryId = 1,
                stockQuantity = 25
            ),
            
            // Strength Training
            ProductEntity(
                id = 3,
                name = "Olympic Barbell Set",
                description = "Complete barbell set with plates for serious strength training",
                price = 899.99,
                imageUrl = "https://example.com/barbell.jpg",
                categoryId = 2,
                stockQuantity = 10
            ),
            ProductEntity(
                id = 4,
                name = "Adjustable Dumbbells",
                description = "Space-saving adjustable dumbbells with quick weight changes",
                price = 399.99,
                imageUrl = "https://example.com/dumbbells.jpg",
                categoryId = 2,
                stockQuantity = 30
            ),
            ProductEntity(
                id = 5,
                name = "Power Rack",
                description = "Heavy-duty power rack for safe squats and bench press",
                price = 1599.99,
                imageUrl = "https://example.com/power-rack.jpg",
                categoryId = 2,
                stockQuantity = 5
            ),
            
            // Supplements
            ProductEntity(
                id = 6,
                name = "Whey Protein Powder",
                description = "Premium whey protein for muscle building and recovery",
                price = 49.99,
                imageUrl = "https://example.com/protein.jpg",
                categoryId = 3,
                stockQuantity = 100
            ),
            ProductEntity(
                id = 7,
                name = "Pre-Workout Formula",
                description = "Energy-boosting pre-workout supplement for intense training",
                price = 34.99,
                imageUrl = "https://example.com/preworkout.jpg",
                categoryId = 3,
                stockQuantity = 75
            ),
            ProductEntity(
                id = 8,
                name = "BCAA Capsules",
                description = "Branched-chain amino acids for muscle recovery",
                price = 29.99,
                imageUrl = "https://example.com/bcaa.jpg",
                categoryId = 3,
                stockQuantity = 50
            ),
            
            // Accessories
            ProductEntity(
                id = 9,
                name = "Gym Bag",
                description = "Spacious gym bag with multiple compartments",
                price = 79.99,
                imageUrl = "https://example.com/gym-bag.jpg",
                categoryId = 4,
                stockQuantity = 40
            ),
            ProductEntity(
                id = 10,
                name = "Water Bottle",
                description = "Insulated water bottle to keep drinks cold",
                price = 24.99,
                imageUrl = "https://example.com/water-bottle.jpg",
                categoryId = 4,
                stockQuantity = 60
            ),
            ProductEntity(
                id = 11,
                name = "Lifting Gloves",
                description = "Comfortable lifting gloves for better grip",
                price = 19.99,
                imageUrl = "https://example.com/gloves.jpg",
                categoryId = 4,
                stockQuantity = 35
            ),
            ProductEntity(
                id = 12,
                name = "Yoga Mat",
                description = "Non-slip yoga mat for stretching and floor exercises",
                price = 39.99,
                imageUrl = "https://example.com/yoga-mat.jpg",
                categoryId = 4,
                stockQuantity = 45
            )
        )
        
        database.productDao().insertProducts(products)
    }
    
    private suspend fun seedUsers() {
        val users = listOf(
            UserEntity(
                id = 1,
                name = "Admin User",
                email = "admin@musclecart.com",
                password = "admin123",
                phone = "+1234567890",
                address = "123 Admin Street, Admin City",
                isAdmin = true
            ),
            UserEntity(
                id = 2,
                name = "John Doe",
                email = "john.doe@example.com",
                password = "john123",
                phone = "+1987654321",
                address = "456 User Avenue, User City",
                isAdmin = false
            ),
            UserEntity(
                id = 3,
                name = "Jane Smith",
                email = "jane.smith@example.com",
                password = "jane123",
                phone = "+1122334455",
                address = "789 Customer Lane, Customer City",
                isAdmin = false
            )
        )
        
        database.userDao().insertUsers(users)
    }
}