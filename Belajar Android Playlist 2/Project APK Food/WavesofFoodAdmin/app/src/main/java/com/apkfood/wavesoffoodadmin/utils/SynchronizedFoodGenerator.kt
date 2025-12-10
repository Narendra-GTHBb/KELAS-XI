package com.apkfood.wavesoffoodadmin.utils

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.apkfood.wavesoffoodadmin.model.Food
import com.apkfood.wavesoffoodadmin.data.SynchronizedImages
import kotlinx.coroutines.tasks.await

/**
 * SYNCHRONIZED Food Generator - menggunakan gambar yang PERSIS SAMA dengan user app
 */
class SynchronizedFoodGenerator {
    
    private val firestore = FirebaseFirestore.getInstance()
    
    /**
     * Generate foods dengan gambar Base64 yang PERSIS SAMA dengan user app
     */
    suspend fun generateSynchronizedFoods(): Boolean {
        return try {
            Log.d("SynchronizedFoodGenerator", "🎯 Generating SYNCHRONIZED foods...")
            
            // Hapus existing foods
            clearExistingFoods()
            
            // Add synchronized foods
            val synchronizedFoods = createSynchronizedFoods()
            
            synchronizedFoods.forEach { food ->
                val docRef = firestore.collection("foods").document()
                val foodWithId = food.copy(id = docRef.id)
                docRef.set(foodWithId).await()
                Log.d("SynchronizedFoodGenerator", "✅ Added SYNCHRONIZED: ${food.name}")
            }
            
            Log.d("SynchronizedFoodGenerator", "🎉 Generated ${synchronizedFoods.size} SYNCHRONIZED foods!")
            true
        } catch (e: Exception) {
            Log.e("SynchronizedFoodGenerator", "❌ Failed to generate synchronized foods", e)
            false
        }
    }
    
    private suspend fun clearExistingFoods() {
        try {
            val existing = firestore.collection("foods").get().await()
            existing.documents.forEach { doc ->
                doc.reference.delete().await()
            }
            Log.d("SynchronizedFoodGenerator", "🗑️ Cleared ${existing.size()} existing foods")
        } catch (e: Exception) {
            Log.e("SynchronizedFoodGenerator", "❌ Failed to clear existing foods", e)
        }
    }
    
    private fun createSynchronizedFoods(): List<Food> {
        val synchronizedData = SynchronizedImages.getSynchronizedFoodData()
        val prices = listOf(35000.0, 35000.0, 65000.0, 25000.0, 18000.0, 22000.0, 15000.0, 55000.0, 75000.0, 85000.0)
        val categories = listOf("desserts", "main-course", "main-course", "main-course", "beverages", "desserts", "beverages", "main-course", "pizza", "pizza")
        val ratings = listOf(4.8, 4.6, 4.3, 4.7, 4.1, 4.2, 4.1, 4.5, 4.5, 4.4)
        val prepTimes = listOf(45, 30, 25, 120, 5, 3, 5, 20, 20, 22)
        
        return synchronizedData.mapIndexed { index, (name, description, imageUrl) ->
            Food(
                name = name,
                description = description,
                price = prices[index],
                imageUrl = imageUrl, // Gambar PERSIS SAMA dengan user app
                categoryId = categories[index],
                ingredients = getIngredients(name),
                isAvailable = true,
                isPopular = index < 5, // 5 makanan pertama popular
                preparationTime = prepTimes[index],
                rating = ratings[index],
                createdAt = System.currentTimeMillis()
            )
        }
    }
    
    private fun getIngredients(name: String): List<String> {
        return when (name) {
            "Chocolate Cake" -> listOf("chocolate", "flour", "eggs", "sugar", "butter")
            "Nasi Padang" -> listOf("rice", "rendang", "vegetables", "sambal")
            "Spaghetti Carbonara" -> listOf("pasta", "eggs", "bacon", "parmesan", "black pepper")
            "Nasi Gudeg" -> listOf("rice", "jackfruit", "coconut milk", "spices")
            "Fresh Orange Juice" -> listOf("fresh orange")
            "Vanilla Ice Cream" -> listOf("vanilla ice cream", "chocolate sauce")
            "Hot Green Tea" -> listOf("green tea leaves", "hot water")
            "Crispy Fried Chicken" -> listOf("chicken", "flour", "spices", "mayonnaise")
            "Pizza Margherita" -> listOf("tomato sauce", "mozzarella", "basil", "olive oil")
            "Pizza Pepperoni" -> listOf("tomato sauce", "mozzarella", "pepperoni")
            else -> emptyList()
        }
    }
}