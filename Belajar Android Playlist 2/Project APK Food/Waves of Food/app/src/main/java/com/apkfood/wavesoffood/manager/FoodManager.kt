package com.apkfood.wavesoffood.manager

import com.apkfood.wavesoffood.data.model.Category
import com.apkfood.wavesoffood.data.model.Food
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Manager untuk menghandle data Food dan Category dari Firebase
 */
class FoodManager {
    
    private val firestore = FirebaseFirestore.getInstance()
    
    companion object {
        private const val COLLECTION_CATEGORIES = "categories"
        private const val COLLECTION_FOODS = "foods"
    }
    
    /**
     * Mengambil daftar kategori dari Firebase
     */
    fun getCategories(
        onSuccess: (List<Category>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        try {
            firestore.collection(COLLECTION_CATEGORIES)
                .whereEqualTo("isActive", true)
                .get()
                .addOnSuccessListener { documents ->
                    try {
                        val categories = documents.mapNotNull { doc ->
                            try {
                                doc.toObject(Category::class.java).copy(id = doc.id)
                            } catch (e: Exception) {
                                null
                            }
                        }.sortedBy { it.sortOrder }
                        onSuccess(categories)
                    } catch (e: Exception) {
                        onFailure("Error parsing categories: ${e.message}")
                    }
                }
                .addOnFailureListener { exception ->
                    onFailure(exception.message ?: "Failed to load categories")
                }
        } catch (e: Exception) {
            onFailure("Error initializing categories: ${e.message}")
        }
    }
    
    /**
     * Mengambil daftar makanan populer
     */
    fun getPopularFoods(
        onSuccess: (List<Food>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        try {
            firestore.collection(COLLECTION_FOODS)
                .whereEqualTo("isAvailable", true)
                .whereEqualTo("isPopular", true)
                .limit(10)
                .get()
                .addOnSuccessListener { documents ->
                    try {
                        val foods = documents.mapNotNull { doc ->
                            try {
                                doc.toObject(Food::class.java).copy(id = doc.id)
                            } catch (e: Exception) {
                                null
                            }
                        }.sortedByDescending { it.rating }
                        onSuccess(foods)
                    } catch (e: Exception) {
                        onFailure("Error parsing popular foods: ${e.message}")
                    }
                }
                .addOnFailureListener { exception ->
                    onFailure(exception.message ?: "Failed to load popular foods")
                }
        } catch (e: Exception) {
            onFailure("Error initializing popular foods: ${e.message}")
        }
    }
    
    /**
     * Mengambil daftar makanan yang direkomendasikan
     */
    fun getRecommendedFoods(
        onSuccess: (List<Food>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        try {
            firestore.collection(COLLECTION_FOODS)
                .whereEqualTo("isAvailable", true)
                .whereEqualTo("isRecommended", true)
                .limit(20)
                .get()
                .addOnSuccessListener { documents ->
                    try {
                        val foods = documents.mapNotNull { doc ->
                            try {
                                doc.toObject(Food::class.java).copy(id = doc.id)
                            } catch (e: Exception) {
                                null
                            }
                        }.sortedByDescending { it.rating }
                        onSuccess(foods)
                    } catch (e: Exception) {
                        onFailure("Error parsing recommended foods: ${e.message}")
                    }
                }
                .addOnFailureListener { exception ->
                    onFailure(exception.message ?: "Failed to load recommended foods")
                }
        } catch (e: Exception) {
            onFailure("Error initializing recommended foods: ${e.message}")
        }
    }
    
    /**
     * Mengambil makanan berdasarkan kategori
     */
    fun getFoodsByCategory(
        categoryId: String,
        onSuccess: (List<Food>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        firestore.collection(COLLECTION_FOODS)
            .whereEqualTo("isAvailable", true)
            .whereEqualTo("categoryId", categoryId)
            .orderBy("name")
            .get()
            .addOnSuccessListener { documents ->
                val foods = documents.mapNotNull { doc ->
                    try {
                        doc.toObject(Food::class.java).copy(id = doc.id)
                    } catch (e: Exception) {
                        null
                    }
                }
                onSuccess(foods)
            }
            .addOnFailureListener { exception ->
                onFailure(exception.message ?: "Failed to load foods by category")
            }
    }
    
    /**
     * Search makanan berdasarkan nama
     */
    fun searchFoods(
        query: String,
        onSuccess: (List<Food>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        try {
            firestore.collection(COLLECTION_FOODS)
                .whereEqualTo("isAvailable", true)
                .get()
                .addOnSuccessListener { documents ->
                    try {
                        val foods = documents.mapNotNull { doc ->
                            try {
                                doc.toObject(Food::class.java).copy(id = doc.id)
                            } catch (e: Exception) {
                                null
                            }
                        }.filter { food ->
                            food.name.contains(query, ignoreCase = true) ||
                            food.description.contains(query, ignoreCase = true) ||
                            food.categoryName.contains(query, ignoreCase = true)
                        }
                        onSuccess(foods)
                    } catch (e: Exception) {
                        onFailure("Error searching foods: ${e.message}")
                    }
                }
                .addOnFailureListener { exception ->
                    onFailure(exception.message ?: "Failed to search foods")
                }
        } catch (e: Exception) {
            onFailure("Error initializing search: ${e.message}")
        }
    }
    
    /**
     * Mengambil detail makanan berdasarkan ID
     */
    fun getFoodById(
        foodId: String,
        onSuccess: (Food?) -> Unit,
        onFailure: (String) -> Unit
    ) {
        firestore.collection(COLLECTION_FOODS)
            .document(foodId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    try {
                        val food = document.toObject(Food::class.java)?.copy(id = document.id)
                        onSuccess(food)
                    } catch (e: Exception) {
                        onFailure("Failed to parse food data")
                    }
                } else {
                    onSuccess(null)
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception.message ?: "Failed to load food details")
            }
    }
}
