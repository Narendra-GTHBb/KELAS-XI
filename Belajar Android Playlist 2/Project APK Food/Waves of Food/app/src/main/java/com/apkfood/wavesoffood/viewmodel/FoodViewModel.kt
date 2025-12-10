package com.apkfood.wavesoffood.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apkfood.wavesoffood.data.model.Food
import com.apkfood.wavesoffood.repository.FoodRepository
import kotlinx.coroutines.launch

/**
 * ViewModel untuk mengelola data makanan di user app
 */
class FoodViewModel : ViewModel() {
    
    private val repository = FoodRepository()
    
    // Foods list
    private val _foods = MutableLiveData<List<Food>>()
    val foods: LiveData<List<Food>> = _foods
    
    // Popular foods
    private val _popularFoods = MutableLiveData<List<Food>>()
    val popularFoods: LiveData<List<Food>> = _popularFoods
    
    // Categories
    private val _categories = MutableLiveData<List<String>>()
    val categories: LiveData<List<String>> = _categories
    
    // Loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    // Error state
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    /**
     * Load all foods from Firebase
     */
    fun loadFoods() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val foodsList = repository.getAllFoods()
                _foods.value = foodsList
                
                // Also load popular foods
                val popularList = foodsList.filter { it.rating >= 4.3 || it.isPopular }
                _popularFoods.value = popularList
                
            } catch (e: Exception) {
                _error.value = e.message ?: "Error loading foods"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Load foods by category
     */
    fun loadFoodsByCategory(categoryName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val foodsList = if (categoryName == "All") {
                    repository.getAllFoods()
                } else {
                    repository.getFoodsByCategory(categoryName)
                }
                _foods.value = foodsList
                
            } catch (e: Exception) {
                _error.value = e.message ?: "Error loading foods by category"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Load popular foods
     */
    fun loadPopularFoods() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val popularList = repository.getPopularFoods()
                _popularFoods.value = popularList
                
            } catch (e: Exception) {
                _error.value = e.message ?: "Error loading popular foods"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Load categories
     */
    fun loadCategories() {
        viewModelScope.launch {
            try {
                val categoriesList = repository.getCategories()
                _categories.value = categoriesList
                
            } catch (e: Exception) {
                _error.value = e.message ?: "Error loading categories"
            }
        }
    }
    
    /**
     * Search foods
     */
    fun searchFoods(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val searchResults = if (query.isBlank()) {
                    repository.getAllFoods()
                } else {
                    repository.searchFoods(query)
                }
                _foods.value = searchResults
                
            } catch (e: Exception) {
                _error.value = e.message ?: "Error searching foods"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Get food by ID
     */
    fun getFoodById(foodId: String, callback: (Food?) -> Unit) {
        viewModelScope.launch {
            try {
                val food = repository.getFoodById(foodId)
                callback(food)
                
            } catch (e: Exception) {
                _error.value = e.message ?: "Error loading food details"
                callback(null)
            }
        }
    }
    
    /**
     * Refresh all data
     */
    fun refreshData() {
        loadFoods()
        loadCategories()
    }
}