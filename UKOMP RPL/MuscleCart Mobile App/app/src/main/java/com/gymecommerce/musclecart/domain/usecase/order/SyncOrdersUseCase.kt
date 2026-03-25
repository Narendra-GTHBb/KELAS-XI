package com.gymecommerce.musclecart.domain.usecase.order

import com.gymecommerce.musclecart.domain.model.Result
import com.gymecommerce.musclecart.domain.repository.OrderRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SyncOrdersUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return try {
            // First, sync orders from server
            when (val syncResult = orderRepository.syncOrders()) {
                is Result.Success -> {
                    // Then, try to sync any unsynced local orders to server
                    syncUnsyncedOrders()
                }
                is Result.Error -> syncResult
                else -> Result.Error("Order sync in progress, try again later")
            }
        } catch (e: Exception) {
            Result.Error("Failed to sync orders: ${e.message}")
        }
    }

    suspend fun refreshOrders(): Result<Unit> {
        return try {
            when (val refreshResult = orderRepository.refreshOrders()) {
                is Result.Success -> {
                    // After refresh, sync any unsynced orders
                    syncUnsyncedOrders()
                }
                is Result.Error -> refreshResult
                else -> Result.Error("Order refresh in progress, try again later")
            }
        } catch (e: Exception) {
            Result.Error("Failed to refresh orders: ${e.message}")
        }
    }

    private suspend fun syncUnsyncedOrders(): Result<Unit> {
        return try {
            val unsyncedOrders = orderRepository.getUnsyncedOrders().first()

            val syncErrors = mutableListOf<String>()
            for (order in unsyncedOrders) {
                when (val updateResult = orderRepository.updateOrder(order)) {
                    is Result.Success -> {
                        // Mark as synced
                        orderRepository.markOrderAsSynced(order.id)
                    }
                    is Result.Error -> {
                        syncErrors.add("Failed to sync order ${order.id}: ${updateResult.message}")
                    }
                    else -> {
                        syncErrors.add("Order ${order.id} sync in progress, try again later")
                    }
                }
            }

            if (syncErrors.isNotEmpty()) {
                Result.Error("Some orders failed to sync:\n${syncErrors.joinToString("\n")}")
            } else {
                Result.Success(Unit)
            }
        } catch (e: Exception) {
            Result.Error("Failed to sync unsynced orders: ${e.message}")
        }
    }

    suspend fun syncOrdersInBackground(): Result<Unit> {
        return try {
            // This method can be called periodically to sync orders in background
            // It's more lenient with errors to avoid disrupting user experience
            orderRepository.syncOrders()
            syncUnsyncedOrders()
            Result.Success(Unit)
        } catch (e: Exception) {
            // Log error but don't propagate it for background sync
            Result.Success(Unit)
        }
    }
}