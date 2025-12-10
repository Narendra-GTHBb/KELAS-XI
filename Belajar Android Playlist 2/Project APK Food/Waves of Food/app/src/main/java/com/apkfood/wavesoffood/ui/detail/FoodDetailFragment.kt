package com.apkfood.wavesoffood.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.apkfood.wavesoffood.R
import com.apkfood.wavesoffood.databinding.FragmentFoodDetailBinding
import com.apkfood.wavesoffood.data.model.Food
import com.apkfood.wavesoffood.manager.CartManager
import com.apkfood.wavesoffood.utils.GuestAccessHelper
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import java.text.NumberFormat
import java.util.*

class FoodDetailFragment : Fragment() {

    private var _binding: FragmentFoodDetailBinding? = null
    private val binding get() = _binding!!
    
    private var food: Food? = null

    companion object {
        private const val ARG_FOOD_ID = "food_id"
        private const val ARG_FOOD_NAME = "food_name"
        private const val ARG_FOOD_DESCRIPTION = "food_description"
        private const val ARG_FOOD_PRICE = "food_price"
        private const val ARG_FOOD_IMAGE_URL = "food_image_url"
        private const val ARG_FOOD_CATEGORY = "food_category"
        private const val ARG_FOOD_RATING = "food_rating"
        private const val ARG_FOOD_DELIVERY_TIME = "food_delivery_time"
        private const val ARG_FOOD_IS_POPULAR = "food_is_popular"

        fun newInstance(food: Food): FoodDetailFragment {
            val fragment = FoodDetailFragment()
            val args = Bundle().apply {
                putString(ARG_FOOD_ID, food.id)
                putString(ARG_FOOD_NAME, food.name)
                putString(ARG_FOOD_DESCRIPTION, food.description)
                putDouble(ARG_FOOD_PRICE, food.price)
                putString(ARG_FOOD_IMAGE_URL, food.imageUrl)
                putString(ARG_FOOD_CATEGORY, food.categoryId)
                putDouble(ARG_FOOD_RATING, food.rating)
                putString(ARG_FOOD_DELIVERY_TIME, "30-45 min") // Default delivery time
                putBoolean(ARG_FOOD_IS_POPULAR, true) // Default popular
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { args ->
            food = Food(
                id = args.getString(ARG_FOOD_ID, ""),
                name = args.getString(ARG_FOOD_NAME, ""),
                description = args.getString(ARG_FOOD_DESCRIPTION, ""),
                price = args.getDouble(ARG_FOOD_PRICE, 0.0),
                imageUrl = args.getString(ARG_FOOD_IMAGE_URL, ""),
                categoryId = args.getString(ARG_FOOD_CATEGORY, ""),
                rating = args.getDouble(ARG_FOOD_RATING, 0.0)
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFoodDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupUI()
        setupClickListeners()
    }

    private fun setupUI() {
        food?.let { foodItem ->
            // Load food image
            Glide.with(this)
                .load(foodItem.imageUrl)
                .apply(
                    RequestOptions()
                        .transform(RoundedCorners(24))
                        .placeholder(R.drawable.placeholder_food)
                        .error(R.drawable.placeholder_food)
                )
                .into(binding.ivFoodImage)

            // Set food details
            binding.tvFoodName.text = foodItem.name
            binding.tvFoodDescription.text = foodItem.description
            binding.tvFoodCategory.text = "Kategori: ${foodItem.categoryId}"
            binding.tvFoodPrice.text = formatPrice(foodItem.price)
            binding.tvFoodRating.text = foodItem.rating.toString()
            binding.tvDeliveryTime.text = "30-45 min"
            
            // Show popular badge if applicable
            binding.tvPopularBadge.visibility = View.VISIBLE
            
            // Set additional details
            setupIngredients(foodItem)
            setupNutritionInfo(foodItem)
        }
    }

    private fun setupIngredients(food: Food) {
        // Sample ingredients based on food category
        val ingredients = when (food.categoryId.lowercase()) {
            "pizza" -> listOf("Flour", "Tomato Sauce", "Mozzarella Cheese", "Olive Oil", "Fresh Basil")
            "burger" -> listOf("Beef Patty", "Lettuce", "Tomato", "Onion", "Cheese", "Bun")
            "chicken" -> listOf("Fresh Chicken", "Spices", "Herbs", "Salt", "Pepper")
            "rice" -> listOf("Jasmine Rice", "Chicken", "Vegetables", "Soy Sauce", "Garlic")
            "dessert" -> listOf("Flour", "Sugar", "Eggs", "Butter", "Vanilla", "Chocolate")
            "drinks" -> listOf("Fresh Fruits", "Water", "Sugar", "Ice", "Natural Flavoring")
            "noodles" -> listOf("Wheat Noodles", "Broth", "Vegetables", "Meat", "Seasonings")
            else -> listOf("Fresh Ingredients", "Quality Spices", "Natural Flavors")
        }
        
        binding.tvIngredients.text = ingredients.joinToString(", ")
    }

    private fun setupNutritionInfo(food: Food) {
        // Sample nutrition info based on food type
        val calories = when (food.categoryId.lowercase()) {
            "pizza" -> "320-450 cal"
            "burger" -> "420-580 cal"
            "chicken" -> "250-380 cal"
            "rice" -> "300-400 cal"
            "dessert" -> "280-420 cal"
            "drinks" -> "50-200 cal"
            "noodles" -> "350-480 cal"
            else -> "250-400 cal"
        }
        
        binding.tvCalories.text = calories
        binding.tvProtein.text = "15-25g"
        binding.tvCarbs.text = "30-45g"
        binding.tvFat.text = "8-15g"
    }

    private fun setupClickListeners() {
        // Back button
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        
        // Add to cart button
        binding.btnAddToCart.setOnClickListener {
            food?.let { foodItem ->
                if (GuestAccessHelper.checkCartAccess(requireContext())) {
                    val quantity = binding.tvQuantity.text.toString().toIntOrNull() ?: 1
                    repeat(quantity) {
                        CartManager.getInstance().addToCart(foodItem)
                    }
                    android.widget.Toast.makeText(
                        context, 
                        "Berhasil menambahkan ${foodItem.name} (${quantity}x) ke keranjang!", 
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        
        // Favorite button
        binding.btnFavorite.setOnClickListener {
            // Handle favorite logic here
            android.widget.Toast.makeText(
                context, 
                "Added ${food?.name} to favorites!", 
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }
        
        // Quantity buttons
        binding.btnMinus.setOnClickListener {
            val currentQuantity = binding.tvQuantity.text.toString().toIntOrNull() ?: 1
            if (currentQuantity > 1) {
                val newQuantity = currentQuantity - 1
                binding.tvQuantity.text = newQuantity.toString()
                updateTotalPrice(newQuantity)
            }
        }
        
        binding.btnPlus.setOnClickListener {
            val currentQuantity = binding.tvQuantity.text.toString().toIntOrNull() ?: 1
            val newQuantity = currentQuantity + 1
            binding.tvQuantity.text = newQuantity.toString()
            updateTotalPrice(newQuantity)
        }
    }
    
    private fun updateTotalPrice(quantity: Int) {
        food?.let { foodItem ->
            val totalPrice = foodItem.price * quantity
            binding.tvTotalPrice.text = formatPrice(totalPrice)
        }
    }
    
    private fun formatPrice(price: Double): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        return formatter.format(price).replace("IDR", "Rp")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
