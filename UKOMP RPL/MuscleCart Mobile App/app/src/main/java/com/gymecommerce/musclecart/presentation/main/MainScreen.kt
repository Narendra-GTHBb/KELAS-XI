package com.gymecommerce.musclecart.presentation.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.gymecommerce.musclecart.presentation.cart.CartScreen
import com.gymecommerce.musclecart.presentation.cart.CartViewModel
import com.gymecommerce.musclecart.presentation.checkout.CheckoutScreen
import com.gymecommerce.musclecart.presentation.home.HomeScreen
import com.gymecommerce.musclecart.domain.model.Product
import com.gymecommerce.musclecart.navigation.NavRoutes
import com.gymecommerce.musclecart.presentation.order.OrderConfirmationScreen
import com.gymecommerce.musclecart.presentation.order.OrderDetailScreen
import com.gymecommerce.musclecart.presentation.order.OrderHistoryScreen
import com.gymecommerce.musclecart.presentation.product.ProductDetailScreen
import com.gymecommerce.musclecart.presentation.product.ProductDetailBottomSheet
import com.gymecommerce.musclecart.presentation.notification.NotificationScreen
import com.gymecommerce.musclecart.presentation.notification.NotificationViewModel
import com.gymecommerce.musclecart.presentation.points.PointsHistoryScreen
import com.gymecommerce.musclecart.presentation.profile.ChangePasswordScreen
import com.gymecommerce.musclecart.presentation.profile.EditAddressScreen
import com.gymecommerce.musclecart.presentation.profile.EditProfileScreen
import com.gymecommerce.musclecart.presentation.track.TrackPackagesScreen
import com.gymecommerce.musclecart.presentation.review.ReviewScreen
import com.gymecommerce.musclecart.presentation.profile.ProfileScreen
import com.gymecommerce.musclecart.presentation.search.SearchScreen
import com.gymecommerce.musclecart.presentation.shop.ShopScreen
import com.gymecommerce.musclecart.presentation.wishlist.WishlistScreen

