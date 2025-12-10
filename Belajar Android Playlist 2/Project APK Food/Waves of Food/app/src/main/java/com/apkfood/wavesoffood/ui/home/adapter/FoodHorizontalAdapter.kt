package com.apkfood.wavesoffood.ui.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.apkfood.wavesoffood.R
import com.apkfood.wavesoffood.data.model.Food
import com.apkfood.wavesoffood.utils.FavoriteManager
import java.text.NumberFormat
import java.util.*

/**
 * Adapter untuk menampilkan makanan dalam format horizontal
 */
class FoodHorizontalAdapter(
    private var foods: MutableList<Food>,
    private val onFoodClick: (Food) -> Unit,
    private val onFavoriteClick: (Food) -> Unit,
    private val onAddToCartClick: (Food) -> Unit
) : RecyclerView.Adapter<FoodHorizontalAdapter.FoodViewHolder>(), FavoriteManager.FavoriteUpdateListener {

    inner class FoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivFoodImage: ImageView = itemView.findViewById(R.id.iv_food_image)
        private val ivFavorite: ImageView = itemView.findViewById(R.id.iv_favorite)
        private val tvRating: TextView = itemView.findViewById(R.id.tv_rating)
        private val tvFoodName: TextView = itemView.findViewById(R.id.tv_food_name)
        private val tvFoodCategory: TextView = itemView.findViewById(R.id.tv_food_category)
        private val tvFoodPrice: TextView = itemView.findViewById(R.id.tv_food_price)
        private val ivAddToCart: ImageView = itemView.findViewById(R.id.iv_add_to_cart)

        fun bind(food: Food) {
            tvFoodName.text = food.name
            tvFoodCategory.text = food.categoryName
            tvRating.text = food.rating.toString()
            
            // Format harga
            val formatRupiah = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
            tvFoodPrice.text = formatRupiah.format(food.price)

            // Load gambar dari URL menggunakan Glide
            try {
                if (food.imageUrl.isNotEmpty()) {
                    Glide.with(itemView.context)
                        .load(food.imageUrl)
                        .placeholder(R.drawable.placeholder_food)
                        .error(R.drawable.placeholder_food)
                        .centerCrop()
                        .timeout(10000)
                        .into(ivFoodImage)
                } else {
                    // Fallback ke placeholder jika URL kosong
                    val imageResource = when (food.categoryName.lowercase()) {
                        "pizza" -> R.drawable.ic_pizza
                        "burger" -> R.drawable.ic_burger
                        "chicken" -> R.drawable.ic_food
                        "rice" -> R.drawable.ic_food
                        "dessert" -> R.drawable.ic_dessert
                        "drinks" -> R.drawable.ic_drinks
                        "noodles" -> R.drawable.ic_food
                        else -> R.drawable.placeholder_food
                    }
                    ivFoodImage.setImageResource(imageResource)
                }
            } catch (e: Exception) {
                ivFoodImage.setImageResource(R.drawable.placeholder_food)
            }

            // Update favorite state
            updateFavoriteState(food)

            // Click listeners
            itemView.setOnClickListener {
                onFoodClick(food)
            }

            ivFavorite.setOnClickListener {
                onFavoriteClick(food)
                // Update favorite state after click dengan animasi
                updateFavoriteStateWithAnimation(food)
            }

            ivAddToCart.setOnClickListener {
                onAddToCartClick(food)
            }
        }
        
        private fun updateFavoriteState(food: Food) {
            val isFavorite = FavoriteManager.isFavorite(itemView.context, food.id)
            if (isFavorite) {
                ivFavorite.setImageResource(R.drawable.ic_heart_filled)
                ivFavorite.setColorFilter(android.graphics.Color.RED)
            } else {
                ivFavorite.setImageResource(R.drawable.ic_heart_empty)
                ivFavorite.clearColorFilter()
            }
        }
        
        private fun updateFavoriteStateWithAnimation(food: Food) {
            // Scale animation pada favorite button
            ivFavorite.animate()
                .scaleX(1.3f)
                .scaleY(1.3f)
                .setDuration(150)
                .withEndAction {
                    updateFavoriteState(food)
                    ivFavorite.animate()
                        .scaleX(1.0f)
                        .scaleY(1.0f)
                        .setDuration(150)
                        .start()
                }
                .start()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_food_horizontal, parent, false)
        return FoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        holder.bind(foods[position])
    }

    override fun getItemCount(): Int = foods.size
    
    fun updateFoods(newFoods: List<Food>) {
        android.util.Log.d("FoodHorizontalAdapter", "🔄 Updating foods: ${newFoods.size} items")
        foods.clear()
        foods.addAll(newFoods)
        notifyDataSetChanged()
        android.util.Log.d("FoodHorizontalAdapter", "✅ Foods updated, adapter now has ${foods.size} items")
    }
    
    override fun onFavoriteUpdated() {
        // Refresh semua items saat favorite berubah
        notifyDataSetChanged()
    }
    
    fun attachToFavoriteManager() {
        FavoriteManager.addListener(this)
    }
    
    fun detachFromFavoriteManager() {
        FavoriteManager.removeListener(this)
    }
}
