package com.apkfood.wavesoffoodadmin.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.apkfood.wavesoffoodadmin.R
import com.apkfood.wavesoffoodadmin.model.Food
import com.apkfood.wavesoffoodadmin.model.FoodCategory
import com.apkfood.wavesoffoodadmin.viewmodel.FoodViewModel
import java.util.*

class AddFoodActivity : AppCompatActivity() {
    
    private lateinit var foodViewModel: FoodViewModel
    
    // Views
    private lateinit var ivFoodImage: ImageView
    private lateinit var etFoodName: TextInputEditText
    private lateinit var etFoodDescription: TextInputEditText
    private lateinit var etFoodPrice: TextInputEditText
    private lateinit var etPreparationTime: TextInputEditText
    private lateinit var actvCategory: AutoCompleteTextView
    private lateinit var etIngredients: TextInputEditText
    private lateinit var switchAvailable: SwitchMaterial
    private lateinit var switchPopular: SwitchMaterial
    private lateinit var btnSave: MaterialButton
    private lateinit var btnCancel: MaterialButton
    private lateinit var loadingOverlay: LinearLayout
    
    private var selectedImageUri: Uri? = null
    private var base64ImageString: String = ""
    private var categories: List<FoodCategory> = emptyList()
    
    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val resultCode = result.resultCode
        val data = result.data
        
        if (resultCode == Activity.RESULT_OK) {
            val fileUri = data?.data
            fileUri?.let {
                selectedImageUri = it
                Glide.with(this)
                    .load(it)
                    .centerCrop()
                    .into(ivFoodImage)
                
                // Convert to base64
                foodViewModel.convertImageToBase64(it, this)
            }
        }
    }
    
    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val resultCode = result.resultCode
        val data = result.data
        
        if (resultCode == Activity.RESULT_OK) {
            val fileUri = data?.data
            fileUri?.let {
                selectedImageUri = it
                Glide.with(this)
                    .load(it)
                    .centerCrop()
                    .into(ivFoodImage)
                
                // Convert to base64
                foodViewModel.convertImageToBase64(it, this)
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_food)
        
        initViews()
        setupToolbar()
        setupViewModel()
        setupClickListeners()
    }
    
    private fun initViews() {
        ivFoodImage = findViewById(R.id.ivFoodImage)
        etFoodName = findViewById(R.id.etFoodName)
        etFoodDescription = findViewById(R.id.etFoodDescription)
        etFoodPrice = findViewById(R.id.etFoodPrice)
        etPreparationTime = findViewById(R.id.etPreparationTime)
        actvCategory = findViewById(R.id.actvCategory)
        etIngredients = findViewById(R.id.etIngredients)
        switchAvailable = findViewById(R.id.switchAvailable)
        switchPopular = findViewById(R.id.switchPopular)
        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)
        loadingOverlay = findViewById(R.id.loadingOverlay)
    }
    
    private fun setupToolbar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }
    }
    
    private fun setupViewModel() {
        foodViewModel = ViewModelProvider(this)[FoodViewModel::class.java]
        
        foodViewModel.categories.observe(this) { categoryList ->
            categories = categoryList
            setupCategorySpinner(categoryList)
        }
        
        foodViewModel.base64Image.observe(this) { base64String ->
            base64ImageString = base64String
        }
        
        foodViewModel.operationResult.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Food added successfully!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Failed to add food", Toast.LENGTH_SHORT).show()
            }
        }
        
        foodViewModel.isLoading.observe(this) { isLoading ->
            loadingOverlay.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        foodViewModel.error.observe(this) { error ->
            Toast.makeText(this, error, Toast.LENGTH_LONG).show()
        }
        
        // Load categories
        foodViewModel.loadCategories()
    }
    
    private fun setupClickListeners() {
        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fabSelectImage).setOnClickListener {
            showImagePickerDialog()
        }
        
        btnSave.setOnClickListener {
            if (validateInput()) {
                saveFood()
            }
        }
        
        btnCancel.setOnClickListener {
            finish()
        }
    }
    
    private fun showImagePickerDialog() {
        val options = arrayOf(
            "📷 Camera",
            "🖼️ Gallery", 
            "📁 File Browser (Laptop Files)",
            "❌ Cancel"
        )
        
        MaterialAlertDialogBuilder(this)
            .setTitle("Select Image Source")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openCamera()
                    1 -> openGallery()
                    2 -> openFileBrowser()
                    3 -> { /* Cancel - do nothing */ }
                }
            }
            .show()
    }
    
    private fun openCamera() {
        ImagePicker.with(this)
            .cameraOnly()
            .crop()
            .compress(1024)
            .maxResultSize(800, 600)
            .createIntent { intent ->
                imagePickerLauncher.launch(intent)
            }
    }
    
    private fun openGallery() {
        ImagePicker.with(this)
            .galleryOnly()
            .crop()
            .compress(1024)
            .maxResultSize(800, 600)
            .createIntent { intent ->
                imagePickerLauncher.launch(intent)
            }
    }
    
    private fun openFileBrowser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/jpg", "image/png", "image/webp"))
        }
        
        try {
            filePickerLauncher.launch(Intent.createChooser(intent, "Select Image from Laptop"))
        } catch (e: Exception) {
            Toast.makeText(this, "No file manager found. Please install a file manager app.", Toast.LENGTH_LONG).show()
        }
    }
    
    private fun setupCategorySpinner(categoryList: List<FoodCategory>) {
        val categoryNames = categoryList.map { it.name }
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categoryNames)
        actvCategory.setAdapter(adapter)
    }
    
    private fun validateInput(): Boolean {
        if (etFoodName.text.toString().trim().isEmpty()) {
            etFoodName.error = "Food name is required"
            etFoodName.requestFocus()
            return false
        }
        
        if (etFoodDescription.text.toString().trim().isEmpty()) {
            etFoodDescription.error = "Description is required"
            etFoodDescription.requestFocus()
            return false
        }
        
        if (etFoodPrice.text.toString().trim().isEmpty()) {
            etFoodPrice.error = "Price is required"
            etFoodPrice.requestFocus()
            return false
        }
        
        if (actvCategory.text.toString().trim().isEmpty()) {
            actvCategory.error = "Category is required"
            actvCategory.requestFocus()
            return false
        }
        
        return true
    }
    
    private fun saveFood() {
        loadingOverlay.visibility = View.VISIBLE
        
        // Get category ID from selected category name
        val selectedCategoryName = actvCategory.text.toString().trim()
        val selectedCategory = categories.find { it.name == selectedCategoryName }
        val categoryId = selectedCategory?.id ?: ""
        
        // Create food object
        val ingredients = etIngredients.text.toString().split(",").map { it.trim() }.filter { it.isNotEmpty() }
        
        val food = Food(
            name = etFoodName.text.toString().trim(),
            description = etFoodDescription.text.toString().trim(),
            price = etFoodPrice.text.toString().toDoubleOrNull() ?: 0.0,
            imageUrl = base64ImageString,
            categoryId = categoryId,
            ingredients = ingredients,
            isAvailable = switchAvailable.isChecked,
            isPopular = switchPopular.isChecked,
            preparationTime = etPreparationTime.text.toString().toIntOrNull() ?: 0,
            rating = 0.0, // Default rating for new food
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        
        foodViewModel.addFood(food)
    }
}
