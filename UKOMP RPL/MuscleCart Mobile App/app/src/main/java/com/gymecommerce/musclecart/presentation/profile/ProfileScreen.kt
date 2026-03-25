package com.gymecommerce.musclecart.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gymecommerce.musclecart.presentation.auth.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onHomeClick: (() -> Unit)? = null,
    onOrderHistoryClick: () -> Unit = {},
    onWishlistClick: () -> Unit = {},
    onTrackPackagesClick: () -> Unit = {},
    onPersonalDetailsClick: () -> Unit = {},
    onAddressClick: () -> Unit = {},
    onPaymentsClick: () -> Unit = {},
    onHelpCenterClick: () -> Unit = {},
    onPrivacyClick: () -> Unit = {},
    onPointsHistoryClick: () -> Unit = {},
    onChangePasswordClick: () -> Unit = {},
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val primaryBlue = Color(0xFF1976D2)
    val backgroundColor = Color(0xFFF5F5F5)

    // Refresh user data from server every time screen opens so points stay current
    LaunchedEffect(Unit) {
        viewModel.refreshUser()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // Custom Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 1.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Back button
                IconButton(
                    onClick = { onHomeClick?.invoke() },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF757575)
                    )
                }
                
                // Title
                Text(
                    text = "PROFILE",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF757575),
                    modifier = Modifier.align(Alignment.Center),
                    letterSpacing = 1.sp
                )
                
                // Settings button
                IconButton(
                    onClick = { /* TODO: Settings */ },
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color(0xFF757575)
                    )
                }
            }
        }
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Profile Avatar & Info
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Avatar with verified badge
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clickable { onPersonalDetailsClick() }
                    ) {
                        // Avatar background
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFB07A)), // Peach color
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "Profile",
                                modifier = Modifier.size(60.dp),
                                tint = Color.White
                            )
                        }
                        
                        // Edit icon overlay (bottom-start)
                        Surface(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .offset(x = 4.dp, y = (-4).dp),
                            shape = CircleShape,
                            color = Color.White,
                            shadowElevation = 3.dp
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit Profile",
                                modifier = Modifier
                                    .size(28.dp)
                                    .padding(5.dp),
                                tint = primaryBlue
                            )
                        }
                        
                        // Verified badge
                        Surface(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .offset(x = (-4).dp, y = (-4).dp),
                            shape = CircleShape,
                            color = primaryBlue,
                            shadowElevation = 2.dp
                        ) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "Verified",
                                modifier = Modifier
                                    .size(24.dp)
                                    .padding(4.dp),
                                tint = Color.White
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Name with ELITE badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = uiState.currentUser?.name ?: "Alex Rivera",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF212121)
                        )
                        
                        Surface(
                            color = primaryBlue,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "ELITE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                    
                    // Email
                    Text(
                        text = uiState.currentUser?.email ?: "rivera.alex@musclecart.io",
                        fontSize = 14.sp,
                        color = Color(0xFF757575),
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Edit Profile button
                    OutlinedButton(
                        onClick = onPersonalDetailsClick,
                        shape = RoundedCornerShape(20.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = primaryBlue
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Edit Profile",
                            fontSize = 13.sp,
                            color = primaryBlue,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            
            // Reward Points Card
            item {
                val userPoints = uiState.currentUser?.points ?: 0
                Surface(
                    modifier = Modifier.fillMaxWidth().clickable { onPointsHistoryClick() },
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFFFF8E1),
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFF59E0B)
                        ) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = "Points",
                                modifier = Modifier
                                    .size(44.dp)
                                    .padding(10.dp),
                                tint = Color.White
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Reward Points",
                                fontSize = 12.sp,
                                color = Color(0xFF757575)
                            )
                            Text(
                                "$userPoints poin",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFD97706)
                            )
                            Text(
                                "= Rp ${String.format("%,d", userPoints / 10).replace(',', '.')}",
                                fontSize = 12.sp,
                                color = Color(0xFF9E9E9E)
                            )
                        }
                    }
                }
            }

            // SHOPPING Section
            item {
                ProfileSection(
                    title = "SHOPPING",
                    items = listOf(
                        ProfileMenuItem(
                            icon = Icons.Default.ShoppingBag,
                            title = "Order History",
                            onClick = onOrderHistoryClick
                        ),
                        ProfileMenuItem(
                            icon = Icons.Default.Favorite,
                            title = "Wishlist",
                            onClick = onWishlistClick
                        ),
                        ProfileMenuItem(
                            icon = Icons.Default.LocalShipping,
                            title = "Track Packages",
                            onClick = onTrackPackagesClick
                        )
                    )
                )
            }
            
            // ACCOUNT Section
            item {
                ProfileSection(
                    title = "ACCOUNT",
                    items = listOf(
                        ProfileMenuItem(
                            icon = Icons.Default.Person,
                            title = "Personal Details",
                            onClick = onPersonalDetailsClick
                        ),
                        ProfileMenuItem(
                            icon = Icons.Default.LocationOn,
                            title = "Alamat Pengiriman",
                            onClick = onAddressClick
                        ),
                        ProfileMenuItem(
                            icon = Icons.Default.Lock,
                            title = "Ubah Password",
                            onClick = onChangePasswordClick
                        ),
                        ProfileMenuItem(
                            icon = Icons.Default.CreditCard,
                            title = "Payments & Billing",
                            onClick = onPaymentsClick
                        )
                    )
                )
            }
            
            // SUPPORT Section
            item {
                ProfileSection(
                    title = "SUPPORT",
                    items = listOf(
                        ProfileMenuItem(
                            icon = Icons.Default.Help,
                            title = "Help Center",
                            onClick = onHelpCenterClick
                        ),
                        ProfileMenuItem(
                            icon = Icons.Default.Security,
                            title = "Privacy & Terms",
                            onClick = onPrivacyClick
                        )
                    )
                )
            }
            
            // Sign Out Button
            item {
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "SIGN OUT",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF757575),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickableNoRipple {
                            viewModel.logout()
                            onLogout()
                        }
                        .padding(vertical = 16.dp),
                    textAlign = TextAlign.Center,
                    letterSpacing = 1.sp
                )
            }
            
            // Version
            item {
                Text(
                    text = "MUSCLECART V3.4.0",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFBDBDBD),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.5.sp
                )
                
                Spacer(modifier = Modifier.height(80.dp)) // Bottom navigation space
            }
        }
    }
}

