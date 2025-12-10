package com.apkfood.wavesoffood.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.apkfood.wavesoffood.databinding.FragmentLoginBinding
import com.apkfood.wavesoffood.manager.AuthManager
import com.apkfood.wavesoffood.MainActivity
import com.apkfood.wavesoffood.utils.UserSessionManager
import com.apkfood.wavesoffood.utils.OrderManager
import com.apkfood.wavesoffood.utils.DataCleanupManager

/**
 * Login Fragment
 * Fragment untuk login pengguna
 */
class LoginFragment : Fragment() {
    
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var authManager: AuthManager
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        authManager = AuthManager()
        
        setupClickListeners()
    }
    
    /**
     * Setup click listeners untuk UI components
     */
    private fun setupClickListeners() {
        binding.apply {
            // Login button
            btnLogin.setOnClickListener {
                handleLogin()
            }
        }
    }
    
    /**
     * Handle login process
     */
    private fun handleLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        
        // Validation
        if (!validateInput(email, password)) {
            return
        }
        
        showLoading(true)
        
        authManager.loginWithEmail(
            email = email,
            password = password,
            onSuccess = { user ->
                showLoading(false)
                
                // Save user info
                saveUserToPreferences(user)
                
                // Navigate to main app
                navigateToMain()
            },
            onFailure = { errorMessage ->
                showLoading(false)
                showError(errorMessage)
            }
        )
    }
    
    /**
     * Validate input fields
     */
    private fun validateInput(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            binding.etEmail.error = "Email tidak boleh kosong"
            binding.etEmail.requestFocus()
            return false
        }
        
        if (password.isEmpty()) {
            binding.etPassword.error = "Password tidak boleh kosong"
            binding.etPassword.requestFocus()
            return false
        }
        
        return true
    }
    
    /**
     * Show/hide loading state
     */
    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            if (isLoading) {
                progressBar.visibility = View.VISIBLE
                btnLogin.isEnabled = false
                btnLogin.text = "Loading..."
            } else {
                progressBar.visibility = View.GONE
                btnLogin.isEnabled = true
                btnLogin.text = "Login"
            }
        }
    }
    
    /**
     * Save user info to preferences using UserSessionManager
     */
    private fun saveUserToPreferences(user: com.apkfood.wavesoffood.model.User) {
        // Use UserSessionManager for proper session management
        UserSessionManager.saveUserSession(requireContext(), user, isGuest = false)
        
        // Clear in-memory orders from previous user/session
        OrderManager.clearAllOrders()
        
        // Smart cleanup: Bersihkan data user lain, simpan data user aktif
        DataCleanupManager.cleanupOtherUsersData(requireContext())
    }
    
    /**
     * Show error message
     */
    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }
    
    /**
     * Navigate to main app
     */
    private fun navigateToMain() {
        try {
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            requireActivity().finish()
        } catch (e: Exception) {
            showError("Error navigating to main: ${e.message}")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
