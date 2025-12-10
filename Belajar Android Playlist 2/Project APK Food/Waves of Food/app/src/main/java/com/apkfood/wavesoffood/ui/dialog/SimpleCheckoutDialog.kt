package com.apkfood.wavesoffood.ui.dialog

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import com.apkfood.wavesoffood.R
import com.apkfood.wavesoffood.manager.CartManager
import com.apkfood.wavesoffood.utils.FormatUtils
import com.apkfood.wavesoffood.utils.OrderManager
import com.apkfood.wavesoffood.utils.UserSessionManager

class SimpleCheckoutDialog {
    
    companion object {
        fun show(context: Context, onOrderCreated: (String) -> Unit) {
            try {
                val builder = AlertDialog.Builder(context)
                val inflater = LayoutInflater.from(context)
                val view = inflater.inflate(R.layout.dialog_simple_checkout, null)
                
                val etName = view.findViewById<EditText>(R.id.etName)
                val etPhone = view.findViewById<EditText>(R.id.etPhone)
                val etAddress = view.findViewById<EditText>(R.id.etAddress)
                val etNotes = view.findViewById<EditText>(R.id.etNotes)
                val rgPayment = view.findViewById<RadioGroup>(R.id.rgPayment)
                
                // Pre-fill user data if available
                val currentUser = UserSessionManager.getCurrentUser(context)
                val isGuest = UserSessionManager.isGuest(context)
                
                if (!isGuest && currentUser != null) {
                    etName.setText(currentUser.name)
                    etPhone.setText(currentUser.phone)
                    etAddress.setText(currentUser.address)
                }
                
                builder.setView(view)
                    .setTitle("Konfirmasi Pesanan")
                    .setPositiveButton("Pesan Sekarang", null) // Set null first to handle manually
                    .setNegativeButton("Batal") { dialog, _ ->
                        dialog.dismiss()
                    }
                
                val alertDialog = builder.create()
                alertDialog.show()
                
                // Handle positive button click manually to prevent double clicks
                val positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                var isProcessing = false
                
                positiveButton.setOnClickListener {
                    if (isProcessing) {
                        Toast.makeText(context, "Sedang memproses pesanan...", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    
                    try {
                        isProcessing = true
                        positiveButton.isEnabled = false
                        positiveButton.text = "Memproses..."
                        
                        val name = etName.text.toString().trim()
                        val phone = etPhone.text.toString().trim()
                        val address = etAddress.text.toString().trim()
                        
                        if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                            Toast.makeText(context, "Harap lengkapi semua data", Toast.LENGTH_SHORT).show()
                            isProcessing = false
                            positiveButton.isEnabled = true
                            positiveButton.text = "Pesan Sekarang"
                            return@setOnClickListener
                        }
                        
                        val paymentMethod = when (rgPayment.checkedRadioButtonId) {
                            R.id.rbCOD -> "COD (Cash on Delivery)"
                            R.id.rbGoPay -> "GoPay"
                            R.id.rbOVO -> "OVO"
                            R.id.rbDANA -> "DANA"
                            else -> "COD (Cash on Delivery)"
                        }
                        
                        val notes = etNotes.text.toString().trim().let { note ->
                            if (note.isEmpty()) "Pesanan melalui aplikasi" else note
                        }
                        
                        val order = OrderManager.createOrder(
                            context = context,
                            userName = name,
                            userEmail = currentUser?.email ?: "guest@example.com",
                            userPhone = phone,
                            deliveryAddress = address,
                            paymentMethod = paymentMethod,
                            notes = notes
                        )
                        
                        CartManager.getInstance().clearCart()
                        onOrderCreated(order.id)
                        alertDialog.dismiss()
                            
                        // Show success message with status info
                        Toast.makeText(context, 
                            "✅ Pesanan berhasil dibuat!\n" +
                            "📱 ID: ${order.id.take(8)}\n" +
                            "⏳ Status akan diupdate otomatis", 
                            Toast.LENGTH_LONG).show()
                            
                    } catch (e: Exception) {
                        isProcessing = false
                        positiveButton.isEnabled = true
                        positiveButton.text = "Pesan Sekarang"
                        Toast.makeText(context, "Gagal membuat pesanan: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
                    
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