@Composable
private fun ProfileSection(
    title: String,
    items: List<ProfileMenuItem>
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF9E9E9E),
            modifier = Modifier.padding(bottom = 12.dp),
            letterSpacing = 1.sp
        )
        
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shape = RoundedCornerShape(12.dp),
            shadowElevation = 0.5.dp
        ) {
            Column {
                items.forEachIndexed { index, item ->
                    ProfileMenuRow(
                        icon = item.icon,
                        title = item.title,
                        onClick = item.onClick
                    )
                    
                    if (index < items.size - 1) {
                        Divider(
                            color = Color(0xFFF0F0F0),
                            thickness = 1.dp,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileMenuRow(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickableNoRipple { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = title,
            tint = Color(0xFF757575),
            modifier = Modifier.size(24.dp)
        )
        
        Text(
            text = title,
            fontSize = 16.sp,
            color = Color(0xFF212121),
            modifier = Modifier
                .padding(start = 16.dp)
                .weight(1f)
        )
        
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = "Navigate",
            tint = Color(0xFFBDBDBD),
            modifier = Modifier.size(20.dp)
        )
    }
}

data class ProfileMenuItem(
    val icon: ImageVector,
    val title: String,
    val onClick: () -> Unit
)

// Helper extension for click without ripple
@Composable
private fun Modifier.clickableNoRipple(onClick: () -> Unit) = this.then(
    Modifier.clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() }
    ) { onClick() }
)
