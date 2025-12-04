package com.apkfood.wavesoffood.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.apkfood.wavesoffood.databinding.ItemCategoryBinding
import com.apkfood.wavesoffood.model.Category
import com.apkfood.wavesoffood.utils.ImageLoader

/**
 * Adapter untuk RecyclerView kategori makanan
 */
class CategoryAdapter(
    private val onCategoryClick: (Category) -> Unit
) : ListAdapter<Category, CategoryAdapter.CategoryViewHolder>(CategoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CategoryViewHolder(
        private val binding: ItemCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(category: Category) {
            binding.apply {
                tvCategoryName.text = category.name
                
                // Load category image if available
                if (category.imageUrl.isNotEmpty()) {
                    ImageLoader.loadImage(
                        root.context,
                        category.imageUrl,
                        ivCategoryImage,
                        8
                    )
                }
                
                root.setOnClickListener {
                    onCategoryClick(category)
                }
            }
        }
    }
}

/**
 * DiffUtil untuk performa yang lebih baik
 */
class CategoryDiffCallback : DiffUtil.ItemCallback<Category>() {
    override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
        return oldItem == newItem
    }
}
