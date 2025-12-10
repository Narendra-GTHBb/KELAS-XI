package com.apkfood.wavesoffood.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.apkfood.wavesoffood.data.model.Food
import kotlinx.coroutines.tasks.await

/**
 * Repository untuk mengambil data makanan dari Firebase
 */
class FoodRepository {
    private val firestore = FirebaseFirestore.getInstance()
    
    /**
     * Mengambil semua makanan dari Firebase
     */
    suspend fun getAllFoods(): List<Food> {
        return try {
            Log.d("FoodRepository", "🔄 USER: Loading foods from Firebase...")
            val result = firestore.collection("foods")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val foods = result.documents.mapNotNull { document ->
                try {
                    val food = document.toObject(Food::class.java)
                    val foodWithId = food?.copy(id = document.id)
                    
                    // Debug: Log the categoryId for each food
                    if (foodWithId != null) {
                        Log.d("FoodRepository", "🍽️ USER Food loaded: ${foodWithId.name} - CategoryId: '${foodWithId.categoryId}' - Document: ${document.id}")
                    }
                    
                    foodWithId
                } catch (e: Exception) {
                    Log.e("FoodRepository", "❌ USER: Error parsing food document: ${document.id}", e)
                    null
                }
            }
            
            Log.d("FoodRepository", "✅ USER: Successfully loaded ${foods.size} foods from Firebase")
            foods.forEach { food ->
                Log.d("FoodRepository", "🍽️ USER Food: ${food.name} - Rp ${food.price}")
            }
            
            foods
        } catch (e: Exception) {
            Log.e("FoodRepository", "Error loading foods from Firebase", e)
            emptyList()
        }
    }
    
    /**
     * Mengambil makanan berdasarkan kategori
     */
    suspend fun getFoodsByCategory(categoryName: String): List<Food> {
        return try {
            val result = firestore.collection("foods")
                .whereEqualTo("categoryName", categoryName)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val foods = result.documents.mapNotNull { document ->
                try {
                    val food = document.toObject(Food::class.java)
                    food?.copy(id = document.id)
                } catch (e: Exception) {
                    Log.e("FoodRepository", "Error parsing food document: ${document.id}", e)
                    null
                }
            }
            
            Log.d("FoodRepository", "Loaded ${foods.size} foods for category: $categoryName")
            foods
        } catch (e: Exception) {
            Log.e("FoodRepository", "Error loading foods for category: $categoryName", e)
            emptyList()
        }
    }
    
    /**
     * Mengambil makanan populer (rating >= 4.3)
     */
    suspend fun getPopularFoods(): List<Food> {
        return try {
            val allFoods = getAllFoods()
            allFoods.filter { it.rating >= 4.3 || it.isPopular }
        } catch (e: Exception) {
            Log.e("FoodRepository", "Error loading popular foods", e)
            emptyList()
        }
    }
    
    /**
     * Mengambil makanan berdasarkan ID
     */
    suspend fun getFoodById(foodId: String): Food? {
        return try {
            val result = firestore.collection("foods")
                .document(foodId)
                .get()
                .await()
            
            if (result.exists()) {
                val food = result.toObject(Food::class.java)
                food?.copy(id = result.id)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("FoodRepository", "Error loading food by ID: $foodId", e)
            null
        }
    }
    
    /**
     * Search makanan berdasarkan nama atau deskripsi
     */
    suspend fun searchFoods(query: String): List<Food> {
        return try {
            val allFoods = getAllFoods()
            val lowercaseQuery = query.lowercase()
            
            allFoods.filter { food ->
                food.name.lowercase().contains(lowercaseQuery) ||
                food.description.lowercase().contains(lowercaseQuery) ||
                food.categoryName.lowercase().contains(lowercaseQuery)
            }
        } catch (e: Exception) {
            Log.e("FoodRepository", "Error searching foods", e)
            emptyList()
        }
    }
    
    /**
     * Mengambil semua kategori yang tersedia
     */
    suspend fun getCategories(): List<String> {
        return try {
            val allFoods = getAllFoods()
            val categories = allFoods.map { it.categoryName }.distinct().sorted()
            Log.d("FoodRepository", "Found categories: $categories")
            categories
        } catch (e: Exception) {
            Log.e("FoodRepository", "Error loading categories", e)
            emptyList()
        }
    }
}