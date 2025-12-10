package com.apkfood.wavesoffood.ui.home

import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.apkfood.wavesoffood.R
import com.apkfood.wavesoffood.databinding.FragmentHomeBinding
import com.apkfood.wavesoffood.data.model.Category
import com.apkfood.wavesoffood.data.model.Food
import com.apkfood.wavesoffood.data.model.NutritionInfo
import com.apkfood.wavesoffood.manager.FoodManager
import com.apkfood.wavesoffood.ui.home.adapter.CategoryAdapter
import com.apkfood.wavesoffood.ui.home.adapter.FoodHorizontalAdapter
import com.apkfood.wavesoffood.adapter.FoodVerticalAdapter
import com.apkfood.wavesoffood.ui.detail.FoodDetailFragment
import com.apkfood.wavesoffood.manager.CartManager
import com.apkfood.wavesoffood.data.FoodDataSource
import com.apkfood.wavesoffood.utils.FavoriteManager
import com.apkfood.wavesoffood.utils.GuestAccessHelper
import com.apkfood.wavesoffood.repository.FoodRepository
import com.apkfood.wavesoffood.repository.CategoryRepository
import kotlinx.coroutines.*
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

/**
 * Home Fragment
 * Fragment utama aplikasi yang menampilkan kategori dan makanan
 */
class HomeFragment : Fragment() {
    
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var foodManager: FoodManager
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var foodRepository: FoodRepository
    private lateinit var categoryRepository: CategoryRepository
    
    // Adapters
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var popularFoodAdapter: FoodHorizontalAdapter
    private lateinit var recommendedFoodAdapter: FoodVerticalAdapter
    
    // Data lists
    private val categories = mutableListOf<Category>()
    private val popularFoods = mutableListOf<Food>()
    private val recommendedFoods = mutableListOf<Food>()
    private val allFoods = mutableListOf<Food>()
    
    // Original data backup (to prevent data loss during filtering)
    private val originalPopularFoods = mutableListOf<Food>()
    private val originalRecommendedFoods = mutableListOf<Food>()
    
    // Args
    private var username: String = "Guest"
    private var isLoggedIn: Boolean = false
    
    companion object {
        private const val ARG_USERNAME = "username"
        private const val ARG_IS_LOGGED_IN = "is_logged_in"
        
        fun newInstance(username: String?, isLoggedIn: Boolean): HomeFragment {
            val fragment = HomeFragment()
            val args = Bundle().apply {
                putString(ARG_USERNAME, username)
                putBoolean(ARG_IS_LOGGED_IN, isLoggedIn)
            }
            fragment.arguments = args
            return fragment
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            username = it.getString(ARG_USERNAME) ?: "Guest"
            isLoggedIn = it.getBoolean(ARG_IS_LOGGED_IN, false)
        }
        
        // Initialize data manager and repository
        foodManager = FoodManager()
        foodRepository = FoodRepository()
        categoryRepository = CategoryRepository()
        
        // Load data from Firebase
        loadFirebaseData()
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupUI()
        setupAdapters()
        setupSearchBar()
        updateUserGreeting()
        
        // Categories will be loaded from Firebase in loadFirebaseData()
    }
    
