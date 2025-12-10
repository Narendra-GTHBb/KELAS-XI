package com.apkfood.wavesoffood.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.button.MaterialButton
import com.apkfood.wavesoffood.R
import com.apkfood.wavesoffood.utils.ForceSameDataGeneratorUser
import com.apkfood.wavesoffood.utils.EmergencyDataForcerUserCorrected
import kotlinx.coroutines.launch

class DebugActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_debug)
        
        setupClickListeners()
    }
    
    private fun setupClickListeners() {
        findViewById<MaterialButton>(R.id.btnForceExactSameData).setOnClickListener {
            forceExactSameData()
        }
        
        findViewById<MaterialButton>(R.id.btnEmergencyExecute).setOnClickListener {
            EMERGENCY_EXECUTE_IDENTICAL_DATA_NOW()
        }
        
        findViewById<MaterialButton>(R.id.btnBack).setOnClickListener {
            finish()
        }
    }
    
    private fun forceExactSameData() {
        MaterialAlertDialogBuilder(this)
            .setTitle("🔥 FORCE EXACT SAME DATA")
            .setMessage("Ini akan MEMAKSA kedua app menggunakan data yang PERSIS SAMA! Semua data akan dihapus dan diganti dengan data baru yang identik.")
            .setPositiveButton("🔥 FORCE NOW!") { _, _ ->
                lifecycleScope.launch {
                    try {
                        val generator = ForceSameDataGeneratorUser()
                        val success = generator.forceExactSameData()
                        
                        if (success) {
                            Toast.makeText(this@DebugActivity, 
                                "🎉 FORCED EXACT SAME DATA! Both apps now use IDENTICAL data!", 
                                Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(this@DebugActivity, 
                                "❌ Failed to force same data", 
                                Toast.LENGTH_LONG).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@DebugActivity, 
                            "❌ Error: ${e.message}", 
                            Toast.LENGTH_LONG).show()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun EMERGENCY_EXECUTE_IDENTICAL_DATA_NOW() {
        MaterialAlertDialogBuilder(this)
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
                executeEmergencyForcing()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun executeEmergencyForcing() {
        lifecycleScope.launch {
            try {
                Toast.makeText(this@DebugActivity, "🚨 EMERGENCY FORCING DATA...", Toast.LENGTH_SHORT).show()
                
                val forcer = EmergencyDataForcerUserCorrected()
                val success = forcer.FORCE_IDENTICAL_DATA_NOW()
                
                if (success) {
                    Toast.makeText(this@DebugActivity, "🎉 DATA IDENTIK BERHASIL DIPAKSA!", Toast.LENGTH_LONG).show()
                    
                    MaterialAlertDialogBuilder(this@DebugActivity)
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
                } else {
                    Toast.makeText(this@DebugActivity, "❌ GAGAL FORCE DATA!", Toast.LENGTH_LONG).show()
                    
                    MaterialAlertDialogBuilder(this@DebugActivity)
                        .setTitle("❌ GAGAL")
                        .setMessage("Gagal memaksa data identik. Cek koneksi internet dan coba lagi.")
                        .setPositiveButton("OK") { _, _ -> }
                        .show()
                }
                
            } catch (e: Exception) {
                Toast.makeText(this@DebugActivity, "❌ ERROR: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}