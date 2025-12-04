package com.apkfood.wavesoffoodadmin.activity

import android.os.Bundle
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.apkfood.wavesoffoodadmin.R
import com.apkfood.wavesoffoodadmin.adapter.OrderAdapter
import com.apkfood.wavesoffoodadmin.model.Order
import com.apkfood.wavesoffoodadmin.model.OrderStatus
import com.apkfood.wavesoffoodadmin.viewmodel.OrdersViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OrderManagementActivity : AppCompatActivity() {
    
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var fab: FloatingActionButton
    private lateinit var layoutEmptyState: LinearLayout
    private lateinit var etSearch: TextInputEditText
    private lateinit var btnFilter: MaterialButton
    private lateinit var btnRefresh: MaterialButton
    private lateinit var chipGroupStatus: ChipGroup
    // Note: Using optional chips since layout may not have all
    private var chipPending: Chip? = null
    private var chipCancelled: Chip? = null
    
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var orderViewModel: OrdersViewModel
    
    // For filtering and searching
    private var allOrders = listOf<Order>()
    private var currentFilterStatus: OrderStatus? = null
    private var currentSearchQuery: String = ""
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            Log.d("OrderManagement", "🚀 Starting OrderManagementActivity onCreate")
            setContentView(R.layout.activity_order_management)
            
            setupToolbar()
            setupViews()
            setupRecyclerView()
            setupViewModel()
            setupSwipeRefresh()
            setupFab()
            setupSearch()
            setupStatusFilter()
            
            Log.d("OrderManagement", "✅ OrderManagementActivity setup completed")
        } catch (e: Exception) {
            Log.e("OrderManagement", "💥 Error in onCreate", e)
            Toast.makeText(this, "Error initializing: ${e.message}", Toast.LENGTH_LONG).show()
            // Don't finish the activity, just show empty state
            showEmptyState()
        }
    }
    
    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Order Management"
    }
    
    private fun setupViews() {
        recyclerView = findViewById(R.id.recyclerView)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        fab = findViewById(R.id.fab)
        layoutEmptyState = findViewById(R.id.layoutEmptyState)
        etSearch = findViewById(R.id.etSearch)
        btnFilter = findViewById(R.id.btnFilter)
        btnRefresh = findViewById(R.id.btnRefresh)
        chipGroupStatus = findViewById(R.id.chipGroupStatus)
        
        // Safe chip finding - these IDs might not exist
        try {
            chipPending = findViewById(R.id.chipPending)
        } catch (e: Exception) {
            Log.w("OrderManagement", "chipPending not found in layout")
        }
        
        try {
            chipCancelled = findViewById(R.id.chipCancelled)
        } catch (e: Exception) {
            Log.w("OrderManagement", "chipCancelled not found in layout")
        }
    }
    
    private fun setupRecyclerView() {
        try {
            Log.d("OrderManagement", "Setting up RecyclerView and OrderAdapter")
            orderAdapter = OrderAdapter(
                onOrderClick = { order ->
                    showOrderDetailsDialog(order)
                },
                onStatusUpdateClick = { order, newStatus ->
                    updateOrderStatus(order, newStatus)
                },
                onConfirmOrderClick = { order ->
                    confirmOrder(order)
                },
                onRejectOrderClick = { order ->
                    rejectOrder(order)
                }
            )
            
            recyclerView.apply {
                layoutManager = LinearLayoutManager(this@OrderManagementActivity)
                adapter = orderAdapter
            }
            Log.d("OrderManagement", "✅ RecyclerView setup completed")
        } catch (e: Exception) {
            Log.e("OrderManagement", "❌ Error setting up RecyclerView", e)
            Toast.makeText(this, "Error setting up order list", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun setupViewModel() {
        Log.d("OrderManagement", "Setting up OrdersViewModel...")
        orderViewModel = ViewModelProvider(this)[OrdersViewModel::class.java]
        
        // Add Firebase connectivity test
        Log.d("OrderManagement", "Testing Firebase connectivity...")
        
        orderViewModel.orders.observe(this) { orders ->
            Log.d("OrderManagement", "Received ${orders.size} orders from ViewModel")
            orders.forEach { order ->
                Log.d("OrderManagement", "Order: ${order.orderNumber} - Status: ${order.status} - Total: ${order.getFormattedTotal()}")
            }
            
            // Store all orders for filtering
            allOrders = orders
            
            // Apply current filters and search
            applyFiltersAndSearch()
        }
        
        orderViewModel.isLoading.observe(this) { isLoading ->
            swipeRefreshLayout.isRefreshing = isLoading
            Log.d("OrderManagement", "Loading state: $isLoading")
        }
        
        orderViewModel.error.observe(this) { error ->
            if (error.isNotEmpty()) {
                Log.e("OrderManagement", "ViewModel error: $error")
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener {
            loadOrders()
        }
    }
    
    private fun setupFab() {
        fab.setOnClickListener {
            // Create a simple test order directly to the list for immediate testing
            createSimpleTestOrder()
        }
    }
    
    private fun createSimpleTestOrder() {
        try {
            Log.d("OrderManagement", "Creating simple test order")
            
            // Create test order data that matches our Order model
            val testOrder = Order(
                id = "test_${System.currentTimeMillis()}",
                userId = "test_user_123",
                userName = "Test User Admin",
                userPhone = "+628123456789",
                phoneNumber = "+628123456789",
                orderNumber = "ORD${System.currentTimeMillis()}",
                items = listOf(
                    com.apkfood.wavesoffoodadmin.model.OrderItem(
                        foodId = "item1",
                        foodName = "Test Nasi Goreng",
                        price = 25000.0,
                        quantity = 2,
                        subtotal = 50000.0
                    )
                ),
                subtotal = 50000.0,
                deliveryFee = 5000.0,
                tax = 5000.0,
                totalAmount = 60000.0,
                status = "pending",
                orderStatus = com.apkfood.wavesoffoodadmin.model.OrderStatus.PENDING,
                paymentMethod = "cash",
                deliveryAddress = "Test Address Jakarta",
                notes = "Test order from admin panel",
                orderDate = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(java.util.Date()),
                orderDateTimestamp = System.currentTimeMillis()
            )
            
            // Add directly to adapter for immediate visibility
            val currentList = orderAdapter.currentList.toMutableList()
            currentList.add(0, testOrder)
            orderAdapter.submitList(currentList)
            
            if (currentList.isNotEmpty()) {
                showOrdersList()
            }
            
            Toast.makeText(this, "✅ Test order created: ${testOrder.orderNumber}", Toast.LENGTH_SHORT).show()
            Log.d("OrderManagement", "Test order created successfully: ${testOrder.id}")
            
            // Also try to save to Firebase
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val orderData = mapOf(
                        "userId" to testOrder.userId,
                        "userName" to testOrder.userName,
                        "userPhone" to testOrder.userPhone,
                        "phoneNumber" to testOrder.phoneNumber,
                        "orderNumber" to testOrder.orderNumber,
                        "items" to testOrder.items.map { item ->
                            mapOf(
                                "foodId" to item.foodId,
                                "foodName" to item.foodName,
                                "price" to item.price,
                                "quantity" to item.quantity,
                                "subtotal" to item.subtotal
                            )
                        },
                        "subtotal" to testOrder.subtotal,
                        "deliveryFee" to testOrder.deliveryFee,
                        "tax" to testOrder.tax,
                        "totalAmount" to testOrder.totalAmount,
                        "status" to testOrder.status,
                        "paymentMethod" to testOrder.paymentMethod,
                        "deliveryAddress" to testOrder.deliveryAddress,
                        "notes" to testOrder.notes,
                        "orderDate" to testOrder.orderDate,
                        "orderDateTimestamp" to testOrder.orderDateTimestamp,
                        "createdAt" to System.currentTimeMillis(),
                        "updatedAt" to System.currentTimeMillis()
                    )
                    
                    val firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                    firestore.collection("orders")
                        .add(orderData)
                        .addOnSuccessListener { documentRef ->
                            Log.d("OrderManagement", "✅ Test order saved to Firebase: ${documentRef.id}")
                            runOnUiThread {
                                Toast.makeText(this@OrderManagementActivity, "Order saved to Firebase: ${documentRef.id}", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("OrderManagement", "❌ Error saving test order to Firebase", e)
                            runOnUiThread {
                                Toast.makeText(this@OrderManagementActivity, "Firebase error: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                } catch (e: Exception) {
                    Log.e("OrderManagement", "💥 Exception saving test order to Firebase", e)
                }
            }
            
        } catch (e: Exception) {
            Log.e("OrderManagement", "Error creating simple test order", e)
            Toast.makeText(this, "Error creating test order: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    
    private fun setupSearch() {
        // Setup search functionality
        etSearch.setOnEditorActionListener { _, _, _ ->
            performSearch()
            true
        }
        
        // Real-time search as user types (with debounce)
        var searchRunnable: Runnable? = null
        etSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                searchRunnable?.let { etSearch.removeCallbacks(it) }
                searchRunnable = Runnable {
                    currentSearchQuery = s.toString().trim()
                    applyFiltersAndSearch()
                }
                etSearch.postDelayed(searchRunnable, 300) // 300ms debounce
            }
        })
        
        btnFilter.setOnClickListener {
            showFilterDialog()
        }
    }
    
    private fun performSearch() {
        currentSearchQuery = etSearch.text.toString().trim()
        applyFiltersAndSearch()
    }
    
    private fun showFilterDialog() {
        val statusOptions = arrayOf(
            "All Orders",
            "Pending", 
            "Confirmed",
            "Preparing",
            "Ready",
            "Delivered",
            "Cancelled"
        )
        
        var selectedIndex = when (currentFilterStatus) {
            null -> 0
            OrderStatus.PENDING -> 1
            OrderStatus.CONFIRMED -> 2
            OrderStatus.PREPARING -> 3
            OrderStatus.READY -> 4
            OrderStatus.DELIVERED -> 5
            OrderStatus.CANCELLED -> 6
            OrderStatus.OUT_FOR_DELIVERY -> 4 // Map to Ready for now
        }
        
        MaterialAlertDialogBuilder(this)
            .setTitle("Filter Orders by Status")
            .setSingleChoiceItems(statusOptions, selectedIndex) { _, which ->
                selectedIndex = which
            }
            .setPositiveButton("Apply") { _, _ ->
                currentFilterStatus = when (selectedIndex) {
                    0 -> null // All orders
                    1 -> OrderStatus.PENDING
                    2 -> OrderStatus.CONFIRMED
                    3 -> OrderStatus.PREPARING
                    4 -> OrderStatus.READY
                    5 -> OrderStatus.DELIVERED
                    6 -> OrderStatus.CANCELLED
                    else -> null
                }
                applyFiltersAndSearch()
                
                val filterText = if (currentFilterStatus == null) "All Orders" else statusOptions[selectedIndex]
                Toast.makeText(this, "Filtering by: $filterText", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun applyFiltersAndSearch() {
        var filteredOrders = allOrders
        
        Log.d("OrderManagement", "🔍 FILTER DEBUG: Starting with ${allOrders.size} total orders")
        
        // Log all orders for debugging
        allOrders.forEachIndexed { index, order ->
            Log.d("OrderManagement", "📋 Order $index: ID=${order.id}, Number=${order.orderNumber}, Status='${order.status}' -> ${order.getOrderStatusEnum()}")
        }
        
        // Apply status filter
        currentFilterStatus?.let { status ->
            Log.d("OrderManagement", "🎯 FILTER DEBUG: Applying status filter: $status")
            
            filteredOrders = filteredOrders.filter { order ->
                val orderStatusEnum = order.getOrderStatusEnum()
                val matches = orderStatusEnum == status
                Log.d("OrderManagement", "🎯 Order ${order.orderNumber}: status='${order.status}' enum=$orderStatusEnum, filter=$status, matches=$matches")
                matches
            }
            
            Log.d("OrderManagement", "🎯 FILTER DEBUG: After status filter: ${filteredOrders.size} orders")
        }
        
        // Apply search filter
        if (currentSearchQuery.isNotEmpty()) {
            Log.d("OrderManagement", "🔍 SEARCH DEBUG: Applying search query: '$currentSearchQuery'")
            
            filteredOrders = filteredOrders.filter { order ->
                val searchInOrderNumber = order.orderNumber.contains(currentSearchQuery, ignoreCase = true)
                val searchInCustomerName = order.userName.contains(currentSearchQuery, ignoreCase = true)
                val searchInPhone = order.getPhoneDisplay().contains(currentSearchQuery, ignoreCase = true)
                val searchInOrderId = order.id.contains(currentSearchQuery, ignoreCase = true)
                val searchInAddress = order.deliveryAddress.contains(currentSearchQuery, ignoreCase = true)
                val searchInItems = order.items.any { item ->
                    item.foodName.contains(currentSearchQuery, ignoreCase = true)
                }
                
                val matches = searchInOrderNumber || searchInCustomerName || searchInPhone || 
                             searchInOrderId || searchInAddress || searchInItems
                
                Log.d("OrderManagement", "🔍 Search ${order.orderNumber}: orderNum=$searchInOrderNumber, name=$searchInCustomerName, phone=$searchInPhone, id=$searchInOrderId, addr=$searchInAddress, items=$searchInItems -> matches=$matches")
                
                matches
            }
            
            Log.d("OrderManagement", "🔍 SEARCH DEBUG: After search filter: ${filteredOrders.size} orders")
        }
        
        Log.d("OrderManagement", "✅ FINAL RESULT: ${filteredOrders.size} orders after filters")
        
        // Update adapter
        orderAdapter.submitList(filteredOrders)
        
        // Show appropriate state
        if (filteredOrders.isEmpty()) {
            if (allOrders.isEmpty()) {
                showEmptyState()
            } else {
                showNoResultsState()
            }
        } else {
            showOrdersList()
        }
    }
    
    private fun setupStatusFilter() {
        chipGroupStatus.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                val checkedChip = findViewById<com.google.android.material.chip.Chip>(checkedIds.first())
                val statusText = checkedChip.text.toString()
                
                Log.d("OrderManagement", "🎯 Chip selected: '$statusText'")
                
                // Map chip text to OrderStatus
                currentFilterStatus = when (statusText.lowercase()) {
                    "all orders", "all" -> {
                        Log.d("OrderManagement", "🎯 Filter set to: ALL (null)")
                        null
                    }
                    "pending" -> {
                        Log.d("OrderManagement", "🎯 Filter set to: PENDING")
                        OrderStatus.PENDING
                    }
                    "processing" -> {
                        Log.d("OrderManagement", "🎯 Filter set to: PREPARING (processing)")
                        OrderStatus.PREPARING
                    }
                    "complete", "completed" -> {
                        Log.d("OrderManagement", "🎯 Filter set to: DELIVERED (completed)")
                        OrderStatus.DELIVERED
                    }
                    "confirmed" -> {
                        Log.d("OrderManagement", "🎯 Filter set to: CONFIRMED")
                        OrderStatus.CONFIRMED
                    }
                    "preparing" -> {
                        Log.d("OrderManagement", "🎯 Filter set to: PREPARING")
                        OrderStatus.PREPARING
                    }
                    "ready" -> {
                        Log.d("OrderManagement", "🎯 Filter set to: READY")
                        OrderStatus.READY
                    }
                    "delivered" -> {
                        Log.d("OrderManagement", "🎯 Filter set to: DELIVERED")
                        OrderStatus.DELIVERED
                    }
                    "cancelled" -> {
                        Log.d("OrderManagement", "🎯 Filter set to: CANCELLED")
                        OrderStatus.CANCELLED
                    }
                    else -> {
                        Log.d("OrderManagement", "🎯 Unknown chip text '$statusText', defaulting to null")
                        null
                    }
                }
                
                applyFiltersAndSearch()
                Toast.makeText(this, "Filtering by: $statusText", Toast.LENGTH_SHORT).show()
            } else {
                // No chip selected - show all
                Log.d("OrderManagement", "🎯 No chip selected, showing all orders")
                currentFilterStatus = null
                applyFiltersAndSearch()
            }
        }
        
        btnRefresh.setOnClickListener {
            Log.d("OrderManagement", "🔄 Refresh clicked - clearing all filters")
            // Clear all filters and search
            currentFilterStatus = null
            currentSearchQuery = ""
            etSearch.setText("")
            chipGroupStatus.clearCheck()
            loadOrders()
        }
    }
    
    private fun loadOrders() {
        Log.d("OrderManagement", "🔄 Loading orders...")
        orderViewModel.loadOrders()
        
        // Also check Firebase directly for debugging
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                firestore.collection("orders")
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        Log.d("OrderManagement", "🔍 Direct Firebase check: ${querySnapshot.size()} documents found")
                        querySnapshot.documents.forEachIndexed { index, doc ->
                            Log.d("OrderManagement", "📄 Document $index: ${doc.id} - Data: ${doc.data}")
                        }
                        
                        runOnUiThread {
                            if (querySnapshot.isEmpty) {
                                Toast.makeText(this@OrderManagementActivity, "No orders found in Firebase", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this@OrderManagementActivity, "Found ${querySnapshot.size()} orders in Firebase", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("OrderManagement", "❌ Direct Firebase check failed", e)
                        runOnUiThread {
                            Toast.makeText(this@OrderManagementActivity, "Firebase error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } catch (e: Exception) {
                Log.e("OrderManagement", "💥 Exception in direct Firebase check", e)
            }
        }
    }
    
    private fun confirmOrder(order: Order) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Confirm Order")
            .setMessage("Are you sure you want to confirm order #${order.orderNumber.ifEmpty { order.id.take(8) }}?")
            .setPositiveButton("Confirm") { _, _ ->
                orderViewModel.updateOrderStatus(order.id, OrderStatus.CONFIRMED)
                Toast.makeText(this, "Order confirmed successfully", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun rejectOrder(order: Order) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Reject Order")
            .setMessage("Are you sure you want to reject order #${order.orderNumber.ifEmpty { order.id.take(8) }}?")
            .setPositiveButton("Reject") { _, _ ->
                orderViewModel.updateOrderStatus(order.id, OrderStatus.CANCELLED)
                Toast.makeText(this, "Order rejected", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun updateOrderStatus(order: Order, newStatus: OrderStatus) {
        val statusName = when (newStatus) {
            OrderStatus.PREPARING -> "Preparing"
            OrderStatus.READY -> "Ready for Delivery"
            OrderStatus.DELIVERED -> "Delivered"
            else -> newStatus.name
        }
        
        MaterialAlertDialogBuilder(this)
            .setTitle("Update Order Status")
            .setMessage("Mark order #${order.orderNumber.ifEmpty { order.id.take(8) }} as $statusName?")
            .setPositiveButton("Update") { _, _ ->
                orderViewModel.updateOrderStatus(order.id, newStatus)
                Toast.makeText(this, "Order status updated to $statusName", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun showOrderDetailsDialog(order: Order) {
        val message = buildString {
            append("Order #${order.orderNumber.ifEmpty { order.id.take(8) }}\n\n")
            append("Customer: ${order.userName}\n")
            append("Phone: ${order.getPhoneDisplay()}\n")
            append("Address: ${order.deliveryAddress}\n\n")
            append("Items:\n")
            order.items.forEach { item ->
                append("• ${item.quantity}x ${item.foodName} - ${order.getFormattedTotal()}\n")
            }
            append("\nTotal: ${order.getFormattedTotal()}\n")
            append("Payment: ${order.paymentMethod}\n")
            if (order.notes.isNotEmpty()) {
                append("Notes: ${order.notes}")
            }
        }
        
        MaterialAlertDialogBuilder(this)
            .setTitle("Order Details")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
    
    private fun showEmptyState() {
        recyclerView.visibility = android.view.View.GONE
        layoutEmptyState.visibility = android.view.View.VISIBLE
        
        // Update empty state message (try multiple possible IDs)
        try {
            val possibleIds = listOf(
                "tvEmptyMessage", "tv_empty_message", "textEmptyMessage", 
                "empty_message", "tvEmpty", "tv_empty"
            )
            
            var found = false
            for (idName in possibleIds) {
                try {
                    val resourceId = resources.getIdentifier(idName, "id", packageName)
                    if (resourceId != 0) {
                        val emptyText = findViewById<android.widget.TextView>(resourceId)
                        emptyText?.text = "No orders found"
                        found = true
                        break
                    }
                } catch (e: Exception) {
                    // Continue to next ID
                }
            }
            
            if (!found) {
                Log.w("OrderManagement", "Could not find empty message TextView")
            }
        } catch (e: Exception) {
            Log.w("OrderManagement", "Could not update empty state text", e)
        }
    }
    
    private fun showNoResultsState() {
        recyclerView.visibility = android.view.View.GONE
        layoutEmptyState.visibility = android.view.View.VISIBLE
        
        // Update message for filtered results
        try {
            val possibleIds = listOf(
                "tvEmptyMessage", "tv_empty_message", "textEmptyMessage", 
                "empty_message", "tvEmpty", "tv_empty"
            )
            
            var found = false
            for (idName in possibleIds) {
                try {
                    val resourceId = resources.getIdentifier(idName, "id", packageName)
                    if (resourceId != 0) {
                        val emptyText = findViewById<android.widget.TextView>(resourceId)
                        val filterInfo = buildString {
                            if (currentFilterStatus != null) {
                                append("No orders found for status: ${currentFilterStatus?.name}")
                            }
                            if (currentSearchQuery.isNotEmpty()) {
                                if (currentFilterStatus != null) append(" and ")
                                append("search: \"$currentSearchQuery\"")
                            }
                            if (currentFilterStatus == null && currentSearchQuery.isEmpty()) {
                                append("No results found")
                            }
                        }
                        emptyText?.text = filterInfo
                        found = true
                        break
                    }
                } catch (e: Exception) {
                    // Continue to next ID
                }
            }
            
            if (!found) {
                Log.w("OrderManagement", "Could not find empty message TextView for no results")
            }
        } catch (e: Exception) {
            Log.w("OrderManagement", "Could not update no results state text", e)
        }
    }
    
    private fun showOrdersList() {
        recyclerView.visibility = android.view.View.VISIBLE
        layoutEmptyState.visibility = android.view.View.GONE
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
