package com.gymecommerce.musclecart.presentation.shop

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.gymecommerce.musclecart.domain.model.Product
import com.gymecommerce.musclecart.presentation.cart.CartViewModel
import com.gymecommerce.musclecart.presentation.common.HomeButton
import com.gymecommerce.musclecart.presentation.product.ProductFilter
import com.gymecommerce.musclecart.presentation.product.ProductListViewModel
import com.gymecommerce.musclecart.presentation.product.SortOption
import com.gymecommerce.musclecart.presentation.components.OptimizedProductThumbnail
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopScreen(
    onProductClick: (Product) -> Unit,
    onCartClick: () -> Unit,
    onProfileClick: () -> Unit,
    onHomeClick: () -> Unit,
    cartItemCount: Int = 0,
    initialCategoryId: Int? = null,
    viewModel: ProductListViewModel = hiltViewModel(),
    cartViewModel: CartViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val cartUiState by cartViewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Apply initial category filter once when screen opens with a categoryId
    LaunchedEffect(initialCategoryId) {
        if (initialCategoryId != null && uiState.selectedCategoryId != initialCategoryId) {
            viewModel.selectCategory(initialCategoryId)
        }
    }
    val lifecycleOwner = LocalLifecycleOwner.current

    // Reload favorite IDs every time this screen resumes (e.g. coming back from wishlist)
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadFavoriteIds()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    var selectedSortOption by remember { mutableStateOf("Popularity") }
    var showSortMenu by remember { mutableStateOf(false) }
    var showFilterDialog by remember { mutableStateOf(false) }
    
    val sortOptions = listOf(
        "Popularity" to SortOption.POPULARITY,
        "Price: Low to High" to SortOption.PRICE_LOW_HIGH,
        "Price: High to Low" to SortOption.PRICE_HIGH_LOW,
        "Name A-Z" to SortOption.NAME_AZ
    )
    val snackbarHostState = remember { SnackbarHostState() }
    var lastAddedProductName by remember { mutableStateOf<String?>(null) }
    var snackbarIsError by remember { mutableStateOf(false) }
    
    // Show error snackbar when cart operation fails (replace plain Toast)
    LaunchedEffect(cartUiState.error) {
        cartUiState.error?.let {
            snackbarIsError = true
            snackbarHostState.showSnackbar(
                message = "Gagal menambahkan ke cart. Silakan coba lagi.",
                duration = SnackbarDuration.Short
            )
            cartViewModel.clearError()
        }
    }
    
    // Show success snackbar when product is added to cart
    LaunchedEffect(uiState.showAddToCartSuccess) {
        if (uiState.showAddToCartSuccess) {
            snackbarIsError = false
            val productName = lastAddedProductName ?: "Product"
            snackbarHostState.showSnackbar(
                message = "✓ $productName added to cart!",
                duration = SnackbarDuration.Short
            )
            viewModel.hideAddToCartSuccess()
            lastAddedProductName = null
        }
    }
    
    // Show favorite snackbar when favorite is toggled
    LaunchedEffect(uiState.favoriteMessage) {
        uiState.favoriteMessage?.let { message ->
            snackbarIsError = uiState.favoriteIsError
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            viewModel.clearFavoriteMessage()
        }
    }
    
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = if (snackbarIsError) Color(0xFFE53935) else Color(0xFF4CAF50),
                    contentColor = Color.White,
                    shape = RoundedCornerShape(8.dp)
                )
            }
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
        ) {
            // Top Bar
            ShopTopBar(
            searchQuery = uiState.searchQuery,
            onSearchQueryChange = { query ->
                viewModel.updateSearchQuery(query)
                if (query.isNotEmpty() && !uiState.isSearchMode) {
                    viewModel.toggleSearchMode()
                } else if (query.isEmpty() && uiState.isSearchMode) {
                    viewModel.toggleSearchMode()
                }
            },
            onCartClick = onCartClick,
            onProfileClick = onProfileClick,
            cartItemCount = cartItemCount,
            onHomeClick = onHomeClick
        )
        
        // Category Tabs
        if (uiState.categories.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(vertical = 16.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // All category
                item {
                    CategoryChip(
                        text = "All",
                        isSelected = uiState.selectedCategoryId == null,
                        onClick = { viewModel.selectCategory(null) }
                    )
                }
                
                items(uiState.categories) { category ->
                    CategoryChip(
                        text = category.name,
                        isSelected = uiState.selectedCategoryId == category.id,
                        onClick = { viewModel.selectCategory(category.id) }
                    )
                }
            }
        }
        
        // Filters and Sort Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Filters Button
            OutlinedButton(
                onClick = { showFilterDialog = true },
                modifier = Modifier.height(40.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (uiState.filter != ProductFilter()) Color(0xFFE53935) else Color(0xFF1976D2)
                )
            ) {
                Icon(
                    Icons.Default.FilterList,
                    contentDescription = "Filters",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (uiState.filter != ProductFilter()) "Filter Aktif" else "Filters",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            
            // Sort Dropdown
            Box {
                OutlinedButton(
                    onClick = { showSortMenu = true },
                    modifier = Modifier.height(40.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF1976D2)
                    )
                ) {
                    Text(
                        text = "SORT BY",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1976D2)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = sortOptions.firstOrNull { it.second == uiState.sortOption }?.first ?: selectedSortOption,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF212121)
                    )
                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = "Sort",
                        tint = Color(0xFF1976D2)
                    )
                }
                
                DropdownMenu(
                    expanded = showSortMenu,
                    onDismissRequest = { showSortMenu = false }
                ) {
                    sortOptions.forEach { (label, option) ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    label,
                                    fontWeight = if (uiState.sortOption == option) FontWeight.Bold else FontWeight.Normal,
                                    color = if (uiState.sortOption == option) Color(0xFF1976D2) else Color(0xFF212121)
                                )
                            },
                            onClick = {
                                selectedSortOption = label
                                viewModel.setSortOption(option)
                                showSortMenu = false
                            }
                        )
                    }
                }
            }
        }
        
        Divider(color = Color(0xFFE0E0E0), thickness = 1.dp)
        
        // Products Grid
        when {
            uiState.isLoading && uiState.products.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF1976D2))
                }
            }
            
            uiState.error != null && uiState.products.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error loading products",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { viewModel.refreshProducts() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1976D2)
                            )
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }
            
            uiState.products.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No products available",
                        fontSize = 16.sp,
                        color = Color(0xFF757575)
                    )
                }
            }
            
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.products) { product ->
                        ShopProductCard(
                            product = product,
                            isFavorite = product.id in uiState.favoriteProductIds,
                            onProductClick = { onProductClick(product) },
                            onAddToCartClick = { 
                                lastAddedProductName = product.name
                                cartViewModel.addToCart(product.id, 1)
                                viewModel.showAddToCartSuccess()
                            },
                            onFavoriteClick = { viewModel.toggleFavorite(product.id) }
                        )
                    }
                }
            }
        }
        }
    }

    if (showFilterDialog) {
        FilterDialog(
            currentFilter = uiState.filter,
            onApply = { newFilter ->
                viewModel.setFilter(newFilter)
                showFilterDialog = false
            },
            onDismiss = { showFilterDialog = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDialog(
    currentFilter: ProductFilter,
    onApply: (ProductFilter) -> Unit,
    onDismiss: () -> Unit
) {
    var minPriceText by remember { mutableStateOf(currentFilter.minPrice?.toInt()?.toString() ?: "") }
    var maxPriceText by remember { mutableStateOf(currentFilter.maxPrice?.toInt()?.toString() ?: "") }
    var inStockOnly by remember { mutableStateOf(currentFilter.inStockOnly) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Filter Produk",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Price range
                Text(
                    text = "Rentang Harga (Rp)",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color(0xFF424242)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = minPriceText,
                        onValueChange = { minPriceText = it.filter { c -> c.isDigit() } },
                        label = { Text("Min", fontSize = 12.sp) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1976D2)
                        )
                    )
                    OutlinedTextField(
                        value = maxPriceText,
                        onValueChange = { maxPriceText = it.filter { c -> c.isDigit() } },
                        label = { Text("Max", fontSize = 12.sp) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1976D2)
                        )
                    )
                }

                // Stock filter
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Tampilkan stok tersedia saja",
                        fontSize = 14.sp,
                        color = Color(0xFF424242)
                    )
                    Switch(
                        checked = inStockOnly,
                        onCheckedChange = { inStockOnly = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF1976D2))
                    )
                }

                // Reset button
                if (currentFilter != ProductFilter()) {
                    TextButton(
                        onClick = {
                            minPriceText = ""
                            maxPriceText = ""
                            inStockOnly = false
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Reset Filter", color = Color(0xFFE53935), fontSize = 13.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onApply(
                        ProductFilter(
                            minPrice = minPriceText.toDoubleOrNull(),
                            maxPrice = maxPriceText.toDoubleOrNull(),
                            inStockOnly = inStockOnly
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
            ) {
                Text("Terapkan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = Color(0xFF757575))
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopTopBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onCartClick: () -> Unit,
    onProfileClick: () -> Unit,
    onHomeClick: () -> Unit,
    cartItemCount: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        // Title Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Logo + Icon di kiri (mepet kiri)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Outlined.LocalFireDepartment,
                    contentDescription = null,
                    tint = Color(0xFF1976D2),
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    text = "MuscleCart",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )
            }
            
            // Home Icon + Cart Icon di kanan
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Home Icon
                HomeButton(
                    onClick = onHomeClick,
                    iconOnly = true
                )
                
                // Cart Icon with Badge
                BadgedBox(
                    badge = {
                        if (cartItemCount > 0) {
                            Badge(
                                containerColor = Color(0xFF1976D2),
                                contentColor = Color.White
                            ) {
                                Text(
                                    text = cartItemCount.toString(),
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                ) {
                    IconButton(onClick = onCartClick) {
                        Icon(
                            Icons.Default.ShoppingCart,
                            contentDescription = "Cart",
                            tint = Color(0xFF1976D2)
                        )
                    }
                }
            }
        }
        
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            placeholder = { 
                Text(
                    text = "Search supplements, gear...",
                    color = Color(0xFF9E9E9E)
                ) 
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color(0xFF9E9E9E)
                )
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedBorderColor = Color(0xFF1976D2),
                unfocusedContainerColor = Color(0xFFFAFAFA),
                focusedContainerColor = Color(0xFFFAFAFA)
            ),
            singleLine = true
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) Color(0xFF1976D2) else Color.Transparent
    val textColor = if (isSelected) Color.White else Color(0xFF424242)
    val borderColor = if (isSelected) Color.Transparent else Color(0xFFE0E0E0)
    
    Surface(
        onClick = onClick,
        modifier = Modifier.height(40.dp),
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                color = textColor
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopProductCard(
    product: Product,
    isFavorite: Boolean = false,
    onProductClick: () -> Unit,
    onAddToCartClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currencyFormatter = remember { NumberFormat.getCurrencyInstance(Locale("id", "ID")) }

    val hasDiscount = product.id % 3 == 0
    val isNew = !hasDiscount && product.id % 2 != 0
    val originalPrice = if (hasDiscount) product.price * 1.25 else null

    val primaryBlue = Color(0xFF2463EB)
    val categoryColor = if (isNew) primaryBlue else Color(0xFF9E9E9E)

    Column(
        modifier = modifier
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

        // ── Price + Add-to-cart row ──────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Price 
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

            // Favourite + Add-to-cart buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Wishlist / favourite button
                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = if (isFavorite) "Remove from wishlist" else "Add to wishlist",
                        tint = if (isFavorite) Color(0xFFE11D48) else Color(0xFF9CA3AF),
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Add to cart button — always blue
                Surface(
                    onClick = onAddToCartClick,
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
