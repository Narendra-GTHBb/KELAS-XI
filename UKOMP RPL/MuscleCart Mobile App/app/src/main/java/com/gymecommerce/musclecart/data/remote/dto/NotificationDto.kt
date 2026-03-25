package com.gymecommerce.musclecart.data.remote.dto

import com.google.gson.annotations.SerializedName

data class NotificationItemDto(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("body") val body: String,
    @SerializedName("type") val type: String,
    @SerializedName("reference_id") val referenceId: Int? = null,
    @SerializedName("reference_type") val referenceType: String? = null,
    @SerializedName("is_read") val isRead: Boolean = false,
    @SerializedName("created_at") val createdAt: String? = null
)

data class NotificationsDataDto(
    @SerializedName("notifications") val notifications: List<NotificationItemDto>,
    @SerializedName("unread_count") val unreadCount: Int
)

data class NotificationsResponse(
    @SerializedName("status") val status: String,
    @SerializedName("data") val data: NotificationsDataDto?
)

data class UnreadCountDto(
    @SerializedName("unread_count") val unreadCount: Int
)
