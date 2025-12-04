package com.apk.stateexample

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel untuk mengelola state secara terpusat
 * Digunakan untuk data yang perlu dibagikan antar Composables
 * dan dipertahankan seumur hidup Activity
 */
class StateExampleViewModel : ViewModel() {
    
    // StateFlow - untuk state yang perlu diamati dari luar ViewModel
    private val _userName = MutableStateFlow("John Wck")
    val userName: StateFlow<String> = _userName.asStateFlow()
    
    // MutableState - untuk state internal yang bisa diamati langsung di Compose
    var counter = mutableStateOf(0)
        private set
    
    // State untuk menyimpan list item
    private val _items = MutableStateFlow(listOf("Item 1", "Item 2", "Item 3"))
    val items: StateFlow<List<String>> = _items.asStateFlow()
    
    /**
     * Fungsi untuk mengubah nama user
     * Menunjukkan bagaimana state dikelola secara terpusat
     */
    fun updateUserName(newName: String) {
        _userName.value = newName
    }
    
    /**
     * Fungsi untuk increment counter
     * Menunjukkan pengelolaan state yang persisten
     */
    fun incrementCounter() {
        counter.value++
    }
    
    /**
     * Fungsi untuk reset counter
     */
    fun resetCounter() {
        counter.value = 0
    }
    
    /**
     * Fungsi untuk menambah item baru
     * Menunjukkan pengelolaan state untuk collections
     */
    fun addItem(item: String) {
        viewModelScope.launch {
            val currentItems = _items.value.toMutableList()
            currentItems.add(item)
            _items.value = currentItems
        }
    }
    
    /**
     * Fungsi untuk menghapus item
     */
    fun removeItem(index: Int) {
        viewModelScope.launch {
            val currentItems = _items.value.toMutableList()
            if (index < currentItems.size) {
                currentItems.removeAt(index)
                _items.value = currentItems
            }
        }
    }
}