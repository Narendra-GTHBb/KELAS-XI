package com.gymecommerce.musclecart.data.repository

import com.gymecommerce.musclecart.data.remote.api.NotificationApiService
import com.gymecommerce.musclecart.domain.repository.NotificationRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepositoryImpl @Inject constructor(
    private val api: NotificationApiService
) : NotificationRepository {

    override suspend fun getNotifications(): Result<Pair<Int, List<NotificationRepository.NotificationItem>>> {
        return try {
            val resp = api.getNotifications()
            if (resp.isSuccessful && resp.body()?.status == "success") {
                val data = resp.body()!!.data!!
                val items = data.notifications.map {
                    NotificationRepository.NotificationItem(
                        id = it.id,
                        title = it.title,
                        body = it.body,
                        type = it.type,
                        referenceId = it.referenceId,
                        referenceType = it.referenceType,
                        isRead = it.isRead,
                        createdAt = it.createdAt
                    )
                }
                Result.success(Pair(data.unreadCount, items))
            } else {
                Result.failure(Exception("Failed to load notifications"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUnreadCount(): Result<Int> {
        return try {
            val resp = api.getUnreadCount()
            if (resp.isSuccessful) {
                val count = resp.body()?.data?.get("unread_count") ?: 0
                Result.success(count)
            } else {
                Result.success(0)
            }
        } catch (e: Exception) {
            Result.success(0)
        }
    }

    override suspend fun readAll(): Result<Unit> {
        return try {
            api.readAll()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun read(id: Int): Result<Unit> {
        return try {
            api.read(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
