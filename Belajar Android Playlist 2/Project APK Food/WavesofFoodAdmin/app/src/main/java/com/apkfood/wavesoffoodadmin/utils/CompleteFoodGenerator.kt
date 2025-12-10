package com.apkfood.wavesoffoodadmin.utils

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.apkfood.wavesoffoodadmin.model.Food
import kotlinx.coroutines.tasks.await

/**
 * Complete Food Generator dengan gambar Base64 yang sesuai
 */
class CompleteFoodGenerator {
    
    private val firestore = FirebaseFirestore.getInstance()
    
    /**
     * Generate complete set of foods dengan Base64 images yang matching
     */
    suspend fun generateCompleteFoodsWithImages(): Boolean {
        return try {
            Log.d("CompleteFoodGenerator", "🚀 Generating complete foods with matching images...")
            
            // Hapus existing foods
            clearExistingFoods()
            
            // Add complete foods set
            val completeFoods = getCompleteFoodsWithMatchingImages()
            
            completeFoods.forEach { food ->
                val docRef = firestore.collection("foods").document()
                val foodWithId = food.copy(id = docRef.id)
                docRef.set(foodWithId).await()
                Log.d("CompleteFoodGenerator", "✅ Added: ${food.name}")
            }
            
            Log.d("CompleteFoodGenerator", "🎉 Generated ${completeFoods.size} complete foods!")
            true
        } catch (e: Exception) {
            Log.e("CompleteFoodGenerator", "❌ Failed to generate complete foods", e)
            false
        }
    }
    
    private suspend fun clearExistingFoods() {
        try {
            val existing = firestore.collection("foods").get().await()
            existing.documents.forEach { doc ->
                doc.reference.delete().await()
            }
            Log.d("CompleteFoodGenerator", "🗑️ Cleared ${existing.size()} existing foods")
        } catch (e: Exception) {
            Log.e("CompleteFoodGenerator", "❌ Failed to clear existing foods", e)
        }
    }
    
