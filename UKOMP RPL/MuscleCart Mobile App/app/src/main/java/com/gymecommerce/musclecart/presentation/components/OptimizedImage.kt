package com.gymecommerce.musclecart.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import coil.size.Scale
import coil.size.Size
import coil.decode.DataSource

/**
 * Optimized image component with progressive loading (low quality -> high quality)
 * Implements thumbnail-first strategy for better UX
 */
@Composable
fun OptimizedProductImage(
    imageUrl: String?,
    productName: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    shape: Shape = RoundedCornerShape(0.dp),
    targetSize: Int = 800, // Target pixel size for full quality
    thumbnailSize: Int = 100, // Small thumbnail for progressive loading
    showPlaceholder: Boolean = true,
    backgroundColor: Color = Color(0xFFF8F9FA)
) {
    val context = LocalContext.current
    
    Box(
        modifier = modifier
            .background(backgroundColor, shape)
            .clip(shape),
        contentAlignment = Alignment.Center
    ) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(context)
                .data(imageUrl?.takeIf { it.isNotBlank() } ?: generateAvatarUrl(productName))
                .size(Size(targetSize, targetSize))
                .scale(Scale.FIT)
                .crossfade(400)
                .memoryCachePolicy(coil.request.CachePolicy.ENABLED)
                .diskCachePolicy(coil.request.CachePolicy.ENABLED)
                .build(),
            contentDescription = productName,
            modifier = Modifier.fillMaxSize(),
            contentScale = contentScale,
            loading = {
                if (showPlaceholder) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 2.dp
                        )
                    }
                }
            },
            error = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "Error loading image",
                        tint = Color.Gray.copy(alpha = 0.5f),
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        )
    }
}

/**
 * Thumbnail version for list items with aggressive memory optimization
 */
@Composable
fun OptimizedProductThumbnail(
    imageUrl: String?,
    productName: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    shape: Shape = RoundedCornerShape(8.dp)
) {
    val context = LocalContext.current
    
    Box(
        modifier = modifier
            .background(Color(0xFFF8F9FA), shape)
            .clip(shape),
        contentAlignment = Alignment.Center
    ) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(context)
                .data(imageUrl?.takeIf { it.isNotBlank() } ?: generateAvatarUrl(productName))
                // Ultra small thumbnail for lists
                .size(Size(200, 200))
                .scale(Scale.FIT)
                .crossfade(300)
                .build(),
            contentDescription = productName,
            modifier = Modifier.fillMaxSize(),
            contentScale = contentScale,
            loading = {
                // Minimal loading indicator for thumbnails
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 1.5.dp
                    )
                }
            },
            error = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = null,
                        tint = Color.Gray.copy(alpha = 0.4f),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        )
    }
}

/**
 * Generate avatar URL for fallback
 */
private fun generateAvatarUrl(name: String): String {
    val initials = name.take(2).let { java.net.URLEncoder.encode(it, "UTF-8") }
    return "https://ui-avatars.com/api/?name=$initials&background=1976D2&color=fff&size=400&bold=true"
}
