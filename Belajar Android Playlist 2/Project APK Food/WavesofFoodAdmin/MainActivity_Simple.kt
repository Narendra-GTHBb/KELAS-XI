package com.apkfood.wavesoffoodadmin

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.apkfood.wavesoffoodadmin.activity.AdminLoginActivity
import com.apkfood.wavesoffoodadmin.repository.AuthRepository
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity() {
    
    private lateinit var authRepository: AuthRepository
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            // Initialize repository
            authRepository = AuthRepository(this)
            
            // Check authentication first
            if (!authRepository.isLoggedIn()) {
                navigateToLogin()
                return
            }
            
            setContentView(R.layout.activity_main)
            
            setupToolbar()
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
    
    private fun navigateToLogin() {
        val intent = Intent(this, AdminLoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(findViewById(androidx.appcompat.R.id.action_bar))
        supportActionBar?.title = "Admin Dashboard"
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
