package com.apkfood.wavesoffood.firebase

import com.apkfood.wavesoffood.data.model.Food
import com.google.firebase.firestore.FirebaseFirestore

/**
 * FirebaseManager - Simplified version for testing
 * Mengelola operasi Firebase untuk aplikasi Waves of Food
 */
class FirebaseManager {
    private val firestore = FirebaseFirestore.getInstance()
    
    /**
     * Mengambil semua data makanan dari Firestore
     */
    fun getFoods(onComplete: (List<Food>) -> Unit) {
        firestore.collection("foods")
            .get()
            .addOnSuccessListener { documents ->
                val foods = documents.mapNotNull { document ->
                    try {
                        document.toObject(Food::class.java).copy(id = document.id)
                    } catch (e: Exception) {
                        null
                    }
                }
                onComplete(foods)
            }
            .addOnFailureListener {
                onComplete(emptyList())
            }
    }
    
    /**
     * Mengambil kategori makanan
     */
    fun getCategories(onComplete: (List<String>) -> Unit) {
        firestore.collection("foods")
            .get()
            .addOnSuccessListener { documents ->
                val categories = documents.mapNotNull { document ->
                    document.getString("category")
                }.distinct()
                onComplete(categories)
            }
            .addOnFailureListener {
                onComplete(emptyList())
            }
    }
}
