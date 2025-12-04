package com.apkfood.wavesoffood.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.apkfood.wavesoffood.databinding.DialogOrderConfirmationBinding
import com.apkfood.wavesoffood.model.Order
import com.apkfood.wavesoffood.model.OrderStatus
import com.apkfood.wavesoffood.utils.FormatUtils
import java.text.SimpleDateFormat
import java.util.*

class OrderConfirmationDialog(
    context: Context,
    private val order: Order,
    private val onConfirm: () -> Unit
) : AlertDialog(context) {

    private lateinit var binding: DialogOrderConfirmationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding = DialogOrderConfirmationBinding.inflate(LayoutInflater.from(context))
        setView(binding.root)
        
        setupOrderDetails()
        setupClickListeners()
        setCancelable(false)
    }

    private fun setupOrderDetails() {
        with(binding) {
            // Order ID
            tvOrderId.text = "ID Pesanan: ${order.id.take(8).uppercase()}"
            
            // Customer Info
            tvCustomerName.text = "Nama: ${order.userName}"
            tvCustomerPhone.text = "No. HP: ${order.userPhone}"
            tvCustomerAddress.text = "Alamat: ${order.deliveryAddress}"
            
            // Order Date
            val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
            tvOrderDate.text = "Tanggal Pesan: ${dateFormat.format(Date(order.orderDate))}"
            
            // Estimated Delivery
            val deliveryDate = Date(order.estimatedDeliveryTime)
            tvDeliveryTime.text = "Estimasi Tiba: ${dateFormat.format(deliveryDate)}"
            
            // Items
            val itemsText = StringBuilder()
            for (item in order.items) {
                val itemTotal = item.food.price * item.quantity
                itemsText.append("• ${item.food.name} x${item.quantity} - ${FormatUtils.formatCurrency(itemTotal)}\n")
            }
            tvOrderItems.text = itemsText.toString()
            
            // Total
            tvTotalAmount.text = FormatUtils.formatCurrency(order.totalAmount)
            
            // Payment Method
            tvPaymentMethod.text = "Pembayaran: ${order.paymentMethod}"
            
            // Status
            tvOrderStatus.text = "Status: ${getStatusDisplayName(order.orderStatus)}"
            
            // Notes if any
            if (order.notes.isNotEmpty()) {
                tvNotes.text = "Catatan: ${order.notes}"
            } else {
                tvNotes.text = "Catatan: -"
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnConfirm.setOnClickListener {
            onConfirm()
            dismiss()
        }
        
        binding.btnViewOrders.setOnClickListener {
            onConfirm()
            dismiss()
            // Navigate to orders page will be handled by the callback
        }
    }
    
    private fun getStatusDisplayName(status: OrderStatus): String {
        return when (status) {
            OrderStatus.PENDING -> "Menunggu Konfirmasi"
            OrderStatus.CONFIRMED -> "Dikonfirmasi"
            OrderStatus.PREPARING -> "Sedang Dipreparasi"
            OrderStatus.ON_THE_WAY -> "Dalam Perjalanan"
            OrderStatus.DELIVERED -> "Sudah Diantar"
            OrderStatus.CANCELLED -> "Dibatalkan"
        }
    }
}
