package com.gymecommerce.musclecart.presentation.product.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.gymecommerce.musclecart.domain.model.Product
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductCard(
    product: Product,
    onProductClick: (Product) -> Unit,
    onAddToCartClick: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currencyFormatter = remember { NumberFormat.getCurrencyInstance(Locale("id", "ID")) }
    
    Card(
        onClick = { onProductClick(product) },
        modifier = modifier
            .fillMaxWidth()
            .height(320.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Product Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Color(0xFFF8F9FA))
            ) {
                val fallbackUrl = "https://ui-avatars.com/api/?name=${java.net.URLEncoder.encode(product.name.take(2), "UTF-8")}&background=1976D2&color=fff&size=400&bold=true"
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(product.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = product.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(2.dp)
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                    contentScale = ContentScale.Fit,
                    error = rememberAsyncImagePainter(
                        model = ImageRequest.Builder(context)
                            .data(fallbackUrl)
                            .crossfade(true)
                            .build()
                    ),
                    placeholder = rememberAsyncImagePainter(
                        model = ImageRequest.Builder(context)
                            .data(fallbackUrl)
                            .build()
                    )
                )
                
                // Stock indicator
                if (product.stock <= 5 && product.stock > 0) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp),
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "Low Stock",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                } else if (product.stock == 0) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp),
                        color = MaterialTheme.colorScheme.error,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "Out of Stock",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onError
                        )
                    }
                }
            }
            
            // Product Info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = product.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.height(36.dp)
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = currencyFormatter.format(product.price),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                if (product.totalReviews > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val fullStars = product.avgRating.toInt()
                        val hasHalf = product.avgRating - fullStars >= 0.5
                        repeat(5) { i ->
                            Icon(
                                imageVector = if (i < fullStars || (i == fullStars && hasHalf)) Icons.Filled.Star else Icons.Outlined.StarOutline,
                                contentDescription = null,
                                tint = Color(0xFFFFC107),
                                modifier = Modifier.size(12.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "(${product.totalReviews})",
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Add to Cart Button
                FilledTonalButton(
                    onClick = { onAddToCartClick(product) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = product.stock > 0,
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (product.stock > 0) "Add to Cart" else "Out of Stock",
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}