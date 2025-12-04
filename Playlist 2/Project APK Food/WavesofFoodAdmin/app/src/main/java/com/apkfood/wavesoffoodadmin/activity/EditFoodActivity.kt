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
import com.apkfood.wavesoffoodadmin.utils.ImageLoaderAdmin

class EditFoodActivity : AppCompatActivity() {
    
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
    private var originalFood: Food? = null
    private var isImageChanged = false
    
    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val resultCode = result.resultCode
        val data = result.data
        
        if (resultCode == Activity.RESULT_OK) {
            val fileUri = data?.data
            fileUri?.let {
                selectedImageUri = it
                isImageChanged = true
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
                isImageChanged = true
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
        setContentView(R.layout.activity_edit_food)
        
        initViews()
        setupToolbar()
        setupViewModel()
        setupClickListeners()
        loadFoodData()
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
        supportActionBar?.title = "Edit Food"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }
    }
    
    private fun setupViewModel() {
        foodViewModel = ViewModelProvider(this)[FoodViewModel::class.java]
        
        foodViewModel.categories.observe(this) { categoryList ->
            categories = categoryList
            setupCategorySpinner(categoryList)
            originalFood?.let { food ->
                selectCategoryById(food.categoryId)
            }
        }
        
        foodViewModel.base64Image.observe(this) { base64String ->
            base64ImageString = base64String
        }
        
        foodViewModel.operationResult.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Food updated successfully!", Toast.LENGTH_SHORT).show()
                setResult(Activity.RESULT_OK)
                finish()
            } else {
                Toast.makeText(this, "Failed to update food", Toast.LENGTH_SHORT).show()
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
                updateFood()
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
    
    private fun loadFoodData() {
        val foodId = intent.getStringExtra("FOOD_ID")
        val foodName = intent.getStringExtra("FOOD_NAME")
        val foodDescription = intent.getStringExtra("FOOD_DESCRIPTION")
        val foodPrice = intent.getDoubleExtra("FOOD_PRICE", 0.0)
        val foodImageUrl = intent.getStringExtra("FOOD_IMAGE_URL")
        val foodCategoryId = intent.getStringExtra("FOOD_CATEGORY_ID")
        val foodIngredientsString = intent.getStringExtra("FOOD_INGREDIENTS")
        val foodIsAvailable = intent.getBooleanExtra("FOOD_IS_AVAILABLE", true)
        val foodIsPopular = intent.getBooleanExtra("FOOD_IS_POPULAR", false)
        val foodPrepTime = intent.getIntExtra("FOOD_PREP_TIME", 0)
        val foodRating = intent.getDoubleExtra("FOOD_RATING", 0.0)
        val foodCreatedAt = intent.getLongExtra("FOOD_CREATED_AT", System.currentTimeMillis())
        
        if (foodId != null && foodName != null) {
            // Parse ingredients from string
            val foodIngredients = if (foodIngredientsString.isNullOrEmpty()) {
                emptyList()
            } else {
                foodIngredientsString.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            }
            
            originalFood = Food(
                id = foodId,
                name = foodName,
                description = foodDescription ?: "",
                price = foodPrice,
                imageUrl = foodImageUrl ?: "",
                categoryId = foodCategoryId ?: "",
                ingredients = foodIngredients,
                isAvailable = foodIsAvailable,
                isPopular = foodIsPopular,
                preparationTime = foodPrepTime,
                rating = foodRating,
                createdAt = foodCreatedAt,
                updatedAt = System.currentTimeMillis()
            )
            
            populateFields(originalFood!!)
        } else {
            Toast.makeText(this, "Error loading food data", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
    
    private fun populateFields(food: Food) {
        etFoodName.setText(food.name)
        etFoodDescription.setText(food.description)
        etFoodPrice.setText(food.price.toString())
        etPreparationTime.setText(food.preparationTime.toString())
        etIngredients.setText(food.ingredients.joinToString(", "))
        switchAvailable.isChecked = food.isAvailable
        switchPopular.isChecked = food.isPopular
        
        // Load current image
        ImageLoaderAdmin.loadImage(this, food.imageUrl, ivFoodImage, 12)
        
        // Store original image for update
        base64ImageString = food.imageUrl
    }
    
    private fun setupCategorySpinner(categoryList: List<FoodCategory>) {
        val categoryNames = categoryList.map { it.name }
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categoryNames)
        actvCategory.setAdapter(adapter)
    }
    
    private fun selectCategoryById(categoryId: String) {
        val category = categories.find { it.id == categoryId }
        category?.let {
            actvCategory.setText(it.name, false)
        }
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
    
    private fun updateFood() {
        val original = originalFood ?: return
        loadingOverlay.visibility = View.VISIBLE
        
        // Get category ID from selected category name
        val selectedCategoryName = actvCategory.text.toString().trim()
        val selectedCategory = categories.find { it.name == selectedCategoryName }
        val categoryId = selectedCategory?.id ?: original.categoryId
        
        // Create updated food object
        val ingredients = etIngredients.text.toString().split(",").map { it.trim() }.filter { it.isNotEmpty() }
        
        val updatedFood = original.copy(
            name = etFoodName.text.toString().trim(),
            description = etFoodDescription.text.toString().trim(),
            price = etFoodPrice.text.toString().toDoubleOrNull() ?: original.price,
            imageUrl = if (isImageChanged) base64ImageString else original.imageUrl,
            categoryId = categoryId,
            ingredients = ingredients,
            isAvailable = switchAvailable.isChecked,
            isPopular = switchPopular.isChecked,
            preparationTime = etPreparationTime.text.toString().toIntOrNull() ?: original.preparationTime,
            updatedAt = System.currentTimeMillis()
        )
        
        foodViewModel.updateFood(updatedFood)
    }
    
    companion object {
        fun createIntent(
            context: android.content.Context,
            food: Food
        ): Intent {
            return Intent(context, EditFoodActivity::class.java).apply {
                putExtra("FOOD_ID", food.id)
                putExtra("FOOD_NAME", food.name)
                putExtra("FOOD_DESCRIPTION", food.description)
                putExtra("FOOD_PRICE", food.price)
                putExtra("FOOD_IMAGE_URL", food.imageUrl)
                putExtra("FOOD_CATEGORY_ID", food.categoryId)
                putExtra("FOOD_INGREDIENTS", food.ingredients.joinToString(","))
                putExtra("FOOD_IS_AVAILABLE", food.isAvailable)
                putExtra("FOOD_IS_POPULAR", food.isPopular)
                putExtra("FOOD_PREP_TIME", food.preparationTime)
                putExtra("FOOD_RATING", food.rating)
                putExtra("FOOD_CREATED_AT", food.getCreatedAtLong())
            }
        }
    }
}