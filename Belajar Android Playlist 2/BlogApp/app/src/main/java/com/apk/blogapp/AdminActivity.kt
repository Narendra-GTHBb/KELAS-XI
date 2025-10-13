package com.apk.blogapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.apk.blogapp.utils.DummyDataGenerator

class AdminActivity : AppCompatActivity() {
    
    private lateinit var btnGenerateDummyData: Button
    private lateinit var btnTestFirebase: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)
        
        initViews()
        setupClickListeners()
    }
    
    private fun initViews() {
        btnGenerateDummyData = findViewById(R.id.btnGenerateDummyData)
        btnTestFirebase = findViewById(R.id.btnTestFirebase)
    }
    
    private fun setupClickListeners() {
        btnGenerateDummyData.setOnClickListener {
            generateDummyData()
        }
        
        btnTestFirebase.setOnClickListener {
            testFirebaseConnection()
        }
    }
    
    private fun generateDummyData() {
        btnGenerateDummyData.isEnabled = false
        btnGenerateDummyData.text = "Uploading to Firebase..."
        
        // Show progress
        Toast.makeText(this, "Starting to upload dummy data to Firebase...", Toast.LENGTH_SHORT).show()
        
        try {
            // Start the generation process with callback
            DummyDataGenerator.generateDummyArticles { success, message ->
                runOnUiThread {
                    if (success) {
                        Toast.makeText(this@AdminActivity, "✅ Data uploaded to Firebase!\nGo to Home and refresh to see articles.", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this@AdminActivity, "❌ Upload failed!\nCheck your internet connection.", Toast.LENGTH_LONG).show()
                    }
                    
                    // Re-enable button
                    btnGenerateDummyData.isEnabled = true
                    btnGenerateDummyData.text = "Generate Dummy Data"
                }
            }
            
        } catch (e: Exception) {
            Log.e("AdminActivity", "Error generating data", e)
            Toast.makeText(this, "Error starting upload: ${e.message}", Toast.LENGTH_LONG).show()
            btnGenerateDummyData.isEnabled = true
            btnGenerateDummyData.text = "Generate Dummy Data"
        }
    }
    
    private fun testFirebaseConnection() {
        btnTestFirebase.isEnabled = false
        btnTestFirebase.text = "Testing..."
        
        Toast.makeText(this, "Testing Firebase connection...", Toast.LENGTH_SHORT).show()
        
        DummyDataGenerator.testSingleUpload { success, message ->
            runOnUiThread {
                Toast.makeText(this@AdminActivity, message, Toast.LENGTH_LONG).show()
                
                btnTestFirebase.isEnabled = true
                btnTestFirebase.text = "Test Firebase Connection"
                
                if (success) {
                    Toast.makeText(this@AdminActivity, "✅ Firebase is working! You can now generate full data.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}