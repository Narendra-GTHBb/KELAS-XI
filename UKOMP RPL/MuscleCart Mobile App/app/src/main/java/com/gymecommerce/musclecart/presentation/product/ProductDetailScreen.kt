package com.gymecommerce.musclecart.presentation.product

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.gymecommerce.musclecart.presentation.common.HomeButton
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: Int,
    onNavigateBack: () -> Unit,
    onAddToCartSuccess: () -> Unit,
    onBuyNow: () -> Unit = {},
    onHomeClick: (() -> Unit)? = null,
    viewModel: ProductDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val currencyFormatter = remember { NumberFormat.getCurrencyInstance(Locale("id", "ID")) }
    var quantity by remember { mutableStateOf(1) }
    val snackbarHostState = remember { SnackbarHostState() }
    var isBuyingNow by remember { mutableStateOf(false) }
    val buyNowScope = rememberCoroutineScope()
    
    LaunchedEffect(productId) {
        viewModel.loadProduct(productId)
    }

    // Show success snackbar when product is added to cart
    LaunchedEffect(uiState.addToCartSuccess) {
        if (uiState.addToCartSuccess) {
            uiState.product?.let { product ->
                snackbarHostState.showSnackbar(
                    message = "✓ ${quantity}x ${product.name} added to cart!",
                    duration = SnackbarDuration.Short
                )
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Product Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (onHomeClick != null) {
                        HomeButton(
                            onClick = onHomeClick,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }
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
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                
                uiState.error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Error loading product",
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { viewModel.loadProduct(productId) }
                            ) {
                                Text("Retry")
                            }
                        }
                    }
                }
                
                uiState.product != null -> {
                    val product = uiState.product!!
                    
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                    // Product Image
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(product.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = product.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .padding(16.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    
                    // Product Info
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = product.name,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = currencyFormatter.format(product.price),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        // Rating
                        if (product.totalReviews > 0) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                val fullStars = product.avgRating.toInt()
                                val halfStar = (product.avgRating - fullStars) >= 0.5
                                for (i in 1..5) {
                                    Icon(
                                        imageVector = if (i <= fullStars) Icons.Filled.Star
                                                      else Icons.Outlined.StarOutline,
                                        contentDescription = null,
                                        tint = Color(0xFFFFC107),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Text(
                                    text = String.format("%.1f", product.avgRating),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "(${product.totalReviews} ulasan)",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))

                        // Stock Status
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Stock: ",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = when {
                                    product.stock > 10 -> "In Stock (${product.stock})"
                                    product.stock > 0 -> "Low Stock (${product.stock})"
                                    else -> "Out of Stock"
                                },
                                fontSize = 14.sp,
                                color = when {
                                    product.stock > 10 -> MaterialTheme.colorScheme.primary
                                    product.stock > 0 -> MaterialTheme.colorScheme.tertiary
                                    else -> MaterialTheme.colorScheme.error
                                },
                                fontWeight = FontWeight.Medium
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Description
                        Text(
                            text = "Description",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = product.description,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        // Quantity Selector and Add to Cart
                        if (product.stock > 0) {
                            Text(
                                text = "Quantity",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // Quantity Controls
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(
                                        onClick = { if (quantity > 1) quantity-- },
                                        enabled = quantity > 1
                                    ) {
                                        Icon(Icons.Default.Remove, contentDescription = "Decrease")
                                    }
                                    
                                    Text(
                                        text = quantity.toString(),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )
                                    
                                    IconButton(
                                        onClick = { if (quantity < product.stock) quantity++ },
                                        enabled = quantity < product.stock
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = "Increase")
                                    }
                                }
                                
                                // Total Price
                                Text(
                                    text = "Total: ${currencyFormatter.format(product.price * quantity.toDouble())}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            // Add to Cart Button
                            Button(
                                onClick = {
                                    viewModel.addToCart(product, quantity)
                                    onAddToCartSuccess()
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !uiState.isAddingToCart
                            ) {
                                if (uiState.isAddingToCart) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                                Text("Add to Cart")
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Buy Now Button
                            Button(
                                onClick = {
                                    buyNowScope.launch {
                                        isBuyingNow = true
                                        val (success, errorMsg) = viewModel.addToCartSuspend(product.id, quantity)
                                        isBuyingNow = false
                                        if (success) {
                                            onBuyNow()
                                        } else {
                                            android.widget.Toast.makeText(
                                                context,
                                                "Gagal: ${errorMsg ?: "Unknown error"}",
                                                android.widget.Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !uiState.isAddingToCart && !isBuyingNow,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary
                                )
                            ) {
                                if (uiState.isAddingToCart) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp,
                                        color = MaterialTheme.colorScheme.onSecondary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                                Text("Buy Now")
                            }
                        } else {
                            // Out of Stock
                            Button(
                                onClick = { },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = false
                            ) {
                                Text("Out of Stock")
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
                }
            }
        }
    }
}