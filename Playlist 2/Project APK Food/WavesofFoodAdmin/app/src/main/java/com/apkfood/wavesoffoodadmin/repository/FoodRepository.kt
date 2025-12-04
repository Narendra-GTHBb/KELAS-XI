package com.apkfood.wavesoffoodadmin.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.apkfood.wavesoffoodadmin.model.*
import kotlinx.coroutines.tasks.await
import android.net.Uri
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import android.util.Base64

class FoodRepository {
    private val firestore = FirebaseFirestore.getInstance()
    
    // Food Management
    suspend fun getAllFoods(): List<Food> {
        return try {
            Log.d("FoodRepository", "🔄 ADMIN: Loading foods from Firebase...")
            val result = firestore.collection("foods")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            
            // SAMAKAN PARSING KAYAK USER APP!
            val foods = result.documents.mapNotNull { document ->
                try {
                    Log.d("FoodRepository", "📄 ADMIN: Processing document ${document.id}")
                    Log.d("FoodRepository", "📋 ADMIN: Raw data: ${document.data}")
                    
                    val food = document.toObject(Food::class.java)
                    val finalFood = food?.copy(id = document.id)
                    
                    Log.d("FoodRepository", "✅ ADMIN: Parsed food: ${finalFood?.name} - ${finalFood?.price}")
                    finalFood
                } catch (e: Exception) {
                    Log.e("FoodRepository", "❌ ADMIN: Error parsing food document: ${document.id}", e)
                    Log.e("FoodRepository", "❌ ADMIN: Document data: ${document.data}")
                    null
                }
            }
            
            Log.d("FoodRepository", "✅ ADMIN: Successfully loaded ${foods.size} foods from Firebase")
            foods.forEach { food ->
                Log.d("FoodRepository", "🍽️ ADMIN Food: ${food.name} - Rp ${food.price}")
            }
            foods
        } catch (e: Exception) {
            Log.e("FoodRepository", "❌ ADMIN: Error loading foods from Firebase", e)
            emptyList()
        }
    }
    
