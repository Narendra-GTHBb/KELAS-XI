package com.apk.blogapp.models

import com.google.firebase.Timestamp
import java.util.Date

data class Article(
    var id: String = "",
    val title: String = "",
    val content: String = "",
    val description: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val authorAvatar: String = "",
    val imageUrl: String = "",
    val category: String = "",
    val tags: List<String> = emptyList(),
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val viewsCount: Int = 0,
    val status: String = "draft", // draft, published
    val createdAt: Timestamp = Timestamp(Date()),
    val updatedAt: Timestamp = Timestamp(Date()),
    val publishedAt: Timestamp? = null,
    // ...existing code...
) {
    // No-argument constructor for Firestore
    constructor() : this(
        id = "",
        title = "",
        content = "",
        description = "",
        authorId = "",
        authorName = "",
        authorAvatar = "",
        imageUrl = "",
        category = "",
        tags = emptyList(),
        likesCount = 0,
        commentsCount = 0,
        viewsCount = 0,
        status = "draft",
        createdAt = Timestamp(Date()),
        updatedAt = Timestamp(Date()),
        publishedAt = null
    )
    
    // Helper function to get formatted date
    fun getFormattedDate(): String {
        val date = publishedAt?.toDate() ?: createdAt.toDate()
        val now = Date()
        val diff = now.time - date.time
        
        return when {
            diff < 60000 -> "Just now"
            diff < 3600000 -> "${diff / 60000}m ago"
            diff < 86400000 -> "${diff / 3600000}h ago"
            diff < 604800000 -> "${diff / 86400000}d ago"
            else -> "${diff / 604800000}w ago"
        }
    }
    
    // Helper function to get reading time estimate
    fun getReadingTime(): String {
        val wordCount = content.split("\\s+".toRegex()).size
        val readingTime = (wordCount / 200).coerceAtLeast(1) // Assume 200 words per minute
        return "${readingTime} min read"
    }
}