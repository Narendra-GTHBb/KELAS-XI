package com.apkfood.wavesoffood.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.apkfood.wavesoffood.databinding.FragmentProfileBinding
import com.apkfood.wavesoffood.utils.UserSessionManager
import com.apkfood.wavesoffood.manager.CartManager
import com.apkfood.wavesoffood.manager.OrderHistoryManager
import com.apkfood.wavesoffood.utils.OrderManager
import com.apkfood.wavesoffood.utils.DataCleanupManager
import com.apkfood.wavesoffood.utils.FavoriteManager
import com.apkfood.wavesoffood.AuthActivity

class ProfileFragment : Fragment() {
    
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    
    companion object {
        private const val TAG = "ProfileFragment"
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        Log.d(TAG, "ProfileFragment created")
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "ProfileFragment view created")
        setupUI()
        setupClickListeners()
        
        // Trigger data isolation on view creation to ensure proper data loading
        DataCleanupManager.cleanupOtherUsersData(requireContext())
    }
    
    private fun setupUI() {
        try {
            val currentUser = UserSessionManager.getCurrentUser(requireContext())
            val isGuest = UserSessionManager.isGuest(requireContext())
            
            Log.d(TAG, "Setup UI - isGuest: $isGuest, currentUser: ${currentUser?.name}")
            
            if (isGuest || currentUser == null) {
                setupGuestUI()
            } else {
                setupRegisteredUserUI(currentUser)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in setupUI", e)
            showToast("Error setting up profile")
        }
    }
    
    private fun setupGuestUI() {
        Log.d(TAG, "Setting up Guest UI")
        
        binding.tvUserName.text = "Guest User"
        binding.tvUserEmail.text = "Masuk untuk fitur lengkap"
        
        binding.layoutOrderHistory.visibility = View.VISIBLE
        binding.layoutFavorites.visibility = View.VISIBLE
        binding.layoutSettings.visibility = View.VISIBLE
        
        binding.btnLogout.text = "MASUK KE AKUN"
        binding.btnLogout.visibility = View.VISIBLE
        
        Log.d(TAG, "Guest UI setup completed")
    }
    
    private fun setupRegisteredUserUI(user: com.apkfood.wavesoffood.model.User) {
        Log.d(TAG, "Setting up Registered User UI for: ${user.name}")
        
        binding.tvUserName.text = user.name
        binding.tvUserEmail.text = user.email
        
        binding.layoutOrderHistory.visibility = View.VISIBLE
        binding.layoutFavorites.visibility = View.VISIBLE
        binding.layoutSettings.visibility = View.VISIBLE
        
        binding.btnLogout.text = "KELUAR"
        binding.btnLogout.visibility = View.VISIBLE
        
        Log.d(TAG, "Registered user UI setup completed")
    }
    
    private fun setupClickListeners() {
        try {
            val isGuest = UserSessionManager.isGuest(requireContext())
            Log.d(TAG, "Setting up click listeners - isGuest: $isGuest")
            
            binding.btnLogout.setOnClickListener {
                Log.d(TAG, "Logout button clicked - isGuest: $isGuest")
                
                if (isGuest) {
                    navigateToLoginAndRestart()
                } else {
                    showLogoutDialog()
                }
            }
            
            if (isGuest) {
                binding.layoutOrderHistory.setOnClickListener {
                    showLoginPrompt("Riwayat Pesanan")
                }
                
                binding.layoutFavorites.setOnClickListener {
                    showLoginPrompt("Favorit")
                }
                
                binding.layoutSettings.setOnClickListener {
                    showGuestSettings()
                }
            } else {
                binding.layoutOrderHistory.setOnClickListener {
                    navigateToOrders()
                }
                
                binding.layoutFavorites.setOnClickListener {
                    navigateToFavorites()
                }
                
                binding.layoutSettings.setOnClickListener {
                    showRegisteredUserSettings()
                }
            }
            
            Log.d(TAG, "Click listeners setup completed")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up click listeners", e)
            showToast("Error setting up profile")
        }
    }
    
    private fun showLogoutDialog() {
        Log.d(TAG, "Showing logout dialog")
        try {
            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Keluar")
                .setMessage("Apakah Anda yakin ingin keluar dari aplikasi?")
                .setPositiveButton("Ya") { _, _ ->
                    Log.d(TAG, "User confirmed logout")
                    performLogout()
                }
                .setNegativeButton("Tidak") { _, _ ->
                    Log.d(TAG, "User cancelled logout")
                }
                .show()
        } catch (e: Exception) {
            Log.e(TAG, "Error showing logout dialog", e)
            performLogout()
        }
    }
    
    private fun performLogout() {
        try {
            Log.d(TAG, "Performing logout...")
            
            // Clear all user session data
            UserSessionManager.clearSession(requireContext())
            Log.d(TAG, "User session cleared")
            
            // Clear temporary data
            CartManager.getInstance().clearCart()
            OrderManager.clearUserOrders(requireContext())
            Log.d(TAG, "Temporary data cleared")
            
            // Perform data cleanup
            DataCleanupManager.performLogoutCleanup(requireContext())
            Log.d(TAG, "Logout cleanup completed")
            
            showToast("Berhasil keluar")
            
            // Navigate to login and completely restart the app
            navigateToLoginAndRestart()
            
        } catch (e: Exception) {
            Log.e(TAG, "Error during logout", e)
            showToast("Error saat keluar: ${e.message}")
            navigateToLoginAndRestart()
        }
    }
    
    private fun navigateToLoginAndRestart() {
        try {
            Log.d(TAG, "Navigating to login and restarting app...")
            
            // Create intent to AuthActivity
            val intent = Intent(requireContext(), AuthActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            
            // Start the AuthActivity
            startActivity(intent)
            
            // Finish current activity completely
            requireActivity().finishAffinity()
            
            // Kill the current process to ensure complete restart
            android.os.Process.killProcess(android.os.Process.myPid())
            
            Log.d(TAG, "App restart completed")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error navigating to login", e)
            showToast("Error navigating to login: ${e.message}")
            // Fallback - just finish the activity
            requireActivity().finish()
        }
    }
    
    private fun showLoginPrompt(feature: String) {
        try {
            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Masuk Diperlukan")
                .setMessage("Untuk mengakses $feature, silakan masuk terlebih dahulu.")
                .setPositiveButton("Masuk") { _, _ ->
                    navigateToLoginAndRestart()
                }
                .setNegativeButton("Nanti", null)
                .show()
        } catch (e: Exception) {
            Log.e(TAG, "Error showing login prompt", e)
            navigateToLoginAndRestart()
        }
    }
    
    private fun navigateToOrders() {
        try {
            showToast("Membuka riwayat pesanan...")
        } catch (e: Exception) {
            Log.e(TAG, "Error navigating to orders", e)
            showToast("Error membuka riwayat pesanan")
        }
    }
    
    private fun navigateToFavorites() {
        try {
            showToast("Membuka favorit...")
        } catch (e: Exception) {
            Log.e(TAG, "Error navigating to favorites", e)
            showToast("Error membuka favorit")
        }
    }
    
    private fun showGuestSettings() {
        try {
            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Pengaturan")
                .setItems(arrayOf(
                    "Tentang Aplikasi",
                    "Kebijakan Privasi",
                    "Masuk ke Akun",
                    "Debug: Reset All Data"
                )) { _, which ->
                    when (which) {
                        0 -> showAboutDialog()
                        1 -> showToast("Kebijakan privasi")
                        2 -> navigateToLoginAndRestart()
                        3 -> showEmergencyResetDialog()
                    }
                }
                .show()
        } catch (e: Exception) {
            Log.e(TAG, "Error showing guest settings", e)
        }
    }
    
    private fun showRegisteredUserSettings() {
        try {
            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Pengaturan")
                .setItems(arrayOf(
                    "Edit Profil",
                    "Tentang Aplikasi",
                    "Kebijakan Privasi",
                    "Debug: Lihat Data Storage"
                )) { _, which ->
                    when (which) {
                        0 -> showEditProfileDialog()
                        1 -> showAboutDialog()
                        2 -> showToast("Kebijakan privasi")
                        3 -> showDataStorageDebug()
                    }
                }
                .show()
        } catch (e: Exception) {
            Log.e(TAG, "Error showing registered user settings", e)
        }
    }
    
    private fun showEditProfileDialog() {
        try {
            val currentUser = UserSessionManager.getCurrentUser(requireContext())
            if (currentUser == null) {
                showToast("Data pengguna tidak ditemukan")
                return
            }
            
            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Profil Saya")
                .setMessage("Nama: ${currentUser.name}\nEmail: ${currentUser.email}")
                .setPositiveButton("OK", null)
                .show()
        } catch (e: Exception) {
            Log.e(TAG, "Error showing edit profile dialog", e)
        }
    }
    
    private fun showAboutDialog() {
        try {
            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Tentang Aplikasi")
                .setMessage("Waves of Food\nVersi 1.0\nAplikasi pemesanan makanan")
                .setPositiveButton("OK", null)
                .show()
        } catch (e: Exception) {
            Log.e(TAG, "Error showing about dialog", e)
        }
    }
    
    private fun showEmergencyResetDialog() {
        try {
            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("EMERGENCY RESET")
                .setMessage("PERINGATAN!\nIni akan menghapus SEMUA data user!\n\nHanya gunakan untuk testing.\nYakin ingin melanjutkan?")
                .setPositiveButton("YA, HAPUS SEMUA") { _, _ ->
                    DataCleanupManager.emergencyDataReset(requireContext())
                    showToast("SEMUA DATA DIHAPUS!")
                    Log.d(TAG, "Emergency data reset performed")
                }
                .setNegativeButton("BATAL", null)
                .show()
        } catch (e: Exception) {
            Log.e(TAG, "Error showing emergency reset dialog", e)
        }
    }
    
    private fun showDataStorageDebug() {
        try {
            val currentUser = UserSessionManager.getCurrentUser(requireContext())
            val debugInfo = StringBuilder()
            
            // Show current user info
            debugInfo.append("=== USER INFO ===\n")
            debugInfo.append("Current User: ${currentUser?.name}\n")
            debugInfo.append("User ID: ${currentUser?.uid}\n")
            debugInfo.append("User Email: ${currentUser?.email}\n")
            debugInfo.append("Is Guest: ${UserSessionManager.isGuest(requireContext())}\n\n")
            
            // Show data storage info
            debugInfo.append(DataCleanupManager.debugShowAllData(requireContext()))
            
            // Show favorites count
            debugInfo.append("\n=== CURRENT USER DATA ===\n")
            debugInfo.append("Favorites Count: ${FavoriteManager.getFavoriteCount(requireContext())}\n")
            debugInfo.append("Order History Count: ${OrderHistoryManager.getOrderHistory(requireContext()).size}\n")
            
            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Data Storage Debug")
                .setMessage(debugInfo.toString())
                .setPositiveButton("Copy to Log") { _, _ ->
                    Log.d(TAG, "DEBUG INFO:\n$debugInfo")
                    showToast("Debug info copied to log")
                }
                .setNegativeButton("Close", null)
                .show()
                
        } catch (e: Exception) {
            Log.e(TAG, "Error showing data storage debug", e)
            showToast("Error getting debug info")
        }
    }
    
    private fun showToast(message: String) {
        try {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            Log.d(TAG, "Toast: $message")
        } catch (e: Exception) {
            Log.e(TAG, "Error showing toast", e)
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.d(TAG, "ProfileFragment destroyed")
    }
}
