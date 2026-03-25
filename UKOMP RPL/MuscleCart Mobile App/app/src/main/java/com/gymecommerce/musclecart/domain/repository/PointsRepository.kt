package com.gymecommerce.musclecart.domain.repository

import com.gymecommerce.musclecart.domain.model.Result

data class PointsHistoryItem(
    val id: Int,
    val points: Int,
    val type: String,
    val description: String?,
    val orderId: Int?,
    val orderNumber: String?,
    val createdAt: String?
)

interface PointsRepository {
    /** Returns the user's current points balance and last 50 history entries. */
    suspend fun getPoints(): Result<Pair<Int, List<PointsHistoryItem>>>
}
