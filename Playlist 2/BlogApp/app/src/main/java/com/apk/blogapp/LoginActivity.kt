package com.apk.blogapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: Button
    private lateinit var btnGoToRegister: Button
    private lateinit var auth: FirebaseAuth
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        
        // Configure Firebase Auth settings for development
        try {
            val authSettings = auth.firebaseAuthSettings
            authSettings.setAppVerificationDisabledForTesting(true)
            authSettings.forceRecaptchaFlowForTesting(false)
        } catch (e: Exception) {
            // Ignore if not available
        }
        
        initViews()
        setupClickListeners()
    }
    
    private fun initViews() {
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnGoToRegister = findViewById(R.id.btnGoToRegister)
    }
    
    private fun setupClickListeners() {
        btnLogin.setOnClickListener {
            loginUser()
        }
        
        btnGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }
    
    private fun loginUser() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()
        
        if (email.isEmpty()) {
            etEmail.error = "Email is required"
            return
        }
        
        if (password.isEmpty()) {
            etPassword.error = "Password is required"
            return
        }
        
        btnLogin.isEnabled = false
        btnLogin.text = "Logging in..."
        
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                btnLogin.isEnabled = true
                btnLogin.text = "Login"
                
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                } else {
                    val exception = task.exception
                    val errorMessage = when {
                        exception?.message?.contains("CONFIGURATION_NOT_FOUND") == true -> 
                            "Login temporarily unavailable. Please try again later."
                        exception?.message?.contains("user not found") == true -> 
                            "No account found with this email. Please register first."
                        exception?.message?.contains("wrong password") == true -> 
                            "Incorrect password. Please try again."
                        exception?.message?.contains("network error") == true -> 
                            "Network error. Please check your internet connection."
                        else -> "Login failed. Please check your credentials and try again."
                    }
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
    }
}