package com.gymecommerce.musclecart.presentation.wishlist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gymecommerce.musclecart.domain.model.Product
import com.gymecommerce.musclecart.presentation.cart.CartViewModel
import com.gymecommerce.musclecart.presentation.components.OptimizedProductThumbnail
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistScreen(
    onBackClick: () -> Unit,
    onCartClick: () -> Unit,
    onProductClick: (Product) -> Unit,
    cartItemCount: Int = 0,
    viewModel: WishlistViewModel = hiltViewModel(),
    cartViewModel: CartViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val filteredProducts by viewModel.filteredProducts.collectAsState()
    val primaryBlue = Color(0xFF1976D2)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Wishlist",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    // Cart Icon with Badge
                    Box(
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        IconButton(onClick = onCartClick) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Cart"
                            )
                        }
                        if (cartItemCount > 0) {
                            Badge(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = (-4).dp, y = 8.dp),
                                containerColor = primaryBlue
                            ) {
                                Text(
                                    text = cartItemCount.toString(),
                                    fontSize = 10.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8F9FA))
        ) {
            // Category Tabs
            CategoryTabs(
                selectedCategory = uiState.selectedCategory,
                itemCount = filteredProducts.size,
                allItemsCount = uiState.allProducts.size,
                onCategorySelected = { viewModel.selectCategory(it) }
            )

            Divider(color = Color(0xFFE0E0E0), thickness = 1.dp)

            // Content
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = primaryBlue)
                    }
                }
                uiState.error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = uiState.error ?: "Unknown error",
                            color = Color.Red
                        )
                    }
                }
                filteredProducts.isEmpty() -> {
                    EmptyWishlist()
                }
                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredProducts, key = { it.id }) { product ->
                            WishlistProductCard(
                                product = product,
                                onProductClick = { onProductClick(product) },
                                onFavoriteClick = { viewModel.removeFavorite(product.id) },
                                onAddToCart = {
                                    cartViewModel.addToCart(product.id, 1)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryTabs(
    selectedCategory: String,
    itemCount: Int,
    allItemsCount: Int,
    onCategorySelected: (String) -> Unit
) {
    val categories = listOf("All Items", "Supplements", "Apparel", "Equip")
    val primaryBlue = Color(0xFF1976D2)
    
    ScrollableTabRow(
        selectedTabIndex = categories.indexOf(selectedCategory),
        containerColor = Color.White,
        contentColor = primaryBlue,
        indicator = { tabPositions ->
            val selectedIdx = categories.indexOf(selectedCategory)
            if (selectedIdx >= 0 && selectedIdx < tabPositions.size) {
                Box(
                    Modifier
                        .tabIndicatorOffset(tabPositions[selectedIdx])
                        .height(3.dp)
                        .background(primaryBlue, RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                )
            }
        },
        divider = {}
    ) {
        categories.forEach { category ->
            val count = if (category == "All Items") allItemsCount else itemCount
            val isSelected = selectedCategory == category
            
            Tab(
                selected = isSelected,
                onClick = { onCategorySelected(category) },
                text = {
                    Text(
                        text = if (category == "All Items") "$category ($allItemsCount)" else category,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) primaryBlue else Color.Gray,
                        fontSize = 14.sp
                    )
                }
            )
        }
    }
}

@Composable
fun WishlistProductCard(
    product: Product,
    onProductClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onAddToCart: () -> Unit
) {
    val primaryBlue = Color(0xFF2463EB)
    val currencyFormatter = remember { NumberFormat.getCurrencyInstance(Locale("id", "ID")) }

    val hasDiscount = product.id % 3 == 0
    val isNew = !hasDiscount && product.id % 2 != 0
    val categoryColor = if (isNew) primaryBlue else Color(0xFF9E9E9E)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onProductClick)
    ) {
        // ── Image container ──────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4f / 5f)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFF8FAFC))
        ) {
            OptimizedProductThumbnail(
                imageUrl = product.imageUrl,
                productName = product.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                shape = RoundedCornerShape(16.dp)
            )

            // Badge top-left
            when {
                isNew -> Surface(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp),
                    color = Color.White.copy(alpha = 0.92f),
                    shape = RoundedCornerShape(4.dp),
                    tonalElevation = 0.dp,
                    shadowElevation = 0.dp
                ) {
                    Text(
                        text = "NEW",
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF374151),
                        letterSpacing = 0.6.sp
                    )
                }
                hasDiscount -> Surface(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp),
                    color = Color(0xFFEF4444),
                    shape = RoundedCornerShape(4.dp),
                    tonalElevation = 0.dp,
                    shadowElevation = 0.dp
                ) {
                    Text(
                        text = "SALE",
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 0.6.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // ── Category label ───────────────────────────────────────────────
        Text(
            text = product.category?.name?.uppercase() ?: "MUSCLECART",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = categoryColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            letterSpacing = 1.2.sp
        )

        Spacer(modifier = Modifier.height(3.dp))

        // ── Product name ─────────────────────────────────────────────────
        Text(
            text = product.name,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0E121B),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ── Price + buttons row ──────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = currencyFormatter.format(product.price),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF0E121B),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Wishlist button — always active/filled (item is in wishlist)
                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        Icons.Filled.Favorite,
                        contentDescription = "Remove from wishlist",
                        tint = Color(0xFFE11D48),
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Add to cart button
                Surface(
                    onClick = onAddToCart,
                    modifier = Modifier.size(34.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = primaryBlue,
                    tonalElevation = 0.dp,
                    shadowElevation = 4.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add to Cart",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
    }
}

@Composable
fun EmptyWishlist() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = Color.Gray.copy(alpha = 0.3f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Your wishlist is empty",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Add products you like to your wishlist",
                fontSize = 14.sp,
                color = Color.Gray.copy(alpha = 0.7f)
            )
        }
    }
}
