package com.apkfood.wavesoffoodadmin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.apkfood.wavesoffoodadmin.R
import com.apkfood.wavesoffoodadmin.model.Order
import com.apkfood.wavesoffoodadmin.model.OrderStatus
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class OrderAdapter(
    private val onOrderClick: (Order) -> Unit,
    private val onStatusUpdateClick: (Order, OrderStatus) -> Unit,
    private val onConfirmOrderClick: (Order) -> Unit,
    private val onRejectOrderClick: (Order) -> Unit
) : ListAdapter<Order, OrderAdapter.OrderViewHolder>(OrderDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvOrderNumber: TextView = itemView.findViewById(R.id.tvOrderNumber)
        private val tvUserName: TextView = itemView.findViewById(R.id.tvUserName)
        private val tvOrderDate: TextView = itemView.findViewById(R.id.tvOrderDate)
        private val chipStatus: Chip = itemView.findViewById(R.id.chipStatus)
        private val tvOrderItems: TextView = itemView.findViewById(R.id.tvOrderItems)
        private val tvDeliveryAddress: TextView = itemView.findViewById(R.id.tvDeliveryAddress)
        private val tvTotalAmount: TextView = itemView.findViewById(R.id.tvTotalAmount)
        private val tvUserPhone: TextView = itemView.findViewById(R.id.tvUserPhone)
        private val layoutActions: LinearLayout = itemView.findViewById(R.id.layoutActions)
        private val layoutStatusUpdate: LinearLayout = itemView.findViewById(R.id.layoutStatusUpdate)
        private val btnReject: MaterialButton = itemView.findViewById(R.id.btnReject)
        private val btnConfirm: MaterialButton = itemView.findViewById(R.id.btnConfirm)
        private val btnMarkPreparing: MaterialButton = itemView.findViewById(R.id.btnMarkPreparing)
        private val btnMarkReady: MaterialButton = itemView.findViewById(R.id.btnMarkReady)
        private val btnMarkDelivered: MaterialButton = itemView.findViewById(R.id.btnMarkDelivered)

        fun bind(order: Order) {
            // Basic order info
            tvOrderNumber.text = "#${order.orderNumber.ifEmpty { order.id.take(8) }}"
            tvUserName.text = order.userName
            tvUserPhone.text = "📞 ${order.getPhoneDisplay()}"
            tvDeliveryAddress.text = order.deliveryAddress
            
            // Format date
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            tvOrderDate.text = dateFormat.format(Date(order.getOrderDateLong()))
            
            // Format order items
            val itemsText = order.items.joinToString(", ") { item ->
                "${item.quantity}x ${item.foodName}"
            }
            tvOrderItems.text = itemsText.ifEmpty { "No items" }
            
            // Format total amount
            val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
            tvTotalAmount.text = formatter.format(order.totalAmount).replace("IDR", "Rp")
            
            // Set status chip
            val orderStatus = order.getOrderStatusEnum()
            chipStatus.text = orderStatus.name
            chipStatus.chipBackgroundColor = itemView.context.getColorStateList(
                when (orderStatus) {
                    OrderStatus.PENDING -> R.color.status_pending
                    OrderStatus.CONFIRMED -> R.color.status_confirmed
                    OrderStatus.PREPARING -> R.color.status_preparing
                    OrderStatus.READY -> R.color.status_ready
                    OrderStatus.OUT_FOR_DELIVERY -> R.color.status_out_for_delivery
                    OrderStatus.DELIVERED -> R.color.status_delivered
                    OrderStatus.CANCELLED -> R.color.status_cancelled
                }
            )
            
            // Show/hide action buttons based on status
            when (orderStatus) {
                OrderStatus.PENDING -> {
                    layoutActions.visibility = View.VISIBLE
                    layoutStatusUpdate.visibility = View.GONE
                }
                OrderStatus.CONFIRMED, OrderStatus.PREPARING, OrderStatus.READY -> {
                    layoutActions.visibility = View.GONE
                    layoutStatusUpdate.visibility = View.VISIBLE
                    
                    // Update status buttons based on current status
                    btnMarkPreparing.isEnabled = orderStatus == OrderStatus.CONFIRMED
                    btnMarkReady.isEnabled = orderStatus == OrderStatus.PREPARING
                    btnMarkDelivered.isEnabled = orderStatus == OrderStatus.READY
                }
                else -> {
                    layoutActions.visibility = View.GONE
                    layoutStatusUpdate.visibility = View.GONE
                }
            }
            
            // Set click listeners
            itemView.setOnClickListener {
                onOrderClick(order)
            }
            
            btnConfirm.setOnClickListener {
                onConfirmOrderClick(order)
            }
            
            btnReject.setOnClickListener {
                onRejectOrderClick(order)
            }
            
            btnMarkPreparing.setOnClickListener {
                onStatusUpdateClick(order, OrderStatus.PREPARING)
            }
            
            btnMarkReady.setOnClickListener {
                onStatusUpdateClick(order, OrderStatus.READY)
            }
            
            btnMarkDelivered.setOnClickListener {
                onStatusUpdateClick(order, OrderStatus.DELIVERED)
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