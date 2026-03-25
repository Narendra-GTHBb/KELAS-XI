# Design Document

## Overview

The Gym Equipment & Fitness Supplement Android application follows modern Android architecture patterns with MVVM (Model-View-ViewModel), Repository pattern, and offline-first design. The app uses Jetpack Compose for UI, Room for local database, Retrofit for networking, and follows Material Design 3 guidelines for a native Android experience.

## Architecture

### Application Structure
```
app/src/main/java/com/gymecommerce/
├── data/
│   ├── local/
│   │   ├── database/
│   │   ├── dao/
│   │   └── entities/
│   ├── remote/
│   │   ├── api/
│   │   └── dto/
│   └── repository/
├── domain/
│   ├── model/
│   ├── repository/
│   └── usecase/
├── presentation/
│   ├── ui/
│   │   ├── auth/
│   │   ├── home/
│   │   ├── products/
│   │   ├── cart/
│   │   ├── checkout/
│   │   ├── orders/
│   │   └── admin/
│   ├── viewmodel/
│   ├── navigation/
│   └── components/
├── di/
└── utils/

app/src/main/res/
├── drawable/
├── values/
│   ├── colors.xml
│   ├── strings.xml
│   └── themes.xml
└── xml/
```

### Technology Stack
- **UI Framework:** Jetpack Compose with Material Design 3
- **Architecture:** MVVM with Repository Pattern
- **Database:** Room (SQLite)
- **Networking:** Retrofit + OkHttp
- **Image Loading:** Coil
- **Dependency Injection:** Hilt
- **Navigation:** Jetpack Navigation Compose
- **Async Operations:** Kotlin Coroutines + Flow
- **Local Storage:** SharedPreferences + DataStore

## Components and Interfaces

### Data Layer

#### Local Database (Room)
```kotlin
@Database(
    entities = [UserEntity::class, CategoryEntity::class, ProductEntity::class, 
                OrderEntity::class, OrderItemEntity::class],
    version = 1
)
abstract class GymEcommerceDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun categoryDao(): CategoryDao
    abstract fun productDao(): ProductDao
    abstract fun orderDao(): OrderDao
}
```

#### Repository Pattern
```kotlin
interface ProductRepository {
    suspend fun getProducts(): Flow<List<Product>>
    suspend fun getProductById(id: Int): Product?
    suspend fun getProductsByCategory(categoryId: Int): Flow<List<Product>>
    suspend fun syncProducts(): Result<Unit>
    suspend fun searchProducts(query: String): Flow<List<Product>>
}

class ProductRepositoryImpl(
    private val localDataSource: ProductDao,
    private val remoteDataSource: ProductApiService,
    private val networkManager: NetworkManager
) : ProductRepository
```

### Domain Layer

#### Use Cases
```kotlin
class GetProductsUseCase(private val repository: ProductRepository) {
    suspend operator fun invoke(): Flow<List<Product>> = repository.getProducts()
}

class AddToCartUseCase(private val cartRepository: CartRepository) {
    suspend operator fun invoke(productId: Int, quantity: Int): Result<Unit>
}

class ProcessCheckoutUseCase(
    private val orderRepository: OrderRepository,
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(shippingAddress: String): Result<Order>
}
```

### Presentation Layer

#### ViewModels
```kotlin
@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ProductListUiState())
    val uiState: StateFlow<ProductListUiState> = _uiState.asStateFlow()
    
    fun loadProducts(categoryId: Int? = null) {
        viewModelScope.launch {
            // Implementation
        }
    }
}

data class ProductListUiState(
    val products: List<Product> = emptyList(),
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedCategory: Int? = null
)
```

## Data Models

### Domain Models
```kotlin
data class User(
    val id: Int,
    val name: String,
    val email: String,
    val role: UserRole
)

data class Product(
    val id: Int,
    val name: String,
    val price: Double,
    val stock: Int,
    val description: String,
    val imageUrl: String,
    val categoryId: Int,
    val category: Category?
)

data class CartItem(
    val productId: Int,
    val product: Product,
    val quantity: Int
)

data class Order(
    val id: Int,
    val userId: Int,
    val totalPrice: Double,
    val status: OrderStatus,
    val shippingAddress: String,
    val createdAt: Long,
    val items: List<OrderItem>
)

enum class OrderStatus { PENDING, PAID, COMPLETED }
enum class UserRole { USER, ADMIN }
```

### Room Entities
```kotlin
@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val price: Double,
    val stock: Int,
    val description: String,
    val imageUrl: String,
    val categoryId: Int,
    val lastSyncTime: Long = System.currentTimeMillis()
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val id: Int,
    val userId: Int,
    val totalPrice: Double,
    val status: String,
    val shippingAddress: String,
    val createdAt: Long,
    val isSynced: Boolean = false
)
```

## User Interface Design

### Design System
```kotlin
// colors.xml
object GymEcommerceColors {
    val Primary = Color(0xFF2563EB)      // Sporty Blue
    val Secondary = Color(0xFF059669)     // Fitness Green
    val Background = Color(0xFFFFFBFE)    // Material You Background
    val Surface = Color(0xFFFFFBFE)       // Material You Surface
    val OnPrimary = Color(0xFFFFFFFF)
    val OnSecondary = Color(0xFFFFFFFF)
    val Error = Color(0xFFBA1A1A)
}

// Theme.kt
@Composable
fun GymEcommerceTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = GymEcommerceColors.Primary,
            secondary = GymEcommerceColors.Secondary,
            background = GymEcommerceColors.Background,
            surface = GymEcommerceColors.Surface
        ),
        typography = Typography,
        content = content
    )
}
```

