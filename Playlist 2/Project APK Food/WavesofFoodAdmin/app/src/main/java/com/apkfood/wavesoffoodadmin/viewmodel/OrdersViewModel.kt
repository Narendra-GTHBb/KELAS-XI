package com.apkfood.wavesoffoodadmin.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apkfood.wavesoffoodadmin.model.Order
import com.apkfood.wavesoffoodadmin.model.OrderStatus
import com.apkfood.wavesoffoodadmin.repository.AdminRepository
import kotlinx.coroutines.launch

class OrdersViewModel : ViewModel() {
    private val repository = AdminRepository()
    
    private val _orders = MutableLiveData<List<Order>>()
    val orders: LiveData<List<Order>> = _orders
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error
    
    init {
        Log.d("OrdersViewModel", "🚀 OrdersViewModel initialized, starting loadOrders")
        loadOrders()
        debugFirebase()
    }
    
    private fun debugFirebase() {
        viewModelScope.launch {
            try {
                Log.d("OrdersViewModel", "🔍 Starting Firebase debug...")
                val debugResult = repository.debugFirebaseOrders()
                Log.d("OrdersViewModel", "🔍 DEBUG COMPLETE: $debugResult")
            } catch (e: Exception) {
                Log.e("OrdersViewModel", "❌ Debug failed", e)
            }
        }
    }
    
    fun loadOrders() {
        Log.d("OrdersViewModel", "🔄 Starting loadOrders")
        _isLoading.value = true
        viewModelScope.launch {
            try {
                Log.d("OrdersViewModel", "📥 Calling repository.getAllOrders()")
                val ordersList = repository.getAllOrders()
                Log.d("OrdersViewModel", "✅ Repository returned ${ordersList.size} orders")
                ordersList.forEachIndexed { index, order ->
                    Log.d("OrdersViewModel", "📦 Order $index: ${order.orderNumber} - ${order.status} - ${order.userName}")
                }
                _orders.value = ordersList
                _isLoading.value = false
                Log.d("OrdersViewModel", "✅ Orders successfully set to LiveData")
            } catch (e: Exception) {
                Log.e("OrdersViewModel", "❌ Error loading orders", e)
                _error.value = "Error loading orders: ${e.message}"
                _isLoading.value = false
            }
        }
    }
    
    fun updateOrderStatus(orderId: String, status: OrderStatus) {
        viewModelScope.launch {
            try {
                repository.updateOrderStatus(orderId, status)
                loadOrders()
            } catch (e: Exception) {
                _error.value = "Error updating order: ${e.message}"
            }
        }
    }
}
