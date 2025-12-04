package com.apkfood.wavesoffoodadmin.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

object ImageUtils {
    
    fun loadBase64Image(imageView: ImageView, base64String: String) {
        try {
            if (base64String.isNotEmpty() && base64String.startsWith("data:image")) {
                // Remove data:image/jpeg;base64, prefix
                val base64Data = base64String.substring(base64String.indexOf(",") + 1)
                val decodedBytes = Base64.decode(base64Data, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                
                Glide.with(imageView.context)
                    .load(bitmap)
                    .centerCrop()
                    .into(imageView)
            } else {
                // Fallback to placeholder
                Glide.with(imageView.context)
                    .load(android.R.drawable.ic_menu_gallery)
                    .into(imageView)
            }
        } catch (e: Exception) {
            // Error loading image, show placeholder
            Glide.with(imageView.context)
                .load(android.R.drawable.ic_menu_gallery)
                .into(imageView)
        }
    }
    
    fun loadBase64ImageRounded(imageView: ImageView, base64String: String, cornerRadius: Int = 12) {
        try {
            if (base64String.isNotEmpty() && base64String.startsWith("data:image")) {
                val base64Data = base64String.substring(base64String.indexOf(",") + 1)
                val decodedBytes = Base64.decode(base64Data, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                
                Glide.with(imageView.context)
                    .load(bitmap)
                    .centerCrop()
                    .apply(RequestOptions.bitmapTransform(RoundedCorners(cornerRadius)))
                    .into(imageView)
            } else {
                Glide.with(imageView.context)
                    .load(android.R.drawable.ic_menu_gallery)
                    .apply(RequestOptions.bitmapTransform(RoundedCorners(cornerRadius)))
                    .into(imageView)
            }
        } catch (e: Exception) {
            Glide.with(imageView.context)
                .load(android.R.drawable.ic_menu_gallery)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(cornerRadius)))
                .into(imageView)
        }
    }
}
