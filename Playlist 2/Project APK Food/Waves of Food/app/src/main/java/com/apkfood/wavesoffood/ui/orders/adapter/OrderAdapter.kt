package com.apkfood.wavesoffood.ui.orders.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.apkfood.wavesoffood.R
import com.apkfood.wavesoffood.databinding.ItemOrderBinding
import com.apkfood.wavesoffood.model.Order
import com.apkfood.wavesoffood.model.OrderStatus
import com.apkfood.wavesoffood.utils.FormatUtils
import java.text.SimpleDateFormat
import java.util.*

class OrderAdapter(
    private val onOrderClick: (Order) -> Unit = {},
    private val onCancelOrder: (Order) -> Unit = {},
    private val onTrackOrder: (Order) -> Unit = {}
) : ListAdapter<Order, OrderAdapter.OrderViewHolder>(OrderDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class OrderViewHolder(
        private val binding: ItemOrderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(order: Order) {
            // Order ID - prioritize WOF format orderNumber
            val displayOrderId = if (order.orderNumber.isNotEmpty()) {
                "#${order.orderNumber}" // Display with # prefix like admin
            } else {
                "#${order.id.take(8).uppercase()}"
            }
            binding.tvOrderId.text = "Order $displayOrderId"
            
            // Order status
            setupOrderStatus(order.orderStatus)
            
            // Order date
            val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
            binding.tvOrderDate.text = dateFormat.format(Date(order.orderDate))
            
            // Item count
            val totalItems = order.items.sumOf { it.quantity }
            binding.tvItemCount.text = "$totalItems item"
            
            // Delivery address
            binding.tvDeliveryAddress.text = order.deliveryAddress
            
            // Payment method
            binding.tvPaymentMethod.text = order.paymentMethod
            
            // Total amount
            binding.tvTotalAmount.text = FormatUtils.formatPrice(order.totalAmount)
            
            // Setup action buttons based on status
            setupActionButtons(order)
            
            // Click listeners
            binding.root.setOnClickListener {
                onOrderClick(order)
            }
            
            binding.btnCancelOrder.setOnClickListener {
                onCancelOrder(order)
            }
            
            binding.btnTrackOrder.setOnClickListener {
                onTrackOrder(order)
            }
        }
        
        private fun setupOrderStatus(status: OrderStatus) {
            val statusText = when (status) {
                OrderStatus.PENDING -> "MENUNGGU"
                OrderStatus.CONFIRMED -> "DIKONFIRMASI"
                OrderStatus.PREPARING -> "DIPROSES"
                OrderStatus.ON_THE_WAY -> "DIKIRIM"
                OrderStatus.DELIVERED -> "SELESAI"
                OrderStatus.CANCELLED -> "DIBATALKAN"
            }
            
            val statusBackground = when (status) {
                OrderStatus.PENDING -> R.drawable.bg_status_pending
                OrderStatus.CONFIRMED -> R.drawable.bg_status_confirmed
                OrderStatus.PREPARING -> R.drawable.bg_status_preparing
                OrderStatus.ON_THE_WAY -> R.drawable.bg_status_on_the_way
                OrderStatus.DELIVERED -> R.drawable.bg_status_delivered
                OrderStatus.CANCELLED -> R.drawable.bg_status_cancelled
            }
            
            binding.tvOrderStatus.text = statusText
            binding.tvOrderStatus.setBackgroundResource(statusBackground)
        }
        
        private fun setupActionButtons(order: Order) {
            val canCancel = order.orderStatus in listOf(OrderStatus.PENDING, OrderStatus.CONFIRMED)
            val canTrack = order.orderStatus in listOf(
                OrderStatus.CONFIRMED, 
                OrderStatus.PREPARING, 
                OrderStatus.ON_THE_WAY
            )
            
            if (canCancel || canTrack) {
                binding.layoutActionButtons.visibility = View.VISIBLE
                binding.btnCancelOrder.visibility = if (canCancel) View.VISIBLE else View.GONE
                binding.btnTrackOrder.visibility = if (canTrack) View.VISIBLE else View.GONE
            } else {
                binding.layoutActionButtons.visibility = View.GONE
            }
        }
    }

    class OrderDiffCallback : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }
    }
}
