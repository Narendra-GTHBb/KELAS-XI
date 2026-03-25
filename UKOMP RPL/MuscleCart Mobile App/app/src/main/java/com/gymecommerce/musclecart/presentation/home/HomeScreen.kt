package com.gymecommerce.musclecart.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.gymecommerce.musclecart.domain.model.Product
import com.gymecommerce.musclecart.presentation.product.ProductDetailBottomSheet
import com.gymecommerce.musclecart.presentation.components.OptimizedProductThumbnail
import com.gymecommerce.musclecart.util.ServerConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onProductClick: (Product) -> Unit, // Keep for backward compatibility but won't be used
    onCategoryClick: (String) -> Unit,
    onCartClick: () -> Unit,
    onShopClick: () -> Unit,
    onSearchClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    cartItemCount: Int = 0,
    notificationCount: Int = 0,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val swipeRefreshState = rememberSwipeRefreshState(uiState.isRefreshing)
    
    // Bottom sheet state
    var showProductDetailBottomSheet by remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                HomeTopBar(
                    cartItemCount = cartItemCount,
                    notificationCount = notificationCount,
                    onCartClick = onCartClick,
                    onSearchClick = onSearchClick,
                    onNotificationClick = onNotificationClick
                )
            }
        ) { paddingValues ->
            SwipeRefresh(
                state = swipeRefreshState,
                onRefresh = { viewModel.refreshHomeData() },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                // Hero Banner
                item {
                    HeroBanner(
                        onShopNowClick = onShopClick
                    )
                }

                // Shop by Goal Section
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    ShopByGoalSection(
                        onCategoryClick = onCategoryClick,
                        onViewAllClick = { /* Navigate to categories */ }
                    )
                }

                // Product Spotlight
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    ProductSpotlightSection(
                        product = uiState.featuredProduct,
                        onProductClick = {
                            uiState.featuredProduct?.let { product ->
                                selectedProduct = product
                                showProductDetailBottomSheet = true
                            }
                        },
                        onAddToCartClick = {
                            uiState.featuredProduct?.let { viewModel.addToCart(it) }
                        }
                    )
                }

                // Recommended for You
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    RecommendedSection(
                        products = uiState.recommendedProducts,
                        onProductClick = { product ->
                            selectedProduct = product
                            showProductDetailBottomSheet = true
                        }
                    )
                }

                // Features Section
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    FeaturesSection()
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
            }
        }
        
        // Product Detail Bottom Sheet
        ProductDetailBottomSheet(
            product = selectedProduct,
            isVisible = showProductDetailBottomSheet,
            onDismiss = { 
                showProductDetailBottomSheet = false 
                selectedProduct = null
            },
            onAddToCartSuccess = {
                // Optional: Show success message or refresh cart count
                // Could add a snackbar here
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    cartItemCount: Int,
    notificationCount: Int = 0,
    onCartClick: () -> Unit,
    onSearchClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {}
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FitnessCenter,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Text(
                    text = "MuscleCart",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        actions = {
            IconButton(onClick = onSearchClick) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            }
            IconButton(onClick = onNotificationClick) {
                BadgedBox(
                    badge = {
                        if (notificationCount > 0) {
                            Badge {
                                Text(
                                    text = if (notificationCount > 99) "99+" else notificationCount.toString()
                                )
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications"
                    )
                }
            }
            IconButton(onClick = onCartClick) {
                BadgedBox(
                    badge = {
                        if (cartItemCount > 0) {
                            Badge { Text(cartItemCount.toString()) }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Cart"
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}

@Composable
fun HeroBanner(
    onShopNowClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(350.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1a1a1a),
                        Color(0xFF2d2d2d)
                    )
                )
            )
    ) {
        // Background Image Placeholder
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1a1a1a))
        )

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color.White)) {
                        append("UNLEASH YOUR\n")
                    }
                    withStyle(style = SpanStyle(color = Color(0xFF2196F3))) {
                        append("POTENTIAL")
                    }
                },
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 38.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Premium equipment and elite\nsupplements engineered for your peak\nperformance.",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.8f),
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onShopNowClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2196F3)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(48.dp)
            ) {
                Text(
                    text = "Shop Now",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun ShopByGoalSection(
    onCategoryClick: (String) -> Unit,
    onViewAllClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "EXPLORE",
            fontSize = 12.sp,
            color = Color(0xFF2196F3),
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Shop by Goal",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            TextButton(onClick = onViewAllClick) {
                Text(
                    text = "View all",
                    color = Color(0xFF2196F3)
                )
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = Color(0xFF2196F3),
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Strength Training - Large card on left
            GoalCard(
                title = "Strength\nTraining",
                subtitle = "12 PRODUCTS",
                backgroundColor = Color(0xFF2d2d2d),
                imageUrl = "${ServerConfig.SERVER_URL}/images/banners/strength%20training%20photo.jpg",
                modifier = Modifier
                    .weight(1f)
                    .height(280.dp),
                onClick = { onCategoryClick("strength") }
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Fat Loss - Top right
                GoalCard(
                    title = "Fat Loss",
                    subtitle = "",
                    backgroundColor = Color(0xFF4a4a4a),
                    imageUrl = "${ServerConfig.SERVER_URL}/images/banners/fat-loss.jpg",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(134.dp),
                    onClick = { onCategoryClick("fat-loss") }
                )

                // Endurance - Bottom right
                GoalCard(
                    title = "Endurance",
                    subtitle = "",
                    backgroundColor = Color(0xFF2d2d2d),
                    imageUrl = "${ServerConfig.SERVER_URL}/images/banners/endurance%20photo.jpg",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(134.dp),
                    onClick = { onCategoryClick("endurance") }
                )
            }
        }
    }
}

