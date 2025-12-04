package com.apkfood.wavesoffood

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.apkfood.wavesoffood.ui.home.HomeFragment
import com.apkfood.wavesoffood.ui.menu.MenuFragment
import com.apkfood.wavesoffood.ui.cart.CartFragment
import com.apkfood.wavesoffood.ui.orders.OrdersFragment
import com.apkfood.wavesoffood.ui.favorite.FavoriteFragment
import com.apkfood.wavesoffood.ui.fragments.ProfileFragment
import com.apkfood.wavesoffood.ui.auth.AuthActivity
import com.apkfood.wavesoffood.manager.CartManager
import com.apkfood.wavesoffood.utils.UserSessionManager
import com.apkfood.wavesoffood.utils.DataCleanupManager

/**
 * Main Activity with Authentication Check
 * Activity utama aplikasi setelah login
 */
class MainActivity : AppCompatActivity(), CartManager.CartUpdateListener {
    
    private lateinit var sharedPreferences: SharedPreferences
    private val handler = Handler(Looper.getMainLooper())
    private var username: String = "Guest"
    private var isLoggedIn: Boolean = false
    private var cartBadge: TextView? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            showDebugToast("1. MainActivity onCreate started")
            
            // Check if user is logged in, if not redirect to AuthActivity
            if (!UserSessionManager.isLoggedIn(this)) {
                redirectToAuth()
                return
            }
            
            setContentView(R.layout.activity_main)
            showDebugToast("2. Layout set successfully")
            
            // Initialize SharedPreferences
            sharedPreferences = getSharedPreferences("WavesOfFoodPrefs", Context.MODE_PRIVATE)
            showDebugToast("3. SharedPreferences initialized")
            
            // Get user data from UserSessionManager
            val currentUser = UserSessionManager.getCurrentUser(this)
            username = currentUser?.name ?: UserSessionManager.getDisplayName(this)
            isLoggedIn = UserSessionManager.isLoggedIn(this)
            showDebugToast("4. Got user data: $username")
            
            // Save to SharedPreferences (for backward compatibility)
            with(sharedPreferences.edit()) {
                putString("current_username", username)
                putBoolean("is_logged_in", isLoggedIn)
                apply()
            }
            showDebugToast("5. Saved to SharedPreferences")
            
            // Start Firebase order listener for real-time status updates
            if (isLoggedIn) {
                com.apkfood.wavesoffood.utils.OrderManager.startFirebaseOrderListener(this)
                showDebugToast("5.1 Firebase order listener started")
                
                // Test Firebase connection (debugging)
                com.apkfood.wavesoffood.utils.OrderManager.testFirebaseConnection(this)
                showDebugToast("5.2 Firebase connection test initiated")
            }
            
            // Update toolbar
            updateToolbar(username)
            showDebugToast("6. Toolbar updated")
            
            // Smart cleanup: Bersihkan data user lain, simpan data user aktif
            DataCleanupManager.cleanupOtherUsersData(this)
            
            // Stop any automatic order status progressions
            com.apkfood.wavesoffood.utils.OrderStatusManager.cancelAllProgressions()
            
            // Show debug notification (temporary for debugging)
            showDebugToast("Smart cleanup completed - isolated data for current user")
            
            // Setup bottom navigation
            setupBottomNavigation()
            showDebugToast("7. Bottom navigation setup")
            
            // Setup cart badge
            setupCartBadge()
            
            // Register as cart listener
            CartManager.getInstance().initialize(this)
            CartManager.getInstance().addListener(this)
            
