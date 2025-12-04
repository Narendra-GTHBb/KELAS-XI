package com.apkfood.wavesoffood.ui.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.apkfood.wavesoffood.databinding.FragmentOrdersBinding
import com.apkfood.wavesoffood.model.Order
import com.apkfood.wavesoffood.ui.orders.adapter.OrderAdapter
import com.apkfood.wavesoffood.utils.OrderManager

/**
 * Fragment untuk menampilkan daftar pesanan
 */
class OrdersFragment : Fragment(), OrderManager.OrderUpdateListener {
    
    private var _binding: FragmentOrdersBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var orderAdapter: OrderAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupRecyclerView()
        updateOrderDisplay()
        
        // Register as order listener
        OrderManager.addListener(this)
        
        // START AUTOMATIC FIREBASE POLLING FOR REAL-TIME UPDATES
        OrderManager.startFirebasePolling(requireContext())
        
        // No more sample data generation - users create real orders
    }
    
    private fun setupViews() {
        binding.swipeRefresh.setOnRefreshListener {
            refreshOrders()
        }
        
        binding.btnStartShopping.setOnClickListener {
            navigateToHome()
        }
        
        // Add debug refresh button (temporary for testing)
        binding.root.setOnLongClickListener {
            Toast.makeText(context, "Manual Firebase refresh triggered", Toast.LENGTH_SHORT).show()
            OrderManager.manualRefreshOrderStatus(requireContext())
            true
        }
        
        // Add super debug button for Firebase listener testing
        binding.btnStartShopping.setOnLongClickListener {
            Toast.makeText(context, "🔥 Firebase listener debug started", Toast.LENGTH_LONG).show()
            OrderManager.startFirebaseOrderListener(requireContext())
            true
        }
    }
    
    private fun setupRecyclerView() {
        orderAdapter = OrderAdapter(
            onOrderClick = { order ->
                showOrderDetails(order)
            },
            onCancelOrder = { order ->
                showCancelOrderDialog(order)
            },
            onTrackOrder = { order ->
                showTrackingInfo(order)
            }
        )
        
        binding.rvOrders.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = orderAdapter
        }
    }
    
    private fun updateOrderDisplay() {
        val orders = OrderManager.getAllOrders(requireContext())
        
        if (orders.isEmpty()) {
            binding.layoutEmptyOrders.visibility = View.VISIBLE
            binding.swipeRefresh.visibility = View.GONE
        } else {
            binding.layoutEmptyOrders.visibility = View.GONE
            binding.swipeRefresh.visibility = View.VISIBLE
            orderAdapter.submitList(orders)
        }
        
        binding.swipeRefresh.isRefreshing = false
    }
    
    private fun refreshOrders() {
        // Refresh from Firebase
        OrderManager.refreshOrdersFromFirebase(requireContext())
        
        // Update display with current cached orders
        updateOrderDisplay()
        
        // Also trigger manual Firebase sync
        OrderManager.manualRefreshOrderStatus(requireContext())
        
        binding.swipeRefresh.isRefreshing = false
    }
    
    private fun showOrderDetails(order: Order) {
        // TODO: Implement order details dialog/fragment
        Toast.makeText(
            requireContext(), 
            "Detail pesanan: ${order.id.take(8)}", 
            Toast.LENGTH_SHORT
        ).show()
    }
    
    private fun showCancelOrderDialog(order: Order) {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Batalkan Pesanan")
            .setMessage("Apakah Anda yakin ingin membatalkan pesanan ini?")
            .setPositiveButton("Ya") { _, _ ->
                if (OrderManager.cancelOrder(requireContext(), order.id)) {
                    Toast.makeText(requireContext(), "Pesanan berhasil dibatalkan", Toast.LENGTH_SHORT).show()
                    updateOrderDisplay()
                } else {
                    Toast.makeText(requireContext(), "Gagal membatalkan pesanan", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Tidak", null)
            .show()
    }
    
    private fun showTrackingInfo(order: Order) {
        val statusMessage = when (order.orderStatus) {
            com.apkfood.wavesoffood.model.OrderStatus.CONFIRMED -> "Pesanan Anda sudah dikonfirmasi dan sedang diproses"
            com.apkfood.wavesoffood.model.OrderStatus.PREPARING -> "Pesanan Anda sedang disiapkan"
            com.apkfood.wavesoffood.model.OrderStatus.ON_THE_WAY -> "Pesanan Anda sedang dalam perjalanan"
            else -> "Status pesanan: ${order.orderStatus}"
        }
        
        Toast.makeText(requireContext(), statusMessage, Toast.LENGTH_LONG).show()
    }
    
    private fun navigateToHome() {
        try {
            val activity = requireActivity()
            if (activity is com.apkfood.wavesoffood.MainActivity) {
                val homeNav = activity.findViewById<android.widget.LinearLayout>(com.apkfood.wavesoffood.R.id.nav_home)
                homeNav?.performClick()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    // OrderManager.OrderUpdateListener implementation
    override fun onOrderAdded(order: Order) {
        updateOrderDisplay()
    }
    
    override fun onOrderUpdated(order: Order) {
        updateOrderDisplay()
    }
    
    override fun onResume() {
        super.onResume()
        
        // Refresh orders whenever fragment becomes visible (e.g., when tab is clicked)
        refreshOrders()
        
        // Ensure we're still listening for updates
        OrderManager.addListener(this)
        
        // Start Firebase polling
        OrderManager.startFirebasePolling(requireContext())
    }
    
    override fun onPause() {
        super.onPause()
        
        // Keep listening but don't poll as aggressively
        // OrderManager.removeListener(this) // Keep listening
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        OrderManager.removeListener(this)
        // STOP FIREBASE POLLING TO SAVE RESOURCES
        OrderManager.stopFirebasePolling()
        _binding = null
    }
}
