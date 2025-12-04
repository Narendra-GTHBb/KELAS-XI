package com.apkfood.wavesoffood.repository

import android.util.Log
import com.apkfood.wavesoffood.data.model.Category
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class CategoryRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val categoriesCollection = firestore.collection("categories")

    suspend fun getAllCategories(): List<Category> {
        return try {
            Log.d("CategoryRepository", "🔄 Loading categories from Firebase...")
            
            val snapshot = categoriesCollection.get().await()
            val categories = mutableListOf<Category>()
            
            for (document in snapshot.documents) {
                try {
                    val category = document.toObject(Category::class.java)
                    if (category != null) {
                        // Use document ID if category id is empty
                        val finalCategory = if (category.id.isEmpty()) {
                            category.copy(id = document.id)
                        } else {
                            category
                        }
                        categories.add(finalCategory)
                        Log.d("CategoryRepository", "✅ Loaded category: ${finalCategory.name} (${finalCategory.id})")
                    }
                } catch (e: Exception) {
                    Log.e("CategoryRepository", "❌ Error parsing category document: ${document.id}", e)
                }
            }
            
            Log.d("CategoryRepository", "✅ Successfully loaded ${categories.size} categories from Firebase")
            categories.sortedBy { it.sortOrder }
            
        } catch (e: Exception) {
            Log.e("CategoryRepository", "❌ Error loading categories from Firebase: ${e.message}", e)
            emptyList()
        }
    }
}