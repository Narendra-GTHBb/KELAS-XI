package com.apkfood.wavesoffoodadmin.test

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.apkfood.wavesoffoodadmin.model.Food
import kotlinx.coroutines.tasks.await

/**
 * Test utility untuk memverifikasi sistem Base64 images
 */
class ImageSystemTest {
    
    private val firestore = FirebaseFirestore.getInstance()
    
    /**
     * Test 1: Verify Firebase connection
     */
    suspend fun testFirebaseConnection(): Boolean {
        return try {
            Log.d("ImageSystemTest", "🔍 Testing Firebase connection...")
            val result = firestore.collection("foods").limit(1).get().await()
            Log.d("ImageSystemTest", "✅ Firebase connection successful")
            true
        } catch (e: Exception) {
            Log.e("ImageSystemTest", "❌ Firebase connection failed", e)
            false
        }
    }
    
    /**
     * Test 2: Add a single sample food with Base64 image
     */
    suspend fun testAddSampleFood(): Boolean {
        return try {
            Log.d("ImageSystemTest", "🔍 Testing sample food addition...")
            
            val testFood = Food(
                name = "🍕 Test Pizza Base64",
                description = "Test pizza with Base64 encoded image for testing purposes",
                price = 50000.0,
                imageUrl = "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAYEBQYFBAYGBQYHBwYIChAKCgkJChQODwwQFxQYGBcUFhYaHSUfGhsjHBYWICwgIyYnKSopGR8tMC0oMCUoKSj/2wBDAQcHBwoIChMKChMoGhYaKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCj/wAARCAAoACgDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwD9U6KKKACiiigAooooAKKKKACiiigAooooAK4jxp8SfDfg6TV9E1LxBp99q1jdaZLHZQXKTyFZJVQOqqxJwrE57V21FAHGj4geDyMf8JRoR9v3i/8As1L/AMJd4P8Aiz4e/wDBhD/hXRUUAdH/AMJd4P8Aiz4e/wDBhD/hR/wl3g/4s+Hv/BhD/hXRUUAdH/wl3g/4s+Hv/BhD/hR/wl3g/wCLPh7/AMGEP+FdFRQB0f8Awl3g/wCLPh7/AMGEP+FH/CXeD/iz4e/8GEP+FdFRQB0f/CXeD/iz4e/8GEP+FH/CXeD/AIs+Hv8AwYQ/4V0VFAHRX/jXwfpEzQ3+vaBaTr1jlvoY2H4E4NOPxK8FLxJ4s8OH/uJ1r+ivx8n/wCEe8ZeAWOYrDWNUsAexpbu9n/8kf9aRY+NPBl6gltfGfhm6jPR4b6Fx/47QB6RRXjmp/GTwNp0rJe+JPDtuzj/Y+Lq3jb/x4f1r3uj/AKKK6P8A2",
                categoryId = "pizza",
                ingredients = listOf("test cheese", "test sauce"),
                isAvailable = true,
                isPopular = false,
                preparationTime = 15,
                rating = 4.0,
                createdAt = System.currentTimeMillis()
            )
            
            val docRef = firestore.collection("foods").document()
            val foodWithId = testFood.copy(id = docRef.id)
            docRef.set(foodWithId).await()
            
            Log.d("ImageSystemTest", "✅ Sample food added successfully with ID: ${docRef.id}")
            true
        } catch (e: Exception) {
            Log.e("ImageSystemTest", "❌ Failed to add sample food", e)
            false
        }
    }
    
    /**
     * Test 3: Retrieve foods and verify Base64 images
     */
    suspend fun testRetrieveFoodsWithImages(): Boolean {
        return try {
            Log.d("ImageSystemTest", "🔍 Testing food retrieval...")
            
            val result = firestore.collection("foods")
                .limit(10)
                .get()
                .await()
            
            val foods = result.toObjects(Food::class.java)
            Log.d("ImageSystemTest", "📊 Retrieved ${foods.size} foods from Firebase")
            
            var base64ImageCount = 0
            foods.forEach { food ->
                if (food.imageUrl.startsWith("data:image")) {
                    base64ImageCount++
                    Log.d("ImageSystemTest", "✅ Found Base64 image: ${food.name}")
                } else {
                    Log.d("ImageSystemTest", "❌ No Base64 image: ${food.name} - ${food.imageUrl}")
                }
            }
            
            Log.d("ImageSystemTest", "📊 Foods with Base64 images: $base64ImageCount/${foods.size}")
            
            if (base64ImageCount > 0) {
                Log.d("ImageSystemTest", "✅ Base64 image system working!")
                true
            } else {
                Log.w("ImageSystemTest", "⚠️ No Base64 images found")
                false
            }
            
        } catch (e: Exception) {
            Log.e("ImageSystemTest", "❌ Failed to retrieve foods", e)
            false
        }
    }
    
    /**
     * Test 4: Clean up test data
     */
    suspend fun cleanupTestData(): Boolean {
        return try {
            Log.d("ImageSystemTest", "🧹 Cleaning up test data...")
            
            val result = firestore.collection("foods")
                .whereEqualTo("name", "🍕 Test Pizza Base64")
                .get()
                .await()
            
            for (document in result.documents) {
                document.reference.delete().await()
                Log.d("ImageSystemTest", "🗑️ Deleted test food: ${document.id}")
            }
            
            Log.d("ImageSystemTest", "✅ Cleanup completed")
            true
        } catch (e: Exception) {
            Log.e("ImageSystemTest", "❌ Cleanup failed", e)
            false
        }
    }
    
    /**
     * Run all tests
     */
    suspend fun runAllTests(): Boolean {
        Log.d("ImageSystemTest", "🚀 Starting Image System Tests...")
        
        var allTestsPassed = true
        
        // Test 1: Firebase connection
        if (!testFirebaseConnection()) {
            allTestsPassed = false
        }
        
        // Test 2: Add sample food
        if (!testAddSampleFood()) {
            allTestsPassed = false
        }
        
        // Test 3: Retrieve and verify
        if (!testRetrieveFoodsWithImages()) {
            allTestsPassed = false
        }
        
        // Test 4: Cleanup
        cleanupTestData()
        
        if (allTestsPassed) {
            Log.d("ImageSystemTest", "🎉 ALL TESTS PASSED - Image system is working!")
        } else {
            Log.e("ImageSystemTest", "❌ SOME TESTS FAILED - Check logs for details")
        }
        
        return allTestsPassed
    }
}