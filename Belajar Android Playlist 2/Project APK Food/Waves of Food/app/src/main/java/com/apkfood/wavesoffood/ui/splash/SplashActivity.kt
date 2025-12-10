package com.apkfood.wavesoffood.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.apkfood.wavesoffood.R
import com.apkfood.wavesoffood.manager.AuthManager
import com.apkfood.wavesoffood.ui.auth.AuthActivity
import com.apkfood.wavesoffood.MainActivity

/**
 * Splash Screen Activity
 * Menampilkan logo dan loading sebelum masuk ke aplikasi utama
 */
class SplashActivity : AppCompatActivity() {
    
    private lateinit var authManager: AuthManager
    
    companion object {
        private const val SPLASH_DELAY = 2000L // 2 detik
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            // Log untuk debugging
            android.util.Log.d("SplashActivity", "onCreate started")
            
            // Set content view sederhana
            setContentView(R.layout.activity_splash)
            
            android.util.Log.d("SplashActivity", "Layout set successfully")
            
            authManager = AuthManager()
            
            android.util.Log.d("SplashActivity", "AuthManager initialized")
            
            // Delay untuk splash screen
            Handler(Looper.getMainLooper()).postDelayed({
                android.util.Log.d("SplashActivity", "Starting authentication check")
                checkAuthenticationStatus()
            }, SPLASH_DELAY)
            
        } catch (e: Exception) {
            android.util.Log.e("SplashActivity", "Error in onCreate", e)
            e.printStackTrace()
            // Fallback langsung ke AuthActivity jika ada error
            navigateToAuth()
        }
    }
    
    /**
     * Check apakah user sudah login atau belum
     */
    private fun checkAuthenticationStatus() {
        try {
            if (authManager.isLoggedIn()) {
                // User sudah login, langsung ke MainActivity
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            } else {
                // User belum login, ke AuthActivity
                navigateToAuth()
            }
            finish()
        } catch (e: Exception) {
            e.printStackTrace()
            navigateToAuth()
        }
    }
    
    /**
     * Navigate ke AuthActivity
     */
    private fun navigateToAuth() {
        try {
            val intent = Intent(this, AuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            e.printStackTrace()
            finish()
        }
    }
}
