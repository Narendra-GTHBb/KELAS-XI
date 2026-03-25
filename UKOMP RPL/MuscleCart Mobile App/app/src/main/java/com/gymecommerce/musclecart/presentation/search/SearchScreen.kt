package com.gymecommerce.musclecart.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gymecommerce.musclecart.domain.model.Product
import com.gymecommerce.musclecart.presentation.cart.CartViewModel
import com.gymecommerce.musclecart.presentation.shop.ShopProductCard

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun SearchScreen(
    onNavigateBack: () -> Unit,
    onProductClick: (Product) -> Unit,
    cartItemCount: Int = 0,
    viewModel: SearchViewModel = hiltViewModel(),
    cartViewModel: CartViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val cartUiState by cartViewModel.uiState.collectAsState()
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val snackbarHostState = remember { SnackbarHostState() }
    var lastAddedProductName by remember { mutableStateOf<String?>(null) }

    // Auto-focus the text field when screen opens
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    // Show cart error snackbar
    LaunchedEffect(cartUiState.error) {
        cartUiState.error?.let {
            snackbarHostState.showSnackbar(
                message = "Gagal menambahkan ke cart. Coba lagi.",
                duration = SnackbarDuration.Short
            )
            cartViewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            SearchTopBar(
                query = uiState.query,
                onQueryChange = viewModel::onQueryChange,
                onBackClick = onNavigateBack,
                onClearClick = { viewModel.onQueryChange("") },
                focusRequester = focusRequester,
                onSearch = { keyboardController?.hide() }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = Color(0xFF4CAF50),
                    contentColor = Color.White,
                    shape = RoundedCornerShape(8.dp)
                )
            }
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                // Empty query - show hint
                uiState.query.isBlank() -> {
                    SearchHintContent(
                        onSuggestionClick = { suggestion ->
                            viewModel.onQueryChange(suggestion)
                        }
                    )
                }

                // Loading
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF1976D2))
                    }
                }

                // Error
                uiState.error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Icon(
                                Icons.Default.SearchOff,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = Color(0xFFBDBDBD)
                            )
                            Text(
                                text = "Pencarian gagal",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF616161)
                            )
                            Text(
                                text = uiState.error ?: "",
                                fontSize = 14.sp,
                                color = Color(0xFF9E9E9E),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // No results found
                uiState.hasSearched && uiState.products.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Icon(
                                Icons.Default.SearchOff,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = Color(0xFFBDBDBD)
                            )
                            Text(
                                text = "Produk tidak ditemukan",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF616161)
                            )
                            Text(
                                text = "Coba kata kunci lain untuk \"${uiState.query}\"",
                                fontSize = 14.sp,
                                color = Color(0xFF9E9E9E),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // Products found
                uiState.products.isNotEmpty() -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Results count header
                        Text(
                            text = "${uiState.products.size} produk ditemukan untuk \"${uiState.query}\"",
                            fontSize = 13.sp,
                            color = Color(0xFF757575),
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White)
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                        )

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
                                    isFavorite = false,
                                    onProductClick = { onProductClick(product) },
                                    onAddToCartClick = {
                                        lastAddedProductName = product.name
                                        cartViewModel.addToCart(product.id, 1)
                                    },
                                    onFavoriteClick = { /* wishlist dari search screen */ }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchTopBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onClearClick: () -> Unit,
    focusRequester: FocusRequester,
    onSearch: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF212121)
                )
            }

            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester),
                placeholder = {
                    Text(
                        text = "Cari produk...",
                        color = Color(0xFF9E9E9E),
                        fontSize = 15.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = Color(0xFF9E9E9E),
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = onClearClick) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = Color(0xFF9E9E9E),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedBorderColor = Color(0xFF1976D2),
                    unfocusedContainerColor = Color(0xFFFAFAFA),
                    focusedContainerColor = Color(0xFFFAFAFA)
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { onSearch() })
            )
        }
    }
}

@Composable
private fun SearchHintContent(
    onSuggestionClick: (String) -> Unit = {}
) {
    val suggestions = listOf(
        "Whey Protein",
        "Pre-Workout",
        "Creatine",
        "BCAA",
        "Dumbbells",
        "Resistance Band"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Icon(
            Icons.Default.Search,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Color(0xFFBDBDBD)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Cari produk favoritmu",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF424242)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Supplement, peralatan gym, dan lebih banyak lagi",
            fontSize = 14.sp,
            color = Color(0xFF9E9E9E),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Saran Pencarian",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF424242),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        suggestions.chunked(3).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { suggestion ->
                    SuggestionChip(
                        onClick = { onSuggestionClick(suggestion) },
                        label = {
                            Text(
                                text = suggestion,
                                fontSize = 13.sp,
                                color = Color(0xFF1976D2)
                            )
                        },
                        modifier = Modifier.wrapContentWidth(),
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = Color(0xFFE3F2FD)
                        ),
                        border = SuggestionChipDefaults.suggestionChipBorder(
                            borderColor = Color(0xFF90CAF9)
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