    suspend fun addFood(food: Food): Boolean {
        return try {
            val docRef = firestore.collection("foods").document()
            val foodWithId = food.copy(id = docRef.id)
            docRef.set(foodWithId).await()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun updateFood(food: Food): Boolean {
        return try {
            firestore.collection("foods")
                .document(food.id)
                .set(food.copy(updatedAt = System.currentTimeMillis()))
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun deleteFood(foodId: String): Boolean {
        return try {
            firestore.collection("foods")
                .document(foodId)
                .delete()
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun addSampleFoodsWithImages(): Boolean {
        return try {
            Log.d("FoodRepository", "Adding sample foods with images...")
            
            val sampleFoods = listOf(
                Food(
                    name = "Spaghetti Carbonara",
                    description = "Creamy pasta with bacon and parmesan cheese",
                    price = 62000.0,
                    imageUrl = "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAYEBQYFBAYGBQYHBwYIChAKCgkJChQODwwQFxQYGBcUFhYaHSUfGhsjHBYWICwgIyYnKSopGR8tMC0oMCUoKSj/2wBDAQcHBwoIChMKChMoGhYaKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCj/wAARCAAoACgDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwD9U6KKKACiiigAooooAKKKKACiiigAooooAK4jxp8SfDfg6TV9E1LxBp99q1jdaZLHZQXKTyFZJVQOqqxJwrE57V21FAHGj4geDyMf8JRoR9v3i/8As1L/AMJd4P8Aiz4e/wDBhD/hXRUUAdH/AMJd4P8Aiz4e/wDBhD/hR/wl3g/4s+Hv/BhD/hXRUUAdH/wl3g/4s+Hv/BhD/hR/wl3g/wCLPh7/AMGEP+FdFRQB0f8Awl3g/wCLPh7/AMGEP+FH/CXeD/iz4e/8GEP+FdFRQB0f/CXeD/iz4e/8GEP+FH/CXeD/AIs+Hv8AwYQ/4V0VFAHRX/jXwfpEzQ3+vaBaTr1jlvoY2H4E4NOPxK8FLxJ4s8OH/uJ1r+ivx8n/wCEe8ZeAWOYrDWNUsAexpbu9n/8kf9aRY+NPBl6gltfGfhm6jPR4b6Fx/47QB6RRXjmp/GTwNp0rJe+JPDtuzj/AI+Lq3jb/wAeH9a97o/4KKu6P/Z",
                    categoryId = "main-course",
                    ingredients = listOf("pasta", "eggs", "bacon", "parmesan", "black pepper"),
                    isAvailable = true,
                    isPopular = true,
                    preparationTime = 25,
                    rating = 4.3,
                    createdAt = System.currentTimeMillis()
                ),
                Food(
                    name = "Fresh Orange Juice",
                    description = "Freshly squeezed orange juice",
                    price = 18000.0,
                    imageUrl = "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAYEBQYFBAYGBQYHBwYIChAKCgkJChQODwwQFxQYGBcUFhYaHSUfGhsjHBYWICwgIyYnKSopGR8tMC0oMCUoKSj/2wBDAQcHBwoIChMKChMoGhYaKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCj/wAARCAAoACgDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwD9U6KKKACiiigAooooAKKKKACiiigAorlPGXxE8J+AdL+3+KdcstKgYhUZnzLI30RByT7AVzA/aO+EhOB4zg/EOXH/AKDOtLuoJtuEjjNrSPPNtddX0P2r/9k=",
                    categoryId = "beverages",
                    ingredients = listOf("fresh orange"),
                    isAvailable = true,
                    isPopular = false,
                    preparationTime = 5,
                    rating = 4.1,
                    createdAt = System.currentTimeMillis()
                ),
                Food(
                    name = "Vanilla Ice Cream",
                    description = "Creamy vanilla ice cream with chocolate sauce",
                    price = 22000.0,
                    imageUrl = "data:image/jpeg;base64,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",
                    categoryId = "desserts",
                    ingredients = listOf("vanilla ice cream", "chocolate sauce"),
                    isAvailable = true,
                    isPopular = false,
                    preparationTime = 3,
                    rating = 4.2,
                    createdAt = System.currentTimeMillis()
                ),
                Food(
                    name = "Hot Green Tea",
                    description = "Traditional Japanese green tea",
                    price = 15000.0,
                    imageUrl = "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAYEBQYFBAYGBQYHBwYIChAKCgkJChQODwwQFxQYGBcUFhYaHSUfGhsjHBYWICwgIyYnKSopGR8tMC0oMCUoKSj/2wBDAQcHBwoIChMKChMoGhYaKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCj/wAARCAAoACgDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwD9U6KKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKAP/2Q==",
                    categoryId = "beverages",
                    ingredients = listOf("green tea leaves", "hot water"),
                    isAvailable = true,
                    isPopular = false,
                    preparationTime = 5,
                    rating = 4.1,
                    createdAt = System.currentTimeMillis()
                ),
                Food(
                    name = "Crispy Fried Chicken",
                    description = "Golden crispy fried chicken",
                    price = 55000.0,
                    imageUrl = "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAYEBQYFBAYGBQYHBwYIChAKCgkJChQODwwQFxQYGBcUFhYaHSUfGhsjHBYWICwgIyYnKSopGR8tMC0oMCUoKSj/2wBDAQcHBwoIChMKChMoGhYaKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCj/wAARCAAoACgDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwD9U6KKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKAP/2Q==",
                    categoryId = "main-course",
                    ingredients = listOf("chicken", "flour", "spices", "oil"),
                    isAvailable = true,
                    isPopular = true,
                    preparationTime = 20,
                    rating = 4.5,
                    createdAt = System.currentTimeMillis()
                )
            )
            
            sampleFoods.forEach { food ->
                val docRef = firestore.collection("foods").document()
                val foodWithId = food.copy(id = docRef.id)
                docRef.set(foodWithId).await()
                Log.d("FoodRepository", "Added sample food: ${food.name}")
            }
            
            Log.d("FoodRepository", "Successfully added ${sampleFoods.size} sample foods")
            true
        } catch (e: Exception) {
            Log.e("FoodRepository", "Error adding sample foods", e)
            false
        }
    }
    fun convertImageToBase64(imageUri: Uri, context: android.content.Context): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            
            // Resize bitmap untuk menghemat space
            val resizedBitmap = resizeBitmap(bitmap, 800, 600)
            
            val byteArrayOutputStream = ByteArrayOutputStream()
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            
            "data:image/jpeg;base64," + Base64.encodeToString(byteArray, Base64.DEFAULT)
        } catch (e: Exception) {
            null
        }
    }
    
    private fun resizeBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        
        val ratioBitmap = width.toFloat() / height.toFloat()
        val ratioMax = maxWidth.toFloat() / maxHeight.toFloat()
        
        var finalWidth = maxWidth
        var finalHeight = maxHeight
        
        if (ratioMax > ratioBitmap) {
            finalWidth = (maxHeight.toFloat() * ratioBitmap).toInt()
        } else {
            finalHeight = (maxWidth.toFloat() / ratioBitmap).toInt()
        }
        
        return Bitmap.createScaledBitmap(bitmap, finalWidth, finalHeight, true)
    }
    
    // Food Categories
    suspend fun getFoodCategories(): List<FoodCategory> {
        return try {
            Log.d("FoodRepository", "Loading categories from Firebase...")
            val result = firestore.collection("categories")
                .orderBy("name")
                .get()
                .await()
            val categories = result.toObjects(FoodCategory::class.java)
            Log.d("FoodRepository", "Successfully loaded ${categories.size} categories from Firebase")
            categories
        } catch (e: Exception) {
            Log.e("FoodRepository", "Error loading categories from Firebase", e)
            emptyList()
        }
    }
    
    suspend fun addSampleCategories(): Boolean {
        return try {
            Log.d("FoodRepository", "Adding sample categories to Firebase...")
            
            val categories = listOf(
                FoodCategory(
                    id = "desserts",
                    name = "🍰 Desserts",
                    imageUrl = "",
                    isActive = true
                ),
                FoodCategory(
                    id = "main-course",
                    name = "🍽️ Main Course",
                    imageUrl = "",
                    isActive = true
                ),
                FoodCategory(
                    id = "beverages",
                    name = "🥤 Beverages",
                    imageUrl = "",
                    isActive = true
                ),
                FoodCategory(
                    id = "pizza",
                    name = "🍕 Pizza",
                    imageUrl = "",
                    isActive = true
                ),
                FoodCategory(
                    id = "snacks",
                    name = "🍿 Snacks",
                    imageUrl = "",
                    isActive = true
                ),
                FoodCategory(
                    id = "appetizers",
                    name = "🥗 Appetizers",
                    imageUrl = "",
                    isActive = true
                )
            )
            
            categories.forEach { category ->
                firestore.collection("categories")
                    .document(category.id)
                    .set(category)
                    .await()
            }
            
            Log.d("FoodRepository", "Successfully added ${categories.size} sample categories")
            true
        } catch (e: Exception) {
            Log.e("FoodRepository", "Error adding sample categories", e)
            false
        }
    }
}

class UserManagementRepository {
    private val firestore = FirebaseFirestore.getInstance()
    
    suspend fun getAllUsers(): List<User> {
        return try {
            Log.d("UserRepository", "Loading users from Firebase...")
            val result = firestore.collection("users")
                .get() // Remove orderBy to avoid missing field errors
                .await()
            val users = result.toObjects(User::class.java)
            Log.d("UserRepository", "Successfully loaded ${users.size} users from Firebase")
            users.forEach { user ->
                Log.d("UserRepository", "User: ${user.getDisplayName()} - ${user.email} - Active: ${user.isActive}")
            }
            // Sort manually by join date
            users.sortedByDescending { it.getJoinDateLong() }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error loading users from Firebase", e)
            emptyList()
        }
    }
    
    suspend fun searchUsers(query: String): List<User> {
        return try {
            val result = firestore.collection("users")
                .orderBy("name")
                .startAt(query)
                .endAt(query + "\uf8ff")
                .get()
                .await()
            result.toObjects(User::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    suspend fun getUserById(userId: String): User? {
        return try {
            val result = firestore.collection("users")
                .document(userId)
                .get()
                .await()
            result.toObject(User::class.java)
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun banUser(userId: String, reason: String = ""): Boolean {
        return try {
            firestore.collection("users")
                .document(userId)
                .update(
                    mapOf(
                        "isActive" to false,
                        "banReason" to reason,
                        "bannedAt" to System.currentTimeMillis()
                    )
                ).await()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun unbanUser(userId: String): Boolean {
        return try {
            firestore.collection("users")
                .document(userId)
                .update(
                    mapOf(
                        "isActive" to true,
                        "banReason" to null,
                        "bannedAt" to null,
                        "unbannedAt" to System.currentTimeMillis()
                    )
                ).await()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun getUserOrders(userId: String): List<Order> {
        return try {
            val result = firestore.collection("orders")
                .whereEqualTo("userId", userId)
                .orderBy("orderDate", Query.Direction.DESCENDING)
                .get()
                .await()
            result.toObjects(Order::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
}
