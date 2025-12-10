package com.apkfood.wavesoffood.ui.favorite

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.apkfood.wavesoffood.databinding.FragmentFavoriteBinding
import com.apkfood.wavesoffood.adapter.FoodVerticalAdapter
import com.apkfood.wavesoffood.ui.detail.FoodDetailFragment
import com.apkfood.wavesoffood.utils.FavoriteManager
import com.apkfood.wavesoffood.utils.GuestAccessHelper
import com.apkfood.wavesoffood.manager.CartManager
import com.apkfood.wavesoffood.data.model.Food
import com.apkfood.wavesoffood.repository.FoodRepository
import kotlinx.coroutines.*

/**
 * Fragment untuk menampilkan makanan favorit
 */
class FavoriteFragment : Fragment(), FavoriteManager.FavoriteUpdateListener {
    
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var favoriteAdapter: FoodVerticalAdapter
    private lateinit var foodRepository: FoodRepository
    private val allFoods = mutableListOf<Food>() // Cache Firebase foods for filtering
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Initialize Firebase repository
        foodRepository = FoodRepository()
        
        setupViews()
        setupRecyclerView()
        loadFirebaseFoods()  // Load from Firebase instead of dummy data
        
        // Register as favorite listener
        FavoriteManager.addListener(this)
    }

    override fun onResume() {
        super.onResume()
        // Always refresh favorites when fragment becomes visible
        // This ensures data is loaded correctly after user switches accounts
        updateFavoriteDisplay()
    }
    
    private fun setupViews() {
        // Any additional setup can go here
    }
    
    private fun setupRecyclerView() {
        favoriteAdapter = FoodVerticalAdapter(
            mutableListOf(),
            onAddToCart = { food ->
                if (GuestAccessHelper.checkCartAccess(requireContext())) {
                    CartManager.getInstance().addToCart(food)
                    showToast("${food.name} ditambahkan ke keranjang")
                }
            },
            onToggleFavorite = { food ->
                val isFavorite = FavoriteManager.toggleFavorite(requireContext(), food)
                showToast(
                    if (isFavorite) "Ditambahkan ke favorit" 
                    else "Dihapus dari favorit"
                )
            },
            onFoodClick = { food ->
                // Navigate to food detail
                val detailFragment = com.apkfood.wavesoffood.ui.detail.FoodDetailFragment.newInstance(food)
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(android.R.id.content, detailFragment)
                    .addToBackStack(null)
                    .commit()
            }
        )
        
        binding.rvFavoriteItems.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = favoriteAdapter
        }
    }
    
    private fun loadFirebaseFoods() {
        // Load foods from Firebase like HomeFragment and MenuFragment
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val foods = withContext(Dispatchers.IO) {
                    foodRepository.getAllFoods()
                }
                
                allFoods.clear()
                allFoods.addAll(foods)
                
                Log.d("FavoriteFragment", "✅ Loaded ${foods.size} foods from Firebase")
                
                // Now update favorites display with real Firebase data
                updateFavoriteDisplay()
                
            } catch (e: Exception) {
                Log.e("FavoriteFragment", "Error loading foods from Firebase: ${e.message}")
                // Fallback: still try to update display
                updateFavoriteDisplay()
            }
        }
    }
    
    private fun updateFavoriteDisplay() {
        // Check if user can access favorites (only for registered users)
        if (!FavoriteManager.canAccessFavorites(requireContext())) {
            // Guest user - show empty state with login prompt
            showGuestState()
            return
        }
        
        val favoriteIds = FavoriteManager.getFavoriteIds(requireContext())
        val favoriteFoods = allFoods.filter { favoriteIds.contains(it.id) }
        
        Log.d("FavoriteFragment", "🔍 User can access favorites. Favorite IDs: $favoriteIds")
        Log.d("FavoriteFragment", "🔍 All foods loaded: ${allFoods.size}")
        allFoods.take(3).forEach { food ->
            Log.d("FavoriteFragment", "🍽️ Available food: ${food.name} (ID: ${food.id})")
        }
        Log.d("FavoriteFragment", "✅ Found ${favoriteFoods.size} favorite foods")
        favoriteFoods.forEach { food ->
            Log.d("FavoriteFragment", "❤️ Favorite food: ${food.name} (ID: ${food.id})")
        }
        
        if (favoriteFoods.isEmpty()) {
            // Show empty state
            binding.layoutEmptyFavorite.visibility = View.VISIBLE
            binding.rvFavoriteItems.visibility = View.GONE
        } else {
            // Show favorite items
            binding.layoutEmptyFavorite.visibility = View.GONE
            binding.rvFavoriteItems.visibility = View.VISIBLE
            
            // Update adapter
            favoriteAdapter.updateFoods(favoriteFoods)
        }
    }
    
    private fun showGuestState() {
        // Show message that user needs to login to access favorites
        binding.layoutEmptyFavorite.visibility = View.VISIBLE
        binding.rvFavoriteItems.visibility = View.GONE
        showToast("Silakan login untuk mengakses fitur favorit")
    }
    
    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
    
    override fun onFavoriteUpdated() {
        // Update UI when favorites change
        updateFavoriteDisplay()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        FavoriteManager.removeListener(this)
        _binding = null
    }
}
