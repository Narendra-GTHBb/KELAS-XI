package com.gymecommerce.musclecart.data.remote.interceptor

import com.gymecommerce.musclecart.data.local.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = tokenManager.getToken()

        // Skip adding token for login/register endpoints
        val url = originalRequest.url.toString()
        if (url.contains("/login") || url.contains("/register")) {
            return chain.proceed(originalRequest)
        }

        // Add Authorization header if token exists
        val newRequest = if (!token.isNullOrEmpty()) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .header("Accept", "application/json")
                .build()
        } else {
            originalRequest.newBuilder()
                .header("Accept", "application/json")
                .build()
        }

        return chain.proceed(newRequest)
    }
}
