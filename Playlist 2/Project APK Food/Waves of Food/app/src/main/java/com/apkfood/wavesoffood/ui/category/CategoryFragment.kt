package com.apkfood.wavesoffood.ui.category

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.apkfood.wavesoffood.databinding.FragmentCategoryBinding
import com.apkfood.wavesoffood.data.model.Category
import com.apkfood.wavesoffood.data.model.Food
import com.apkfood.wavesoffood.data.model.NutritionInfo
import com.apkfood.wavesoffood.adapter.FoodVerticalAdapter
import com.apkfood.wavesoffood.ui.detail.FoodDetailFragment
import com.apkfood.wavesoffood.data.FoodDataSource
import com.apkfood.wavesoffood.manager.CartManager
import com.apkfood.wavesoffood.utils.FavoriteManager
import com.apkfood.wavesoffood.utils.GuestAccessHelper

/**
 * Category Fragment
 * Fragment untuk menampilkan makanan berdasarkan kategori tertentu
 */
class CategoryFragment : Fragment() {
    
    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var categoryFoodsAdapter: FoodVerticalAdapter
    private val categoryFoods = mutableListOf<Food>()
    private val allCategoryFoods = mutableListOf<Food>()
    
    private var categoryName: String = ""
    
    companion object {
        private const val ARG_CATEGORY_NAME = "category_name"
        
        fun newInstance(categoryName: String): CategoryFragment {
            val fragment = CategoryFragment()
            val args = Bundle()
            args.putString(ARG_CATEGORY_NAME, categoryName)
            fragment.arguments = args
            return fragment
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            categoryName = it.getString(ARG_CATEGORY_NAME) ?: ""
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupUI()
        setupFoods()
        setupSearch()
        loadCategoryFoods()
    }
    
    private fun setupUI() {
        binding.tvCategoryTitle.text = categoryName
        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }
    
    private fun setupFoods() {
        categoryFoodsAdapter = FoodVerticalAdapter(
            categoryFoods,
            onAddToCart = { food ->
                if (GuestAccessHelper.checkCartAccess(requireContext())) {
                    CartManager.getInstance().addToCart(food)
                    Toast.makeText(context, "Added to cart: ${food.name}", Toast.LENGTH_SHORT).show()
                }
            },
            onToggleFavorite = { food ->
                val isFavorite = com.apkfood.wavesoffood.utils.FavoriteManager.toggleFavorite(food)
                Toast.makeText(context, 
                    if (isFavorite) "Added to favorites: ${food.name}" 
                    else "Removed from favorites: ${food.name}", 
                    Toast.LENGTH_SHORT).show()
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
        
        binding.rvCategoryFoods.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = categoryFoodsAdapter
        }
    }
    
    private fun setupSearch() {
        binding.etCategorySearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterFoods(s.toString())
            }
            
            override fun afterTextChanged(s: Editable?) {}
        })
    }
    
    private fun filterFoods(query: String) {
        if (query.isEmpty()) {
            updateFoodsList(allCategoryFoods)
            return
        }
        
        val lowercaseQuery = query.lowercase()
        val filteredFoods = allCategoryFoods.filter {
            it.name.lowercase().contains(lowercaseQuery) || 
            it.description.lowercase().contains(lowercaseQuery)
        }
        
        updateFoodsList(filteredFoods)
    }
    
    private fun updateFoodsList(newFoods: List<Food>) {
        categoryFoods.clear()
        categoryFoods.addAll(newFoods)
        categoryFoodsAdapter.notifyDataSetChanged()
        
        binding.tvFoodCount.text = "${newFoods.size} items found"
    }
    
    private fun loadCategoryFoods() {
        val allFoods = FoodDataSource.getAllFoods()
        val filteredFoods = allFoods.filter { 
            it.categoryName.equals(categoryName, ignoreCase = true) 
        }
        
        allCategoryFoods.clear()
        allCategoryFoods.addAll(filteredFoods)
        updateFoodsList(filteredFoods)
    }
    
    private fun getAllFoods(): List<Food> {
        return listOf(
            // Pizza
            Food(
                id = "1",
                name = "Margherita Pizza",
                description = "Classic Italian pizza with fresh tomatoes, mozzarella cheese, and basil",
                price = 85000.0,
                categoryName = "Pizza",
                imageUrl = "https://images.unsplash.com/photo-1574071318508-1cdbab80d002?w=800&q=80",
                rating = 4.5,
                isPopular = true,
                deliveryTime = "25 min"
            ),
            Food(
                id = "2",
                name = "Pepperoni Pizza",
                description = "Delicious pizza topped with pepperoni slices and melted cheese",
                price = 95000.0,
                categoryName = "Pizza",
                imageUrl = "https://images.unsplash.com/photo-1628840042765-356cda07504e?w=800&q=80",
                rating = 4.7,
                isPopular = true,
                deliveryTime = "25 min"
            ),
            Food(
                id = "3",
                name = "BBQ Chicken Pizza",
                description = "BBQ sauce, grilled chicken, red onions, and cheese",
                price = 105000.0,
                categoryName = "Pizza",
                imageUrl = "https://images.unsplash.com/photo-1513104890138-7c749659a591?w=800&q=80",
                rating = 4.6,
                isPopular = false,
                deliveryTime = "30 min"
            ),
            Food(
                id = "4",
                name = "Meat Lovers Pizza",
                description = "Loaded with pepperoni, sausage, ham, and bacon",
                price = 115000.0,
                categoryName = "Pizza",
                imageUrl = "https://images.unsplash.com/photo-1565299624946-b28f40a0ca4b?w=800&q=80",
                rating = 4.8,
                isPopular = true,
                deliveryTime = "30 min"
            ),

            // Burgers
            Food(
                id = "5",
                name = "Classic Beef Burger",
                description = "Juicy beef patty with lettuce, tomato, onion, and special sauce",
                price = 75000.0,
                categoryName = "Burger",
                imageUrl = "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=800&q=80",
                rating = 4.4,
                isPopular = true,
                deliveryTime = "15 min"
            ),
            Food(
                id = "6",
                name = "Cheese Burger",
                description = "Double cheese with beef patty, pickles, and ketchup",
                price = 85000.0,
                categoryName = "Burger",
                imageUrl = "https://images.unsplash.com/photo-1586190848861-99aa4a171e90?w=800&q=80",
                rating = 4.3,
                isPopular = false,
                deliveryTime = "15 min"
            ),
            Food(
                id = "7",
                name = "Chicken Burger",
                description = "Grilled chicken breast with mayo, lettuce, and tomato",
                price = 70000.0,
                categoryName = "Burger",
                imageUrl = "https://images.unsplash.com/photo-1615297363857-adf6e242c089?w=800&q=80",
                rating = 4.2,
                isPopular = false,
                deliveryTime = "18 min"
            ),
            Food(
                id = "8",
                name = "Double Beef Burger",
                description = "Two beef patties with cheese, lettuce, and special sauce",
                price = 95000.0,
                categoryName = "Burger",
                imageUrl = "https://images.unsplash.com/photo-1553979459-d2229ba7433b?w=800&q=80",
                rating = 4.6,
                isPopular = true,
                deliveryTime = "20 min"
            ),

            // Chicken
            Food(
                id = "9",
                name = "Crispy Fried Chicken",
                description = "Golden crispy fried chicken with secret spices",
                price = 65000.0,
                categoryName = "Chicken",
                imageUrl = "https://images.unsplash.com/photo-1626645738196-c2a7c87a8f58?w=800&q=80",
                rating = 4.6,
                isPopular = true,
                deliveryTime = "20 min"
            ),
            Food(
                id = "10",
                name = "Grilled Chicken",
                description = "Healthy grilled chicken with herbs and spices",
                price = 70000.0,
                categoryName = "Chicken",
                imageUrl = "https://images.unsplash.com/photo-1532550907401-a500c9a57435?w=800&q=80",
                rating = 4.4,
                isPopular = false,
                deliveryTime = "25 min"
            ),
            Food(
                id = "11",
                name = "Buffalo Wings",
                description = "Spicy buffalo chicken wings with ranch dipping sauce",
                price = 80000.0,
                categoryName = "Chicken",
                imageUrl = "https://images.unsplash.com/photo-1588510787208-b8129d2a8f12?w=800&q=80",
                rating = 4.5,
                isPopular = true,
                deliveryTime = "22 min"
            ),
            Food(
                id = "12",
                name = "Chicken Tenders",
                description = "Crispy chicken tenders with honey mustard sauce",
                price = 60000.0,
                categoryName = "Chicken",
                imageUrl = "https://images.unsplash.com/photo-1562967914-608f82629710?w=800&q=80",
                rating = 4.3,
                isPopular = false,
                deliveryTime = "18 min"
            ),

            // Rice
            Food(
                id = "13",
                name = "Nasi Goreng Special",
                description = "Indonesian fried rice with chicken, egg, and vegetables",
                price = 45000.0,
                categoryName = "Rice",
                imageUrl = "https://images.unsplash.com/photo-1603133872878-684f208fb84b?w=800&q=80",
                rating = 4.3,
                isPopular = true,
                deliveryTime = "15 min"
            ),
            Food(
                id = "14",
                name = "Chicken Fried Rice",
                description = "Fragrant fried rice with tender chicken pieces",
                price = 50000.0,
                categoryName = "Rice",
                imageUrl = "https://images.unsplash.com/photo-1645112411341-6c4fd023882a?w=800&q=80",
                rating = 4.2,
                isPopular = false,
                deliveryTime = "15 min"
            ),
            Food(
                id = "15",
                name = "Hainanese Chicken Rice",
                description = "Tender poached chicken served with fragrant rice",
                price = 55000.0,
                categoryName = "Rice",
                imageUrl = "https://images.unsplash.com/photo-1585937421612-70a008356c36?w=800&q=80",
                rating = 4.4,
                isPopular = false,
                deliveryTime = "30 min"
            ),
            Food(
                id = "16",
                name = "Beef Fried Rice",
                description = "Savory fried rice with tender beef and mixed vegetables",
                price = 60000.0,
                categoryName = "Rice",
                imageUrl = "https://images.unsplash.com/photo-1512058564366-18510be2db19?w=800&q=80",
                rating = 4.5,
                isPopular = true,
                deliveryTime = "18 min"
            ),

            // Desserts
            Food(
                id = "17",
                name = "Chocolate Fudge Cake",
                description = "Rich and decadent chocolate cake with fudge frosting",
                price = 35000.0,
                categoryName = "Dessert",
                imageUrl = "https://images.unsplash.com/photo-1621303837174-89787a7d4729?w=800&q=80",
                rating = 4.7,
                isPopular = true,
                deliveryTime = "5 min"
            ),
            Food(
                id = "18",
                name = "Vanilla Ice Cream",
                description = "Premium vanilla ice cream with fresh cream",
                price = 25000.0,
                categoryName = "Dessert",
                imageUrl = "https://images.unsplash.com/photo-1563805042-7684c019e1cb?w=800&q=80",
                rating = 4.5,
                isPopular = false,
                deliveryTime = "2 min"
            ),
            Food(
                id = "19",
                name = "Strawberry Cheesecake",
                description = "Creamy cheesecake topped with fresh strawberries",
                price = 40000.0,
                categoryName = "Dessert",
                imageUrl = "https://images.unsplash.com/photo-1533134242443-d4fd215305ad?w=800&q=80",
                rating = 4.6,
                isPopular = true,
                deliveryTime = "5 min"
            ),
            Food(
                id = "20",
                name = "Chocolate Brownie",
                description = "Warm chocolate brownie with vanilla ice cream",
                price = 30000.0,
                categoryName = "Dessert",
                imageUrl = "https://images.unsplash.com/photo-1606313564200-e75d5e30476c?w=800&q=80",
                rating = 4.4,
                isPopular = false,
                deliveryTime = "8 min"
            ),

            // Drinks
            Food(
                id = "21",
                name = "Fresh Orange Juice",
                description = "Freshly squeezed orange juice, vitamin C rich",
                price = 20000.0,
                categoryName = "Drinks",
                imageUrl = "https://images.unsplash.com/photo-1600271886742-f049cd451bba?w=800&q=80",
                rating = 4.3,
                isPopular = false,
                deliveryTime = "3 min"
            ),
            Food(
                id = "22",
                name = "Iced Coffee",
                description = "Rich coffee served over ice with milk",
                price = 25000.0,
                categoryName = "Drinks",
                imageUrl = "https://images.unsplash.com/photo-1610632380989-680fe40816c6?w=800&q=80",
                rating = 4.4,
                isPopular = true,
                deliveryTime = "5 min"
            ),
            Food(
                id = "23",
                name = "Bubble Milk Tea",
                description = "Taiwanese milk tea with chewy tapioca pearls",
                price = 30000.0,
                categoryName = "Drinks",
                imageUrl = "https://images.unsplash.com/photo-1558857563-c0c3a20e084b?w=800&q=80",
                rating = 4.5,
                isPopular = true,
                deliveryTime = "5 min"
            ),
            Food(
                id = "24",
                name = "Smoothie Bowl",
                description = "Healthy smoothie bowl with fresh fruits and granola",
                price = 35000.0,
                categoryName = "Drinks",
                imageUrl = "https://images.unsplash.com/photo-1511690743698-d9d85f2fbf38?w=800&q=80",
                rating = 4.2,
                isPopular = false,
                deliveryTime = "8 min"
            ),

            // Noodles
            Food(
                id = "25",
                name = "Chicken Ramen",
                description = "Japanese style ramen with tender chicken and vegetables",
                price = 60000.0,
                categoryName = "Noodles",
                imageUrl = "https://images.unsplash.com/photo-1569718212165-3a8278d5f624?w=800&q=80",
                rating = 4.6,
                isPopular = true,
                deliveryTime = "20 min"
            ),
            Food(
                id = "26",
                name = "Spaghetti Carbonara",
                description = "Creamy pasta with bacon, eggs, and parmesan cheese",
                price = 75000.0,
                categoryName = "Noodles",
                imageUrl = "https://images.unsplash.com/photo-1563379926898-05f4575a45d8?w=800&q=80",
                rating = 4.4,
                isPopular = false,
                deliveryTime = "18 min"
            ),
            Food(
                id = "27",
                name = "Pad Thai",
                description = "Thai stir-fried noodles with shrimp, tofu, and peanuts",
                price = 65000.0,
                categoryName = "Noodles",
                imageUrl = "https://images.unsplash.com/photo-1637806931079-a769ceed2159?w=800&q=80",
                rating = 4.3,
                isPopular = false,
                deliveryTime = "15 min"
            ),
            Food(
                id = "28",
                name = "Beef Noodle Soup",
                description = "Rich beef broth with tender noodles and vegetables",
                price = 70000.0,
                categoryName = "Noodles",
                imageUrl = "https://images.unsplash.com/photo-1555126634-323283e090fa?w=800&q=80",
                rating = 4.5,
                isPopular = true,
                deliveryTime = "22 min"
            )
        )
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