    private fun loadFirebaseData() {
        // Use coroutine to call suspend function
        GlobalScope.launch(Dispatchers.Main) {
            try {
                // Load both foods and categories concurrently
                val (foods, firebaseCategories) = withContext(Dispatchers.IO) {
                    val foodsDeferred = async { foodRepository.getAllFoods() }
                    val categoriesDeferred = async { categoryRepository.getAllCategories() }
                    
                    Pair(foodsDeferred.await(), categoriesDeferred.await())
                }
                
                // Clear existing data
                allFoods.clear()
                popularFoods.clear()
                recommendedFoods.clear()
                categories.clear()
                
                allFoods.addAll(foods)
                
                // Update categories with Firebase data + "All" category
                val allCategory = Category("all", "All", "🍽️")
                categories.add(allCategory)
                
                // Map Firebase categories with proper icons
                val mappedCategories = firebaseCategories.map { category ->
                    val iconName = mapCategoryToIcon(category.name.lowercase())
                    category.copy(icon = iconName)
                }
                categories.addAll(mappedCategories)
                
                // Populate popular foods (prioritize foods with high ratings or marked as popular)
                val popularItems = foods.filter { 
                    (it.isPopular == true) || (it.rating > 0.0 && it.rating >= 4.5) 
                }.sortedByDescending { it.rating }
                    .take(10)
                
                // If not enough popular items, add more from highest rated
                if (popularItems.size < 5) {
                    val additionalItems = foods.sortedByDescending { 
                        if (it.rating > 0.0) it.rating else 3.0 // Default rating if not set
                    }.filter { !popularItems.contains(it) }
                        .take(10 - popularItems.size)
                    popularFoods.addAll(popularItems + additionalItems)
                } else {
                    popularFoods.addAll(popularItems)
                }
                
                // IMPORTANT: Backup original data before any filtering
                originalPopularFoods.clear()
                originalPopularFoods.addAll(popularFoods)
                
                // Populate recommended foods (different from popular, or use isRecommended flag)
                val recommendedItems = foods.filter { 
                    (it.isRecommended == true) || (!popularFoods.contains(it))
                }.sortedByDescending { 
                    if (it.rating > 0.0) it.rating else 3.0 // Default rating if not set
                }.take(20)
                
                recommendedFoods.addAll(recommendedItems)
                
                // Ensure we always have some recommended foods
                if (recommendedFoods.isEmpty() && foods.isNotEmpty()) {
                    recommendedFoods.addAll(foods.take(10))
                    Log.d("HomeFragment", "⚠️ No recommended foods found, using first 10 foods as fallback")
                }
                
                // IMPORTANT: Backup original recommended foods too
                originalRecommendedFoods.clear()
                originalRecommendedFoods.addAll(recommendedFoods)
                
                // TEMPORARY DEBUG: Force some items if still empty
                if (recommendedFoods.isEmpty()) {
                    Log.d("HomeFragment", "⚠️ CRITICAL: No foods available at all!")
                }
                
                Log.d("HomeFragment", "✅ Loaded ${foods.size} total foods")
                Log.d("HomeFragment", "✅ Popular foods: ${popularFoods.size}")
                Log.d("HomeFragment", "✅ Recommended foods: ${recommendedFoods.size}")
                Log.d("HomeFragment", "✅ Categories: ${categories.size}")
                
                // Debug: Print some food details
                foods.take(5).forEach { food ->
                    Log.d("HomeFragment", "🍽️ Food: ${food.name} - CategoryId: '${food.categoryId}', Popular: ${food.isPopular}, Recommended: ${food.isRecommended}, Rating: ${food.rating}")
                }
                
                // Debug: Show all unique categoryIds
                val uniqueCategoryIds = foods.map { it.categoryId }.distinct().filter { it.isNotEmpty() }
                Log.d("HomeFragment", "📂 Found categoryIds: $uniqueCategoryIds")
                
                // Update UI
                if (::categoryAdapter.isInitialized) {
                    categoryAdapter.updateCategories(categories)
                    Log.d("HomeFragment", "✅ Updated categories")
                }
                if (::popularFoodAdapter.isInitialized) {
                    popularFoodAdapter.notifyDataSetChanged()
                    Log.d("HomeFragment", "✅ Updated popular foods")
                }
                if (::recommendedFoodAdapter.isInitialized) {
                    Log.d("HomeFragment", "🔄 Updating recommended adapter with ${recommendedFoods.size} foods")
                    recommendedFoodAdapter.updateFoods(recommendedFoods)
                    Log.d("HomeFragment", "✅ Updated recommended foods")
                    
                    // Force visibility and refresh
                    binding.rvRecommendedFoods.visibility = android.view.View.VISIBLE
                    binding.rvRecommendedFoods.requestLayout()
                }
            } catch (e: Exception) {
                Log.e("HomeFragment", "Error loading Firebase data: ${e.message}")
            }
        }
    }
    
    private fun setupUI() {
        // Setup UI components if needed
    }
    
    private fun setupAdapters() {
        // Category Adapter
        categoryAdapter = CategoryAdapter { category ->
            filterByCategory(category.id)
        }
        binding.rvCategories.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = categoryAdapter
        }
        
