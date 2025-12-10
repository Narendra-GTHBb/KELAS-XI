package com.apkfood.wavesoffood.manager

import android.util.Log
import com.apkfood.wavesoffood.model.Order
import com.apkfood.wavesoffood.model.CartItem
import com.apkfood.wavesoffood.model.OrderStatus
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

/**
 * Manager untuk menangani operasi Firebase terkait order
 */
class FirebaseOrderManager {
    
    companion object {
        private const val TAG = "FirebaseOrderManager"
        private const val COLLECTION_ORDERS = "orders"
    }
    
    private val firestore = FirebaseFirestore.getInstance()
    
    /**
     * Submit order to Firebase
     */
    suspend fun submitOrderToFirebase(order: Order): Result<String> {
        return try {
            Log.d(TAG, "🚀 Submitting order to Firebase: ${order.id}")
            
            // Use existing order number (already in WOF format)
            val orderNumber = order.orderNumber
            
            // Convert order to Firebase-compatible format that matches admin model
            val orderData = mapOf(
                "userId" to order.userId,
                "userName" to order.userName,
                "userEmail" to order.userEmail,
                "userPhone" to order.userPhone,
                "phoneNumber" to order.userPhone, // Admin model field
                "orderNumber" to orderNumber, // Admin model field
                "deliveryAddress" to order.deliveryAddress,
                "orderDate" to java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(java.util.Date(order.orderDate)),
                "orderDateTimestamp" to order.orderDate,
                "paymentMethod" to order.paymentMethod,
                "paymentStatus" to "unpaid", // Admin model field
                "notes" to order.notes,
                "items" to order.items.map { item ->
                    mapOf(
                        "foodId" to item.food.id,
                        "foodName" to item.food.name,
                        "price" to item.food.price,
                        "quantity" to item.quantity,
                        "subtotal" to item.getTotalPrice(),
                        "totalPrice" to item.getTotalPrice(),
                        "imageUrl" to item.food.imageUrl
                    )
                },
                "subtotal" to order.totalAmount, // Original user price
                "deliveryFee" to 0.0, // Let admin handle delivery fee
                "tax" to 0.0, // Let admin handle tax calculation
                "totalAmount" to order.totalAmount, // Keep original user price - no automatic markup
                "userTotalAmount" to order.totalAmount, // Keep original user price for consistency
                "status" to "pending", // Firebase uses lowercase string
                "orderStatus" to "PENDING", // For enum compatibility
                "estimatedDeliveryTime" to "",
                "actualDeliveryTime" to "",
                "deliveryTime" to 0L,
                "completedAt" to 0L,
                "createdAt" to System.currentTimeMillis(),
                "updatedAt" to System.currentTimeMillis()
            )
            
            Log.d(TAG, "📦 Order data prepared: $orderData")
            
            // Add to Firebase
            val documentRef = firestore.collection(COLLECTION_ORDERS)
                .add(orderData)
                .await()
            
            Log.d(TAG, "Order submitted to Firebase successfully: ${documentRef.id}")
            Result.success(documentRef.id)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error submitting order to Firebase", e)
            Result.failure(e)
        }
    }
    
    /**
     * Get user orders from Firebase
     */
    suspend fun getUserOrders(userId: String): Result<List<Order>> {
        return try {
            Log.d(TAG, "🔍 Getting orders for user: $userId")
            
            // Simplified query without orderBy to avoid index requirement
            val result = firestore.collection(COLLECTION_ORDERS)
                .whereEqualTo("userId", userId)
                .get()
                .await()
            
            val orders = result.documents.mapNotNull { doc ->
                try {
                    convertFirebaseToOrder(doc.data ?: return@mapNotNull null, doc.id)
                } catch (e: Exception) {
                    Log.e(TAG, "Error converting document to Order", e)
                    null
                }
            }.sortedByDescending { it.orderDate } // Sort in code instead of Firebase
            
            Log.d(TAG, "✅ Retrieved ${orders.size} orders for user: $userId")
            Result.success(orders)
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error getting user orders from Firebase", e)
            Result.failure(e)
        }
    }
    
    private var orderListener: com.google.firebase.firestore.ListenerRegistration? = null
    