@Composable
fun GoalCard(
    title: String,
    subtitle: String,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    imageUrl: String? = null,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
    ) {
        // Background Image or Color
        if (imageUrl != null) {
            OptimizedProductThumbnail(
                imageUrl = imageUrl,
                productName = title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                shape = RoundedCornerShape(16.dp)
            )
            // Dark overlay untuk keterbacaan teks
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            )
                        )
                    )
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
            )
        }
        
        // Text Content
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            if (subtitle.isNotEmpty()) {
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
fun ProductSpotlightSection(
    product: Product?,
    onProductClick: () -> Unit,
    onAddToCartClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Product Spotlight",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .height(3.dp)
                    .width(40.dp)
                    .background(Color(0xFF2196F3))
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        product?.let { prod ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onProductClick),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Product Image
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    ) {
                        OptimizedProductThumbnail(
                            imageUrl = prod.imageUrl,
                            productName = prod.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Editor's Choice Badge
                    Surface(
                        color = Color(0xFF2196F3).copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "EDITOR'S CHOICE",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2196F3),
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Product Name
                    Text(
                        text = prod.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Description
                    Text(
                        text = prod.description,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Price and Add to Cart
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$${String.format("%.2f", prod.price)}",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Button(
                            onClick = onAddToCartClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1a237e)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(48.dp)
                        ) {
                            Text(
                                text = "Add to Cart",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RecommendedSection(
    products: List<Product>,
    onProductClick: (Product) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Recommended for You",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(products) { product ->
                RecommendedProductCard(
                    product = product,
                    onClick = { onProductClick(product) }
                )
            }
        }
    }
}

@Composable
fun RecommendedProductCard(
    product: Product,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Product Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                OptimizedProductThumbnail(
                    imageUrl = product.imageUrl,
                    productName = product.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit,
                    shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                )
            }

            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = product.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "$${String.format("%.2f", product.price)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun FeaturesSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        FeatureItem(
            icon = Icons.Default.LocalShipping,
            title = "Fast Shipping",
            description = "Free delivery on orders over $99"
        )

        Spacer(modifier = Modifier.height(16.dp))

        FeatureItem(
            icon = Icons.Default.SupportAgent,
            title = "Expert Support",
            description = "Available 24/7 for your questions"
        )

        Spacer(modifier = Modifier.height(16.dp))

        FeatureItem(
            icon = Icons.Default.Security,
            title = "Secure Payment",
            description = "Encrypted checkout process"
        )
    }
}

@Composable
fun FeatureItem(
    icon: ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF2196F3).copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF2196F3),
                modifier = Modifier.size(24.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = description,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}
