package com.apkfood.wavesoffood.ui.cart.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.apkfood.wavesoffood.databinding.ItemCartBinding
import com.apkfood.wavesoffood.model.CartItem
import com.apkfood.wavesoffood.utils.FormatUtils

/**
 * Adapter untuk menampilkan item dalam keranjang belanja
 */
class CartAdapter(
    private val onQuantityChanged: (String, Int) -> Unit,
    private val onRemoveItem: (String) -> Unit
) : ListAdapter<CartItem, CartAdapter.CartViewHolder>(CartDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CartViewHolder(binding, onQuantityChanged, onRemoveItem)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    override fun onBindViewHolder(
        holder: CartViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty() && payloads[0] == "QUANTITY_CHANGED") {
            holder.updateQuantityOnly(getItem(position))
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    class CartViewHolder(
        private val binding: ItemCartBinding,
        private val onQuantityChanged: (String, Int) -> Unit,
        private val onRemoveItem: (String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(cartItem: CartItem) {
            with(binding) {
                // Set food details
                textFoodName.text = cartItem.food.name
                textFoodDescription.text = cartItem.food.description
                textFoodPrice.text = FormatUtils.formatPrice(cartItem.food.price)
                
                // Set quantity dan total price langsung tanpa animasi (untuk initial bind)
                textQuantity.text = cartItem.quantity.toString()
                textTotalPrice.text = FormatUtils.formatPrice(cartItem.getTotalPrice())

                // Load food image
                Glide.with(imageFoodPhoto.context)
                    .load(cartItem.food.imageUrl)
                    .centerCrop()
                    .into(imageFoodPhoto)

                // Set click listeners
                buttonDecrease.setOnClickListener {
                    // Ambil quantity terbaru dari CartManager
                    val currentQuantity = com.apkfood.wavesoffood.manager.CartManager.getInstance().getQuantity(cartItem.food.id)
                    val newQuantity = currentQuantity - 1
                    if (newQuantity > 0) {
                        onQuantityChanged(cartItem.food.id, newQuantity)
                    } else {
                        onRemoveItem(cartItem.food.id)
                    }
                }

                buttonIncrease.setOnClickListener {
                    // Ambil quantity terbaru dari CartManager
                    val currentQuantity = com.apkfood.wavesoffood.manager.CartManager.getInstance().getQuantity(cartItem.food.id)
                    val newQuantity = currentQuantity + 1
                    onQuantityChanged(cartItem.food.id, newQuantity)
                }

                buttonRemove.setOnClickListener {
                    onRemoveItem(cartItem.food.id)
                }
            }
        }
        
        fun updateQuantityOnly(cartItem: CartItem) {
            with(binding) {
                // Update hanya quantity dan total price dengan animasi
                updateQuantityWithAnimation(cartItem.quantity)
                textTotalPrice.text = FormatUtils.formatPrice(cartItem.getTotalPrice())
            }
        }
        
        private fun updateQuantityWithAnimation(quantity: Int) {
            with(binding.textQuantity) {
                // Scale animation saat quantity berubah
                animate()
                    .scaleX(1.3f)
                    .scaleY(1.3f)
                    .setDuration(150)
                    .withEndAction {
                        text = quantity.toString()
                        animate()
                            .scaleX(1.0f)
                            .scaleY(1.0f)
                            .setDuration(150)
                            .start()
                    }
                    .start()
            }
        }
        
        private fun animateQuantityChange(onComplete: () -> Unit) {
            with(binding) {
                // Disable buttons sementara
                buttonIncrease.isEnabled = false
                buttonDecrease.isEnabled = false
                
                // Pulse animation pada quantity
                textQuantity.animate()
                    .scaleX(0.8f)
                    .scaleY(0.8f)
                    .setDuration(100)
                    .withEndAction {
                        textQuantity.animate()
                            .scaleX(1.0f)
                            .scaleY(1.0f)
                            .setDuration(100)
                            .withEndAction {
                                // Enable buttons kembali
                                buttonIncrease.isEnabled = true
                                buttonDecrease.isEnabled = true
                                onComplete()
                            }
                            .start()
                    }
                    .start()
            }
        }
    }

    class CartDiffCallback : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem.food.id == newItem.food.id
        }

        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem.food.id == newItem.food.id &&
                   oldItem.quantity == newItem.quantity &&
                   oldItem.food.price == newItem.food.price
        }
        
        override fun getChangePayload(oldItem: CartItem, newItem: CartItem): Any? {
            return if (oldItem.quantity != newItem.quantity) {
                "QUANTITY_CHANGED"
            } else {
                null
            }
        }
    }
}
