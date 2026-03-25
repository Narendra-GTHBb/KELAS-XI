package com.gymecommerce.musclecart.di

import android.content.Context
import androidx.room.Room
import com.gymecommerce.musclecart.data.local.dao.*
import com.gymecommerce.musclecart.data.local.database.GymEcommerceDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideGymEcommerceDatabase(
        @ApplicationContext context: Context
    ): GymEcommerceDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            GymEcommerceDatabase::class.java,
            GymEcommerceDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration() // Remove in production
            .build()
    }
    
    @Provides
    fun provideUserDao(database: GymEcommerceDatabase): UserDao {
        return database.userDao()
    }
    
    @Provides
    fun provideCategoryDao(database: GymEcommerceDatabase): CategoryDao {
        return database.categoryDao()
    }
    
    @Provides
    fun provideProductDao(database: GymEcommerceDatabase): ProductDao {
        return database.productDao()
    }
    
    @Provides
    fun provideOrderDao(database: GymEcommerceDatabase): OrderDao {
        return database.orderDao()
    }
    
    @Provides
    fun provideCartDao(database: GymEcommerceDatabase): CartDao {
        return database.cartDao()
    }
}