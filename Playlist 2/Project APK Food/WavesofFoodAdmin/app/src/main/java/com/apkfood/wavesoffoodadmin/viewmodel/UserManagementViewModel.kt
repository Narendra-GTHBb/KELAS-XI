package com.apkfood.wavesoffoodadmin.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apkfood.wavesoffoodadmin.model.User
import com.apkfood.wavesoffoodadmin.model.Order
import com.apkfood.wavesoffoodadmin.repository.UserManagementRepository
import kotlinx.coroutines.launch

class UserManagementViewModel : ViewModel() {
    private val repository = UserManagementRepository()
    
    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> = _users
    
    private val _userOrders = MutableLiveData<List<Order>>()
    val userOrders: LiveData<List<Order>> = _userOrders
    
    private val _selectedUser = MutableLiveData<User?>()
    val selectedUser: LiveData<User?> = _selectedUser
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error
    
    private val _operationResult = MutableLiveData<Boolean>()
    val operationResult: LiveData<Boolean> = _operationResult
    
    private val _searchResults = MutableLiveData<List<User>>()
    val searchResults: LiveData<List<User>> = _searchResults
    
    init {
        loadUsers()
    }
    
    fun loadUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val usersList = repository.getAllUsers()
                _users.value = usersList
                _searchResults.value = usersList // Initialize search results
            } catch (e: Exception) {
                _error.value = e.message ?: "Error loading users"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun updateSearchResults(users: List<User>) {
        _searchResults.value = users
    }
    
    fun searchUsers(query: String) {
        if (query.isEmpty()) {
            _searchResults.value = _users.value ?: emptyList()
            return
        }
        
        viewModelScope.launch {
            try {
                val results = repository.searchUsers(query)
                _searchResults.value = results
            } catch (e: Exception) {
                _error.value = e.message ?: "Error searching users"
            }
        }
    }
    
    fun getUserDetails(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = repository.getUserById(userId)
                _selectedUser.value = user
                
                // Load user orders
                val orders = repository.getUserOrders(userId)
                _userOrders.value = orders
            } catch (e: Exception) {
                _error.value = e.message ?: "Error loading user details"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun banUser(userId: String, reason: String = "") {
        viewModelScope.launch {
            try {
                val result = repository.banUser(userId, reason)
                _operationResult.value = result
                if (result) {
                    loadUsers() // Refresh list
                    // Update selected user if it's the same user
                    _selectedUser.value?.let { user ->
                        if (user.id == userId) {
                            _selectedUser.value = user.copy(isActive = false)
                        }
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Error banning user"
                _operationResult.value = false
            }
        }
    }
    
    fun unbanUser(userId: String) {
        viewModelScope.launch {
            try {
                val result = repository.unbanUser(userId)
                _operationResult.value = result
                if (result) {
                    loadUsers() // Refresh list
                    // Update selected user if it's the same user
                    _selectedUser.value?.let { user ->
                        if (user.id == userId) {
                            _selectedUser.value = user.copy(isActive = true)
                        }
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Error unbanning user"
                _operationResult.value = false
            }
        }
    }
    
    fun clearSelectedUser() {
        _selectedUser.value = null
        _userOrders.value = emptyList()
    }
    
    fun filterUsers(showBannedOnly: Boolean = false) {
        val allUsers = _users.value ?: return
        val filteredUsers = if (showBannedOnly) {
            allUsers.filter { !it.isActive }
        } else {
            allUsers.filter { it.isActive }
        }
        _searchResults.value = filteredUsers
    }
}
