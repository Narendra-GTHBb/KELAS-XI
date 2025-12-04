package com.apkfood.wavesoffood.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.apkfood.wavesoffood.databinding.ItemFoodBinding
import com.apkfood.wavesoffood.model.FoodItem
import com.apkfood.wavesoffood.utils.FormatUtils
import com.apkfood.wavesoffood.utils.ImageLoader

/**
 * Adapter untuk RecyclerView makanan
 */
class FoodAdapter(
    private val onFoodClick: (FoodItem) -> Unit
) : ListAdapter<FoodItem, FoodAdapter.FoodViewHolder>(FoodDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val binding = ItemFoodBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class FoodViewHolder(
        private val binding: ItemFoodBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(foodItem: FoodItem) {
            binding.apply {
                tvFoodName.text = foodItem.name
                tvFoodPrice.text = FormatUtils.formatCurrency(foodItem.price)
                tvFoodRating.text = foodItem.rating.toString()
                
                // Load food image
                if (foodItem.imageUrl.isNotEmpty()) {
                    ImageLoader.loadImage(
                        root.context,
                        foodItem.imageUrl,
                        ivFoodImage,
                        16
                    )
                }
                
                root.setOnClickListener {
                    onFoodClick(foodItem)
                }
            }
        }
    }
}

/**
 * DiffUtil untuk performa yang lebih baik
 */
class FoodDiffCallback : DiffUtil.ItemCallback<FoodItem>() {
    override fun areItemsTheSame(oldItem: FoodItem, newItem: FoodItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FoodItem, newItem: FoodItem): Boolean {
        return oldItem == newItem
    }
}
