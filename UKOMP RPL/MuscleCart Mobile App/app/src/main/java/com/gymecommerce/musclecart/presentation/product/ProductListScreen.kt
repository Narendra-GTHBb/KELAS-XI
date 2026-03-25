package com.gymecommerce.musclecart.presentation.product

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.gymecommerce.musclecart.domain.model.Category
import com.gymecommerce.musclecart.domain.model.Product
import com.gymecommerce.musclecart.presentation.product.components.CategoryCard
import com.gymecommerce.musclecart.presentation.product.components.ProductCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    onProductClick: (Product) -> Unit,
    onAddToCartClick: (Product) -> Unit,
    viewModel: ProductListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val swipeRefreshState = rememberSwipeRefreshState(uiState.isRefreshing)
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top App Bar with Search
        TopAppBar(
            title = {
                Text(
                    text = "MuscleCart",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            actions = {
                IconButton(
                    onClick = { viewModel.toggleSearchMode() }
                ) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search"
                    )
                }
            }
        )
        
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = { viewModel.refreshProducts() }
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Search Bar (if in search mode)
                if (uiState.isSearchMode) {
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = { viewModel.updateSearchQuery(it) },
                        label = { Text("Search products...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        singleLine = true
                    )
                }
                
                // Categories
                if (uiState.categories.isNotEmpty()) {
                    Text(
                        text = "Categories",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                    
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // All Categories option
                        item {
                            CategoryCard(
                                category = Category(
                                    id = -1,
                                    name = "All",
                                    description = "",
                                    imageUrl = "",
                                    createdAt = 0L,
                                    updatedAt = 0L
                                ),
                                isSelected = uiState.selectedCategoryId == null,
                                onCategoryClick = { viewModel.selectCategory(null) }
                            )
                        }
                        
                        items(uiState.categories) { category ->
                            CategoryCard(
                                category = category,
                                isSelected = uiState.selectedCategoryId == category.id,
                                onCategoryClick = { viewModel.selectCategory(category.id) }
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                // Products Grid
                when {
                    uiState.isLoading && uiState.products.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
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
                                    onClick = { viewModel.refreshProducts() }
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
                                text = if (uiState.isSearchMode && uiState.searchQuery.isNotEmpty()) {
                                    "No products found for \"${uiState.searchQuery}\""
                                } else {
                                    "No products available"
                                },
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    
                    else -> {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(uiState.products) { product ->
                                ProductCard(
                                    product = product,
                                    onProductClick = onProductClick,
                                    onAddToCartClick = { 
                                        onAddToCartClick(it)
                                        viewModel.showAddToCartSuccess()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    
    // Show snackbar for add to cart success
    if (uiState.showAddToCartSuccess) {
        LaunchedEffect(uiState.showAddToCartSuccess) {
            viewModel.hideAddToCartSuccess()
        }
    }
}