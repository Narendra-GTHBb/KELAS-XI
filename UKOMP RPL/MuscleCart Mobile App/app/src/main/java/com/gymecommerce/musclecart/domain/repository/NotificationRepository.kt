package com.gymecommerce.musclecart.domain.repository

interface NotificationRepository {
    data class NotificationItem(
        val id: Int,
        val title: String,
        val body: String,
        val type: String,
        val referenceId: Int?,
        val referenceType: String?,
        val isRead: Boolean,
        val createdAt: String?
    )

    suspend fun getNotifications(): Result<Pair<Int, List<NotificationItem>>>
    suspend fun getUnreadCount(): Result<Int>
    suspend fun readAll(): Result<Unit>
    suspend fun read(id: Int): Result<Unit>
}