    /**
     * Listen for order status updates from Firebase
     */
    fun listenForOrderUpdates(userId: String, onOrderUpdate: (Order) -> Unit) {
        // Remove previous listener to prevent duplicates
        orderListener?.remove()
        
        Log.d(TAG, "🔄 Starting Firebase listener for userId: $userId")
        orderListener = firestore.collection(COLLECTION_ORDERS)
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "❌ Error listening for order updates", error)
                    return@addSnapshotListener
                }
                
                Log.d(TAG, "🔄 Firebase snapshot received. Documents: ${snapshot?.documents?.size ?: 0}")
                
                // HARDCORE: ONLY PROCESS MODIFIED CHANGES - NEVER ADD NEW ORDERS!
                snapshot?.documentChanges?.forEach { change ->
                    try {
                        val orderData = change.document.data
                        val orderNumber = orderData["orderNumber"] as? String ?: "NO_ORDER_NUMBER"
                        
                        Log.d(TAG, "🔄 Change detected: ${change.document.id} - Type: ${change.type} - OrderNumber: $orderNumber")
                        
                        // ABSOLUTE RULE: Only MODIFIED changes allowed
                        when (change.type) {
                            com.google.firebase.firestore.DocumentChange.Type.MODIFIED -> {
                                val order = convertFirebaseToOrder(orderData, change.document.id)
                                Log.d(TAG, "✅ PROCESSING MODIFIED: $orderNumber - Status: ${order.orderStatus} - Price: ${order.totalAmount}")
                                onOrderUpdate(order)
                            }
                            com.google.firebase.firestore.DocumentChange.Type.ADDED -> {
                                Log.w(TAG, "🚨 BLOCKED ADDED: $orderNumber - PREVENTING DUPLICATE!")
                                // ABSOLUTELY DO NOT PROCESS ADDED CHANGES!
                            }
                            com.google.firebase.firestore.DocumentChange.Type.REMOVED -> {
                                Log.d(TAG, "🗑️ REMOVED: $orderNumber")
                            }
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "❌ Error processing order update for ${change.document.id}", e)
                    }
                }
            }
    }
    
    /**
     * Stop listening for order updates
     */
    fun stopListening() {
        orderListener?.remove()
        orderListener = null
        Log.d(TAG, "🛑 Firebase order listener stopped")
    }
    
    /**
     * Convert Firebase document data to Order object
     */
    private fun convertFirebaseToOrder(data: Map<String, Any>, documentId: String): Order {
        val items = (data["items"] as? List<Map<String, Any>>)?.map { itemData ->
            CartItem(
                food = com.apkfood.wavesoffood.data.model.Food(
                    id = itemData["foodId"] as? String ?: "",
                    name = itemData["foodName"] as? String ?: "",
                    price = (itemData["price"] as? Number)?.toDouble() ?: 0.0,
                    imageUrl = itemData["imageUrl"] as? String ?: ""
                ),
                quantity = (itemData["quantity"] as? Number)?.toInt() ?: 1
            )
        } ?: emptyList()
        
        val statusString = data["status"] as? String ?: "pending"
        Log.d(TAG, "🔄 Converting status: '$statusString' to OrderStatus enum")
        
        val status = when (statusString.lowercase()) {
            "pending" -> OrderStatus.PENDING
            "confirmed" -> OrderStatus.CONFIRMED
            "preparing" -> OrderStatus.PREPARING
            "ready" -> OrderStatus.PREPARING  // Map READY to PREPARING since user app doesn't have READY
            "out_for_delivery" -> OrderStatus.ON_THE_WAY
            "on_the_way" -> OrderStatus.ON_THE_WAY
            "delivered" -> OrderStatus.DELIVERED
            "cancelled" -> OrderStatus.CANCELLED
            else -> {
                Log.w(TAG, "⚠️ Unknown status: '$statusString', defaulting to PENDING")
                OrderStatus.PENDING
            }
        }
        
        // ALWAYS USE USER ORIGINAL TOTAL FROM subtotal OR userTotalAmount field
        val userTotal = (data["userTotalAmount"] as? Number)?.toDouble() 
            ?: (data["subtotal"] as? Number)?.toDouble() 
            ?: (data["totalAmount"] as? Number)?.toDouble() ?: 0.0
        
        Log.d(TAG, "💰 Firebase order total - subtotal: ${data["subtotal"]}, userTotalAmount: ${data["userTotalAmount"]}, totalAmount: ${data["totalAmount"]}")
        Log.d(TAG, "💰 Using user total: $userTotal for order display")
        
        Log.d(TAG, "✅ Status converted: '$statusString' -> $status")
        
        return Order(
            id = documentId,
            userId = data["userId"] as? String ?: "",
            userName = data["userName"] as? String ?: "",
            userEmail = data["userEmail"] as? String ?: "",
            userPhone = data["userPhone"] as? String ?: "",
            orderNumber = data["orderNumber"] as? String ?: "",
            deliveryAddress = data["deliveryAddress"] as? String ?: "",
            items = items,
            totalAmount = (data["userTotalAmount"] as? Number)?.toDouble() 
                ?: (data["subtotal"] as? Number)?.toDouble() 
                ?: 0.0, // Use original user price, fallback to subtotal
            orderStatus = status,
            orderDate = (data["orderDateTimestamp"] as? Number)?.toLong() ?: System.currentTimeMillis(),
            estimatedDeliveryTime = (data["estimatedDeliveryTime"] as? Number)?.toLong() ?: 0L,
            paymentMethod = data["paymentMethod"] as? String ?: "",
            notes = data["notes"] as? String ?: ""
        )
    }
    
    /**
     * Test method to verify Firebase connectivity and order retrieval
     */
    suspend fun testFirebaseOrderRetrieval(userId: String): Result<List<Map<String, Any>>> {
        return try {
            Log.d(TAG, "🧪 Testing Firebase order retrieval for userId: $userId")
            
            val result = firestore.collection(COLLECTION_ORDERS)
                .get()
                .await()
            
            Log.d(TAG, "🧪 Total documents in orders collection: ${result.documents.size}")
            
            val allOrdersData = result.documents.map { doc ->
                val data = doc.data ?: emptyMap()
                val docUserId = data["userId"] as? String ?: "MISSING"
                Log.d(TAG, "🧪 Document ${doc.id}: userId='$docUserId', status='${data["status"]}', matches=${docUserId == userId}")
                mapOf("id" to doc.id, "data" to data, "userIdMatches" to (docUserId == userId))
            }
            
            val userOrders = result.documents.filter { doc ->
                val data = doc.data ?: emptyMap()
                (data["userId"] as? String) == userId
            }
            
            Log.d(TAG, "🧪 Orders matching userId '$userId': ${userOrders.size}")
            userOrders.forEach { doc ->
                Log.d(TAG, "🧪 Matching order: ${doc.id} - status: ${doc.data?.get("status")}")
            }
            
            Result.success(allOrdersData)
        } catch (e: Exception) {
            Log.e(TAG, "🧪 Firebase test failed", e)
            Result.failure(e)
        }
    }
    
    /**
     * Update order status in Firebase
     */
    suspend fun updateOrderStatus(orderNumber: String, newStatus: String): Result<Boolean> {
        return try {
            Log.d(TAG, "🔄 Updating order status in Firebase: $orderNumber -> $newStatus")
            
            // Find order by orderNumber
            val querySnapshot = firestore.collection(COLLECTION_ORDERS)
                .whereEqualTo("orderNumber", orderNumber)
                .get()
                .await()
            
            if (querySnapshot.documents.isEmpty()) {
                Log.e(TAG, "❌ Order not found in Firebase: $orderNumber")
                return Result.failure(Exception("Order not found: $orderNumber"))
            }
            
            val documentId = querySnapshot.documents.first().id
            Log.d(TAG, "✅ Found order document: $documentId")
            
            // Update status in Firebase
            val updates = mapOf(
                "status" to newStatus.lowercase(),
                "orderStatus" to newStatus.uppercase(),
                "updatedAt" to System.currentTimeMillis()
            )
            
            firestore.collection(COLLECTION_ORDERS)
                .document(documentId)
                .update(updates)
                .await()
            
            Log.d(TAG, "✅ Successfully updated order status in Firebase: $orderNumber -> $newStatus")
            Result.success(true)
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to update order status in Firebase", e)
            Result.failure(e)
        }
    }
}