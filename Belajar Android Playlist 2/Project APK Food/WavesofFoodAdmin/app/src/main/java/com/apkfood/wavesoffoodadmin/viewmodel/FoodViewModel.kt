package com.apkfood.wavesoffoodadmin.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apkfood.wavesoffoodadmin.model.Food
import com.apkfood.wavesoffoodadmin.model.FoodCategory
import com.apkfood.wavesoffoodadmin.repository.FoodRepository
import kotlinx.coroutines.launch
import android.net.Uri
import android.content.Context

class FoodViewModel : ViewModel() {
    private val repository = FoodRepository()
    
    private val _foods = MutableLiveData<List<Food>>()
    val foods: LiveData<List<Food>> = _foods
    
    private val _categories = MutableLiveData<List<FoodCategory>>()
    val categories: LiveData<List<FoodCategory>> = _categories
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error
    
    private val _operationResult = MutableLiveData<Boolean>()
    val operationResult: LiveData<Boolean> = _operationResult
    
    private val _base64Image = MutableLiveData<String>()
    val base64Image: LiveData<String> = _base64Image
    
    init {
        // Load foods automatically when ViewModel is created
        loadFoods()
    }
    
    fun loadFoods() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val foodsList = repository.getAllFoods()
                _foods.value = foodsList
            } catch (e: Exception) {
                _error.value = e.message ?: "Error loading foods"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun loadCategories() {
        viewModelScope.launch {
            try {
                val categoriesList = repository.getFoodCategories()
                if (categoriesList.isEmpty()) {
                    // If no categories found, add sample categories first
                    val added = repository.addSampleCategories()
                    if (added) {
                        // Reload categories after adding samples
                        val newCategoriesList = repository.getFoodCategories()
                        _categories.value = newCategoriesList
                    } else {
                        _error.value = "Failed to add sample categories"
                    }
                } else {
                    _categories.value = categoriesList
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Error loading categories"
            }
        }
    }
    
    fun addFood(food: Food) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.addFood(food)
                _operationResult.value = result
                if (result) {
                    loadFoods() // Refresh list
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Error adding food"
                _operationResult.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun updateFood(food: Food) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.updateFood(food)
                _operationResult.value = result
                if (result) {
                    loadFoods() // Refresh list
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Error updating food"
                _operationResult.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun deleteFood(foodId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.deleteFood(foodId)
                _operationResult.value = result
                if (result) {
                    loadFoods() // Refresh list
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Error deleting food"
                _operationResult.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun addSampleFoodsWithImages() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.addSampleFoodsWithImages()
                _operationResult.value = result
                if (result) {
                    loadFoods() // Refresh list
                    _error.value = "Sample foods with images added successfully!"
                } else {
                    _error.value = "Failed to add sample foods"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Error adding sample foods"
                _operationResult.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun convertImageToBase64(imageUri: Uri, context: Context) {
        viewModelScope.launch {
            try {
                val base64String = repository.convertImageToBase64(imageUri, context)
                base64String?.let {
                    _base64Image.value = it
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Error converting image"
            }
        }
    }
}
