package com.apk.blogapp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.apk.blogapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream
import java.io.IOException

class EditProfileActivity : AppCompatActivity() {
    
    private lateinit var ivProfileImage: ImageView
    private lateinit var etFullName: EditText
    private lateinit var etBio: EditText
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button
    private lateinit var btnChangePhoto: Button
    private lateinit var progressBar: ProgressBar
    
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var selectedImageBitmap: Bitmap? = null
    private var currentUser: User? = null
    
    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri = result.data?.data
            if (imageUri != null) {
                loadImageFromUri(imageUri)
            }
        }
    }
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openImagePicker()
        } else {
            Toast.makeText(this, "Permission needed to access photos", Toast.LENGTH_SHORT).show()
        }
    }
    
    private val requestMultiplePermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.values.all { it }
        if (granted) {
            openImagePicker()
        } else {
            Toast.makeText(this, "Permission needed to access photos", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        
        initViews()
        setupClickListeners()
        loadCurrentUserData()
    }
    
    private fun initViews() {
        ivProfileImage = findViewById(R.id.ivProfileImage)
        etFullName = findViewById(R.id.etFullName)
        etBio = findViewById(R.id.etBio)
        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)
        btnChangePhoto = findViewById(R.id.btnChangePhoto)
        progressBar = findViewById(R.id.progressBar)
        
        // Setup toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Edit Profile"
    }
    
    private fun setupClickListeners() {
        btnSave.setOnClickListener {
            saveProfile()
        }
        
        btnCancel.setOnClickListener {
            finish()
        }
        
        btnChangePhoto.setOnClickListener {
            checkPermissionAndOpenPicker()
        }
    }
    
    private fun loadCurrentUserData() {
        val firebaseUser = auth.currentUser ?: return
        
        showLoading(true)
        
        firestore.collection("users")
            .document(firebaseUser.uid)
            .get()
            .addOnSuccessListener { document ->
                showLoading(false)
                if (document.exists()) {
                    currentUser = document.toObject(User::class.java)
                    currentUser?.let { populateFields(it) }
                } else {
                    // Create default user data
                    currentUser = User(
                        id = firebaseUser.uid,
                        email = firebaseUser.email ?: "",
                        fullName = firebaseUser.displayName ?: "User",
                        username = firebaseUser.email?.substringBefore("@") ?: "user",
                        bio = "",
                        profileImageUrl = ""
                    )
                    populateFields(currentUser!!)
                }
            }
            .addOnFailureListener {
                showLoading(false)
                Toast.makeText(this, "Failed to load profile data", Toast.LENGTH_SHORT).show()
            }
    }
    
    private fun populateFields(user: User) {
        etFullName.setText(user.fullName)
        etBio.setText(user.bio)
        
        // Load profile image if exists
        if (user.profileImageUrl.isNotEmpty()) {
            try {
                val decodedBytes = Base64.decode(user.profileImageUrl, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                ivProfileImage.setImageBitmap(bitmap)
            } catch (e: Exception) {
                // Set default avatar if image loading fails
                ivProfileImage.setImageResource(R.drawable.ic_launcher_foreground)
            }
        } else {
            ivProfileImage.setImageResource(R.drawable.ic_launcher_foreground)
        }
    }
    
    private fun checkPermissionAndOpenPicker() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                // Android 13+ (API 33+) - use READ_MEDIA_IMAGES
                when {
                    ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_MEDIA_IMAGES
                    ) == PackageManager.PERMISSION_GRANTED -> {
                        openImagePicker()
                    }
                    else -> {
                        requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                    }
                }
            }
            else -> {
                // Android 12 and below - use READ_EXTERNAL_STORAGE
                when {
                    ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED -> {
                        openImagePicker()
                    }
                    else -> {
                        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                }
            }
        }
    }
    
    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        imagePickerLauncher.launch(intent)
    }
    
    private fun loadImageFromUri(uri: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(uri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            
            // Create square bitmap with proper scaling
            selectedImageBitmap = originalBitmap?.let { bitmap ->
                val size = Math.min(bitmap.width, bitmap.height)
                val x = (bitmap.width - size) / 2
                val y = (bitmap.height - size) / 2
                
                // Crop to square
                val squareBitmap = Bitmap.createBitmap(bitmap, x, y, size, size)
                
                // Scale to reasonable size (512x512)
                val finalSize = 512
                Bitmap.createScaledBitmap(squareBitmap, finalSize, finalSize, true)
            }
            
            ivProfileImage.setImageBitmap(selectedImageBitmap)
            
        } catch (e: IOException) {
            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun saveProfile() {
        val fullName = etFullName.text.toString().trim()
        val bio = etBio.text.toString().trim()
        
        if (fullName.isEmpty()) {
            etFullName.error = "Name cannot be empty"
            return
        }
        
        showLoading(true)
        
        // Convert image to base64 if selected
        val base64Image = selectedImageBitmap?.let { bitmap ->
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            val imageBytes = outputStream.toByteArray()
            Base64.encodeToString(imageBytes, Base64.DEFAULT)
        } ?: currentUser?.profileImageUrl ?: ""
        
        // Prepare updated user data
        val updatedUser = currentUser?.copy(
            fullName = fullName,
            bio = bio,
            username = fullName.lowercase().replace(" ", "_"),
            profileImageUrl = base64Image
        ) ?: return
        
        // Save to Firestore
        firestore.collection("users")
            .document(updatedUser.id)
            .set(updatedUser)
            .addOnSuccessListener {
                showLoading(false)
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                setResult(Activity.RESULT_OK)
                finish()
            }
            .addOnFailureListener {
                showLoading(false)
                Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
            }
    }
    
    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        btnSave.isEnabled = !show
        btnChangePhoto.isEnabled = !show
    }
    
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}