        // Popular Food Adapter
        popularFoodAdapter = FoodHorizontalAdapter(
            foods = popularFoods,
            onFoodClick = { food -> navigateToFoodDetail(food) },
            onFavoriteClick = { food -> 
                val isFavorite = FavoriteManager.toggleFavorite(requireContext(), food)
                android.widget.Toast.makeText(context, 
                    if (isFavorite) "Ditambahkan ke favorit" else "Dihapus dari favorit", 
                    android.widget.Toast.LENGTH_SHORT).show()
            },
            onAddToCartClick = { food -> /* Handle add to cart */ }
        )
        binding.rvPopularFoods.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = popularFoodAdapter
        }
        
        // Recommended Food Adapter
        recommendedFoodAdapter = FoodVerticalAdapter(
            foods = mutableListOf(),  // Start with empty list
            onAddToCart = { food -> /* Handle add to cart */ },
            onToggleFavorite = { food -> 
                val isFavorite = FavoriteManager.toggleFavorite(requireContext(), food)
                android.widget.Toast.makeText(context, 
                    if (isFavorite) "Ditambahkan ke favorit" else "Dihapus dari favorit", 
                    android.widget.Toast.LENGTH_SHORT).show()
            },
            onFoodClick = { food -> navigateToFoodDetail(food) }
        )
        binding.rvRecommendedFoods.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = recommendedFoodAdapter
            // Ensure visibility
            visibility = android.view.View.VISIBLE
        }
    }
    
    private fun updateUserGreeting() {
        val displayName = if (isLoggedIn && username.isNotEmpty() && username != "Guest") {
            username.replaceFirstChar { 
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            }
        } else {
            "Guest"
        }
        
        binding.tvUserName.text = "Hi, $displayName!"
        binding.tvLocation.text = "Jakarta, Indonesia"
    }
    
    private fun setupSearchBar() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterFoods(s.toString())
            }
            
            override fun afterTextChanged(s: Editable?) {}
        })
    }
    
    private fun filterFoods(query: String) {
        Log.d("HomeFragment", "🔍 Searching for: $query")
        
        if (query.isEmpty()) {
            // Restore original lists - but keep categories unchanged
            popularFoodAdapter.updateFoods(originalPopularFoods)
            recommendedFoodAdapter.updateFoods(originalRecommendedFoods)
            return
        }
        
        val lowercaseQuery = query.lowercase()
        
        // Filter only food lists using original data, keep categories unchanged
        val filteredPopularFoods = originalPopularFoods.filter {
            it.name.lowercase().contains(lowercaseQuery) || 
            it.description.lowercase().contains(lowercaseQuery)
        }
        
        val filteredRecommendedFoods = originalRecommendedFoods.filter {
            it.name.lowercase().contains(lowercaseQuery) || 
            it.description.lowercase().contains(lowercaseQuery)
        }
        
        Log.d("HomeFragment", "🔍 Found ${filteredPopularFoods.size} popular, ${filteredRecommendedFoods.size} recommended")
        
        // Update only food adapters, never touch categories
        popularFoodAdapter.updateFoods(filteredPopularFoods)
        recommendedFoodAdapter.updateFoods(filteredRecommendedFoods)
    }
    
    private fun filterByCategory(categoryId: String?) {
        Log.d("HomeFragment", "🔍 Filtering by category: $categoryId")
        
        // Always get fresh data from original backup
        val allPopularFoods = originalPopularFoods.toList()
        Log.d("HomeFragment", "🔍 Original popular foods available: ${allPopularFoods.size}")
        
        if (categoryId == null || categoryId == "all") {
            // Show all foods - reset to original data
            Log.d("HomeFragment", "🔍 Showing all popular foods: ${allPopularFoods.size}")
            popularFoodAdapter.updateFoods(allPopularFoods)
        } else {
            // Filter popular foods by category
            allPopularFoods.forEach { food ->
                Log.d("HomeFragment", "🔍 Popular food: ${food.name} has categoryId: '${food.categoryId}'")
            }
            
            val filteredPopularFoods = allPopularFoods.filter { 
                food -> food.categoryId == categoryId 
            }
            Log.d("HomeFragment", "🔍 Found ${filteredPopularFoods.size} popular foods for category $categoryId")
            
            // If no foods found, show all as fallback
            if (filteredPopularFoods.isEmpty()) {
                Log.d("HomeFragment", "⚠️ No foods found for category $categoryId, showing all foods")
                popularFoodAdapter.updateFoods(allPopularFoods)
            } else {
                popularFoodAdapter.updateFoods(filteredPopularFoods)
            }
        }
    }
    
    private fun updateLists(
        newCategories: List<Category>,
        newPopularFoods: List<Food>,
        newRecommendedFoods: List<Food>
    ) {
        // Update categories
        categories.clear()
        categories.addAll(newCategories)
        categoryAdapter.updateCategories(categories)
        
        // Update popular foods
        popularFoods.clear()
        popularFoods.addAll(newPopularFoods)
        popularFoodAdapter.notifyDataSetChanged()
        
        // Update recommended foods
        recommendedFoods.clear()
        recommendedFoods.addAll(newRecommendedFoods)
        recommendedFoodAdapter.updateFoods(recommendedFoods)
    }
    
    private fun navigateToFoodDetail(food: Food) {
        val foodDetailFragment = FoodDetailFragment.newInstance(food)
        
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, foodDetailFragment)
            .addToBackStack(null)
            .commit()
    }
    
    private fun getPopularFoods(): List<Food> {
        return originalPopularFoods.toList()  // Always return original data
    }

    private fun getRecommendedFoods(): List<Food> {
        return originalRecommendedFoods.toList()  // Always return original data
    }
    
    private fun getDummyCategories(): List<Category> {
        return listOf(
            Category("1", "Burger", "ic_burger"),
            Category("2", "Pizza", "ic_pizza"),
            Category("3", "Fried Chicken", "ic_food"),
            Category("4", "Sandwich", "ic_food"),
            Category("5", "Pasta", "ic_food"),
            Category("6", "Dessert", "ic_dessert"),
            Category("7", "Drinks", "ic_drinks"),
            Category("8", "Noodles", "ic_sushi")
        )
    }
    
    private fun mapCategoryToIcon(categoryName: String): String {
        return when {
            categoryName.contains("burger") -> "ic_burger"
            categoryName.contains("pizza") -> "ic_pizza"
            categoryName.contains("chicken") -> "ic_food"
            categoryName.contains("dessert") -> "ic_dessert"
            categoryName.contains("drink") -> "ic_drinks"
            categoryName.contains("noodle") -> "ic_sushi"
            categoryName.contains("rice") -> "ic_food"
            categoryName.contains("snack") -> "ic_food"
            else -> "ic_food"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        
        // Detach adapter from favorite manager
        if (::popularFoodAdapter.isInitialized) {
            popularFoodAdapter.detachFromFavoriteManager()
        }
        
        _binding = null
    }
}