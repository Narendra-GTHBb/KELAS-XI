package com.apkfood.wavesoffood.utils

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.apkfood.wavesoffood.ui.auth.AuthActivity

/**
 * Helper untuk validasi guest access dan menampilkan pesan login
 */
object GuestAccessHelper {
    
    /**
     * Cek apakah user boleh mengakses fitur cart
     * Jika guest, tampilkan pesan dan return false
     */
    fun checkCartAccess(context: Context): Boolean {
        if (UserSessionManager.isGuest(context)) {
            showLoginRequired(context, "Silakan login terlebih dahulu untuk menambahkan ke keranjang")
            return false
        }
        return true
    }
    
    /**
     * Cek apakah user boleh mengakses fitur checkout
     * Jika guest, tampilkan pesan dan return false
     */
    fun checkCheckoutAccess(context: Context): Boolean {
        if (UserSessionManager.isGuest(context)) {
            showLoginRequired(context, "Silakan login terlebih dahulu untuk melakukan checkout")
            return false
        }
        return true
    }
    
    /**
     * Tampilkan pesan login required
     */
    private fun showLoginRequired(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
    
    /**
     * Navigate to login screen
     */
    fun navigateToLogin(context: Context) {
        val intent = Intent(context, AuthActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}
