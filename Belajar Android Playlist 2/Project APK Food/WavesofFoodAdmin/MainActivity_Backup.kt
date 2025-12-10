package com.apkfood.wavesoffoodadmin

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.apkfood.wavesoffoodadmin.activity.AdminLoginActivity
import com.apkfood.wavesoffoodadmin.activity.FoodManagementActivity
import com.apkfood.wavesoffoodadmin.activity.UserManagementActivity
import com.apkfood.wavesoffoodadmin.repository.AuthRepository
import com.apkfood.wavesoffoodadmin.viewmodel.AuthViewModel
import com.apkfood.wavesoffoodadmin.viewmodel.DashboardViewModel
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.NumberFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    
    private lateinit var dashboardViewModel: DashboardViewModel
    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var authRepository: AuthRepository
    
    // Views
    private lateinit var tvTotalOrders: TextView
    private lateinit var tvTotalRevenue: TextView
    private lateinit var tvTotalUsers: TextView
    private lateinit var tvTotalRestaurants: TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialize repository
        authRepository = AuthRepository(this)
        
        // Check authentication first
        if (!authViewModel.isLoggedIn()) {
            navigateToLogin()
            return
        }
        
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        setupToolbar()
        initViews()
        setupViewModel()
        setupClickListeners()
        observeAuth()
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
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_profile -> {
                showAdminProfile()
                true
            }
            R.id.action_logout -> {
                showLogoutConfirmation()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    private fun observeAuth() {
        // Simple approach: Just get cached admin data without observing
        lifecycleScope.launch {
            val cachedAdmin = authRepository.getCachedAdmin()
            if (cachedAdmin != null) {
                runOnUiThread {
                    supportActionBar?.subtitle = "Welcome, ${cachedAdmin.name} (${cachedAdmin.role})"
                }
            }
        }
    }
    
    private fun showAdminProfile() {
        authViewModel.currentAdmin.value?.let { admin ->
            MaterialAlertDialogBuilder(this)
                .setTitle("Admin Profile")
                .setMessage("""
                    Name: ${admin.name}
                    Email: ${admin.email}
                    Role: ${admin.role}
                    Last Login: ${if (admin.lastLogin > 0) java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(java.util.Date(admin.lastLogin)) else "Never"}
                """.trimIndent())
                .setPositiveButton("Close", null)
                .show()
        }
    }
    
    private fun showLogoutConfirmation() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Logout") { _, _ ->
                authViewModel.logout()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun initViews() {
        tvTotalOrders = findViewById(R.id.tvTotalOrders)
        tvTotalRevenue = findViewById(R.id.tvTotalRevenue)
        tvTotalUsers = findViewById(R.id.tvTotalUsers)
        tvTotalRestaurants = findViewById(R.id.tvTotalRestaurants)
    }
    
    private fun setupViewModel() {
        dashboardViewModel = ViewModelProvider(this)[DashboardViewModel::class.java]
        
        dashboardViewModel.analytics.observe(this) { analytics ->
            updateDashboard(analytics)
        }
    }
    
    private fun updateDashboard(analytics: com.apkfood.wavesoffoodadmin.model.Analytics) {
        tvTotalOrders.text = analytics.totalOrders.toString()
        tvTotalUsers.text = analytics.totalUsers.toString()
        tvTotalRestaurants.text = analytics.totalRestaurants.toString()
        
        // Format currency
        val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        tvTotalRevenue.text = formatter.format(analytics.totalRevenue).replace("IDR", "Rp")
    }
    
    private fun setupClickListeners() {
        findViewById<MaterialCardView>(R.id.cardManageOrders).setOnClickListener {
            // TODO: Navigate to Orders Activity
            // startActivity(Intent(this, OrdersActivity::class.java))
        }
        
        findViewById<MaterialCardView>(R.id.cardManageRestaurants).setOnClickListener {
            // TODO: Navigate to Restaurants Activity
            // startActivity(Intent(this, RestaurantsActivity::class.java))
        }
        
        findViewById<MaterialCardView>(R.id.cardManageUsers).setOnClickListener {
            startActivity(Intent(this, com.apkfood.wavesoffoodadmin.activity.UserManagementActivity::class.java))
        }
        
        findViewById<MaterialCardView>(R.id.cardAnalytics).setOnClickListener {
            // TODO: Navigate to Analytics Activity
            // startActivity(Intent(this, AnalyticsActivity::class.java))
        }
        
        findViewById<FloatingActionButton>(R.id.fabRefresh).setOnClickListener {
            dashboardViewModel.refreshData()
        }
        
        // Add menu item for food management
        findViewById<MaterialCardView>(R.id.cardManageRestaurants).setOnClickListener {
            startActivity(Intent(this, com.apkfood.wavesoffoodadmin.activity.FoodManagementActivity::class.java))
        }
    }
}