    private fun getCompleteFoodsWithMatchingImages(): List<Food> {
        return listOf(
            // Pizza
            Food(
                name = "Pizza Margherita",
                description = "Classic pizza with fresh tomatoes, mozzarella cheese, and basil",
                price = 75000.0,
                imageUrl = "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAYEBQYFBAYGBQYHBwYIChAKCgkJChQODwwQFxQYGBcUFhYaHSUfGhsjHBYWICwgIyYnKSopGR8tMC0oMCUoKSj/2wBDAQcHBwoIChMKChMoGhYaKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCj/wAARCAAoACgDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwD9U6KKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKAP/2Q==",
                categoryId = "pizza",
                ingredients = listOf("tomato sauce", "mozzarella", "basil", "olive oil"),
                isAvailable = true,
                isPopular = true,
                preparationTime = 20,
                rating = 4.5,
                createdAt = System.currentTimeMillis()
            ),
            
            Food(
                name = "Pizza Pepperoni",
                description = "Delicious pizza topped with pepperoni and cheese",
                price = 85000.0,
                imageUrl = "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAYEBQYFBAYGBQYHBwYIChAKCgkJChQODwwQFxQYGBcUFhYaHSUfGhsjHBYWICwgIyYnKSopGR8tMC0oMCUoKSj/2wBDAQcHBwoIChMKChMoGhYaKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCj/wAARCAAoACgDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwD9U6KKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKAP/2Q==",
                categoryId = "pizza",
                ingredients = listOf("tomato sauce", "mozzarella", "pepperoni"),
                isAvailable = true,
                isPopular = true,
                preparationTime = 22,
                rating = 4.4,
                createdAt = System.currentTimeMillis()
            ),
            
            // Chocolate Cake - matching user app
            Food(
                name = "Chocolate Cake",
                description = "Rich, moist chocolate layer cake with chocolate frosting",
                price = 35000.0,
                imageUrl = "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAYEBQYFBAYGBQYHBwYIChAKCgkJChQODwwQFxQYGBcUFhYaHSUfGhsjHBYWICwgIyYnKSopGR8tMC0oMCUoKSj/2wBDAQcHBwoIChMKChMoGhYaKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCj/wAARCAAoACgDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwD9U6KKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKAP/2Q==",
                categoryId = "desserts",
                ingredients = listOf("chocolate", "flour", "eggs", "sugar", "butter"),
                isAvailable = true,
                isPopular = true,
                preparationTime = 45,
                rating = 4.8,
                createdAt = System.currentTimeMillis()
            ),
            
            // Nasi Padang - matching user app
            Food(
                name = "Nasi Padang",
                description = "Traditional Indonesian rice dish with rendang and vegetables",
                price = 35000.0,
                imageUrl = "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAYEBQYFBAYGBQYHBwYIChAKCgkJChQODwwQFxQYGBcUFhYaHSUfGhsjHBYWICwgIyYnKSopGR8tMC0oMCUoKSj/2wBDAQcHBwoIChMKChMoGhYaKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCj/wAARCAAoACgDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwD9U6KKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKAP/2Q==",
                categoryId = "main-course",
                ingredients = listOf("rice", "rendang", "vegetables", "sambal"),
                isAvailable = true,
                isPopular = true,
                preparationTime = 30,
                rating = 4.6,
                createdAt = System.currentTimeMillis()
            ),
            
            // Spaghetti Carbonara - matching admin app
            Food(
                name = "Spaghetti Carbonara",
                description = "Pasta dengan sauce creamy bacon dan parmesan cheese",
                price = 65000.0,
                imageUrl = "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAYEBQYFBAYGBQYHBwYIChAKCgkJChQODwwQFxQYGBcUFhYaHSUfGhsjHBYWICwgIyYnKSopGR8tMC0oMCUoKSj/2wBDAQcHBwoIChMKChMoGhYaKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCj/wAARCAAoACgDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwD9U6KKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKAP/2Q==",
                categoryId = "main-course",
                ingredients = listOf("pasta", "eggs", "bacon", "parmesan", "black pepper"),
                isAvailable = true,
                isPopular = true,
                preparationTime = 25,
                rating = 4.3,
                createdAt = System.currentTimeMillis()
            ),
            
            // Nasi Gudeg - matching user app
            Food(
                name = "Nasi Gudeg",
                description = "Traditional Yogyakarta sweet jackfruit curry with rice",
                price = 25000.0,
                imageUrl = "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAYEBQYFBAYGBQYHBwYIChAKCgkJChQODwwQFxQYGBcUFhYaHSUfGhsjHBYWICwgIyYnKSopGR8tMC0oMCUoKSj/2wBDAQcHBwoIChMKChMoGhYaKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCj/wAARCAAoACgDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAxQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwD9U6KKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKAP/2Q==",
                categoryId = "main-course",
                ingredients = listOf("rice", "jackfruit", "coconut milk", "spices"),
                isAvailable = true,
                isPopular = true,
                preparationTime = 120,
                rating = 4.7,
                createdAt = System.currentTimeMillis()
            ),
            
            // Fresh Orange Juice
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
            
            // Vanilla Ice Cream
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
            
            // Hot Green Tea
            Food(
                name = "Hot Green Tea",
                description = "Traditional Japanese green tea",
                price = 15000.0,
                imageUrl = "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAYEBQYFBAYGBQYHBwYIChAKCgkJChQODwwQFxQYGBcUFhYaHSUfGhsjHBYWICwgIyYnKSopGR8tMC0oMCUoKSj/2wBDAQcHBwoIChMKChMoGhYaKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCj/wAARCAAoACgDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwD9U6KKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKAP/2Q==",
                categoryId = "beverages",
                ingredients = listOf("green tea leaves", "hot water"),
                isAvailable = true,
                isPopular = false,
                preparationTime = 5,
                rating = 4.1,
                createdAt = System.currentTimeMillis()
            ),
            
            // Crispy Fried Chicken
            Food(
                name = "Crispy Fried Chicken",
                description = "Golden crispy fried chicken with mayo and vegetables",
                price = 55000.0,
                imageUrl = "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAYEBQYFBAYGBQYHBwYIChAKCgkJChQODwwQFxQYGBcUFhYaHSUfGhsjHBYWICwgIyYnKSopGR8tMC0oMCUoKSj/2wBDAQcHBwoIChMKChMoGhYaKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCj/wAARCAAoACgDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwD9U6KKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKAP/2Q==",
                categoryId = "main-course",
                ingredients = listOf("chicken", "flour", "spices", "mayonnaise"),
                isAvailable = true,
                isPopular = true,
                preparationTime = 20,
                rating = 4.5,
                createdAt = System.currentTimeMillis()
            )
        )
    }
}