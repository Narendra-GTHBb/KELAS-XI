package com.gymecommerce.musclecart

import android.app.Application
import android.util.Log
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import coil.util.DebugLogger
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.ConnectionPool
import okhttp3.Dispatcher
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class MuscleCartApplication : Application(), ImageLoaderFactory {
    
    override fun onCreate() {
        super.onCreate()
        Log.d("MuscleCart", "Application started - image cache preserved")
    }
    
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun newImageLoader(): ImageLoader {
        val loggingInterceptor = HttpLoggingInterceptor { message ->
            Log.d("CoilHttp", message)
        }.apply {
            level = HttpLoggingInterceptor.Level.HEADERS
        }
        
        // Limit OkHttp dispatcher to max 2 concurrent requests
        // php artisan serve is single-threaded, so we must serialize image requests
        val okHttpDispatcher = Dispatcher().apply {
            maxRequests = 2          // max 2 total concurrent requests
            maxRequestsPerHost = 2   // max 2 to same host
        }

        // Small connection pool - reuse 2 connections, keep alive 5 min
        val connectionPool = ConnectionPool(2, 5, TimeUnit.MINUTES)
        
        return ImageLoader.Builder(this)
            .okHttpClient {
                OkHttpClient.Builder()
                    .dispatcher(okHttpDispatcher)
                    .connectionPool(connectionPool)
                    .addInterceptor(RetryInterceptor(maxRetries = 3))
                    .addInterceptor(loggingInterceptor)
                    // Force HTTP/1.1 to avoid chunked transfer encoding issues with Laravel dev server
                    .protocols(listOf(Protocol.HTTP_1_1))
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)
                    .build()
            }
            // Limit Coil's own parallelism to avoid flooding the server
            .fetcherDispatcher(Dispatchers.IO.limitedParallelism(2))
            .decoderDispatcher(Dispatchers.IO.limitedParallelism(2))
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.30)
                    .strongReferencesEnabled(true)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizeBytes(256L * 1024 * 1024) // 256 MB
                    .build()
            }
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .networkCachePolicy(CachePolicy.ENABLED)
            .respectCacheHeaders(false)
            .crossfade(true)
            .allowHardware(true)
            .build()
    }
}

/**
 * OkHttp Interceptor that retries failed requests with exponential backoff.
 * Critical for php artisan serve which is single-threaded and drops concurrent connections.
 */
class RetryInterceptor(private val maxRetries: Int = 3) : Interceptor {
    
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var lastException: IOException? = null
        
        for (attempt in 0..maxRetries) {
            try {
                val response = chain.proceed(request)
                if (response.isSuccessful || attempt >= maxRetries) {
                    return response
                }
                // Server returned error, close body and retry
                response.body?.close()
                Log.w("RetryInterceptor", "Attempt $attempt failed with ${response.code} for ${request.url}, retrying...")
            } catch (e: IOException) {
                lastException = e
                Log.w("RetryInterceptor", "Attempt $attempt failed for ${request.url}: ${e.message}, retrying...")
                if (attempt >= maxRetries) throw e
            }
            
            // Exponential backoff: 500ms, 1000ms, 2000ms
            try {
                val delay = 500L * (1 shl attempt)
                Thread.sleep(delay)
            } catch (_: InterruptedException) {
                Thread.currentThread().interrupt()
                throw lastException ?: IOException("Interrupted during retry")
            }
        }
        
        throw lastException ?: IOException("All $maxRetries retries failed for ${request.url}")
    }
}