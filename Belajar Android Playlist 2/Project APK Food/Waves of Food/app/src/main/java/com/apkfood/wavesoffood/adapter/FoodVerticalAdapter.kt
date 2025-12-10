package com.apkfood.wavesoffood.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.apkfood.wavesoffood.R
import com.apkfood.wavesoffood.data.model.Food

class FoodVerticalAdapter(
    private var foods: MutableList<Food>,
    private val onAddToCart: (Food) -> Unit,
    private val onToggleFavorite: (Food) -> Unit,
    private val onFoodClick: ((Food) -> Unit)? = null
) : RecyclerView.Adapter<FoodVerticalAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val foodImage: ImageView = itemView.findViewById(R.id.iv_food_image)
        val foodName: TextView = itemView.findViewById(R.id.tv_food_name)
        val foodDescription: TextView = itemView.findViewById(R.id.tv_food_description)
        val foodPrice: TextView = itemView.findViewById(R.id.tv_food_price)
        val btnAddToCart: TextView = itemView.findViewById(R.id.btn_add_to_cart)
        val btnFavorite: ImageView = itemView.findViewById(R.id.iv_favorite)

        fun bind(food: Food) {
            foodName.text = food.name
            foodDescription.text = food.description
            foodPrice.text = "Rp ${String.format("%,.0f", food.price)}"
            
            Glide.with(itemView.context)
                .load(food.imageUrl)
                .into(foodImage)

            // Update favorite state
            updateFavoriteState(food)

            // Click pada card untuk detail
            itemView.setOnClickListener {
                onFoodClick?.invoke(food)
            }

            btnAddToCart.setOnClickListener {
                onAddToCart(food)
            }

            btnFavorite.setOnClickListener {
                onToggleFavorite(food)
                // Animate heart with scale
                btnFavorite.animate()
                    .scaleX(1.3f)
                    .scaleY(1.3f)
                    .setDuration(150)
                    .withEndAction {
                        btnFavorite.animate()
                            .scaleX(1.0f)
                            .scaleY(1.0f)
                            .setDuration(150)
                            .withEndAction {
                                // Update state after animation completes
                                updateFavoriteState(food)
                            }
                            .start()
                    }
                    .start()
            }
        }

        private fun updateFavoriteState(food: Food) {
            val isFavorite = com.apkfood.wavesoffood.utils.FavoriteManager.isFavorite(itemView.context, food.id)
            if (isFavorite) {
                btnFavorite.setImageResource(R.drawable.ic_heart_filled) // Filled heart
            } else {
                btnFavorite.setImageResource(R.drawable.ic_heart_empty) // Empty heart
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_food_vertical, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(foods[position])
    }

    override fun getItemCount(): Int = foods.size

    fun updateFoods(newFoods: List<Food>) {
        android.util.Log.d("FoodVerticalAdapter", "🔄 Updating foods: ${newFoods.size} items")
        foods.clear()
        foods.addAll(newFoods)
        notifyDataSetChanged()
        android.util.Log.d("FoodVerticalAdapter", "✅ Foods updated, adapter now has ${foods.size} items")
    }
}
