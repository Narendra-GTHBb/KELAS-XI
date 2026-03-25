package com.gymecommerce.musclecart.presentation.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumHeader(
    currentRoute: String,
    cartItemCount: Int = 0,
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onMenuItemClick: (String) -> Unit = {},
    onLogout: () -> Unit = {}
) {
    var showMenu by remember { mutableStateOf(false) }
    val primaryBlue = Color(0xFF1976D2)
    val isHomePage = currentRoute == "home"
    
    Column {
        // Header Surface
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(10f),
            color = Color.White,
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left section - Back button or Logo
                if (!isHomePage) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF212121)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    // Small home icon for non-home pages
                    IconButton(
                        onClick = onHomeClick,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.Home,
                            contentDescription = "Home",
                            tint = primaryBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                }
                
                // Logo
                Text(
                    text = "MuscleCart",
                    fontSize = if (isHomePage) 24.sp else 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryBlue,
                    modifier = Modifier.weight(1f)
                )
                
                // Right section - Search, Cart, Menu
                IconButton(
                    onClick = onSearchClick,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color(0xFF757575)
                    )
                }
                
                // Cart with badge
                BadgedBox(
                    badge = {
                        if (cartItemCount > 0) {
                            Badge(
                                containerColor = primaryBlue,
                                contentColor = Color.White
                            ) {
                                Text(
                                    text = cartItemCount.toString(),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                ) {
                    IconButton(
                        onClick = onCartClick,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            Icons.Default.ShoppingCart,
                            contentDescription = "Cart",
                            tint = Color(0xFF757575)
                        )
                    }
                }
                
                // Menu
                IconButton(
                    onClick = { showMenu = !showMenu },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = Color(0xFF757575)
                    )
                }
            }
        }
        
        // Dropdown Menu
        AnimatedVisibility(
            visible = showMenu,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .zIndex(9f),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    MenuItem(
                        icon = Icons.Default.Home,
                        title = "Home",
                        onClick = {
                            showMenu = false
                            onMenuItemClick("home")
                        }
                    )
                    
                    MenuItem(
                        icon = Icons.Default.Storefront,
                        title = "Shop",
                        onClick = {
                            showMenu = false
                            onMenuItemClick("shop")
                        }
                    )
                    
                    MenuItem(
                        icon = Icons.Default.Category,
                        title = "Categories",
                        onClick = {
                            showMenu = false
                            onMenuItemClick("categories")
                        }
                    )
                    
                    MenuItem(
                        icon = Icons.Default.Receipt,
                        title = "Orders",
                        onClick = {
                            showMenu = false
                            onMenuItemClick("orders")
                        }
                    )
                    
                    MenuItem(
                        icon = Icons.Default.Person,
                        title = "Profile",
                        onClick = {
                            showMenu = false
                            onMenuItemClick("profile")
                        }
                    )
                    
                    Divider(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        color = Color(0xFFE0E0E0)
                    )
                    
                    MenuItem(
                        icon = Icons.Default.Logout,
                        title = "Logout",
                        onClick = {
                            showMenu = false
                            onLogout()
                        },
                        textColor = Color(0xFFE53935)
                    )
                }
            }
        }
        
        // Overlay to close menu when clicking outside
        if (showMenu) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        showMenu = false
                    }
            )
        }
    }
}

@Composable
private fun MenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit,
    textColor: Color = Color(0xFF212121)
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = title,
            tint = if (textColor == Color(0xFFE53935)) textColor else Color(0xFF757575),
            modifier = Modifier.size(24.dp)
        )
        
        Text(
            text = title,
            fontSize = 16.sp,
            color = textColor,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}