package com.apkfood.wavesoffood.ui.cart

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.apkfood.wavesoffood.databinding.FragmentCartBinding
import com.apkfood.wavesoffood.ui.cart.adapter.CartAdapter
import com.apkfood.wavesoffood.manager.CartManager
import com.apkfood.wavesoffood.utils.FormatUtils
import com.apkfood.wavesoffood.utils.OrderManager
import com.apkfood.wavesoffood.utils.UserSessionManager

class CartFragment : Fragment(), CartManager.CartUpdateListener {
    
    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var cartAdapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        CartManager.getInstance().addListener(this)
        
        setupRecyclerView()
        setupClickListeners()
        updateCartDisplay()
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            onQuantityChanged = { foodId, newQuantity ->
                CartManager.getInstance().updateQuantity(foodId, newQuantity)
            },
            onRemoveItem = { foodId ->
                CartManager.getInstance().removeFromCart(foodId)
                showToast("Item dihapus dari keranjang")
            }
        )
        binding.rvCartItems.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = cartAdapter
        }
    }

    private fun setupClickListeners() {
        binding.btnCheckout.setOnClickListener {
            handleCheckout()
        }
    }

    private fun updateCartDisplay() {
        val cartItems = CartManager.getInstance().getCartItems()
        Log.d("CartFragment", "updateCartDisplay called, items count: ${cartItems.size}")
        
        if (cartItems.isEmpty()) {
            Log.d("CartFragment", "Cart is empty - showing empty state")
            binding.layoutEmptyCart.visibility = View.VISIBLE
            binding.rvCartItems.visibility = View.GONE
            binding.cardBottomSummary.visibility = View.GONE
        } else {
            Log.d("CartFragment", "Cart has items - showing cart content")
            binding.layoutEmptyCart.visibility = View.GONE
            binding.rvCartItems.visibility = View.VISIBLE
            binding.cardBottomSummary.visibility = View.VISIBLE
            
            cartAdapter.submitList(cartItems)
            
            // Update total price
            val total = CartManager.getInstance().getTotalPrice()
            Log.d("CartFragment", "Total price: $total")
            binding.tvTotalPrice.text = FormatUtils.formatCurrency(total)
        }
    }

    private fun handleCheckout() {
        val cartItems = CartManager.getInstance().getCartItems()
        if (cartItems.isEmpty()) {
            showToast("Keranjang kosong! Silakan tambahkan item terlebih dahulu.")
            return
        }
        
        try {
            Log.d("CartFragment", "Starting checkout process...")
            
            val context = requireContext()
            if (context == null) {
                showToast("Context tidak tersedia")
                return
            }
            
            // Step 1: Confirmation dialog
            val alertDialog = android.app.AlertDialog.Builder(context)
                .setTitle("Konfirmasi Pesanan")
                .setMessage("Total: ${FormatUtils.formatCurrency(CartManager.getInstance().getTotalPrice())}\n\nLanjutkan ke form pemesanan?")
                .setPositiveButton("Ya, Lanjutkan") { dialog, _ ->
                    Log.d("CartFragment", "User confirmed, showing checkout form")
                    dialog.dismiss()
                    showCheckoutForm()
                }
                .setNegativeButton("Batal") { dialog, _ ->
                    Log.d("CartFragment", "User cancelled order")
                    dialog.dismiss()
                }
                .setCancelable(true)
                .create()
            
            Log.d("CartFragment", "Showing confirmation dialog...")
            alertDialog.show()
            Log.d("CartFragment", "Confirmation dialog shown successfully")
                
        } catch (e: Exception) {
            Log.e("CartFragment", "Error in checkout", e)
            showToast("Terjadi kesalahan: ${e.message}")
            e.printStackTrace()
        }
    }
    
    private fun showCheckoutForm() {
        try {
            // Step 2: Show checkout form dialog
            com.apkfood.wavesoffood.ui.dialog.SimpleCheckoutDialog.show(requireContext()) { orderId ->
                Log.d("CartFragment", "Order created with ID: $orderId")
                showToast("✅ Pesanan berhasil dibuat!\nID: ${orderId.take(8)}")
                updateCartDisplay() // Refresh cart display
                navigateToOrders()
            }
        } catch (e: Exception) {
            Log.e("CartFragment", "Error showing checkout form", e)
            showToast("Error menampilkan form: ${e.message}")
            // Don't create fallback order - let user try again
        }
    }
    
    private fun navigateToOrders() {
        try {
            val activity = requireActivity()
            if (activity is com.apkfood.wavesoffood.MainActivity) {
                val ordersNav = activity.findViewById<android.widget.LinearLayout>(com.apkfood.wavesoffood.R.id.nav_orders)
                ordersNav?.performClick()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
    
    override fun onCartUpdated() {
        Log.d("CartFragment", "onCartUpdated called")
        updateCartDisplay()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        CartManager.getInstance().removeListener(this)
        _binding = null
    }
}
