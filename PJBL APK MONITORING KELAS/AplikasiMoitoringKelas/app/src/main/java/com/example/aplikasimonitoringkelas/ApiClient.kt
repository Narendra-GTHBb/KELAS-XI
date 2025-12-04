package com.example.aplikasimonitoringkelas

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    // Untuk HP fisik: gunakan IP komputer di jaringan WiFi yang sama
    // Untuk Emulator: gunakan 10.0.2.2
    private const val BASE_URL = "http://192.168.40.62:8000/api/"
    
    // Kurangi level logging untuk menghindari masalah dengan response besar
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC // Ganti dari BODY ke BASIC
    }
    
    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(120, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(120, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()
    
    // Gson dengan setLenient untuk handle JSON yang besar/complex
    private val gson = GsonBuilder()
        .setLenient()
        .serializeNulls()
        .create()
    
    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService::class.java)
    }
}