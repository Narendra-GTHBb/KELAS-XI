package com.gymecommerce.musclecart.di

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.gymecommerce.musclecart.data.repository.AuthRepositoryImpl
import com.gymecommerce.musclecart.data.repository.CartRepositoryImpl
import com.gymecommerce.musclecart.data.repository.CategoryRepositoryImpl
import com.gymecommerce.musclecart.data.repository.FavoriteRepositoryImpl
import com.gymecommerce.musclecart.data.repository.OrderRepositoryImpl
import com.gymecommerce.musclecart.data.repository.ProductRepositoryImpl
import com.gymecommerce.musclecart.data.repository.ShippingRepositoryImpl
import com.gymecommerce.musclecart.data.repository.UserRepositoryImpl
import com.gymecommerce.musclecart.data.repository.ReviewRepositoryImpl
import com.gymecommerce.musclecart.data.repository.VoucherRepositoryImpl
import com.gymecommerce.musclecart.data.repository.NotificationRepositoryImpl
import com.gymecommerce.musclecart.data.repository.PointsRepositoryImpl
import com.gymecommerce.musclecart.domain.repository.AuthRepository
import com.gymecommerce.musclecart.domain.repository.NotificationRepository
import com.gymecommerce.musclecart.domain.repository.CartRepository
import com.gymecommerce.musclecart.domain.repository.CategoryRepository
import com.gymecommerce.musclecart.domain.repository.FavoriteRepository
import com.gymecommerce.musclecart.domain.repository.OrderRepository
import com.gymecommerce.musclecart.domain.repository.ProductRepository
import com.gymecommerce.musclecart.domain.repository.ShippingRepository
import com.gymecommerce.musclecart.domain.repository.UserRepository
import com.gymecommerce.musclecart.domain.repository.ReviewRepository
import com.gymecommerce.musclecart.domain.repository.VoucherRepository
import com.gymecommerce.musclecart.domain.repository.PointsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// Extension property for DataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_preferences")

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }
    
    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }
    
    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("muscle_cart_prefs", Context.MODE_PRIVATE)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository
    
    @Binds
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository
    
    @Binds
    abstract fun bindProductRepository(
        productRepositoryImpl: ProductRepositoryImpl
    ): ProductRepository
    
    @Binds
    abstract fun bindCategoryRepository(
        categoryRepositoryImpl: CategoryRepositoryImpl
    ): CategoryRepository
    
    @Binds
    abstract fun bindCartRepository(
        cartRepositoryImpl: CartRepositoryImpl
    ): CartRepository
    
    @Binds
    abstract fun bindOrderRepository(
        orderRepositoryImpl: OrderRepositoryImpl
    ): OrderRepository

    @Binds
    abstract fun bindFavoriteRepository(
        favoriteRepositoryImpl: FavoriteRepositoryImpl
    ): FavoriteRepository

    @Binds
    abstract fun bindShippingRepository(
        shippingRepositoryImpl: ShippingRepositoryImpl
    ): ShippingRepository

    @Binds
    abstract fun bindReviewRepository(
        reviewRepositoryImpl: ReviewRepositoryImpl
    ): ReviewRepository

    @Binds
    abstract fun bindVoucherRepository(
        voucherRepositoryImpl: VoucherRepositoryImpl
    ): VoucherRepository

    @Binds
    abstract fun bindPointsRepository(
        pointsRepositoryImpl: PointsRepositoryImpl
    ): PointsRepository

    @Binds
    abstract fun bindNotificationRepository(
        notificationRepositoryImpl: NotificationRepositoryImpl
    ): NotificationRepository
}