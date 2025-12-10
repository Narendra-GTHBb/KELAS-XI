package com.apkfood.wavesoffoodadmin.activity

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.apkfood.wavesoffoodadmin.MainActivity
import com.apkfood.wavesoffoodadmin.databinding.ActivityAdminLoginBinding
import com.apkfood.wavesoffoodadmin.viewmodel.AuthViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText

class AdminLoginActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityAdminLoginBinding
    private val authViewModel: AuthViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupUI()
        observeViewModel()
        
        // Check if already logged in
        if (authViewModel.isLoggedIn()) {
            binding.progressBar.visibility = View.VISIBLE
            authViewModel.getCurrentAdmin()
        }
    }
    
    private fun setupUI() {
        // Setup text watchers for validation
        binding.etEmail.addTextChangedListener {
            validateEmail()
            updateLoginButtonState()
        }
        
        binding.etPassword.addTextChangedListener {
            validatePassword()
            updateLoginButtonState()
        }
        
        // Setup click listeners
        binding.btnLogin.setOnClickListener {
            if (validateForm()) {
                login()
            }
        }
        
        binding.tvForgotPassword.setOnClickListener {
            showForgotPasswordDialog()
        }
        
        // Load saved email if remember me was checked
        loadSavedCredentials()
        
        // Initially disable login button
        updateLoginButtonState()
    }
    
    private fun updateLoginButtonState() {
        val isFormValid = validateForm()
        val isNotLoading = authViewModel.isLoading.value != true
        binding.btnLogin.isEnabled = isFormValid && isNotLoading
    }
    
    private fun loadSavedCredentials() {
        val sharedPrefs = getSharedPreferences("login_prefs", MODE_PRIVATE)
        val savedEmail = sharedPrefs.getString("saved_email", "")
        val rememberMe = sharedPrefs.getBoolean("remember_me", false)
        
        if (rememberMe && !savedEmail.isNullOrEmpty()) {
            binding.etEmail.setText(savedEmail)
            binding.cbRememberMe.isChecked = true
        }
    }
    
    private fun saveCredentials() {
        val sharedPrefs = getSharedPreferences("login_prefs", MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        
        if (binding.cbRememberMe.isChecked) {
            editor.putString("saved_email", binding.etEmail.text.toString().trim())
            editor.putBoolean("remember_me", true)
        } else {
            editor.remove("saved_email")
            editor.putBoolean("remember_me", false)
        }
        
        editor.apply()
    }
    
    private fun observeViewModel() {
        authViewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.etEmail.isEnabled = !isLoading
            binding.etPassword.isEnabled = !isLoading
            binding.cbRememberMe.isEnabled = !isLoading
            binding.tvForgotPassword.isEnabled = !isLoading
            updateLoginButtonState()
        }
        
        authViewModel.currentAdmin.observe(this) { admin ->
            if (admin != null) {
                // Show welcome message
                Toast.makeText(this, "Welcome back, ${admin.name}!", Toast.LENGTH_SHORT).show()
                
                // Successfully logged in, navigate to main activity
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
        
        authViewModel.loginResult.observe(this) { result ->
            result?.let {
                if (it.isFailure) {
                    val error = it.exceptionOrNull()?.message ?: "Login failed"
                    
                    // Show specific error messages
                    when {
                        error.contains("Invalid email or password") -> {
                            binding.tilEmail.error = " "
                            binding.tilPassword.error = "Invalid email or password"
                        }
                        error.contains("Admin account not found") -> {
                            binding.tilEmail.error = "Admin account not found"
                        }
                        error.contains("inactive") -> {
                            showError("Your admin account has been deactivated. Please contact support.")
                        }
                        error.contains("internet") || error.contains("network") -> {
                            showError("No internet connection. Please check your network and try again.")
                        }
                        else -> {
                            showError(error)
                        }
                    }
                    
                    authViewModel.clearLoginResult()
                }
            }
        }
        
        authViewModel.resetPasswordResult.observe(this) { result ->
            result?.let {
                if (it.isSuccess) {
                    Toast.makeText(this, "Password reset email sent", Toast.LENGTH_LONG).show()
                } else {
                    val error = it.exceptionOrNull()?.message ?: "Failed to send reset email"
                    showError(error)
                }
                authViewModel.clearResetPasswordResult()
            }
        }
    }
    
    private fun validateEmail(): Boolean {
        val email = binding.etEmail.text.toString().trim()
        return if (email.isEmpty()) {
            binding.tilEmail.error = "Email is required"
            false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Invalid email format"
            false
        } else {
            binding.tilEmail.error = null
            true
        }
    }
    
    private fun validatePassword(): Boolean {
        val password = binding.etPassword.text.toString()
        return if (password.isEmpty()) {
            binding.tilPassword.error = "Password is required"
            false
        } else if (password.length < 6) {
            binding.tilPassword.error = "Password must be at least 6 characters"
            false
        } else {
            binding.tilPassword.error = null
            true
        }
    }
    
    private fun validateForm(): Boolean {
        val isEmailValid = validateEmail()
        val isPasswordValid = validatePassword()
        return isEmailValid && isPasswordValid
    }
    
    private fun login() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()
        
        // Save credentials if remember me is checked
        saveCredentials()
        
        // Clear any previous errors
        binding.tilEmail.error = null
        binding.tilPassword.error = null
        
        authViewModel.login(email, password)
    }
    
    private fun showForgotPasswordDialog() {
        val dialogView = layoutInflater.inflate(
            com.apkfood.wavesoffoodadmin.R.layout.dialog_forgot_password, 
            null
        )
        val etEmail = dialogView.findViewById<TextInputEditText>(
            com.apkfood.wavesoffoodadmin.R.id.etResetEmail
        )
        
        MaterialAlertDialogBuilder(this)
            .setTitle("Reset Password")
            .setMessage("Enter your email address to receive a password reset link")
            .setView(dialogView)
            .setPositiveButton("Send") { _, _ ->
                val email = etEmail.text.toString().trim()
                if (email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    authViewModel.resetPassword(email)
                } else {
                    Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun showError(message: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Login Error")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
}