@Composable
fun MainScreen(
    onLogout: () -> Unit,
    cartViewModel: CartViewModel,
    viewModel: MainViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val cartCount by viewModel.cartItemCount.collectAsState()

    val notificationViewModel: NotificationViewModel = hiltViewModel()
    val notificationUiState by notificationViewModel.uiState.collectAsState()

    val showBottomBar = currentDestination?.route in listOf(
        NavRoutes.HOME,
        NavRoutes.SHOP_BASE,
        NavRoutes.SHOP,
        NavRoutes.PROFILE
    ) || currentDestination?.route?.startsWith("shop") == true

    val bottomNavItems = listOf(
        MainTabItem(NavRoutes.HOME, "Home", Icons.Outlined.Home, Icons.Filled.Home),
        MainTabItem(NavRoutes.SHOP_BASE, "Shop", Icons.Outlined.Storefront, Icons.Filled.Storefront),
        MainTabItem(NavRoutes.PROFILE, "Profile", Icons.Outlined.Person, Icons.Filled.Person)
    )

    val currentRoute = currentDestination?.route ?: NavRoutes.HOME
    val selectedIndex = when {
        currentRoute == NavRoutes.HOME -> 0
        currentRoute.startsWith("shop") -> 1
        currentRoute == NavRoutes.PROFILE -> 2
        else -> bottomNavItems.indexOfFirst { it.route == currentRoute }.takeIf { it >= 0 } ?: 0
    }

    Scaffold(
        bottomBar = {
            AnimatedVisibility(visible = showBottomBar) {
                CustomBottomNavigationBar(
                    items = bottomNavItems,
                    selectedIndex = selectedIndex,
                    onItemClick = { index ->
                        val route = bottomNavItems[index].route
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = NavRoutes.HOME,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(NavRoutes.HOME) {
                HomeScreen(
                    onProductClick = { product ->
                        // This callback is no longer used as bottom sheet is handled internally
                        // Keeping for backward compatibility
                    },
                    onCategoryClick = { categoryName ->
                        // Map category name/slug to route and navigate to shop with category
                        navController.navigate(NavRoutes.shop()) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = false
                            }
                            launchSingleTop = true
                        }
                    },
                    onNotificationClick = {
                        navController.navigate(NavRoutes.NOTIFICATIONS)
                    },
                    onCartClick = {
                        navController.navigate(NavRoutes.CART)
                    },
                    onShopClick = {
                        navController.navigate(NavRoutes.SHOP_BASE) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onSearchClick = {
                        navController.navigate(NavRoutes.SEARCH)
                    },
                    cartItemCount = cartCount,
                    notificationCount = notificationUiState.unreadCount
                )
            }

            composable(NavRoutes.SEARCH) {
                SearchScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onProductClick = { product ->
                        navController.navigate(NavRoutes.productDetail(product.id))
                    },
                    cartItemCount = cartCount
                )
            }

            composable(NavRoutes.NOTIFICATIONS) {
                NotificationScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onOrderClick = { orderId ->
                        navController.navigate(NavRoutes.orderDetail(orderId))
                    }
                )
            }

            composable(NavRoutes.POINTS_HISTORY) {
                PointsHistoryScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(NavRoutes.CHANGE_PASSWORD) {
                ChangePasswordScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(NavRoutes.EDIT_PROFILE) {
                EditProfileScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(NavRoutes.EDIT_ADDRESS) {
                EditAddressScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(NavRoutes.TRACK_PACKAGES) {
                TrackPackagesScreen(
                    onOrderClick = { orderId ->
                        navController.navigate(NavRoutes.orderDetail(orderId))
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = NavRoutes.PRODUCT_DETAIL,
                arguments = listOf(navArgument("productId") { type = NavType.IntType }),
                deepLinks = listOf(
                    navDeepLink { uriPattern = "musclecart://product/{productId}" }
                )
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getInt("productId") ?: return@composable
                ProductDetailScreen(
                    productId = productId,
                    onNavigateBack = { navController.popBackStack() },
                    onAddToCartSuccess = { /* optional: show snackbar or refresh cart count */ },
                    onBuyNow = { navController.navigate(NavRoutes.CART) },
                    onHomeClick = {
                        navController.navigate(NavRoutes.HOME) {
                            popUpTo(NavRoutes.HOME) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(NavRoutes.CART) {
                CartScreen(
                    onCheckoutClick = { navController.navigate(NavRoutes.CHECKOUT) },
                    onContinueShoppingClick = {
                        navController.navigate(NavRoutes.HOME) {
                            popUpTo(NavRoutes.HOME) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(NavRoutes.CHECKOUT) {
                CheckoutScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onOrderSuccess = { orderId, address, productNames ->
                        navController.navigate(NavRoutes.orderConfirmation(orderId, address, productNames)) {
                            popUpTo(NavRoutes.CART) { inclusive = true }
                        }
                    },
                    onNavigateToAddress = { navController.navigate(NavRoutes.EDIT_ADDRESS) },
                    onHomeClick = {
                        navController.navigate(NavRoutes.HOME) {
                            popUpTo(NavRoutes.HOME) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(
                route = NavRoutes.ORDER_CONFIRMATION,
                arguments = listOf(
                    navArgument("orderId") { type = NavType.IntType },
                    navArgument("address") { type = NavType.StringType; defaultValue = "" },
                    navArgument("productNames") { type = NavType.StringType; defaultValue = "" }
                )
            ) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getInt("orderId") ?: return@composable
                val shippingAddress = backStackEntry.arguments?.getString("address") ?: ""
                val productNames = backStackEntry.arguments?.getString("productNames") ?: ""
                OrderConfirmationScreen(
                    orderId = orderId,
                    shippingAddress = shippingAddress,
                    productNames = productNames,
                    onViewOrderDetails = {
                        navController.navigate(NavRoutes.ORDERS) {
                            popUpTo(NavRoutes.HOME) { saveState = true }
                            launchSingleTop = true
                        }
                    },
                    onContinueShopping = {
                        navController.navigate(NavRoutes.HOME) {
                            popUpTo(NavRoutes.HOME) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(NavRoutes.ORDERS) {
                OrderHistoryScreen(
                    onOrderClick = { orderId ->
                        navController.navigate(NavRoutes.orderDetail(orderId))
                    },
                    onHomeClick = {
                        navController.navigate(NavRoutes.HOME) {
                            popUpTo(NavRoutes.HOME) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(
                route = NavRoutes.ORDER_DETAIL,
                arguments = listOf(navArgument("orderId") { type = NavType.IntType }),
                deepLinks = listOf(
                    navDeepLink { uriPattern = "musclecart://order/{orderId}" }
                )
            ) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getInt("orderId") ?: return@composable
                OrderDetailScreen(
                    orderId = orderId,
                    onNavigateBack = { navController.popBackStack() },
                    onHomeClick = {
                        navController.navigate(NavRoutes.HOME) {
                            popUpTo(NavRoutes.HOME) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onReviewClick = { productId, productName ->
                        navController.navigate(NavRoutes.review(productId, orderId, productName))
                    }
                )
            }

            composable(
                route = NavRoutes.REVIEW,
                arguments = listOf(
                    navArgument("productId") { type = NavType.IntType },
                    navArgument("orderId") { type = NavType.IntType },
                    navArgument("productName") { type = NavType.StringType; defaultValue = "" }
                )
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getInt("productId") ?: return@composable
                val reviewOrderId = backStackEntry.arguments?.getInt("orderId") ?: return@composable
                val productName = backStackEntry.arguments?.getString("productName") ?: ""
                ReviewScreen(
                    productId = productId,
                    orderId = reviewOrderId,
                    productName = productName,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(NavRoutes.PROFILE) {
                ProfileScreen(
                    onLogout = onLogout,
                    onHomeClick = {
                        navController.navigate(NavRoutes.HOME) {
                            popUpTo(NavRoutes.HOME) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onOrderHistoryClick = {
                        navController.navigate(NavRoutes.ORDERS)
                    },
                    onWishlistClick = {
                        navController.navigate(NavRoutes.WISHLIST)
                    },
                    onTrackPackagesClick = {
                        navController.navigate(NavRoutes.TRACK_PACKAGES)
                    },
                    onPersonalDetailsClick = {
                        navController.navigate(NavRoutes.EDIT_PROFILE)
                    },
                    onPointsHistoryClick = {
                        navController.navigate(NavRoutes.POINTS_HISTORY)
                    },
                    onChangePasswordClick = {
                        navController.navigate(NavRoutes.CHANGE_PASSWORD)
                    },
                    onAddressClick = {
                        navController.navigate(NavRoutes.EDIT_ADDRESS)
                    },
                    onPaymentsClick = {
                        // TODO: Navigate to payments
                    },
                    onHelpCenterClick = {
                        // TODO: Navigate to help
                    },
                    onPrivacyClick = {
                        // TODO: Navigate to privacy
                    }
                )
            }

            composable(
                route = NavRoutes.SHOP,
                arguments = listOf(
                    navArgument("categoryId") {
                        type = NavType.IntType
                        defaultValue = -1
                    }
                )
            ) { backStackEntry ->
                val categoryId = backStackEntry.arguments?.getInt("categoryId")?.takeIf { it > 0 }
                // Bottom sheet state for shop screen
                var showProductDetailBottomSheet by remember { mutableStateOf(false) }
                var selectedProduct by remember { mutableStateOf<Product?>(null) }
                val shopSnackbarHostState = remember { SnackbarHostState() }
                val shopCoroutineScope = rememberCoroutineScope()

                Box(modifier = Modifier.fillMaxSize()) {
                    ShopScreen(
                        onProductClick = { product ->
                            selectedProduct = product
                            showProductDetailBottomSheet = true
                        },
                        onCartClick = {
                            navController.navigate(NavRoutes.CART)
                        },
                        onProfileClick = {
                            navController.navigate(NavRoutes.PROFILE) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onHomeClick = {
                            navController.navigate(NavRoutes.HOME) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        initialCategoryId = categoryId,
                        cartItemCount = cartCount
                    )

                    // Product Detail Bottom Sheet for Shop
                    ProductDetailBottomSheet(
                        product = selectedProduct,
                        isVisible = showProductDetailBottomSheet,
                        onDismiss = { 
                            showProductDetailBottomSheet = false 
                            selectedProduct = null
                        },
                        onAddToCartSuccess = {
                            val productName = selectedProduct?.name ?: "Product"
                            shopCoroutineScope.launch {
                                shopSnackbarHostState.showSnackbar(
                                    message = "✓ $productName added to cart!",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        },
                        onBuyNow = {
                            showProductDetailBottomSheet = false
                            selectedProduct = null
                            navController.navigate(NavRoutes.CART)
                        },
                        onViewReviews = { productId, productName ->
                            showProductDetailBottomSheet = false
                            selectedProduct = null
                            navController.navigate(NavRoutes.review(productId, 0, productName))
                        }
                    )
                    
                    // Styled success snackbar overlay (dari ProductDetailBottomSheet)
                    SnackbarHost(
                        hostState = shopSnackbarHostState,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 16.dp)
                    ) { data ->
                        Snackbar(
                            snackbarData = data,
                            containerColor = Color(0xFF4CAF50),
                            contentColor = Color.White,
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
            }

            composable(NavRoutes.WISHLIST) {
                // Bottom sheet state for wishlist screen
                var showProductDetailBottomSheet by remember { mutableStateOf(false) }
                var selectedProduct by remember { mutableStateOf<Product?>(null) }
                
                Box(modifier = Modifier.fillMaxSize()) {
                    WishlistScreen(
                        onBackClick = {
                            navController.popBackStack()
                        },
                        onCartClick = {
                            navController.navigate(NavRoutes.CART)
                        },
                        onProductClick = { product ->
                            selectedProduct = product
                            showProductDetailBottomSheet = true
                        },
                        cartItemCount = cartCount
                    )
                    
                    // Product Detail Bottom Sheet for Wishlist
                    ProductDetailBottomSheet(
                        product = selectedProduct,
                        isVisible = showProductDetailBottomSheet,
                        onDismiss = { 
                            showProductDetailBottomSheet = false 
                            selectedProduct = null
                        },
                        onAddToCartSuccess = {
                            // Optional: Show success message
                        },
                        onBuyNow = {
                            showProductDetailBottomSheet = false
                            selectedProduct = null
                            navController.navigate(NavRoutes.CART)
                        },
                        onViewReviews = { productId, productName ->
                            showProductDetailBottomSheet = false
                            selectedProduct = null
                            navController.navigate(NavRoutes.review(productId, 0, productName))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CustomBottomNavigationBar(
    items: List<MainTabItem>,
    selectedIndex: Int,
    onItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        val screenWidth = maxWidth
        val itemWidth = screenWidth / items.size
        val indicatorWidth = 40.dp
        
        val indicatorOffset by animateDpAsState(
            targetValue = (itemWidth * selectedIndex) + (itemWidth - indicatorWidth) / 2,
            animationSpec = tween(
                durationMillis = 300,
                easing = androidx.compose.animation.core.FastOutSlowInEasing
            ),
            label = "indicator_offset"
        )

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.5.dp)
                    .background(Color.LightGray.copy(alpha = 0.3f))
            )
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .offset(x = indicatorOffset)
                        .width(indicatorWidth)
                        .height(3.dp)
                        .background(
                            color = Color(0xFF2196F3),
                            shape = RoundedCornerShape(bottomStart = 2.dp, bottomEnd = 2.dp)
                        )
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEachIndexed { index, item ->
                    BottomNavItem(
                        item = item,
                        isSelected = selectedIndex == index,
                        onClick = { onItemClick(index) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun BottomNavItem(
    item: MainTabItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val color = if (isSelected) Color(0xFF2196F3) else Color.Gray
    val iconAlpha = if (isSelected) 1f else 0.6f

    Column(
        modifier = modifier
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (isSelected) item.iconSelected else item.icon,
            contentDescription = item.label,
            tint = color.copy(alpha = iconAlpha),
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = item.label,
            fontSize = 12.sp,
            color = color,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

data class MainTabItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val iconSelected: ImageVector
)


