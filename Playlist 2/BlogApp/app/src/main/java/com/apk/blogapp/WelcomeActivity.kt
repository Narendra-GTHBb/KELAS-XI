package com.apk.blogapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.apk.blogapp.utils.AuthManager

class WelcomeActivity : AppCompatActivity() {
    
    private lateinit var btnLogin: Button
    private lateinit var btnRegister: Button
    private lateinit var btnTestMode: Button
    private lateinit var authManager: AuthManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        
        authManager = AuthManager(this)
        
        initViews()
        setupClickListeners()
    }
    
    private fun initViews() {
        btnLogin = findViewById(R.id.btnLogin)
        btnRegister = findViewById(R.id.btnRegister)
        btnTestMode = findViewById(R.id.btnTestMode)
    }
    
    private fun setupClickListeners() {
        btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        
        btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        
        btnTestMode.setOnClickListener {
            // Login as test user without Firebase Auth
            authManager.loginAsTestUser()
            Toast.makeText(this, "Logged in as Test User", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }
}