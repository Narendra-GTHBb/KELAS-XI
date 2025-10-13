package com.apk.blogapp

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apk.blogapp.adapters.ArticleAdapter
import com.apk.blogapp.models.Article
import com.apk.blogapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ProfileFragment : Fragment() {
    
    private lateinit var ivProfileAvatar: ImageView
    private lateinit var tvUserName: TextView
    private lateinit var tvUserEmail: TextView
    private lateinit var tvUserBio: TextView
    private lateinit var tvArticleCount: TextView
    private lateinit var tvFollowersCount: TextView
    private lateinit var tvFollowingCount: TextView
    private lateinit var tvLikesCount: TextView
    private lateinit var recyclerViewMyArticles: RecyclerView
    private lateinit var btnEditProfile: Button
    private lateinit var btnLogout: Button
    private lateinit var btnRefreshArticles: android.widget.ImageButton
    private lateinit var articleAdapter: ArticleAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var recyclerViewFavorites: RecyclerView
    private lateinit var favoritesAdapter: ArticleAdapter
    
    private val editProfileLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            // Refresh user data after editing
            loadUserData()
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        
        initViews(view)
        setupClickListeners()
        setupRecyclerView()
        loadUserData()
        loadMyArticles()
        
        recyclerViewFavorites = view.findViewById(R.id.recyclerViewFavorites)
        favoritesAdapter = ArticleAdapter(
            onItemClick = { article ->
                val intent = Intent(context, ArticleDetailActivity::class.java)
                intent.putExtra("articleId", article.id)
                startActivity(intent)
            }
            ,
            onBookmarkClick = { article, btn ->
                val currentUser = auth.currentUser ?: return@ArticleAdapter
                val favRef = firestore.collection("users").document(currentUser.uid).collection("favorites").document(article.id)
                favRef.delete().addOnSuccessListener {
                    Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show()
                    loadFavoriteArticles()
                }.addOnFailureListener { e ->
                    Toast.makeText(context, "Failed to remove favorite: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
            ,
            onBindBookmarkState = { _, btn ->
                // In favorites list all items are favorited
                btn.setImageResource(R.drawable.ic_favorite_filled)
            }
        )
        recyclerViewFavorites.layoutManager = LinearLayoutManager(context)
        recyclerViewFavorites.adapter = favoritesAdapter
        loadFavoriteArticles()

        // Listen for article publish events to refresh lists
        parentFragmentManager.setFragmentResultListener("article_published", viewLifecycleOwner) { key, bundle ->
            android.util.Log.d("ProfileFragment", "Received article_published result: ${bundle.getString("articleId")}")
            // Reload articles and favorites
            loadMyArticles()
            loadFavoriteArticles()
        }
        // Refresh button (if present)
        try {
            btnRefreshArticles = view.findViewById(R.id.btnRefreshArticles)
            btnRefreshArticles.setOnClickListener {
                loadMyArticles()
                loadFavoriteArticles()
                Toast.makeText(context, "Refreshed", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            // No-op if view not present
        }
    }
    
    private fun initViews(view: View) {
        ivProfileAvatar = view.findViewById(R.id.ivProfileAvatar)
        tvUserName = view.findViewById(R.id.tvUserName)
        tvUserEmail = view.findViewById(R.id.tvUserEmail)
        tvUserBio = view.findViewById(R.id.tvUserBio)
        tvArticleCount = view.findViewById(R.id.tvArticleCount)
        tvFollowersCount = view.findViewById(R.id.tvFollowersCount)
        tvFollowingCount = view.findViewById(R.id.tvFollowingCount)
        tvLikesCount = view.findViewById(R.id.tvLikesCount)
        recyclerViewMyArticles = view.findViewById(R.id.recyclerViewMyArticles)
        btnEditProfile = view.findViewById(R.id.btnEditProfile)
        btnLogout = view.findViewById(R.id.btnLogout)
        // btnRefreshArticles is optional; findViewById handled in onViewCreated to avoid NPE
    }
    
    private fun setupClickListeners() {
        btnEditProfile.setOnClickListener {
            val intent = Intent(context, EditProfileActivity::class.java)
            editProfileLauncher.launch(intent)
        }
        
        btnLogout.setOnClickListener {
            auth.signOut()
            val intent = Intent(context, WelcomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
    
    private fun setupRecyclerView() {
        articleAdapter = ArticleAdapter(
            onItemClick = { article ->
                Toast.makeText(context, "Opening: ${article.title}", Toast.LENGTH_SHORT).show()
            },
            onAuthorClick = { article ->
                // User clicked on their own profile
            },
            onLikeClick = { article ->
                Toast.makeText(context, "Liked: ${article.title}", Toast.LENGTH_SHORT).show()
            },
            onShareClick = { article ->
                Toast.makeText(context, "Sharing: ${article.title}", Toast.LENGTH_SHORT).show()
            }
        )
        
        recyclerViewMyArticles.layoutManager = LinearLayoutManager(context)
        recyclerViewMyArticles.adapter = articleAdapter
    }
    
    private fun loadUserData() {
        val currentUser = auth.currentUser ?: return
        
        // Display basic info
        tvUserEmail.text = currentUser.email ?: "No email"
        tvUserName.text = currentUser.displayName ?: "User"
        
        // Load additional user data from Firestore
        firestore.collection("users")
            .document(currentUser.uid)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val user = document.toObject(User::class.java)
                    user?.let { updateUI(it) }
                } else {
                    // Create user document if it doesn't exist
                    createUserDocument(currentUser.uid)
                }
            }
            .addOnFailureListener {
                // Set default values
                setDefaultUserStats()
            }
    }
    
    private fun createUserDocument(userId: String) {
        val currentUser = auth.currentUser ?: return
        
        val user = User(
            id = userId,
            email = currentUser.email ?: "",
            fullName = currentUser.displayName ?: "User",
            username = currentUser.email?.substringBefore("@") ?: "user",
            bio = "Welcome to my profile!",
            profileImageUrl = currentUser.photoUrl?.toString() ?: ""
        )
        
        firestore.collection("users")
            .document(userId)
            .set(user)
            .addOnSuccessListener {
                updateUI(user)
            }
    }
    
    private fun updateUI(user: User) {
        tvUserName.text = user.fullName
        tvUserBio.text = user.bio.ifEmpty { "Welcome to my profile!" }
        tvArticleCount.text = user.articlesCount.toString()
        tvFollowersCount.text = user.followersCount.toString()
        tvFollowingCount.text = user.followingCount.toString()
        tvLikesCount.text = user.totalLikes.toString()
        
        // Load profile image with proper scaling
        if (user.profileImageUrl.isNotEmpty()) {
            try {
                val decodedBytes = Base64.decode(user.profileImageUrl, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                ivProfileAvatar.setImageBitmap(bitmap)
            } catch (e: Exception) {
                // Set default avatar if image loading fails
                ivProfileAvatar.setImageResource(R.drawable.ic_launcher_foreground)
            }
        } else {
            // Set default avatar
            ivProfileAvatar.setImageResource(R.drawable.ic_launcher_foreground)
        }
    }
    
    private fun setDefaultUserStats() {
        tvUserBio.text = "Welcome to my profile!"
        tvArticleCount.text = "0"
        tvFollowersCount.text = "0"
        tvFollowingCount.text = "0"
        tvLikesCount.text = "0"
    }
    
    private fun loadMyArticles() {
        val currentUser = auth.currentUser ?: return
        // First try: load from users/{uid}/myArticles without server-side ordering to avoid index needs
        firestore.collection("users").document(currentUser.uid)
            .collection("myArticles")
            .get()
            .addOnSuccessListener { docs ->
                android.util.Log.d("ProfileFragment", "loadMyArticles - got ${docs.size()} docs from myArticles")
                val list = mutableListOf<Article>()
                for (d in docs) {
                    try {
                        val a = d.toObject(Article::class.java)
                        a.id = d.id
                        list.add(a)
                    } catch (e: Exception) {
                        android.util.Log.e("ProfileFragment", "Error parsing myArticles ${d.id}: ${e.message}")
                    }
                }
                // Sort client-side by createdAt (safe if field exists)
                val sorted = list.sortedByDescending { it.createdAt?.toDate()?.time ?: 0L }
                if (sorted.isNotEmpty()) {
                    articleAdapter.updateArticles(sorted)
                    android.util.Log.d("ProfileFragment", "After updateArticles (myArticles) adapter count=${sorted.size}")
                    // Debug: log recycler view state
                    android.util.Log.d("ProfileFragment", "recyclerViewMyArticles: width=${recyclerViewMyArticles.width} height=${recyclerViewMyArticles.height} isAttached=${recyclerViewMyArticles.isAttachedToWindow} adapter=${recyclerViewMyArticles.adapter}")
                    // Force a UI refresh and ensure top is visible
                    recyclerViewMyArticles.invalidate()
                    recyclerViewMyArticles.post { recyclerViewMyArticles.scrollToPosition(0) }
                    tvArticleCount.text = sorted.count { it.status == "published" }.toString()
                } else {
                    // Fallback to querying articles collection directly without orderBy
                    android.util.Log.w("ProfileFragment", "No docs under users/{uid}/myArticles - falling back to articles collection")
                    firestore.collection("articles")
                        .whereEqualTo("authorId", currentUser.uid)
                        .get()
                        .addOnSuccessListener { documents ->
                            val articles = mutableListOf<Article>()
                            for (document in documents) {
                                try {
                                    val article = document.toObject(Article::class.java)
                                    article.id = document.id
                                    articles.add(article)
                                } catch (e: Exception) {
                                    android.util.Log.e("ProfileFragment", "Error parsing my article ${document.id}: ${e.message}")
                                }
                            }
                            val sortedArticles = articles.sortedByDescending { it.createdAt?.toDate()?.time ?: 0L }
                            articleAdapter.updateArticles(sortedArticles)
                            android.util.Log.d("ProfileFragment", "After updateArticles (articles fallback) adapter count=${sortedArticles.size}")
                            android.util.Log.d("ProfileFragment", "recyclerViewMyArticles: width=${recyclerViewMyArticles.width} height=${recyclerViewMyArticles.height} isAttached=${recyclerViewMyArticles.isAttachedToWindow} adapter=${recyclerViewMyArticles.adapter}")
                            recyclerViewMyArticles.invalidate()
                            recyclerViewMyArticles.post { recyclerViewMyArticles.scrollToPosition(0) }
                            tvArticleCount.text = sortedArticles.count { it.status == "published" }.toString()
                            // Ensure we have per-user copies in users/{uid}/myArticles for faster/profile listing
                            try {
                                val userRef = firestore.collection("users").document(currentUser.uid).collection("myArticles")
                                for (a in sortedArticles) {
                                    val myArticleData = mapOf(
                                        "id" to a.id,
                                        "title" to a.title,
                                        "description" to a.description,
                                        "content" to a.content,
                                        "authorId" to a.authorId,
                                        "authorName" to a.authorName,
                                        "authorAvatar" to a.authorAvatar,
                                        "imageUrl" to a.imageUrl,
                                        "status" to a.status,
                                        "createdAt" to a.createdAt,
                                        "updatedAt" to a.updatedAt,
                                        "publishedAt" to a.publishedAt,
                                        "likesCount" to a.likesCount,
                                        "commentsCount" to a.commentsCount,
                                        "viewsCount" to a.viewsCount
                                    )
                                    userRef.document(a.id).set(myArticleData)
                                }
                            } catch (syncEx: Exception) {
                                android.util.Log.w("ProfileFragment", "Failed to sync myArticles: ${syncEx.message}")
                            }
                        }
                        .addOnFailureListener { e ->
                            android.util.Log.e("ProfileFragment", "Fallback articles query failed: ${e.message}")
                        }
                }
            }
            .addOnFailureListener { e ->
                android.util.Log.e("ProfileFragment", "loadMyArticles (myArticles) failed: ${e.message}")
                // Try fallback without order
                firestore.collection("users").document(currentUser.uid)
                    .collection("myArticles")
                    .get()
                    .addOnSuccessListener { docs2 ->
                        val fallbackList = mutableListOf<Article>()
                        for (d in docs2) {
                            try {
                                val a = d.toObject(Article::class.java)
                                a.id = d.id
                                fallbackList.add(a)
                            } catch (ex: Exception) {
                                android.util.Log.e("ProfileFragment", "Error parsing myArticles fallback ${d.id}: ${ex.message}")
                            }
                        }
                        val sortedFallback = fallbackList.sortedByDescending { it.createdAt?.toDate()?.time ?: 0L }
                        articleAdapter.updateArticles(sortedFallback)
                        android.util.Log.d("ProfileFragment", "After updateArticles (myArticles fallback) adapter count=${sortedFallback.size}")
                        android.util.Log.d("ProfileFragment", "recyclerViewMyArticles: width=${recyclerViewMyArticles.width} height=${recyclerViewMyArticles.height} isAttached=${recyclerViewMyArticles.isAttachedToWindow} adapter=${recyclerViewMyArticles.adapter}")
                        recyclerViewMyArticles.invalidate()
                        recyclerViewMyArticles.post { recyclerViewMyArticles.scrollToPosition(0) }
                        tvArticleCount.text = sortedFallback.count { it.status == "published" }.toString()
                    }
                    .addOnFailureListener { ex ->
                        android.util.Log.e("ProfileFragment", "Failed to load myArticles fallback: ${ex.message}")
                    }
            }
    }
    
    private fun loadFavoriteArticles() {
        val currentUser = auth.currentUser ?: return
        firestore.collection("users")
            .document(currentUser.uid)
            .collection("favorites")
            .get()
            .addOnSuccessListener { documents ->
                val articles = mutableListOf<Article>()
                android.util.Log.d("ProfileFragment", "Retrieved ${documents.size()} favorite docs")
                for (document in documents) {
                    try {
                        val data = document.data
                        // Manual parsing to avoid Firestore mapping issues
                        val article = Article(
                            id = document.id,
                            title = data["title"] as? String ?: "",
                            content = data["content"] as? String ?: "",
                            description = data["description"] as? String ?: "",
                            authorId = data["authorId"] as? String ?: "",
                            authorName = data["authorName"] as? String ?: "",
                            authorAvatar = data["authorAvatar"] as? String ?: "",
                            imageUrl = data["imageUrl"] as? String ?: "",
                            category = data["category"] as? String ?: "",
                            tags = (data["tags"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                            likesCount = (data["likesCount"] as? Number)?.toInt() ?: 0,
                            commentsCount = (data["commentsCount"] as? Number)?.toInt() ?: 0,
                            viewsCount = (data["viewsCount"] as? Number)?.toInt() ?: 0,
                            status = data["status"] as? String ?: "draft",
                            createdAt = data["createdAt"] as? com.google.firebase.Timestamp ?: com.google.firebase.Timestamp.now(),
                            updatedAt = data["updatedAt"] as? com.google.firebase.Timestamp ?: com.google.firebase.Timestamp.now(),
                            publishedAt = data["publishedAt"] as? com.google.firebase.Timestamp
                        )
                        articles.add(article)
                    } catch (e: Exception) {
                        android.util.Log.e("ProfileFragment", "Error parsing favorite ${document.id}: ${e.message}")
                    }
                }
                android.util.Log.d("ProfileFragment", "Loaded ${articles.size} favorite articles")
                favoritesAdapter.updateArticles(articles)
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to load your favorites: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
    
    override fun onResume() {
        super.onResume()
        // Refresh data when fragment becomes visible
        loadUserData()
        loadMyArticles()
        loadFavoriteArticles()
    }
}