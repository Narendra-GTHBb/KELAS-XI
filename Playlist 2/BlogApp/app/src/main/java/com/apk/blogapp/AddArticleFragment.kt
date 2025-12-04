package com.apk.blogapp

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.apk.blogapp.models.Article
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.Date

class AddArticleFragment : Fragment() {
    
    private lateinit var etTitle: EditText
    private lateinit var etDescription: EditText
    private lateinit var etContent: EditText
    private lateinit var btnPublish: Button
    private lateinit var btnSaveDraft: Button
    private lateinit var llImageUpload: LinearLayout
    private lateinit var ivImagePreview: ImageView
    private lateinit var tvImageUpload: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    
    private var selectedImageUri: Uri? = null
    private var selectedImageBase64: String = ""
    
    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            loadAndConvertImage(it)
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_article, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        
        initViews(view)
        setupClickListeners()
    }
    
    private fun initViews(view: View) {
        etTitle = view.findViewById(R.id.etTitle)
        etDescription = view.findViewById(R.id.etDescription)
        etContent = view.findViewById(R.id.etContent)
        btnPublish = view.findViewById(R.id.btnPublish)
        btnSaveDraft = view.findViewById(R.id.btnSaveDraft)
        llImageUpload = view.findViewById(R.id.llImageUpload)
        ivImagePreview = view.findViewById(R.id.ivImagePreview)
        tvImageUpload = view.findViewById(R.id.tvImageUpload)
    }
    
    private fun setupClickListeners() {
        btnPublish.setOnClickListener {
            publishArticle()
        }
        
        btnSaveDraft.setOnClickListener {
            saveDraft()
        }
        
        llImageUpload.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }
    }
    
    private fun publishArticle() {
        if (validateInput()) {
            createArticle("published")
        }
    }
    
    private fun saveDraft() {
        if (validateInput()) {
            createArticle("draft")
        }
    }
    
    private fun loadAndConvertImage(uri: Uri) {
        try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            originalBitmap?.let { bitmap ->
                // Resize proporsional maksimal 800px
                val maxSize = 800
                val ratio = Math.min(maxSize.toFloat() / bitmap.width, maxSize.toFloat() / bitmap.height)
                val newWidth = (bitmap.width * ratio).toInt()
                val newHeight = (bitmap.height * ratio).toInt()
                val scaledBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
                
                // Convert to base64
                val outputStream = ByteArrayOutputStream()
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
                val imageBytes = outputStream.toByteArray()
                selectedImageBase64 = Base64.encodeToString(imageBytes, Base64.DEFAULT)
                
                // Update UI
                ivImagePreview.setImageBitmap(scaledBitmap)
                ivImagePreview.scaleType = ImageView.ScaleType.CENTER_INSIDE
                tvImageUpload.text = "Image selected"
            }
        } catch (e: IOException) {
            Toast.makeText(context, "Failed to load image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateInput(): Boolean {
        val title = etTitle.text.toString().trim()
        val description = etDescription.text.toString().trim()
        val content = etContent.text.toString().trim()
        
        if (title.isEmpty()) {
            etTitle.error = "Please enter a title"
            etTitle.requestFocus()
            return false
        }
        
        if (description.isEmpty()) {
            etDescription.error = "Please enter a description"
            etDescription.requestFocus()
            return false
        }
        
        if (content.isEmpty()) {
            etContent.error = "Please enter content"
            etContent.requestFocus()
            return false
        }
        
        return true
    }
    
    private fun createArticle(status: String) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(context, "Please login to create articles", Toast.LENGTH_SHORT).show()
            return
        }
        
        btnPublish.isEnabled = false
        btnSaveDraft.isEnabled = false
        
        val title = etTitle.text.toString().trim()
        val description = etDescription.text.toString().trim()
        val content = etContent.text.toString().trim()
        
        val article = Article(
            title = title,
            description = description,
            content = content,
            category = "General", // Default category
            authorId = currentUser.uid,
            authorName = currentUser.displayName ?: currentUser.email ?: "Anonymous",
            authorAvatar = currentUser.photoUrl?.toString() ?: "",
            imageUrl = selectedImageBase64, // Use base64 image
            status = status,
            createdAt = Timestamp(Date()),
            updatedAt = Timestamp(Date()),
            publishedAt = if (status == "published") Timestamp(Date()) else null
        )
        
        firestore.collection("articles")
            .add(article)
            .addOnSuccessListener { documentReference ->
                btnPublish.isEnabled = true
                btnSaveDraft.isEnabled = true
                
                val message = if (status == "published") {
                    "Article published successfully!"
                } else {
                    "Article saved as draft!"
                }
                
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                clearFields()
                
                // Update user article count
                updateUserArticleCount(currentUser.uid)
                // Also add a reference copy under users/{uid}/myArticles for quick profile listing
                try {
                    val myArticleData = mapOf(
                        "id" to documentReference.id,
                        "title" to article.title,
                        "description" to article.description,
                        "content" to article.content,
                        "authorId" to article.authorId,
                        "authorName" to article.authorName,
                        "authorAvatar" to article.authorAvatar,
                        "imageUrl" to article.imageUrl,
                        "status" to article.status,
                        "createdAt" to article.createdAt,
                        "updatedAt" to article.updatedAt,
                        "publishedAt" to article.publishedAt,
                        "likesCount" to article.likesCount,
                        "commentsCount" to article.commentsCount,
                        "viewsCount" to article.viewsCount
                    )
                    firestore.collection("users").document(currentUser.uid)
                        .collection("myArticles").document(documentReference.id)
                        .set(myArticleData)
                } catch (e: Exception) {
                    // Non-fatal
                    android.util.Log.w("AddArticleFragment", "Failed to write myArticles copy: ${e.message}")
                }
                // Notify other fragments (Profile) that a new article was created
                parentFragmentManager.setFragmentResult("article_published", Bundle().apply {
                    putString("status", status)
                    putString("articleId", documentReference.id)
                })
            }
            .addOnFailureListener { exception ->
                btnPublish.isEnabled = true
                btnSaveDraft.isEnabled = true
                
                Toast.makeText(context, "Failed to save article: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }
    
    private fun updateUserArticleCount(userId: String) {
        firestore.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                val currentCount = document.getLong("articlesCount") ?: 0
                firestore.collection("users").document(userId)
                    .update("articlesCount", currentCount + 1)
            }
    }
    
    private fun clearFields() {
        etTitle.text.clear()
        etDescription.text.clear()
        etContent.text.clear()
        selectedImageUri = null
        selectedImageBase64 = ""
        ivImagePreview.setImageResource(android.R.drawable.ic_menu_camera)
        tvImageUpload.text = "Tap to add article image"
    }
}