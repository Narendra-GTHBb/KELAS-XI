package com.apkfood.wavesoffoodadmin.model

import com.google.firebase.Timestamp

data class User(
    val id: String = "",
    val name: String = "",
    val fullName: String = "", // Legacy field
    val email: String = "",
    val phone: String = "",
    val phoneNumber: String = "", // Legacy field
    val profileImageUrl: String = "",
    val address: String = "",
    val isActive: Boolean = true,
    val totalOrders: Int = 0,
    val orderCount: Int = 0, // Legacy field
    val totalSpent: Double = 0.0,
    val joinDate: Long = System.currentTimeMillis(),
    val registrationDate: String = "", // Legacy field
    val createdAt: Any? = null, // Can be Long or Timestamp
    val lastOrderDate: Long = 0L,
    val banReason: String = "",
    val bannedAt: Long = 0L,
    val unbannedAt: Long = 0L,
    val fcmToken: String = ""
) {
    // Helper functions to get consistent data regardless of format
    fun getDisplayName(): String = if (name.isNotEmpty()) name else fullName
    
    fun getPhoneDisplay(): String = if (phone.isNotEmpty()) phone else phoneNumber
    
    fun getTotalOrdersDisplay(): Int = if (totalOrders > 0) totalOrders else orderCount
    
    fun getJoinDateLong(): Long {
        // Try to get from different possible fields
        return when {
            joinDate > 0 -> joinDate
            createdAt is Long -> createdAt
            createdAt is Timestamp -> createdAt.seconds * 1000
            registrationDate.isNotEmpty() -> {
                try {
                    // Try to parse date string "2024-01-15"
                    val parts = registrationDate.split("-")
                    if (parts.size == 3) {
                        val year = parts[0].toInt()
                        val month = parts[1].toInt() - 1 // Month is 0-based
                        val day = parts[2].toInt()
                        java.util.Calendar.getInstance().apply {
                            set(year, month, day)
                        }.timeInMillis
                    } else System.currentTimeMillis()
                } catch (e: Exception) {
                    System.currentTimeMillis()
                }
            }
            else -> System.currentTimeMillis()
        }
    }
}

data class AdminUser(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val passwordHash: String = "",
    val role: AdminRole = AdminRole.ADMIN,
    val permissions: List<String> = emptyList(),
    val isActive: Boolean = true,
    val profileImage: String = "",
    val phone: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val lastLogin: Long = 0L
)

enum class AdminRole {
    SUPER_ADMIN,
    ADMIN,
    MODERATOR
}

data class AppSettings(
    val id: String = "app_settings",
    val appName: String = "",
    val appVersion: String = "",
    val maintenanceMode: Boolean = false,
    val deliveryFee: Double = 0.0,
    val minOrderAmount: Double = 0.0,
    val maxDeliveryDistance: Int = 0,
    val averageDeliveryTime: Int = 0,
    val commissionRate: Double = 0.0,
    val taxRate: Double = 0.0,
    val supportEmail: String = "",
    val supportPhone: String = "",
    val privacyPolicyUrl: String = "",
    val termsOfServiceUrl: String = "",
    val updatedAt: Long = System.currentTimeMillis(),
    val updatedBy: String = ""
)
