package com.apk.blogapp

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.apk.blogapp.R
import com.apk.blogapp.models.Article
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.graphics.BitmapFactory
import android.util.Base64

class ArticleDetailActivity : AppCompatActivity() {
    private lateinit var ivArticleImage: ImageView
    private lateinit var tvTitle: TextView
    private lateinit var tvAuthor: TextView
    private lateinit var tvDate: TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvContent: TextView
    private lateinit var btnLike: ImageButton
    private lateinit var btnFavorite: ImageButton
    private lateinit var tvLikesCount: TextView

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var articleId: String = ""
    private var isFavorited = false
    private var isLiked = false
    private var likesCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article_detail)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        ivArticleImage = findViewById(R.id.ivArticleImage)
        ivArticleImage.scaleType = ImageView.ScaleType.CENTER_INSIDE
        tvTitle = findViewById(R.id.tvTitle)
        tvAuthor = findViewById(R.id.tvAuthor)
        tvDate = findViewById(R.id.tvDate)
        tvDescription = findViewById(R.id.tvDescription)
        tvContent = findViewById(R.id.tvContent)
        btnLike = findViewById(R.id.btnLike)
        btnFavorite = findViewById(R.id.btnFavorite)
        tvLikesCount = findViewById(R.id.tvLikesCount)

        articleId = intent.getStringExtra("articleId") ?: ""
        if (articleId.isNotEmpty()) {
            loadArticleDetail(articleId)
        }

        btnLike.setOnClickListener {
            toggleLike()
        }
        btnFavorite.setOnClickListener {
            toggleFavorite()
        }
    }

    private fun loadArticleDetail(id: String) {
        firestore.collection("articles").document(id).get()
            .addOnSuccessListener { doc ->
                val article = doc.toObject(Article::class.java)
                if (article != null) {
                    tvTitle.text = article.title
                    tvAuthor.text = article.authorName
                    tvDate.text = article.getFormattedDate()
                    tvDescription.text = article.description
                    tvContent.text = article.content
                    likesCount = article.likesCount
                    tvLikesCount.text = likesCount.toString()
                    // Load image
                    if (article.imageUrl.isNotEmpty()) {
                        try {
                            val decodedBytes = Base64.decode(article.imageUrl, Base64.DEFAULT)
                            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                            ivArticleImage.setImageBitmap(bitmap)
                        } catch (e: Exception) {
                            ivArticleImage.setImageResource(R.drawable.ic_launcher_foreground)
                        }
                    } else {
                        ivArticleImage.setImageResource(R.drawable.ic_launcher_foreground)
                    }
                    checkFavoriteStatus(id)
                    checkLikeStatus(id)
                }
            }
    }

    private fun toggleLike() {
        val userId = auth.currentUser?.uid ?: return
        val likeRef = firestore.collection("articles").document(articleId).collection("likes").document(userId)
        if (isLiked) {
            likeRef.delete()
            likesCount--
            tvLikesCount.text = likesCount.toString()
            btnLike.setImageResource(R.drawable.ic_like_outline)
        } else {
            likeRef.set(mapOf("liked" to true))
            likesCount++
            tvLikesCount.text = likesCount.toString()
            btnLike.setImageResource(R.drawable.ic_like_filled)
        }
        isLiked = !isLiked
        // Update likesCount in article document
        firestore.collection("articles").document(articleId).update("likesCount", likesCount)
    }

    private fun checkLikeStatus(id: String) {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("articles").document(id).collection("likes").document(userId)
            .get().addOnSuccessListener { doc ->
                isLiked = doc.exists()
                btnLike.setImageResource(if (isLiked) R.drawable.ic_like_filled else R.drawable.ic_like_outline)
            }
    }

    private fun toggleFavorite() {
        android.util.Log.d("ArticleDetailActivity", "toggleFavorite called, articleId=$articleId")
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "User belum login!", Toast.LENGTH_LONG).show()
            android.util.Log.e("ArticleDetailActivity", "User belum login!")
            return
        }
        if (articleId.isEmpty()) {
            Toast.makeText(this, "ArticleId kosong!", Toast.LENGTH_LONG).show()
            android.util.Log.e("ArticleDetailActivity", "ArticleId kosong!")
            return
        }
        val favRef = firestore.collection("users").document(userId).collection("favorites").document(articleId)
        if (isFavorited) {
            favRef.delete()
                .addOnSuccessListener {
                    isFavorited = false
                    btnFavorite.setImageResource(R.drawable.ic_favorite_outline)
                    Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show()
                    android.util.Log.d("ArticleDetailActivity", "Removed from favorites: $articleId")
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to remove favorite: ${it.message}", Toast.LENGTH_LONG).show()
                    android.util.Log.e("ArticleDetailActivity", "Failed to remove favorite: ${it.message}")
                }
        } else {
            firestore.collection("articles").document(articleId).get()
                .addOnSuccessListener { doc ->
                    val article = doc.toObject(Article::class.java)
                    if (article != null) {
                        val favoriteData = mapOf(
                            "id" to article.id,
                            "title" to article.title,
                            "content" to article.content,
                            "description" to article.description,
                            "authorId" to article.authorId,
                            "authorName" to article.authorName,
                            "authorAvatar" to article.authorAvatar,
                            "imageUrl" to article.imageUrl,
                            "category" to article.category,
                            "tags" to article.tags,
                            "likesCount" to article.likesCount,
                            "commentsCount" to article.commentsCount,
                            "viewsCount" to article.viewsCount,
                            "status" to article.status,
                            "createdAt" to article.createdAt,
                            "updatedAt" to article.updatedAt,
                            "publishedAt" to article.publishedAt
                        )
                        favRef.set(favoriteData)
                            .addOnSuccessListener {
                                isFavorited = true
                                btnFavorite.setImageResource(R.drawable.ic_favorite_filled)
                                Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show()
                                android.util.Log.d("ArticleDetailActivity", "Added to favorites: $articleId")
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Failed to add favorite: ${it.message}", Toast.LENGTH_LONG).show()
                                android.util.Log.e("ArticleDetailActivity", "Failed to add favorite: ${it.message}")
                            }
                    } else {
                        Toast.makeText(this, "Failed to get article data", Toast.LENGTH_LONG).show()
                        android.util.Log.e("ArticleDetailActivity", "Failed to get article data")
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to get article: ${it.message}", Toast.LENGTH_LONG).show()
                    android.util.Log.e("ArticleDetailActivity", "Failed to get article: ${it.message}")
                }
        }
    }

    private fun checkFavoriteStatus(id: String) {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("users").document(userId).collection("favorites").document(id)
            .get().addOnSuccessListener { doc ->
                isFavorited = doc.exists()
                btnFavorite.setImageResource(if (isFavorited) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_outline)
            }
    }
}
