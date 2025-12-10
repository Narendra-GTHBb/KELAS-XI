package com.apkfood.wavesoffood.data

import com.apkfood.wavesoffood.data.model.Food

/**
 * Data source untuk makanan dummy yang konsisten di seluruh aplikasi
 */
object FoodDataSource {
    
    fun getAllFoods(): List<Food> {
        return listOf(
            // Pizza Category
            Food(
                id = "1",
                name = "Pizza Margherita",
                description = "Pizza klasik dengan tomat, mozzarella, dan basil segar",
                price = 75000.0,
                imageUrl = "https://images.unsplash.com/photo-1565299624946-b28f40a0ca4b?w=800&q=80",
                categoryName = "Pizza",
                rating = 4.5
            ),
            Food(
                id = "2", 
                name = "Pizza Pepperoni",
                description = "Pizza dengan pepperoni dan keju mozzarella yang melimpah",
                price = 85000.0,
                imageUrl = "https://images.unsplash.com/photo-1628840042765-356cda07504e?w=800&q=80",
                categoryName = "Pizza",
                rating = 4.7
            ),
            Food(
                id = "3",
                name = "Pizza Vegetarian",
                description = "Pizza dengan berbagai sayuran segar dan keju",
                price = 70000.0,
                imageUrl = "https://images.unsplash.com/photo-1574071318508-1cdbab80d002?w=800&q=80",
                categoryName = "Pizza",
                rating = 4.4
            ),
            
            // Burger Category
            Food(
                id = "4",
                name = "Burger Beef Classic", 
                description = "Burger daging sapi dengan sayuran segar dan saus spesial",
                price = 55000.0,
                imageUrl = "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=800&q=80",
                categoryName = "Burger",
                rating = 4.3
            ),
            Food(
                id = "5",
                name = "Cheese Burger",
                description = "Burger dengan double cheese dan daging juicy",
                price = 60000.0,
                imageUrl = "https://images.unsplash.com/photo-1551615593-ef5fe247e8f7?w=800&q=80",
                categoryName = "Burger", 
                rating = 4.6
            ),
            Food(
                id = "6",
                name = "Chicken Burger",
                description = "Burger ayam crispy dengan mayonnaise dan lettuce",
                price = 50000.0,
                imageUrl = "https://images.unsplash.com/photo-1606755962773-d324e2311c82?w=800&q=80",
                categoryName = "Burger",
                rating = 4.2
            ),
            
            // Pasta Category
            Food(
                id = "7",
                name = "Spaghetti Carbonara",
                description = "Pasta dengan saus creamy, bacon, dan parmesan cheese",
                price = 65000.0,
                imageUrl = "https://images.unsplash.com/photo-1621996346565-e3dbc353d30e?w=800&q=80",
                categoryName = "Pasta",
                rating = 4.8
            ),
            Food(
                id = "8",
                name = "Fettuccine Alfredo",
                description = "Pasta dengan saus putih creamy yang lezat",
                price = 62000.0,
                imageUrl = "https://images.unsplash.com/photo-1579631542720-3a87824fff86?w=800&q=80",
                categoryName = "Pasta",
                rating = 4.5
            ),
            
            // Salad Category
            Food(
                id = "9",
                name = "Caesar Salad",
                description = "Salad segar dengan crouton, parmesan, dan dressing caesar",
                price = 45000.0,
                imageUrl = "https://images.unsplash.com/photo-1546793665-c74683f339c1?w=800&q=80",
                categoryName = "Salad",
                rating = 4.1
            ),
            Food(
                id = "10",
                name = "Greek Salad",
                description = "Salad mediterania dengan olive, feta cheese, dan sayuran",
                price = 48000.0,
                imageUrl = "https://images.unsplash.com/photo-1540420773420-3366772f4999?w=800&q=80",
                categoryName = "Salad",
                rating = 4.3
            ),
            
            // Dessert Category
            Food(
                id = "11",
                name = "Chocolate Cake",
                description = "Kue coklat lembut dengan frosting coklat yang manis",
                price = 35000.0,
                imageUrl = "https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=800&q=80",
                categoryName = "Dessert",
                rating = 4.9
            ),
            Food(
                id = "12",
                name = "Tiramisu",
                description = "Dessert Italia klasik dengan kopi dan mascarpone",
                price = 40000.0,
                imageUrl = "https://images.unsplash.com/photo-1571877227200-a0d98ea607e9?w=800&q=80",
                categoryName = "Dessert",
                rating = 4.7
            ),
            
            // Chicken Category
            Food(
                id = "13",
                name = "Ayam Bakar",
                description = "Ayam bakar dengan bumbu rempah khas Indonesia",
                price = 45000.0,
                imageUrl = "https://images.unsplash.com/photo-1606755962773-d324e2311c82?w=800&q=80",
                categoryName = "Chicken",
                rating = 4.6
            ),
            Food(
                id = "14",
                name = "Ayam Geprek",
                description = "Ayam crispy geprek dengan sambal pedas",
                price = 35000.0,
                imageUrl = "https://images.unsplash.com/photo-1546793665-c74683f339c1?w=800&q=80",
                categoryName = "Chicken",
                rating = 4.7
            ),
            Food(
                id = "15",
                name = "Chicken Wings",
                description = "Sayap ayam goreng dengan saus BBQ",
                price = 40000.0,
                imageUrl = "https://images.unsplash.com/photo-1527477396000-e27163b481c2?w=800&q=80",
                categoryName = "Chicken",
                rating = 4.5
            ),
            
            // Rice Category
            Food(
                id = "16",
                name = "Nasi Gudeg",
                description = "Nasi dengan gudeg khas Yogyakarta dan lauk pelengkap",
                price = 25000.0,
                imageUrl = "https://images.unsplash.com/photo-1574071318508-1cdbab80d002?w=800&q=80",
                categoryName = "Rice",
                rating = 4.8
            ),
            Food(
                id = "17",
                name = "Nasi Liwet",
                description = "Nasi liwet Solo dengan ayam suwir dan lauk tradisional",
                price = 30000.0,
                imageUrl = "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=800&q=80",
                categoryName = "Rice",
                rating = 4.6
            ),
            Food(
                id = "18",
                name = "Nasi Padang",
                description = "Nasi putih dengan rendang dan aneka lauk Padang",
                price = 35000.0,
                imageUrl = "https://images.unsplash.com/photo-1551615593-ef5fe247e8f7?w=800&q=80",
                categoryName = "Rice",
                rating = 4.9
            ),
            
            // Noodles Category  
            Food(
                id = "19",
                name = "Mie Ayam",
                description = "Mie kuning dengan ayam suwir dan bakso",
                price = 20000.0,
                imageUrl = "https://images.unsplash.com/photo-1540420773420-3366772f4999?w=800&q=80",
                categoryName = "Noodles",
                rating = 4.4
            ),
            Food(
                id = "20",
                name = "Mie Goreng",
                description = "Mie goreng spesial dengan telur dan sayuran",
                price = 22000.0,
                imageUrl = "https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=800&q=80",
                categoryName = "Noodles",
                rating = 4.5
            ),
            Food(
                id = "21",
                name = "Ramen",
                description = "Ramen Jepang dengan kuah kaldu yang gurih",
                price = 45000.0,
                imageUrl = "https://images.unsplash.com/photo-1569718212165-3a8278d5f624?w=800&q=80",
                categoryName = "Noodles",
                rating = 4.7
            ),
            
            // Drinks Category
            Food(
                id = "22",
                name = "Fresh Orange Juice",
                description = "Jus jeruk segar tanpa pemanis tambahan",
                price = 25000.0,
                imageUrl = "https://images.unsplash.com/photo-1613478223719-2ab802602423?w=800&q=80",
                categoryName = "Drinks",
                rating = 4.2
            ),
            Food(
                id = "23",
                name = "Iced Coffee",
                description = "Kopi dingin dengan es dan creamer",
                price = 20000.0,
                imageUrl = "https://images.unsplash.com/photo-1461023058943-07fcbe16d735?w=800&q=80",
                categoryName = "Drinks",
                rating = 4.4
            )
        )
    }
    
    fun getFoodsByCategory(category: String): List<Food> {
        return getAllFoods().filter { it.categoryName == category }
    }
    
    fun getFoodById(id: String): Food? {
        return getAllFoods().find { it.id == id }
    }
    
    fun getPopularFoods(): List<Food> {
        return getAllFoods().filter { it.rating >= 4.5 }
    }
}
