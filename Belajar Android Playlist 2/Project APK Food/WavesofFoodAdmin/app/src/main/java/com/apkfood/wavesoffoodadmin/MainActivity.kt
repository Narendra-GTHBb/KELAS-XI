package com.apkfood.wavesoffoodadmin

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.apkfood.wavesoffoodadmin.activity.AdminLoginActivity
import com.apkfood.wavesoffoodadmin.activity.OrderManagementActivity
import com.apkfood.wavesoffoodadmin.activity.UserManagementActivity
import com.apkfood.wavesoffoodadmin.activity.FoodManagementActivity
import com.apkfood.wavesoffoodadmin.repository.AuthRepository
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity() {
    
    private lateinit var authRepository: AuthRepository
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            // Initialize repository
            authRepository = AuthRepository(this)
            
            // Check authentication properly
            if (!authRepository.isLoggedIn()) {
                navigateToLogin()
                return
            }
            
            // Set content and show dashboard
            setContentView(R.layout.activity_main)
            setupToolbar()
            setupQuickActions()
            showWelcomeMessage()
            
        } catch (e: Exception) {
            // Show error and go back to login
            MaterialAlertDialogBuilder(this)
                .setTitle("Error")
                .setMessage("Failed to load dashboard: ${e.message}")
                .setPositiveButton("OK") { _, _ ->
                    navigateToLogin()
                }
                .show()
        }
    }
    
    private fun checkAuthenticationWithDelay() {
        if (!authRepository.isLoggedIn()) {
            navigateToLogin()
        } else {
            // Authentication successful, show welcome message
            showWelcomeMessage()
        }
    }
    
    private fun navigateToLogin() {
        val intent = Intent(this, AdminLoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
    
    private fun setupToolbar() {
        try {
            val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
            if (toolbar != null) {
                setSupportActionBar(toolbar)
                supportActionBar?.title = "Admin Dashboard"
            }
        } catch (e: Exception) {
            // Ignore toolbar setup error for now
        }
    }
    
    private fun showWelcomeMessage() {
        try {
            val cachedAdmin = authRepository.getCachedAdmin()
            if (cachedAdmin != null) {
                supportActionBar?.subtitle = "Welcome, ${cachedAdmin.name}"
            }
        } catch (e: Exception) {
            // Ignore error for now
        }
    }
    
    private fun setupQuickActions() {
        try {
            // Manage Orders
            val cardManageOrders = findViewById<MaterialCardView>(R.id.cardManageOrders)
            cardManageOrders?.setOnClickListener {
                try {
                    val intent = Intent(this, OrderManagementActivity::class.java)
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(this, "Error opening Order Management: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
            
            // Manage Users  
            val cardManageUsers = findViewById<MaterialCardView>(R.id.cardManageUsers)
            cardManageUsers?.setOnClickListener {
                try {
                    val intent = Intent(this, UserManagementActivity::class.java)
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(this, "Error opening User Management: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
            
            // Manage Restaurants/Foods
            val cardManageRestaurants = findViewById<MaterialCardView>(R.id.cardManageRestaurants)
            cardManageRestaurants?.setOnClickListener {
                try {
                    val intent = Intent(this, FoodManagementActivity::class.java)
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(this, "Error opening Food Management: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
            
            // Analytics
            val cardAnalytics = findViewById<MaterialCardView>(R.id.cardAnalytics)
            cardAnalytics?.setOnClickListener {
                try {
                    Toast.makeText(this, "Analytics feature coming soon!", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
            
        } catch (e: Exception) {
            Toast.makeText(this, "Error setting up quick actions: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    private fun logout() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { _, _ ->
                authRepository.logout()
                navigateToLogin()
            }
            .setNegativeButton("No", null)
            .show()
    }
}
