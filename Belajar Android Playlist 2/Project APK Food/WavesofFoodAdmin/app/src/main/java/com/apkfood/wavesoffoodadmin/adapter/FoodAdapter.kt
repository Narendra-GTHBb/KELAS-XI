package com.apkfood.wavesoffoodadmin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.apkfood.wavesoffoodadmin.R
import com.apkfood.wavesoffoodadmin.model.Food
import com.apkfood.wavesoffoodadmin.utils.ImageLoaderAdmin
import java.text.NumberFormat
import java.util.*

class FoodAdapter(
    private val onFoodClick: (Food) -> Unit,
    private val onEditClick: (Food) -> Unit,
    private val onDeleteClick: (Food) -> Unit
) : ListAdapter<Food, FoodAdapter.FoodViewHolder>(FoodDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_food, parent, false)
        return FoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class FoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivFoodImage: ImageView = itemView.findViewById(R.id.ivFoodImage)
        private val tvFoodName: TextView = itemView.findViewById(R.id.tvFoodName)
        private val tvFoodDescription: TextView = itemView.findViewById(R.id.tvFoodDescription)
        private val tvFoodPrice: TextView = itemView.findViewById(R.id.tvFoodPrice)
        private val tvRating: TextView = itemView.findViewById(R.id.tvRating)
        private val tvPrepTime: TextView = itemView.findViewById(R.id.tvPrepTime)
        private val chipPopular: Chip = itemView.findViewById(R.id.chipPopular)
        private val chipAvailable: Chip = itemView.findViewById(R.id.chipAvailable)
        private val btnEdit: ImageButton = itemView.findViewById(R.id.btnEdit)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)

        fun bind(food: Food) {
            tvFoodName.text = food.name
            tvFoodDescription.text = food.description
            tvRating.text = String.format("%.1f", food.rating)
            tvPrepTime.text = "${food.preparationTime} min"
            
            // Format price
            val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
            val formattedPrice = formatter.format(food.price).replace("IDR", "Rp")
            tvFoodPrice.text = formattedPrice
            
            // Load image with enhanced loader
            ImageLoaderAdmin.loadImage(itemView.context, food.imageUrl, ivFoodImage, 12)
            
            // Popular chip
            chipPopular.visibility = if (food.isPopular) View.VISIBLE else View.GONE
            
            // Available status
            if (food.isAvailable) {
                chipAvailable.text = "Available"
                chipAvailable.setChipBackgroundColorResource(R.color.success_green)
            } else {
                chipAvailable.text = "Unavailable"
                chipAvailable.setChipBackgroundColorResource(R.color.error_red)
            }
            
            // Click listeners
            itemView.setOnClickListener {
                onFoodClick(food)
            }
            
            btnEdit.setOnClickListener {
                onEditClick(food)
            }
            
            btnDelete.setOnClickListener {
                onDeleteClick(food)
            }
        }
    }

    class FoodDiffCallback : DiffUtil.ItemCallback<Food>() {
        override fun areItemsTheSame(oldItem: Food, newItem: Food): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Food, newItem: Food): Boolean {
            return oldItem == newItem
        }
    }
}
