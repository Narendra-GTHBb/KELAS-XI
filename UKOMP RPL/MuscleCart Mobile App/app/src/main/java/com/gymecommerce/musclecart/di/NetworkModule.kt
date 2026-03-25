package com.gymecommerce.musclecart.di

import com.gymecommerce.musclecart.data.local.TokenManager
import com.gymecommerce.musclecart.data.remote.api.AuthApiService
import com.gymecommerce.musclecart.data.remote.api.CartApiService
import com.gymecommerce.musclecart.data.remote.api.CategoryApiService
import com.gymecommerce.musclecart.data.remote.api.FavoriteApiService
import com.gymecommerce.musclecart.data.remote.api.OrderApiService
import com.gymecommerce.musclecart.data.remote.api.ProductApiService
import com.gymecommerce.musclecart.data.remote.api.ShippingApiService
import com.gymecommerce.musclecart.data.remote.api.ReviewApiService
import com.gymecommerce.musclecart.data.remote.api.VoucherApiService
import com.gymecommerce.musclecart.data.remote.api.NotificationApiService
import com.gymecommerce.musclecart.data.remote.api.PointsApiService
import com.gymecommerce.musclecart.data.remote.interceptor.AuthInterceptor
import com.gymecommerce.musclecart.util.ServerConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = ServerConfig.BASE_URL

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor
    ): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideProductApiService(retrofit: Retrofit): ProductApiService {
        return retrofit.create(ProductApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideCategoryApiService(retrofit: Retrofit): CategoryApiService {
        return retrofit.create(CategoryApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideCartApiService(retrofit: Retrofit): CartApiService {
        return retrofit.create(CartApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideOrderApiService(retrofit: Retrofit): OrderApiService {
        return retrofit.create(OrderApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideFavoriteApiService(retrofit: Retrofit): FavoriteApiService {
        return retrofit.create(FavoriteApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideShippingApiService(retrofit: Retrofit): ShippingApiService {
        return retrofit.create(ShippingApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideReviewApiService(retrofit: Retrofit): ReviewApiService {
        return retrofit.create(ReviewApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideVoucherApiService(retrofit: Retrofit): VoucherApiService {
        return retrofit.create(VoucherApiService::class.java)
    }

    @Provides
    @Singleton
    fun providePointsApiService(retrofit: Retrofit): PointsApiService {
        return retrofit.create(PointsApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideNotificationApiService(retrofit: Retrofit): NotificationApiService {
        return retrofit.create(NotificationApiService::class.java)
    }
}
