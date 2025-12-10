package com.apkfood.wavesoffoodadmin.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.apkfood.wavesoffoodadmin.R

/**
 * Enhanced ImageLoader with token support and Base64 decoding
 */
object ImageLoaderAdmin {
    
    private const val API_TOKEN = "your_api_token_here" // Replace with actual token if needed
    
    /**
     * Load image with Glide - supports Base64, URLs with tokens
     */
    fun loadImage(context: Context, imageUrl: String, imageView: ImageView, cornerRadius: Int = 16) {
        try {
            Log.d("ImageLoaderAdmin", "🖼️ Loading image: ${imageUrl.take(50)}...")
            
            when {
                imageUrl.isEmpty() -> {
                    Log.w("ImageLoaderAdmin", "⚠️ Empty image URL")
                    loadPlaceholder(context, imageView, cornerRadius)
                }
                imageUrl.startsWith("data:image") -> {
                    Log.d("ImageLoaderAdmin", "🔄 Loading Base64 image")
                    loadBase64Image(context, imageView, imageUrl, cornerRadius)
                }
                imageUrl.startsWith("http") -> {
                    Log.d("ImageLoaderAdmin", "🌐 Loading URL image with token")
                    loadFromUrlWithToken(context, imageView, imageUrl, cornerRadius)
                }
                else -> {
                    Log.w("ImageLoaderAdmin", "❓ Unknown image format, trying as URL")
                    loadFromUrlWithToken(context, imageView, imageUrl, cornerRadius)
                }
            }
        } catch (e: Exception) {
            Log.e("ImageLoaderAdmin", "❌ Error loading image", e)
            loadPlaceholder(context, imageView, cornerRadius)
        }
    }
    
    /**
     * Load Base64 encoded image
     */
    private fun loadBase64Image(context: Context, imageView: ImageView, base64String: String, cornerRadius: Int) {
        try {
            // Remove data:image/jpeg;base64, prefix
            val commaIndex = base64String.indexOf(",")
            if (commaIndex == -1) {
                Log.e("ImageLoaderAdmin", "❌ Invalid Base64 format")
                loadPlaceholder(context, imageView, cornerRadius)
                return
            }
            
            val base64Data = base64String.substring(commaIndex + 1)
            Log.d("ImageLoaderAdmin", "📏 Base64 data length: ${base64Data.length}")
            
            val decodedBytes = Base64.decode(base64Data, Base64.DEFAULT)
            Log.d("ImageLoaderAdmin", "📏 Decoded bytes: ${decodedBytes.size}")
            
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            
            if (bitmap != null) {
                Log.d("ImageLoaderAdmin", "✅ Base64 decoded successfully: ${bitmap.width}x${bitmap.height}")
                
                Glide.with(context)
                    .load(bitmap)
                    .apply(
                        RequestOptions()
                            .placeholder(android.R.drawable.ic_menu_gallery)
                            .error(android.R.drawable.ic_menu_gallery)
                            .transform(RoundedCorners(cornerRadius))
                    )
                    .into(imageView)
            } else {
                Log.e("ImageLoaderAdmin", "❌ Failed to decode Base64 to bitmap")
                loadPlaceholder(context, imageView, cornerRadius)
            }
            
        } catch (e: Exception) {
            Log.e("ImageLoaderAdmin", "❌ Error decoding Base64", e)
            loadPlaceholder(context, imageView, cornerRadius)
        }
    }
    
    /**
     * Load image from URL with authentication token
     */
    private fun loadFromUrlWithToken(context: Context, imageView: ImageView, imageUrl: String, cornerRadius: Int) {
        try {
            // Create URL with headers if token is needed
            val glideUrl = GlideUrl(imageUrl, LazyHeaders.Builder()
                .addHeader("Authorization", "Bearer $API_TOKEN")
                .addHeader("User-Agent", "WavesOfFoodAdmin/1.0")
                .build())
            
            Glide.with(context)
                .load(glideUrl)
                .apply(
                    RequestOptions()
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .error(android.R.drawable.ic_menu_gallery)
                        .transform(RoundedCorners(cornerRadius))
                )
                .into(imageView)
                
            Log.d("ImageLoaderAdmin", "🌐 URL image loaded with token")
            
        } catch (e: Exception) {
            Log.e("ImageLoaderAdmin", "❌ Error loading URL image", e)
            loadPlaceholder(context, imageView, cornerRadius)
        }
    }
    
    /**
     * Load placeholder image
     */
    private fun loadPlaceholder(context: Context, imageView: ImageView, cornerRadius: Int) {
        Log.d("ImageLoaderAdmin", "🖼️ Loading placeholder image")
        
        Glide.with(context)
            .load(android.R.drawable.ic_menu_gallery)
            .apply(
                RequestOptions()
                    .transform(RoundedCorners(cornerRadius))
            )
            .into(imageView)
    }
    
    /**
     * Simple load without corner radius
     */
    fun loadImage(context: Context, imageUrl: String, imageView: ImageView) {
        loadImage(context, imageUrl, imageView, 16)
    }
    
    /**
     * Test Base64 image decoding
     */
    fun testBase64Decoding(base64String: String): Boolean {
        return try {
            if (!base64String.startsWith("data:image")) {
                return false
            }
            
            val base64Data = base64String.substring(base64String.indexOf(",") + 1)
            val decodedBytes = Base64.decode(base64Data, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            
            bitmap != null
        } catch (e: Exception) {
            false
        }
    }
}