package com.apkfood.wavesoffoodadmin.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.apkfood.wavesoffoodadmin.R
import com.apkfood.wavesoffoodadmin.adapter.FoodAdapter
import com.apkfood.wavesoffoodadmin.model.Food
import com.apkfood.wavesoffoodadmin.viewmodel.FoodViewModel
import com.apkfood.wavesoffoodadmin.test.ImageSystemTest
import com.apkfood.wavesoffoodadmin.utils.FirebaseDebugger
import com.apkfood.wavesoffoodadmin.utils.CompleteFoodGenerator
import com.apkfood.wavesoffoodadmin.utils.SynchronizedFoodGenerator
import com.apkfood.wavesoffoodadmin.utils.EmergencyExecutor
import kotlinx.coroutines.launch

class FoodManagementActivity : AppCompatActivity() {
    
    private lateinit var foodViewModel: FoodViewModel
    private lateinit var foodAdapter: FoodAdapter
    
    // Views
    private lateinit var rvFoods: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var layoutEmptyState: LinearLayout
    private lateinit var loadingOverlay: LinearLayout
    private lateinit var fabAddFood: FloatingActionButton
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_management)
        
        initViews()
        setupToolbar()
        setupRecyclerView()
        setupViewModel()
        setupClickListeners()
        
        // Load foods automatically when activity starts
        loadFoods()
    }
    
    override fun onResume() {
        super.onResume()
        // Refresh data when returning to this activity
        loadFoods()
    }
    
    private fun loadFoods() {
        Log.d("FoodManagementActivity", "🔄 Loading foods...")
        foodViewModel.loadFoods()
    }
    
    private fun initViews() {
        rvFoods = findViewById(R.id.rvFoods)
        swipeRefresh = findViewById(R.id.swipeRefresh)
        layoutEmptyState = findViewById(R.id.layoutEmptyState)
        loadingOverlay = findViewById(R.id.loadingOverlay)
        fabAddFood = findViewById(R.id.fabAddFood)
    }
    
    private fun setupToolbar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }
    }
    
    private fun setupRecyclerView() {
        foodAdapter = FoodAdapter(
            onFoodClick = { food ->
                // TODO: Navigate to food details
                showFoodDetails(food)
            },
            onEditClick = { food ->
                // TODO: Navigate to edit food
                editFood(food)
            },
            onDeleteClick = { food ->
                showDeleteConfirmation(food)
            }
        )
        
        rvFoods.apply {
            layoutManager = LinearLayoutManager(this@FoodManagementActivity)
            adapter = foodAdapter
        }
    }
    
    private fun setupViewModel() {
        foodViewModel = ViewModelProvider(this)[FoodViewModel::class.java]
        
        foodViewModel.foods.observe(this) { foods ->
            Log.d("FoodManagementActivity", "Foods observed: ${foods.size} items")
            foods.forEach { food ->
                Log.d("FoodManagementActivity", "Food: ${food.name} - ${food.price}")
            }
            foodAdapter.submitList(foods)
            showEmptyState(foods.isEmpty())
        }
        
        foodViewModel.isLoading.observe(this) { isLoading ->
            Log.d("FoodManagementActivity", "Loading state: $isLoading")
            loadingOverlay.visibility = if (isLoading) View.VISIBLE else View.GONE
            swipeRefresh.isRefreshing = isLoading
        }
        
        foodViewModel.error.observe(this) { error ->
            Toast.makeText(this, error, Toast.LENGTH_LONG).show()
        }
        
        foodViewModel.operationResult.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Operation completed successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Operation failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun setupClickListeners() {
        swipeRefresh.setOnRefreshListener {
            foodViewModel.loadFoods()
        }
        
        fabAddFood.setOnClickListener {
            startActivity(Intent(this, AddFoodActivity::class.java))
        }
        
        // Long click on FAB to show options menu
        fabAddFood.setOnLongClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("Admin Debug Options")
                .setItems(arrayOf(
                    "🍽️ Add Sample Foods with Images",
                    "🧪 Test Image System",
                    "🔍 Debug Firebase Data",
                    "🔄 Force Refresh Firebase",
                    "🎯 Generate Complete Foods",
                    "⚡ SYNCHRONIZED FOODS (FIX!)",
                    "🔥 FORCE EXACT SAME DATA 🔥",
                    "🚨 EMERGENCY EXECUTE IDENTICAL DATA NOW! 🚨",
                    "❌ Cancel"
                )) { _, which ->
                    when (which) {
                        0 -> addSampleFoods()
                        1 -> testImageSystem()
                        2 -> debugFirebaseData()
                        3 -> forceRefreshFirebase()
                        4 -> generateCompleteFoods()
                        5 -> generateSynchronizedFoods()
                        6 -> forceExactSameData()
                        7 -> EmergencyExecutor.EXECUTE_FORCE_IDENTICAL_DATA_NOW(this@FoodManagementActivity)
                        8 -> { /* Cancel - do nothing */ }
                    }
                }
                .show()
            true
        }
    }
    
    private fun addSampleFoods() {
        loadingOverlay.visibility = View.VISIBLE
        foodViewModel.addSampleFoodsWithImages()
        Toast.makeText(this, "Adding sample foods with images...", Toast.LENGTH_SHORT).show()
    }
    
    private fun showEmptyState(isEmpty: Boolean) {
        rvFoods.visibility = if (isEmpty) View.GONE else View.VISIBLE
        layoutEmptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }
    
    private fun showFoodDetails(food: Food) {
        val message = """
            Name: ${food.name}
            Description: ${food.description}
            Price: Rp ${String.format("%,.0f", food.price)}
            Category ID: ${food.categoryId}
            Ingredients: ${food.ingredients.joinToString(", ")}
            Preparation Time: ${food.preparationTime} minutes
            Rating: ${food.rating}
            Available: ${if (food.isAvailable) "Yes" else "No"}
            Popular: ${if (food.isPopular) "Yes" else "No"}
        """.trimIndent()
        
        MaterialAlertDialogBuilder(this)
            .setTitle("Food Details")
            .setMessage(message)
            .setPositiveButton("Close", null)
            .show()
    }
    
    private fun editFood(food: Food) {
        val intent = EditFoodActivity.createIntent(this, food)
        startActivity(intent)
    }
    
    private fun showDeleteConfirmation(food: Food) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Delete Food")
            .setMessage("Are you sure you want to delete ${food.name}?")
            .setPositiveButton("Delete") { _, _ ->
                foodViewModel.deleteFood(food.id)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    /**
     * Test image system functionality
     */
    private fun testImageSystem() {
        lifecycleScope.launch {
            val imageTest = ImageSystemTest()
            
            Toast.makeText(this@FoodManagementActivity, "🔍 Running image system tests...", Toast.LENGTH_SHORT).show()
            
            val allTestsPassed = imageTest.runAllTests()
            
            if (allTestsPassed) {
                Toast.makeText(this@FoodManagementActivity, "✅ All tests passed! Image system working!", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this@FoodManagementActivity, "❌ Some tests failed. Check logs.", Toast.LENGTH_LONG).show()
            }
        }
    }
    
    /**
     * Debug Firebase data
     */
    private fun debugFirebaseData() {
        lifecycleScope.launch {
            val debugger = FirebaseDebugger()
            
            Toast.makeText(this@FoodManagementActivity, "🔍 Debugging Firebase data...", Toast.LENGTH_SHORT).show()
            
            val foods = debugger.debugFirebaseFoods()
            
            val base64Count = foods.count { it.imageUrl.startsWith("data:image") }
            val message = "Found ${foods.size} foods in Firebase\n" +
                         "Base64 images: $base64Count\n" +
                         "Check logs for details"
            
            MaterialAlertDialogBuilder(this@FoodManagementActivity)
                .setTitle("Firebase Debug Results")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show()
        }
    }
    
    /**
     * Force refresh Firebase with fresh sample data
     */
    private fun forceRefreshFirebase() {
        lifecycleScope.launch {
            val debugger = FirebaseDebugger()
            
            MaterialAlertDialogBuilder(this@FoodManagementActivity)
                .setTitle("Force Refresh Firebase")
                .setMessage("This will delete all existing foods and add fresh sample foods with Base64 images. Continue?")
                .setPositiveButton("Yes, Refresh") { _, _ ->
                    lifecycleScope.launch {
                        loadingOverlay.visibility = View.VISIBLE
                        
                        val success = debugger.forceRefreshSampleFoods()
                        
                        loadingOverlay.visibility = View.GONE
                        
                        if (success) {
                            Toast.makeText(this@FoodManagementActivity, "✅ Firebase refreshed successfully!", Toast.LENGTH_LONG).show()
                            foodViewModel.loadFoods() // Reload data
                        } else {
                            Toast.makeText(this@FoodManagementActivity, "❌ Failed to refresh Firebase", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }
    
    /**
     * Generate complete set of foods with matching images
     */
    private fun generateCompleteFoods() {
        Log.d("FoodManagement", "🎯 Generating complete foods with matching images...")
        
        MaterialAlertDialogBuilder(this)
            .setTitle("Generate Complete Foods")
            .setMessage("This will replace all existing foods with a complete set that matches the user app. Continue?")
            .setPositiveButton("Yes, Generate") { _, _ ->
                lifecycleScope.launch {
                    loadingOverlay.visibility = View.VISIBLE
                    
                    val generator = CompleteFoodGenerator()
                    val success = generator.generateCompleteFoodsWithImages()
                    
                    runOnUiThread {
                        loadingOverlay.visibility = View.GONE
                        if (success) {
                            Toast.makeText(this@FoodManagementActivity, 
                                "✅ Complete foods generated! Check both apps now.", 
                                Toast.LENGTH_LONG).show()
                            foodViewModel.loadFoods() // Refresh list
                        } else {
                            Toast.makeText(this@FoodManagementActivity, 
                                "❌ Failed to generate complete foods", 
                                Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    /**
     * Generate SYNCHRONIZED foods - PERSIS SAMA dengan user app
     */
    private fun generateSynchronizedFoods() {
        Log.d("FoodManagement", "⚡ Generating SYNCHRONIZED foods...")
        
        MaterialAlertDialogBuilder(this)
            .setTitle("🎯 SYNCHRONIZED FOODS FIX")
            .setMessage("This will generate foods with EXACT SAME images as user app. This should fix the image sync issue!")
            .setPositiveButton("YES, FIX IT!") { _, _ ->
                lifecycleScope.launch {
                    loadingOverlay.visibility = View.VISIBLE
                    
                    val generator = SynchronizedFoodGenerator()
                    val success = generator.generateSynchronizedFoods()
                    
                    runOnUiThread {
                        loadingOverlay.visibility = View.GONE
                        if (success) {
                            Toast.makeText(this@FoodManagementActivity, 
                                "✅ SYNCHRONIZED foods generated! Images should match now!", 
                                Toast.LENGTH_LONG).show()
                            loadFoods() // Refresh list
                        } else {
                            Toast.makeText(this@FoodManagementActivity, 
                                "❌ Failed to generate synchronized foods", 
                                Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun forceExactSameData() {
        MaterialAlertDialogBuilder(this)
            .setTitle("🔥 FORCE EXACT SAME DATA")
            .setMessage("Ini akan MEMAKSA kedua app menggunakan data yang PERSIS SAMA! Semua data akan dihapus dan diganti dengan data baru yang identik.")
            .setPositiveButton("🔥 FORCE NOW!") { _, _ ->
                lifecycleScope.launch {
                    try {
                        val generator = com.apkfood.wavesoffoodadmin.utils.ForceSameDataGenerator()
                        val success = generator.forceExactSameData()
                        
                        if (success) {
                            Toast.makeText(this@FoodManagementActivity, 
                                "🎉 FORCED EXACT SAME DATA! Both apps now use IDENTICAL data!", 
                                Toast.LENGTH_LONG).show()
                            loadFoods() // Refresh list
                        } else {
                            Toast.makeText(this@FoodManagementActivity, 
                                "❌ Failed to force same data", 
                                Toast.LENGTH_LONG).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@FoodManagementActivity, 
                            "❌ Error: ${e.message}", 
                            Toast.LENGTH_LONG).show()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