            // Delay loading fragment to see if this is the issue
            handler.postDelayed({
                try {
                    if (savedInstanceState == null) {
                        showDebugToast("8. About to load HomeFragment...")
                        val homeFragment = HomeFragment.newInstance(username, isLoggedIn)
                        loadFragment(homeFragment)
                        showDebugToast("9. HomeFragment loaded successfully!")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    showDebugToast("ERROR in fragment loading: ${e.message}")
                }
            }, 1000) // Delay 1 second
            
        } catch (e: Exception) {
            e.printStackTrace()
            showDebugToast("MAIN ERROR: ${e.message}")
        }
    }
    
    
    private fun redirectToAuth() {
        val intent = Intent(this, AuthActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }
    
    private fun showDebugToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    
    private fun updateToolbar(username: String) {
        try {
            // Find toolbar and its TextView - tidak perlu update karena akan di handle di HomeFragment
            Toast.makeText(this, "Welcome $username! (Long press for debug)", Toast.LENGTH_LONG).show()
            
            // Add debug mode access via long press on main container
            findViewById<android.widget.FrameLayout>(R.id.fragment_container)?.setOnLongClickListener {
                openDebugMode()
                true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            showDebugToast("Toolbar error: ${e.message}")
        }
    }
    
    private fun setupBottomNavigation() {
        try {
            findViewById<android.widget.LinearLayout>(R.id.nav_home)?.setOnClickListener {
                showDebugToast("Home clicked")
                val homeFragment = HomeFragment.newInstance(username, isLoggedIn)
                loadFragment(homeFragment)
            }
            
            findViewById<android.widget.LinearLayout>(R.id.nav_menu)?.setOnClickListener {
                showDebugToast("Menu clicked")
                loadFragment(MenuFragment())
            }
            
            findViewById<android.widget.LinearLayout>(R.id.nav_cart)?.setOnClickListener {
                showDebugToast("Cart clicked")
                if (UserSessionManager.isGuest(this)) {
                    showLoginPrompt("Keranjang")
                } else {
                    loadFragment(CartFragment())
                }
            }
            
            findViewById<android.widget.LinearLayout>(R.id.nav_orders)?.setOnClickListener {
                showDebugToast("Orders clicked")
                if (UserSessionManager.isGuest(this)) {
                    showLoginPrompt("Riwayat Pesanan")
                } else {
                    loadFragment(OrdersFragment())
                }
            }
            
            findViewById<android.widget.LinearLayout>(R.id.nav_favorites)?.setOnClickListener {
                showDebugToast("Favorites clicked")
                if (UserSessionManager.isGuest(this)) {
                    showLoginPrompt("Favorit")
                } else {
                    loadFragment(FavoriteFragment())
                }
            }
            
            findViewById<android.widget.LinearLayout>(R.id.nav_profile)?.setOnClickListener {
                showDebugToast("Profile clicked")
                loadFragment(ProfileFragment())
            }
            
        } catch (e: Exception) {
            e.printStackTrace()
            showDebugToast("Navigation error: ${e.message}")
        }
    }
    
    private fun setupCartBadge() {
        cartBadge = findViewById(R.id.cart_badge)
        updateCartBadge()
    }
    
    private fun updateCartBadge() {
        val itemCount = CartManager.getInstance().getTotalItems()
        cartBadge?.let { badge ->
            if (itemCount > 0) {
                badge.visibility = android.view.View.VISIBLE
                badge.text = if (itemCount > 99) "99+" else itemCount.toString()
            } else {
                badge.visibility = android.view.View.GONE
            }
        }
    }
    
    override fun onCartUpdated() {
        runOnUiThread {
            updateCartBadge()
        }
    }
    
    private fun showLoginPrompt(feature: String) {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Masuk Diperlukan")
            .setMessage("Untuk mengakses $feature, silakan masuk terlebih dahulu.")
            .setPositiveButton("Masuk") { _, _ ->
                redirectToAuth()
            }
            .setNegativeButton("Nanti", null)
            .show()
    }
    
    private fun loadFragment(fragment: Fragment) {
        try {
            if (!isFinishing && !isDestroyed) {
                showDebugToast("Loading fragment...")
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commitAllowingStateLoss()
                showDebugToast("Fragment transaction committed")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            showDebugToast("Fragment load error: ${e.message}")
        }
    }
    
    private fun openDebugMode() {
        val intent = Intent(this, com.apkfood.wavesoffood.activity.DebugActivity::class.java)
        startActivity(intent)
    }
    
    override fun onResume() {
        super.onResume()
        
        // HARDCORE FIX: Always restart Firebase listener saat app resume
        Log.d("MainActivity", "🔄 App resumed - Restarting Firebase listener")
        com.apkfood.wavesoffood.utils.OrderManager.stopFirebaseOrderListener()
        com.apkfood.wavesoffood.utils.OrderManager.startFirebaseOrderListener(this)
        showDebugToast("🔄 Firebase listener restarted on resume")
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Stop Firebase order listener
        com.apkfood.wavesoffood.utils.OrderManager.stopFirebaseOrderListener()
        // Remove cart listener
        CartManager.getInstance().removeListener(this)
    }
}
