package com.apkfood.wavesoffood.ui.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.apkfood.wavesoffood.R
import com.apkfood.wavesoffood.data.model.Category
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

/**
 * Adapter untuk menampilkan daftar kategori makanan
 */
class CategoryAdapter(
    private val onCategoryClick: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private val categories = mutableListOf<Category>()

    fun updateCategories(newCategories: List<Category>) {
        categories.clear()
        categories.addAll(newCategories)
        notifyDataSetChanged()
    }

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivCategoryIcon: ImageView = itemView.findViewById(R.id.ivCategoryImage)
        private val tvCategoryEmoji: TextView = itemView.findViewById(R.id.tvCategoryEmoji)
        private val tvCategoryName: TextView = itemView.findViewById(R.id.tvCategoryName)

        fun bind(category: Category) {
            tvCategoryName.text = category.name
            
            // Use emoji for category icons
            setEmojiIcon(category)

            itemView.setOnClickListener {
                onCategoryClick(category)
            }
        }
        
        private fun setEmojiIcon(category: Category) {
            // Map category to emoji based on ID or name
            val emoji = when (category.id?.lowercase() ?: category.name.lowercase()) {
                "cat_burger", "burger" -> "🍔"
                "cat_pizza", "pizza" -> "🍕"
                "cat_chicken", "chicken", "fried chicken" -> "🍗"
                "cat_dessert", "dessert", "desserts" -> "🧁"
                "cat_drinks", "drink", "drinks" -> "🧃"
                "cat_noodles", "noodle", "noodles" -> "🍜"
                "cat_rice", "rice", "rice dishes" -> "🍚"
                "cat_snacks", "snack", "snacks" -> "🍿"
                "all" -> "🍽️"
                else -> "🍴"
            }
            
            // Set emoji to TextView
            tvCategoryEmoji.text = emoji
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount(): Int = categories.size
}