### Screen Layouts

#### Home Screen
```kotlin
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onProductClick: (Int) -> Unit,
    onCategoryClick: (Int) -> Unit
) {
    LazyColumn {
        item {
            // Featured Products Carousel
            LazyRow {
                items(uiState.featuredProducts) { product ->
                    ProductCard(
                        product = product,
                        onClick = { onProductClick(product.id) }
                    )
                }
            }
        }
        
        item {
            // Categories Grid
            LazyVerticalGrid(columns = GridCells.Fixed(2)) {
                items(uiState.categories) { category ->
                    CategoryCard(
                        category = category,
                        onClick = { onCategoryClick(category.id) }
                    )
                }
            }
        }
    }
}
```

#### Product Detail Screen
```kotlin
@Composable
fun ProductDetailScreen(
    product: Product,
    onAddToCart: (Int) -> Unit,
    onBackClick: () -> Unit
) {
    Column {
        // Image Gallery
        AsyncImage(
            model = product.imageUrl,
            contentDescription = product.name,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )
        
        // Product Info
        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = "$${product.price}",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Stock: ${product.stock}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        
        // Add to Cart Section
        var quantity by remember { mutableStateOf(1) }
        Row {
            QuantitySelector(
                quantity = quantity,
                onQuantityChange = { quantity = it }
            )
            Button(
                onClick = { onAddToCart(quantity) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add to Cart")
            }
        }
    }
}
```

### Navigation Structure
```kotlin
@Composable
fun GymEcommerceNavigation() {
    val navController = rememberNavController()
    
    Scaffold(
        bottomBar = {
            BottomNavigation {
                BottomNavigationItem(
                    selected = currentRoute == "home",
                    onClick = { navController.navigate("home") },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") }
                )
                BottomNavigationItem(
                    selected = currentRoute == "products",
                    onClick = { navController.navigate("products") },
                    icon = { Icon(Icons.Default.ShoppingBag, contentDescription = "Products") },
                    label = { Text("Products") }
                )
                BottomNavigationItem(
                    selected = currentRoute == "cart",
                    onClick = { navController.navigate("cart") },
                    icon = { 
                        BadgedBox(badge = { Badge { Text("$cartItemCount") } }) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                        }
                    },
                    label = { Text("Cart") }
                )
                BottomNavigationItem(
                    selected = currentRoute == "orders",
                    onClick = { navController.navigate("orders") },
                    icon = { Icon(Icons.Default.Receipt, contentDescription = "Orders") },
                    label = { Text("Orders") }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("home") { HomeScreen() }
            composable("products") { ProductListScreen() }
            composable("cart") { CartScreen() }
            composable("orders") { OrderHistoryScreen() }
            composable("product/{productId}") { ProductDetailScreen() }
        }
    }
}
```

## Offline-First Architecture

### Data Synchronization Strategy
```kotlin
class SyncManager @Inject constructor(
    private val productRepository: ProductRepository,
    private val orderRepository: OrderRepository,
    private val networkManager: NetworkManager
) {
    suspend fun syncAll() {
        if (networkManager.isConnected()) {
            try {
                // Sync products from server
                productRepository.syncProducts()
                
                // Upload pending orders
                orderRepository.syncPendingOrders()
                
                // Download user orders
                orderRepository.syncUserOrders()
            } catch (e: Exception) {
                // Handle sync errors
            }
        }
    }
}
```

### Offline Capabilities
- **Product Browsing:** Cache all product data locally for offline viewing
- **Cart Management:** Store cart items in local database
- **Order Queue:** Queue orders when offline, sync when connected
- **Image Caching:** Cache product images for offline viewing
- **Search:** Local search through cached product data

## Error Handling

### Network Error Handling
```kotlin
sealed class NetworkResult<T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error<T>(val message: String) : NetworkResult<T>()
    data class Loading<T>(val isLoading: Boolean) : NetworkResult<T>()
}

@Composable
fun ErrorHandling(
    error: String?,
    onRetry: () -> Unit
) {
    error?.let {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Error: $it",
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                Button(onClick = onRetry) {
                    Text("Retry")
                }
            }
        }
    }
}
```

## Testing Strategy

### Unit Testing
- **ViewModels:** Test state management and business logic
- **Use Cases:** Validate business rules and data transformations
- **Repositories:** Mock network and database interactions
- **Utilities:** Test helper functions and extensions

### UI Testing
- **Compose Tests:** Test UI components and interactions
- **Navigation Tests:** Verify screen transitions and deep links
- **Integration Tests:** Test complete user flows
- **Accessibility Tests:** Verify screen reader support and touch targets

### Database Testing
- **Room Tests:** Test DAO operations and migrations
- **Repository Tests:** Test data synchronization logic
- **Migration Tests:** Ensure database schema updates work correctly

## Security Considerations

### Authentication & Data Protection
- **Secure Storage:** Use Android Keystore for sensitive data
- **Network Security:** Implement certificate pinning for API calls
- **Input Validation:** Validate all user inputs on client and server
- **Session Management:** Secure token storage and automatic refresh

### Privacy & Permissions
- **Minimal Permissions:** Request only necessary permissions
- **Data Encryption:** Encrypt sensitive data in local database
- **User Consent:** Clear privacy policy and data usage disclosure
- **Secure Communication:** HTTPS only for all network requests