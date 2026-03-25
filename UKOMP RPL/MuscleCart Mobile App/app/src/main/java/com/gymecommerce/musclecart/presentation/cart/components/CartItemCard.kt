package com.gymecommerce.musclecart.presentation.cart.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
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
import com.gymecommerce.musclecart.domain.model.CartItem
import com.gymecommerce.musclecart.presentation.components.OptimizedProductThumbnail

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartItemCard(
    cartItem: CartItem,
    onQuantityChange: (Int) -> Unit,
    onRemoveClick: () -> Unit,
    modifier: Modifier = Modifier,
    isUpdating: Boolean = false
) {
    val primaryBlue = Color(0xFF1976D2)
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Product Image with Badge
            Box(
                modifier = Modifier.size(100.dp)
            ) {
                OptimizedProductThumbnail(
                    imageUrl = cartItem.product.imageUrl,
                    productName = cartItem.product.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    shape = RoundedCornerShape(12.dp)
                )
                
                // Stock Badge
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(6.dp),
                    color = when {
                        cartItem.product.stock > 10 -> Color(0xFF4CAF50)
                        cartItem.product.stock > 0 -> Color(0xFFFF9800)
                        else -> Color(0xFFF44336)
                    },
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = when {
                            cartItem.product.stock > 10 -> "IN STOCK"
                            cartItem.product.stock > 0 -> "LOW STOCK"
                            else -> "OUT OF STOCK"
                        },
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }
            
            // Product Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Product Name
                Text(
                    text = cartItem.product.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = Color(0xFF212121)
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Product Description/Variant
                Text(
                    text = cartItem.product.category?.name ?: "MuscleCart Product",
                    fontSize = 13.sp,
                    color = Color(0xFF9E9E9E),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Price and Quantity Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    // Price
                    Text(
                        text = cartItem.product.getFormattedPrice(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryBlue
                    )
                    
                    // Quantity Selector
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFFF5F5F5)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            IconButton(
                                onClick = {
                                    if (cartItem.quantity > 1) {
                                        onQuantityChange(cartItem.quantity - 1)
                                    }
                                },
                                enabled = !isUpdating && cartItem.quantity > 1,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    Icons.Default.Remove,
                                    contentDescription = "Decrease",
                                    tint = if (cartItem.quantity > 1) Color(0xFF212121) else Color(0xFFBDBDBD),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            
                            Text(
                                text = cartItem.quantity.toString(),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF212121),
                                modifier = Modifier.widthIn(min = 24.dp)
                            )
                            
                            IconButton(
                                onClick = {
                                    if (cartItem.quantity < cartItem.product.stock) {
                                        onQuantityChange(cartItem.quantity + 1)
                                    }
                                },
                                enabled = !isUpdating && cartItem.quantity < cartItem.product.stock,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Increase",
                                    tint = if (cartItem.quantity < cartItem.product.stock) primaryBlue else Color(0xFFBDBDBD),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
            
            // Delete Button
            IconButton(
                onClick = onRemoveClick,
                enabled = !isUpdating,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Remove from cart",
                    tint = Color(0xFF9E9E9E),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        
        // Loading indicator
        if (isUpdating) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = primaryBlue
            )
        }
    }
}