package com.apk.blogapp.models

import com.google.firebase.Timestamp
import java.util.Date

data class User(
    var id: String = "",
    val email: String = "",
    val fullName: String = "",
    val username: String = "",
    val bio: String = "",
    val profileImageUrl: String = "",
    val articlesCount: Int = 0,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val totalLikes: Int = 0,
    val totalViews: Int = 0,
    val joinedDate: Timestamp = Timestamp(Date()),
    val lastActive: Timestamp = Timestamp(Date()),
    val isVerified: Boolean = false,
    val socialLinks: Map<String, String> = emptyMap()
) {
    // No-argument constructor for Firestore
    constructor() : this(
        id = "",
        email = "",
        fullName = "",
        username = "",
        bio = "",
        profileImageUrl = "",
        articlesCount = 0,
        followersCount = 0,
        followingCount = 0,
        totalLikes = 0,
        totalViews = 0,
        joinedDate = Timestamp(Date()),
        lastActive = Timestamp(Date()),
        isVerified = false,
        socialLinks = emptyMap()
    )
    
    // Helper function to get initials for avatar
    fun getInitials(): String {
        return if (fullName.isNotBlank()) {
            fullName.split(" ").mapNotNull { it.firstOrNull()?.uppercaseChar() }
                .take(2).joinToString("")
        } else {
            email.firstOrNull()?.uppercaseChar()?.toString() ?: "U"
        }
    }
    
    // Helper function to get formatted join date
    fun getFormattedJoinDate(): String {
        val date = joinedDate.toDate()
        val calendar = java.util.Calendar.getInstance()
        calendar.time = date
        val month = calendar.getDisplayName(java.util.Calendar.MONTH, java.util.Calendar.SHORT, java.util.Locale.getDefault())
        val year = calendar.get(java.util.Calendar.YEAR)
        return "Joined $month $year"
    }
}