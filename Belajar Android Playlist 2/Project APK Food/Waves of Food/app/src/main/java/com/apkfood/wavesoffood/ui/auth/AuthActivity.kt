package com.apkfood.wavesoffood.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.apkfood.wavesoffood.MainActivity
import com.apkfood.wavesoffood.R
import com.apkfood.wavesoffood.utils.UserSessionManager
import com.apkfood.wavesoffood.utils.OrderManager
import com.apkfood.wavesoffood.utils.DataCleanupManager

/**
 * Authentication Activity
 * Activity untuk login/register dengan fragment navigation
 */
class AuthActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        
        // Hide action bar
        supportActionBar?.hide()
        
        // Check if already logged in
        if (!UserSessionManager.isGuest(this) && UserSessionManager.getCurrentUser(this) != null) {
            navigateToMain()
            return
        }
        
        // Show login fragment by default
        if (savedInstanceState == null) {
            showLoginFragment()
        }
        
        setupClickListeners()
    }
    
    private fun setupClickListeners() {
        // Login button (fragment navigation)
        findViewById<Button>(R.id.btnLogin)?.setOnClickListener {
            showLoginFragment()
        }
        
        // Register button (fragment navigation)  
        findViewById<Button>(R.id.btnRegister)?.setOnClickListener {
            showRegisterFragment()
        }
        
        // Guest button
        findViewById<Button>(R.id.btnGuest)?.setOnClickListener {
            loginAsGuest()
        }
    }
    
    private fun showLoginFragment() {
        replaceFragment(LoginFragment())
    }
    
    private fun showRegisterFragment() {
        // Temporarily disabled - RegisterFragment under development
        // replaceFragment(RegisterFragment())
        Toast.makeText(this, "Register functionality coming soon!", Toast.LENGTH_SHORT).show()
    }
    
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
    
    private fun loginAsGuest() {
        // Clear in-memory orders from previous user/session
        OrderManager.clearAllOrders()
        
        // Smart cleanup: Bersihkan data user lain untuk guest mode
        DataCleanupManager.cleanupOtherUsersData(this)
        
        // Create guest user session
        UserSessionManager.saveGuestSession(this)
        
        // Navigate to main
        navigateToMain()
    }
    
    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }
    
    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }
}
