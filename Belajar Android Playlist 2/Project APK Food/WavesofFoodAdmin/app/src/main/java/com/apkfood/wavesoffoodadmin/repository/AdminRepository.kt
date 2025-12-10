package com.apkfood.wavesoffoodadmin.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ListenerRegistration
import com.apkfood.wavesoffoodadmin.model.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AdminRepository {
    private val firestore = FirebaseFirestore.getInstance()
    
    // Simple test method to check Firebase connectivity
    suspend fun testFirebaseConnection(): Boolean {
        return try {
            android.util.Log.d("AdminRepository", "Testing Firebase connection...")
            val testDoc = firestore.collection("test").document("connection")
            testDoc.set(mapOf("timestamp" to System.currentTimeMillis())).await()
            android.util.Log.d("AdminRepository", "✅ Firebase connection successful")
            true
        } catch (e: Exception) {
            android.util.Log.e("AdminRepository", "❌ Firebase connection failed", e)
            false
        }
    }
    
    // Direct Firebase debug method
    suspend fun debugFirebaseOrders(): String {
        return try {
            android.util.Log.d("AdminRepository", "🔍 DEBUG: Direct Firebase orders check")
            val result = firestore.collection("orders").get().await()
            val debugInfo = StringBuilder()
            
            debugInfo.append("Total documents in 'orders' collection: ${result.documents.size}\n")
            
            result.documents.forEachIndexed { index, doc ->
                debugInfo.append("Document $index:\n")
                debugInfo.append("  ID: ${doc.id}\n")
                debugInfo.append("  Exists: ${doc.exists()}\n")
                debugInfo.append("  Data keys: ${doc.data?.keys}\n")
                val data = doc.data
                if (data != null) {
                    debugInfo.append("  userEmail: ${data["userEmail"]}\n")
                    debugInfo.append("  orderNumber: ${data["orderNumber"]}\n")
                    debugInfo.append("  status: ${data["status"]}\n")
                    debugInfo.append("  orderStatus: ${data["orderStatus"]}\n")
                    debugInfo.append("  totalAmount: ${data["totalAmount"]}\n")
                    debugInfo.append("  createdAt: ${data["createdAt"]}\n")
                }
                debugInfo.append("\n")
            }
            
            val result_string = debugInfo.toString()
            android.util.Log.d("AdminRepository", "🔍 DEBUG RESULT:\n$result_string")
            result_string
        } catch (e: Exception) {
            val error = "❌ Firebase debug failed: ${e.message}"
            android.util.Log.e("AdminRepository", error, e)
            error
        }
    }
    
    // Real-time orders listener
    fun getOrdersFlow(): Flow<List<Order>> = callbackFlow {
        val listener = firestore.collection("orders")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    android.util.Log.e("AdminRepository", "Error in orders flow", error)
                    close(error)
                    return@addSnapshotListener
                }
                
                val orders = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        val order = parseFirebaseDocumentToOrder(doc.data ?: emptyMap(), doc.id)
                        android.util.Log.d("AdminRepository", "✅ Flow parsed order: ${order.orderNumber} - ${order.status}")
                        order
                    } catch (e: Exception) {
                        android.util.Log.e("AdminRepository", "❌ Flow error parsing order ${doc.id}", e)
                        null
                    }
                } ?: emptyList()
                
                // Sort by timestamp manually if orderDateTimestamp exists
                val sortedOrders = orders.sortedByDescending { it.orderDateTimestamp }
                
                android.util.Log.d("AdminRepository", "Flow received ${sortedOrders.size} orders")
                trySend(sortedOrders)
            }
        
        awaitClose { listener.remove() }
    }
    
    // Orders Management
    suspend fun getAllOrders(): List<Order> {
        return try {
            android.util.Log.d("AdminRepository", "🔍 Getting all orders from Firebase")
            val result = firestore.collection("orders")
                .get()
                .await()
            
            android.util.Log.d("AdminRepository", "🔍 Direct Firebase check: ${result.documents.size} documents found")
            
            result.documents.forEachIndexed { index, doc ->
                android.util.Log.d("AdminRepository", "🔍 Document $index: ${doc.id}")
                android.util.Log.d("AdminRepository", "🔍 Document data: ${doc.data}")
            }
            
            val orders = result.documents.mapNotNull { doc ->
                try {
                    val order = parseFirebaseDocumentToOrder(doc.data ?: emptyMap(), doc.id)
                    android.util.Log.d("AdminRepository", "✅ getAllOrders parsed order: ${order.orderNumber} - ${order.status}")
                    order
                } catch (e: Exception) {
                    android.util.Log.e("AdminRepository", "❌ getAllOrders error parsing order ${doc.id}", e)
                    android.util.Log.e("AdminRepository", "❌ Document data: ${doc.data}")
                    null
                }
            }
            
            // Sort manually by timestamp
            val sortedOrders = orders.sortedByDescending { it.orderDateTimestamp }
            android.util.Log.d("AdminRepository", "✅ Retrieved ${sortedOrders.size} orders successfully")
            sortedOrders
        } catch (e: Exception) {
            android.util.Log.e("AdminRepository", "Error getting orders", e)
            emptyList()
        }
    }
    
    suspend fun getOrdersByStatus(status: OrderStatus): List<Order> {
        return try {
            val statusString = status.name.lowercase()
            val result = firestore.collection("orders")
                .whereEqualTo("status", statusString)
                .get()
                .await()
            
            val orders = result.documents.mapNotNull { doc ->
                try {
                    parseFirebaseDocumentToOrder(doc.data ?: emptyMap(), doc.id)
                } catch (e: Exception) {
                    android.util.Log.e("AdminRepository", "Error parsing order by status ${doc.id}", e)
                    null
                }
            }
            
            orders.sortedByDescending { it.orderDateTimestamp }
        } catch (e: Exception) {
            android.util.Log.e("AdminRepository", "Error getting orders by status", e)
            emptyList()
        }
    }
    
    suspend fun updateOrderStatus(orderId: String, status: OrderStatus): Boolean {
        return try {
            val statusString = status.name.lowercase()
            firestore.collection("orders")
                .document(orderId)
                .update(
                    mapOf(
                        "status" to statusString,
                        "updatedAt" to System.currentTimeMillis()
                    )
                )
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    // Restaurants Management
    suspend fun getAllRestaurants(): List<Restaurant> {
        return try {
            val result = firestore.collection("restaurants")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            result.toObjects(Restaurant::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    suspend fun approveRestaurant(restaurantId: String): Boolean {
        return try {
            firestore.collection("restaurants")
                .document(restaurantId)
                .update("isActive", true, "updatedAt", System.currentTimeMillis())
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun suspendRestaurant(restaurantId: String): Boolean {
        return try {
            firestore.collection("restaurants")
                .document(restaurantId)
                .update("isActive", false, "updatedAt", System.currentTimeMillis())
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    // Users Management
    suspend fun getAllUsers(): List<User> {
        return try {
            val result = firestore.collection("users")
                .orderBy("joinDate", Query.Direction.DESCENDING)
                .get()
                .await()
            result.toObjects(User::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    suspend fun suspendUser(userId: String): Boolean {
        return try {
            firestore.collection("users")
                .document(userId)
                .update("isActive", false)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    // Analytics
    suspend fun getAnalytics(period: String = "today"): Analytics {
        return try {
            val ordersResult = firestore.collection("orders").get().await()
            val usersResult = firestore.collection("users").get().await()
            val restaurantsResult = firestore.collection("restaurants").get().await()
            
            val orders = ordersResult.toObjects(Order::class.java)
            val totalRevenue = orders.filter { it.getOrderStatusEnum() == OrderStatus.DELIVERED }
                .sumOf { it.totalAmount }
            
            Analytics(
                totalOrders = orders.size,
                totalRevenue = totalRevenue,
                totalUsers = usersResult.size(),
                totalRestaurants = restaurantsResult.size(),
                activeOrders = orders.count { it.getOrderStatusEnum() !in listOf(OrderStatus.DELIVERED, OrderStatus.CANCELLED) },
                completedOrders = orders.count { it.getOrderStatusEnum() == OrderStatus.DELIVERED },
                cancelledOrders = orders.count { it.getOrderStatusEnum() == OrderStatus.CANCELLED },
                averageOrderValue = if (orders.isNotEmpty()) totalRevenue / orders.size else 0.0,
                period = period
            )
        } catch (e: Exception) {
            Analytics()
        }
    }
    
    /**
     * Robust method to parse Firebase document to Order object
     * Handles type mismatches and missing fields gracefully
     */
    private fun parseFirebaseDocumentToOrder(data: Map<String, Any>, documentId: String): Order {
        try {
            android.util.Log.d("AdminRepository", "🔍 Parsing document $documentId with keys: ${data.keys}")
            
            // Parse items array
            val items = (data["items"] as? List<*>)?.mapNotNull { item ->
                if (item is Map<*, *>) {
                    val itemMap = item as Map<String, Any>
                    OrderItem(
                        foodId = itemMap["foodId"] as? String ?: "",
                        foodName = itemMap["foodName"] as? String ?: "",
                        price = (itemMap["price"] as? Number)?.toDouble() ?: 0.0,
                        quantity = (itemMap["quantity"] as? Number)?.toInt() ?: 1,
                        totalPrice = (itemMap["subtotal"] as? Number)?.toDouble() 
                                   ?: (itemMap["totalPrice"] as? Number)?.toDouble() ?: 0.0,
                        imageUrl = itemMap["imageUrl"] as? String ?: ""
                    )
                } else null
            } ?: emptyList()
            
            // Handle timestamps and dates
            val createdAtValue = data["createdAt"]
            val orderDateTimestamp = when (createdAtValue) {
                is Number -> createdAtValue.toLong()
                is com.google.firebase.Timestamp -> createdAtValue.toDate().time
                else -> System.currentTimeMillis()
            }
            
            val completedAtValue = data["completedAt"]
            val completedAt = when (completedAtValue) {
                is Number -> completedAtValue.toLong()
                is com.google.firebase.Timestamp -> completedAtValue.toDate().time
                else -> 0L
            }
            
            val deliveryTimeValue = data["deliveryTime"]
            val deliveryTime = when (deliveryTimeValue) {
                is Number -> deliveryTimeValue.toLong()
                else -> 0L
            }
            
            val order = Order(
                id = documentId,
                userId = data["userId"] as? String ?: "",
                userName = data["userName"] as? String ?: "",
                userPhone = data["userPhone"] as? String ?: "",
                phoneNumber = data["phoneNumber"] as? String ?: (data["userPhone"] as? String ?: ""),
                orderNumber = data["orderNumber"] as? String ?: "",
                restaurantId = data["restaurantId"] as? String ?: "",
                restaurantName = data["restaurantName"] as? String ?: "",
                items = items,
                subtotal = (data["subtotal"] as? Number)?.toDouble() ?: 0.0,
                deliveryFee = (data["deliveryFee"] as? Number)?.toDouble() ?: 5000.0,
                tax = (data["tax"] as? Number)?.toDouble() ?: 0.0,
                totalAmount = (data["totalAmount"] as? Number)?.toDouble() ?: 0.0,
                status = data["status"] as? String ?: "pending",
                paymentMethod = data["paymentMethod"] as? String ?: "",
                deliveryAddress = data["deliveryAddress"] as? String ?: "",
                notes = data["notes"] as? String ?: "",
                orderDate = data["orderDate"] as? String ?: "",
                orderDateTimestamp = orderDateTimestamp,
                estimatedDeliveryTime = data["estimatedDeliveryTime"] as? String ?: "",
                actualDeliveryTime = data["actualDeliveryTime"] as? String ?: "",
                deliveryTime = deliveryTime,
                completedAt = completedAt,
                createdAt = createdAtValue,
                updatedAt = data["updatedAt"]
            )
            
            android.util.Log.d("AdminRepository", "✅ Successfully parsed order: ${order.orderNumber} with status: '${order.status}' -> ${order.getOrderStatusEnum()}")
            android.util.Log.d("AdminRepository", "✅ Order items: ${order.items.size}, Total: ${order.totalAmount}")
            return order
            
        } catch (e: Exception) {
            android.util.Log.e("AdminRepository", "❌ Error in parseFirebaseDocumentToOrder", e)
            android.util.Log.e("AdminRepository", "❌ Document data: $data")
            throw e
        }
    }
}
