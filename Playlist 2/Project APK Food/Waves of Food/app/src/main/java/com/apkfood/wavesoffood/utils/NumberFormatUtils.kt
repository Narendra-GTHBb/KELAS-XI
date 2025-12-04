package com.apkfood.wavesoffood.utils

import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

/**
 * Utility class untuk formatting angka dan mata uang
 */
object NumberFormatUtils {
    
    private val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    private val decimalFormatter = DecimalFormat("#,###.##")
    
    /**
     * Format angka ke format currency Indonesia (Rupiah)
     */
    fun formatCurrency(amount: Double): String {
        return "Rp ${decimalFormatter.format(amount)}"
    }
    
    /**
     * Format angka ke format currency Indonesia (Rupiah) dengan Long
     */
    fun formatCurrency(amount: Long): String {
        return formatCurrency(amount.toDouble())
    }
    
    /**
     * Format angka ke format currency Indonesia (Rupiah) dengan Int
     */
    fun formatCurrency(amount: Int): String {
        return formatCurrency(amount.toDouble())
    }
    
    /**
     * Format angka biasa dengan pemisah ribuan
     */
    fun formatNumber(number: Double): String {
        return decimalFormatter.format(number)
    }
    
    /**
     * Format angka biasa dengan pemisah ribuan
     */
    fun formatNumber(number: Long): String {
        return decimalFormatter.format(number)
    }
    
    /**
     * Format angka biasa dengan pemisah ribuan
     */
    fun formatNumber(number: Int): String {
        return decimalFormatter.format(number)
    }
    
    /**
     * Parse string currency kembali ke Double
     */
    fun parseCurrency(currencyString: String): Double {
        return try {
            val cleanString = currencyString.replace("Rp", "")
                .replace(".", "")
                .replace(",", ".")
                .trim()
            cleanString.toDouble()
        } catch (e: NumberFormatException) {
            0.0
        }
    }
    
    /**
     * Format persentase
     */
    fun formatPercentage(value: Double): String {
        return "${decimalFormatter.format(value)}%"
    }
    
    /**
     * Format rating (1 desimal)
     */
    fun formatRating(rating: Double): String {
        return String.format("%.1f", rating)
    }
}
