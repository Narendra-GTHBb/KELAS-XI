package com.apk.blogapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    
    private lateinit var auth: FirebaseAuth
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        
        Log.d("SplashActivity", "Splash screen started")
        
        // Initialize Firebase Auth
        try {
            auth = FirebaseAuth.getInstance()
            Log.d("SplashActivity", "Firebase Auth initialized successfully")
        } catch (e: Exception) {
            Log.e("SplashActivity", "Firebase Auth initialization failed", e)
            // If Firebase fails, go to welcome screen
            startActivity(Intent(this, WelcomeActivity::class.java))
            finish()
            return
        }
        
        // Use coroutine instead of Handler
        lifecycleScope.launch {
            delay(2000) // 2 seconds
            checkUserAuthentication()
        }
    }
    
    private fun checkUserAuthentication() {
        try {
            val currentUser = auth.currentUser
            
            Log.d("SplashActivity", "=== AUTH CHECK ===")
            Log.d("SplashActivity", "Current user: ${currentUser?.email}")
            Log.d("SplashActivity", "User ID: ${currentUser?.uid}")
            Log.d("SplashActivity", "Is user null? ${currentUser == null}")
            
            if (currentUser != null && !currentUser.isAnonymous) {
                // User is logged in, go to home
                Log.d("SplashActivity", "✅ User IS logged in, going to HomeActivity")
                startActivity(Intent(this, HomeActivity::class.java))
            } else {
                // User not logged in, go to welcome
                Log.d("SplashActivity", "❌ User NOT logged in, going to WelcomeActivity")
                startActivity(Intent(this, WelcomeActivity::class.java))
            }
        } catch (e: Exception) {
            Log.e("SplashActivity", "Error checking authentication", e)
            // On error, go to welcome screen
            startActivity(Intent(this, WelcomeActivity::class.java))
        }
        
        finish()
    }
}