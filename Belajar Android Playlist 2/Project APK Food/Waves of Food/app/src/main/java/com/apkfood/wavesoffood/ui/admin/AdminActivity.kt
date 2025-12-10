package com.apkfood.wavesoffood.ui.admin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.apkfood.wavesoffood.databinding.ActivityAdminBinding

/**
 * Admin Activity
 * Activity untuk panel admin
 */
class AdminActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityAdminBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupUI()
    }
    
    private fun setupUI() {
        supportActionBar?.title = "Admin Panel"
    }
}
