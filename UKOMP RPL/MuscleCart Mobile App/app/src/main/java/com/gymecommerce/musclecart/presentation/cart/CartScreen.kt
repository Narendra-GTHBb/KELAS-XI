package com.gymecommerce.musclecart.presentation.cart

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gymecommerce.musclecart.domain.model.Cart
import com.gymecommerce.musclecart.domain.model.CartItem
import com.gymecommerce.musclecart.domain.model.Category
import com.gymecommerce.musclecart.domain.model.Product
import com.gymecommerce.musclecart.presentation.cart.components.CartItemCard
import com.gymecommerce.musclecart.presentation.common.HomeButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    onCheckoutClick: () -> Unit,
    onContinueShoppingClick: () -> Unit,
    viewModel: CartViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Refresh cart every time this screen becomes visible
    LaunchedEffect(Unit) {
        viewModel.refreshCart()
    }
    
    var showClearCartDialog by remember { mutableStateOf(false) }
    var promoCode by remember { mutableStateOf("") }
    val error = uiState.error
    
    val backgroundColor = Color(0xFFF5F5F5)
    val primaryBlue = Color(0xFF1976D2)
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Custom Top Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    // Back button
                    IconButton(
                        onClick = onContinueShoppingClick,
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF212121)
                        )
                    }
                    
                    // Title and counter in center
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Your Cart",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF212121)
                        )
                        
                        if (uiState.cart.isNotEmpty()) {
                            Text(
                                text = "${uiState.cart.getTotalItems()} ITEMS SELECTED",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = primaryBlue
                            )
                        }
                    }
                    
                    // Menu button
                    IconButton(
                        onClick = { showClearCartDialog = true },
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "Menu",
                            tint = Color(0xFF212121)
                        )
                    }
                }
            }
            
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = primaryBlue)
                    }
                }
                
                error != null -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Error",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Text(
                                text = error,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            Button(
                                onClick = { viewModel.clearError() },
                                modifier = Modifier.padding(top = 8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = primaryBlue
                                )
                            ) {
                                Text("Dismiss")
                            }
                        }
                    }
                }
                
                uiState.cart.isEmpty() -> {
                    // Empty cart state
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.ShoppingCart,
                            contentDescription = "Empty cart",
                            modifier = Modifier.size(120.dp),
                            tint = Color(0xFFBDBDBD)
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Text(
                            text = "Your cart is empty",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF424242)
                        )
                        
                        Text(
                            text = "Add some products to get started",
                            fontSize = 16.sp,
                            color = Color(0xFF757575),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
                        )
                        
                        Button(
                            onClick = onContinueShoppingClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = primaryBlue
                            ),
                            shape = RoundedCornerShape(28.dp)
                        ) {
                            Text("Continue Shopping", fontSize = 16.sp)
                        }
                    }
                }
                
                else -> {
                    // Cart with items
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Cart items
                        items(
                            items = uiState.cart.items,
                            key = { it.productId }
                        ) { cartItem ->
                            CartItemCard(
                                cartItem = cartItem,
                                onQuantityChange = { newQuantity ->
                                    viewModel.updateQuantity(cartItem.productId, newQuantity)
                                },
                                onRemoveClick = {
                                    viewModel.removeFromCart(cartItem.productId)
                                },
                                isUpdating = uiState.updatingItems.contains(cartItem.productId)
                            )
                        }
                        
                        // Promo Code Section
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp)
                                ) {
                                    Text(
                                        text = "PROMO CODE",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF9E9E9E),
                                        letterSpacing = 1.sp
                                    )
                                    
                                    Spacer(modifier = Modifier.height(12.dp))
                                    
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        OutlinedTextField(
                                            value = promoCode,
                                            onValueChange = { promoCode = it },
                                            modifier = Modifier.weight(1f),
                                            placeholder = {
                                                Text(
                                                    "Enter code (e.g. GAINS20)",
                                                    fontSize = 14.sp,
                                                    color = Color(0xFFBDBDBD)
                                                )
                                            },
                                            singleLine = true,
                                            colors = OutlinedTextFieldDefaults.colors(
                                                unfocusedBorderColor = Color(0xFFE0E0E0),
                                                focusedBorderColor = primaryBlue
                                            ),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        
                                        Button(
                                            onClick = { /* TODO: Apply promo */ },
                                            modifier = Modifier
                                                .height(56.dp)
                                                .widthIn(min = 100.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = primaryBlue
                                            ),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                "APPLY",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        
                        // Summary Section
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp)
                                ) {
                                    // Total Amount
                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        color = Color(0xFFF0F8FF),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Total Amount",
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF212121)
                                            )
                                            Text(
                                                text = uiState.cart.getFormattedTotalPrice(),
                                                fontSize = 24.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = primaryBlue
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        
                        // Trust Badges
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Shield,
                                    contentDescription = "Secure",
                                    tint = Color(0xFFBDBDBD),
                                    modifier = Modifier.size(40.dp)
                                )
                                Icon(
                                    Icons.Default.CreditCard,
                                    contentDescription = "Payment",
                                    tint = Color(0xFFBDBDBD),
                                    modifier = Modifier.size(40.dp)
                                )
                                Icon(
                                    Icons.Default.LocalShipping,
                                    contentDescription = "Delivery",
                                    tint = Color(0xFFBDBDBD),
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                            
                            Text(
                                text = "SECURE 256-BIT ENCRYPTED CHECKOUT",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFFBDBDBD),
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                letterSpacing = 0.5.sp
                            )
                        }
                        
                        // Bottom spacing untuk sticky button
                        item {
                            Spacer(modifier = Modifier.height(120.dp))
                        }
                    }
                }
            }
        }
        
        // =============== STICKY CHECKOUT BUTTON ===============
        // Tombol Checkout sticky di bottom - hanya muncul jika cart tidak kosong
        if (!uiState.cart.isEmpty()) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                color = Color.White,
                shadowElevation = 16.dp,
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Button(
                        onClick = onCheckoutClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        enabled = !uiState.isUpdating,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryBlue
                        ),
                        shape = RoundedCornerShape(30.dp),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 8.dp
                        )
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (uiState.isUpdating) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color.White
                                )
                            } else {
                                Text(
                                    text = "Checkout Now",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    Icons.Default.ArrowForward,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    
    // Clear cart confirmation dialog
    if (showClearCartDialog) {
        AlertDialog(
            onDismissRequest = { showClearCartDialog = false },
            title = { Text("Clear Cart") },
            text = { Text("Are you sure you want to remove all items from your cart?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showClearCartDialog = false
                        viewModel.clearCart()
                    }
                ) {
                    Text("Clear All")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showClearCartDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

// ======================== PREVIEW WITH DUMMY DATA ========================

/**
 * Preview function untuk demonstrasi desain Cart Page
 * Menampilkan data dummy sesuai dengan desain di foto
 */
@Composable
fun CartScreenPreview() {
    val dummyCategory1 = Category(
        id = 1,
        name = "Whey Protein",
        description = "Premium whey protein",
        imageUrl = "https://images.unsplash.com/photo-1593095948071-474c5cc2989d?w=400",
        createdAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis()
    )
    
    val dummyCategory2 = Category(
        id = 2,
        name = "Pre-Workout",
        description = "Energy boosters",
        imageUrl = "https://images.unsplash.com/photo-1590956026096-e8b43de60a1f?w=400",
        createdAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis()
    )
    
    val dummyCategory3 = Category(
        id = 3,
        name = "Accessories",
        description = "Gym accessories",
        imageUrl = "https://images.unsplash.com/photo-1534438327276-14e5300c3a48?w=400",
        createdAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis()
    )
    
    val dummyProducts = listOf(
        Product(
            id = 1,
            name = "Whey Protein Isolate",
            price = 59.99,
            stock = 25,
            description = "Chocolate / 2.2kg",
            imageUrl = "https://images.unsplash.com/photo-1593095948071-474c5cc2989d?w=400",
            categoryId = 1,
            category = dummyCategory1,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            lastSyncTime = System.currentTimeMillis()
        ),
        Product(
            id = 2,
            name = "Pre-Workout Blast",
            price = 44.99,
            stock = 18,
            description = "Sour Apple / 30 Servings",
            imageUrl = "https://images.unsplash.com/photo-1590956026096-e8b43de60a1f?w=400",
            categoryId = 2,
            category = dummyCategory2,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            lastSyncTime = System.currentTimeMillis()
        ),
        Product(
            id = 3,
            name = "MuscleShaker Elite",
            price = 19.99,
            stock = 5,
            description = "Matte Black / 750ml",
            imageUrl = "https://images.unsplash.com/photo-1534438327276-14e5300c3a48?w=400",
            categoryId = 3,
            category = dummyCategory3,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            lastSyncTime = System.currentTimeMillis()
        )
    )
    
    val dummyCartItems = listOf(
        CartItem(productId = 1, product = dummyProducts[0], quantity = 1),
        CartItem(productId = 2, product = dummyProducts[1], quantity = 2),
        CartItem(productId = 3, product = dummyProducts[2], quantity = 1)
    )
    
    val dummyCart = Cart(items = dummyCartItems)
    
    val dummyUiState = CartUiState(
        cart = dummyCart,
        cartItemCount = 3,
        isLoading = false,
        isUpdating = false,
        updatingItems = emptySet(),
        error = null
    )
    
    CartScreenContent(
        uiState = dummyUiState,
        onCheckoutClick = {},
        onContinueShoppingClick = {},
        onQuantityChange = { _, _ -> },
        onRemoveClick = {},
        onClearCart = {},
        onClearError = {}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CartScreenContent(
    uiState: CartUiState,
    onCheckoutClick: () -> Unit,
    onContinueShoppingClick: () -> Unit,
    onQuantityChange: (Int, Int) -> Unit,
    onRemoveClick: (Int) -> Unit,
    onClearCart: () -> Unit,
    onClearError: () -> Unit
) {
    var showClearCartDialog by remember { mutableStateOf(false) }
    var promoCode by remember { mutableStateOf("") }
    val error = uiState.error
    
    val backgroundColor = Color(0xFFF5F5F5)
    val primaryBlue = Color(0xFF1976D2)
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Custom Top Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onContinueShoppingClick) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color(0xFF212121)
                            )
                        }
                        
                        IconButton(onClick = { showClearCartDialog = true }) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = "Menu",
                                tint = Color(0xFF212121)
                            )
                        }
                    }
                    
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Your Cart",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF212121)
                        )
                        
                        if (uiState.cart.isNotEmpty()) {
                            Text(
                                text = "${uiState.cart.getTotalItems()} ITEMS SELECTED",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = primaryBlue,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
            
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = primaryBlue)
                    }
                }
                
                error != null -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Error",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Text(
                                text = error,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            Button(
                                onClick = onClearError,
                                modifier = Modifier.padding(top = 8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = primaryBlue
                                )
                            ) {
                                Text("Dismiss")
                            }
                        }
                    }
                }
                
                uiState.cart.isEmpty() -> {
                    // Empty cart state
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.ShoppingCart,
                            contentDescription = "Empty cart",
                            modifier = Modifier.size(120.dp),
                            tint = Color(0xFFBDBDBD)
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Text(
                            text = "Your cart is empty",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF424242)
                        )
                        
                        Text(
                            text = "Add some products to get started",
                            fontSize = 16.sp,
                            color = Color(0xFF757575),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
                        )
                        
                        Button(
                            onClick = onContinueShoppingClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = primaryBlue
                            ),
                            shape = RoundedCornerShape(28.dp)
                        ) {
                            Text("Continue Shopping", fontSize = 16.sp)
                        }
                    }
                }
                
                else -> {
                    // Cart with items
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Cart items
                        items(
                            items = uiState.cart.items,
                            key = { it.productId }
                        ) { cartItem ->
                            CartItemCard(
                                cartItem = cartItem,
                                onQuantityChange = { newQuantity ->
                                    onQuantityChange(cartItem.productId, newQuantity)
                                },
                                onRemoveClick = {
                                    onRemoveClick(cartItem.productId)
                                },
                                isUpdating = uiState.updatingItems.contains(cartItem.productId)
                            )
                        }
                        
                        // Promo Code Section
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp)
                                ) {
                                    Text(
                                        text = "PROMO CODE",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF9E9E9E),
                                        letterSpacing = 1.sp
                                    )
                                    
                                    Spacer(modifier = Modifier.height(12.dp))
                                    
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        OutlinedTextField(
                                            value = promoCode,
                                            onValueChange = { promoCode = it },
                                            modifier = Modifier.weight(1f),
                                            placeholder = {
                                                Text(
                                                    "Enter code (e.g. GAINS20)",
                                                    fontSize = 14.sp,
                                                    color = Color(0xFFBDBDBD)
                                                )
                                            },
                                            singleLine = true,
                                            colors = OutlinedTextFieldDefaults.colors(
                                                unfocusedBorderColor = Color(0xFFE0E0E0),
                                                focusedBorderColor = primaryBlue
                                            ),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        
                                        Button(
                                            onClick = { /* TODO: Apply promo */ },
                                            modifier = Modifier
                                                .height(56.dp)
                                                .widthIn(min = 100.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = primaryBlue
                                            ),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                "APPLY",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        
                        // Summary Section
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp)
                                ) {
                                    // Total Amount
                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        color = Color(0xFFF0F8FF),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Total Amount",
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF212121)
                                            )
                                            Text(
                                                text = uiState.cart.getFormattedTotalPrice(),
                                                fontSize = 24.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = primaryBlue
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        
                        // Trust Badges
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Shield,
                                    contentDescription = "Secure",
                                    tint = Color(0xFFBDBDBD),
                                    modifier = Modifier.size(40.dp)
                                )
                                Icon(
                                    Icons.Default.CreditCard,
                                    contentDescription = "Payment",
                                    tint = Color(0xFFBDBDBD),
                                    modifier = Modifier.size(40.dp)
                                )
                                Icon(
                                    Icons.Default.LocalShipping,
                                    contentDescription = "Delivery",
                                    tint = Color(0xFFBDBDBD),
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                            
                            Text(
                                text = "SECURE 256-BIT ENCRYPTED CHECKOUT",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFFBDBDBD),
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                letterSpacing = 0.5.sp
                            )
                        }
                        
                        // Checkout Button
                        item {
                            Button(
                                onClick = onCheckoutClick,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(60.dp),
                                enabled = !uiState.isUpdating,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = primaryBlue
                                ),
                                shape = RoundedCornerShape(30.dp)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (uiState.isUpdating) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(24.dp),
                                            color = Color.White
                                        )
                                    } else {
                                        Text(
                                            text = "Checkout Now",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Icon(
                                            Icons.Default.ArrowForward,
                                            contentDescription = null,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            }
                        }
                        
                        // Bottom spacing
                        item {
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                }
            }
        }
    }
    
    // Clear cart confirmation dialog
    if (showClearCartDialog) {
        AlertDialog(
            onDismissRequest = { showClearCartDialog = false },
            title = { Text("Clear Cart") },
            text = { Text("Are you sure you want to remove all items from your cart?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showClearCartDialog = false
                        onClearCart()
                    }
                ) {
                    Text("Clear All")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showClearCartDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}