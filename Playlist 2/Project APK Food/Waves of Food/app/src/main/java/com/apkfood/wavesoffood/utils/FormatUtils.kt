package com.apkfood.wavesoffood.utils

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Utility class untuk formatting berbagai data
 */
object FormatUtils {
    
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    private val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    
    /**
     * Format angka menjadi format rupiah
     */
    fun formatCurrency(amount: Double): String {
        return currencyFormat.format(amount)
    }
    
    /**
     * Format harga makanan
     */
    fun formatPrice(price: Double): String {
        return formatCurrency(price).replace("IDR", "Rp")
    }
    
    /**
     * Format timestamp menjadi tanggal yang mudah dibaca
     */
    fun formatDate(timestamp: Long): String {
        return dateFormat.format(Date(timestamp))
    }
    
    /**
     * Format waktu estimasi pengiriman
     */
    fun formatEstimatedTime(minutes: Int): String {
        return when {
            minutes < 60 -> "$minutes mins"
            minutes == 60 -> "1 hour"
            else -> {
                val hours = minutes / 60
                val remainingMinutes = minutes % 60
                if (remainingMinutes == 0) {
                    "$hours hours"
                } else {
                    "$hours hours $remainingMinutes mins"
                }
            }
        }
    }
    
    /**
     * Validasi email
     */
    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    
    /**
     * Validasi nomor telepon Indonesia
     */
    fun isValidPhoneNumber(phone: String): Boolean {
        val phonePattern = "^(\\+62|62|0)8[1-9][0-9]{6,9}$"
        return phone.matches(phonePattern.toRegex())
    }
}
