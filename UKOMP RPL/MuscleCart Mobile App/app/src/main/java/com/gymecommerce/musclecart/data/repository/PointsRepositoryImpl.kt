package com.gymecommerce.musclecart.data.repository

import com.gymecommerce.musclecart.data.remote.api.PointsApiService
import com.gymecommerce.musclecart.domain.model.Result
import com.gymecommerce.musclecart.domain.repository.PointsHistoryItem
import com.gymecommerce.musclecart.domain.repository.PointsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PointsRepositoryImpl @Inject constructor(
    private val pointsApiService: PointsApiService
) : PointsRepository {

    override suspend fun getPoints(): Result<Pair<Int, List<PointsHistoryItem>>> {
        return try {
            val response = pointsApiService.getPoints()
            val body = response.body()
            if (response.isSuccessful && body?.data != null) {
                val data = body.data
                val history = data.history.map { dto ->
                    PointsHistoryItem(
                        id = dto.id,
                        points = dto.points,
                        type = dto.type,
                        description = dto.description,
                        orderId = dto.orderId,
                        orderNumber = dto.orderNumber,
                        createdAt = dto.createdAt
                    )
                }
                Result.Success(Pair(data.balance, history))
            } else {
                Result.Error(body?.status ?: "Failed to load points (HTTP ${response.code()})")
            }
        } catch (e: Exception) {
            Result.Error("Failed to load points: ${e.message}")
        }
    }
}
