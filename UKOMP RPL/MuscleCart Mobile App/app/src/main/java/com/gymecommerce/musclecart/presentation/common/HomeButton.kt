package com.gymecommerce.musclecart.presentation.common

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Reusable Home Button Component
 * 
 * A professional, elegant home button that can be placed on any screen.
 * Follows the design specifications:
 * - Position: Top-left corner
 * - Border radius: 10dp (8-12dp range)
 * - Background: Light gray or soft blue
 * - Accent color: Sporty blue (#1976D2)
 * - Smooth transitions (0.2-0.3s)
 * - Hover effect with subtle shadow
 * 
 * @param onClick Callback when the button is clicked
 * @param modifier Optional modifier for custom positioning
 * @param enabled Whether the button is enabled (default: true)
 * @param iconOnly Show only icon without text (default: false)
 */
@Composable
fun HomeButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    iconOnly: Boolean = false
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        label = "homeButtonScale"
    )
    
    val sportyBlue = Color(0xFF1976D2)
    val softBlueBackground = Color(0xFFE3F2FD)
    
    if (iconOnly) {
        // Icon-only button
        IconButton(
            onClick = {
                if (enabled) {
                    isPressed = true
                    onClick()
                }
            },
            modifier = modifier.scale(scale),
            enabled = enabled
        ) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "Home",
                tint = if (enabled) sportyBlue else Color.Gray
            )
        }
    } else {
        // Full button with text
        Surface(
            onClick = {
                if (enabled) {
                    isPressed = true
                    onClick()
                }
            },
            modifier = modifier.scale(scale),
            enabled = enabled,
            shape = RoundedCornerShape(10.dp),
            color = if (enabled) softBlueBackground else Color(0xFFE0E0E0),
            shadowElevation = if (isPressed) 1.dp else 3.dp,
            tonalElevation = 0.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home",
                    tint = if (enabled) sportyBlue else Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Home",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (enabled) sportyBlue else Color.Gray
                )
            }
        }
    }
    
    // Reset pressed state after animation
    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(200)
            isPressed = false
        }
    }
}

/**
 * Home Button with default top-left positioning
 * 
 * This variant is pre-positioned for top-left corner placement
 * and includes default padding.
 * 
 * @param onClick Callback when the button is clicked
 * @param enabled Whether the button is enabled (default: true)
 */
@Composable
fun TopLeftHomeButton(
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    HomeButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.padding(16.dp)
    )
}
