package com.apk.blogapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apk.blogapp.adapters.ArticleAdapter
import com.apk.blogapp.models.Article
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import java.util.*

class HomeFragment : Fragment() {
    
    private lateinit var tvWelcome: TextView
    private lateinit var tvNoArticles: TextView
    private lateinit var layoutEmptyState: LinearLayout
    private lateinit var recyclerViewArticles: RecyclerView
    private lateinit var articleAdapter: ArticleAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        
        initViews(view)
        setupWelcomeMessage()
        setupRecyclerView()
        loadArticles()
    }
    
    private fun setupWelcomeMessage() {
        val currentUser = auth.currentUser
        
        Log.d("HomeFragment", "=== WELCOME MESSAGE CHECK ===")
        Log.d("HomeFragment", "Current user: ${currentUser?.email}")
        Log.d("HomeFragment", "User ID: ${currentUser?.uid}")
        
        if (currentUser != null && !currentUser.isAnonymous) {
            // Get user name from email or display name
            val userName = currentUser.displayName ?: currentUser.email?.substringBefore("@") ?: "User"
            tvWelcome.text = "Welcome back, $userName"
            Log.d("HomeFragment", "✅ Showing welcome for logged user: $userName")
        } else {
            // User shouldn't be here if not logged in - redirect to login
            Log.e("HomeFragment", "❌ ERROR: User not logged in but in HomeFragment!")
            tvWelcome.text = "Please login to continue"
            
            // Redirect to login
            requireActivity().finish()
            startActivity(Intent(requireContext(), WelcomeActivity::class.java))
        }
    }
    
    private fun initViews(view: View) {
        tvWelcome = view.findViewById(R.id.tvWelcome)
        tvNoArticles = view.findViewById(R.id.tvNoArticles)
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState)
        recyclerViewArticles = view.findViewById(R.id.recyclerViewArticles)
    }
    

    
    private fun setupRecyclerView() {
        articleAdapter = ArticleAdapter(
            onItemClick = { article ->
                // TODO: Navigate to article detail
                Toast.makeText(context, "Opening: ${article.title}", Toast.LENGTH_SHORT).show()
            },
            onAuthorClick = { article ->
                // TODO: Navigate to author profile
                Toast.makeText(context, "Author: ${article.authorName}", Toast.LENGTH_SHORT).show()
            },
            onLikeClick = { article ->
                // TODO: Handle like functionality
                Toast.makeText(context, "Liked: ${article.title}", Toast.LENGTH_SHORT).show()
            },
            onShareClick = { article ->
                // TODO: Handle share functionality
                Toast.makeText(context, "Sharing: ${article.title}", Toast.LENGTH_SHORT).show()
            }
            ,
            onBookmarkClick = { article, btn ->
                val userId = auth.currentUser?.uid
                if (userId == null) {
                    Toast.makeText(context, "Please login to favorite articles", Toast.LENGTH_SHORT).show()
                    return@ArticleAdapter
                }
                val favRef = firestore.collection("users").document(userId).collection("favorites").document(article.id)
                // Check current status
                favRef.get().addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        favRef.delete().addOnSuccessListener {
                            btn.setImageResource(R.drawable.ic_favorite_outline)
                            Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener { e ->
                            Toast.makeText(context, "Failed to remove favorite: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        val data = mapOf(
                            "id" to article.id,
                            "title" to article.title,
                            "description" to article.description,
                            "authorId" to article.authorId,
                            "authorName" to article.authorName,
                            "imageUrl" to article.imageUrl,
                            "createdAt" to article.createdAt
                        )
                        favRef.set(data).addOnSuccessListener {
                            btn.setImageResource(R.drawable.ic_favorite_filled)
                            Toast.makeText(context, "Added to favorites", Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener { e ->
                            Toast.makeText(context, "Failed to add favorite: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }.addOnFailureListener { e ->
                    Toast.makeText(context, "Failed to check favorite: ${e.message}", Toast.LENGTH_LONG).show()
                }
            },
            onBindBookmarkState = { article, btn ->
                val userId = auth.currentUser?.uid
                if (userId == null) {
                    btn.setImageResource(R.drawable.ic_favorite_outline)
                    return@ArticleAdapter
                }
                val favRef = firestore.collection("users").document(userId).collection("favorites").document(article.id)
                favRef.get().addOnSuccessListener { doc ->
                    if (doc.exists()) btn.setImageResource(R.drawable.ic_favorite_filled)
                    else btn.setImageResource(R.drawable.ic_favorite_outline)
                }.addOnFailureListener {
                    btn.setImageResource(R.drawable.ic_favorite_outline)
                }
            }
        )
        
        recyclerViewArticles.layoutManager = LinearLayoutManager(context)
        recyclerViewArticles.adapter = articleAdapter
    }
    
    private fun loadArticles() {
        android.util.Log.d("HomeFragment", "Loading articles from Firebase...")
        
        // Load from Firebase with better error handling
        firestore.collection("articles")
            .whereEqualTo("status", "published")
            .get()
            .addOnSuccessListener { documents ->
                val articles = mutableListOf<Article>()
                android.util.Log.d("HomeFragment", "Retrieved ${documents.size()} documents from Firebase")
                
                for (document in documents) {
                    try {
                        val article = document.toObject(Article::class.java)
                        article.id = document.id
                        articles.add(article)
                        android.util.Log.d("HomeFragment", "Added article: ${article.title}")
                    } catch (e: Exception) {
                        android.util.Log.w("HomeFragment", "Error parsing article ${document.id}: ${e.message}")
                    }
                }
                
                // Sort by createdAt (newest first)
                articles.sortByDescending { 
                    try {
                        it.createdAt.toDate()
                    } catch (e: Exception) {
                        Date(0) // fallback for invalid dates
                    }
                }
                
                android.util.Log.d("HomeFragment", "Final articles count: ${articles.size}")
                
                if (articles.isEmpty()) {
                    android.util.Log.d("HomeFragment", "No published articles found, using dummy data as fallback")
                    val dummyArticles = createDummyArticles()
                    showArticles(dummyArticles)
                } else {
                    android.util.Log.d("HomeFragment", "Showing ${articles.size} articles from Firebase")
                    showArticles(articles)
                }
            }
            .addOnFailureListener { exception ->
                android.util.Log.e("HomeFragment", "Error loading from Firebase: ${exception.message}")
                // Firebase failed, use dummy data as fallback
                val dummyArticles = createDummyArticles()
                showArticles(dummyArticles)
                Toast.makeText(context, "Loading offline data. Check your connection.", Toast.LENGTH_SHORT).show()
            }
    }
    
    private fun createDummyArticles(): List<Article> {
        return listOf(
            Article(
                id = "dummy_1",
                title = "Getting Started with Android Development",
                content = "Android development is an exciting journey that opens up countless possibilities for creating mobile applications. In this comprehensive guide, we'll explore the fundamentals of Android development, from setting up your development environment to publishing your first app on the Google Play Store.\n\nFirst, let's talk about the tools you'll need. Android Studio is the official integrated development environment (IDE) for Android development. It provides everything you need to build Android apps, including a code editor, debugger, and performance tools.\n\nKotlin has become the preferred language for Android development, offering concise syntax and powerful features that make development faster and more enjoyable. Java is still supported, but Kotlin is now Google's preferred language for new Android projects.\n\nThe Android architecture components provide a robust foundation for building high-quality apps. These include ViewModel, LiveData, Room database, and Navigation components that help you build apps that are maintainable, testable, and robust.",
                description = "A comprehensive guide to getting started with Android development, covering tools, languages, and best practices.",
                authorId = "dummy_author_1",
                authorName = "John Developer",
                category = "Technology",
                tags = listOf("android", "mobile", "development", "kotlin"),
                imageUrl = "https://images.unsplash.com/photo-1607252650355-f7fd0460ccdb?w=800",
                status = "published",
                createdAt = Timestamp(Date(System.currentTimeMillis() - 86400000 * 7)), // 7 days ago
                updatedAt = Timestamp(Date(System.currentTimeMillis() - 86400000 * 7)),
                likesCount = 45,
                viewsCount = 234
            ),
            Article(
                id = "dummy_2",
                title = "Mastering Kotlin for Android",
                content = "Kotlin has revolutionized Android development with its concise syntax and powerful features. This modern programming language offers null safety, extension functions, and many other features that make development more efficient and enjoyable.\n\nOne of the most significant advantages of Kotlin is its interoperability with Java. You can gradually migrate your existing Java codebase to Kotlin without any issues. Kotlin compiles to the same bytecode as Java, ensuring seamless integration.\n\nLet's explore some key Kotlin features that every Android developer should master: data classes, sealed classes, coroutines for asynchronous programming, and extension functions that allow you to add functionality to existing classes.",
                description = "Deep dive into Kotlin features that make Android development more efficient and enjoyable.",
                authorId = "dummy_author_1",
                authorName = "John Developer",
                category = "Programming",
                tags = listOf("kotlin", "android", "programming", "mobile"),
                imageUrl = "https://images.unsplash.com/photo-1517180102446-f3ece451e9d8?w=800",
                status = "published",
                createdAt = Timestamp(Date(System.currentTimeMillis() - 86400000 * 5)), // 5 days ago
                updatedAt = Timestamp(Date(System.currentTimeMillis() - 86400000 * 5)),
                likesCount = 67,
                viewsCount = 189
            ),
            Article(
                id = "dummy_3",
                title = "UI/UX Best Practices for Mobile Apps",
                content = "Creating intuitive and beautiful user interfaces is crucial for app success. In this article, we'll explore essential UI/UX principles that every mobile developer should know.\n\nMaterial Design provides a comprehensive guide for creating beautiful and functional user interfaces. It includes guidelines for typography, colors, spacing, and component behavior. Following these guidelines ensures your app feels familiar to users.\n\nUser experience goes beyond just visual design. It includes app performance, loading times, intuitive navigation, and accessibility. A well-designed app should be usable by everyone, including users with disabilities.",
                description = "Essential UI/UX principles every mobile developer should know.",
                authorId = "dummy_author_2",
                authorName = "Sarah Tech",
                category = "Design",
                tags = listOf("ui", "ux", "design", "mobile", "user-experience"),
                imageUrl = "https://images.unsplash.com/photo-1559028006-448665bd7c7f?w=800",
                status = "published",
                createdAt = Timestamp(Date(System.currentTimeMillis() - 86400000 * 3)), // 3 days ago
                updatedAt = Timestamp(Date(System.currentTimeMillis() - 86400000 * 3)),
                likesCount = 89,
                viewsCount = 356
            ),
            Article(
                id = "dummy_4",
                title = "Firebase Integration for Android Apps",
                content = "Firebase provides a comprehensive suite of tools for mobile app development. From real-time databases to authentication, Firebase can significantly speed up your development process.\n\nFirestore is Firebase's NoSQL document database that scales automatically and provides real-time synchronization. It's perfect for mobile apps that need to sync data across multiple devices.\n\nFirebase Authentication makes it easy to add user authentication to your app. It supports email/password, phone number, and social media authentication providers like Google, Facebook, and Twitter.",
                description = "Learn how to integrate Firebase services into your Android applications.",
                authorId = "dummy_author_3",
                authorName = "Alex Designer",
                category = "Backend",
                tags = listOf("firebase", "backend", "database", "authentication"),
                imageUrl = "https://images.unsplash.com/photo-1558494949-ef010cbdcc31?w=800",
                status = "published",
                createdAt = Timestamp(Date(System.currentTimeMillis() - 86400000 * 2)), // 2 days ago
                updatedAt = Timestamp(Date(System.currentTimeMillis() - 86400000 * 2)),
                likesCount = 78,
                viewsCount = 267
            ),
            Article(
                id = "dummy_5",
                title = "Building MVVM Architecture in Android",
                content = "Model-View-ViewModel (MVVM) is a design pattern that helps separate the development of the graphical user interface from the business logic. This separation allows for more maintainable and testable code.\n\nIn Android development, MVVM works excellently with Android Architecture Components. ViewModel holds the UI-related data and survives configuration changes. LiveData provides observable data holders that respect the lifecycle of app components.\n\nImplementing MVVM in your Android app involves creating separate layers for your data models, view models, and UI components. This separation makes your code more organized and easier to test.",
                description = "Comprehensive guide to implementing MVVM architecture in Android applications.",
                authorId = "dummy_author_1",
                authorName = "John Developer",
                category = "Architecture",
                tags = listOf("mvvm", "architecture", "android", "viewmodel", "livedata"),
                imageUrl = "https://images.unsplash.com/photo-1551288049-bebda4e38f71?w=800",
                status = "published",
                createdAt = Timestamp(Date(System.currentTimeMillis() - 86400000 * 1)), // 1 day ago
                updatedAt = Timestamp(Date(System.currentTimeMillis() - 86400000 * 1)),
                likesCount = 56,
                viewsCount = 198
            ),
            Article(
                id = "dummy_6",
                title = "Android Testing Best Practices",
                content = "Testing is a crucial part of Android development that ensures your app works correctly and provides a good user experience. Android provides several frameworks and tools for testing your applications.\n\nUnit tests focus on testing individual components in isolation. They're fast to run and help catch bugs early in development. Use JUnit and Mockito for effective unit testing in Android.\n\nInstrumentation tests run on actual devices or emulators and test the interaction between different components. Espresso is the recommended framework for UI testing in Android applications.",
                description = "Learn essential testing strategies and tools for Android development.",
                authorId = "dummy_author_2",
                authorName = "Sarah Tech",
                category = "Testing",
                tags = listOf("testing", "junit", "espresso", "android", "quality"),
                imageUrl = "https://images.unsplash.com/photo-1516321318423-f06f85e504b3?w=800",
                status = "published",
                createdAt = Timestamp(Date(System.currentTimeMillis() - 86400000 * 0)), // Today
                updatedAt = Timestamp(Date(System.currentTimeMillis() - 86400000 * 0)),
                likesCount = 42,
                viewsCount = 156
            )
        )
    }
    
    private fun loadArticlesSimple() {
        // This method is kept for backward compatibility but not used
        loadArticles()
    }
    
    private fun showArticles(articles: List<Article>) {
        layoutEmptyState.visibility = View.GONE
        recyclerViewArticles.visibility = View.VISIBLE
        articleAdapter.updateArticles(articles)
    }
    
    private fun showEmptyState() {
        layoutEmptyState.visibility = View.VISIBLE
        recyclerViewArticles.visibility = View.GONE
    }
    
    override fun onResume() {
        super.onResume()
        // Refresh articles when fragment becomes visible
        loadArticles()
    }
}