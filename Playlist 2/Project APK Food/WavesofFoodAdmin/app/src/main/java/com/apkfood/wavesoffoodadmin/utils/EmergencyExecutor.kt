package com.apkfood.wavesoffoodadmin.utils

import android.app.Activity
import android.app.AlertDialog
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * EMERGENCY EXECUTOR - LANGSUNG FORCE DATA IDENTIK!
 */
object EmergencyExecutor {
    
    fun EXECUTE_FORCE_IDENTICAL_DATA_NOW(activity: Activity) {
        AlertDialog.Builder(activity)
            .setTitle("🚨 EMERGENCY FORCE IDENTICAL DATA")
            .setMessage("""
                AKAN LANGSUNG MEMAKSA DATA IDENTIK!
                
                ⚠️ INI AKAN:
                1. HAPUS SEMUA data di Firebase
                2. FORCE 5 menu identik persis 
                3. PAKSA SEMUA gambar, harga, nama SAMA
                
                LANJUTKAN?
            """.trimIndent())
            .setPositiveButton("🔥 FORCE SEKARANG! 🔥") { _, _ ->
                executeEmergencyForcing(activity)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun executeEmergencyForcing(activity: Activity) {
        val forcer = EmergencyDataForcer()
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d("EmergencyExecutor", "🚨 STARTING EMERGENCY FORCE EXECUTION!")
                
                val success = forcer.FORCE_IDENTICAL_DATA_NOW()
                
                withContext(Dispatchers.Main) {
                    if (success) {
                        showSuccessDialog(activity)
                    } else {
                        showErrorDialog(activity)
                    }
                }
                
            } catch (e: Exception) {
                Log.e("EmergencyExecutor", "❌ EMERGENCY EXECUTION FAILED", e)
                withContext(Dispatchers.Main) {
                    showErrorDialog(activity)
                }
            }
        }
    }
    
    private fun showSuccessDialog(activity: Activity) {
        Toast.makeText(activity, "🎉 DATA IDENTIK BERHASIL DIPAKSA!", Toast.LENGTH_LONG).show()
        
        AlertDialog.Builder(activity)
            .setTitle("✅ BERHASIL!")
            .setMessage("""
                🎉 DATA BERHASIL DIPAKSA IDENTIK!
                
                ✅ 5 menu identik dibuat
                ✅ Semua gambar SAMA
                ✅ Semua harga SAMA  
                ✅ Semua nama SAMA
                
                Silakan cek kedua aplikasi sekarang!
            """.trimIndent())
            .setPositiveButton("OK") { _, _ -> }
            .show()
    }
    
    private fun showErrorDialog(activity: Activity) {
        Toast.makeText(activity, "❌ GAGAL FORCE DATA!", Toast.LENGTH_LONG).show()
        
        AlertDialog.Builder(activity)
            .setTitle("❌ GAGAL")
            .setMessage("Gagal memaksa data identik. Cek koneksi internet dan coba lagi.")
            .setPositiveButton("OK") { _, _ -> }
            .show()
    }
}