package com.apk.blogapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthSettings

class RegisterActivity : AppCompatActivity() {
    
    private lateinit var etFullName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var btnGoToLogin: Button
    private lateinit var auth: FirebaseAuth
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        
        auth = FirebaseAuth.getInstance()
        
        // Configure Firebase Auth settings for development
        try {
            val authSettings = auth.firebaseAuthSettings
            authSettings.setAppVerificationDisabledForTesting(true)
            // Force disable reCAPTCHA for testing
            authSettings.forceRecaptchaFlowForTesting(false)
        } catch (e: Exception) {
            // Ignore if not available
        }
        
        initViews()
        setupClickListeners()
    }
    
    private fun initViews() {
        etFullName = findViewById(R.id.etFullName)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)
        btnGoToLogin = findViewById(R.id.btnGoToLogin)
    }
    
    private fun setupClickListeners() {
        btnRegister.setOnClickListener {
            registerUser()
        }
        
        btnGoToLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
    
    private fun registerUser() {
        val fullName = etFullName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()
        val confirmPassword = etConfirmPassword.text.toString()
        
        if (fullName.isEmpty()) {
            etFullName.error = "Please enter your full name"
            return
        }
        
        if (email.isEmpty()) {
            etEmail.error = "Please enter your email"
            return
        }
        
        if (password.isEmpty()) {
            etPassword.error = "Please enter your password"
            return
        }
        
        if (password.length < 6) {
            etPassword.error = "Password must be at least 6 characters"
            return
        }
        
        if (password != confirmPassword) {
            etConfirmPassword.error = "Passwords do not match"
            return
        }
        
        btnRegister.isEnabled = false
        btnRegister.text = "Creating Account..."
        
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                btnRegister.isEnabled = true
                btnRegister.text = "Register"
                
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let {
                        // Update user profile with full name
                        val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                            .setDisplayName(fullName)
                            .build()
                        
                        it.updateProfile(profileUpdates).addOnCompleteListener { profileTask ->
                            if (profileTask.isSuccessful) {
                                // Show welcome dialog
                                androidx.appcompat.app.AlertDialog.Builder(this)
                                    .setTitle("Welcome to Blog App!")
                                    .setMessage("Registration successful!\nWelcome, $fullName!\n\nPlease login to continue.")
                                    .setPositiveButton("Login Now") { _, _ ->
                                        // Complete logout - clear all Firebase Auth data
                                        try {
                                            auth.signOut()
                                            // Clear all SharedPreferences related to auth
                                            val prefs = getSharedPreferences("firebase_auth", MODE_PRIVATE)
                                            prefs.edit().clear().apply()
                                            val prefs2 = getSharedPreferences("com.google.firebase.auth", MODE_PRIVATE)
                                            prefs2.edit().clear().apply()
                                        } catch (e: Exception) {
                                            // Ignore errors
                                        }
                                        
                                        // Force restart the app to clear all auth state
                                        finishAffinity()
                                        val intent = Intent(this, SplashActivity::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        startActivity(intent)
                                    }
                                    .setCancelable(false)
                                    .show()
                            }
                        }
                    }
                } else {
                    val exception = task.exception
                    val errorMessage = when {
                        exception?.message?.contains("CONFIGURATION_NOT_FOUND") == true -> 
                            "Registration temporarily unavailable. Please try again later."
                        exception?.message?.contains("email address is already in use") == true -> 
                            "This email is already registered. Please use a different email or try logging in."
                        exception?.message?.contains("network error") == true -> 
                            "Network error. Please check your internet connection."
                        else -> exception?.message ?: "Registration failed. Please try again."
                    }
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
    }
}