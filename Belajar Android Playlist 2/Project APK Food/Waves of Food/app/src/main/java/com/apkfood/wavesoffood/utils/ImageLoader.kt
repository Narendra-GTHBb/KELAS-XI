package com.apkfood.wavesoffood.utils

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
import com.apkfood.wavesoffood.R

/**
 * Enhanced ImageLoader with token support and comprehensive Base64 handling
 */
object ImageLoader {
    
    private const val API_TOKEN = "your_api_token_here" // Replace with actual token if needed
    
    /**
     * Load gambar dengan Glide dengan rounded corners - supports Base64 dan URLs dengan token
     */
    fun loadImage(context: Context, imageUrl: String, imageView: ImageView, cornerRadius: Int = 16) {
        try {
            Log.d("ImageLoader", "🖼️ Loading image: ${imageUrl.take(50)}...")
            
            when {
                imageUrl.isEmpty() -> {
                    Log.w("ImageLoader", "⚠️ Empty image URL")
                    loadPlaceholder(context, imageView, cornerRadius)
                }
                imageUrl.startsWith("data:image") -> {
                    Log.d("ImageLoader", "🔄 Loading Base64 image")
                    loadBase64Image(context, imageView, imageUrl, cornerRadius)
                }
                imageUrl.startsWith("http") -> {
                    Log.d("ImageLoader", "🌐 Loading URL image with token")
                    loadFromUrlWithToken(context, imageView, imageUrl, cornerRadius)
                }
                else -> {
                    Log.w("ImageLoader", "❓ Unknown image format, trying as URL")
                    loadFromUrlWithToken(context, imageView, imageUrl, cornerRadius)
                }
            }
        } catch (e: Exception) {
            Log.e("ImageLoader", "❌ Error loading image", e)
            loadPlaceholder(context, imageView, cornerRadius)
        }
    }
    
    private fun loadBase64Image(context: Context, imageView: ImageView, base64String: String, cornerRadius: Int) {
        try {
            // Remove data:image/jpeg;base64, prefix
            val commaIndex = base64String.indexOf(",")
            if (commaIndex == -1) {
                Log.e("ImageLoader", "❌ Invalid Base64 format")
                loadPlaceholder(context, imageView, cornerRadius)
                return
            }
            
            val base64Data = base64String.substring(commaIndex + 1)
            Log.d("ImageLoader", "📏 Base64 data length: ${base64Data.length}")
            
            val decodedBytes = Base64.decode(base64Data, Base64.DEFAULT)
            Log.d("ImageLoader", "📏 Decoded bytes: ${decodedBytes.size}")
            
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            
            if (bitmap != null) {
                Log.d("ImageLoader", "✅ Base64 decoded successfully: ${bitmap.width}x${bitmap.height}")
                
                Glide.with(context)
                    .load(bitmap)
                    .apply(
                        RequestOptions()
                            .placeholder(R.drawable.ic_food)
                            .error(R.drawable.ic_food)
                            .transform(RoundedCorners(cornerRadius))
                            .centerCrop()
                    )
                    .into(imageView)
            } else {
                Log.e("ImageLoader", "❌ Failed to decode Base64 to bitmap")
                loadPlaceholder(context, imageView, cornerRadius)
            }
            
        } catch (e: Exception) {
            Log.e("ImageLoader", "❌ Error decoding Base64", e)
            loadPlaceholder(context, imageView, cornerRadius)
        }
    }
    
    private fun loadFromUrlWithToken(context: Context, imageView: ImageView, imageUrl: String, cornerRadius: Int) {
        try {
            // Create URL with headers if token is needed
            val glideUrl = GlideUrl(imageUrl, LazyHeaders.Builder()
                .addHeader("Authorization", "Bearer $API_TOKEN")
                .addHeader("User-Agent", "WavesOfFood/1.0")
                .build())
            
            Glide.with(context)
                .load(glideUrl)
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.ic_food)
                        .error(R.drawable.ic_food)
                        .transform(RoundedCorners(cornerRadius))
                        .centerCrop()
                )
                .into(imageView)
                
            Log.d("ImageLoader", "🌐 URL image loaded with token")
            
        } catch (e: Exception) {
            Log.e("ImageLoader", "❌ Error loading URL image", e)
            loadPlaceholder(context, imageView, cornerRadius)
        }
    }
    
    private fun loadPlaceholder(context: Context, imageView: ImageView, cornerRadius: Int) {
        Glide.with(context)
            .load(R.drawable.ic_food)
            .apply(
                RequestOptions()
                    .transform(RoundedCorners(cornerRadius))
            )
            .into(imageView)
    }
    
    /**
     * Load gambar sederhana tanpa context (menggunakan context dari ImageView)
     */
    fun loadImage(imageView: ImageView, imageUrl: String) {
        loadImage(imageView.context, imageUrl, imageView, 16)
    }
    
    /**
     * Load gambar circular untuk profile
     */
    fun loadCircularImage(context: Context, imageUrl: String, imageView: ImageView) {
        try {
            when {
                imageUrl.isEmpty() -> {
                    loadCircularPlaceholder(context, imageView)
                }
                imageUrl.startsWith("data:image") -> {
                    loadCircularBase64Image(context, imageView, imageUrl)
                }
                else -> {
                    loadCircularFromUrl(context, imageView, imageUrl)
                }
            }
        } catch (e: Exception) {
            loadCircularPlaceholder(context, imageView)
        }
    }
    
    private fun loadCircularBase64Image(context: Context, imageView: ImageView, base64String: String) {
        try {
            val base64Data = base64String.substring(base64String.indexOf(",") + 1)
            val decodedBytes = Base64.decode(base64Data, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            
            Glide.with(context)
                .load(bitmap)
                .apply(RequestOptions().circleCrop())
                .into(imageView)
        } catch (e: Exception) {
            loadCircularPlaceholder(context, imageView)
        }
    }
    
    private fun loadCircularFromUrl(context: Context, imageView: ImageView, imageUrl: String) {
        Glide.with(context)
            .load(imageUrl)
            .apply(RequestOptions().circleCrop())
            .into(imageView)
    }
    
    private fun loadCircularPlaceholder(context: Context, imageView: ImageView) {
        Glide.with(context)
            .load(R.drawable.ic_food)
            .apply(RequestOptions().circleCrop())
            .into(imageView)
    }
}
