package com.apkfood.wavesoffood.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.apkfood.wavesoffood.databinding.DialogCheckoutBinding
import com.apkfood.wavesoffood.manager.CartManager
import com.apkfood.wavesoffood.utils.FormatUtils
import com.apkfood.wavesoffood.utils.OrderManager
import com.apkfood.wavesoffood.utils.UserSessionManager

class CheckoutDialog(
    context: Context,
    private val onOrderCreated: (String) -> Unit
) : AlertDialog(context) {

    private lateinit var binding: DialogCheckoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding = DialogCheckoutBinding.inflate(LayoutInflater.from(context))
        setView(binding.root)
        
        setupViews()
        setupClickListeners()
        setCancelable(true)
    }

    private fun setupViews() {
        setupUserFields()
        updateOrderSummary()
    }

    private fun setupUserFields() {
        val currentUser = UserSessionManager.getCurrentUser(context)
        val isGuest = UserSessionManager.isGuest(context)
        
        if (!isGuest && currentUser != null) {
            binding.etName.setText(currentUser.name)
            binding.etEmail.setText(currentUser.email)
            binding.etPhone.setText(currentUser.phone)
            val fullAddress = currentUser.address
            binding.etAddress.setText(fullAddress)
        } else {
            binding.etName.setText("")
            binding.etEmail.setText("")
            binding.etPhone.setText("")
            binding.etAddress.setText("")
        }
    }

    private fun updateOrderSummary() {
        val cartItems = CartManager.getInstance().getCartItems()
        val itemsText = StringBuilder()
        var totalPrice = 0.0

        for (item in cartItems) {
            val itemTotal = item.food.price * item.quantity
            totalPrice += itemTotal
            itemsText.append("${item.food.name} x${item.quantity} - ${FormatUtils.formatCurrency(itemTotal)}\n")
        }

        binding.tvTotalItems.text = itemsText.toString()
        binding.tvTotalPrice.text = FormatUtils.formatCurrency(totalPrice)
    }

    private fun setupClickListeners() {
        binding.btnConfirm.setOnClickListener {
            if (validateInputs()) {
                processCheckout()
            }
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun validateInputs(): Boolean {
        with(binding) {
            if (etName.text.toString().trim().isEmpty()) {
                etName.error = "Name is required"
                return false
            }
            
            if (etEmail.text.toString().trim().isEmpty()) {
                etEmail.error = "Email is required"
                return false
            }
            
            if (etPhone.text.toString().trim().isEmpty()) {
                etPhone.error = "Phone is required"
                return false
            }
            
            if (etAddress.text.toString().trim().isEmpty()) {
                etAddress.error = "Address is required"
                return false
            }

            val selectedPaymentId = rgPaymentMethod.checkedRadioButtonId
            if (selectedPaymentId == -1) {
                Toast.makeText(context, "Please select a payment method", Toast.LENGTH_SHORT).show()
                return false
            }

            return true
        }
    }

    private fun processCheckout() {
        try {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val phone = binding.etPhone.text.toString().trim()
            val address = binding.etAddress.text.toString().trim()
            
            // Get selected payment method safely
            val paymentMethod = when (binding.rgPaymentMethod.checkedRadioButtonId) {
                binding.rbCOD.id -> "COD (Cash on Delivery)"
                binding.rbGoPay.id -> "GoPay"
                binding.rbOVO.id -> "OVO"
                binding.rbDANA.id -> "DANA"
                else -> "COD (Cash on Delivery)" // Default fallback
            }

            val currentUser = UserSessionManager.getCurrentUser(context)
            val isGuest = UserSessionManager.isGuest(context)
            
            if (!isGuest && currentUser != null) {
                val updatedUser = currentUser.copy(
                    name = name,
                    email = email,
                    phone = phone,
                    address = address
                )
                UserSessionManager.saveUserSession(context, updatedUser)
            }

            // Create order using OrderManager
            val order = OrderManager.createOrder(
                context = context,
                userName = name,
                userEmail = email,
                userPhone = phone,
                deliveryAddress = address,
                paymentMethod = paymentMethod,
                notes = binding.etNotes.text.toString().trim()
            )

            // Show order confirmation dialog instead of just a toast
            showOrderConfirmation(order)
            
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to place order: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    
    private fun showOrderConfirmation(order: com.apkfood.wavesoffood.model.Order) {
        dismiss() // Close checkout dialog first
        
        val confirmationDialog = OrderConfirmationDialog(context, order) {
            onOrderCreated(order.id)
        }
        confirmationDialog.show()
    }
}
